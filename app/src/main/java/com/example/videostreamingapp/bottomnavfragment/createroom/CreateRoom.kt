package com.example.videostreamingapp.bottomnavfragment.createroom


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.CreateRoomFragmentBinding
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.example.videostreamingapp.ui.RoomActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur


class CreateRoom : Fragment() {

    private lateinit var viewModel: CreateRoomViewModel
    private lateinit var createBinding: CreateRoomFragmentBinding
    private lateinit var videoUrl: EditText
    private lateinit var url: String
    private lateinit var button: Button
    private var progressDialog: CustomProgressDialog = CustomProgressDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(CreateRoomViewModel::class.java)
        val root: View = inflater.inflate(R.layout.create_room_fragment, container, false)

        createBinding =
            CreateRoomFragmentBinding.inflate(inflater, container, false)

        activity?.setActionBar(createBinding.toolbar)
        activity?.title = resources.getString(R.string.create_room)
        videoUrl = root.findViewById(R.id.videoUrl)
        button = root.findViewById(R.id.button)
        createBinding.videoUrl.setText(MainScreenActivity.newUrl)

            uploadVideo("Public")


        createBinding.privacyTypeGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val uploadTypeRadioButton = group
                    .findViewById<View>(checkedId) as RadioButton

                    uploadVideo(uploadTypeRadioButton.text.toString())

            }
        )

        return createBinding.root
        // return inflater.inflate(R.layout.create_r, container, false)
    }

    private fun setBlur(blurView: BlurView) {
        val radius = 15f
        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(activity))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)
    }

    private fun uploadVideo(privacyType: String) {

        createBinding.createBtn.setOnClickListener {
            if (validateForm()){
                sendVideoCreateRoomDataToDB(privacyType)
            }
        }

    }

    private fun sendVideoCreateRoomDataToDB(privacyType: String) {
        progressDialog.show(requireContext(), "Creating room...")
        //  createBinding.progressBar.visibility = View.VISIBLE

        val converter = com.example.videostreamingapp.utils.Converter()
        converter.getCurrentTime1()
        if (validateForm()) {
            converter._time.observeForever(Observer {
                val intent = Intent(requireActivity(), RoomActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("isLive", "false")
                intent.putExtra("typeOfRoom", privacyType)
                intent.putExtra("currentTime", it.toString())
                intent.putExtra("category", createBinding.roomCategory.text.toString().trim())
                //  MainScreenActivity.listOfMember = list
                //  createBinding.progressBar.visibility = View.GONE
                startActivity(intent)
                requireActivity().finish()
            })

        }
    }

    private fun validateForm(): Boolean {
        var check = false
        url = createBinding.videoUrl.text.toString()
        if (url.isEmpty() || !createBinding.videoUrl.text!!.contains("https://youtu.be")) {
            createBinding.videoUrl.error = "This field is required"
            createBinding.roomCategory.error = "This field is required"
        } else if (createBinding.roomCategory.text!!.isEmpty()) {
            createBinding.videoUrl.error = "This field is required"
        } else {
            check = true
        }
        return check
    }


}