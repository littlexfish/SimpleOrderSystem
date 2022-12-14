package edu.nptu.dllab.sos.io.db

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getStringOrNull
import edu.nptu.dllab.sos.util.Exceptions

/**
 * The info of resource that need save in db
 *
 * @author Little Fish
 */
class DBRes {
	
	/**
	 * Resource id
	 */
	var id = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.RES_ID.columnName)
			return field
		}
	
	/**
	 * The shop id
	 */
	var shopId = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.RES_SHOP_ID.columnName)
			return field
		}
	
	/**
	 * The resource path of this resource
	 */
	var path = ""
		get() {
			if(field.isBlank()) throw Exceptions.DBDataNotGetException(DBColumn.RES_PATH.columnName)
			return field
		}
	
	/**
	 * The sha256 of this resource data
	 */
	var sha256 = ""
		get() {
			if(field.isBlank()) throw Exceptions.DBDataNotGetException(
				DBColumn.RES_SHA256.columnName)
			return field
		}
	
	/**
	 * The size of this resource data
	 */
	var size = -1
		get() {
			if(field < 0) throw Exceptions.DBDataNotGetException(DBColumn.RES_SIZE.columnName)
			return field
		}
	
	/**
	 * Initialize with value
	 */
	constructor(i: Int = -1, shop: Int = -1, p: String = "", sha: String = "", s: Int = -1) {
		id = i
		shopId = shop
		path = p
		sha256 = sha
		size = s
	}
	
	/**
	 * Initialize from db cursor
	 */
	constructor(cursor: Cursor) {
		if(cursor.isAfterLast) return
		run {
			val idI = cursor.getColumnIndex(DBColumn.RES_ID.columnName)
			if(idI >= 0) id = cursor.getInt(idI)
		}
		run {
			val shopI = cursor.getColumnIndex(DBColumn.RES_SHOP_ID.columnName)
			if(shopI >= 0) shopId = cursor.getInt(shopI)
		}
		run {
			val pathI = cursor.getColumnIndex(DBColumn.RES_PATH.columnName)
			if(pathI >= 0) path = cursor.getStringOrNull(pathI) ?: ""
		}
		run {
			val shaI = cursor.getColumnIndex(DBColumn.RES_SHA256.columnName)
			if(shaI >= 0) sha256 = cursor.getStringOrNull(shaI) ?: ""
		}
		run {
			val sizeI = cursor.getColumnIndex(DBColumn.RES_SIZE.columnName)
			if(sizeI >= 0) size = cursor.getInt(sizeI)
		}
	}
	
	/**
	 * Map as [ContentValues] to insert or update into db
	 */
	fun toContentValues(values: ContentValues = ContentValues()): ContentValues {
		values.put(DBColumn.RES_ID.columnName, id)
		values.put(DBColumn.RES_SHOP_ID.columnName, shopId)
		values.put(DBColumn.RES_PATH.columnName, path)
		values.put(DBColumn.RES_SHA256.columnName, sha256)
		values.put(DBColumn.RES_SIZE.columnName, size)
		return values
	}
	
}