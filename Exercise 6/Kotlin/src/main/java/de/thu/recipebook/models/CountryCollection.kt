package de.thu.recipebook.models

import java.util.*

object CountryCollection {
    private val COUNTRIES: TreeMap<String, String> = object : TreeMap<String, String>() {
        init {
            put("Albania", "al")
            put("Andorra", "ad")
            put("Austria", "at")
            put("Belarus", "by")
            put("Belgium", "be")
            put("Bosnia and Herzegovina", "ba")
            put("Bulgaria", "bg")
            put("Croatia", "hr")
            put("Czech Republic", "cz")
            put("Denmark", "dk")
            put("Estonia", "ee")
            put("Finland", "fi")
            put("France", "fr")
            put("Germany", "de")
            put("Greece", "gr")
            put("Hungary", "hu")
            put("Iceland", "is")
            put("Ireland", "ie")
            put("Italy", "it")
            put("Latvia", "lv")
            put("Liechtenstein", "li")
            put("Lithuania", "lt")
            put("Luxembourg", "lu")
            put("Malta", "mt")
            put("Moldova", "md")
            put("Monaco", "mc")
            put("Montenegro", "me")
            put("Netherlands", "nl")
            put("North Macedonia", "mk")
            put("Norway", "no")
            put("Poland", "pl")
            put("Portugal", "pt")
            put("Romania", "ro")
            put("Russia", "ru")
            put("San Marino", "sm")
            put("Serbia", "rs")
            put("Slovakia", "sk")
            put("Slovenia", "si")
            put("Spain", "es")
            put("Sweden", "se")
            put("Switzerland", "ch")
            put("Ukraine", "ua")
            put("United Kingdom", "gb")
        }
    }

    val countries: List<String>
        get() = ArrayList(COUNTRIES.keys)

    fun getImageResourceName(country: String): String? {
        return COUNTRIES[country]
    }
}