package it.polito.ic2020.did2020.fiitapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.paziente_pesatura.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.roundToInt


class PazientePesatura : Fragment(R.layout.paziente_pesatura) {

    val fDatabase= FirebaseFirestore.getInstance()
    val fAuth = FirebaseAuth.getInstance()
    val fUser = fAuth.currentUser.uid
    val dbPesi: MutableList<ItemPeso> = mutableListOf()
    val db = fDatabase.collection("pazienti").document(fUser)

    var misurazioni = mutableListOf<Float>()
    var endTime: Boolean = true

    val timer = object: CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            showMessaggi.text = "Resta sulla bilancia.\nAttendi\n".plus(millisUntilFinished / 1000).plus("s")
        }
        override fun onFinish() {
            endTime = false;
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        setListaPesature()

        btn_pesami.setOnClickListener {
            showMessaggi.setTextColor(resources.getColor(R.color.black))
            misurazioni.clear()
            endTime = true

            var manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            builder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            builder.setNetworkSpecifier(
                WifiNetworkSpecifier.Builder().apply {
                    setSsid("FiitConnect")
                    setWpa2Passphrase("password")
                }.build()
            )
            try {
                manager.requestNetwork(builder.build(), object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        timer.start()
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(5000L)
                            while (endTime) {
                                delay(1000L)
                                val urlConnection = network.openConnection(URL("http://192.168.4.1/"))
                                misurazioni.add(urlConnection.getInputStream().bufferedReader().readText().toFloat())
                            }
                            withContext(Dispatchers.Main) {
                                val currentPeso = ((misurazioni.average() * 100).roundToInt() / 100.0).toFloat()
                                showPeso.text = currentPeso.toString().plus("kg")
                                showMessaggi.text = "\nMisurazione effettuata\n con successo!"
                                showMessaggi.setTextColor(resources.getColor(R.color.green_primary))
                                manager.bindProcessToNetwork(null)

                                db.get().addOnCompleteListener { doc ->
                                    val pesi: MutableList<Float> = doc.result?.get("pesatura.peso") as MutableList<Float>
                                    val date: MutableList<String> = doc.result?.get("pesatura.data") as MutableList<String>
                                    pesi.add(0, currentPeso)
                                    date.add(0, System.currentTimeMillis().toString())
                                    db.update("pesatura.peso", pesi)
                                    db.update("pesatura.data", date)
                                    Toast.makeText(context, "Peso registrato", Toast.LENGTH_LONG).show()

                                }
                            }
                        }

                    }
                })
            } catch (e: SecurityException) {
                showMessaggi.text = "ERRORE\nImpossibile connettersi"
            }

        }
    }

    private fun setListaPesature() {
        val dbPaziente = fDatabase.collection("pazienti").document(fUser)
        dbPaziente.get().addOnCompleteListener{ docPeso ->
            val pesi: List<Float> = docPeso.result?.get("pesatura.peso") as List<Float>
            val date: List<String> = docPeso.result?.get("pesatura.data") as List<String>
            for(i in pesi.indices){
                dbPesi.add(ItemPeso(pesi[i],date[i]))
            }

            if(pesi.isNotEmpty()) {
                showPeso.text = pesi[0].toString().plus("kg")

                listaPesi.layoutManager= LinearLayoutManager(context)
                listaPesi.adapter = AdapterPesi(dbPesi);
            } else {
                noInserimento.visibility = View.VISIBLE
            }
        }

    }
}
