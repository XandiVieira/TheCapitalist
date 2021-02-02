package com.relyon.thecapitalist.util

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.relyon.thecapitalist.model.User


object Util {
    lateinit var db: DatabaseReference
    var user: User? = null
    var fbUser: FirebaseUser? = null

    fun restartClass() {
        user = null
        fbUser = null
    }
}