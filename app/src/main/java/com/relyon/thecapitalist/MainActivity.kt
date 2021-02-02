package com.relyon.thecapitalist

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.widget.LoginButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.relyon.thecapitalist.model.User
import com.relyon.thecapitalist.util.Constants
import com.relyon.thecapitalist.util.Util
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    private val activity: Activity? = null
    private var firebaseUser: FirebaseUser? = null
    private var user: User? = null
    private var db: DatabaseReference? = null
    private var firebaseInstanceId: String? = null

    private lateinit var loginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startFirebaseInstances()
        setLayoutAttributes()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult: InstanceIdResult ->
            firebaseInstanceId = instanceIdResult.token
        }

        if (firebaseUser == null) {
            goLoginScreen()
        } else {
            retrieveUser()
        }
    }

    private fun retrieveUser() {
        db?.child(Constants.DATABASE_REF_USER)?.child(firebaseUser!!.uid)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    db?.child(Constants.DATABASE_REF_USER)?.child(firebaseUser!!.uid)!!
                        .removeEventListener(
                            this
                        )
                    user = dataSnapshot.getValue(User::class.java)
                    if (user == null) {
                        createUser()
                    }
                    Util.user = user
                    updateToken()
                    startActivity(Intent(applicationContext, BoardGameActivity::class.java))
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun updateToken() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            db?.child(Constants.DATABASE_REF_USER)?.child(user?.uid.toString())
                ?.child(Constants.DATABASE_REF_TOKEN)
                ?.setValue(firebaseInstanceId)
        }
    }

    private fun createUser() {
        user = User(
            firebaseUser?.uid.toString(),
            firebaseUser?.displayName.toString(),
            0F,
            firebaseInstanceId.toString(),
            false,
            firebaseUser?.displayName.toString(),
            firebaseUser?.photoUrl.toString()
        )
        db?.child(Constants.DATABASE_REF_USER)?.child(user!!.uid)?.setValue(user)
    }

    private fun goLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setLayoutAttributes() {

    }

    private fun startFirebaseInstances() {
        FirebaseApp.initializeApp(this)
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        val mDatabaseRef = mFirebaseDatabase.reference
        db = mDatabaseRef
        Util.db = db as DatabaseReference
    }
}