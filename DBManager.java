package com.pro.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


public class DBManager {
	
	private Vector<String> messages;
	private String host;
	private String db;
	private String username;
	private String password;
	
	private Connection connexion = null;
	private Statement statement = null;
	private PreparedStatement cps = null;

	public DBManager(String db, String username, String password) {
		this("localhost", db, username, password);
	}
	
	public DBManager(String host, String db, String username, String password) {
		messages = new Vector<>();
		this.host = host;
		this.db = db;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * <h1>Set a Driver Manager Connection to a database user identified by a password</h1>
	 * Intern syntax : Connection connection = DriverManager.getConnection(url, user, pass)
	 * <p>url : jdbc:mysql://host:port/database</p>
	 * <p>user : username for SQL server </p>
	 * <p>pass : user password for SQL server </p>
	 * ....
	 * <p>This Method initialize a Statement Object that will be used to perform SQL queries </p>
	 * <p>Intern syntax : Statement statement = connection.getStatement() </p>
	 * ....*/
	public void connect() {
		// SETTING THE DRIVER
		try {
			Class.forName("com.mysql.jdbc.Driver");
			messages.add(">> DRIVER LOADED.");
		} catch (ClassNotFoundException e) {
			messages.add(e.getMessage());
		}
		// CONNEXION
		String url = "jdbc:mysql://" + host + ":3306/" + db; 
		try {
			connexion = DriverManager.getConnection(url, username, password);
			messages.add(">> USER " + username + " CONNECTED TO " + db + " DATABASE.");
		} catch (SQLException e) {
			messages.add(e.getMessage());
		}
		// STATEMENT
		try {
			statement = connexion.createStatement();
			messages.add(">> STATEMENT CREATED.");
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		// SHOW LOG
		showLog();
	}
	
	/** Close Connection and Statement Objects */
	public void closeConnection() {
		try {
			if(connexion != null) connexion.close();
			if(statement != null) statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// SERVICES--------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	/** GENERAL STATEMENTS SELECT, INSERT, UPDATE, DELETE 
	 * @param sql SQL Syntax used to perform any actions on the database
	 * @return 	  A ResultSet Object with executeQuery Method in Statement Object
	 * <br>Example 1 : executeQuery("SELECT * FROM students")
	 * <br>O = <b>SELECT * FROM students</b> 
	 * <br>Example 2 : executeQuery("SELECT * FROM students WHERE id = 5")
	 * <br>O = <b>SELECT * FROM students WHERE id = 5</b> 
	 * <br>....
	 * <br>Note : This Method is not recommended to use with INSERT, DELETE, UPDATE operations !*/
	public ResultSet executeQuery(String sql) throws SQLException {
		return statement.executeQuery(sql);
	}
	// GET AND SET GENERAL STATEMENTS (SECURED)-------------------------------------------------------------------------------
	/** GET SELECT STATEMENTS 
	 * @param sql SQL Prepared Query Syntax used to perform SELECT actions on the database
	 * @param attrs List of values that will be replace the '?' Symbol in the prepared Query
	 * <br>Example 1 : get("SELECT * FROM students WHERE id = ?", 5)
	 * <br>O = <b>SELECT * FROM students WHERE id = 5</b> 
	 * <br>Example 2 : get("SELECT * FROM students WHERE note > ? AND id = ?", 12, 5) 
	 * <br>O = <b>SELECT * FROM students WHERE note > 12 AND id = 5</b> 
	 * <br>....
	 * <br>Note : This is a recommended method to perform all select actions with prepared queries
	 * <br>Note : This is an unaffected SQL injections method*/
	public ResultSet get(String sql, Object ...attrs) throws Exception {
		cps = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for(int i = 1; i <= attrs.length; i++) {
			cps.setObject(i, attrs[i - 1]);
		}
		return cps.executeQuery();
	}
	
	/** SET INSERT, UPDATE, DELETE STATEMENTS 
	 * @param sql SQL Prepared Query Syntax used to perform INSERT, UPDATE, DELETE actions on the database
	 * @param attrs List of values that will be replace the '?' Symbol in the prepared Query
	 * <br>Example 1 : set("INSERT INTO students (name, note) VALUES (?, ?)", "HAMZA", 12)
	 * <br>O = <b>INSERT INTO students (name, note) VALUES ('HAMZA', 12)</b> 
	 * <br>Example 2 : set("UPDATE students SET name LIKE ? WHERE id < ?", "HAMZA", 5)
	 * <br>O = <b>UPDATE students SET name LIKE 'HAMZA' WHERE id < 5</b> 
	 * <br>Example 3 : set("DELETE FROM students WHERE id <> ?", 5)
	 * <br>O = <b>DELETE FROM students WHERE id <> 5</b>
	 * <br>....
	 * <br>Note : This is a recommended method to perform all insert, update, delete actions with prepared queries
	 * <br>Note : This is an unaffected SQL injections method*/
	public int set(String sql, Object ...attrs) throws Exception {
		cps = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for(int i = 1; i <= attrs.length; i++) {
			cps.setObject(i, attrs[i - 1]);
		}
		return cps.executeUpdate();
	}
	//----------------------------------------------------------------------------------------------------------------------------	
	// SELECT SPECIFIC OPERATIONS-----------------------------------------------------------------------------------------------
	/** SELECT * FROM table; 
	 * @param table Name of the database table which the action should be performed
	 * <br>Example 1 : selectAllFrom("students")
	 * <br>O = <b>SELECT * FROM students</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific select on a specific table*/
	public ResultSet selectAllFrom(String table) throws Exception {
		return statement.executeQuery("SELECT * FROM " + table);
	}
	
	/** SELECT * FROM table WHERE index = value; 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : selectAllFrom("students", "name", "HAMZA")
	 * <br>O = <b>SELECT * FROM students WHERE name = 'HAMZA'</b>
	 * <br>Example 2 : selectAllFrom("students", "id", "5")
	 * <br>O = <b>SELECT * FROM students WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific select on a specific table*/
	public ResultSet selectAllFrom(String table, String index, String value) throws Exception {
		return statement.executeQuery("SELECT * FROM " + table + " WHERE " + index + " = " + format(value));
	}
	
	/** SELECT * FROM table WHERE index = value; (SECURED) 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : selectAllFrom("students", "name", "HAMZA")
	 * <br>O = <b>SELECT * FROM students WHERE name = 'HAMZA'</b>
	 * <br>Example 2 : selectAllFrom("students", "id", "5")
	 * <br>O = <b>SELECT * FROM students WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific select on a specific table
	 * <br>Note : This is an unaffected SQL injections method*/
	public ResultSet s_selectAllFrom(String table, String index, String value) throws Exception {
		cps = connexion.prepareStatement("SELECT * FROM " + table + " WHERE " + index + " = ?", Statement.RETURN_GENERATED_KEYS);
		cps.setString(1, value);
		return cps.executeQuery();
	}
	//----------------------------------------------------------------------------------------------------------------------------	
	// INSERT SPECIFIC OPERATIONS-----------------------------------------------------------------------------------------------
	/** INSERT INTO table VALUES (val1, val2, ....); 
	 * @param table Name of the database table which the action should be performed
	 * @param values List of values that will be added to the table
	 * <br>Example 1 : insertInto("students", "5", "HAMZA", "ALAOUI")
	 * <br>O = <b>INSERT INTO students VALUES ('5', 'HAMZA', 'ALAOUI')</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific insert on a specific table
	 * <br>Note : All Values should be a String typed data values
	 * <br>Note : Each Column data of the table should have a Column value */
	public int insertInto(String table, String ...values) throws Exception {
		return statement.executeUpdate("INSERT INTO " + table + " VALUES (" + format(values) + ")");
	}
	
	/** INSERT INTO table VALUES (val1, val2, ....); (SECURED) 
	 * @param table Name of the database table which the action should be performed
	 * @param values List of values that will be added to the table
	 * <br>Example 1 : insertInto("students", "5", "HAMZA", "ALAOUI")
	 * <br>O = <b>INSERT INTO students VALUES ('5', 'HAMZA', 'ALAOUI')</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific insert on a specific table
	 * <br>Note : Each Column data of the table should have a Column value
	 * <br>Note : This is an unaffected SQL injections method */
	public int s_insertInto(String table, String ...values) throws Exception {
		String[] fields = new String[values.length];
		for(int i = 0; i<fields.length; i++) fields[i] = "?";
		String exp = format(fields).replace('\'', ' ').trim();
		cps = connexion.prepareStatement("INSERT INTO " + table + " VALUES (" + exp + ")", Statement.RETURN_GENERATED_KEYS);
		for(int i = 1; i <= values.length; i++) {
			cps.setString(i, values[i - 1]);
		}
		return cps.executeUpdate();
	}
	//----------------------------------------------------------------------------------------------------------------------------	
	// UPDATE SPECIFIC OPERATIONS-----------------------------------------------------------------------------------------------
	/** UPDATE table SET index = value; 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : updateAll("students", "id", "5")
	 * <br>O = <b>UPDATE students SET id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific update on a specific table */
	public int updateAll(String table, String index, String value) throws Exception {
		return statement.executeUpdate("UPDATE " + table + " SET " + index + " = " + format(value));
	}
	
	/** UPDATE table SET index = value; (SECURED) 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : updateAll("students", "id", "5")
	 * <br>O = <b>UPDATE students SET id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific update on a specific table 
	 * <br>Note : This is an unaffected SQL injections method */
	public int s_updateAll(String table, String index, String value) throws Exception {
		cps = connexion.prepareStatement("UPDATE " + table + " SET " + index + " = ?", Statement.RETURN_GENERATED_KEYS);
		cps.setString(1, value);
		return cps.executeUpdate();
	}
	
	/** UPDATE table SET index = value WHERE cond_index = cond_val; 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * @param cond_index Name of the table Column that will be used as a Condition
	 * @param cond_val Value of the table Column that will be used as a Condition
	 * <br>Example 1 : updateAll("students", "name", "HAMZA", "id", "5")
	 * <br>O = <b>UPDATE students SET name = 'HAMZA' WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific update on a specific table */
	public int update(String table, String index, String value, String cond_index, String cond_val) throws Exception {
		return statement.executeUpdate("UPDATE " + table + " SET " + index + " = " + format(value) + " WHERE " + cond_index + " =  " + format(cond_val));
	}
	
	/** UPDATE table SET index = value WHERE cond_index = cond_val; (SECURED) 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * @param cond_index Name of the table Column that will be used as a Condition
	 * @param cond_val Value of the table Column that will be used as a Condition
	 * <br>Example 1 : updateAll("students", "name", "HAMZA", "id", "5")
	 * <br>O = <b>UPDATE students SET name = 'HAMZA' WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific update on a specific table 
	 * <br>Note : This is an unaffected SQL injections method */
	public int s_update(String table, String index, String value, String cond_index, String cond_val) throws Exception {
		cps = connexion.prepareStatement("UPDATE " + table + " SET " + index + " = ?" + " WHERE " + cond_index + " = ?", Statement.RETURN_GENERATED_KEYS);
		cps.setString(1, value);
		cps.setString(2, cond_val);
		return cps.executeUpdate();
	}
	//----------------------------------------------------------------------------------------------------------------------------	
	// DELETE SPECIFIC OPERATIONS-----------------------------------------------------------------------------------------------
	/** DELETE FROM table; 
	 * @param table Name of the database table which the action should be performed
	 * <br>Example 1 : deleteAllFrom("students")
	 * <br>O = <b>DELETE FROM students</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific delete on a specific table */
	public int deleteAllFrom(String table) throws Exception {
		return statement.executeUpdate("DELETE FROM " + table);
	}
	
	/** DELETE FROM table WHERE index = value; 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : deleteFrom("students", "id", "5")
	 * <br>O = <b>DELETE FROM students WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific delete on a specific table */
	public int deleteFrom(String table, String index, String value) throws Exception {
		return statement.executeUpdate("DELETE FROM " + table + " WHERE " + index + " = " + format(value));
	}
	
	/** DELETE FROM table WHERE index = value; (SECURED) 
	 * @param table Name of the database table which the action should be performed
	 * @param index Name of the table Column
	 * @param value Value of the table Column
	 * <br>Example 1 : deleteFrom("students", "id", "5")
	 * <br>O = <b>DELETE FROM students WHERE id = '5'</b>
	 * <br>....
	 * <br>Note : This is a quick method to perform a specific delete on a specific table 
	 * <br>Note : This is an unaffected SQL injections method */
	public int s_deleteFrom(String table, String index, String value) throws Exception {
		cps = connexion.prepareStatement("DELETE FROM " + table + " WHERE " + index + " = ?", Statement.RETURN_GENERATED_KEYS);
		cps.setString(1, value);
		return cps.executeUpdate();
	}
	//----------------------------------------------------------------------------------------------------------------------------
	// GETTERS AND SETTERS---------------------------------------------------------------------------------------------------------
	public Vector<String> getMessages() {
		return messages;
	}
	public void setMessages(Vector<String> messages) {
		this.messages = messages;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Connection getConnexion() {
		return connexion;
	}
	public void setConnexion(Connection connexion) {
		this.connexion = connexion;
	}
	public Statement getStatement() {
		return statement;
	}
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	public PreparedStatement getCps() {
		return cps;
	}
	/// HELP FUNCTIONS----------------------------------------------------------------------------------------------------------
	/** FORMAT MULTIPLE STRING VALUES  
	 * @param values List of String values that will be formated
	 * Example 1 : format("va1", "val2", "val3")
	 * O = <b>'val1', 'val2', 'val3'</b> */
	public static String format(String ...values) {
		String ret = "";
		for(String s : values) {
			ret += ", '" + s + "'";
		}	
		return ret.substring(1).trim();
	}
	
	public void showLog() {
		for(String s : messages) {
			System.out.println(s);
		}
	}
	/// TEST FUNCTIONS----------------------------------------------------------------------------------------------------------
	static void test1() {
		DBManager dbm = new DBManager("hmz_jee", "HAMZA", "HAMZA");
		dbm.connect();
		ResultSet res = null;
		// AFTER CONNEXION ALL OPERATION SHOULD BE INSIDE THE BLOC TRY CATCH
		try {
			// SELECT * FROM user;
			res = dbm.selectAllFrom("user");
			
			// SHOW RESULT DATA
			while(res.next()) {
				System.out.println(res.getString("email"));
				System.out.println(res.getString("password"));
				System.out.println(res.getString("username"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		dbm.closeConnection();
	}
	
	static void test2() {
		DBManager dbm = new DBManager("hmz_jee", "HAMZA", "HAMZA");
		dbm.connect();
		try {
			String table = "user";
			String email = "hamza@hotmail.com";
			String password = "passport";
			String username = "hamea5word'";
			// CLEAR TABLE
			dbm.deleteAllFrom(table);
			//dbm.deleteFrom(table, "username", "yzerf");
			//dbm.s_deleteFrom(table, "username", "hamea5word'");
			// INSERT DATA
			dbm.insertInto(table, "u@hotmail.com", "jkdedoedef", "yzerf");
			dbm.s_insertInto(table, email, password, username);
			// UPDATE DATA
			dbm.updateAll(table, "password", "");
			//dbm.s_updateAll(table, "username", "'hamza5word'");
			dbm.update(table, "username", "hamza5word", "email", email);
			dbm.s_update(table, "username", "'hamza5word'", "email", email);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.closeConnection();
		}
	}
	
	static void test3() {
		DBManager dbm = new DBManager("hmz_jee", "root", "");
		dbm.connect();
		
		try {
			//dbm.set("INSERT INTO user (email, password) VALUES (? , ?)", "kdheukd@uedeod.com", "skjdheod");
			//dbm.set("INSERT INTO user (email, password) VALUES (? , ?)", "said@uedeod.com", "dede");
			dbm.set("DELETE FROM user WHERE iduser = ?", 3);
			
			ResultSet result = dbm.get("SELECT * FROM user");
			while (result.next()) {
				System.out.println(result.getObject("email"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbm.closeConnection();
		}
	}
	
	
	public static void main(String[] args) {
		test3();	
	}
	
}
