package com.acruxcs.lawyer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var bottomNavigation: BottomNavigationView
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_menu)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_menu)
            .setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (nav_host_fragment.findNavController().currentDestination?.id == R.id.mainFragment ||
            nav_host_fragment.findNavController().currentDestination?.id == R.id.lawyersFragment
        ) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        var firebaseUser: FirebaseUser? = Firebase.auth.currentUser
    }
}