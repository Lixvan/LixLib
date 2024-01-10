package com.diktapk.lixutils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class IntentUtils constructor(fragment: Fragment) {

    companion object{

        fun buildIntentForOpenFile(type: TypeFileToOpen): Intent {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = type.mime
            return intent
        }

    }

    private val activityResultContract : ActivityResultLauncher<Intent>
    private lateinit var uriData : (uri : Uri?) -> Unit


    init {
        activityResultContract= createMethodRegisterForActivityResult(fragment)
    }


    private fun createMethodRegisterForActivityResult(fragment : Fragment) : ActivityResultLauncher<Intent>
            =  fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ it ->
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                uriData.invoke(uri)
                return@registerForActivityResult
            }

            uriData.invoke(null)

        }else {
            //profileViewModel.anErrorOccurred.value = "Ocurrio un error al seleccionar la foto"
            uriData.invoke(null)
        }
    }

    fun sendRequestIntent(intent : Intent, uriData : (uri : Uri?) -> Unit) {
        this.uriData = uriData
        activityResultContract.launch(intent)
    }

    enum class TypeFileToOpen constructor( val mime : String) {

        AUDIO("audio/*"),
        VIDEO("video/*"),
        IMAGE("image/*"),
        ALL("*/*")

    }

}