package com.ef;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.ef.ParserConfig.DurationEnum;
import com.ef.util.LogUtil;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
@SuppressWarnings("deprecation")
public class ParameterReader {

	private static String START_DATE_OPTION_TXT = "startDate";
	private static String DURATION_OPTION_TXT = "duration";
	private static String THRESHOLD_OPTION_TXT = "threshold";
	public static String PARSER_DATE_PATTERN = "yyyy-MM-dd.HH:mm:ss";
	public static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static ParameterReader parameterReader = null;

	private ParameterReader() {
	}

	public static ParameterReader getInstance() {
		if (null == parameterReader) {
			parameterReader = new ParameterReader();
		}
		return parameterReader;
	}

	public ParserConfig readParameter(String... args) {
		ParserConfig parserConfig = new ParserConfig();

		Options options = new Options();

		Option inputOption = new Option(START_DATE_OPTION_TXT, START_DATE_OPTION_TXT, true, "Input Start Date");
		inputOption.setRequired(true);
		inputOption.setType(Date.class);
		options.addOption(inputOption);

		inputOption = new Option(DURATION_OPTION_TXT, DURATION_OPTION_TXT, true, "Input Duration");
		inputOption.setRequired(true);
		inputOption.setType(DurationEnum.class);
		options.addOption(inputOption);

		inputOption = new Option(THRESHOLD_OPTION_TXT, THRESHOLD_OPTION_TXT, true, "Input Threshold");
		inputOption.setRequired(true);
		options.addOption(inputOption);

		CommandLineParser cmdparser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = cmdparser.parse(options, args);
			parserConfig.setStartDate(parseStartDate(cmd.getOptionValue(START_DATE_OPTION_TXT), PARSER_DATE_PATTERN));
			parserConfig.setThreshold(Integer.parseInt(cmd.getOptionValue(THRESHOLD_OPTION_TXT)));
			parserConfig.setDuration(parseDuration(cmd.getOptionValue(DURATION_OPTION_TXT)));
			Date endDate = getEndDate(parserConfig.getStartDate(), parserConfig.getDuration(), PARSER_DATE_PATTERN);
			parserConfig.setEndDate(endDate);

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} 
		return parserConfig;
	}

	private Date parseStartDate(String dateStr, String dateFromat) {
		Date date = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFromat);
		simpleDateFormat.setLenient(false);

		try {
			// if not valid, it will throw ParseException
			date = simpleDateFormat.parse(dateStr);

			// Format the date again with a proper format.
			simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
			String dateFormattedStr = simpleDateFormat.format(date);
			date = simpleDateFormat.parse(dateFormattedStr);
		} catch (java.text.ParseException e) {
			LogUtil.consolLog(e.getMessage());
			System.exit(1);
		}
		return date;
	}

	private Date getEndDate(Date startDate, DurationEnum durationEnum, String dateFromat) {
		Date enDate = null;
		Calendar startdateCal = Calendar.getInstance();
		startdateCal.setTime(startDate);
		switch (durationEnum) {
		case daily:
			startdateCal.add(Calendar.DAY_OF_MONTH, 1);
			break;
		case hourly:
			startdateCal.add(Calendar.HOUR_OF_DAY, 1);
			break;
		default:
			throw new RuntimeException("Default value is not supported for duration parameter.");
		}
		enDate = startdateCal.getTime();
		return enDate;
	}

	private DurationEnum parseDuration(String durationStr) {
		if (durationStr.equalsIgnoreCase(DurationEnum.daily.toString())) {
			return DurationEnum.daily;
		} else if (durationStr.equalsIgnoreCase(DurationEnum.hourly.toString())) {
			return DurationEnum.hourly;
		} else {
			throw new RuntimeException("{" + durationStr + "} is not supported for duration parameter.");
		}
	}

}
