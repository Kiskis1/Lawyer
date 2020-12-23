package com.acruxcs.lawyer

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.acruxcs.lawyer.databinding.ActivityMainBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.SharedPrefRepository
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_DARK_MODE_ON
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import com.acruxcs.lawyer.repository.SharedPrefRepository.preferences
import com.acruxcs.lawyer.ui.login.LoginFragmentDirections
import com.acruxcs.lawyer.utils.Utils
import com.crazylegend.viewbinding.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient

    private val _binding by viewBinding(ActivityMainBinding::inflate)

    val binding get() = _binding

    private lateinit var navHostFragment: NavHostFragment
    private val viewModel: ActivityViewModel by viewModels()

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
        setContentView(binding.root)
        supportActionBar?.hide()
        SharedPrefRepository.invoke(this)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        binding.bottomMenu.setupWithNavController(navHostFragment.navController)
        binding.bottomMenu.setOnNavigationItemReselectedListener {
            navHostFragment.navController.popBackStack(it.itemId, false)
        }

        viewModel.bottomNavigationVisibility.observe(this) { navVisibility ->
            binding.bottomMenu.visibility = navVisibility
        }
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment,
                R.id.profileFragment,
                R.id.lawyersFragment,
                -> viewModel.showBottomNav()
                else -> viewModel.hideBottomNav()

            }
        }

        if (preferences.getBoolean(SHARED_LOGGED_IN, false) && savedInstanceState == null) {
            val dir = LoginFragmentDirections.actionLoginFragmentToMainFragment()
            navHostFragment.navController
                .navigate(dir)
        }
        appContext = baseContext
        Utils.switchDarkMode(preferences.getBoolean(SHARED_DARK_MODE_ON, false))
        if (savedInstanceState == null) {
            viewModel.getUserData(Firebase.auth.currentUser?.uid)
        } else {
            binding.progressBar.progressLayout.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        when (navHostFragment.navController.currentDestination?.id) {
            R.id.mainFragment,
            R.id.loginFragment,
            -> finish()
            R.id.lawyersFragment,
            R.id.profileFragment,
            -> navHostFragment.navController.navigate(R.id.mainFragment)
            else -> super.onBackPressed()
        }
    }

    companion object {
        val user = MutableLiveData<User>()
        lateinit var appContext: Context
    }
}
