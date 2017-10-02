package com.ef.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ef.entity.UserInfo;
import com.ef.util.LogUtil;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class DatabaseManager {

	private final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/";
	private final static String DATABASE_NAME = "parserdb";
	private final static String DATABASE_USERNAME = "root";
	private final static String DATABASE_PASSWORD = "123456";

	private Connection getDBConnection() {

		Connection connection = null;
		try {
			Class.forName(DRIVER_CLASS_NAME);
			connection = DriverManager.getConnection(DATABASE_URL + DATABASE_NAME, DATABASE_USERNAME,
					DATABASE_PASSWORD);
		} catch (SQLException e) {
			LogUtil.consolLog(e.getMessage());
		} catch (ClassNotFoundException e) {
			LogUtil.consolLog(e.getMessage());
		}
		return connection;
	}

	public void insertServerLogInfo(UserInfo userInfo) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection
					.prepareStatement("insert into server_access_log (IP,date,request) values (?,?,?)");
			preparedStatement.setString(1, userInfo.getIP());
			preparedStatement.setTimestamp(2, new Timestamp(userInfo.getLoginTime().getTime()));
			preparedStatement.setString(3, userInfo.getRequest());
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

	public void insertUserBlockInfo(UserInfo userInfo) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement("insert into server_log (IP,comment) values (?,?)");
			preparedStatement.setString(1, userInfo.getIP());
			preparedStatement.setString(2, userInfo.getComment());
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

	public List<UserInfo> getBlockedUserInfo(Timestamp startDate, Timestamp endDate, int threshold)
			throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
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
				UserInfo userInfo = new UserInfo(resultSet.getString(2), comment, resultSet.getInt(1));
				insertUserBlockInfo(userInfo);
				userInfos.add(userInfo);
			}
			return userInfos;
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
