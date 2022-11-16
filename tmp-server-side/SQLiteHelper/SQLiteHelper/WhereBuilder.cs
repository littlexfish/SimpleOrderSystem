using System;
using System.Text;

namespace SQLiteHelper {
	
	// ReSharper disable once ClassNeverInstantiated.Global
	public class WhereBuilder {
		
		public static readonly ConditionSymbol Equal = Build("=");
		public static readonly ConditionSymbol NotEqual = Build("!=");
		public static readonly ConditionSymbol Less = Build("<");
		public static readonly ConditionSymbol Greater = Build(">");
		public static readonly ConditionSymbol LessEqual = Build("<=");
		public static readonly ConditionSymbol GreaterEqual = Build(">=");
		
		private readonly StringBuilder _sb = new();

		private bool _needNewCondition = true;
		private int _bracket;

		public WhereBuilder And() {
			CheckNeedNewCondition();
			_sb.Append(" AND ");
			_needNewCondition = true;
			return this;
		}

		public WhereBuilder Or() {
			CheckNeedNewCondition();
			_sb.Append(" OR ");
			_needNewCondition = true;
			return this;
		}

		public WhereBuilder SimpleCondition(object left, ConditionSymbol symbol, object right) {
			Util.CheckNotNullParameter(left, "left");
			Util.CheckNotNullParameter(symbol, "symbol");
			Util.CheckNotNullParameter(right, "right");
			CheckNoAndOr();
			var leftText = left is string;
			if(leftText) _sb.Append('\'');
			_sb.Append(left);
			if(leftText) _sb.Append('\'');
			_sb.Append(symbol.Condition);
			var rightText = right is string;
			if(rightText) _sb.Append('\'');
			_sb.Append(right);
			if(rightText) _sb.Append('\'');
			_needNewCondition = false;
			return this;
		}

		public WhereBuilder IsNotNull(string column) {
			Util.CheckNotNullParameter(column, "column");
			CheckNoAndOr();
			_sb.Append($"{column} IS NOT NULL");
			_needNewCondition = false;
			return this;
		}
		
		public WhereBuilder IsNull(string column) {
			Util.CheckNotNullParameter(column, "column");
			CheckNoAndOr();
			_sb.Append($"{column} IS NULL");
			_needNewCondition = false;
			return this;
		}

		public WhereBuilder LeftBracket() {
			CheckNoAndOr();
			_sb.Append('(');
			_bracket++;
			return this;
		}

		public WhereBuilder RightBracket() {
			CheckNeedNewCondition();
			if(_bracket <= 0) throw new ArgumentException("no left bracket");
			_sb.Append(')');
			_bracket--;
			return this;
		}

		public WhereBuilder Like(string like) {
			Util.CheckNotNullParameter(like, "like");
			CheckNoAndOr();
			_sb.Append($"LIKE '{like}'");
			_needNewCondition = false;
			return this;
		}

		public WhereBuilder Glob(string glob) {
			Util.CheckNotNullParameter(glob, "glob");
			CheckNoAndOr();
			_sb.Append($"GLOB '{glob}'");
			_needNewCondition = false;
			return this;
		}

		public WhereBuilder In(string column, params object[] inValues) {
			Util.CheckNotNullParameter(column, "column");
			Util.CheckNotNullParameter(inValues, "inValues");
			CheckNoAndOr();
			var sql = new StringBuilder("IN (");
			for(var i = 0; i < inValues.Length; i++) {
				sql.Append(inValues[i]);
				if(i < inValues.Length - 1) sql.Append(',');
			}

			_sb.Append(sql.ToString());
			_needNewCondition = false;
			return this;
		}

		public WhereBuilder NotIn(string column, params object[] inValues) {
			Util.CheckNotNullParameter(column, "column");
			Util.CheckNotNullParameter(inValues, "inValues");
			CheckNoAndOr();
			_sb.Append("NOT ");
			In(column, inValues);
			return this;
		}

		public WhereBuilder Between(object start, object end) {
			Util.CheckNotNullParameter(start, "start");
			Util.CheckNotNullParameter(end, "end");
			CheckNoAndOr();
			_sb.Append($"BETWEEN {start} AND {end}");
			return this;
		}
		
		private void CheckNeedNewCondition() {
			if(!_needNewCondition) return;
			if(_sb.Length <= 0) throw new ArgumentException("at the start of where");
			throw new ArgumentException("already has \"AND\" or \"OR\"");
		}

		private void CheckNoAndOr() {
			if(!_needNewCondition) throw new ArgumentException("a condition need \"AND\" or \"OR\" to connect each other");
		}

		public string BuildSqlCommand() {
			if(_needNewCondition) throw new Exception("can not end with \"AND\" or \"OR\"");
			if(_bracket != 0) throw new Exception("bracket count error");
			return _sb.ToString();
		}

		public override string ToString() { return BuildSqlCommand(); }

		private static ConditionSymbol Build(string con) {
			return new(con);
		}

		public class ConditionSymbol {
			internal readonly string Condition;
			
			internal ConditionSymbol(string con) {
				Condition = con;
			}
			
		}
		
	}
	
}