package edu.nptu.dllab.sos.util

/**
 * The class contains some of exception that public can use
 *
 * @author Little Fish
 * @since 22/10/03
 */
abstract class Exceptions {
	class DataFormatException : RuntimeException {
		constructor() : super()
		constructor(msg: String) : super(msg)
		constructor(e: Exception) : super(e)
		constructor(e: Exception, msg: String) : super(msg, e)
	}
	
	class EventNotFoundException(event: String) : RuntimeException("event not found: $event")
	
	class DBDataNotGetException(columnName: String) : RuntimeException("data \"$columnName\" not select as filter")
}