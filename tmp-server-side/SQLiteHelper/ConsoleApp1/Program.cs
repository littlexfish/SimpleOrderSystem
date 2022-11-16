using System;
using System.Collections.Generic;
using SQLite;
using SQLiteHelper;

namespace ConsoleApp1 {
	class Program {
		static void Main(string[] args) {

			// var sql = new SQLiteConnection("test.db");
			//
			// // create table
			// sql.CreateTable<Val>();
			//
			// // insert
			// var test = new Val {test = 1};
			// sql.Insert(test); // as same as line below
			// sql.CreateCommand("INSERT INTO Val (test, t2) VALUES (1, 'hi'), (2, 'bye')").ExecuteNonQuery();
			Console.WriteLine(new InsertBuilder("Val", "test", "t2").AddValues(1, "hi").AddValues(2, "bye").BuildSqlCommand());
			
			// update
			// sql.CreateCommand("UPDATE Val SET test=2, t2='HI' WHERE test=1 AND test IS NOT NULL").ExecuteNonQuery();
			//
			// // delete
			// sql.CreateCommand("DELETE FROM Val WHERE test=1").ExecuteNonQuery();
			//
			// // select
			// List<Val> vals = sql.Query<Val>("SELECT * FROM Val WHERE test=1 LIMIT 5");
			

		}
	}

	class Val {
		[Column("test")] public int test { get; set; } = 0;
		[Column("t2")] public string t2 { get; set; } = "";
	}
	
}