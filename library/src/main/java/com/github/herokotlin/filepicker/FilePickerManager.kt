package com.github.herokotlin.filepicker

import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.provider.MediaStore
import com.github.herokotlin.filepicker.model.File

object FilePickerManager {

    private const val PERMISSION_REQUEST_CODE = 12322

    lateinit var onRequestPermissions: (List<String>, Int) -> Boolean

    var onPermissionsGranted: (() -> Unit)? = null

    var onPermissionsDenied: (() -> Unit)? = null

    var onFetchWithoutPermissions: (() -> Unit)? = null

    var onFetchWithoutExternalStorage: (() -> Unit)? = null

    private lateinit var onRequestPermissionsComplete: () -> Unit

    private lateinit var onScanComplete: (List<File>) -> Unit

    private var fileList = mutableListOf<File>()

    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            onScanComplete.invoke(fileList)
        }
    }

    private var scanTask: Thread? = null

    fun scan(context: Context, configuration: FilePickerConfiguration, callback: (List<File>) -> Unit) {

        onScanComplete = callback

        Thread(Runnable {

            // 降低线程优先级
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

            // 存储当前线程，方便停止
            scanTask = Thread.currentThread()

            val contentProvider = context.contentResolver

            val cursor = contentProvider.query(
                MediaStore.Files.getContentUri("external"),
                FilePickerConstant.FILE_FIELDS,
                getSelection(
                    configuration.fileMinSize,
                    configuration.fileMaxSize,
                    configuration.fileMimeTypes
                ),
                null,
                configuration.fileSortField + " " + if (configuration.fileSortAscending) "ASC" else "DESC"
            )

            cursor?.let {

                fileList.clear()

                while (it.moveToNext()) {

                    val file = File.build(
                        it.getString(it.getColumnIndex(FilePickerConstant.FIELD_PATH)),
                        it.getInt(it.getColumnIndex(FilePickerConstant.FIELD_SIZE)),
                        it.getString(it.getColumnIndex(FilePickerConstant.FIELD_MIME_TYPE)),
                        if (configuration.fileSortField == FilePickerConstant.FIELD_UPDATE_TIME) {
                            it.getLong(it.getColumnIndex(FilePickerConstant.FIELD_UPDATE_TIME))
                        }
                        else {
                            it.getLong(it.getColumnIndex(FilePickerConstant.FIELD_CREATE_TIME))
                        }
                    )

                    if (file != null && configuration.filter(file)) {
                        fileList.add(file)
                    }

                }

                it.close()

            }

            // 回到主线程
            handler.sendEmptyMessage(0)

        }).start()

    }

    fun requestPermissions(callback: () -> Unit) {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            onFetchWithoutExternalStorage?.invoke()
            return
        }

        onRequestPermissionsComplete = callback

        if (onRequestPermissions(
                listOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        ) {
            callback()
        }

    }

    fun requestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }

        for (i in 0 until permissions.size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                onPermissionsDenied?.invoke()
                return
            }
        }

        onPermissionsGranted?.invoke()
        onRequestPermissionsComplete()

    }

    private fun getSelection(minSize: Int, maxSize: Int, mimeTypes: List<String>): String? {

        val list = mutableListOf<String>()

        val sizeList = mutableListOf<String>()
        if (minSize > 0) {
            sizeList.add(
                "${FilePickerConstant.FIELD_SIZE} >= $minSize"
            )
        }
        if (maxSize > 0) {
            sizeList.add(
                "${FilePickerConstant.FIELD_SIZE} <= $maxSize"
            )
        }
        if (sizeList.count() > 0) {
            list.add(
                sizeList.joinToString(" AND ")
            )
        }

        if (mimeTypes.count() > 0) {
            val item = mimeTypes.map {"${FilePickerConstant.FIELD_MIME_TYPE} = \"$it\"" }
            list.add(item.joinToString(" OR "))
        }

        if (list.count() > 0) {
            return list.joinToString(" AND ")
        }

        return null

    }

}