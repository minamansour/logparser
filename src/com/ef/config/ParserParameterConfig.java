package com.ef.config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class ParserParameterConfig {

	public enum DurationEnum {
		hourly, daily;
	}

	private Date startDate;

	private Date endDate;

	private DurationEnum duration;

	private Integer threshold;
	
	private String filePath;
	

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStartDateAsString() {
		return new SimpleDateFormat(ParserParameterReader.PARSER_DATE_PATTERN).format(getStartDate());
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndDateAsString() {
		return new SimpleDateFormat(ParserParameterReader.PARSER_DATE_PATTERN).format(getEndDate());
	}

	public DurationEnum getDuration() {
		return duration;
	}

	public void setDuration(DurationEnum duration) {
		this.duration = duration;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

}
