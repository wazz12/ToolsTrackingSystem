package com.demo.toolstracking.util

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.IOException
import java.nio.charset.Charset

const val TOOLS_JSON = "tools.json"
const val FRIENDS_JSON = "friends.json"

fun loadJSONFromAsset(context: Context, assetFileName: String): String {
    val json: String
    try {
        val inputStream = context.assets.open(assetFileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charset.defaultCharset())
    } catch (ex: IOException) {
        ex.printStackTrace()
        return ""
    }

    return json
}

fun setAvatarImage(context: Context, imageName: String, imageView: ImageView) {
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(context)
        .setDefaultRequestOptions(requestOptions)
        .load(resourceId)
        .into(imageView)
}

fun showToast(context: Context, text: String) {
    val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}