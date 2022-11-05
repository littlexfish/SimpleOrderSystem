#nullable enable
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SQLiteHelper {
	public class TableBuilder {

		private bool _ifNotExists = true;
		private readonly string _name;
		private readonly List<string> _columnOrder = new();
		private readonly Dictionary<string, ColumnHolder> _column = new();
		private readonly HashSet<string> _primaryKey = new();

		public TableBuilder(string tableName) {
			_name = Util.CheckNotNullParameter(tableName, "tableName");
		}

		public TableBuilder SetCreateNotExists(bool flag) {
			_ifNotExists = flag;
			return this;
		}
		
		public TableBuilder AddColumn(int index, string column, SqlType type, int? size = null, bool notNull = false, object? defaultValue = null) {
			Util.CheckNotNullParameter(column, "column");
			Util.CheckNotNullParameter(type, "type");
			if(_column.ContainsKey(column)) throw new ArgumentException("column name duplicate");
			if(notNull && defaultValue == null) throw new ArgumentException("default value is null with not null is true");
			if(index < 0 || index > _columnOrder.Count) _columnOrder.Add(column);
			else _columnOrder.Insert(index, column);
			_column.Add(column, new ColumnHolder(column, type, size, notNull, defaultValue));
			return this;
		}

		public TableBuilder AddColumn(string column, SqlType type, int? size = null, bool notNull = false, object? defaultValue = null) {
			return AddColumn(-1, column, type, size, notNull, defaultValue);
		}

		public TableBuilder AddColumn(int index, string column, string type, int? size = null, bool notNull = false, object? defaultValue = null) {
			return AddColumn(index, column, SqlType.GetByName(type), size, notNull, defaultValue);
		}

		public TableBuilder AddColumn(string column, string type, int? size = null, bool notNull = false, object? defaultValue = null) {
			return AddColumn(-1, column, type, size, notNull, defaultValue);
		}

		public TableBuilder RemoveColumn(string column) {
			if(_column.ContainsKey(column)) {
				_column.Remove(column);
				_columnOrder.Remove(column);
			}
			return this;
		}

		public TableBuilder AddPrimaryKey(string column) {
			_primaryKey.Add(column);
			return this;
		}

		public string BuildSqlCommand() {
			var sql = new StringBuilder("CREATE TABLE");
			if(_ifNotExists) sql.Append(" IF NOT EXISTS");
			sql.Append($" {_name} (");
			if(_column.Count <= 0) throw new Exception("table must have at least one column");
			for(var i = 0; i < _columnOrder.Count; i++) {
				sql.Append(_column[_columnOrder[i]].ToSql());
				if(i < _columnOrder.Count - 1) {
					sql.Append(',');
				}
			}

			if(_primaryKey.Count > 0) {
				sql.Append(",PRIMARY KEY (");
				var primary = _primaryKey.ToList();
				for(var i = 0; i < primary.Count; i++) {
					var key = primary[i];
					if(!_column.ContainsKey(key)) throw new Exception($"no exists data name to be primary key: {key}");
					sql.Append(key);
					if(i < primary.Count - 1) {
						sql.Append(',');
					}
				}

				sql.Append(')');
			}

			sql.Append(");");
			return sql.ToString();
		}

		public override string ToString() { return BuildSqlCommand(); }

		private class ColumnHolder {

			private readonly string _column;
			private readonly SqlType _type;
			private readonly int? _size;
			private readonly bool _notNull;
			private readonly object? _defaultValue;

			internal ColumnHolder(string c, SqlType t, int? s = null, bool n = false, object? d = null) {
				_column = c;
				_type = t;
				_size = s;
				_notNull = n;
				_defaultValue = d;
			}

			internal string ToSql() {
				var sql = new StringBuilder($"{_column} {_type.SqlCommand}");
				if(_size != null) sql.Append($"({_size})");
				if(_notNull) sql.Append(" NOT NULL");
				sql.Append($" DEFAULT {_defaultValue ?? "NULL"}");
				return sql.ToString();
			}
			
		}

	}
}