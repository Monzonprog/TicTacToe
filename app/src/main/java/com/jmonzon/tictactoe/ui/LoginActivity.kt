package com.jmonzon.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var tryLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Use ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        events()

        firebaseAuth = FirebaseAuth.getInstance()
    }

    //Declarate events in the forms
    private fun events() {
        binding.buttonLogin.setOnClickListener {
            var email = binding.editTextEmail.text.toString()
            var password = binding.editTextPassword.text.toString()

            when {
                email.isEmpty() -> binding.editTextEmail.error = getText(R.string.email_vacio)
                password.isEmpty() -> binding.editTextPassword.error =
                    getText(R.string.password_vacio)
                else -> {
                    changeLoginFormVisibility(false)
                    loginUser(email, password)
                }
            }
        }

        binding.textViewToRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                tryLogin = true
                if (task.isSuccessful) {
                    Log.d("LoginActivity -> ", "Logado OK")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                    Log.w("LoginActivity -> ", "Logado KO")
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user !== null) {
            val intent = Intent(this, FindGameActivity::class.java)
            finish()
            intent.putExtra("user", user)
            startActivity(intent)
        } else {
            changeLoginFormVisibility(true)
            if (tryLogin) {
                binding.editTextEmail.error = getText(R.string.nombre_email_pass_incorrecto)
                binding.editTextEmail.requestFocus()
            }
        }
    }

    private fun changeLoginFormVisibility(showForm: Boolean) {
        binding.progressBarLogin.visibility = if (showForm) GONE else VISIBLE
        binding.formLogin.visibility = if (showForm) VISIBLE else GONE
    }

    override fun onStart() {
        super.onStart()
        //Check if the user have session initialized and don´t show the login form
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        updateUI(currentUser)
    }
}