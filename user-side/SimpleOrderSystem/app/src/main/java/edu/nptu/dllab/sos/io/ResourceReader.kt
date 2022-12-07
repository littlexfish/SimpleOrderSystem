package edu.nptu.dllab.sos.io

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * The class that can get resource
 *
 * @author Little Fish
 */
object ResourceReader {
	
	/**
	 * Get resource from shop id and res id
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 *
	 * @return res file
	 */
	fun getResource(context: Context, shopId: Int, resId: Int): File {
		val db = DBHelper(context)
		val cur = db.select(DBHelper.TABLE_RES, where = "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId", limit = 1)
		val res = DBRes(cur)
		cur.close()
		db.close()
		return File(ResourceIO.getFilePath(context, shopId, res.path))
	}
	
	fun getResourcesOfShop(context: Context, shopId: Int): List<DBRes> {
		val db = DBHelper(context)
		val cur = db.select(DBHelper.TABLE_RES, where = "${DBColumn.RES_SHOP_ID.columnName}=$shopId")
		val rs = ArrayList<DBRes>()
		while(cur.moveToNext()) {
			rs.add(DBRes(cur))
		}
		cur.close()
		db.close()
		return rs
	}
	
	fun getResourcesAsBitmap(context: Context, shopId: Int): List<Pair<Int, Bitmap>> {
		val rs = getResourcesOfShop(context, shopId)
		return rs.map {
			val i = File(ResourceIO.getFilePath(context, it.shopId, it.path)).inputStream()
			val b = BitmapFactory.decodeStream(i)
			i.close()
			Pair(it.id, b)
		}
	}
	
	/**
	 * Get resource as byte array from shop id and res id
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 *
	 * @return res bytes
	 */
	fun getResourceAsBytes(context: Context, shopId: Int, resId: Int): ByteArray {
		val file = getResource(context, shopId, resId)
		
		val fis = FileInputStream(file)
		val bos = ByteArrayOutputStream()
		val buffer = ByteArray(65536)
		while(true) {
			val size = fis.read(buffer)
			if(size < 0) break
			bos.write(buffer, 0, size)
		}
		fis.close()
		return bos.toByteArray()
	}
	
	
}