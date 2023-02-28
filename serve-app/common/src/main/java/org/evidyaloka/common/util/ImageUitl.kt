package org.evidyaloka.common.util

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.*

/**
 * @author Madhankumar
 * created on 08-04-2021
 *
 */
object ImageUitl {

    fun getBitMap(){

    }

    @Throws(FileNotFoundException::class,IOException::class)
    fun getBitmapFormUri(ac: Activity, uri: Uri):Bitmap?{
        var input:InputStream? = ac.contentResolver.openInputStream(uri)
        val onlyBoundsOptions:BitmapFactory.Options = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if ((originalWidth == -1) || (originalHeight == -1))
            return null
        val hh = 800f;
        val ww = 480f;

        var be = 1;
        if (originalWidth > originalHeight && originalWidth > ww) {
            be = (originalWidth / ww).toInt();
        } else if (originalWidth < originalHeight && originalHeight > hh) {
            be = (originalHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1;
        val bitmapOptions =  BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input?.close()

        return bitmap?.let { compressImage(it) };
    }


    fun compressImage(image:Bitmap): Bitmap? {

        val baos = ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        var options = 100;
        while (baos.toByteArray().size / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        val isBm =  ByteArrayInputStream(baos.toByteArray());
        val bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    fun getFileFromMediaUri(ac: Context, uri:Uri): File? {
        if(uri.getScheme().toString().compareTo("content") == 0){
            val cr = ac.getContentResolver();
            val cursor: Cursor? = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                val filePath = cursor.getString(cursor.getColumnIndex("_data"));
                cursor.close();
                if (filePath != null) {
                    return File(filePath);
                }
            }
        }else if(uri.getScheme().toString().compareTo("file") == 0){
            return File(uri.toString().replace("file://",""));
        }
        return null;
    }



    fun getBitmapDegree(path: String):Int {
        var degree = 0;
        try {

            val exifInterface =  ExifInterface(path);
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL);
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180;
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270;
            }
        } catch (e:IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace();
        }
        return degree;
    }


    fun rotateBitmapByDegree(bm: Bitmap, degree:Int): Bitmap {
        var returnBm: Bitmap? = null;

        val matrix = Matrix();
        matrix.postRotate(degree.toFloat());
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true);
        } catch (e:OutOfMemoryError) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

}