package com.jmonzon.tictactoe.model

import java.util.*

data class Plays(
    val playerOneId: String,
    val playerTwoId: String,
    val cellSelected: List<Int>,
    val turnPlayerOne: Boolean,
    val winnerId: String,
    val created: Date,
    val abandonmentId: String
)
