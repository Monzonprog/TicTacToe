package com.jmonzon.tictactoe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jmonzon.tictactoe.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}