package edu.nptu.dllab.sos.android

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

/**
 * A class use for get bitmap from outside
 */
object DownloadBitmap {
	
	/**
	 * Get bitmap from input stream
	 */
	fun getBitmap(inStream: InputStream): Bitmap {
		val o = ByteArrayOutputStream()
		inStream.copyTo(o)
		inStream.close()
		return BitmapFactory.decodeByteArray(o.toByteArray(), 0, o.size())
	}
	
	/**
	 * Get bitmap from given url
	 */
	fun getBitmap(url: String): Bitmap {
		val i = URL(url).openStream()
		return getBitmap(i)
	}
	
	/**
	 * Get drawable from given url
	 */
	fun getDrawable(url: String, res: Resources?): Drawable = BitmapDrawable(res, getBitmap(url))
	
	/**
	 * Get drawable from input stream
	 */
	fun getDrawable(inStream: InputStream, res: Resources?): Drawable =
		BitmapDrawable(res, getBitmap(inStream))
	
	private val bitmap = HashMap<String, Bitmap>()
	
	fun getBitmapFromBuffer(context: Context, name: String): Bitmap? {
		if(!bitmap.containsKey(name)) {
			val filter = context.assets.list("res")
			if(filter == null || !filter.contains(name)) {
				return null
			}
			val ins = context.assets.open("res/$name")
			val bit = getBitmap(ins)
			ins.close()
			bitmap[name] = bit
		}
		return bitmap[name]
	}
	
}