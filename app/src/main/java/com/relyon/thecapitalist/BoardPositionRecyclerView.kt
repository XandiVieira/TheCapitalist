package com.relyon.thecapitalist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.relyon.thecapitalist.model.BoardPosition
import kotlinx.android.synthetic.main.item_board_position.view.*


class BoardPositionRecyclerView(width: Int, height: Int, changePosition: ChangePosition) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var boardPositions = listOf<BoardPosition>()
    private var width = width
    private var height = height
    private var changePosition = changePosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BoardPositionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_board_position, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BoardPositionViewHolder
        viewHolder.bindView(boardPositions[position], changePosition, width, height)
    }

    override fun getItemCount(): Int = boardPositions.size

    fun setColorList(listOfColor: List<BoardPosition>) {
        this.boardPositions = listOfColor
        notifyDataSetChanged()
    }
}

class BoardPositionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(boardPosition: BoardPosition, changePosition: ChangePosition, width: Int, height: Int) {
        itemView.background_position.setBackgroundColor(boardPosition.color)
        itemView.layoutParams.height = height
        itemView.layoutParams.width = width
        itemView.background_position.setOnClickListener {
            if (boardPosition.color != Color.TRANSPARENT) {
                changePosition.updatePosition(
                    itemView.background_position.width,
                    itemView.background_position.height,
                    itemView.background_position.x,
                    itemView.background_position.y,
                    boardPosition
                )
            }
        }
    }
}