package com.jmonzon.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.databinding.ActivityRegistroBinding
import com.jmonzon.tictactoe.model.User

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        events()

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun events() {
        binding.buttonRegistro.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            when {
                name.isEmpty() -> binding.editTextName.error = getText(R.string.nombre_vacio)
                email.isEmpty() -> binding.editTextEmail.error = getText(R.string.email_vacio)
                password.isEmpty() -> binding.editTextPassword.error =
                    getText(R.string.password_vacio)
                else -> createUser(name, email, password)
            }
        }
    }

    private fun createUser(name: String, email: String, password: String) {
        changeLoginFormVisibility(false)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("RegistroActivity -> ", "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                    keepUserInDB(user, name)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("RegistroActivity -> ", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, getString(R.string.error_create_user),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun keepUserInDB(user: FirebaseUser?, name : String) {
        val newUser = User(name, 0, 0)
        db.collection("users")
            .document(user?.uid!!)
            .set(newUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user !== null) {
            //Navigate  to next screen
            finish()
            val intent = Intent(this, FindGameActivity::class.java)
            startActivity(intent)
        }
        else {
            changeLoginFormVisibility(true)
            binding.editTextEmail.error = getText(R.string.nombre_email_pass_incorrecto)
            binding.editTextEmail.requestFocus()
        }

    }

    private fun changeLoginFormVisibility(showForm: Boolean) {
        binding.progressBarRegistro.visibility = if (showForm) View.GONE else View.VISIBLE
        binding.formRegistry.visibility = if (showForm) View.VISIBLE else View.GONE
    }
}