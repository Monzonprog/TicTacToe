package com.jmonzon.tictactoe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.databinding.ActivityFindGameBinding

class FindGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindGameBinding.inflate(layoutInflater)
        val view : View = binding.root
        setContentView(view)

        binding.progressBarFindGame.isIndeterminate = true
        binding.textViewLoading.text = getText(R.string.cargando)
    }
}