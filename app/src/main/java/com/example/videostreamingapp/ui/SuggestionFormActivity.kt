package com.example.videostreamingapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.videostreamingapp.databinding.ActivitySuggestionBinding
import com.example.videostreamingapp.mainscreen.MainScreenActivity

class SuggestionFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuggestionBinding
    private lateinit var viewModel: SuggestionViewModel
    private var progressDialog: CustomProgressDialog = CustomProgressDialog()

    @SuppressLint("UseSupportActionBar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuggestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SuggestionViewModel::class.java)
        setActionBar(binding.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = "Your Suggestion"
        actionBar!!.subtitle = "You can add bug report/feature request"
        binding.sendSuggestionBtn.setOnClickListener {
            if (validate()) {
                progressDialog.show(this, "Please wait...")
                makeSuggestionDb(binding.suggestionText.text.toString())
                binding.suggestionText.text!!.clear()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
        }
    }

    private fun makeSuggestionDb(text: String) {
        viewModel.makeSuggestionRequestToDb(text)
        viewModel._makeSuggestionRequestToDb.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "Your Suggestion is send successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Retry: Suggestion not send", Toast.LENGTH_SHORT)
                    .show()
            }
            Log.d("checkFrT", "check three : $it")

            progressDialog.dialog.dismiss()
        })


    }

    private fun validate(): Boolean {
        return if (binding.suggestionText.text.toString().isEmpty()) {
            binding.suggestionText.error = "Please write some suggestions"
            false
        } else {
            true
        }
    }
}