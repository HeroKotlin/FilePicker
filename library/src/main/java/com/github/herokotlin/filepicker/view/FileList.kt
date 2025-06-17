package com.github.herokotlin.filepicker.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.filepicker.FilePickerConfiguration
import com.github.herokotlin.filepicker.model.File
import com.github.herokotlin.filepicker.databinding.FilePickerFileItemBinding
import com.github.herokotlin.filepicker.databinding.FilePickerFileListBinding
import androidx.core.view.isVisible

class FileList : FrameLayout {

    lateinit var binding: FilePickerFileListBinding

    var onFileClick: ((File) -> Unit)? = null

    var onSelectedFileListChange: (() -> Unit)? = null

    var fileList = listOf<File>()

        set(value) {

            // 这里不应判断 value.count() > 0
            // 如果手机里确实没有对应的文件，返回空数组是很正常的事
            if (binding.spinnerView.isVisible) {
                binding.spinnerView.visibility = View.GONE
                if (value.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                }
                else {
                    binding.emptyView.visibility = View.VISIBLE
                }
            }

            field = value

            adapter.notifyDataSetChanged()

        }

    var selectedFileList = mutableListOf<File>()

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

        binding = FilePickerFileListBinding.inflate(LayoutInflater.from(context), this, true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    fun init(configuration: FilePickerConfiguration) {

        this.configuration = configuration

        adapter = FileListAdapter()

        binding.recyclerView.adapter = adapter

    }

    private fun toggleSingleChecked(file: File) {

        // checked 获取反选值
        val checked = !file.selected
        val selectedCount = selectedFileList.count()

        if (selectedCount == 1) {
            val selectedFile = selectedFileList[0]
            selectedFile.selected = false
            selectedFileList.remove(selectedFile)
            // 如果只是点击取消选择，要触发回调
            // 否则跟下面那段一起触发回调
            if (!checked) {
                onSelectedFileListChange?.invoke()
            }
            adapter.notifyItemChanged(selectedFile.index)
        }

        if (checked) {
            file.selected = true
            selectedFileList.add(file)
            onSelectedFileListChange?.invoke()
            adapter.notifyItemChanged(file.index)
        }

    }

    private fun toggleMultiChecked(file: File) {

        val selected = !file.selected
        val selectedCount = selectedFileList.count()

        if (selected) {

            if (selectedCount == configuration.maxSelectCount) {
                return
            }

            file.selected = true
            selectedFileList.add(file)
            onSelectedFileListChange?.invoke()

            // 到达最大值，就无法再选了
            if (selectedCount + 1 == configuration.maxSelectCount) {
                adapter.notifyDataSetChanged()
            }
            else {
                adapter.notifyItemChanged(file.index)
            }

        }
        else {

            selectedFileList.remove(file)
            onSelectedFileListChange?.invoke()

            file.selected = false

            // 上个状态是到达上限
            if (selectedCount == configuration.maxSelectCount) {
                adapter.notifyDataSetChanged()
            }
            else {
                adapter.notifyItemChanged(file.index)
            }

        }

    }

    inner class FileListAdapter: RecyclerView.Adapter<FileItem>() {

        override fun getItemCount(): Int {
            return fileList.size
        }

        override fun onBindViewHolder(holder: FileItem, position: Int) {

            val file = fileList[position]

            file.index = position

            file.selectable = if (file.selected) {
                true
            }
            // 单选总是可选
            else if (configuration.maxSelectCount == 1) {
                true
            }
            else {
                selectedFileList.count() < configuration.maxSelectCount
            }

            holder.bind(file)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItem {
            val binding = FilePickerFileItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return FileItem(
                binding,
                configuration,
                {
                    onFileClick?.invoke(it)
                },
                {
                    if (configuration.maxSelectCount > 1) {
                        toggleMultiChecked(it)
                    }
                    else {
                        toggleSingleChecked(it)
                    }
                }
            )
        }

    }

}