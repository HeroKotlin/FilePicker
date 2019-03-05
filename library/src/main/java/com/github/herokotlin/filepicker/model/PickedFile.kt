package com.github.herokotlin.filepicker.model

import java.net.URLEncoder
import java.util.regex.Pattern
import java.io.File as NativeFile

data class PickedFile(
    val path: String,
    val name: String,
    val size: Int
) {
    companion object {

        // 文件名包含其他字符，需转存一份，避免调用者出现编码问题，导致无法上传
        private val pattern = Pattern.compile("[^A-Za-z0-9_]")

        fun build(file: File, cacheDir: String): PickedFile {

            var path = file.path
            var fileName = file.name

            var extName = ""

            val index = fileName.indexOf(".")
            if (index > 0) {
                extName = fileName.substring(index)
                fileName = fileName.substring(0, index)
            }

            if (pattern.matcher(fileName).find()) {
                val source = NativeFile(path)
                fileName = URLEncoder.encode(fileName, "utf-8")
                path = "$cacheDir/$fileName$extName"
                source.copyTo(NativeFile(path), true)
            }

            return PickedFile(path, file.name, file.size)

        }

    }
}