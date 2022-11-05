using System;
using System.Collections.Generic;

namespace SQLiteHelper {
	public class SqlType {
		
		private static readonly Dictionary<string, SqlType> Types = new();
		
		public static readonly SqlType Int = Build("INT", "Int");
		public static readonly SqlType Integer = Build("INTEGER", "Integer");
		public static readonly SqlType TinyInt = Build("TINYINT", "TinyInt");
		public static readonly SqlType SmallInt = Build("SMALLINT", "SmallInt");
		public static readonly SqlType MediumInt = Build("MEDIUMINT", "MediumInt");
		public static readonly SqlType BigInt = Build("BIGINT", "BigInt");
		
		public static readonly SqlType Character = Build("CHARACTER", "Character");
		public static readonly SqlType VarChar = Build("VARCHAR", "VarChar");
		public static readonly SqlType Text = Build("TEXT", "Text");
		
		public static readonly SqlType Blob = Build("BLOB", "Blob");
		
		public static readonly SqlType Real = Build("REAL", "Real");
		public static readonly SqlType Double = Build("DOUBLE", "Double");
		public static readonly SqlType Float = Build("FLOAT", "Float");
		
		public static readonly SqlType Boolean = Build("BOOLEAN", "Boolean");
		public static readonly SqlType Date = Build("DATE", "Date =");
		public static readonly SqlType Datetime = Build("DATETIME", "Datetime");
		
		public readonly string SqlCommand;

		private SqlType(string sql) {
			SqlCommand = sql;
		}

		public string Name() {
			foreach(var kv in Types) {
				if(kv.Value == this) return kv.Key;
			}

			throw new Exception(); // may not be here
		}
		
		private static SqlType Build(string sql, string name) {
			var i = new SqlType(sql);
			Types[name] = i;
			return i;
		}

		public static SqlType GetByName(string name) {
			Util.CheckNotNullParameter(name, "name");
			if(!Types.ContainsKey(name)) throw new ArgumentException($"type not found by name: {name}");
			return Types[name];
		}
		
	}
}