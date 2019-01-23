package jp.co.altonotes.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ����̓�����\���N���X�B
 * Date, Calendar, SimpleDateFormat�Ȃǂ̋@�\�𓝍����Ă���B
 *
 * @author Yamamoto Keita
 *
 */
public class DateTime implements Serializable, Cloneable {

	private static final long serialVersionUID = 1711840884381779802L;

	/** ���j����\���萔 */
	public static final int SUNDAY = 1;
	/** ���j����\���萔 */
	public static final int MONDAY = 2;
	/** �Ηj����\���萔 */
	public static final int TUESDAY = 3;
	/** ���j����\���萔 */
	public static final int WEDNESDAY = 4;
	/** �ؗj����\���萔 */
	public static final int THURSDAY = 5;
	/** ���j����\���萔 */
	public static final int FRIDAY = 6;
	/** �y�j����\���萔 */
	public static final int SATURDAY = 7;

	private static final long MILLISECOND_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final long MILLISECOND_IN_AN_HOUR = 1000 * 60 * 60;
	private static final long MILLISECOND_IN_A_MINUTE = 1000 * 60;

	private Calendar calendar;
	
	/**
	 * �R���X�g���N�^�[
	 *
	 * @param date
	 */
	public DateTime(Date date) {
		calendar = Calendar.getInstance();
		calendar.setTime(date);
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param calendar
	 */
	public DateTime(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param timestamp
	 */
	public DateTime(Timestamp timestamp) {
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param pattern
	 * @param source
	 */
	public DateTime(String pattern, String source) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatter.parse(source));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param pattern
	 * @param source
	 * @param locale
	 * @throws ParseException
	 */
	public DateTime(String pattern, String source, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		calendar = Calendar.getInstance();
		try {
			calendar.setTime(formatter.parse(source));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param l
	 */
	public DateTime(long l) {
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(l));
	}

	/**
	 * ���ݎ�����\��DateTime�C���X�^���X���擾����B
	 *
	 * @return ���ݎ�����\���C���X�^���X
	 */
	public static DateTime now() {
		return new DateTime(Calendar.getInstance());
	}

	/**
	 * ������\��DateTime�C���X�^���X���擾����B
	 * ������00��00��00�b000�~���b�ɐݒ肳���B
	 *
	 * @return ������\��DateTime�C���X�^���X
	 */
	public static DateTime today() {
		DateTime today = now();
		today.setHour(0);
		today.setMinute(0);
		today.setSecond(0);
		today.setMilliSecond(0);
		return today;
	}

	/**
	 * �f�t�H���g�̓��t�������擾����B<br>
	 * ���݂̎����ł�2000/01/01 00:00 00.000<br>
	 *
	 * ���̃C���X�^���X�̎����ɈӖ��͂Ȃ��A�����̏����l�ł���B
	 * DateTime �C���X�^���X�ɂ͎������m��̏�Ԃ����݂��Ȃ����ߓK���Ȏ�����ݒ肵���B<br>
	 *
	 * setYear, setMonth, setDay �ȂǂŎ�����ݒ肷�邱�Ƃ�O��ɁA�܂������C���X�^���X���擾����ꍇ���̃��\�b�h���g���B
	 *
	 * @return �f�t�H���g�̓��t����
	 */
	public static DateTime defaultInstance() {
		DateTime date = now();
		date.setYear(2000);
		date.setMonth(1);
		date.setDay(1);
		date.setHour(0);
		date.setMinute(0);
		date.setSecond(0);
		date.setMilliSecond(0);
		return date;
	}

	/**
	 * yyyyMMdd�̃t�H�[�}�b�g�œ��t���w�肵�ăC���X�^���X���쐬����B
	 *
	 * @param str
	 * @return �����̕�����ŕ\�������t
	 */
	public static DateTime date(String str) {
		return new DateTime("yyyyMMdd", str);
	}

	/**
	 * HH:mm�̃t�H�[�}�b�g�Ŏ������w�肵�ăC���X�^���X���쐬����B
	 *
	 * @param str
	 * @return �����̕�����ŕ\�������t
	 */
	public static DateTime time(String str) {
		DateTime today = DateTime.today();
		
		DateTime time = new DateTime("HH:mm", str);
		time.setYear(today.getYear());
		time.setMonth(today.getMonth());
		time.setDay(today.getDay());
		
		return time;
	}

	/**
	 * �����Ɏw�肵�����t���O�̓��t�����肷��B
	 *
	 * @param dateTime
	 * @return ���̓��t�������Ɏw�肵�����t���O�̏ꍇ <code>true</code>
	 */
	public boolean before(DateTime dateTime) {
		return calendar.before(dateTime.getCalendar());
	}

	/**
	 * �����Ɏw�肵�����t����̓��t�����肷��B
	 * @param dateTime
	 * @return ���̓��t�������Ɏw�肵�����t����̏ꍇ <code>true</code>
	 */
	public boolean after(DateTime dateTime) {
		return calendar.after(dateTime.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�̎��������A�����̎������O�����肷��B<br>
	 * ���t�͖������Ď����̂ݔ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X�̎��������A�����̎������O�̏ꍇ <code>true</code>
	 */
	public boolean beforeByTime(DateTime dateTime) {
		DateTime newThis = this.defaultYearDate();
		DateTime newArg = dateTime.defaultYearDate();
		return newThis.getCalendar().before(newArg.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�̎��������A�����̎������ォ���肷��B<br>
	 * ���t�͖������Ď����̂ݔ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X�̎��������A�����̎�������̏ꍇ
	 */
	public boolean afterByTime(DateTime dateTime) {
		DateTime newThis = this.defaultYearDate();
		DateTime newArg = dateTime.defaultYearDate();
		return newThis.getCalendar().after(newArg.getCalendar());
	}

	/**
	 * ���̃C���X�^���X��������DateTime���O�̓��t��\���ꍇtrue��Ԃ��B
	 * �����͖������ē��t�����Ŕ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X��������DateTime���O�̓��t��\���ꍇtrue
	 */
	public boolean beforeByDate(DateTime dateTime) {
		DateTime newThis = this.minimumTimeOfDate();
		DateTime newArg = dateTime.minimumTimeOfDate();
		return newThis.getCalendar().before(newArg.getCalendar());
	}

	/**
	 * ���̃C���X�^���X��������DateTime����̓��t��\���ꍇtrue��Ԃ��B
	 * �����͖������ē��t�����Ŕ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X��������DateTime����̓��t��\���ꍇtrue
	 */
	public boolean afterByDate(DateTime dateTime) {
		DateTime newThis = this.minimumTimeOfDate();
		DateTime newArg = dateTime.minimumTimeOfDate();
		return newThis.getCalendar().after(newArg.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�̔N������������DateTime�Ɠ����������肷��B
	 * �����b�~���b�𖳎����ē��t�����Ŕ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X�̔N������������DateTime�Ɠ������ꍇ true
	 */
	public boolean equalsByDate(DateTime dateTime) {
		return this.minimumTimeOfDate().equals(dateTime.minimumTimeOfDate());
	}

	/**
	 * ���̃C���X�^���X�̎�����������DateTime�Ɠ����������肷��B
	 * �N�����𖳎����Ď��������Ŕ�r����B
	 *
	 * @param dateTime
	 * @return ���̃C���X�^���X�̎�����������DateTime�Ɠ������ꍇ true
	 */
	public boolean equalsByTime(DateTime dateTime) {
		return this.defaultYearDate().equals(dateTime.defaultYearDate());
	}

	/**
	 * �w�肵���t�H�[�}�b�g�ŕ�����ɕϊ�����B
	 *
	 * @param pattern
	 * @return ���̓��t��\��������
	 */
	public String format(String pattern) {
		return format(pattern, Locale.US);
	}

	/**
	 * �w�肵���t�H�[�}�b�g�Řa��̕�����ɕϊ�����B
	 *
	 * @param pattern
	 * @return ���̓��t��a���ŕ\��������
	 */
	public String formatInJapanese(String pattern) {
		return format(pattern, new Locale("ja", "JP", "JP"));
	}

	/**
	 * �w�肵��Locale�̃t�H�[�}�b�g�œ����𕶎���ɕϊ�����B
	 *
	 * @param pattern
	 * @param locale
	 * @return ���̓��t��\��������
	 */
	public String format(String pattern, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		String result = formatter.format(calendar.getTime());
		return result;
	}
	
	/**
	 * ���̍ŏ��̓��t��Ԃ�
	 * @return ���̍ŏ��̓��t
	 */
	public DateTime firstDateOfMonth() {
		DateTime clone = this.clone();
		clone.setDay(1);
		return clone;
	}

	/**
	 * �w�肵���~���b�A������i�߂��C���X�^���X���擾����B<br>
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̎����ɖ߂�B
	 *
	 * @param milli
	 * @return �w�肵���~���b�A������i�߂��C���X�^���X
	 */
	public DateTime moveByMilliSecond(int milli) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MILLISECOND, milli);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵���b�A������i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̎����ɖ߂�B
	 *
	 * @param second
	 * @return �w�肵���b�A������i�߂�DateTime�C���X�^���X
	 */
	public DateTime moveBySecond(int second) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.SECOND, second);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵�����A������i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̎����ɖ߂�B
	 *
	 * @param minute
	 * @return �w�肵�����A������i�߂�DateTime�C���X�^���X
	 */
	public DateTime moveByMinute(int minute) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MINUTE, minute);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵�����ԁA������i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̎����ɖ߂�B
	 *
	 * @param hour
	 * @return �w�肵�����ԁA������i�߂�DateTime�C���X�^���X
	 */
	public DateTime moveByHour(int hour) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.HOUR, hour);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵�������A���t��i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param day
	 * @return ���t���ړ�����DateTime�C���X�^���X
	 */
	public DateTime moveByDay(int day) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.DATE, day);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵�������A���t��i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param month
	 * @return ���t���ړ�����DateTime�C���X�^���X
	 */
	public DateTime moveByMonth(int month) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MONTH, month);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵���N���A���t��i�߂�DateTime�C���X�^���X���擾����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param year
	 * @return ���t���ړ�����DateTime�C���X�^���X
	 */
	public DateTime moveByYear(int year) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.YEAR, year);
		return new DateTime(newCalendar);
	}

	/**
	 * �w�肵�����t�������牽�~���b�ォ���擾����B
	 * �����ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B
	 *
	 * @param time
	 * @return �����̓��t��������A���̓��t�����܂łɌo�߂����~���b
	 */
	public int countMilliSecondsFrom(DateTime time) {
		long milliSecond = getTimeInMillis() - time.getTimeInMillis();
		return (int) milliSecond;
	}

	/**
	 * �w�肵�����t�������牽�b�ォ���擾����B
	 * �����ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B
	 *
	 * @param time
	 * @return �����̓��t��������A���̓��t�����܂łɌo�߂����b
	 */
	public int countSecondsFrom(DateTime time) {
		long minute = (getTimeInMillis() - time.getTimeInMillis()) / 1000;
		return (int) minute;
	}

	/**
	 * �w�肵�����t�������牽���ォ���擾����B
	 * �����ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B
	 *
	 * @param time
	 * @return �����̓��t��������A���̓��t�����܂łɌo�߂�����
	 */
	public int countMinutesFrom(DateTime time) {
		long minute = (getTimeInMillis() - time.getTimeInMillis()) / MILLISECOND_IN_A_MINUTE;
		return (int) minute;
	}

	/**
	 * �w�肵�����t�������牽���Ԍォ���擾����B
	 * �����ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B
	 *
	 * @param time
	 * @return �����̓��t��������A���̓��t�����܂łɌo�߂�������
	 */
	public int countHoursFrom(DateTime time) {
		long hour = (getTimeInMillis() - time.getTimeInMillis()) / MILLISECOND_IN_AN_HOUR;
		return (int) hour;
	}

	/**
	 * �w�肵�����t���牽���ォ���擾����B
	 * �������̏ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B
	 * �[�b�ɂ��덷���������邩�ǂ�����Java�̎����ˑ��B
	 *
	 * @param date
	 * @return �����̓��t��������A���̓��t�����܂łɌo�߂�������
	 */
	public int countDaysFrom(DateTime date) {
		DateTime date1 = this.minimumTimeOfDate();
		DateTime date2 = date.minimumTimeOfDate();
		long day = (date1.getTimeInMillis() - date2.getTimeInMillis()) / MILLISECOND_IN_A_DAY;
		return (int) day;
	}

	/**
	 * �a��̔N����Ԃ��B�iex."���a", "����"�j
	 *
	 * @return �a��̔N��
	 */
	public String getJapaneseNenGo() {
		Locale locale = new Locale("ja", "JP", "JP");
		String nenGo = new SimpleDateFormat("GGGG", locale).format(calendar.getTime());
		return nenGo;
	}

	/**
	 * �a��̔N��Ԃ��B�iex.���a56�N�̏ꍇ 56�A����20�N�̏ꍇ 20�j
	 *
	 * @return �a��̔N
	 */
	public int getJapaneseYear() {
		Calendar cal = Calendar.getInstance(new Locale("ja", "JP", "JP"));
		cal.setTime(calendar.getTime());
		return cal.get(Calendar.YEAR);
	}

	/**
	 * ������0��0��0�b0�~���b�ɃZ�b�g�����V�����C���X�^���X���쐬����B
	 *
	 * @return ������0��0��0�b0�~���b�ɃZ�b�g�����V�����C���X�^���X
	 */
	public DateTime minimumTimeOfDate() {
		DateTime newTime = this.clone();
		newTime.setHour(0);
		newTime.setMinute(0);
		newTime.setSecond(0);
		newTime.setMilliSecond(0);
		return newTime;
	}

	/**
	 * ������23��59��59�b999�~���b�ɃZ�b�g�����V�����C���X�^���X���쐬����B
	 *
	 * @return ������23��59��59�b999�~���b�ɃZ�b�g�����V�����C���X�^���X
	 */
	public DateTime maximumTimeOfDate() {
		DateTime newTime = this.clone();
		newTime.setHour(23);
		newTime.setMinute(59);
		newTime.setSecond(59);
		newTime.setMilliSecond(999);
		return newTime;
	}

	/**
	 * ���̓��t���y�������肷��B
	 *
	 * @return ���̓��t���y���̏ꍇ <code>true</code>
	 */
	public boolean isWeekHoliday() {
		int dayOfWeek = getDayOfWeek();
		return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
	}

	/**
	 * �N�������l�ɂ��A���t�Ǝ���������ێ������V�����C���X�^���X���쐬����B
	 *
	 * @return ���t�Ǝ���������ێ������V�����C���X�^���X
	 */
	public DateTime defaultYear() {
		DateTime newTime = this.clone();
		//���肪��������f�t�H���g2000�N�ɂ��Ƃ��B�����Ƃ����̂�����΂���ŁB
		newTime.setYear(2000);
		return newTime;
	}

	/**
	 * �N�Ɠ��t�������l�ɂ��A����������ێ������V�����C���X�^���X���쐬����B
	 *
	 * @return ����������ێ������V�����C���X�^���X
	 */
	public DateTime defaultYearDate() {
		DateTime newTime = this.clone();
		//���肪��������f�t�H���g2000�N�ɂ��Ƃ��B�����Ƃ����̂�����΂���ŁB
		newTime.setYear(2000);
		newTime.setMonth(1);
		newTime.setDay(1);
		return newTime;
	}

	/**
	 * �j���𐔒l�Ŏ擾����B
	 * �iex. 1:���A2:���A3:�΁A4:���A5:�؁A6:���A7:�y�j
	 *
	 * @return �j����\�����l
	 */
	public int getDayOfWeek() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * ���̍ŏI�����擾����B�iex. 1��:31�A2��:28�j
	 *
	 * @return ���̍ŏI��
	 */
	public int getMaxDayOfMonth() {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * ���̍ŏI�����擾����B�iex. 1��:31�A2��:28�j
	 *
	 * @return ���̍ŏI��
	 */
	public DateTime getLastDateOfMonth() {
		int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.set(Calendar.DAY_OF_MONTH, maximum);
		return new DateTime(newCalendar);
	}

	/**
	 * �j������{��Ŏ擾����B
	 *
	 * @return �j����\��������
	 */
	public String getYoubi() {
		int dayOfWeek = getDayOfWeek();
		switch (dayOfWeek) {
		case 1:
			return "��";
		case 2:
			return "��";
		case 3:
			return "��";
		case 4:
			return "��";
		case 5:
			return "��";
		case 6:
			return "��";
		case 7:
			return "�y";
		default:
			return null;
		}
	}

	/**
	 * ���݂��Ȃ����t��K���ȓ��t�Ɏ����ϊ����邩��ݒ肷��B
	 * true�̏ꍇ�����ϊ�����B�iex. 2010/01/32 ��2010/02/01�j
	 * �f�t�H���g��true�B
	 *
	 * @param lenient
	 */
	public void setLenient(boolean lenient) {
		calendar.setLenient(lenient);
	}

	/**
	 * Calendar�C���X�^���X���Z�b�g����B
	 *
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * Date�C���X�^���X���Z�b�g����B
	 * @param date
	 */
	public void setDate(Date date) {
		calendar.setTime(date);
	}

	/**
	 * �N���Z�b�g����B
	 * @param year
	 */
	public void setYear(int year) {
		calendar.set(Calendar.YEAR, year);
	}

	/**
	 * �����Z�b�g����B
	 * @param month
	 */
	public void setMonth(int month) {
		calendar.set(Calendar.MONTH, month - 1);
	}

	/**
	 * �����Z�b�g����B
	 * @param day
	 */
	public void setDay(int day) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * �����Z�b�g����B
	 * �i0�`24�Őݒ�\�j
	 * @param hour
	 */
	public void setHour(int hour) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
	}

	/**
	 * �����Z�b�g����B
	 * @param minute
	 */
	public void setMinute(int minute) {
		calendar.set(Calendar.MINUTE, minute);
	}

	/**
	 * �b���Z�b�g����B
	 * @param second
	 */
	public void setSecond(int second) {
		calendar.set(Calendar.SECOND, second);
	}

	/**
	 * �~���b���Z�b�g����B
	 * @param milliSecond
	 */
	public void setMilliSecond(int milliSecond) {
		calendar.set(Calendar.MILLISECOND, milliSecond);
	}

	/**
	 * �N���擾����B
	 * @return �N
	 */
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * �����擾����B
	 * @return ��
	 */
	public int getMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * �����擾����B
	 * @return ��
	 */
	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * �����擾����B�i0�`24���j
	 * @return ��
	 */
	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * �����擾����B
	 * @return ��
	 */
	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * �b���擾����B
	 * @return �b
	 */
	public int getSecond() {
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * �~���b���擾����B
	 * @return �~���b
	 */
	public int getMilliSecond() {
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * ���̃C���X�^���X�Ɠ�������\��Calendar�C���X�^���X���擾����B
	 * @return ���̃C���X�^���X�Ɠ�������\��Calendar�C���X�^���X
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * ���̃C���X�^���X�Ɠ�������\��Date�C���X�^���X���擾����B
	 * @return ���̃C���X�^���X�Ɠ�������\��Date�C���X�^���X
	 */
	public Date getDate() {
		return calendar.getTime();
	}

	/**
	 * ���̃C���X�^���X�Ɠ�����\��java.sql.Date�C���X�^���X���擾����B
	 * @return ���̃C���X�^���X�Ɠ�����\��java.sql.Date�C���X�^���X
	 */
	public java.sql.Date getSQLDate() {
		return new java.sql.Date(minimumTimeOfDate().getTimeInMillis());
	}

	/**
	 * ���̃C���X�^���X�Ɠ�������\��Timestamp�C���X�^���X���擾����B
	 * @return ���̃C���X�^���X�Ɠ�������\��Timestamp�C���X�^���X
	 */
	public Timestamp getTimestamp() {
		return new Timestamp(getTimeInMillis());
	}

	/**
	 * ���t�����̒l���~���b�Ŏ擾����B
	 * @return ���t������\���~���b
	 */
	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	/**
	 * �����񂪐��������t���`�F�b�N����
	 * @param str
	 * @param format
	 * @return ���������t�̏ꍇ<code>true</code>
	 */
	public static boolean isValidDate(String str, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		
		try {
			sdf.parse(str);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/*
	 * yyyy/MM/dd HH:mm ss.SSS �̃t�H�[�}�b�g�̕������Ԃ��B
	 *
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return format("yyyy/MM/dd HH:mm ss.SSS");
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DateTime clone() {
		DateTime clone = null;
		try {
			clone = (DateTime) super.clone();
			clone.calendar = (Calendar)calendar.clone();
		} catch (CloneNotSupportedException e) {
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		return clone;
	}

	/*
	 * (�� Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		return result;
	}

	/*
	 * �w�肵��DateTime�����̃C���X�^���X�Ɠ���������\���ꍇtrue��Ԃ��B
	 *
	 * (�� Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DateTime)) {
			return false;
		}

		DateTime dateTime = (DateTime) obj;
		return calendar.equals(dateTime.calendar);
	}

}
