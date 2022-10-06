package edu.nptu.dllab.sos.io.db

import edu.nptu.dllab.sos.io.DBHelper
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The enum of database
 * @param defaultColumn - the column index of full data
 * @param columnName - the name of column
 * @param tableName - the table name of column, any if empty string
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
enum class DBColumn(private val defaultColumn: Int, val columnName: String, val tableName: String = "") {
	
	/**
	 * Resource unique id, use to check resource, start from 0
	 */
	@SOSVersion(since = "0.0")
	RES_ID(0, "id", DBHelper.TABLE_RES),
	
	/**
	 * Resource shop id
	 */
	@SOSVersion(since = "0.0")
	RES_SHOP_ID(1, "shopId", DBHelper.TABLE_RES),
	
	/**
	 * Resource local path, just save after shop id
	 */
	@SOSVersion(since = "0.0")
	RES_PATH(0, "path", DBHelper.TABLE_RES),
	
	/**
	 * Resource data sha256, use to avoid tamper
	 */
	@SOSVersion(since = "0.0")
	RES_SHA256(2, "sha256", DBHelper.TABLE_RES),
	
	/**
	 * Resource data size, bytes count
	 */
	@SOSVersion(since = "0.0")
	RES_SIZE(3, "size", DBHelper.TABLE_RES),
	
	/**
	 * Menu shop id
	 */
	@SOSVersion(since = "0.0")
	MENU_SHOP_ID(0, "shopId", DBHelper.TABLE_MENU),
	
	/**
	 * Menu version
	 */
	@SOSVersion(since = "0.0")
	MENU_VERSION(1, "version", DBHelper.TABLE_MENU),
	;
	
}