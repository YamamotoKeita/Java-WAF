package test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;

import jp.co.altonotes.model.DateTime;
import jp.co.altonotes.util.JapaneseCalendar;
import jp.co.altonotes.util.StopWatch;

import org.junit.Test;

public class JapaneseCalendarTest {

	/**
	 * ハッピーマンデーのテスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void happyMondayTest() throws ParseException {
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/11"));//成人の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/07/19"));//海の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/20"));//敬老の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/10/11"));//体育の日

		JapaneseCalendar.HappyMonday hm = null;

		//成人の日
		hm = JapaneseCalendar.HappyMonday.SEIJIN_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(0)));

		//海の日
		hm = JapaneseCalendar.HappyMonday.UMI_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(1)));

		//敬老の日
		hm = JapaneseCalendar.HappyMonday.KEIRO_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(2)));

		//体育の日
		hm = JapaneseCalendar.HappyMonday.TAIIKU_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(3)));

		DateTime date = new DateTime("yyyy/MM/dd", "2010/01/01");

		for (int i = 0; i < 365; i++) {
			boolean isHappyMonday = false;
			DateTime target = date.moveByDay(i);
			for (DateTime happyMonday : list2010) {
				if (target.equals(happyMonday)) {
					isHappyMonday = true;
				}
			}
			assertEquals(isHappyMonday, JapaneseCalendar.isHappyMonday(target));
		}
	}

	/**
	 * 固定祝日のテスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void fixedHolidayTest() throws ParseException {
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/01"));//元日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/02/11"));//建国記念の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/04/29"));//昭和の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/03"));//憲法記念日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/04"));//みどりの日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/05"));//こどもの日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/03"));//文化の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/23"));//勤労感謝の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/12/23"));//天皇誕生日

		JapaneseCalendar.FixedHoliday fh = null;
		//建国記念の日
		fh = JapaneseCalendar.FixedHoliday.KENKOKU_KINEN_NO_HI;
		assertEquals(true, fh.getDateTime(2010).equals(new DateTime("yyyy/MM/dd", "2010/02/11")));

		//昭和の日
		fh = JapaneseCalendar.FixedHoliday.SHOWA_NO_HI;
		assertEquals(true, fh.getDateTime(2010).equals(new DateTime("yyyy/MM/dd", "2010/04/29")));

		DateTime date = new DateTime("yyyy/MM/dd", "2010/01/01");
		for (int i = 0; i < 365; i++) {
			boolean isFixedHoliday = false;
			DateTime target = date.moveByDay(i);
			for (DateTime fixedHoliday : list2010) {
				if (target.equals(fixedHoliday)) {
					isFixedHoliday = true;
				}
			}
			assertEquals(isFixedHoliday, JapaneseCalendar.isFixedHoliday(target));
		}
	}

	/**
	 * 春分の日、秋分の日のテスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void shunbunShuubunTest() throws ParseException {
		DateTime shunbun = JapaneseCalendar.getShunbunNoHi(2010);
		DateTime shuubun = JapaneseCalendar.getShuubunNoHi(2010);

		assertEquals(false, shunbun.equals(new DateTime("yyyy/MM/dd", "2010/03/20")));
		assertEquals(true, shunbun.equals(new DateTime("yyyy/MM/dd", "2010/03/21")));
		assertEquals(false, shunbun.equals(new DateTime("yyyy/MM/dd", "2010/03/22")));

		assertEquals(false, JapaneseCalendar.isShunbunNoHi(new DateTime("yyyy/MM/dd", "2010/03/20")));
		assertEquals(true, JapaneseCalendar.isShunbunNoHi(new DateTime("yyyy/MM/dd", "2010/03/21")));
		assertEquals(false, JapaneseCalendar.isShunbunNoHi(new DateTime("yyyy/MM/dd", "2010/03/22")));

		assertEquals(false, shuubun.equals(new DateTime("yyyy/MM/dd", "2010/09/22")));
		assertEquals(true, shuubun.equals(new DateTime("yyyy/MM/dd", "2010/09/23")));
		assertEquals(false, shuubun.equals(new DateTime("yyyy/MM/dd", "2010/09/24")));

		assertEquals(false, JapaneseCalendar.isShuubunNoHi(new DateTime("yyyy/MM/dd", "2010/09/22")));
		assertEquals(true, JapaneseCalendar.isShuubunNoHi(new DateTime("yyyy/MM/dd", "2010/09/23")));
		assertEquals(false, JapaneseCalendar.isShuubunNoHi(new DateTime("yyyy/MM/dd", "2010/09/24")));
	}

	/**
	 * 振替休日テスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void actingHolidayTest() throws ParseException {
		ArrayList<DateTime> list = new ArrayList<DateTime>();

		//2007/01/01 〜 2024/09/23 までの振替休日
		list.add(new DateTime("yyyy/MM/dd", "2012/01/02"));
		list.add(new DateTime("yyyy/MM/dd", "2017/01/02"));
		list.add(new DateTime("yyyy/MM/dd", "2023/01/02"));
		list.add(new DateTime("yyyy/MM/dd", "2007/02/12"));
		list.add(new DateTime("yyyy/MM/dd", "2018/02/12"));
		list.add(new DateTime("yyyy/MM/dd", "2024/02/12"));
		list.add(new DateTime("yyyy/MM/dd", "2016/03/21"));
		list.add(new DateTime("yyyy/MM/dd", "2010/03/22"));
		list.add(new DateTime("yyyy/MM/dd", "2007/04/30"));
		list.add(new DateTime("yyyy/MM/dd", "2012/04/30"));
		list.add(new DateTime("yyyy/MM/dd", "2018/04/30"));
		list.add(new DateTime("yyyy/MM/dd", "2008/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2009/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2013/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2014/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2015/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2019/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2020/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2024/05/06"));
		list.add(new DateTime("yyyy/MM/dd", "2024/09/23"));
		list.add(new DateTime("yyyy/MM/dd", "2007/09/24"));
		list.add(new DateTime("yyyy/MM/dd", "2018/09/24"));
		list.add(new DateTime("yyyy/MM/dd", "2013/11/04"));
		list.add(new DateTime("yyyy/MM/dd", "2019/11/04"));
		list.add(new DateTime("yyyy/MM/dd", "2024/11/04"));
		list.add(new DateTime("yyyy/MM/dd", "2008/11/24"));
		list.add(new DateTime("yyyy/MM/dd", "2014/11/24"));
		list.add(new DateTime("yyyy/MM/dd", "2007/12/24"));
		list.add(new DateTime("yyyy/MM/dd", "2012/12/24"));
		list.add(new DateTime("yyyy/MM/dd", "2018/12/24"));

		// 国民の休日
		list.add(new DateTime("yyyy/MM/dd", "2009/09/22"));
		list.add(new DateTime("yyyy/MM/dd", "2015/09/22"));

		DateTime date = new DateTime("yyyy/MM/dd", "2007/01/07");
		final DateTime lastDate = new DateTime("yyyy/MM/dd", "2024/12/31");

		while (!date.after(lastDate)) {
			boolean isActingHoliday = false;
			for (DateTime actingHoliday : list) {
				if (date.equals(actingHoliday)) {
					isActingHoliday = true;
				}
			}
			assertEquals(isActingHoliday, JapaneseCalendar.isActingHoliday(date));
			date = date.moveByDay(1);
		}
	}

	/**
	 * 正月テスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void shgatsuTest() throws ParseException {
		assertEquals(false, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "12/30")));
		assertEquals(true, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "12/31")));
		assertEquals(true, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "01/01")));
		assertEquals(true, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "01/02")));
		assertEquals(true, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "01/03")));
		assertEquals(false, JapaneseCalendar.isMarketShogatsu(new DateTime("MM/dd", "01/04")));
	}

	/**
	 * 休日総合テスト
	 *
	 * @throws ParseException
	 */
	@Test
	public void holidayTest() throws ParseException {

		//2010年の祝日
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/01"));//元日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/11"));//成人の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/02/11"));//建国記念の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/03/21"));//春分の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/03/22"));//振替休日（春分の日）
		list2010.add(new DateTime("yyyy/MM/dd", "2010/04/29"));//昭和の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/03"));//憲法記念日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/04"));//みどりの日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/05"));//こどもの日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/07/19"));//海の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/20"));//敬老の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/23"));//秋分の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/10/11"));//体育の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/03"));//文化の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/23"));//勤労感謝の日
		list2010.add(new DateTime("yyyy/MM/dd", "2010/12/23"));//天皇誕生日

		DateTime date = new DateTime("yyyy/MM/dd", "2010/01/01");
		StopWatch sw = new StopWatch();
		sw.start();
		for (int i = 0; i < 365; i++) {
			boolean isHoliday = false;
			DateTime target = date.moveByDay(i);
			if (target.isWeekHoliday()) {
				isHoliday = true;
			} else {
				for (DateTime holiday : list2010) {
					if (target.equals(holiday)) {
						isHoliday = true;
					}
				}
			}
			assertEquals(isHoliday, JapaneseCalendar.isHoliday(target));
		}
		sw.stop();
		System.out.print("総合休日判定：");
		sw.printLastTime();
	}

}
