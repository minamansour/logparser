package com.ef.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ef.entity.AccessLogInfo;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class DatabaseManager {

	private final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/parserdb?useSSL=false";
	private final static String DATABASE_USERNAME = "root";
	private final static String DATABASE_PASSWORD = "123456";

	private Connection getDBConnection() throws SQLException, ClassNotFoundException {
		Class.forName(DRIVER_CLASS_NAME);
		return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	}

	public void insertServerLogInfo(AccessLogInfo accessLogInfo) throws SQLException, ClassNotFoundException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection
					.prepareStatement("INSERT INTO server_access_log (IP,date,request) VALUES (?,?,?)");
			preparedStatement.setString(1, accessLogInfo.getIP());
			preparedStatement.setTimestamp(2, new Timestamp(accessLogInfo.getDate().getTime()));
			preparedStatement.setString(3, accessLogInfo.getRequest());
			preparedStatement.executeUpdate();

		} finally {
			if (null != preparedStatement && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (null != dbConnection && !dbConnection.isClosed()) {
				dbConnection.close();
			}
		}
	}

	public void insertUserBlockInfo(AccessLogInfo accessLogInfo) throws SQLException, ClassNotFoundException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement("INSERT INTO  server_log (IP,comment) VALUES (?,?)");
			preparedStatement.setString(1, accessLogInfo.getIP());
			preparedStatement.setString(2, accessLogInfo.getComment());
			preparedStatement.executeUpdate();

		} finally {
			if (null != preparedStatement && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (null != dbConnection && !dbConnection.isClosed()) {
				dbConnection.close();
			}
		}
	}

	public List<AccessLogInfo> getBlockedUserInfo(Timestamp startDate, Timestamp endDate, int threshold)
			throws SQLException, ClassNotFoundException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		List<AccessLogInfo> accessLogInfos = new ArrayList<AccessLogInfo>();
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection
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
			if (null != dbConnection && !dbConnection.isClosed()) {
				dbConnection.close();
			}
		}
	}
}
