package edu.nptu.dllab.sos.util

import edu.nptu.dllab.sos.io.SocketHandler

/**
 * A util class use for hold static data
 */
@SOSVersion(since = "0.0")
object StaticData {
	
	/**
	 * The socket handler of now processing
	 */
	@SOSVersion(since = "0.0")
	lateinit var socketHandler: SocketHandler
	
	
	
}