package com.example.videostreamingapp.loginwithgmail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.videostreamingapp.R
import com.example.videostreamingapp.username.UserNameActivity
import com.example.videostreamingapp.databinding.ActivityLoginWithGmailBinding
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginWithGmail : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignClient: GoogleSignInClient
    private lateinit var signInBtn: SignInButton
    lateinit var loginViewModel: LoginViewModel
    lateinit var loginBinding: ActivityLoginWithGmailBinding
    private var progressDialog: CustomProgressDialog = CustomProgressDialog()

    companion object {
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_gmail)

        loginViewModel = ViewModelProvider(
            this
        ).get(LoginViewModel::class.java)
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_gmail)
        signInBtn = findViewById(R.id.sign_in_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.google_sign_key))
            .requestEmail()
            .build()

        googleSignClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        loginBinding.signInButton.setOnClickListener {
            progressDialog.show(this,"Please wait...")
          //  loginBinding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                signIn()
            }
        }
    }

    private fun signIn() {
        progressDialog.dialog.dismiss()
        val signInIntent = googleSignClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                   // loginBinding.progressBar.visibility = View.GONE
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                }
            }
            else{
              //  loginBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
      //  loginBinding.progressBar.visibility = View.VISIBLE
        progressDialog.show(this,"Preparing...")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    loginViewModel.checkUserOldNew().observe(this, Observer {
                        if (it) {
                          //  loginBinding.progressBar.visibility = View.GONE
                              progressDialog.dialog.dismiss()
                            launchUserNameActivity()
                        } else {
                            directlyLogin(credential)
                        }
                    })
                }
                else{
                    progressDialog.dialog.dismiss()
                  //  loginBinding.progressBar.visibility = View.GONE
                }
            }
    }

    private fun launchUserNameActivity() {
        val intent = Intent(this, UserNameActivity::class.java)
        startActivity(intent)
    }

    private fun directlyLogin(credential: AuthCredential) {
        loginViewModel.signInWithGoogle(credential).observe(this, Observer {
            if (it) {
                progressDialog.dialog.dismiss()
                val intent = Intent(this, MainScreenActivity::class.java)
                startActivity(intent)
            }
        })

    }
}