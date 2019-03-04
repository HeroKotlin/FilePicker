package com.github.herokotlin.filepicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.enum.FileType
import com.github.herokotlin.filepicker.model.File
import com.github.herokotlin.filepicker.R
import kotlinx.android.synthetic.main.file_picker_file_item.view.*

class FileItem(view: View, private val configuration: FilePickerConfiguration, private val onClick: ((File) -> Unit), private val onToggle: ((Int) -> Unit)): RecyclerView.ViewHolder(view) {

    private val iconView = view.iconView

    private val nameView = view.nameView

    private val sizeView = view.sizeView

    private val timeView = view.timeView

    private val selectButton = view.selectButton

    private var index = -1

    private lateinit var file: File

    init {
        view.setOnClickListener {
            onClick.invoke(file)
        }
        selectButton.setOnClickListener {
            file.selected = !file.selected
            onToggle.invoke(index)
        }
    }

    fun bind(index: Int, file: File) {

        nameView.text = file.name
        sizeView.text = configuration.formatSize(file.size)
        timeView.text = configuration.formatTime(file.time * 1000)

        iconView.setImageResource(
            when (file.type) {
                FileType.WORD -> {
                    R.drawable.file_picker_word
                }
                FileType.EXCEL -> {
                    R.drawable.file_picker_excel
                }
                FileType.PPT -> {
                    R.drawable.file_picker_ppt
                }
                FileType.PDF -> {
                    R.drawable.file_picker_pdf
                }
                else -> {
                    R.drawable.file_picker_txt
                }
            }
        )

        selectButton.setImageResource(
            if (file.selected) {
                R.drawable.file_picker_select_button_checked
            }
            else {
                R.drawable.file_picker_select_button_unchecked
            }
        )

        this.index = index
        this.file = file

    }

}