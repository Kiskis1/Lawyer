package com.acruxcs.lawyer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    private lateinit var bottomNavigation: BottomNavigationView
    lateinit var googleSignInClient: GoogleSignInClient
    private var user = MutableLiveData<User>()
    private lateinit var sh: SharedPreferences

    var dataLoadedListener: DataLoadedListener? = null

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sh = getSharedPreferences(Utils.SHARED_KEY, 0)

        bottomNavigation = findViewById(R.id.bottom_menu)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_menu)
            .setupWithNavController(navController)
        getUserData(Firebase.auth.currentUser?.uid)
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

    private fun getUserData(userId: String?): MutableLiveData<User> {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user.value = snapshot.getValue(User::class.java)
                println(user.value)
                with(sh.edit()) {
                    putString(Utils.SHARED_USER_DATA, Gson().toJson(user.value))
                    apply()
                }
                dataLoadedListener?.dataLoaded()
                println(user.value)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        FirebaseRepository.getUser(userId)
            ?.addValueEventListener(userListener)

        return user
    }

    interface DataLoadedListener {
        fun dataLoaded()
    }

    fun setActivityListener(listener: DataLoadedListener?) {
        this.dataLoadedListener = listener
    }
}