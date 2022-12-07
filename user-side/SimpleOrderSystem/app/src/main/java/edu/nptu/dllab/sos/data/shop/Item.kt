package edu.nptu.dllab.sos.data.shop

import android.os.Parcel
import android.os.Parcelable

/**
 * A class of one item in order
 *
 * @author Little Fish
 */
class Item: Parcelable {
	
	/**
	 * The item id
	 */
	val itemId: String
	
	/**
	 * The additions of item
	 */
	val additions: List<Addition>
	
	/**
	 * The note of item
	 */
	val note: String
	
	constructor(i: String, a: List<Addition>, n: String) {
		itemId = i
		additions = a
		note = n
	}
	
	constructor(parcel: Parcel) {
		itemId = parcel.readString() ?: ""
		val size = parcel.readInt()
		val arr = ArrayList<Addition>()
		for(i in 0 until size) {
			val a = parcel.readParcelable<Addition>(javaClass.classLoader)
			a?.let { arr.add(it) }
		}
		additions = arr
		note = parcel.readString() ?: ""
	}
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(itemId)
		parcel.writeInt(additions.size)
		for(i in additions) {
			parcel.writeParcelable(i, 0)
		}
		parcel.writeString(note)
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	companion object CREATOR : Parcelable.Creator<Item> {
		override fun createFromParcel(parcel: Parcel): Item {
			return Item(parcel)
		}
		
		override fun newArray(size: Int): Array<Item?> {
			return arrayOfNulls(size)
		}
	}
	
	
}