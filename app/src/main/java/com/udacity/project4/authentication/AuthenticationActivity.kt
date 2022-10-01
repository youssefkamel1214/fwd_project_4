package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.utils.Constants
import com.udacity.project4.utils.FirebaseUserLiveData

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    val isaldreadyloggedin= FirebaseAuth.getInstance().currentUser!=null
    val userState= FirebaseUserLiveData().map {
        if(it==null){
            Constants.UserLogINState.UNLoggingIN
        }
        else{
            Constants.UserLogINState.LoggingIN
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginbuttton.setOnClickListener{
            buildingAuthui()
        }
        // to check first if already was logged in or not
        if(isaldreadyloggedin){
            startmainactivity()
        }
        userState.observe(this) {
            if (it == Constants.UserLogINState.LoggingIN)
                startmainactivity()
        }
//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun startmainactivity() {
        val intent = Intent(this.applicationContext, RemindersActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun buildingAuthui() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(),
            Constants.SignCode
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SignCode) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode != Activity.RESULT_OK) {
//                Toast.makeText(this,response?.error?.message, Toast.LENGTH_LONG).show()
                Log.i(Constants.AUTHTAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}
