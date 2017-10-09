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

import com.ef.config.ParserParameterConfig;
import com.ef.config.ParserParameterReader;
import com.ef.database.DatabaseManager;
import com.ef.entity.AccessLogInfo;
import com.ef.util.LogUtil;

/**
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class Parser {

	private final Path fFilePath;
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	private final static String IP_REG_EXP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private final static String DATE_REG_EXP = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}";
	private final static String FILE_DELIMITTER_STRING = "\\|";

	protected DatabaseManager databaseManager;

	public Parser(String aFileName) throws IOException {
		fFilePath = Paths.get(aFileName);
		databaseManager = new DatabaseManager();
	}

	public static void main(String... args) throws IOException {
		ParserParameterConfig parserConfig = ParserParameterReader.getInstance().readParameter(args);
		String filePath = null;
		if (parserConfig != null && parserConfig.getFilePath() != null && !parserConfig.getFilePath().isEmpty()) {
			filePath = parserConfig.getFilePath();
		}

		if (filePath == null) {
			System.out.println("Enter the server log file path in this format \"C:\\log.txt\" : ");
			Scanner scanner = new Scanner(System.in);
			while (true) {
				filePath = scanner.nextLine();
				if (null == filePath || filePath.isEmpty()) {
					System.out.println("Please enter a valid file name");
				} else {
					break;
				}
			}
			scanner.close();
		}

		Parser parser = new Parser(filePath);
		parser.processRequest(parserConfig);
	}

	public void processRequest(ParserParameterConfig parserConfig) {
		try {

			processLineByLine();

			List<AccessLogInfo> blockedUserInfo = databaseManager.getBlockedUserInfo(
					new Timestamp(parserConfig.getStartDate().getTime()),
					new Timestamp(parserConfig.getEndDate().getTime()), parserConfig.getThreshold());
			for (AccessLogInfo accessLogInfo : blockedUserInfo) {
				LogUtil.consolLog("IP \"" + accessLogInfo.getIP() + "\" having \"" + accessLogInfo.getRequestCount()
						+ "\" requests");
			}
		} catch (NoSuchFileException e) {
			LogUtil.consolLog("No file found: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.consolLog(e.getMessage());
		}
	}

	private void processLineByLine() throws ParseException, IOException, ClassNotFoundException, SQLException {
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
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void processLine(String aLine) throws ParseException, ClassNotFoundException, SQLException, IOException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(aLine).useDelimiter(FILE_DELIMITTER_STRING);
			AccessLogInfo accessLogInfo = new AccessLogInfo();
			while (scanner.hasNext()) {
				processWord(scanner.next().trim(), accessLogInfo);
			}
			databaseManager.insertServerLogInfo(accessLogInfo);
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
	private void processWord(String aWord, AccessLogInfo accessLogInfo) throws ParseException {
		if (aWord.matches(IP_REG_EXP)) {
			accessLogInfo.setIP(aWord);
		} else if (aWord.matches(DATE_REG_EXP)) {
			java.util.Date date = new SimpleDateFormat(ParserParameterReader.DATE_PATTERN).parse(aWord);
			accessLogInfo.setDate(new java.util.Date(date.getTime()));
		} else {
			accessLogInfo.setRequest(aWord);
		}
	}

}
