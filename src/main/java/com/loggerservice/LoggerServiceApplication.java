package com.loggerservice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loggerservice.model.LoggerModel;

public class LoggerServiceApplication {

	public static void main(String[] args) {

		Connection con = createConnection();
		createTableForLog(con);

		FileInputStream inputStream = null;
		Scanner sc = null;
		String path = args[0]+"\\logfile.txt";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, LoggerModel> logStartMap = new HashMap<>();
		Map<String, LoggerModel> logStopMap = new HashMap<>();
		try {
			inputStream = new FileInputStream(path);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				LoggerModel obj = null;
				if (!(line.equals("[") || line.equals("]"))) {
					obj = mapper.readValue(line, LoggerModel.class);
					String logId = obj.getId() + "_" + obj.getState();

					if (obj.getState().equals("STARTED")) {
						logStartMap.put(obj.getId(), obj);

					} else {
						logStopMap.put(obj.getId(), obj);
					}
				}

			}
			
			//INSERT DATA
			logStopMap.forEach((k,v)->{
				LoggerModel loggerModel = logStartMap.get(v.getId());
				if (loggerModel != null) {
					long consumedTime = v.getTimestamp() - loggerModel.getTimestamp();
					v.setConsumedTime(consumedTime);
					if(consumedTime > 4)
					{
						v.setAlert("TRUE");
					}
					else
					{
						v.setAlert("FALSE");
					}
					}
				
				insertData(con, v);
				});	
			
			if (sc.ioException() != null)

			{
				throw sc.ioException();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	private static Connection createConnection() {
		Connection con = null;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");

			con = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
			if (con != null) {
				System.out.println("Connection created successfully");

			} else {
				System.out.println("Problem with creating connection");
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		return con;
	}

	private static void createTableForLog(Connection con) {

		String statement = "DROP TABLE LOGDATA IF EXISTS; CREATE TABLE LOGDATA (ID VARCHAR(50),"
				+ " HOST VARCHAR(50), TYPE VARCHAR(20), "
				+ "DURATION VARCHAR(20), ALERT VARCHAR(5));";

		try {
			Statement stmt = con.createStatement();
			stmt.execute(statement);
			System.out.println("table crate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void insertData(Connection con, LoggerModel obj) {
		String query = "INSERT INTO LOGDATA VALUES (?,?,?,?,?)";
		try {
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, obj.getId());
			stmt.setString(2, obj.getHost());
			stmt.setString(3, obj.getType());
			stmt.setLong(4, obj.getConsumedTime());
			stmt.setString(5, obj.getAlert());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
