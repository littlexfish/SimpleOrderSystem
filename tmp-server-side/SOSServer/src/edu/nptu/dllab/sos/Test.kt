package edu.nptu.dllab.sos

import com.google.gson.*

fun main() {
	
	val jsonObject = JsonObject()
	jsonObject.add("array", JsonArray().apply {
		add(true)
		add(50872)
	})
	jsonObject.add("object", JsonObject().apply {
		addProperty("50872", 50872)
		add("null", JsonNull.INSTANCE)
	})
	println(jsonObject)
//	println(GsonBuilder().setPrettyPrinting().create().toJson(jsonObject))
	
	val json = JsonParser.parseString(jsonObject.toString())
	println(json)
}

class Test {
}