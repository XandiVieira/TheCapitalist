package com.relyon.thecapitalist.model

import com.relyon.thecapitalist.enums.BoardRegion

data class BoardPosition(

    val name: String? = "",
    val boardRegion: BoardRegion? = BoardRegion.TOP,
    val color: Int? = 0
)