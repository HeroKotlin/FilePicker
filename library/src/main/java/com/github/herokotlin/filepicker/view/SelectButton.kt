package com.github.herokotlin.filepicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.github.herokotlin.filepicker.R
import kotlinx.android.synthetic.main.file_picker_select_button.view.*

internal class SelectButton: RelativeLayout {

    var checkable = false

        set(value) {

            field = value

            visibility = if (value) {
                View.VISIBLE
            }
            else {
                View.GONE
            }

        }

    var checked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value
            
            imageView.setImageResource(
                if (value) {
                    R.drawable.file_picker_select_button_checked
                }
                else {
                    R.drawable.file_picker_select_button_unchecked
                }
            )

        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.file_picker_select_button, this)
    }

}