using System;
using SQLiteHelper;

namespace ConsoleApp1 {
	class Program {
		static void Main(string[] args) {
			
			Console.WriteLine(Environment.CurrentDirectory);
			
			// var table = new SelectBuilder("test").AddSelectColumn("asd")
			// 	.AddSelectColumn("qwe", "t")
			// 	.AddSelectColumn("w")
			// 	.AddSelectColumn("q", "Q")
			// 	.SetLimit(5).AddOrderBy("asd").SetDesc().SetOffset(50);
			// Console.WriteLine(table.BuildSqlCommand());

		}
	}
}