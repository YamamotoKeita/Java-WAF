package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.co.altonotes.model.CalendarDate;

import org.junit.Test;

/**
 *
 * @author Yamamoto Keita
 *
 */
public class CalendarDateTest {

	/**
	 *
	 */
	@Test
	public void デフォルトコンストラクター() {
		CalendarDate date = new CalendarDate();
		assertEquals("????/??/??, ？？ ??年??月??日", date.toString());
		assertTrue(date.isUnDefined());
		assertFalse(date.isDefined());
		assertFalse(CalendarDate.today().equals(date));
		assertEquals(CalendarDate.UNDEFINED, date.getYear());
		assertEquals(CalendarDate.UNDEFINED, date.getMonth());
		assertEquals(CalendarDate.UNDEFINED, date.getDay());
		assertEquals(CalendarDate.UNDEFINED, date.getJapaneseNenGo());
		assertEquals(CalendarDate.UNDEFINED, date.getJapaneseYear());

		try {
			date.getDate();
			fail();
		} catch (IllegalStateException e) {
			assertEquals("年月日が全てセットされていません：????/??/??, ？？ ??年??月??日", e.getMessage());
		}
	}

	/**
	 * @throws ParseException 
	 * 
	 */
	@Test
	public void コンストラクター() throws ParseException {
		CalendarDate date1 = new CalendarDate(Calendar.getInstance());
		CalendarDate date2 = new CalendarDate(new Date());
		CalendarDate date3 = new CalendarDate(new Date().getTime());
		CalendarDate date4 = new CalendarDate(new Timestamp(new Date().getTime()));
		
		assertEquals(date1, date2);
		assertEquals(date2, date3);
		assertEquals(date3, date4);

		CalendarDate date5 = new CalendarDate("yyyyMMdd", "20110401");
		CalendarDate date6 = new CalendarDate(new SimpleDateFormat("yyyyMMdd").parse("20110401"));
		CalendarDate date7 = new CalendarDate("GGGGyy年MM月dd日", "平成23年04月01日", new Locale("ja", "JP", "JP"));
		
		assertEquals(date5, date6);
		assertEquals(date6, date7);
		
		assertTrue(date1.isDefined());
		assertTrue(date2.isDefined());
		assertTrue(date3.isDefined());
		assertTrue(date4.isDefined());
		assertTrue(date5.isDefined());
		
		assertFalse(date1.isUnDefined());
		assertFalse(date2.isUnDefined());
		assertFalse(date3.isUnDefined());
		assertFalse(date4.isUnDefined());
		assertFalse(date5.isUnDefined());
	}

	/**
	 * 
	 * @throws ParseException
	 */
	@Test
	public void create() throws ParseException {
		CalendarDate date0 = new CalendarDate(new SimpleDateFormat("yyyyMMdd").parse("20110401"));
		CalendarDate date1 = CalendarDate.create(2011, 4, 1);
		CalendarDate date2 = CalendarDate.createByJapaneseCalendar(CalendarDate.HEISEI, 23, 4, 1);
		
		assertEquals(date0, date1);
		assertEquals(date1, date2);
	}

	/**
	 * 
	 */
	@Test
	public void compare() {
		CalendarDate date1 = CalendarDate.create(2011, 4, 1);
		CalendarDate date2 = CalendarDate.create(2011, 4, 1);
		CalendarDate date3 = CalendarDate.create(2011, 4, 2);
		CalendarDate date4 = CalendarDate.create(2011, 3, 31);
		
		assertTrue(date1.afterOrEquals(date2));
		assertTrue(date1.afterOrEquals(date4));
		assertTrue(date1.beforeOrEquals(date2));
		assertTrue(date1.beforeOrEquals(date3));
		assertFalse(date1.afterOrEquals(date3));
		assertFalse(date1.beforeOrEquals(date4));
		
		assertTrue(date1.after(date4));
		assertFalse(date1.after(date2));

		assertTrue(date1.before(date3));
		assertFalse(date1.before(date2));
	}
	
	/**
	 *
	 */
	@Test
	public void setTest() {
		CalendarDate date = new CalendarDate();

		assertEquals("????/??/??, ？？ ??年??月??日", date.toString());

		date.setYear(2010);
		assertEquals("2010/??/??, 平成 22年??月??日", date.toString());
		assertEquals(2010, date.getYear());
		assertEquals(CalendarDate.HEISEI, date.getJapaneseNenGo());
		
		date.setMonth(12);
		assertEquals("2010/12/??, 平成 22年12月??日", date.toString());
		assertEquals(2010, date.getYear());
		assertEquals(CalendarDate.HEISEI, date.getJapaneseNenGo());
		assertEquals(12, date.getMonth());

		date.setDay(7);
		assertEquals("2010/12/07, 平成 22年12月07日", date.toString());
		assertEquals(2010, date.getYear());
		assertEquals(CalendarDate.HEISEI, date.getJapaneseNenGo());
		assertEquals(12, date.getMonth());
		assertEquals(7, date.getDay());

		date.setJapaneseNenGo(CalendarDate.SHOWA);
		assertEquals("1947/12/07, 昭和 22年12月07日", date.toString());
		assertEquals(1947, date.getYear());
		assertEquals(CalendarDate.SHOWA, date.getJapaneseNenGo());
		assertEquals(12, date.getMonth());
		assertEquals(7, date.getDay());

		date.setDay(CalendarDate.UNDEFINED);
		assertEquals("1947/12/??, 昭和 22年12月??日", date.toString());
		assertEquals(1947, date.getYear());
		assertEquals(CalendarDate.SHOWA, date.getJapaneseNenGo());
		assertEquals(12, date.getMonth());
		assertEquals(CalendarDate.UNDEFINED, date.getDay());

		date.setMonth(CalendarDate.UNDEFINED);
		assertEquals("1947/??/??, 昭和 22年??月??日", date.toString());

		date.setYear(CalendarDate.UNDEFINED);
		assertEquals("????/??/??, ？？ ??年??月??日", date.toString());

		date.setJapaneseNenGo(CalendarDate.HEISEI);
		assertEquals("????/??/??, 平成 ??年??月??日", date.toString());

		date.setJapaneseYear(23);
		assertEquals("2011/??/??, 平成 23年??月??日", date.toString());
		assertEquals(2011, date.getYear());
		assertEquals(CalendarDate.HEISEI, date.getJapaneseNenGo());
		assertEquals(CalendarDate.UNDEFINED, date.getMonth());
		assertEquals(CalendarDate.UNDEFINED, date.getDay());
	}

	/**
	 * 
	 */
	@Test
	public void today() {
		Calendar calendar = Calendar.getInstance();
		clearTime(calendar);
		assertEquals(calendar, CalendarDate.today().getCalendar());
	}

	/**
	 * 
	 */
	@Test
	public void date() {
		assertEquals(date("20100101"), CalendarDate.create(2010, 1, 1).getCalendar());
	}

	/**
	 * 
	 */
	@Test
	public void equals() {
		CalendarDate today = CalendarDate.today();
		CalendarDate date = new CalendarDate();

		Calendar cal = Calendar.getInstance();
		date.setYear(cal.get(Calendar.YEAR));
		date.setMonth(cal.get(Calendar.MONTH) + 1);
		date.setDay(cal.get(Calendar.DAY_OF_MONTH));

		assertTrue(today.equals(date));
	}
	
	/**
	 * 
	 */
	@Test
	public void move() {
		CalendarDate date = CalendarDate.create(2011, 4, 1);
		date = date.moveByDay(1);
		assertEquals(date, CalendarDate.create(2011, 4, 2));

		date = date.moveByDay(-1);
		assertEquals(date, CalendarDate.create(2011, 4, 1));

		date = date.moveByMonth(1);
		assertEquals(date, CalendarDate.create(2011, 5, 1));

		date = date.moveByMonth(-1);
		assertEquals(date, CalendarDate.create(2011, 4, 1));

		date = date.moveByYear(1);
		assertEquals(date, CalendarDate.create(2012, 4, 1));

		date = date.moveByYear(-1);
		assertEquals(date, CalendarDate.create(2011, 4, 1));
		
		// 繰り上がり、繰り下がり
		date = date.moveByDay(30);
		assertEquals(date, CalendarDate.create(2011, 5, 1));
		
		date = date.moveByDay(-30);
		assertEquals(date, CalendarDate.create(2011, 4, 1));
		
		date = date.moveByMonth(9);
		assertEquals(date, CalendarDate.create(2012, 1, 1));

		date = date.moveByMonth(-9);
		assertEquals(date, CalendarDate.create(2011, 4, 1));
	}

	/**
	 * Calendarの時刻を全て0にする。
	 *
	 * @param calendar
	 */
	private static void clearTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * yyyyMMddの文字列からCalendarを作成する。
	 *
	 * @param str
	 * @return
	 */
	private static Calendar date(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = formatter.parse(str);
		} catch (ParseException e) {
			throw (IllegalArgumentException) new IllegalArgumentException().initCause(e);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
}
