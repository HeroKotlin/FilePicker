package com.github.herokotlin.filepicker

import android.content.Context
import android.os.*
import android.provider.MediaStore
import com.github.herokotlin.filepicker.model.File

object FilePickerManager {

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

                val FIELD_TIME = if (configuration.fileSortField == FilePickerConstant.FIELD_UPDATE_TIME) {
                    FilePickerConstant.FIELD_UPDATE_TIME
                }
                else {
                    FilePickerConstant.FIELD_CREATE_TIME
                }

                while (it.moveToNext()) {

                    val file = File.build(
                        it.getString(it.getColumnIndex(FilePickerConstant.FIELD_PATH)),
                        it.getInt(it.getColumnIndex(FilePickerConstant.FIELD_SIZE)),
                        it.getString(it.getColumnIndex(FilePickerConstant.FIELD_MIME_TYPE)),
                        it.getLong(it.getColumnIndex(FIELD_TIME))
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