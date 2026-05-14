package com.example.praktik12

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class MediaHelper (private val context: Context) {
    //mengambil request code untuk gallery
    fun getRcGallery() : Int{
        return REQ_CODE_GALLERY
    }
    //mengubah Bitmap menjadi String Base64
    fun bitmapToString(bmp : Bitmap) : String{
        //kompresi gambar dengan menurunkan kualitas gambar menjadi
        //tinggal 60% dari aslinya
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        //mengubah hasil gambar menjadi byte array
        val byteArray = outputStream.toByteArray()
        //encode byte array ke Base64 String
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    //mengambil gambar dari gallery lalu mengubahnya menjadi String
    fun getBitmapToString(uri : Uri, imv : ImageView) : String{
        var bmp = MediaStore.Images.Media.getBitmap(
            this.context.contentResolver, uri)
        //ukuran maksimum gambar
        var dim = 720
        //resize gambar agar ukuran tidak terlalu besar
        if(bmp.height > bmp.width){
            bmp = Bitmap.createScaledBitmap(
                bmp, (bmp.width*dim).div(bmp.height), dim, true)
        }else{
            bmp = Bitmap.createScaledBitmap(
                bmp, dim, (bmp.height*dim).div(bmp.width), true)
        }
        //menampilkan gambar ke ImageView
        imv.setImageBitmap(bmp)
        //mengembalikan gambar dalam bentuk Base64 String
        return bitmapToString(bmp)
    }

    companion object{
        //request code untuk membuka gallery
        const val REQ_CODE_GALLERY = 100
    }
}