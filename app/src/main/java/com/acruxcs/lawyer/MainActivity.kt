package com.acruxcs.lawyer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient
    private var dataLoadedListener: DataLoadedListener? = null

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.init(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottom_menu.setupWithNavController(navHostFragment.navController)

        Utils.switchDarkMode(Utils.preferences.getBoolean(Utils.SHARED_DARK_MODE_ON, false))
        if (savedInstanceState == null) {
            bottom_menu.visibility = View.GONE
            getUserData(Firebase.auth.currentUser?.uid)
        }
    }

    override fun onBackPressed() {
        if (nav_host_fragment.findNavController().currentDestination?.id == R.id.mainFragment ||
            nav_host_fragment.findNavController().currentDestination?.id == R.id.lawyersFragment ||
            nav_host_fragment.findNavController().currentDestination?.id == R.id.profileFragment ||
            nav_host_fragment.findNavController().currentDestination?.id == R.id.loginFragment
        ) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getUserData(userId: String?) {
        bottom_menu.visibility = View.GONE
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                Utils.preferences
                    .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(temp)) }
                dataLoadedListener?.dataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        FirebaseRepository.getUser(userId)
            ?.addValueEventListener(userListener)
    }

    interface DataLoadedListener {
        fun dataLoaded()
    }

    fun setActivityListener(listener: DataLoadedListener?) {
        this.dataLoadedListener = listener
    }
}
