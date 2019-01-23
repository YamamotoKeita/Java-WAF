package jp.co.altonotes.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 特定の日時を表すクラス。
 * Date, Calendar, SimpleDateFormatなどの機能を統合している。
 *
 * @author Yamamoto Keita
 *
 */
public class DateTime implements Serializable, Cloneable {

	private static final long serialVersionUID = 1711840884381779802L;

	/** 日曜日を表す定数 */
	public static final int SUNDAY = 1;
	/** 月曜日を表す定数 */
	public static final int MONDAY = 2;
	/** 火曜日を表す定数 */
	public static final int TUESDAY = 3;
	/** 水曜日を表す定数 */
	public static final int WEDNESDAY = 4;
	/** 木曜日を表す定数 */
	public static final int THURSDAY = 5;
	/** 金曜日を表す定数 */
	public static final int FRIDAY = 6;
	/** 土曜日を表す定数 */
	public static final int SATURDAY = 7;

	private static final long MILLISECOND_IN_A_DAY = 1000 * 60 * 60 * 24;
	private static final long MILLISECOND_IN_AN_HOUR = 1000 * 60 * 60;
	private static final long MILLISECOND_IN_A_MINUTE = 1000 * 60;

	private Calendar calendar;
	
	/**
	 * コンストラクター
	 *
	 * @param date
	 */
	public DateTime(Date date) {
		calendar = Calendar.getInstance();
		calendar.setTime(date);
	}

	/**
	 * コンストラクター
	 *
	 * @param calendar
	 */
	public DateTime(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * コンストラクター
	 *
	 * @param timestamp
	 */
	public DateTime(Timestamp timestamp) {
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
	}

	/**
	 * コンストラクター
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
	 * コンストラクター
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
	 * コンストラクター
	 *
	 * @param l
	 */
	public DateTime(long l) {
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(l));
	}

	/**
	 * 現在時刻を表すDateTimeインスタンスを取得する。
	 *
	 * @return 現在時刻を表すインスタンス
	 */
	public static DateTime now() {
		return new DateTime(Calendar.getInstance());
	}

	/**
	 * 今日を表すDateTimeインスタンスを取得する。
	 * 時刻は00時00分00秒000ミリ秒に設定される。
	 *
	 * @return 今日を表すDateTimeインスタンス
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
	 * デフォルトの日付時刻を取得する。<br>
	 * 現在の実装では2000/01/01 00:00 00.000<br>
	 *
	 * このインスタンスの時刻に意味はなく、ただの初期値である。
	 * DateTime インスタンスには時刻未確定の状態が存在しないため適当な時刻を設定した。<br>
	 *
	 * setYear, setMonth, setDay などで時刻を設定することを前提に、まず初期インスタンスを取得する場合このメソッドを使う。
	 *
	 * @return デフォルトの日付時刻
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
	 * yyyyMMddのフォーマットで日付を指定してインスタンスを作成する。
	 *
	 * @param str
	 * @return 引数の文字列で表される日付
	 */
	public static DateTime date(String str) {
		return new DateTime("yyyyMMdd", str);
	}

	/**
	 * HH:mmのフォーマットで時刻を指定してインスタンスを作成する。
	 *
	 * @param str
	 * @return 引数の文字列で表される日付
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
	 * 引数に指定した日付より前の日付か判定する。
	 *
	 * @param dateTime
	 * @return この日付が引数に指定した日付より前の場合 <code>true</code>
	 */
	public boolean before(DateTime dateTime) {
		return calendar.before(dateTime.getCalendar());
	}

	/**
	 * 引数に指定した日付より後の日付か判定する。
	 * @param dateTime
	 * @return この日付が引数に指定した日付より後の場合 <code>true</code>
	 */
	public boolean after(DateTime dateTime) {
		return calendar.after(dateTime.getCalendar());
	}

	/**
	 * このインスタンスの持つ時刻が、引数の時刻より前か判定する。<br>
	 * 日付は無視して時刻のみ比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスの持つ時刻が、引数の時刻より前の場合 <code>true</code>
	 */
	public boolean beforeByTime(DateTime dateTime) {
		DateTime newThis = this.defaultYearDate();
		DateTime newArg = dateTime.defaultYearDate();
		return newThis.getCalendar().before(newArg.getCalendar());
	}

	/**
	 * このインスタンスの持つ時刻が、引数の時刻より後か判定する。<br>
	 * 日付は無視して時刻のみ比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスの持つ時刻が、引数の時刻より後の場合
	 */
	public boolean afterByTime(DateTime dateTime) {
		DateTime newThis = this.defaultYearDate();
		DateTime newArg = dateTime.defaultYearDate();
		return newThis.getCalendar().after(newArg.getCalendar());
	}

	/**
	 * このインスタンスが引数のDateTimeより前の日付を表す場合trueを返す。
	 * 時刻は無視して日付だけで比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスが引数のDateTimeより前の日付を表す場合true
	 */
	public boolean beforeByDate(DateTime dateTime) {
		DateTime newThis = this.minimumTimeOfDate();
		DateTime newArg = dateTime.minimumTimeOfDate();
		return newThis.getCalendar().before(newArg.getCalendar());
	}

	/**
	 * このインスタンスが引数のDateTimeより後の日付を表す場合trueを返す。
	 * 時刻は無視して日付だけで比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスが引数のDateTimeより後の日付を表す場合true
	 */
	public boolean afterByDate(DateTime dateTime) {
		DateTime newThis = this.minimumTimeOfDate();
		DateTime newArg = dateTime.minimumTimeOfDate();
		return newThis.getCalendar().after(newArg.getCalendar());
	}

	/**
	 * このインスタンスの年月日が引数のDateTimeと等しいか判定する。
	 * 時分秒ミリ秒を無視して日付だけで比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスの年月日が引数のDateTimeと等しい場合 true
	 */
	public boolean equalsByDate(DateTime dateTime) {
		return this.minimumTimeOfDate().equals(dateTime.minimumTimeOfDate());
	}

	/**
	 * このインスタンスの時刻が引数のDateTimeと等しいか判定する。
	 * 年月日を無視して時刻だけで比較する。
	 *
	 * @param dateTime
	 * @return このインスタンスの時刻が引数のDateTimeと等しい場合 true
	 */
	public boolean equalsByTime(DateTime dateTime) {
		return this.defaultYearDate().equals(dateTime.defaultYearDate());
	}

	/**
	 * 指定したフォーマットで文字列に変換する。
	 *
	 * @param pattern
	 * @return この日付を表す文字列
	 */
	public String format(String pattern) {
		return format(pattern, Locale.US);
	}

	/**
	 * 指定したフォーマットで和暦の文字列に変換する。
	 *
	 * @param pattern
	 * @return この日付を和歴で表す文字列
	 */
	public String formatInJapanese(String pattern) {
		return format(pattern, new Locale("ja", "JP", "JP"));
	}

	/**
	 * 指定したLocaleのフォーマットで日時を文字列に変換する。
	 *
	 * @param pattern
	 * @param locale
	 * @return この日付を表す文字列
	 */
	public String format(String pattern, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
		String result = formatter.format(calendar.getTime());
		return result;
	}
	
	/**
	 * 月の最初の日付を返す
	 * @return 月の最初の日付
	 */
	public DateTime firstDateOfMonth() {
		DateTime clone = this.clone();
		clone.setDay(1);
		return clone;
	}

	/**
	 * 指定したミリ秒、時刻を進めたインスタンスを取得する。<br>
	 * 引数に負の値を指定した場合、過去の時刻に戻る。
	 *
	 * @param milli
	 * @return 指定したミリ秒、時刻を進めたインスタンス
	 */
	public DateTime moveByMilliSecond(int milli) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MILLISECOND, milli);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した秒、時刻を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の時刻に戻る。
	 *
	 * @param second
	 * @return 指定した秒、時刻を進めたDateTimeインスタンス
	 */
	public DateTime moveBySecond(int second) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.SECOND, second);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した分、時刻を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の時刻に戻る。
	 *
	 * @param minute
	 * @return 指定した分、時刻を進めたDateTimeインスタンス
	 */
	public DateTime moveByMinute(int minute) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MINUTE, minute);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した時間、時刻を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の時刻に戻る。
	 *
	 * @param hour
	 * @return 指定した時間、時刻を進めたDateTimeインスタンス
	 */
	public DateTime moveByHour(int hour) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.HOUR, hour);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した日数、日付を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param day
	 * @return 日付を移動したDateTimeインスタンス
	 */
	public DateTime moveByDay(int day) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.DATE, day);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した月数、日付を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param month
	 * @return 日付を移動したDateTimeインスタンス
	 */
	public DateTime moveByMonth(int month) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.MONTH, month);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した年数、日付を進めたDateTimeインスタンスを取得する。
	 * 引数に負の値を指定した場合、過去の日付に戻る。
	 *
	 * @param year
	 * @return 日付を移動したDateTimeインスタンス
	 */
	public DateTime moveByYear(int year) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.YEAR, year);
		return new DateTime(newCalendar);
	}

	/**
	 * 指定した日付時刻から何ミリ秒後かを取得する。
	 * 同じ場合は0、過去の場合マイナスの値を返す。
	 *
	 * @param time
	 * @return 引数の日付時刻から、この日付時刻までに経過したミリ秒
	 */
	public int countMilliSecondsFrom(DateTime time) {
		long milliSecond = getTimeInMillis() - time.getTimeInMillis();
		return (int) milliSecond;
	}

	/**
	 * 指定した日付時刻から何秒後かを取得する。
	 * 同じ場合は0、過去の場合マイナスの値を返す。
	 *
	 * @param time
	 * @return 引数の日付時刻から、この日付時刻までに経過した秒
	 */
	public int countSecondsFrom(DateTime time) {
		long minute = (getTimeInMillis() - time.getTimeInMillis()) / 1000;
		return (int) minute;
	}

	/**
	 * 指定した日付時刻から何分後かを取得する。
	 * 同じ場合は0、過去の場合マイナスの値を返す。
	 *
	 * @param time
	 * @return 引数の日付時刻から、この日付時刻までに経過した分
	 */
	public int countMinutesFrom(DateTime time) {
		long minute = (getTimeInMillis() - time.getTimeInMillis()) / MILLISECOND_IN_A_MINUTE;
		return (int) minute;
	}

	/**
	 * 指定した日付時刻から何時間後かを取得する。
	 * 同じ場合は0、過去の場合マイナスの値を返す。
	 *
	 * @param time
	 * @return 引数の日付時刻から、この日付時刻までに経過した時間
	 */
	public int countHoursFrom(DateTime time) {
		long hour = (getTimeInMillis() - time.getTimeInMillis()) / MILLISECOND_IN_AN_HOUR;
		return (int) hour;
	}

	/**
	 * 指定した日付から何日後かを取得する。
	 * 同じ日の場合は0、過去の場合マイナスの値を返す。
	 * 閏秒による誤差が発生するかどうかはJavaの実装依存。
	 *
	 * @param date
	 * @return 引数の日付時刻から、この日付時刻までに経過した日数
	 */
	public int countDaysFrom(DateTime date) {
		DateTime date1 = this.minimumTimeOfDate();
		DateTime date2 = date.minimumTimeOfDate();
		long day = (date1.getTimeInMillis() - date2.getTimeInMillis()) / MILLISECOND_IN_A_DAY;
		return (int) day;
	}

	/**
	 * 和暦の年号を返す。（ex."昭和", "平成"）
	 *
	 * @return 和暦の年号
	 */
	public String getJapaneseNenGo() {
		Locale locale = new Locale("ja", "JP", "JP");
		String nenGo = new SimpleDateFormat("GGGG", locale).format(calendar.getTime());
		return nenGo;
	}

	/**
	 * 和暦の年を返す。（ex.昭和56年の場合 56、平成20年の場合 20）
	 *
	 * @return 和暦の年
	 */
	public int getJapaneseYear() {
		Calendar cal = Calendar.getInstance(new Locale("ja", "JP", "JP"));
		cal.setTime(calendar.getTime());
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 時刻を0時0分0秒0ミリ秒にセットした新しいインスタンスを作成する。
	 *
	 * @return 時刻を0時0分0秒0ミリ秒にセットした新しいインスタンス
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
	 * 時刻を23時59分59秒999ミリ秒にセットした新しいインスタンスを作成する。
	 *
	 * @return 時刻を23時59分59秒999ミリ秒にセットした新しいインスタンス
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
	 * この日付が土日か判定する。
	 *
	 * @return この日付が土日の場合 <code>true</code>
	 */
	public boolean isWeekHoliday() {
		int dayOfWeek = getDayOfWeek();
		return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
	}

	/**
	 * 年を初期値にし、日付と時刻だけを保持した新しいインスタンスを作成する。
	 *
	 * @return 日付と時刻だけを保持した新しいインスタンス
	 */
	public DateTime defaultYear() {
		DateTime newTime = this.clone();
		//きりがいいからデフォルト2000年にしとく。もっといいのがあればそれで。
		newTime.setYear(2000);
		return newTime;
	}

	/**
	 * 年と日付を初期値にし、時刻だけを保持した新しいインスタンスを作成する。
	 *
	 * @return 時刻だけを保持した新しいインスタンス
	 */
	public DateTime defaultYearDate() {
		DateTime newTime = this.clone();
		//きりがいいからデフォルト2000年にしとく。もっといいのがあればそれで。
		newTime.setYear(2000);
		newTime.setMonth(1);
		newTime.setDay(1);
		return newTime;
	}

	/**
	 * 曜日を数値で取得する。
	 * （ex. 1:日、2:月、3:火、4:水、5:木、6:金、7:土）
	 *
	 * @return 曜日を表す数値
	 */
	public int getDayOfWeek() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 月の最終日を取得する。（ex. 1月:31、2月:28）
	 *
	 * @return 月の最終日
	 */
	public int getMaxDayOfMonth() {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 月の最終日を取得する。（ex. 1月:31、2月:28）
	 *
	 * @return 月の最終日
	 */
	public DateTime getLastDateOfMonth() {
		int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.set(Calendar.DAY_OF_MONTH, maximum);
		return new DateTime(newCalendar);
	}

	/**
	 * 曜日を日本語で取得する。
	 *
	 * @return 曜日を表す文字列
	 */
	public String getYoubi() {
		int dayOfWeek = getDayOfWeek();
		switch (dayOfWeek) {
		case 1:
			return "日";
		case 2:
			return "月";
		case 3:
			return "火";
		case 4:
			return "水";
		case 5:
			return "木";
		case 6:
			return "金";
		case 7:
			return "土";
		default:
			return null;
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
		calendar.setLenient(lenient);
	}

	/**
	 * Calendarインスタンスをセットする。
	 *
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * Dateインスタンスをセットする。
	 * @param date
	 */
	public void setDate(Date date) {
		calendar.setTime(date);
	}

	/**
	 * 年をセットする。
	 * @param year
	 */
	public void setYear(int year) {
		calendar.set(Calendar.YEAR, year);
	}

	/**
	 * 月をセットする。
	 * @param month
	 */
	public void setMonth(int month) {
		calendar.set(Calendar.MONTH, month - 1);
	}

	/**
	 * 日をセットする。
	 * @param day
	 */
	public void setDay(int day) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * 時をセットする。
	 * （0～24で設定可能）
	 * @param hour
	 */
	public void setHour(int hour) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
	}

	/**
	 * 分をセットする。
	 * @param minute
	 */
	public void setMinute(int minute) {
		calendar.set(Calendar.MINUTE, minute);
	}

	/**
	 * 秒をセットする。
	 * @param second
	 */
	public void setSecond(int second) {
		calendar.set(Calendar.SECOND, second);
	}

	/**
	 * ミリ秒をセットする。
	 * @param milliSecond
	 */
	public void setMilliSecond(int milliSecond) {
		calendar.set(Calendar.MILLISECOND, milliSecond);
	}

	/**
	 * 年を取得する。
	 * @return 年
	 */
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 月を取得する。
	 * @return 月
	 */
	public int getMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 日を取得する。
	 * @return 日
	 */
	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 時を取得する。（0～24時）
	 * @return 時
	 */
	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 分を取得する。
	 * @return 分
	 */
	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 秒を取得する。
	 * @return 秒
	 */
	public int getSecond() {
		return calendar.get(Calendar.SECOND);
	}

	/**
	 * ミリ秒を取得する。
	 * @return ミリ秒
	 */
	public int getMilliSecond() {
		return calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * このインスタンスと同日時を表すCalendarインスタンスを取得する。
	 * @return このインスタンスと同日時を表すCalendarインスタンス
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * このインスタンスと同日時を表すDateインスタンスを取得する。
	 * @return このインスタンスと同日時を表すDateインスタンス
	 */
	public Date getDate() {
		return calendar.getTime();
	}

	/**
	 * このインスタンスと同日を表すjava.sql.Dateインスタンスを取得する。
	 * @return このインスタンスと同日を表すjava.sql.Dateインスタンス
	 */
	public java.sql.Date getSQLDate() {
		return new java.sql.Date(minimumTimeOfDate().getTimeInMillis());
	}

	/**
	 * このインスタンスと同日時を表すTimestampインスタンスを取得する。
	 * @return このインスタンスと同日時を表すTimestampインスタンス
	 */
	public Timestamp getTimestamp() {
		return new Timestamp(getTimeInMillis());
	}

	/**
	 * 日付時刻の値をミリ秒で取得する。
	 * @return 日付時刻を表すミリ秒
	 */
	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	/**
	 * 文字列が正しい日付かチェックする
	 * @param str
	 * @param format
	 * @return 正しい日付の場合<code>true</code>
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
	 * yyyy/MM/dd HH:mm ss.SSS のフォーマットの文字列を返す。
	 *
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return format("yyyy/MM/dd HH:mm ss.SSS");
	}

	/*
	 * (非 Javadoc)
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
	 * (非 Javadoc)
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
	 * 指定したDateTimeがこのインスタンスと同じ日時を表す場合trueを返す。
	 *
	 * (非 Javadoc)
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
