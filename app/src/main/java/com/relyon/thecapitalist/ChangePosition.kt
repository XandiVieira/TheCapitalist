package com.relyon.thecapitalist

import com.relyon.thecapitalist.model.BoardPosition
import com.relyon.thecapitalist.model.Player

interface ChangePosition {

    fun updatePosition(
        width: Int,
        height: Int,
        x: Float,
        y: Float,
        boardPosition: BoardPosition,
        player: Player
    )
}
