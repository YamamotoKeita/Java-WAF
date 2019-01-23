package jp.co.altonotes.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 特定の日付を表す。<br>
 * Date, Calendar, SimpleDateFormatなどの機能を持つ。
 *
 * @author Yamamoto Keita
 *
 */
public class CalendarDate implements Cloneable, Serializable {

	private static final long serialVersionUID = -519632160912964120L;

	private static final long MILLISECOND_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final Locale JAPANESE_LOCALE = new Locale("ja", "JP", "JP");
	private static final String UNDEFINED_ERROR_MESSAGE = "年月日が全てセットされていません：";

	/** 日曜日*/
	public static final int SUNDAY = 1;
	/** 月曜日*/
	public static final int MONDAY = 2;
	/** 火曜日*/
	public static final int TUESDAY = 3;
	/** 水曜日*/
	public static final int WEDNESDAY = 4;
	/** 木曜日*/
	public static final int THURSDAY = 5;
	/** 金曜日*/
	public static final int FRIDAY = 6;
	/** 土曜日*/
	public static final int SATURDAY = 7;

	/** 明治	*/
	public static final int MEIJI = 1;
	/** 大正	*/
	public static final int TAISHO = 2;
	/** 昭和	*/
	public static final int SHOWA = 3;
	/** 平成	*/
	public static final int HEISEI = 4;

	/** 年や月や日が設定されていないことを表す数値	*/
	public static final int UNDEFINED = -1;

	private Calendar calendar = Calendar.getInstance();
	private Calendar japaneseCalendar = Calendar.getInstance(JAPANESE_LOCALE);

	private int japaneseNenGo = UNDEFINED;
	private int japaneseYear = UNDEFINED;
	private int year = UNDEFINED;
	private int month = UNDEFINED;
	private int day = UNDEFINED;

	/**
	 * コンストラクター。
	 */
	public CalendarDate() {
		clearTime();
	}

	/**
	 * コンストラクター
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
	 * コンストラクター
	 *
	 * @param date
	 */
	public CalendarDate(Date date) {
		setDate(date);
	}

	/**
	 * コンストラクター
	 *
	 * @param calendar
	 */
	public CalendarDate(Calendar calendar) {
		setCalendar(calendar);
	}

	/**
	 * コンストラクター
	 *
	 * @param timestamp
	 */
	public CalendarDate(Timestamp timestamp) {
		setTimestamp(timestamp);
	}

	/**
	 * コンストラクター
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
	 * コンストラクター
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
	 * コンストラクター
	 *
	 * @param millis
	 */
	public CalendarDate(long millis) {
		setTimeInMillis(millis);
	}

	/**
	 * 今日を表すインスタンスを取得する。
	 *
	 * @return 今日を表すインスタンス
	 */
	public static CalendarDate today() {
		return new CalendarDate(new Date());
	}

	/**
	 * 年、月、日を指定してインスタンスを作成する
	 *
	 * @param year
	 * @param month
	 * @param day
	 * @return 年、月、日を指定して作成したインスタンス
	 */
	public static CalendarDate create(int year, int month, int day) {
		return new CalendarDate(year, month, day);
	}

	/**
	 * 年号、日本皇紀での年、月、日を指定してインスタンスを作成する
	 *
	 * @param nengo
	 * @param japaneseYear
	 * @param month
	 * @param day
	 * @return 作成したインスタンス
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
	 * 年、月、日が全て設定されているか判定する
	 *
	 * @return 年、月、日が全て設定された状態の場合<code>true</code>
	 */
	public boolean isDefined() {
		return (0 < year) && (0 < month) && (0 < day);
	}

	/**
	 * 年、月、日が全て設定されていないか判定する
	 *
	 * @return 年、月、日が全て設定されていない場合<code>true</code>
	 */
	public boolean isUnDefined() {
		return !isDefined();
	}

	/**
	 * このインスタンスが引数の日付より前の日付か判定する。
	 *
	 * @param date
	 * @return このインスタンスが引数の日付より前の日付を表す場合 <code>true</code>
	 */
	public boolean before(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.before(date.getCalendar());
	}

	/**
	 * このインスタンスが引数の日付以前か判定する。
	 * @param date
	 * @return 引数の日付以前の場合 <code>true</code>
	 */
	public boolean beforeOrEquals(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return !calendar.after(date.getCalendar());
	}

	/**
	 * このインスタンスが引数の日付より後の日付か判定する。
	 *
	 * @param date
	 * @return このインスタンスが引数の日付より後の日付を表す場合 <code>true</code>
	 */
	public boolean after(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.after(date.getCalendar());
	}

	/**
	 * このインスタンスが引数の日付以後か判定する。
	 *
	 * @param date
	 * @return このインスタンスが引数の日付以後の場合 <code>true</code>
	 */
	public boolean afterOrEquals(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return !calendar.before(date.getCalendar());
	}

	/**
	 * 指定したフォーマットで文字列に変換する。
	 *
	 * @param pattern
	 * @return 日付を表す文字列
	 */
	public String format(String pattern) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}

		return format(pattern, Locale.US);
	}

	/**
	 * 指定したフォーマットで和暦の文字列に変換する。
	 *
	 * @param pattern
	 * @return 日付を表す和暦の文字列
	 */
	public String formatInJapanese(String pattern) {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return format(pattern, JAPANESE_LOCALE);
	}

	/**
	 * 指定したLocaleのフォーマットで日時を文字列に変換する。
	 *
	 * @param pattern
	 * @param locale
	 * @return 日付を表す文字列
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
	 * 指定した日数だけ日付を進めたインスタンスを作成する。<br>
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param day
	 * @return 日付を移動したオブジェクト
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
	 * 指定した月数だけ日付を進めたインスタンスを作成する。
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param month
	 * @return 日付を移動したオブジェクト
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
	 * 指定した年数だけ日付を進めたインスタンスを作成する。<br>
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param year
	 * @return 日付を移動したオブジェクト
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
	 * 引数の日付から何日後かを取得する。<br>
	 * 同じ日付の場合は0、過去の場合マイナスの値を返す。<br>
	 * 閏秒による誤差が発生するかどうかはJavaの実装依存。
	 *
	 * @param date
	 * @return 引数の日付から数えた日数
	 */
	public int countDaysFrom(CalendarDate date) {
		if (isUnDefined() || date.isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		long day = (getTimeInMillis() - date.getTimeInMillis()) / MILLISECOND_IN_A_DAY;
		return (int) day;
	}

	/**
	 * 土日か判定する
	 *
	 * @return 土日の場合<code>true</code>
	 */
	public boolean isWeekHoliday() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		int dayOfWeek = getDayOfWeek();
		return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
	}

	/**
	 * 曜日を数値で取得する。
	 * （ex. 1:日、2:月、3:火、4:水、5:木、6:金、7:土）
	 *
	 * @return 曜日を表す数値
	 */
	public int getDayOfWeek() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 月の最終日を取得する。（ex. 1月:31、2月:28）
	 *
	 * @return 月の最終日
	 */
	public int getMaxDayOfMonth() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 曜日を日本語で取得する。<br>
	 * （日、月、火、水、木、金、土）
	 *
	 * @return 日本語の曜日
	 */
	public String getYoubi() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}

		int dayOfWeek = getDayOfWeek();

		switch (dayOfWeek) {
		case SUNDAY:
			return "日";
		case MONDAY:
			return "月";
		case TUESDAY:
			return "火";
		case WEDNESDAY:
			return "水";
		case THURSDAY:
			return "木";
		case FRIDAY:
			return "金";
		case SATURDAY:
			return "土";
		default:
			throw new IllegalStateException("Invalid code of week day.This may be framework bug!!");// 起こらないはず
		}
	}

	/**
	 * 存在しない日付を適当な日付に自動変換するかを設定する。
	 * trueの場合自動変換する。（ex. 2010/01/32 →2010/02/01）
	 * デフォルトはtrue。
	 *
	 * @param lenient
	 */
	public void setLenient(boolean lenient) {
		//TODO 仕様を考える
		calendar.setLenient(lenient);
	}

	/**
	 * Dateインスタンスをセットする。
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
	 * Calendarインスタンスをセットする。
	 *
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		setDate(calendar.getTime());
	}

	/**
	 * Timestampインスタンスをセットする。
	 *
	 * @param timestamp
	 */
	public void setTimestamp(Timestamp timestamp) {
		setTimeInMillis(timestamp.getTime());
	}

	/**
	 * エポック時からのミリ秒をセットする。
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
	 * 年をセットする。
	 *
	 * @param year
	 */
	public void setYear(int year) {
		if (year <= 0 && year != UNDEFINED) {
			//Exceptionをthrowするべきか悩むが、汎用性を志向して例外とみなさないことにする
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
	 * 月をセットする。
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
	 * 日をセットする。
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
	 * 日本の年号をセットする。
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
	 * 和暦の年をセットする。
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
	 * 年を取得する。
	 * @return 西暦の年
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * 月を取得する。
	 * @return 月
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * 日を取得する。
	 * @return 日
	 */
	public int getDay() {
		return this.day;
	}

	/**
	 * 和暦の年号コードを返す。
	 *
	 * @return 和歴の年号を表す数値
	 */
	public int getJapaneseNenGo() {
		return this.japaneseNenGo;
	}

	/**
	 * 和暦の年を返す。（ex.昭和56年の場合 56、平成20年の場合 20）
	 *
	 * @return 和暦の年
	 */
	public int getJapaneseYear() {
		return this.japaneseYear;
	}

	/**
	 * 和暦の年号を文字列で返す。（ex."昭和", "平成"）
	 *
	 * @return 和暦の年号
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
	 * このオブジェクトと同日時を表すCalendarインスタンスを取得する。
	 * @return このオブジェクトと同日時を表すCalendarインスタンス
	 */
	public Calendar getCalendar() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return (Calendar) calendar.clone();
	}

	/**
	 * このオブジェクトと同日時を表すDateインスタンスを取得する。
	 * @return このオブジェクトと同日時を表すDateインスタンス
	 */
	public Date getDate() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		// Calendar#getTime()でとれるDateはCalendarと紐付かないので、cloneしなくても大丈夫
		return calendar.getTime();
	}

	/**
	 * このオブジェクトと同日を表すjava.sql.Dateインスタンスを取得する。
	 *
	 * @return このオブジェクトと同日を表すjava.sql.Dateインスタンス
	 */
	public java.sql.Date getSQLDate() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return new java.sql.Date(getTimeInMillis());
	}

	/**
	 * このオブジェクトと同日時を表すTimestampインスタンスを取得する。
	 * @return このオブジェクトと同日時を表すTimestampインスタンス
	 */
	public Timestamp getTimestamp() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return new Timestamp(getTimeInMillis());
	}

	/**
	 * 日付時刻の値をミリ秒で取得する。
	 * @return このオブジェクトと同日時を表すミリ秒
	 */
	public long getTimeInMillis() {
		if (isUnDefined()) {
			throw new IllegalStateException(UNDEFINED_ERROR_MESSAGE + toString());
		}
		return calendar.getTimeInMillis();
	}

	/**
	 * calendarの日付を元に各フィールドに値をセットする
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
	 * 時刻をクリアする
	 */
	private void clearTime() {
		clearTime(calendar);
		clearTime(japaneseCalendar);
	}

	/**
	 * 時刻をクリアする
	 */
	private static void clearTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/*
	 * yyyy/MM/dd のフォーマットの文字列を返す。
	 *
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (isDefined()) {
			return format("yyyy/MM/dd") + ", " + formatInJapanese("GGGG yy年MM月dd日");
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
				sb.append("？？");
			}

			sb.append(" ");

			if (0 < japaneseYear) {
				sb.append(padLeftZero(japaneseYear, 2));
			} else {
				sb.append("??");
			}

			sb.append("年");
			sb.append(monthStr + "月");
			sb.append(dayStr + "日");

			return sb.toString();
		}
	}

	/*
	 * このインスタンスのコピーを作成する
	 *
	 * (非 Javadoc)
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

	/* (非 Javadoc)
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

	/* (非 Javadoc)
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
	 * 数値の左に0をつけて指定の長さの文字列にする
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
