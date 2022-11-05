#nullable enable
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SQLiteHelper {
	
	public class UpdateBuilder {

		private readonly string _name;
		private readonly Dictionary<string, object> _updates = new();
		private string? _where;

		public UpdateBuilder(string tableName) {
			_name = Util.CheckNotNullParameter(tableName, "tableName");
		}

		public UpdateBuilder AddUpdateColumn(string column, object value) {
			Util.CheckNotNullParameter(column, "column");
			Util.CheckNotNullParameter(value, "value");
			_updates.Add(column, value);
			return this;
		}

		public UpdateBuilder SetWhereCause(string? where) {
			_where = where;
			return this;
		}

		public UpdateBuilder SetWhereCause(WhereBuilder builder) {
			return SetWhereCause(Util.CheckNotNullParameter(builder, "builder").BuildSqlCommand());
		}

		public string BuildSqlCommand() {
			var sql = new StringBuilder($"UPDATE {_name}\nSET ");
			var ks = _updates.Keys.ToList();
			for(var i = 0; i < ks.Count; i++) {
				var k = ks[i];
				var value = _updates[k];
				sql.Append($"{k}={value}");
				if(i < ks.Count - 1) {
					sql.Append(',');
				}
			}

			if(_where != null) {
				sql.Append($"\nWHERE {_where}");
			}

			sql.Append(';');
			return sql.ToString();
		}

		public override string ToString() { return BuildSqlCommand(); }
	}
	
}