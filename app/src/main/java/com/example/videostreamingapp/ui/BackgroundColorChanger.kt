package com.example.videostreamingapp.ui

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.widget.RelativeLayout


class BackgroundColorChanger {

    fun changeColor(changeBackLayout: RelativeLayout, context: Context, pos: Int) {
//        val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.my_drawable)
//        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
//        DrawableCompat.setTint(wrappedDrawable, Color.RED)
        var color = Color.rgb(252, 245, 95)
        val border = GradientDrawable()
        //   border.setColor(-0x1) //white background
        border.cornerRadius = 12F
       // border.setColor(Color.rgb(252,245,95))
        when (pos) {
            0 -> {
                color = Color.rgb(240, 128, 128)
            }
            1 -> {
                color = Color.rgb(154, 205, 50)
            }
            2 -> {
                color = Color.rgb(30, 144, 255)
            }
            3 -> {
                color = Color.rgb(255, 255, 224)
            }
            4 -> {
                color = Color.rgb(155, 89, 182)
            }
        }
        border.setStroke(5, color)
       // changeBackLayout.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        changeBackLayout.setBackgroundDrawable(border)


    }
}