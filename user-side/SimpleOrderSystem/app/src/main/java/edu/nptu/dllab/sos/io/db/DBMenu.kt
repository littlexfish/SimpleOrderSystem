package edu.nptu.dllab.sos.io.db

import android.content.ContentValues
import android.database.Cursor
import edu.nptu.dllab.sos.util.Exceptions
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The info of menu that need save in db
 *
 * @author Little Fish
 * @since 22/10/06
 */
@SOSVersion(since = "0.0")
class DBMenu {
	
	var shopId = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.MENU_SHOP_ID.columnName)
			return field
		}
	var version = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.MENU_VERSION.columnName)
			return field
		}
	
	/**
	 * Initialize with value
	 */
	@SOSVersion(since = "0.0")
	constructor(shop: Int = -1, v: Int = -1) {
		shopId = shop
		version = v
	}
	
	/**
	 * Initialize from db cursor
	 */
	@SOSVersion(since = "0.0")
	constructor(cursor: Cursor) {
		if(cursor.isAfterLast) return
		run {
			val shopI = cursor.getColumnIndex(DBColumn.RES_SHOP_ID.columnName)
			if(shopI > 0) shopId = cursor.getInt(shopI)
		}
		run {
			val vI = cursor.getColumnIndex(DBColumn.RES_PATH.columnName)
			if(vI > 0) version = cursor.getInt(vI)
		}
	}
	
	/**
	 * Map as [ContentValues] to insert or update into db
	 */
	@SOSVersion(since = "0.0")
	fun toContentValues(values: ContentValues = ContentValues()): ContentValues {
		values.put(DBColumn.MENU_SHOP_ID.columnName, shopId)
		values.put(DBColumn.MENU_VERSION.columnName, version)
		return values
	}
	
}