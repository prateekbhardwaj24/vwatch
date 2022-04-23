package com.example.videostreamingapp.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.videostreamingapp.R

class CustomProgressDialog {
    lateinit var dialog: CustomDialog

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_layout, null)
        if (title != null) {
            view.findViewById<TextView>(R.id.cp_title).text = title
        }

        // Card Color
        view.findViewById<LinearLayout>(R.id.cp_cardview).setBackgroundResource(R.drawable.progress_backgrpund)

        // Progress Bar Color
        setColorFilter(view.findViewById<ProgressBar>(R.id.cp_bar).indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.purple_200, null))

        // Text Color
       // view.cp_title.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }
    fun show(context: Context, title: String,s:String): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_layout, null)
        if (title != null) {
            view.findViewById<TextView>(R.id.cp_title).text = title
        }

        // Card Color
        view.findViewById<LinearLayout>(R.id.cp_cardview).setBackgroundResource(R.drawable.progress_backgrpund)
        view.findViewById<ProgressBar>(R.id.cp_bar).visibility = View.GONE
        // Progress Bar Color
    //    setColorFilter(view.findViewById<ProgressBar>(R.id.cp_bar).indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.purple_200, null))

        // Text Color
        // view.cp_title.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundColor(Color.parseColor("#8B000000"))
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}