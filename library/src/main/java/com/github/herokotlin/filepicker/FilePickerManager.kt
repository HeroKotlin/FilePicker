package com.github.herokotlin.filepicker

import android.content.Context
import android.os.*
import android.provider.MediaStore
import com.github.herokotlin.filepicker.model.File

object FilePickerManager {

    private lateinit var onScanComplete: (List<File>) -> Unit

    private var fileList = mutableListOf<File>()

    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
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

            val query = getQuery(
                configuration.fileMinSize,
                configuration.fileMaxSize,
                configuration.fileMimeTypes
            )

            val cursor = contentProvider.query(
                MediaStore.Files.getContentUri("external"),
                FilePickerConstant.FILE_FIELDS,
                query.where,
                query.args.toTypedArray(),
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

                    var path = ""
                    var size = 0
                    var mimeType = ""
                    var time: Long = 0

                    val pathIndex = it.getColumnIndex(FilePickerConstant.FIELD_PATH)
                    val sizeIndex = it.getColumnIndex(FilePickerConstant.FIELD_SIZE)
                    val mimeTypeIndex = it.getColumnIndex(FilePickerConstant.FIELD_MIME_TYPE)
                    val timeIndex = it.getColumnIndex(FIELD_TIME)

                    if (pathIndex >= 0 ) {
                        path = it.getString(pathIndex)
                    }
                    if (sizeIndex >= 0 ) {
                        size = it.getInt(sizeIndex)
                    }
                    if (mimeTypeIndex >= 0 ) {
                        mimeType = it.getString(mimeTypeIndex)
                    }
                    if (timeIndex >= 0 ) {
                        time = it.getLong(timeIndex)
                    }

                    val file = File.build(path, size, mimeType, time)
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

    private fun getQuery(minSize: Int, maxSize: Int, mimeTypes: List<String>): Query {

        val args = mutableListOf<String>()
        val where = mutableListOf<String>()

        val sizeList = mutableListOf<String>()
        if (minSize > 0) {
            args.add(minSize.toString())
            sizeList.add(
                "${FilePickerConstant.FIELD_SIZE} >= ?"
            )
        }
        if (maxSize > 0) {
            args.add(maxSize.toString())
            sizeList.add(
                "${FilePickerConstant.FIELD_SIZE} <= ?"
            )
        }
        if (sizeList.count() > 0) {
            where.add(
                sizeList.joinToString(" AND ")
            )
        }

        if (mimeTypes.count() > 0) {
            val item = mimeTypes.map {
                args.add(it)
                "${FilePickerConstant.FIELD_MIME_TYPE} = ?"
            }
            where.add(item.joinToString(" OR "))
        }

        var whereStr = ""

        val count = where.count()
        if (count > 0) {
            whereStr = if (count > 1) {
                where.joinToString(" AND ") { "($it)" }
            } else {
                where[0]
            }
        }

        return Query(whereStr, args)

    }

    private class Query(val where: String, val args: List<String>)

}