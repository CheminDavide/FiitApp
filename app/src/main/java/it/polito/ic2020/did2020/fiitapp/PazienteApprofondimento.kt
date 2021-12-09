package it.polito.ic2020.did2020.fiitapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import it.polito.ic2020.did2020.fiitapp.Model.RSSObject
import kotlinx.android.synthetic.main.paziente_approfondimento.*
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class PazienteApprofondimento : Fragment(R.layout.paziente_approfondimento) {

    private val RSS_link = "http://forum.corriere.it/nutrizione/rss.xml"
    private val RSS_to_JSON_API ="https://api.rss2json.com/v1/api.json?rss_url="


   override fun onViewCreated(view: View, savedInstanceState: Bundle?){
       recyclerView.layoutManager = LinearLayoutManager(context)
       loadRSS()

       topAppBar.setNavigationOnClickListener {
           Navigation.findNavController(view).navigateUp()
       }
    }



    private fun loadRSS() {
        val loadRSSAsync = object:AsyncTask<String, String, String>(){
            //var mDialog = ProgressDialog(this@PazienteApprofondimento)

            override fun onPostExecute(result: String?) {
                //mDialog.dismiss()
                var rssObject: RSSObject = Gson().fromJson<RSSObject>(result, RSSObject::class.java)
                val adapter = context?.let { AdapterFeed(rssObject, it) }

                recyclerView.adapter = adapter
                if (adapter != null) {
                    adapter.notifyDataSetChanged()
                }
            }

            override fun doInBackground(vararg params: String): String {
                val result:String
                result = GetHTTPDataHandler(params[0]) ?: ""
                return result
            }


            override fun onPreExecute() {
                //mDialog.setMessage("Please wait...")
                //mDialog.show()
            }
        }
        val url_get_data = StringBuilder(RSS_to_JSON_API)
        url_get_data.append(RSS_link)
        loadRSSAsync.execute(url_get_data.toString())

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_refresh)
            loadRSS()
        return true
    }

    fun GetHTTPDataHandler(urlString: String?): String? {
        var stream = ""
        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
                val r = BufferedReader(InputStreamReader(inputStream))
                val sb = java.lang.StringBuilder()
                var line: String? = ""
                while (r.readLine().also { line = it } != null) sb.append(line)
                stream = sb.toString()
                urlConnection.disconnect()
            }
        } catch (ex: Exception) {
            return null
        }
        return stream
    }
}
