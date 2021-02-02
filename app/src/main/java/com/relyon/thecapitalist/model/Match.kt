package com.relyon.thecapitalist.model

data class Match(

    var uid: String = "",
    var players: List<Player>? = null,
    var price: Float = 0f,
    var started: Boolean = false
)