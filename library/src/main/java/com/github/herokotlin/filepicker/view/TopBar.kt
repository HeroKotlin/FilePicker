package com.github.herokotlin.filepicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.R
import com.github.herokotlin.filepicker.databinding.FilePickerTopBarBinding

internal class TopBar: RelativeLayout {

    lateinit var binding: FilePickerTopBarBinding

    lateinit var configuration: FilePickerConfiguration

    var selectedCount = -1

        set(value) {

            if (field == value) {
                return
            }

            field = value

            var title = submitButtonTitle
            if (value > 0) {
                binding.submitButton.isEnabled = true
                binding.submitButton.alpha = 1f
                if (configuration.maxSelectCount > 1) {
                    title = "$submitButtonTitle($value/${configuration.maxSelectCount})"
                }
            }
            else {
                binding.submitButton.isEnabled = false
                binding.submitButton.alpha = 0.5f
            }

            binding.submitButton.text = title
        }

    private val submitButtonTitle: String by lazy {
        if (configuration.submitButtonTitle.isEmpty()) {
            resources.getString(R.string.file_picker_submit_button_title)
        }
        else {
            configuration.submitButtonTitle
        }
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
        binding = FilePickerTopBarBinding.inflate(LayoutInflater.from(context), this, true)
    }

}