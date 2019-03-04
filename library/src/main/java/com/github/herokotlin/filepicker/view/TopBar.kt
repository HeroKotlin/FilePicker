package com.github.herokotlin.filepicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.R
import kotlinx.android.synthetic.main.file_picker_top_bar.view.*

internal class TopBar: RelativeLayout {

    lateinit var configuration: FilePickerConfiguration

    var selectedCount = -1

        set(value) {

            if (field == value) {
                return
            }

            field = value

            var title = submitButtonTitle
            if (value > 0) {
                submitButton.isEnabled = true
                submitButton.alpha = 1f
                if (configuration.maxSelectCount > 1) {
                    title = "$submitButtonTitle($value/${configuration.maxSelectCount})"
                }
            }
            else {
                submitButton.isEnabled = false
                submitButton.alpha = 0.5f
            }

            submitButton.text = title
        }

    private val submitButtonTitle: String by lazy {
        resources.getString(R.string.file_picker_submit_button_title)
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
        LayoutInflater.from(context).inflate(R.layout.file_picker_top_bar, this)
    }

}