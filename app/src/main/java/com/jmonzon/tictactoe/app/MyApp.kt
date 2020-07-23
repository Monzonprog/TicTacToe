package com.jmonzon.tictactoe.app

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //Initialize Firebase only once when App start
        FirebaseApp.initializeApp(this)
    }
}