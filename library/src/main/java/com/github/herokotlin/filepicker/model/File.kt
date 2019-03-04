package com.github.herokotlin.filepicker.model

import com.github.herokotlin.filepicker.FilePickerConstant
import com.github.herokotlin.filepicker.enum.FileType

data class File(
    val path: String,
    var name: String,
    var size: Int,
    val type: FileType,
    var time: Long,

    var index: Int = 0,
    var selected: Boolean = false,
    var selectable: Boolean = false
) {
    companion object {

        fun build(path: String, size: Int, mimeType: String?, time: Long): File? {

            // 读取 DISPLAY_NAME 经常为空，读取 TITLE 没有扩展名
            // 因此这里只传入 path 就行了，build 函数来提取文件名

            val fileType = when (mimeType) {
                FilePickerConstant.MIME_TYPE_TEXT -> {
                    FileType.TXT
                }
                FilePickerConstant.MIME_TYPE_PDF -> {
                    FileType.PDF
                }
                FilePickerConstant.MIME_TYPE_DOC -> {
                    FileType.WORD
                }
                FilePickerConstant.MIME_TYPE_DOCX -> {
                    FileType.WORD
                }
                FilePickerConstant.MIME_TYPE_XLS -> {
                    FileType.EXCEL
                }
                FilePickerConstant.MIME_TYPE_XLSX -> {
                    FileType.EXCEL
                }
                FilePickerConstant.MIME_TYPE_PPT -> {
                    FileType.PPT
                }
                FilePickerConstant.MIME_TYPE_PPTX -> {
                    FileType.PPT
                }
                FilePickerConstant.MIME_TYPE_CSV -> {
                    FileType.EXCEL
                }
                else -> {
                    FileType.UNKNOWN
                }
            }

            if (fileType == FileType.UNKNOWN) {
                return null
            }

            val index = path.lastIndexOf("/")
            val name: String

            if (index >= 0) {
                name = path.substring(index + 1)
            }
            else {
                return null
            }

            return File(path, name, size, fileType, time)

        }

    }
}