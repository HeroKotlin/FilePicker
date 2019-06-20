package com.github.herokotlin.filepicker

import android.app.Activity
import com.github.herokotlin.filepicker.model.PickedFile

interface FilePickerCallback {

    fun onCancel(activity: Activity)

    fun onSubmit(activity: Activity, assetList: List<PickedFile>)

}