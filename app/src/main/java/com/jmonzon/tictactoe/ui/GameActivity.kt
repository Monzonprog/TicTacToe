package com.jmonzon.tictactoe.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.app.Constants
import com.jmonzon.tictactoe.databinding.ActivityGameBinding
import com.jmonzon.tictactoe.model.Plays
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var boxes: ArrayList<ImageView>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var uid: String
    private lateinit var playId: String
    private lateinit var listenerPlay: ListenerRegistration
    private lateinit var plays: Plays
    private var playerOneName = ""
    private var playerTwoName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        initViews()
        initGame()
    }

    private fun initGame() {
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        uid = firebaseUser.uid
        val extras: Bundle = intent.extras!!
        playId = extras.getString(Constants.EXTRA_PLAY_ID)!!
    }

    private fun initViews() {
        boxes = ArrayList<ImageView>()
        boxes.add(binding.contentGame.imageView0)
        boxes.add(binding.contentGame.imageView1)
        boxes.add(binding.contentGame.imageView2)
        boxes.add(binding.contentGame.imageView3)
        boxes.add(binding.contentGame.imageView4)
        boxes.add(binding.contentGame.imageView5)
        boxes.add(binding.contentGame.imageView6)
        boxes.add(binding.contentGame.imageView7)
        boxes.add(binding.contentGame.imageView8)
    }

    override fun onStart() {
        super.onStart()
        playListener()
    }

    private fun playListener() {
        listenerPlay = db.collection("plays")
            .document(playId)
            .addSnapshotListener(this) { documentSnapshot, error ->
                if (error !== null) {
                    Toast.makeText(
                        this,
                        getString(R.string.error_to_obtain_info),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                val source: String = if (
                    documentSnapshot !== null && documentSnapshot.metadata.hasPendingWrites()) "Local" else "Server"

                if (documentSnapshot!!.exists() && source.equals("Server")) {
                    plays = documentSnapshot.toObject(Plays::class.java)!!
                }
                if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                    getPlayerGames()
                }
            }
    }

    private fun getPlayerGames() {
        //Obtain name´s player one
        db.collection("users")
            .document(plays.playerOneId.toString())
            .get()
            .addOnSuccessListener (this) {task ->
                playerOneName = task.get("name").toString()
                binding.contentGame.textViewPlayerOne.text = playerOneName
            }
        //Obtain name´s player two
        db.collection("users")
            .document(plays.playerTwoId.toString())
            .get()
            .addOnSuccessListener (this) {task ->
                playerTwoName = task.get("name").toString()
                binding.contentGame.textViewPlayerTwo.text = playerTwoName
            }
    }

    override fun onStop() {
        listenerPlay.remove()
        super.onStop()
    }

}