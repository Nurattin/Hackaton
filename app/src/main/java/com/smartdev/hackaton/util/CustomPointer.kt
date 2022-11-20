package com.smartdev.hackaton.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.smartdev.hackaton.R

class CustomPointer @JvmOverloads
constructor(ctx: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    init {

        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.custom_pointer, this)
    }

    fun setValues(place: Int = 1, isChecked: Boolean, category: String) {
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        val place_txt = findViewById<TextView>(R.id.place)

        when (category) {
            "Интересные" -> imageButton.setImageResource(R.drawable.ic_interest)
            "Спорт" -> imageButton.setImageResource(
                listOf(
                    R.drawable.ic_share,
                    R.drawable.ic_sport_variant
                ).random()
            )
            "Канпинг" -> imageButton.setImageResource(R.drawable.ic_camp)
            "Парк" -> imageButton.setImageResource(R.drawable.ic_parck)
            "Отдых" -> imageButton.setImageResource(R.drawable.ic_relaxation)
            "Университет" -> imageButton.setImageResource(R.drawable.ic_university)
            else -> imageButton.setImageResource(R.drawable.marker)
        }
    }


    fun setColor() {
        val place_txt = findViewById<TextView>(R.id.place)
        place_txt.text = "asda"
    }
}