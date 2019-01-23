package test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import jp.co.altonotes.model.DateTime;

import org.junit.Test;


public class DateTimeTest {

	@Test
	public void countTest() throws ParseException {
		DateTime time1 = new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 00:00 00");
		DateTime time2 = new DateTime("yyyy/MM/dd HH:mm ss", "2008/12/31 23:00 00");

		assertEquals(1, time1.countDaysFrom(time2));
		assertEquals(1, time1.countHoursFrom(time2));
		assertEquals(60, time1.countMinutesFrom(time2));
		assertEquals(3600, time1.countSecondsFrom(time2));
		assertEquals(3600000, time1.countMilliSecondsFrom(time2));

		assertEquals(-1, time2.countDaysFrom(time1));
		assertEquals(-1, time2.countHoursFrom(time1));
		assertEquals(-60, time2.countMinutesFrom(time1));
		assertEquals(-3600, time2.countSecondsFrom(time1));
		assertEquals(-3600000, time2.countMilliSecondsFrom(time1));

		assertEquals(0, time1.countDaysFrom(time1));
		assertEquals(0, time1.countHoursFrom(time1));
		assertEquals(0, time1.countMinutesFrom(time1));
		assertEquals(0, time1.countSecondsFrom(time1));
		assertEquals(0, time1.countMilliSecondsFrom(time1));
	}

	@Test
	public void moveTest() throws ParseException {
		DateTime time1 = new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 00:00 00");
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2008/12/31 00:00 00").equals(time1.moveByDay(-1)));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 00:00 00").equals(time1.moveByDay(0)));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 00:00 00").equals(time1.moveByDay(1)));

		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 01:00 00").equals(time1.moveByHour(1)));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 00:01 00").equals(time1.moveByMinute(1)));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/01 00:00 01").equals(time1.moveBySecond(1)));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss.SSS", "2009/01/01 00:00 00.001").equals(time1.moveByMilliSecond(1)));

	}

	@Test
	public void compareTest() throws ParseException {
		DateTime time1 = new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 01");

		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2010/01/03 00:00 00").beforeByTime(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2008/01/01 02:02 00").beforeByTime(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 01").beforeByTime(time1));

		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2008/01/03 01:02 00").afterByTime(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2010/01/01 00:02 00").afterByTime(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 01").afterByTime(time1));

		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 01").equalsByTime(time1));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/03 01:01 01").equalsByTime(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 02").equalsByTime(time1));

		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 01:01 01").equalsByDate(time1));
		assertEquals(true, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/02 23:59 59").equalsByDate(time1));
		assertEquals(false, new DateTime("yyyy/MM/dd HH:mm ss", "2009/01/03 01:01 01").equalsByDate(time1));
	}

	@Test
	public void maxTest() {
		assertEquals(DateTime.date("20100228"), DateTime.date("20100201").getLastDateOfMonth());
		assertEquals(DateTime.date("20100430"), DateTime.date("20100401").getLastDateOfMonth());
		assertEquals(DateTime.date("20100531"), DateTime.date("20100501").getLastDateOfMonth());

		assertEquals(DateTime.date("20100131"), DateTime.date("20100101").getLastDateOfMonth());
		assertEquals(DateTime.date("20100131"), DateTime.date("20100115").getLastDateOfMonth());
		assertEquals(DateTime.date("20100131"), DateTime.date("20100131").getLastDateOfMonth());
	}

}
