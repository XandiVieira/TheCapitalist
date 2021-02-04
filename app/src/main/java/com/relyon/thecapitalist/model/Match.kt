package com.relyon.thecapitalist.model

import com.relyon.thecapitalist.enums.MatchStatus

data class Match(

    var uid: String,
    var players: List<Player>,
    var price: Float,
    var status: MatchStatus,
) {
    constructor() : this("", emptyList(), 0f, MatchStatus.LOBBY)
}