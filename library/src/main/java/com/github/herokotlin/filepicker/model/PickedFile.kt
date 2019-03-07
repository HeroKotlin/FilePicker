package com.github.herokotlin.filepicker.model

data class PickedFile(
    val path: String,
    val name: String,
    val size: Int
) {
    companion object {

        fun build(file: File): PickedFile {
            return PickedFile(file.path, file.name, file.size)
        }

    }
}