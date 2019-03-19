package com.github.herokotlin.filepicker

import android.app.Activity
import com.github.herokotlin.filepicker.model.File
import java.text.SimpleDateFormat
import java.util.*

abstract class FilePickerConfiguration {

    /**
     * 最多选择多少个文件
     */
    var maxSelectCount = 9

    /**
     * 扫描磁盘时，设置包含的文件类型
     */
    var fileMimeTypes = listOf(
        FilePickerConstant.MIME_TYPE_CSV,
        FilePickerConstant.MIME_TYPE_PDF,
        FilePickerConstant.MIME_TYPE_DOC,
        FilePickerConstant.MIME_TYPE_XLS,
        FilePickerConstant.MIME_TYPE_PPT,
        FilePickerConstant.MIME_TYPE_DOCX,
        FilePickerConstant.MIME_TYPE_XLSX,
        FilePickerConstant.MIME_TYPE_PPTX
    )

    /**
     * 文件最小尺寸，设置为 0 表示不限制
     */
    var fileMinSize = FilePickerConstant.SIZE_KB

    /**
     * 文件最大尺寸，设置为 0 表示不限制
     */
    var fileMaxSize = 20 * FilePickerConstant.SIZE_MB

    /**
     * 排序字段
     */
    var fileSortField = FilePickerConstant.FIELD_UPDATE_TIME

    /**
     * 是否正序
     */
    var fileSortAscending = false

    /**
     * 当天的时间格式
     */
    var dateFormatCurrentDate = "HH:mm"

    /**
     * 当年的时间格式
     */
    var dateFormatCurrentYear = "MM月dd日"

    /**
     * 比较早期的时间格式
     */
    var dateFormatAnyTime = "yyyy年MM月dd日"

    /**
     * 确定按钮的文字
     */
    var submitButtonTitle = "确定"

    /**
     * 取消按钮的文字
     */
    var cancelButtonTitle = "取消"

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
                dateFormatCurrentDate
            }
            else {
                dateFormatCurrentYear
            }
        }
        else {
            dateFormatAnyTime
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