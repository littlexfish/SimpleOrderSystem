package edu.nptu.dllab.sos.io

import android.content.Context
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import edu.nptu.dllab.sos.util.SOSVersion
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * The class that can get resource
 *
 * @author Little Fish
 * @since 22/10/04
 */
@SOSVersion(since = "0.0")
object ResourceReader {
	
	/**
	 * Get resource from shop id and res id
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 *
	 * @return res file
	*/
	@SOSVersion(since = "0.0")
	fun getResource(context: Context, shopId: Int, resId: Int): File {
		val db = DBHelper(context)
		val cur = db.select(DBHelper.TABLE_RES, where = "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId", limit = 1)
		val res = DBRes(cur)
		cur.close()
		db.close()
		return File(ResourceIO.getFilePath(context, shopId, res.path))
	}
	
	/**
	 * Get resource as byte array from shop id and res id
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 *
	 * @return res bytes
	 */
	@SOSVersion(since = "0.0")
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