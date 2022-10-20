package edu.nptu.dllab.sos.android

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

object DownloadBitmap {
	
	fun getBitmap(inStream: InputStream): Bitmap {
		val o = ByteArrayOutputStream()
		inStream.copyTo(o)
		inStream.close()
		return BitmapFactory.decodeByteArray(o.toByteArray(), 0, o.size())
	}
	
	fun getBitmap(url: String): Bitmap {
		val i = URL(url).openStream()
		return getBitmap(i)
	}
	
	fun getDrawable(url: String, res: Resources?): Drawable = BitmapDrawable(res, getBitmap(url))
	
	fun getDrawable(inStream: InputStream, res: Resources?): Drawable = BitmapDrawable(res, getBitmap(inStream))
	
}