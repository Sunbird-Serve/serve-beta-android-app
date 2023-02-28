package org.evidyaloka.common.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Madhankumar
 * created on 08-04-2021
 */
class TakePicturePreview : ActivityResultContract<Void?, Uri?>() {
    private var mPhotoFile: File? = null

    @CallSuper
    override fun createIntent(context: Context, input: Void?): Intent {
        return getCameraIntent(context) ?: Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    override fun getSynchronousResult(
        context: Context,
        input: Void?
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {

        try {
            if(resultCode == Activity.RESULT_OK){
                return mPhotoFile?.let{
                     Uri.fromFile(it)
                }?: null
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun getCameraIntent(context: Context): Intent? {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile(context)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context, context.applicationContext.packageName + ".provider",
                        photoFile
                    )
                    mPhotoFile = photoFile
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    return takePictureIntent
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    private fun createImageFile(context: Context): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "temp_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)

    }
}