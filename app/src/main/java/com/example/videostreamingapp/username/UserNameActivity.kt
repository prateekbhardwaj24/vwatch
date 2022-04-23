package com.example.videostreamingapp.username

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.videostreamingapp.loginwithgmail.LoginViewModel

import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.ActivityUserNameBinding
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.ui.CustomProgressDialog


class UserNameActivity : AppCompatActivity() {
    lateinit var loginViewModel: LoginViewModel
    lateinit var userNameBindig: ActivityUserNameBinding
    private lateinit var gender:String
    private var progressDialog:CustomProgressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(
            this
        ).get(LoginViewModel::class.java)

        userNameBindig = DataBindingUtil.setContentView(this, R.layout.activity_user_name)
        userNameBindig.userNameInputBind = loginViewModel
        userNameBindig.doneButton.setOnClickListener {
            if (validateForm()) {
                progressDialog.show(this,"Creating Account...")
                loginViewModel.insertUserData(userNameBindig.userNameInput.text.toString(),gender)
                    .observe(this, Observer {
                        if (it) {
                            Log.d("123check", "dataif: " + " $it")
                            val intent = Intent(this, MainScreenActivity::class.java)
                            startActivity(intent)
                            finish()
                            progressDialog.dialog.dismiss()
                        } else {
                            Log.d("123check", "data: " + " blnk")
                            Toast.makeText(this,"Please login again",Toast.LENGTH_SHORT).show()
                            progressDialog.dialog.dismiss()
                        }
                    })
            }
        }

        userNameBindig.maleBox.setOnClickListener {
            userNameBindig.genderL.setBackgroundResource(0)
            userNameBindig.femaleBox.setBackgroundResource(0)
            userNameBindig.maleBox.background = ContextCompat.getDrawable(this, R.drawable.gender_select_border)
            gender = "Male"
        }
        userNameBindig.femaleBox.setOnClickListener {
            userNameBindig.genderL.setBackgroundResource(0)
            userNameBindig.maleBox.setBackgroundResource(0)
            userNameBindig.femaleBox.background = ContextCompat.getDrawable(this, R.drawable.gender_select_border)
            gender = "Female"
        }

    }

    private fun validateForm(): Boolean {
        var check = false
        if (!this::gender.isInitialized){
            userNameBindig.genderL.background = ContextCompat.getDrawable(this, R.drawable.gender_select_border)
        }else if(userNameBindig.userNameInput.text.toString().isEmpty()){
            userNameBindig.userNameInput.error = "Required"
        }else{
            check =  true
        }
        return check
    }
}