package com.ef;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.ef.database.DatabaseManager;
import com.ef.entity.UserInfo;
import com.ef.util.LogUtil;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class Parser {

	private final Path fFilePath;
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	private final static String IP_REG_EXP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private final static String DATE_REG_EXP = "\\d{4}-\\d{2}-\\d{2}.\\d{2}:\\d{2}:\\d{2}";

	protected DatabaseManager databaseManager;

	public Parser(String aFileName) {
		fFilePath = Paths.get(aFileName);
		databaseManager = new DatabaseManager();
	}

	public static void main(String... args) throws IOException {
		ParserConfig parserConfig = ParameterReader.getInstance().readParameter(args);
		System.out.println("Enter the server log file path in this format \"C:\\log.txt\" : ");
		Scanner scanner = new Scanner(System.in);
		String fileName = null;
		while (true) {
			fileName = scanner.nextLine();
			if (null == fileName || fileName.isEmpty()) {
				System.out.println("Please enter a valid file name");
			} else {
				break;
			}
		}
		scanner.close();
		Parser parser = new Parser(fileName);
		parser.processRequest(parserConfig);
	}

	public void processRequest(ParserConfig parserConfig) {
		try {
			processLineByLine();
			List<UserInfo> blockedUserInfo = databaseManager.getBlockedUserInfo(
					new Timestamp(parserConfig.getStartDate().getTime()),
					new Timestamp(parserConfig.getEndDate().getTime()), parserConfig.getThreshold());
			for (UserInfo userInfo : blockedUserInfo) {
				LogUtil.consolLog(
						"IP \"" + userInfo.getIP() + "\" having \"" + userInfo.getRequestCount() + "\" requests");
			}
		} catch (NoSuchFileException e) {
			LogUtil.consolLog("No file found: "+e.getMessage());
		} catch (Exception e) {
			LogUtil.consolLog(e.getMessage());
		}
	}

	private void processLineByLine() throws ParseException, IOException {
		try (Scanner scanner = new Scanner(fFilePath, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				processLine(scanner.nextLine());
			}
		}
	}

	/**
	 * 
	 * 
	 * @throws ParseException
	 */
	@SuppressWarnings("resource")
	private void processLine(String aLine) throws ParseException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(aLine).useDelimiter("\\|");
			UserInfo userInfo = new UserInfo();
			while (scanner.hasNext()) {
				processWord(scanner.next().trim(), userInfo);
			}
			databaseManager.insertServerLogInfo(userInfo);
		} catch (SQLException e) {
			LogUtil.consolLog(e.getMessage());
		} finally {
			if (null != scanner) {
				scanner.close();
			}
		}
	}

	/**
	 * Process each word in the line and match it with IP & Date reg
	 * pattern.<br>
	 * Save the matched type to the user info object.
	 * 
	 * @throws ParseException
	 *             "if Date is not parsable"
	 */
	private void processWord(String aWord, UserInfo userInfo) throws ParseException {
		if (aWord.matches(IP_REG_EXP)) {
			userInfo.setIP(aWord);
		} else if (aWord.matches(DATE_REG_EXP)) {
			java.util.Date date = new SimpleDateFormat(ParameterReader.PARSER_DATE_PATTERN).parse(aWord);
			userInfo.setLoginTime(new java.util.Date(date.getTime()));
		} else {
			userInfo.setRequest(aWord);
		}
	}

}
