package com.acruxcs.lawyer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acruxcs.lawyer.ui.lawyers.LawyersFragment
import com.acruxcs.lawyer.ui.main.MainFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.bottom_nav_home -> {
                val fragment = MainFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                    .commit()
                true
            }
            R.id.bottom_nav_question -> {
                val fragment = LawyersFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                    .commit()
                true
            }
            R.id.bottom_nav_lawyers -> {
                val fragment = LawyersFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                    .commit()
                true
            }
            else -> false
        }
    }
}