package com.pro.managers;

import java.sql.ResultSet;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class PoolDBManager extends DBManager {
	
	private BoneCP poolConnection = null;

	public PoolDBManager(String db, String username, String password) {
		super(db, username, password);
	}

	public PoolDBManager(String host, String db, String username, String password) {
		super(host, db, username, password);
	}
	
	/**
	 * <h1>Set a Driver Source Pool Connection to a database user identified by a password</h1>
	 * Intern syntax : Connection connection = DriverManager.getConnection(url, user, pass)
	 * <p>url : jdbc:mysql://host:port/database </p>
	 * <p>user : username for SQL server </p>
	 * <p>pass : user password for SQL server </p>
	 * ....
	 * <p>This Method set a number of connections that can be used at once </p>
	 * <p>The Number of Connections created is equal to : (MINCPP + 1) x PC </p>
	 * <p>MINCPP : Minimum Connection Per Partition</p>
	 * <p>PC : Partitions Count Number of Partitions</p>
	 * <p>This Method initialize a Statement Object that will be used to perform SQL queries</p>
	 * <p>Intern syntax : Statement statement = connection.getStatement() </p>
	 * ....*/
	public void connect() {
		// SETTING THE DRIVER
		try {
			Class.forName("com.mysql.jdbc.Driver");
			getMessages().add(">> DRIVER LOADED");
		} catch (Exception e) {
			getMessages().add(e.getMessage());
		}
		String url = "jdbc:mysql://" + getHost() + ":3306/" + getDb();
		// CONNECTION
		try {
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url);
			config.setUsername(getUsername());
			config.setPassword(getPassword());
			config.setMinConnectionsPerPartition(5); // MINIMUM CONNECTION => STARTS AT MIN + 1 = 6
			config.setMaxConnectionsPerPartition(10); // EACH PARTITIONS SHOUD BE AT MAX OF 10 CONNECTIONS
			config.setPartitionCount(2); // (MIN + 1) x PARTITIONS 
			poolConnection = new BoneCP(config);
			setConnexion(poolConnection.getConnection());
			getMessages().add(">> CONNECTION POOL CONFUGURED FOR USER " + getUsername() + " TO DATABASE " + getDb());
		} catch (Exception e) {
			getMessages().add(e.getMessage());
		}
		// STATEMENT
		try {
			setStatement(getConnexion().createStatement());
			getMessages().add(">> STATEMENT CREATED.");
		} catch (Exception e) {
			getMessages().add(e.getMessage());
		}
		// SHOW LOG
		showLog();
	}
	
	static void test1(PoolDBManager dbm) {
		try {
			ResultSet res = dbm.selectAllFrom("user");
			while(res.next()) {
				System.out.println(res.getString("email"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.closeConnection();
		}
	}
	
	static void test2(PoolDBManager dbm) {
		
		try {
			dbm.set("INSERT INTO user (email, password) VALUES (? , ?)", "hamza@hotmail.com", "lsijdl");
			ResultSet res = dbm.getCps().getGeneratedKeys();
			if(res.next()) System.out.println("THE GENERATED ID IS = " + res.getInt(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.closeConnection();
		}
	}
	
	static void test3(PoolDBManager dbm) {
		try {
			dbm.deleteAllFrom("user");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbm.closeConnection();
		}
	}

	public static void main(String[] args) {
		PoolDBManager dbm = new PoolDBManager("hmz_jee", "root", "");
		dbm.connect();
		test3(dbm);
		test2(dbm);
		test1(dbm);
	}

}
