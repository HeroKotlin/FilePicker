package com.github.herokotlin.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.herokotlin.filepicker.model.PickedFile
import com.github.herokotlin.filepicker.model.File as PickerFile
import kotlinx.android.synthetic.main.file_picker_activity.*
import kotlinx.android.synthetic.main.file_picker_top_bar.view.*
import java.io.File

import java.util.regex.Pattern

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

        // 先排序
        val selectedList = mutableListOf<PickerFile>()

        fileListView.selectedFileList.forEach {
            selectedList.add(it)
        }

        selectedList.sortBy { it.index }

        // 文件名包含其他字符，需转存一份，避免调用者出现编码问题，导致无法上传
        val pattern = Pattern.compile("[^A-Za-z0-9_]")
        val targetPathPrefix = "${externalCacheDir.absolutePath}/${System.currentTimeMillis()}"

        val result = selectedList.map {

            var path = it.path
            var fileName = it.name

            var extName = ""

            val index = fileName.indexOf(".")
            if (index > 0) {
                extName = fileName.substring(index)
                fileName = fileName.substring(0, index)
            }

            if (pattern.matcher(fileName).find()) {
                val source = File(path)
                path = "$targetPathPrefix${it.index}$extName"
                source.copyTo(File(path), true)
            }

            // 保持源文件名
            PickedFile(path, it.name, it.size)
        }

        callback.onSubmit(this, result)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FilePickerManager.requestPermissionsResult(requestCode, permissions, grantResults)
    }

}