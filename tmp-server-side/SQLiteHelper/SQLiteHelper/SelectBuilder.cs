#nullable enable
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SQLiteHelper {
	
	public class SelectBuilder {

		private static readonly SelectAll All = new SelectAll();
		
		private readonly string _name;
		private SelectColumn _column = All;
		private string? _where;
		private readonly List<string> _orderBy = new();
		private bool _asc = true;
		private int _limit = -1;
		private int _offset;
		
		public SelectBuilder(string tableName) {
			_name = Util.CheckNotNullParameter(tableName, "tableName");
		}

		public SelectBuilder SetSelectAll() {
			_column = All;
			return this;
		}

		public SelectBuilder AddSelectColumn(string column, string? display = null) {
			Util.CheckNotNullParameter(column, "column");
			if(_column == All) _column = new Select();
			(_column as Select)?.AddColumn(column, display);
			return this;
		}

		public SelectBuilder SetWhereCause(string? where) {
			_where = where;
			return this;
		}

		public SelectBuilder SetWhereCause(WhereBuilder builder) {
			return SetWhereCause(Util.CheckNotNullParameter(builder, "builder").BuildSqlCommand());
		}

		public SelectBuilder AddOrderBy(params string[] columns) {
			_orderBy.AddRange(Util.CheckNotNullParameter(columns, "columns"));
			return this;
		}

		public SelectBuilder SetAsc() {
			_asc = true;
			return this;
		}
		
		public SelectBuilder SetDesc() {
			_asc = false;
			return this;
		}

		public SelectBuilder SetLimit(int limit) {
			_limit = limit;
			return this;
		}

		public SelectBuilder SetOffset(int offset) {
			_offset = offset;
			return this;
		}
		
		public string BuildSqlCommand() {
			var sql = new StringBuilder($"SELECT {_column.ToSql()} FROM {_name}");
			if(_where != null) sql.Append($" WHERE {_where}");
			if(_orderBy.Count > 0) {
				var sb = new StringBuilder(" ORDER BY ");
				for(int i = 0; i < _orderBy.Count; i++) {
					sb.Append(_orderBy[i]);
					if(i < _orderBy.Count - 1) sb.Append(',');
				}

				sql.Append(sb.ToString());
				if(!_asc) sql.Append(" DESC");
			}

			if(_limit >= 0) sql.Append($" LIMIT {_limit}");
			if(_offset > 0) sql.Append($" OFFSET {_offset}");
			sql.Append(';');
			return sql.ToString();
		}

		public override string ToString() { return BuildSqlCommand(); }

		internal abstract class SelectColumn {
			internal abstract string ToSql();
		}
		
		internal class SelectAll : SelectColumn {
			internal override string ToSql() {
				return "*";
			}
		}

		internal class Select : SelectColumn {

			private readonly Dictionary<string, string?> _nameWithDisplay = new();

			internal void AddColumn(string column, string? display = null) {
				_nameWithDisplay.Add(column, display);
			}

			internal override string ToSql() {
				var sql = new StringBuilder();
				var ks = _nameWithDisplay.Keys.ToList();
				for(var i = 0; i < ks.Count; i++) {
					var k = ks[i];
					var v = _nameWithDisplay[k];
					sql.Append(k);
					if(v != null) {
						sql.Append($" AS {v}");
					}

					if(i < ks.Count - 1) sql.Append(',');
				}

				return sql.ToString();
			}
		}
		
	}
	
}