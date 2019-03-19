package com.github.herokotlin.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.herokotlin.filepicker.model.PickedFile
import com.github.herokotlin.filepicker.model.File as PickerFile
import kotlinx.android.synthetic.main.file_picker_activity.*
import kotlinx.android.synthetic.main.file_picker_top_bar.view.*

class FilePickerActivity: AppCompatActivity() {

    companion object {

        lateinit var callback: FilePickerCallback

        lateinit var configuration: FilePickerConfiguration

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

        FilePickerManager.onPermissionsGranted = {
            callback.onPermissionsGranted(this)
        }
        FilePickerManager.onPermissionsDenied = {
            callback.onPermissionsDenied(this)
        }
        FilePickerManager.onFetchWithoutPermissions = {
            callback.onFetchWithoutPermissions(this)
        }
        FilePickerManager.onFetchWithoutExternalStorage = {
            callback.onFetchWithoutExternalStorage(this)
        }
        FilePickerManager.onRequestPermissions = { permissions, requestCode ->
            configuration.requestPermissions(this, permissions, requestCode)
        }

        FilePickerManager.requestPermissions {
            FilePickerManager.scan(this, configuration) {
                fileListView.fileList = it
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FilePickerManager.requestPermissionsResult(requestCode, permissions, grantResults)
    }

}