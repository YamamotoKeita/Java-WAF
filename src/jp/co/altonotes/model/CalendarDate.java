package jp.co.altonotes.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ����̓��t��\���B<br>
 * Date, Calendar, SimpleDateFormat�Ȃǂ̋@�\�����B
 *
 * @author Yamamoto Keita
 *
 */
public class CalendarDate implements Cloneable, Serializable {

	private static final long serialVersionUID = -519632160912964120L;

	private static final long MILLISECOND_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final Locale JAPANESE_LOCALE = new Locale("ja", "JP", "JP");
	private static final String UNDEFINED_ERROR_MESSAGE = "�N�������S�ăZ�b�g����Ă��܂���F";

	/** ���j��*/
	public static final int SUNDAY = 1;
	/** ���j��*/
	public static final int MONDAY = 2;
	/** �Ηj��*/
	public static final int TUESDAY = 3;
	/** ���j��*/
	public static final int WEDNESDAY = 4;
	/** �ؗj��*/
	public static final int THURSDAY = 5;
	/** ���j��*/
	public static final int FRIDAY = 6;
	/** �y�j��*/
	public static final int SATURDAY = 7;

	/** ����	*/
	public static final int MEIJI = 1;
	/** �吳	*/
	public static final int TAISHO = 2;
	/** ���a	*/
	public static final int SHOWA = 3;
	/** ����	*/
	public static final int HEISEI = 4;

	/** �N�⌎������ݒ肳��Ă��Ȃ����Ƃ�\�����l	*/
	public static final int UNDEFINED = -1;

	private Calendar calendar = Calendar.getInstance();
	private Calendar japaneseCalendar = Calendar.getInstance(JAPANESE_LOCALE);

	private int japaneseNenGo = UNDEFINED;
	private int japaneseYear = UNDEFINED;
	private int year = UNDEFINED;
	private int month = UNDEFINED;
	private int day = UNDEFINED;

	/**
	 * �R���X�g���N�^�[�B
	 */
	public CalendarDate() {
		clearTime();
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param year
	 * @param month
	 * @param day
	 */
	public CalendarDate(int year, int month, int day) {
		clearTime();

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		japaneseCalendar.setTimeInMillis(calendar.getTimeInMillis());

		reflectFields();
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param date
	 */
	public CalendarDate(Date date) {
		setDate(date);
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param calendar
	 */
	public CalendarDate(Calendar calendar) {
		setCalendar(calendar);
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param timestamp
	 */
	public CalendarDate(Timestamp timestamp) {
		setTimestamp(timestamp);
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param pattern
	 * @param source
	 * @throws ParseException
	 */
	public CalendarDate(String pattern, String source) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		setDate(formatter.parse(source));
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param pattern
	 * @param source
	 * @param locale
	 * @throws ParseException
	 */
	public CalendarDate(String pattern, String source, Locale locale) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		setDate(formatter.parse(source));
	}

	/**
	 * �R���X�g���N�^�[
	 *
	 * @param millis
	 */
	public CalendarDate(long millis) {
		setTimeInMillis(millis);
	}

	/**
	 * ������\���C���X�^���X���擾����B
	 *
	 * @return ������\���C���X�^���X
	 */
	public static CalendarDate today() {
		return new CalendarDate(new Date());
	}

	/**
	 * �N�A���A�����w�肵�ăC���X�^���X���쐬����
	 *
	 * @param year
	 * @param month
	 * @param day
	 * @return �N�A���A�����w�肵�č쐬�����C���X�^���X
	 */
	public static CalendarDate create(int year, int month, int day) {
		return new CalendarDate(year, month, day);
	}

	/**
	 * �N���A���{�c�I�ł̔N�A���A�����w�肵�ăC���X�^���X���쐬����
	 *
	 * @param nengo
	 * @param japaneseYear
	 * @param month
	 * @param day
	 * @return �쐬�����C���X�^���X
	 */
	public static CalendarDate createByJapaneseCalendar(int nengo, int japaneseYear, int month, int day) {
		CalendarDate date = new CalendarDate();
		date.setJapaneseNenGo(nengo);
		date.setJapaneseYear(japaneseYear);
		date.setMonth(month);
		date.setDay(day);
		return date;
	}

	/**
	 * �N�A���A�����S�Đݒ肳��Ă��邩���肷��
	 *
	 * @return �N�A���A�����S�Đݒ肳�ꂽ��Ԃ̏ꍇ<code>true</code>
	 */
	public boolean isDefined() {
		return (0 < year) && (0 < month) && (0 < day);
	}

	/**
	 * �N�A���A�����S�Đݒ肳��Ă��Ȃ������肷��
	 *
	 * @return �N�A���A�����S�Đݒ肳��Ă��Ȃ��ꍇ<code>true</code>
	 */
	public boolean isUnDefined() {
		return !isDefined();
	}

	/**
	 * ���̃C���X�^���X�������̓��t���O�̓��t�����肷��B
	 *
	 * @param date
	 * @return ���̃C���X�^���X�������̓��t���O�̓��t��\���ꍇ <code>true</code>
	 */
	public boolean before(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.before(date.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�������̓��t�ȑO�����肷��B
	 * @param date
	 * @return �����̓��t�ȑO�̏ꍇ <code>true</code>
	 */
	public boolean beforeOrEquals(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return !calendar.after(date.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�������̓��t����̓��t�����肷��B
	 *
	 * @param date
	 * @return ���̃C���X�^���X�������̓��t����̓��t��\���ꍇ <code>true</code>
	 */
	public boolean after(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.after(date.getCalendar());
	}

	/**
	 * ���̃C���X�^���X�������̓��t�Ȍォ���肷��B
	 *
	 * @param date
	 * @return ���̃C���X�^���X�������̓��t�Ȍ�̏ꍇ <code>true</code>
	 */
	public boolean afterOrEquals(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return !calendar.before(date.getCalendar());
	}

	/**
	 * �w�肵���t�H�[�}�b�g�ŕ�����ɕϊ�����B
	 *
	 * @param pattern
	 * @return ���t��\��������
	 */
	public String format(String pattern) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}

		return format(pattern, Locale.US);
	}

	/**
	 * �w�肵���t�H�[�}�b�g�Řa��̕�����ɕϊ�����B
	 *
	 * @param pattern
	 * @return ���t��\���a��̕�����
	 */
	public String formatInJapanese(String pattern) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return format(pattern, JAPANESE_LOCALE);
	}

	/**
	 * �w�肵��Locale�̃t�H�[�}�b�g�œ����𕶎���ɕϊ�����B
	 *
	 * @param pattern
	 * @param locale
	 * @return ���t��\��������
	 */
	public String format(String pattern, Locale locale) {
		if (!isDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		String result = formatter.format(calendar.getTime());
		return result;
	}

	/**
	 * �w�肵�������������t��i�߂��C���X�^���X���쐬����B<br>
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param day
	 * @return ���t���ړ������I�u�W�F�N�g
	 */
	public CalendarDate moveByDay(int day) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.DAY_OF_MONTH, day);
		return new CalendarDate(newCalendar);
	}

	/**
	 * �w�肵�������������t��i�߂��C���X�^���X���쐬����B
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param month
	 * @return ���t���ړ������I�u�W�F�N�g
	 */
	public CalendarDate moveByMonth(int month) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MONTH, month);
		return new CalendarDate(newCalendar);
	}

	/**
	 * �w�肵���N���������t��i�߂��C���X�^���X���쐬����B<br>
	 * �����ɕ��̒l���w�肵���ꍇ�A�ߋ��̓��t�ɖ߂�B
	 *
	 * @param year
	 * @return ���t���ړ������I�u�W�F�N�g
	 */
	public CalendarDate moveByYear(int year) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.YEAR, year);
		return new CalendarDate(newCalendar);
	}

	/**
	 * �����̓��t���牽���ォ���擾����B<br>
	 * �������t�̏ꍇ��0�A�ߋ��̏ꍇ�}�C�i�X�̒l��Ԃ��B<br>
	 * �[�b�ɂ��덷���������邩�ǂ�����Java�̎����ˑ��B
	 *
	 * @param date
	 * @return �����̓��t���琔��������
	 */
	public int countDaysFrom(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		long day = (getTimeInMillis() - date.getTimeInMillis()) / MILLISECOND_IN_A_DAY;
		return (int) day;
	}

	/**
	 * �y�������肷��
	 *
	 * @return �y���̏ꍇ<code>true</code>
	 */
	public boolean isWeekHoliday() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		int dayOfWeek = getDayOfWeek();
		return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
	}

	/**
	 * �j���𐔒l�Ŏ擾����B
	 * �iex. 1:���A2:���A3:�΁A4:���A5:�؁A6:���A7:�y�j
	 *
	 * @return �j����\�����l
	 */
	public int getDayOfWeek() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * ���̍ŏI�����擾����B�iex. 1��:31�A2��:28�j
	 *
	 * @return ���̍ŏI��
	 */
	public int getMaxDayOfMonth() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * �j������{��Ŏ擾����B<br>
	 * �i���A���A�΁A���A�؁A���A�y�j
	 *
	 * @return ���{��̗j��
	 */
	public String getYoubi() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}

		int dayOfWeek = getDayOfWeek();

		switch (dayOfWeek) {
		case SUNDAY:
			return "��";
		case MONDAY:
			return "��";
		case TUESDAY:
			return "��";
		case WEDNESDAY:
			return "��";
		case THURSDAY:
			return "��";
		case FRIDAY:
			return "��";
		case SATURDAY:
			return "�y";
		default:
			throw new IllegalStateException("Invalid code of week day.This may be framework bug!!");// �N����Ȃ��͂�
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
		//TODO �d�l���l����
		calendar.setLenient(lenient);
	}

	/**
	 * Date�C���X�^���X���Z�b�g����B
	 *
	 * @param date
	 */
	public void setDate(Date date) {
		calendar.setTime(date);
		japaneseCalendar.setTime(date);
		clearTime();
		reflectFields();
	}

	/**
	 * Calendar�C���X�^���X���Z�b�g����B
	 *
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		setDate(calendar.getTime());
	}

	/**
	 * Timestamp�C���X�^���X���Z�b�g����B
	 *
	 * @param timestamp
	 */
	public void setTimestamp(Timestamp timestamp) {
		setTimeInMillis(timestamp.getTime());
	}

	/**
	 * �G�|�b�N������̃~���b���Z�b�g����B
	 *
	 * @param millis
	 */
	public void setTimeInMillis(long millis) {
		calendar.setTimeInMillis(millis);
		japaneseCalendar.setTimeInMillis(millis);
		clearTime();
		reflectFields();
	}

	/**
	 * �N���Z�b�g����B
	 *
	 * @param year
	 */
	public void setYear(int year) {
		if (year <= 0 && year != UNDEFINED) {
			//Exception��throw����ׂ����Y�ނ��A�ėp�����u�����ė�O�Ƃ݂Ȃ��Ȃ����Ƃɂ���
			return;
		}
		this.year = year;
		if (year != UNDEFINED) {
			calendar.set(Calendar.YEAR, year);
			japaneseCalendar.setTimeInMillis(calendar.getTimeInMillis());
			japaneseNenGo = japaneseCalendar.get(Calendar.ERA);
			japaneseYear = japaneseCalendar.get(Calendar.YEAR);
		} else {
			japaneseNenGo = UNDEFINED;
			japaneseYear = UNDEFINED;
		}
	}

	/**
	 * �����Z�b�g����B
	 *
	 * @param month
	 */
	public void setMonth(int month) {
		if (month <= 0 && month != UNDEFINED) {
			return;
		}
		this.month = month;
		calendar.set(Calendar.MONTH, month - 1);
		japaneseCalendar.setTimeInMillis(calendar.getTimeInMillis());
	}

	/**
	 * �����Z�b�g����B
	 *
	 * @param day
	 */
	public void setDay(int day) {
		if (day <= 0 && day != UNDEFINED) {
			return;
		}
		this.day = day;
		calendar.set(Calendar.DAY_OF_MONTH, day);
		japaneseCalendar.setTimeInMillis(calendar.getTimeInMillis());
	}

	/**
	 * ���{�̔N�����Z�b�g����B
	 *
	 * @param nengo
	 */
	public void setJapaneseNenGo(int nengo) {
		if (nengo <= 0 && nengo != UNDEFINED) {
			return;
		}
		this.japaneseNenGo = nengo;
		japaneseCalendar.set(Calendar.ERA, nengo);
		calendar.setTimeInMillis(japaneseCalendar.getTimeInMillis());

		if (0 < this.japaneseNenGo && 0 < this.japaneseYear) {
			year = calendar.get(Calendar.YEAR);
		} else {
			year = UNDEFINED;
		}
	}

	/**
	 * �a��̔N���Z�b�g����B
	 *
	 * @param jpYear
	 */
	public void setJapaneseYear(int jpYear) {
		if (jpYear <= 0 && jpYear != UNDEFINED) {
			return;
		}
		this.japaneseYear = jpYear;

		japaneseCalendar.set(Calendar.YEAR, jpYear);
		calendar.setTimeInMillis(japaneseCalendar.getTimeInMillis());

		if (0 < this.japaneseNenGo && 0 < this.japaneseYear) {
			year = calendar.get(Calendar.YEAR);
		} else {
			year = UNDEFINED;
		}
	}

	/**
	 * �N���擾����B
	 * @return ����̔N
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * �����擾����B
	 * @return ��
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * �����擾����B
	 * @return ��
	 */
	public int getDay() {
		return this.day;
	}

	/**
	 * �a��̔N���R�[�h��Ԃ��B
	 *
	 * @return �a���̔N����\�����l
	 */
	public int getJapaneseNenGo() {
		return this.japaneseNenGo;
	}

	/**
	 * �a��̔N��Ԃ��B�iex.���a56�N�̏ꍇ 56�A����20�N�̏ꍇ 20�j
	 *
	 * @return �a��̔N
	 */
	public int getJapaneseYear() {
		return this.japaneseYear;
	}

	/**
	 * �a��̔N���𕶎���ŕԂ��B�iex."���a", "����"�j
	 *
	 * @return �a��̔N��
	 */
	public String getJapaneseNenGoLabel() {
		if (0 < japaneseNenGo) {
			SimpleDateFormat formatter = new SimpleDateFormat("GGGG", JAPANESE_LOCALE);
			return formatter.format(calendar.getTime());
		} else {
			return null;
		}
	}

	/**
	 * ���̃I�u�W�F�N�g�Ɠ�������\��Calendar�C���X�^���X���擾����B
	 * @return ���̃I�u�W�F�N�g�Ɠ�������\��Calendar�C���X�^���X
	 */
	public Calendar getCalendar() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return (Calendar) calendar.clone();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ɠ�������\��Date�C���X�^���X���擾����B
	 * @return ���̃I�u�W�F�N�g�Ɠ�������\��Date�C���X�^���X
	 */
	public Date getDate() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		// Calendar#getTime()�łƂ��Date��Calendar�ƕR�t���Ȃ��̂ŁAclone���Ȃ��Ă����v
		return calendar.getTime();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ɠ�����\��java.sql.Date�C���X�^���X���擾����B
	 *
	 * @return ���̃I�u�W�F�N�g�Ɠ�����\��java.sql.Date�C���X�^���X
	 */
	public java.sql.Date getSQLDate() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return new java.sql.Date(getTimeInMillis());
	}

	/**
	 * ���̃I�u�W�F�N�g�Ɠ�������\��Timestamp�C���X�^���X���擾����B
	 * @return ���̃I�u�W�F�N�g�Ɠ�������\��Timestamp�C���X�^���X
	 */
	public Timestamp getTimestamp() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return new Timestamp(getTimeInMillis());
	}

	/**
	 * ���t�����̒l���~���b�Ŏ擾����B
	 * @return ���̃I�u�W�F�N�g�Ɠ�������\���~���b
	 */
	public long getTimeInMillis() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.getTimeInMillis();
	}

	/**
	 * calendar�̓��t�����Ɋe�t�B�[���h�ɒl���Z�b�g����
	 */
	private void reflectFields() {
		reflectDefaultFields();
		reflectJapaneseFields();
	}

	private void reflectDefaultFields() {
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}

	private void reflectJapaneseFields() {
		japaneseNenGo = japaneseCalendar.get(Calendar.ERA);
		japaneseYear = japaneseCalendar.get(Calendar.YEAR);
	}

	/**
	 * �������N���A����
	 */
	private void clearTime() {
		clearTime(calendar);
		clearTime(japaneseCalendar);
	}

	/**
	 * �������N���A����
	 */
	private static void clearTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/*
	 * yyyy/MM/dd �̃t�H�[�}�b�g�̕������Ԃ��B
	 *
	 * (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (isDefined()) {
			return format("yyyy/MM/dd") + ", " + formatInJapanese("GGGG yy�NMM��dd��");
		} else {
			StringBuilder sb = new StringBuilder(30);

			if (0 < year) {
				sb.append(padLeftZero(year, 4));
			} else {
				sb.append("????");
			}

			sb.append("/");

			String monthStr = null;
			if (0 < month) {
				monthStr = padLeftZero(month, 2);
			} else {
				monthStr = "??";
			}

			sb.append(monthStr);
			sb.append("/");

			String dayStr = null;
			if (0 < day) {
				dayStr = padLeftZero(day, 2);
			} else {
				dayStr = "??";
			}

			sb.append(dayStr);
			sb.append(", ");

			if (0 < japaneseNenGo) {
				sb.append(getJapaneseNenGoLabel());
			} else {
				sb.append("�H�H");
			}

			sb.append(" ");

			if (0 < japaneseYear) {
				sb.append(padLeftZero(japaneseYear, 2));
			} else {
				sb.append("??");
			}

			sb.append("�N");
			sb.append(monthStr + "��");
			sb.append(dayStr + "��");

			return sb.toString();
		}
	}

	/*
	 * ���̃C���X�^���X�̃R�s�[���쐬����
	 *
	 * (�� Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CalendarDate clone() {
		CalendarDate clone = null;

		try {
			clone = (CalendarDate) super.clone();
			clone.calendar = (Calendar) calendar.clone();
			clone.japaneseCalendar = (Calendar) japaneseCalendar.clone();
		} catch (CloneNotSupportedException e) {
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}

		return clone;
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + japaneseNenGo;
		result = prime * result + japaneseYear;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarDate other = (CalendarDate) obj;
		if (day != other.day)
			return false;
		if (japaneseNenGo != other.japaneseNenGo)
			return false;
		if (japaneseYear != other.japaneseYear)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	/**
	 * ���l�̍���0�����Ďw��̒����̕�����ɂ���
	 *
	 * @param num
	 * @param size
	 * @return
	 */
	private static String padLeftZero(int num, int size) {
		String source = String.valueOf(num);
		int srcLen = source.length();
		StringBuilder sb = new StringBuilder(size);

		for (int i = 0; i < size - srcLen; i++) {
			sb.append('0');
		}

		sb.append(source);
		return sb.toString();
	}

}
