package com.jmonzon.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.app.Constants
import com.jmonzon.tictactoe.databinding.ActivityFindGameBinding
import com.jmonzon.tictactoe.model.Plays

class FindGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindGameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var uid: String
    private lateinit var playId: String
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindGameBinding.inflate(layoutInflater)
        val view: View = binding.root
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
        binding.buttonPlay.setOnClickListener {
            changeMenuVisibility(false)
            lookForFreePlay()

        }
        binding.buttonRanking.setOnClickListener { }
    }

    private fun lookForFreePlay() {
        binding.textViewLoading.text = getText(R.string.buscando_partida)
        db.collection("plays")
            .whereEqualTo("playerTwoId", "")
            .get()
            .addOnCompleteListener(this) { task ->
                if (task.result!!.size() == 0) {
                    //Don´t exist any play, create a new free play
                    createNewGame()
                } else {
                    //We use the position 0 because is possible that we get a list
                    val docPlay: DocumentSnapshot = task.result!!.documents[0]
                    playId = docPlay.id
                    val play: Plays? = docPlay.toObject(Plays::class.java)
                    play!!.playerTwoId = uid
                    db.collection("plays")
                        .document(playId)
                        .set(play)
                        .addOnSuccessListener(this) {
                            binding.textViewLoading.text = getString(R.string.partida_encontrada)
                            binding.animationView.repeatCount = 0
                            binding.animationView.setAnimation("checked_animation.json")
                            binding.animationView.playAnimation()

                            val handler = Handler()
                            val runnable = Runnable {
                                startGame()
                            }
                            handler.postDelayed(runnable, 1500)
                        }
                        .addOnFailureListener(this) { task ->
                            changeMenuVisibility(true)
                            Toast.makeText(this, getText(R.string.existe_error), Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
    }

    private fun createNewGame() {
        binding.textViewLoading.text = getText(R.string.creando_partida)
        val newPlays = Plays(uid)

        db.collection("plays")
            .add(newPlays)
            .addOnSuccessListener { documentReference ->
                playId = documentReference.id
                waitPlayer()
            }
            .addOnFailureListener { exception ->
                changeMenuVisibility(true)
                Toast.makeText(this, getText(R.string.existe_error), Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun waitPlayer() {
        binding.textViewLoading.text = getText(R.string.esperando_jugador)
        listenerRegistration = db.collection("plays")
            .document(playId)
            .addSnapshotListener { value, error ->
                if (value!!.get("playerTwoId") != "") {
                    binding.textViewLoading.text = getString(R.string.hay_oponente)
                    binding.animationView.repeatCount = 0
                    binding.animationView.setAnimation("checked_animation.json")
                    binding.animationView.playAnimation()
                    val handler = Handler()
                    val runnable = Runnable {
                        startGame()
                    }
                    handler.postDelayed(runnable, 1500)
                }
            }
    }

    private fun startGame() {
        if (listenerRegistration != null) {
            listenerRegistration?.remove()
        }
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(Constants.EXTRA_PLAY_ID, playId)
        startActivity(intent)
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

    override fun onStop() {
        if (listenerRegistration !== null) {
            listenerRegistration?.remove()
        }
        if(playId !== "") {
            db.collection("plays")
                .document(playId)
                .delete()
                .addOnCompleteListener (this){
                    playId = ""
                }
        }
        super.onStop()
    }
}