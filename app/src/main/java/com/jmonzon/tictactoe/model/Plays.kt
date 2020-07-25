package com.jmonzon.tictactoe.model

import java.util.*

data class Plays(
    var playerOneId: String,
    var playerTwoId: String,
    var cellSelected: List<Int>,
    var turnPlayerOne: Boolean,
    var winnerId: String,
    var created: Date,
    var abandonmentId: String
)
