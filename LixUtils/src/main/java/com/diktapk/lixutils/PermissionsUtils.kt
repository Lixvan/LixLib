package com.diktapk.lixutils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionsUtils constructor(private val fragment : Fragment, private val onGrantedPermissionsListener : OnGrantedPermissions) {

    enum class TypePermission constructor( val permissions: Array<String>){

        ReadMediaImages(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            else
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        ),

        ReadMediaVideo(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            else
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        ),

        ReadMediaAudio(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            else
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        ),

        ReadMediaMultimedia(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
            else
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        ),

        LocationCoarseAndFineLocation(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        ),

        Camera(arrayOf(Manifest.permission.CAMERA))

    }

    private var activityResult : ActivityResultLauncher<Array<String>>
    private var codeRequest : String ? = null


    init {
        activityResult = onlyRegisterForActivityResult()
    }

    private fun onlyRegisterForActivityResult(): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult( ActivityResultContracts.RequestMultiplePermissions()){
            verifyPermissions(it, onGrantedPermissionsListener)
        }
    }


    fun requestPermissions(typePermission : TypePermission, codeRequest : String? = "") {
        this.codeRequest = codeRequest
        verifyPermissions(fragment.requireContext(), typePermission.permissions)
        activityResult.launch(typePermission.permissions)
    }




    private fun verifyPermissions(result: Map<String, Boolean>, listener: OnGrantedPermissions ?) {
        if(listener == null) return

        for (granted in result.values)
            if (!granted) {
                listener.statusPermission(false, codeRequest ?: "")
                return
            }
        listener.statusPermission(true, codeRequest ?: "")
    }


    private fun verifyPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        return true
    }


    interface OnGrantedPermissions {
        fun statusPermission(granted: Boolean, codeRequest : String)
    }


}