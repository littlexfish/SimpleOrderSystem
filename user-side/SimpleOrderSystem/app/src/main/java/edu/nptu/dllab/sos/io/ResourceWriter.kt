package edu.nptu.dllab.sos.io

import android.content.Context
import androidx.core.net.toUri
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * The class that can save resource
 *
 * @author Little Fish
 */
object ResourceWriter {
	
	/**
	 * Save the resource to specific path
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 * @param path - the path to save
	 * @param data - res data
	 * @param sha256 - sha256 of file, `null` if disable check
	 *
	 * @return `false` if some error that cannot request save res
	 */
	fun saveResource(context: Context, shopId: Int, resId: Int, path: String, data: ByteArray, sha256: String? = null): Boolean {
		if(!checkSha256(data, sha256)) return false
		// save path "file/res/<shopId>/path"
		val file = ResourceIO.getFile(context, shopId, path)
		val fileUri = file.toUri()
		val dirPathList = fileUri.pathSegments
		val dir = File(dirPathList.subList(0, dirPathList.lastIndex).joinToString("/"))
		dir.mkdirs()
		file.createNewFile()
		val os = FileOutputStream(file)
		os.write(data)
		os.close()
		
		val db = DBHelper(context)
		val cur = db.select(DBHelper.TABLE_RES, where = "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId")
		val hasOld = cur.count > 0
		cur.close()
		
		val res = DBRes(resId, shopId, path, sha256 ?: "", data.size)
		if(hasOld) db.updateRes(DBHelper.TABLE_RES, res.toContentValues(), resId, shopId)
		else db.insert(DBHelper.TABLE_RES, res.toContentValues())
		db.close()
		return true
	}
	
	
	/**
	 * Check sha256 is ok
	 */
	private fun checkSha256(data: ByteArray, sha256: String?): Boolean {
		if(sha256 == null) return true
		val digest = MessageDigest.getInstance("SHA-256")
		val hashBytes = digest.digest(data)
		
		val sb = StringBuilder()
		for(i in hashBytes) {
			val hex = String.format("%02x", i)
			sb.append(hex.substring(hex.length - 2))
		}
		return sb.toString() == sha256
	}
	
}