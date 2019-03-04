package com.github.herokotlin.filepicker

import android.app.Activity
import android.provider.MediaStore
import com.github.herokotlin.filepicker.model.File
import java.text.SimpleDateFormat
import java.util.*

abstract class FilePickerConfiguration {

    /**
     * 最多选择多少个文件
     */
    var maxSelectCount = 9

    /**
     * 扫描磁盘时，设置包含的文件类型（include 和 exclude 只能二选一）
     */
    var includeFileMediaTypes = listOf<Int>()

    /**
     * 扫描磁盘时，设置剔除的文件类型
     */
    var excludeFileMediaTypes = listOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO,
        MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST
    )

    /**
     * 文件最小尺寸
     */
    var minSize = FilePickerConstant.SIZE_KB

    /**
     * 文件最大尺寸
     */
    var maxSize = 20 * FilePickerConstant.SIZE_MB

    /**
     * 排序字段
     */
    var fileSortField = FilePickerConstant.FIELD_UPDATE_TIME

    /**
     * 是否正序
     */
    var fileSortAscending = false

    /**
     * 请求权限
     */
    abstract fun requestPermissions(activity: Activity, permissions: List<String>, requestCode: Int): Boolean

    /**
     * 格式化文件尺寸
     */
    open fun formatSize(size: Int): String {

        val unit = FilePickerConstant.SIZE_KB
        if (size < unit) {
            return "$size B"
        }

        val exp = (Math.log(size.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = ("KMGTPE")[exp - 1]

        return String.format("%.1f %sB", size / Math.pow(unit.toDouble(), exp.toDouble()), pre)

    }

    /**
     * 格式化文件时间
     */
    open fun formatTime(time: Long): String {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)

        calendar.time = Date(time)

        val pattern = if (year == calendar.get(Calendar.YEAR)) {
            if (month == calendar.get(Calendar.MONTH)
                && date == calendar.get(Calendar.DATE)
            ) {
                "HH:mm"
            }
            else {
                "MM月dd日"
            }
        }
        else {
            "yyyy年MM月dd日"
        }

        return SimpleDateFormat(pattern, Locale.US).format(calendar.time)

    }

    /**
     * 过滤文件
     */
    open fun filter(file: File): Boolean {
        return file.name.indexOf(".") > 0
    }

}