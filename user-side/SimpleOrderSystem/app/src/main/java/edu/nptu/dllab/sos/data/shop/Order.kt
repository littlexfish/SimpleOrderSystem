package edu.nptu.dllab.sos.data.shop

import android.os.Parcel
import android.os.Parcelable

/**
 * A class use for shop side, contains all items in one order
 *
 * @author Little Fish
 */
class Order: Parcelable {
	
	/**
	 * The order id
	 */
	val id: Int
	
	/**
	 * The items
	 */
	val items: List<Item>
	
	constructor(id: Int, items: List<Item>) {
		this.id = id
		this.items = items
	}
	
	constructor(parcel: Parcel) {
		id = parcel.readInt()
		val size = parcel.readInt()
		val arr = ArrayList<Item>()
		for(i in 0 until size) {
			val a = parcel.readParcelable<Item>(javaClass.classLoader)
			a?.let { arr.add(it) }
		}
		items = arr
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(id)
		parcel.writeInt(items.size)
		for(i in items) {
			parcel.writeParcelable(i, 0)
		}
	}
	
	companion object CREATOR : Parcelable.Creator<Order> {
		override fun createFromParcel(parcel: Parcel): Order {
			return Order(parcel)
		}
		
		override fun newArray(size: Int): Array<Order?> {
			return arrayOfNulls(size)
		}
	}
	
	
}