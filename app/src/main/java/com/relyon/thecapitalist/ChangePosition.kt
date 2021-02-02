package com.relyon.thecapitalist

import com.relyon.thecapitalist.model.BoardPosition

interface ChangePosition {

    fun updatePosition(width: Int, height: Int, x: Float, y: Float, boardPosition: BoardPosition)
}
