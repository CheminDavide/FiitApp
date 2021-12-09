package it.polito.ic2020.did2020.fiitapp.Model

data class RSSObject(
        val status:String,
        val feed:Feed,
        val items:List<Item>)