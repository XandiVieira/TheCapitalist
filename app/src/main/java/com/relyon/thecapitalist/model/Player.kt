package com.relyon.thecapitalist.model

data class Player(

    var uid: String? = "",
    var nickname: String? = "",
    var coordinates: Coordinate? = null,
    var number: Int? = 0,
    var color: Int? = 0,
    var isHost: Boolean? = false
)