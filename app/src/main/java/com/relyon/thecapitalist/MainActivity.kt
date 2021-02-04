package com.relyon.thecapitalist

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.relyon.thecapitalist.adapter.MatchAdapter
import com.relyon.thecapitalist.enums.BoardRegion
import com.relyon.thecapitalist.enums.MatchStatus
import com.relyon.thecapitalist.model.*
import com.relyon.thecapitalist.util.Constants
import com.relyon.thecapitalist.util.Util
import java.util.*

class MainActivity : AppCompatActivity() {

    private var activity: Activity? = null
    private var firebaseUser: FirebaseUser? = null
    private var user: User? = null
    private var db: DatabaseReference? = null
    private var firebaseInstanceId: String? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var createMatch: Button
    private lateinit var findMatch: Button
    private lateinit var matchesRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this

        startFirebaseInstances()
        setLayoutAttributes()

        if (firebaseUser == null) {
            goLoginScreen()
        } else {
            retrieveUser()
        }

        createMatch.setOnClickListener {
            createMatch()
        }

        findMatch.setOnClickListener {
            findMatch()
        }
    }

    private fun findMatch() {
        val matchesInLobby: MutableList<Match> = mutableListOf()
        Util.db.child(Constants.DATABASE_REF_MATCH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Util.db.child(Constants.DATABASE_REF_MATCH).removeEventListener(this)
                    for (DataSnapshot in snapshot.children) {
                        val match = DataSnapshot.getValue(Match::class.java)!!
                        if (match.players.isNotEmpty()) {
                            matchesInLobby.add(match)
                        }
                    }
                    linearLayoutManager = LinearLayoutManager(applicationContext)
                    matchesRV.layoutManager = linearLayoutManager
                    val matchAdapter = MatchAdapter(
                        applicationContext,
                        activity!!
                    )
                    matchesRV.adapter = matchAdapter
                    matchAdapter.setMatchesList(matchesInLobby)
                }

                override fun onCancelled(error: DatabaseError) {
                    return
                }
            })
    }

    private fun createMatch() {
        val match = Match(
            UUID.randomUUID().toString(), listOf(
                createPlayer()
            ), 500f, MatchStatus.LOBBY
        )
        Util.db.child(Constants.DATABASE_REF_MATCH).child(match.uid).setValue(match)
        startActivity(
            Intent(
                applicationContext,
                BoardGameActivity::class.java
            ).putExtra(Constants.MATCH_UID, match.uid)
        )
    }

    private fun createPlayer(): Player {
        val coordinates = HashMap<String, Float>()
        coordinates["x"] = 0f
        coordinates["y"] = 0f

        val playerNumber = 0

        val color = Color.RED
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return Player(
            Util.user?.uid.toString(),
            Util.user?.nickname.toString(),
            Coordinate(
                displayMetrics.widthPixels / 5,
                displayMetrics.heightPixels / 7,
                0f,
                0f,
                BoardPosition("place 1", BoardRegion.TOP, Color.DKGRAY)
            ),
            playerNumber!!,
            color,
            true
        )
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
                    findMatch.visibility = View.VISIBLE
                    createMatch.visibility = View.VISIBLE
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
        db?.child(Constants.DATABASE_REF_USER)?.child(user!!.uid.toString())?.setValue(user)
    }

    private fun goLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setLayoutAttributes() {
        createMatch = findViewById(R.id.create_match)
        findMatch = findViewById(R.id.find_match)
        matchesRV = findViewById(R.id.matches)
    }

    private fun startFirebaseInstances() {
        FirebaseApp.initializeApp(this)
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        val mDatabaseRef = mFirebaseDatabase.reference
        db = mDatabaseRef
        Util.db = db as DatabaseReference

        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult: InstanceIdResult ->
            firebaseInstanceId = instanceIdResult.token
        }
    }
}