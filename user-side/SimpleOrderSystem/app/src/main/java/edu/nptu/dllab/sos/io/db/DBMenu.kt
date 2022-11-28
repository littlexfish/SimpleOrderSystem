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
	
	/**
	 * The shop id
	 */
	@SOSVersion(since = "0.0")
	var shopId = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.MENU_SHOP_ID.columnName)
			return field
		}
	var name = ""
	/**
	 * The menu version
	 */
	@SOSVersion(since = "0.0")
	var version = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.MENU_VERSION.columnName)
			return field
		}
	
	/**
	 * Initialize with value
	 */
	@SOSVersion(since = "0.0")
	constructor(shop: Int = -1, n: String = "", v: Int = -1) {
		shopId = shop
		name = n
		version = v
	}
	
	/**
	 * Initialize from db cursor
	 */
	@SOSVersion(since = "0.0")
	constructor(cursor: Cursor) {
		if(cursor.isAfterLast) return
		run {
			val shopI = cursor.getColumnIndex(DBColumn.MENU_SHOP_ID.columnName)
			if(shopI >= 0) shopId = cursor.getInt(shopI)
		}
		run {
			val nI = cursor.getColumnIndex(DBColumn.MENU_NAME.columnName)
			if(nI >= 0) name = cursor.getString(nI)
		}
		run {
			val vI = cursor.getColumnIndex(DBColumn.MENU_VERSION.columnName)
			if(vI >= 0) version = cursor.getInt(vI)
		}
	}
	
	/**
	 * Map as [ContentValues] to insert or update into db
	 */
	@SOSVersion(since = "0.0")
	fun toContentValues(values: ContentValues = ContentValues()): ContentValues {
		values.put(DBColumn.MENU_SHOP_ID.columnName, shopId)
		values.put(DBColumn.MENU_NAME.columnName, name)
		values.put(DBColumn.MENU_VERSION.columnName, version)
		return values
	}
	
}