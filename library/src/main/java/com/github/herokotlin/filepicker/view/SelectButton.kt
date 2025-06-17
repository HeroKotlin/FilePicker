package com.github.herokotlin.filepicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.filepicker.R
import com.github.herokotlin.filepicker.databinding.FilePickerSelectButtonBinding

internal class SelectButton: RelativeLayout {

    lateinit var binding: FilePickerSelectButtonBinding

    var checked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.imageView.setImageResource(
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
        binding = FilePickerSelectButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

}