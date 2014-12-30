//Note this one

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static test.database.Tables.*;

/**
 * Created by rewbycraft on 12/29/14.
 */
public class Main {
	public static void main(String args[]) {
		try {
			System.err.println("Creating database...");
			//This won't erase the db, but it would update the schema when you update the program, assuming you use migrations properly.
			Flyway flyway = new Flyway();
			flyway.setDataSource("jdbc:sqlite:./db.sqlite", null, null);
			flyway.migrate();


			System.err.println("Getting connection...");
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:./db.sqlite");

			System.err.println("Creating DSLContext...");
			DSLContext create = DSL.using(conn, SQLDialect.SQLITE);


			System.err.println("Inserting records...");
			//Create a record (or three)
			create.insertInto(SOMETABLE).values(1, 5, 111).execute();
			create.insertInto(SOMETABLE).values(2, 7, 145).execute();
			create.insertInto(SOMETABLE).values(3, 6, 135).execute();

			System.err.println("QUERY1");
			//Make a select query
			for (Record r : create.select().from(SOMETABLE).where(SOMETABLE.ID.eq(1)).fetch())
				System.err.println(r.getValue(SOMETABLE.ID) + "\t" + r.getValue(SOMETABLE.COL1) + "\t" + r.getValue(SOMETABLE.COL2));

			System.err.println("Updating record...");
			//Update
			create.update(SOMETABLE).set(SOMETABLE.COL2, 121).where(SOMETABLE.ID.eq(1)).execute();

			System.err.println("QUERY2");
			//Make a select query (again)
			for (Record r : create.select().from(SOMETABLE).where(SOMETABLE.ID.eq(1)).fetch())
				System.err.println(r.getValue(SOMETABLE.ID) + "\t" + r.getValue(SOMETABLE.COL1) + "\t" + r.getValue(SOMETABLE.COL2));

			//This one sorts!
			System.err.println("QUERY3");
			for (Record r : create.select().from(SOMETABLE).orderBy(SOMETABLE.COL1.asc()).fetch())
				System.err.println(r.getValue(SOMETABLE.ID) + "\t" + r.getValue(SOMETABLE.COL1) + "\t" + r.getValue(SOMETABLE.COL2));

			System.err.println("Deleting record...");
			//Delete the record
			create.delete(SOMETABLE).where(SOMETABLE.ID.eq(1)).execute();

			//We're gonna delete the db, because cleanlyness and this is merely a test.
			new File("./db.sqlite").delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
