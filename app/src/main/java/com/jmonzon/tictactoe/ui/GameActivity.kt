package com.jmonzon.tictactoe.ui

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.jmonzon.tictactoe.R
import com.jmonzon.tictactoe.app.Constants
import com.jmonzon.tictactoe.databinding.ActivityGameBinding
import com.jmonzon.tictactoe.model.Plays
import com.jmonzon.tictactoe.model.User
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var boxes: ArrayList<ImageView>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var uid: String
    private lateinit var playId: String
    private lateinit var winnerId: String
    private lateinit var listenerPlay: ListenerRegistration
    private lateinit var plays: Plays
    private var playerOneName = ""
    private var playerTwoName = ""
    private lateinit var userPlayerOne: User
    private lateinit var userPlayerTwo: User


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
                    getPlayerName()
                }
                updateUI()
                updatePlayersUI()
            }
    }

    private fun updatePlayersUI() {

        if (plays.turnPlayerOne) {
            binding.contentGame.textViewPlayerOne.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.contentGame.textViewPlayerTwo.setTextColor(Color.BLACK)
        } else {
            binding.contentGame.textViewPlayerOne.setTextColor(Color.BLACK)
            binding.contentGame.textViewPlayerTwo.setTextColor(resources.getColor(R.color.colorPrimary))
        }

        if (!plays.winnerId!!.isEmpty()) {
            winnerId = plays.winnerId.toString()
            showGameOverDialog()
        }
    }

    private fun updateUI() {
        var x = 0
        while (x in 0..8) {
            val box = plays.cellSelected!![x]
            val ivCurrentBox: ImageView = boxes[x]
            if (box == 0) {
                ivCurrentBox.setImageResource(R.drawable.ic_empty_square)
            } else if (box == 1) {
                ivCurrentBox.setImageResource(R.drawable.ic_player_one)
            } else {
                ivCurrentBox.setImageResource(R.drawable.ic_player_two)
            }
            x++
        }
    }

    private fun getPlayerName() {
        //Obtain name´s player one
        db.collection("users")
            .document(plays.playerOneId.toString())
            .get()
            .addOnSuccessListener(this) { task ->
               // userPlayerOne = task.toObject(User::class.java)!!
                playerOneName = task.get("name").toString()
                binding.contentGame.textViewPlayerOne.text = playerOneName
            }
        //Obtain name´s player two
        db.collection("users")
            .document(plays.playerTwoId.toString())
            .get()
            .addOnSuccessListener(this) { task ->
                //userPlayerTwo = task.toObject(User::class.java)!!
                playerTwoName = task.get("name").toString()
                binding.contentGame.textViewPlayerTwo.text = playerTwoName
            }
    }

    override fun onStop() {
        listenerPlay.remove()
        super.onStop()
    }

    fun boxClicked(view: View) {
        if (!plays.winnerId!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.end_game), Toast.LENGTH_SHORT).show()
        } else {
            if (plays.turnPlayerOne && plays.playerOneId.equals(uid)) {
                //Player one is playing
                updatePlays(view.tag.toString())
            } else if (!plays.turnPlayerOne && plays.playerTwoId.equals(uid)) {
                updatePlays(view.tag.toString())
            } else {
                Toast.makeText(this, getString(R.string.not_turn), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePlays(boxNumber: String) {
        val position: Int = boxNumber.toInt()
        if (plays.cellSelected!![position] != 0) {
            Toast.makeText(this, R.string.select_free_box, Toast.LENGTH_SHORT).show()
        } else {
            if (plays.turnPlayerOne) {
                boxes[position].setImageResource(R.drawable.ic_player_one)
                plays.cellSelected!![position] = 1
            } else {
                boxes[position].setImageResource(R.drawable.ic_player_two)
                plays.cellSelected!![position] = 2
            }
            if (existSolution()) {
                plays.winnerId = uid
                Toast.makeText(this, "Hay un ganador", Toast.LENGTH_SHORT).show()
            } else if (existTie()) {
                plays.winnerId = "EMPATE"
                Toast.makeText(this, "Hay empate", Toast.LENGTH_SHORT).show()
            } else {
                changeTurn()
            }
            //Update Firestore with new data
            db.collection("plays")
                .document(playId)
                .set(plays)
                .addOnSuccessListener {
                    Log.w("GameActivity Ok: ", "Jugada guardada")
                }
                .addOnFailureListener {
                    Log.w("GameActivity Error: ", "Error al guardar la jugada")
                }
        }
    }

    private fun changeTurn() {
        plays.turnPlayerOne = !plays.turnPlayerOne
    }

    private fun existTie(): Boolean {
        var exist = false
        var existBoxFree = false
        var x = 0
        while (x in 0..8) {
            if (plays.cellSelected!![x] == 0) {
                existBoxFree = true
                break
            }
            x++
        }

        if (!existBoxFree) { //Tie
            exist = true
        }
        return exist
    }

    private fun existSolution(): Boolean {
        var exist = false

        var selectedCells: List<Int> = plays.cellSelected!!
        if (selectedCells[0] == selectedCells[1] && selectedCells[1] == selectedCells[2] && selectedCells[2] != 0) {
            exist = true
        } else if (selectedCells[3] == selectedCells[4] && selectedCells[4] == selectedCells[5] && selectedCells[5] != 0) {
            exist = true
        } else if (selectedCells[6] == selectedCells[7] && selectedCells[7] == selectedCells[8] && selectedCells[8] != 0) {
            exist = true
        } else if (selectedCells[0] == selectedCells[3] && selectedCells[3] == selectedCells[6] && selectedCells[6] != 0) {
            exist = true
        } else if (selectedCells[1] == selectedCells[4] && selectedCells[4] == selectedCells[7] && selectedCells[7] != 0) {
            exist = true
        } else if (selectedCells[2] == selectedCells[5] && selectedCells[5] == selectedCells[8] && selectedCells[8] != 0) {
            exist = true
        } else if (selectedCells[0] == selectedCells[4] && selectedCells[4] == selectedCells[8] && selectedCells[8] != 0) {
            exist = true
        } else if (selectedCells[2] == selectedCells[4] && selectedCells[4] == selectedCells[6] && selectedCells[6] != 0) {
            exist = true
        }
        return exist
    }

    private fun showGameOverDialog() {
        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }
        //Use our own layout
        val view = layoutInflater.inflate(R.layout.dialog_game_over, null)

        var tvPoints: TextView = view.findViewById(R.id.textViewPoints)
        var tvInformation: TextView = view.findViewById(R.id.textViewInformation)
        var lottieAnimation: LottieAnimationView = view.findViewById((R.id.animation_view))

        //Custom title
        val title = TextView(this)
        title.text = getString(R.string.game_over)
        title.gravity = Gravity.CENTER;
        title.setPadding(10, 10, 10, 10);
        title.setTextColor(Color.BLACK);
        title.textSize = 20F

        builder?.setCustomTitle(title)
        //Dialog can´t be closed if don´t push button
        builder?.setCancelable(false)
        builder?.setView(view)

        if (plays.winnerId == "EMPATE") {
            //updatePoints(1)
            tvInformation.text = getString(R.string.game_tie)
            tvPoints.text = getString(R.string.one_point)
        } else if (winnerId == uid) {

            //updatePoints(3)
            tvInformation.text = getString(R.string.winner_text)
            tvPoints.text = getString(R.string.three_points)
        } else {

            //updatePoints(0)
            tvInformation.text = getString(R.string.loser_text)
            lottieAnimation.setAnimation("thumbs_down_animation.json")
            lottieAnimation.playAnimation()
            lottieAnimation.repeatCount = Animation.INFINITE
        }
        builder?.apply {
            setPositiveButton(R.string.exit,
                DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
        }
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()


    }

    private fun updatePoints(points: Int) {
        lateinit var newUserPoints : User
        if (playerOneName == userPlayerOne.name) {
            userPlayerOne.points = userPlayerOne.points + points
            newUserPoints = userPlayerOne
        } else {
            userPlayerTwo.points = userPlayerTwo.points + points
            newUserPoints = userPlayerTwo
        }
        db.collection("users")
            .document(uid)
            .set(newUserPoints)
            .addOnSuccessListener (this) {

            }
            .addOnFailureListener (this) {

            }
    }

}