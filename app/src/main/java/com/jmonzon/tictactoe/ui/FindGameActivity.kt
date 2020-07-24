package com.jmonzon.tictactoe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.databinding.ActivityFindGameBinding

class FindGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindGameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindGameBinding.inflate(layoutInflater)
        val view : View = binding.root
        setContentView(view)
        initProgressBar()
        initFirebase()
        events()
    }

    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        uid = firebaseUser.uid
    }

    private fun events() {
        binding.buttonPlay.setOnClickListener { changeMenuVisibility(false)}
        binding.buttonRanking.setOnClickListener { }
    }

    private fun initProgressBar() {
        binding.progressBarFindGame.isIndeterminate = true
        binding.textViewLoading.text = getText(R.string.cargando)
        changeMenuVisibility(true)
    }

    private fun changeMenuVisibility(showMenu: Boolean) {
        binding.layoutProgressBar.visibility = if (showMenu) View.GONE else View.VISIBLE
        binding.menuJuego.visibility = if (showMenu) View.VISIBLE else View.GONE
    }

    override fun onRestart() {
        super.onRestart()
        changeMenuVisibility(true)
    }
}