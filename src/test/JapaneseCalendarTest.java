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
	 * �n�b�s�[�}���f�[�̃e�X�g
	 *
	 * @throws ParseException
	 */
	@Test
	public void happyMondayTest() throws ParseException {
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/11"));//���l�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/07/19"));//�C�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/20"));//�h�V�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/10/11"));//�̈�̓�

		JapaneseCalendar.HappyMonday hm = null;

		//���l�̓�
		hm = JapaneseCalendar.HappyMonday.SEIJIN_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(0)));

		//�C�̓�
		hm = JapaneseCalendar.HappyMonday.UMI_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(1)));

		//�h�V�̓�
		hm = JapaneseCalendar.HappyMonday.KEIRO_NO_HI;
		assertEquals(true, hm.getDateTime(2010).equals(list2010.get(2)));

		//�̈�̓�
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
	 * �Œ�j���̃e�X�g
	 *
	 * @throws ParseException
	 */
	@Test
	public void fixedHolidayTest() throws ParseException {
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/01"));//����
		list2010.add(new DateTime("yyyy/MM/dd", "2010/02/11"));//�����L�O�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/04/29"));//���a�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/03"));//���@�L�O��
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/04"));//�݂ǂ�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/05"));//���ǂ��̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/03"));//�����̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/23"));//�ΘJ���ӂ̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/12/23"));//�V�c�a����

		JapaneseCalendar.FixedHoliday fh = null;
		//�����L�O�̓�
		fh = JapaneseCalendar.FixedHoliday.KENKOKU_KINEN_NO_HI;
		assertEquals(true, fh.getDateTime(2010).equals(new DateTime("yyyy/MM/dd", "2010/02/11")));

		//���a�̓�
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
	 * �t���̓��A�H���̓��̃e�X�g
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
	 * �U�֋x���e�X�g
	 *
	 * @throws ParseException
	 */
	@Test
	public void actingHolidayTest() throws ParseException {
		ArrayList<DateTime> list = new ArrayList<DateTime>();

		//2007/01/01 �` 2024/09/23 �܂ł̐U�֋x��
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

		// �����̋x��
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
	 * �����e�X�g
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
	 * �x�������e�X�g
	 *
	 * @throws ParseException
	 */
	@Test
	public void holidayTest() throws ParseException {

		//2010�N�̏j��
		ArrayList<DateTime> list2010 = new ArrayList<DateTime>();
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/01"));//����
		list2010.add(new DateTime("yyyy/MM/dd", "2010/01/11"));//���l�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/02/11"));//�����L�O�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/03/21"));//�t���̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/03/22"));//�U�֋x���i�t���̓��j
		list2010.add(new DateTime("yyyy/MM/dd", "2010/04/29"));//���a�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/03"));//���@�L�O��
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/04"));//�݂ǂ�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/05/05"));//���ǂ��̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/07/19"));//�C�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/20"));//�h�V�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/09/23"));//�H���̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/10/11"));//�̈�̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/03"));//�����̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/11/23"));//�ΘJ���ӂ̓�
		list2010.add(new DateTime("yyyy/MM/dd", "2010/12/23"));//�V�c�a����

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
		System.out.print("�����x������F");
		sw.printLastTime();
	}

}
