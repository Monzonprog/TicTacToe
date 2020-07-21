package com.jmonzon.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jmonzon.tictactoe.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Use ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        events()
    }

    //Declarate events in the forms
    private fun events() {
        binding.buttonLogin.setOnClickListener {
            var email = binding.editTextEmail.text.toString()
            var password = binding.editTextPassword.text.toString()

            changeLoginFormVisibility(false)
        }

        binding.textViewToRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)

        }
    }

    private fun changeLoginFormVisibility(showForm: Boolean) {
        binding.progressBarLogin.visibility = if (showForm) GONE else VISIBLE
        binding.formLogin.visibility = if (showForm) VISIBLE else GONE
    }
}