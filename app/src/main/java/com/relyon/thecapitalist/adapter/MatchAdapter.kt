package com.relyon.thecapitalist.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.relyon.thecapitalist.BoardGameActivity
import com.relyon.thecapitalist.R
import com.relyon.thecapitalist.enums.BoardRegion
import com.relyon.thecapitalist.model.BoardPosition
import com.relyon.thecapitalist.model.Coordinate
import com.relyon.thecapitalist.model.Match
import com.relyon.thecapitalist.model.Player
import com.relyon.thecapitalist.util.Constants
import com.relyon.thecapitalist.util.Util
import kotlinx.android.synthetic.main.item_match.view.*
import java.util.*

class MatchAdapter(context: Context, activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var matches = listOf<Match>()
    private var context = context
    private var activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MatchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MatchViewHolder
        viewHolder.bindView(matches[position], context, activity)
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    fun setMatchesList(matchesInLobby: List<Match>) {
        this.matches = matchesInLobby
        notifyDataSetChanged()
    }
}

class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(match: Match, context: Context, activity: Activity) {
        itemView.uid.text = match.uid
        val price = match.price
        itemView.price.text = "$price R$"

        itemView.setOnClickListener {
            val player = createPlayer(match, activity)
            context.startActivity(
                Intent(
                    activity,
                    BoardGameActivity::class.java
                ).putExtra(Constants.MATCH_UID, match.uid)
                    .putExtra(Constants.PLAYER_NUMBER, player.number)
            )
        }
    }

    private fun createPlayer(match: Match?, activity: Activity): Player {
        val coordinates = HashMap<String, Float>()
        coordinates["x"] = 0f
        coordinates["y"] = 0f

        val playerNumber = match?.players?.size ?: 0

        val color = if (match == null) Color.RED else Color.GREEN
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val player = Player(
            Util.user?.uid.toString(),
            Util.user?.nickname.toString(),
            Coordinate(
                displayMetrics.widthPixels / 5,
                displayMetrics.heightPixels / 7,
                0f,
                0f,
                BoardPosition("place 1", BoardRegion.TOP, Color.DKGRAY)
            ),
            playerNumber,
            color,
            true
        )
        Util.db.child(Constants.DATABASE_REF_MATCH).child(match!!.uid)
            .child(Constants.DATABASE_REF_PLAYER).child(player.number.toString()).setValue(player)
        return player
    }
}