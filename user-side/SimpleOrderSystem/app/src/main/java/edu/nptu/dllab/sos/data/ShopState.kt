package edu.nptu.dllab.sos.data

/**
 * The shop state
 *
 * @author Little Fish
 */
enum class ShopState(val key: String) {
	
	/**
	 * The shop is open
	 */
	OPEN("open"),
	
	/**
	 * The shop is not open
	 */
	CLOSE("close");
	
	companion object {
		/**
		 * Get state with string, avoid case sensitive
		 */
		fun getStateByString(str: String) = valueOf(str.uppercase())
	}
}