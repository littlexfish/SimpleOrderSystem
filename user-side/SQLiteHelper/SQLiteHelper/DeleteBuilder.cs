#nullable enable
namespace SQLiteHelper {
	public class DeleteBuilder {
		
		private readonly string _name;
		private string? _where;

		public DeleteBuilder(string tableName, string? where = null) {
			_name = Util.CheckNotNullParameter(tableName, "tableName");
			_where = where;
		}

		public DeleteBuilder SetWhereCause(string? where) {
			_where = where;
			return this;
		}

		public DeleteBuilder SetWhereCause(WhereBuilder builder) {
			return SetWhereCause(Util.CheckNotNullParameter(builder, "builder").BuildSqlCommand());
		}
		
		public string BuildSqlCommand() {
			var sql = $"DELETE FROM {_name}";
			if(_where != null) sql += $"\nWHERE {_where}";
			return sql + ';';
		}

		public override string ToString() { return BuildSqlCommand(); }
	}
}