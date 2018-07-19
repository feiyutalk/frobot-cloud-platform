package org.iceslab.frobot.commons.utils.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Date operation
 * @author whkeep
 */
public class DateUtils {
	private final static Logger LOGGER = Logger.getLogger(DateUtils.class);

	/**
	 * get the current time of the resource
	 * @return format like "2017-07-08 11:15:53"
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		return sdf.format(date);
	}

	public static Date dateAdd(Date time, int delaytime) {
		// TODO 计算给定时间多少分钟后的时间
		Calendar specialDate = Calendar.getInstance();
		specialDate.setTime(time);
		specialDate.add(Calendar.MINUTE, delaytime); // 特定时间的1年后
		Date date = specialDate.getTime();
		return date;
	}

	public static Date dateSub(Date time, int delaytime) {
		// TODO 计算给定时间多少分钟前的时间
		Calendar specialDate = Calendar.getInstance();
		specialDate.setTime(time);
		specialDate.add(Calendar.MINUTE, -delaytime);
		Date date = specialDate.getTime();
		return date;
	}

	public static boolean before(Date date1, Date date2) {
		// TODO 若date1比date2早,返回true.
		return date1.before(date2);
	}

	public static boolean after(Date date1, Date date2) {
		// TODO 若date1比date2晚.返回true.
		return date1.after(date2);
	}

	/** 
	 * The format of time transform from String to Date
	 */
	public static Date toDateTime(String time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateParse = sdf.parse(time);
			return dateParse;
		} catch (ParseException e) {
			LOGGER.debug("The format of time transform to Date error!");
		}
		return null;
	}

	/** Date型日期转化为String型日期 */
	public static String toStringTime(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(time);
		return format;
	}
}

