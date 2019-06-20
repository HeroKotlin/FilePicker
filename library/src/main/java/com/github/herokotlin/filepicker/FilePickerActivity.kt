package com.github.herokotlin.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.herokotlin.filepicker.model.PickedFile
import com.github.herokotlin.permission.Permission
import com.github.herokotlin.filepicker.model.File as PickerFile
import kotlinx.android.synthetic.main.file_picker_activity.*
import kotlinx.android.synthetic.main.file_picker_top_bar.view.*

class FilePickerActivity: AppCompatActivity() {

    companion object {

        lateinit var callback: FilePickerCallback

        lateinit var configuration: FilePickerConfiguration

        val permission = Permission(19903, listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))

        fun newInstance(context: Activity) {
            val intent = Intent(context, FilePickerActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.file_picker_activity)

        fileListView.init(configuration)
        fileListView.onSelectedFileListChange = {
            topBar.selectedCount = fileListView.selectedFileList.count()
        }

        topBar.configuration = configuration
        if (configuration.submitButtonTitle.isNotEmpty()) {
            topBar.submitButton.text = configuration.submitButtonTitle
        }
        if (configuration.cancelButtonTitle.isNotEmpty()) {
            topBar.cancelButton.text = configuration.cancelButtonTitle
        }
        topBar.cancelButton.setOnClickListener {
            callback.onCancel(this)
        }
        topBar.submitButton.setOnClickListener {
            submit()
        }

        // 请求到权限之后再进来
        FilePickerManager.scan(this, configuration) {
            fileListView.fileList = it
        }

    }

    private fun submit() {

        val selectedList = mutableListOf<PickerFile>()

        fileListView.selectedFileList.forEach {
            selectedList.add(it)
        }

        selectedList.sortBy { it.index }

        callback.onSubmit(
            this,
            selectedList.map { PickedFile.build(it) }
        )

    }

}