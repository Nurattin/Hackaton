package com.smartdev.hackaton.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.smartdev.hackaton.R

class CustomPointer @JvmOverloads
constructor(ctx: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    init {

        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.custom_pointer, this)
    }

    fun setValues(place: Int = 1, isChecked: Boolean) {
        val place_txt = findViewById<TextView>(R.id.place)
        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        if (isChecked){
            imageButton.setImageResource(R.drawable.ic_pointer_check)
        }
        place_txt.text = place.toString()
    }


    fun setColor(){
        val place_txt = findViewById<TextView>(R.id.place)
        place_txt.text = "asda"
    }
}