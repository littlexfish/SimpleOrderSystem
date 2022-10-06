package edu.nptu.dllab.sos.data

import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The shop state
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
enum class ShopState {
	/**
	 * The shop is open
	 */
	@SOSVersion(since = "0.0")
	OPEN,
	
	/**
	 * The shop is not open
	 */
	@SOSVersion(since = "0.0")
	CLOSE
	;
	companion object {
		/**
		 * Get state with string, avoid case sensitive
		 */
		@SOSVersion(since = "0.0")
		fun getStateByString(str: String) = valueOf(str.uppercase())
	}
}