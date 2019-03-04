package com.github.herokotlin.filepicker.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.model.File
import com.github.herokotlin.filepicker.R
import kotlinx.android.synthetic.main.file_picker_file_list.view.*

class FileList : FrameLayout {

    var onFileClick: ((File) -> Unit)? = null

    var fileList = listOf<File>()

        set(value) {

            if (value == field) {
                return
            }

            field = value

            adapter.notifyDataSetChanged()

        }

    private lateinit var configuration: FilePickerConfiguration

    private lateinit var adapter: FileListAdapter

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

        LayoutInflater.from(context).inflate(R.layout.file_picker_file_list, this)

        recyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    fun init(configuration: FilePickerConfiguration) {

        this.configuration = configuration

        adapter = FileListAdapter()

        recyclerView.adapter = adapter

    }

    inner class FileListAdapter: RecyclerView.Adapter<FileItem>() {

        override fun getItemCount(): Int {
            return fileList.size
        }

        override fun onBindViewHolder(holder: FileItem, position: Int) {
            holder.bind(position, fileList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItem {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.file_picker_file_item, parent, false)
            return FileItem(view, configuration, {
                onFileClick?.invoke(it)
            }) {
                adapter.notifyItemChanged(it)
            }
        }

    }

}