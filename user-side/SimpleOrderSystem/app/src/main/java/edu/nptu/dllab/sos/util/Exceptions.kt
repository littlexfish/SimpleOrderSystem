package edu.nptu.dllab.sos.util

import kotlin.RuntimeException

/**
 * The class contains some of exception that public can use
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
abstract class Exceptions {
	@SOSVersion(since = "0.0")
	class DataFormatException : RuntimeException {
		constructor() : super()
		constructor(msg: String) : super(msg)
		constructor(e: Exception) : super(e)
		constructor(e: Exception, msg: String) : super(msg, e)
	}
	@SOSVersion(since = "0.0")
	class EventNotFoundException(event: String) :
		RuntimeException("event not found: $event")
	@SOSVersion(since = "0.0")
	class DBDataNotGetException(columnName: String) :
		RuntimeException("data \"$columnName\" not select as filter")
}