package com.github.herokotlin.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.github.herokotlin.filepicker.databinding.FilePickerActivityBinding
import com.github.herokotlin.filepicker.model.PickedFile
import com.github.herokotlin.filepicker.model.File as PickerFile

class FilePickerActivity: AppCompatActivity() {

    companion object {

        lateinit var callback: FilePickerCallback

        lateinit var configuration: FilePickerConfiguration

        fun newInstance(context: Activity) {
            val intent = Intent(context, FilePickerActivity::class.java)
            context.startActivity(intent)
        }

    }

    private lateinit var binding: FilePickerActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // >= 安卓15 关闭 edge-to-edge
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }

        binding = FilePickerActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.fileListView.init(configuration)
        binding.fileListView.onSelectedFileListChange = {
            binding.topBar.selectedCount = binding.fileListView.selectedFileList.count()
        }

        binding.topBar.configuration = configuration
        if (configuration.submitButtonTitle.isNotEmpty()) {
            binding.topBar.binding.submitButton.text = configuration.submitButtonTitle
        }
        if (configuration.cancelButtonTitle.isNotEmpty()) {
            binding.topBar.binding.cancelButton.text = configuration.cancelButtonTitle
        }
        binding.topBar.binding.cancelButton.setOnClickListener {
            callback.onCancel(this)
        }
        binding.topBar.binding.submitButton.setOnClickListener {
            submit()
        }

        // 请求到权限之后再进来
        FilePickerManager.scan(this, configuration) {
            binding.fileListView.fileList = it
        }

    }

    private fun submit() {

        val selectedList = mutableListOf<PickerFile>()

        binding.fileListView.selectedFileList.forEach {
            selectedList.add(it)
        }

        selectedList.sortBy { it.index }

        callback.onSubmit(
            this,
            selectedList.map { PickedFile.build(it) }
        )

    }

}