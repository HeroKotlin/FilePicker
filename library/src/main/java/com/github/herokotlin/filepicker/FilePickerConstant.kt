package com.github.herokotlin.filepicker

import android.provider.MediaStore

object FilePickerConstant {

    const val SIZE_KB = 1024

    const val SIZE_MB = 1024 * SIZE_KB

    const val FIELD_PATH = MediaStore.Files.FileColumns.DATA

    const val FIELD_SIZE = MediaStore.Files.FileColumns.SIZE

    const val FIELD_CREATE_TIME = MediaStore.Files.FileColumns.DATE_ADDED

    const val FIELD_UPDATE_TIME = MediaStore.Files.FileColumns.DATE_MODIFIED

    const val FIELD_MIME_TYPE = MediaStore.Files.FileColumns.MIME_TYPE

    const val FIELD_MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE

    val FILE_FIELDS = arrayOf(FIELD_PATH, FIELD_SIZE, FIELD_CREATE_TIME, FIELD_UPDATE_TIME, FIELD_MIME_TYPE)

    const val MIME_TYPE_TEXT = "text/plain"

    const val MIME_TYPE_CSV = "text/comma-separated-values"

    const val MIME_TYPE_PDF = "application/pdf"

    const val MIME_TYPE_DOC = "application/msword"

    const val MIME_TYPE_XLS = "application/vnd.ms-excel"

    const val MIME_TYPE_PPT = "application/vnd.ms-powerpoint"

    const val MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    const val MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    const val MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    const val MIME_TYPE_M4A = "audio/mpeg"


    const val MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE

    const val MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

    const val MEDIA_TYPE_AUDIO = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO

    const val MEDIA_TYPE_PLAYLIST = MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST

}