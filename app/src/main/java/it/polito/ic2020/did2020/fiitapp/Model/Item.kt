package it.polito.ic2020.did2020.fiitapp.Model

import java.util.*

data class Item(
    val title:String,
    val pubDate: String,
    val link:String,
    val guid:String,
    val author:String,
    val thumbnail:String,
    val description:String,
    val content:String,
    val enclosure:Object,
    val categories: List<String>)