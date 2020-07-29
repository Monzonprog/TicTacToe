package com.jmonzon.tictactoe.model

import java.util.*
import kotlin.collections.ArrayList

class Plays {
    var playerOneId: String? = null
    var playerTwoId: String? = null
    var cellSelected: MutableList<Int>? = createList()
    var turnPlayerOne = false
    var winnerId: String? = null
    var created: Date? = null
    var abandonmentId: String? = null

    constructor() {}
    constructor(playerOneId: String?) {
        this.playerOneId = playerOneId
        playerTwoId = ""
        cellSelected = createList()
        turnPlayerOne = true
        created = Date()
        winnerId = ""
        abandonmentId = ""
    }

    fun createList(): ArrayList<Int>{
        var aux = ArrayList<Int>()
        for (i in 0..8) {
            aux.add(0)
        }
        return aux
    }
}
