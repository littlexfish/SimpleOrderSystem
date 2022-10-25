import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.msgpack.core.MessagePack
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

fun main(args: Array<String>) {
	if(args.isEmpty()) throw IllegalArgumentException("need at least one arguments")
	var j2m = true
	var out: String? = null
	var file = args.last()
	val cmdL = arrayOf("-j", "--json", "-m", "--msgpack")
	val cmdLN = arrayOf("-o", "--output", "-i", "--input")
	for(cmd in cmdL) {
		val index = args.indexOf(cmd)
		if(index >= 0) {
			when(cmd) {
				"-j", "--json" -> {
					j2m = true
				}
				"-m", "--msgpack" -> {
					j2m = false
				}
			}
		}
	}
	for(cmd in cmdLN) {
		val index = args.indexOf(cmd)
		if(index >= 0 && index + 1 < args.size) {
			val data = args[index + 1]
			when(cmd) {
				"-o", "--output" -> {
					out = data
				}
				"-i", "--input" -> {
					file = data
				}
			}
		}
	}
	if(j2m) Main().runJ2M(file, out) else Main().runM2J(file, out)
}
class Main {
	
	fun runJ2M(file: String, out: String?) {
		val inFile = File(file)
		val ins = inFile.inputStream()
		val data = ins.readAllBytes().toString(Charsets.UTF_8)
		ins.close()
		
		val element = JsonParser.parseString(data)
		val value = processJson(element)
		
		val outFile = File(out ?: (file.removeSuffix(inFile.extension) + "msgpack"))
		val dir = outFile.absoluteFile.parentFile
		dir.mkdirs()
		
		val outs = outFile.outputStream()
		val packer = MessagePack.newDefaultPacker(outs)
		packer.packValue(value)
		packer.flush()
		packer.close()
		outs.close()
		
		println("Successful")
	}
	
	private fun processJson(json: JsonElement): Value =
		if(json.isJsonPrimitive) {
			val p = json.asJsonPrimitive
			when {
				p.isBoolean -> ValueFactory.newBoolean(p.asBoolean)
				p.isNumber -> {
					val numString = p.asBigDecimal.toPlainString()
					if(numString.contains(".")) ValueFactory.newFloat(p.asDouble)
					else ValueFactory.newInteger(p.asLong)
				}
				p.isString -> ValueFactory.newString(p.asString)
				else -> throw RuntimeException("not throw")
			}
		}
		else if(json.isJsonNull) ValueFactory.newNil()
		else if(json.isJsonArray) {
			val ret = ArrayList<Value>()
			val arr = json.asJsonArray
			for(j in arr) {
				ret.add(processJson(j))
			}
			ValueFactory.newArray(ret)
		}
		else if(json.isJsonObject) {
			val map = ValueFactory.newMapBuilder()
			val obj = json.asJsonObject
			for(k in obj.keySet()) {
				map.put(ValueFactory.newString(k), processJson(obj[k]))
			}
			map.build()
		}
		else throw RuntimeException("not throw")
	
	fun runM2J(file: String, out: String?) {
		val inFile = File(file)
		val ins = inFile.inputStream()
		val unpacker = MessagePack.newDefaultUnpacker(ins)
		val value = unpacker.unpackValue()
		unpacker.close()
		ins.close()
		
		val outFile = File(out ?: (file.removeSuffix(inFile.extension) + "json"))
		val dir = outFile.absoluteFile.parentFile
		dir.mkdirs()
		
		val outs = outFile.outputStream()
		outs.write(value.toJson().toByteArray(Charsets.UTF_8))
		outs.close()
		
		println("Successful")
	}
	
}