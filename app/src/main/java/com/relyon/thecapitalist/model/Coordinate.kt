package com.relyon.thecapitalist.model

data class Coordinate(

    var width: Int = 0,
    var height: Int = 0,
    var x: Float = 0f,
    var y: Float = 0f,
    var boardPosition: BoardPosition? = null
)