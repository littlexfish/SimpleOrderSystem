package edu.nptu.dllab.sos.io

import android.content.Context
import edu.nptu.dllab.sos.io.db.DBColumn
import edu.nptu.dllab.sos.io.db.DBRes
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The class that can process some resource io
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
object ResourceIO {
	
	/**
	 * Check the resource is exists
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 * @param path - the path to check
	 *
	 * @return `true` if resource is exists and path is same as data store in db
	 */
	@SOSVersion(since = "0.0")
	fun checkExists(context: Context, shopId: Int, resId: Int, path: String): Boolean {
		val db = DBHelper(context)
		val select = "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId"
		val cur = db.select(DBHelper.TABLE_RES, where = select, limit = 1)
		val res = DBRes(cur)
		cur.close()
		db.close()
		return res.path == path
	}
	
	/**
	 * Check the sha256 between local data and server given is same
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param resId - the res unique id
	 * @param sha256 - the sha256 to check
	 *
	 * @return `true` if sha256 is same as data store in db
	 */
	@SOSVersion(since = "0.0")
	fun checkResSha256(context: Context, shopId: Int, resId: Int, sha256: String): Boolean {
		val db = DBHelper(context)
		val select = "${DBColumn.RES_ID.columnName}=$resId AND ${DBColumn.RES_SHOP_ID.columnName}=$shopId"
		val cur = db.select(DBHelper.TABLE_RES, where = select, limit = 1)
		val res = DBRes(cur)
		cur.close()
		db.close()
		return res.sha256 == sha256
	}
	
	/**
	 * Get full path the res store
	 * @param context - the app context
	 * @param shopId - the shopId
	 * @param path - the relative path of shop res
	 */
	@SOSVersion(since = "0.0")
	fun getFilePath(context: Context, shopId: Int, path: String) = "${context.filesDir}/$shopId/$path"
	
}