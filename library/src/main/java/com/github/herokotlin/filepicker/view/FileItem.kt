package com.github.herokotlin.filepicker.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.enum.FileType
import com.github.herokotlin.filepicker.model.File
import com.github.herokotlin.filepicker.R
import com.github.herokotlin.filepicker.databinding.FilePickerFileItemBinding

class FileItem(binding: FilePickerFileItemBinding, private val configuration: FilePickerConfiguration, private val onClick: ((File) -> Unit), private val onToggle: ((File) -> Unit)): RecyclerView.ViewHolder(binding.root) {

    private val iconView = binding.iconView

    private val nameView = binding.nameView

    private val sizeView = binding.sizeView

    private val timeView = binding.timeView

    private val selectButton = binding.selectButton

    private lateinit var file: File

    init {
        binding.root.setOnClickListener {
            onClick.invoke(file)
        }
        selectButton.setOnClickListener {
            onToggle.invoke(file)
        }
    }

    fun bind(file: File) {

        nameView.text = file.name
        sizeView.text = configuration.formatSize(file.size)
        timeView.text = configuration.formatTime(file.time * 1000)

        selectButton.checked = file.selected

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
                FileType.AUDIO -> {
                    R.drawable.file_picker_audio
                }
                else -> {
                    R.drawable.file_picker_txt
                }
            }
        )

        this.file = file

        selectButton.visibility = if (file.selectable) {
            View.VISIBLE
        }
        else {
            View.GONE
        }

    }

}