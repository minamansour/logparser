package com.ef;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @Auther Mina Mansour
 * @Date 1/10/2017
 */
public class ParserConfig {

	public enum DurationEnum {
		hourly, daily;
	}

	private Date startDate;

	private Date endDate;

	private DurationEnum duration;

	private Integer threshold;

	public Date getStartDate() {
		return startDate;
	}

	public String getStartDateAsString() {
		return new SimpleDateFormat(ParameterReader.PARSER_DATE_PATTERN).format(getStartDate());
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
		return new SimpleDateFormat(ParameterReader.PARSER_DATE_PATTERN).format(getEndDate());
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
