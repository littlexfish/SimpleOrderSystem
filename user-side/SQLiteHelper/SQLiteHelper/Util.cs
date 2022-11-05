using System;

namespace SQLiteHelper {
	internal class Util {

		internal static T CheckNotNullParameter<T>(T value, string name) {
			if(value == null) throw new ArgumentException($"parameter {name} must not null");
			return value;
		}
		
	}
}