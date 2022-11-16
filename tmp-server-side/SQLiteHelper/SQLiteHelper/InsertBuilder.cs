using System;
using System.Collections.Generic;
using System.Text;

namespace SQLiteHelper {
	
	public class InsertBuilder {

		private readonly string _name;
		private readonly List<string> _columns;
		private readonly List<object[]> _values = new();

		public InsertBuilder(string tableName, params string[] columns) {
			_name = Util.CheckNotNullParameter(tableName, "tableName");
			if(columns.Length < 1) throw new ArgumentException("must have at least one column to insert");
			_columns = new(Util.CheckNotNullParameter(columns, "columns"));
		}

		public InsertBuilder AddValues(params object[] values) {
			Util.CheckNotNullParameter(values, "values");
			if(values.Length != _columns.Count) throw new ArgumentException("insert values size must same as column size");
			_values.Add(values);
			return this;
		}

		public string BuildSqlCommand() {
			var sql = new StringBuilder($"INSERT INTO {_name} (");

			for(var i = 0; i < _columns.Count; i++) {
				sql.Append(_columns[i]);
				if(i < _columns.Count - 1) {
					sql.Append(',');
				}
			}

			sql.Append(") VALUES ");

			for(var i = 0; i < _values.Count; i++) {
				sql.Append('(');
				for(var j = 0; j < _values[i].Length; j++) {
					var isText = _values[i][j] is string;
					if(isText) sql.Append('\'');
					sql.Append(_values[i][j]);
					if(isText) sql.Append('\'');
					if(j < _values[i].Length - 1) {
						sql.Append(',');
					}
				}
				sql.Append(')');
				if(i < _values.Count - 1) {
					sql.Append(',');
				}
			}

			sql.Append(';');
			return sql.ToString();
		}

		public override string ToString() { return BuildSqlCommand(); }
	}
	
}