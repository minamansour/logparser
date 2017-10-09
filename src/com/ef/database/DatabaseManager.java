package com.ef.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ef.entity.AccessLogInfo;

/**
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class DatabaseManager {

	private final static String DRIVER_CLASS_NAME = "jdbc.driver";
	private final static String DATABASE_URL = "jdbc.url";
	private final static String DATABASE_USERNAME = "jdbc.username";
	private final static String DATABASE_PASSWORD = "jdbc.password";
	private Connection connection = null;
	private Properties connectionProps = new Properties();
	
	public DatabaseManager () throws IOException{
		FileInputStream fileInputStream = new FileInputStream("src/com/ef/database/db.properties");
		connectionProps.load(fileInputStream);
		fileInputStream.close();
	}
	
	private Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
		String driver = connectionProps.getProperty(DRIVER_CLASS_NAME);
		if (driver != null) {
			Class.forName("com.mysql.jdbc.Driver");
		}
		String url = connectionProps.getProperty(DATABASE_URL);
		String username = connectionProps.getProperty(DATABASE_USERNAME);
		String password = connectionProps.getProperty(DATABASE_PASSWORD);

		return DriverManager.getConnection(url, username, password);
	}


	public void insertServerLogInfo(AccessLogInfo accessLogInfo)
			throws SQLException, ClassNotFoundException, IOException {
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement("INSERT INTO server_access_log (IP,date,request) VALUES (?,?,?)");
			preparedStatement.setString(1, accessLogInfo.getIP());
			preparedStatement.setTimestamp(2, new Timestamp(accessLogInfo.getDate().getTime()));
			preparedStatement.setString(3, accessLogInfo.getRequest());
			preparedStatement.executeUpdate();

		} finally {
			if (null != preparedStatement && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (null != connection && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void insertUserBlockInfo(AccessLogInfo accessLogInfo)
			throws SQLException, ClassNotFoundException, IOException {
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("INSERT INTO  server_log (IP,comment) VALUES (?,?)");
			preparedStatement.setString(1, accessLogInfo.getIP());
			preparedStatement.setString(2, accessLogInfo.getComment());
			preparedStatement.executeUpdate();

		} finally {
			if (null != preparedStatement && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (null != connection && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public List<AccessLogInfo> getBlockedUserInfo(Timestamp startDate, Timestamp endDate, int threshold)
			throws SQLException, ClassNotFoundException, IOException {

		PreparedStatement preparedStatement = null;
		List<AccessLogInfo> accessLogInfos = new ArrayList<AccessLogInfo>();
		try {
			connection = getConnection();
			preparedStatement = connection
					.prepareStatement("SELECT count(SAL.primarykey) AS IP_COUNT, SAL.IP FROM server_access_log SAL "
							+ "Where SAL.date >= ? AND SAL.date <= ? " + "GROUP BY SAL.IP "
							+ "HAVING count(SAL.primarykey) > ? ");
			preparedStatement.setTimestamp(1, startDate);
			preparedStatement.setTimestamp(2, endDate);
			preparedStatement.setInt(3, threshold);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet != null && resultSet.next()) {
				String comment = "User was blocked due to exceed [" + resultSet.getInt(1)
						+ "] number of requests between this interval [" + startDate + "]-[" + endDate + "].";
				AccessLogInfo accessLogInfo = new AccessLogInfo(resultSet.getString(2), comment, resultSet.getInt(1));
				insertUserBlockInfo(accessLogInfo);
				accessLogInfos.add(accessLogInfo);
			}
			return accessLogInfos;
		} finally {
			if (null != preparedStatement && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (null != connection && !connection.isClosed()) {
				connection.close();
			}
		}
	}
}
