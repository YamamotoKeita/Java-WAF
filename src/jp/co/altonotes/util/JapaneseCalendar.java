package jp.co.altonotes.util;

import jp.co.altonotes.model.DateTime;

/**
 * 2007年以降の法制度に従い、日本の祝日、休日を判定するクラス。
 * 2006年以前は「みどりの日」の日付が異なるため対応していない。
 *
 * @author Yamamoto Keita
 *
 */
public class JapaneseCalendar {

	/**
	 * このクラスで処理可能な最小の年度。<br>
	 * この年より前は法制度が異なるため対応していない。
	 */
	public static final int MINIMUM_YEAR = 2007;
	
	/**
	 * 指定した日付が休日か判定する。
	 * 休日は土日、祝日、振替休日を全て含む。
	 *
	 * @param date
	 * @return 引数の日付が休日の場合<code>true</code>
	 */
	public static boolean isHoliday(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "年より前には対応していません。");
		}

		//土日判定
		if (date.isWeekHoliday()) {
			return true;
		}

		//祭日判定
		if (isHighDay(date)) {
			return true;
		}

		//振り替え休日、国民の休日判定
		if (isActingHoliday(date)) {
			return true;
		}

		return false;
	}

	/**
	 * 引数の日付が株式市場の休日か判定する。
	 *
	 * @param date
	 * @return 引数の日付が株式市場の休日の場合<code>true</code>
	 */
	public static boolean isMarketHoliday(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "年より前には対応していません。");
		}

		//土日判定
		if (date.isWeekHoliday()) {
			return true;
		}

		//祭日判定
		if (isHighDay(date)) {
			return true;
		}

		//正月判定
		if (isMarketShogatsu(date)) {
			return true;
		}

		//振り替え休日判定
		if (isActingHoliday(date)) {
			return true;
		}

		return false;
	}

	/**
	 * 指定した日付が株式市場の正月休みか判定する。
	 *
	 * @param date
	 * @return 引数の日付が正月休みの場合<code>true</code>
	 */
	public static boolean isMarketShogatsu(DateTime date) {
		//12月31日～1月3日
		int month = date.getMonth();
		if (month == 12) {
			return date.getDay() == 31;
		} else if (month == 1) {
			int day = date.getDay();
			return day == 1 || day == 2 || day == 3;
		}
		return false;
	}

	/**
	 * 指定した日付が祭日か判定する。
	 * 振替休日は祭日に該当しない。
	 *
	 * @param date
	 * @return 引数の日付が祭日の場合<code>true</code>
	 */
	public static boolean isHighDay(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "年より前には対応していません。");
		}

		//固定祝日判定
		if (isFixedHoliday(date)) {
			return true;
		}

		//ハッピーマンデー判定
		if (isHappyMonday(date)) {
			return true;
		}

		//春分の日、秋分の日判定
		if (isShunbunNoHi(date) || isShuubunNoHi(date)) {
			return true;
		}

		return false;
	}
	
	/**
	 * 休日の名前を取得する
	 * @param date
	 * @return 休日の名前
	 */
	public static String getHolidayName(DateTime date) {
		FixedHoliday fixedHoliday = getFixedHoliday(date);
		if (fixedHoliday != null) {
			return fixedHoliday.getName();
		}
		
		HappyMonday happyMonday = getHappyMonday(date);
		if (happyMonday != null) {
			return happyMonday.getName();
		}
		
		if (isShunbunNoHi(date)) {
			return "春分の日";
		}
		if (isShuubunNoHi(date)) {
			return "秋分の日";
		}

		//振り替え休日判定
		if (isActingHoliday(date)) {
			return "振替休日";
		}

		return null;
	}

	/**
	 * 指定した日付が固定祝日か判定する。
	 *
	 * @param date
	 * @return 引数の日付が固定祝日の場合<code>true</code>
	 */
	public static boolean isFixedHoliday(DateTime date) {
		return getFixedHoliday(date) != null;
	}
	
	public static FixedHoliday getFixedHoliday(DateTime date) {
		int month = date.getMonth();
		//固定祝日判定
		FixedHoliday[] fixedHolidaies = FixedHoliday.values();
		for (FixedHoliday fixedHoliday : fixedHolidaies) {
			if (fixedHoliday.getMonth() == month) {	//高速化のため、先に月でふるいがけ
				DateTime holiday = fixedHoliday.getDateTime(date.getYear());
				if (date.equalsByDate(holiday)) {
					return fixedHoliday;
				}
			}
		}
		return null;
	}

	/**
	 * 指定した日付がハッピーマンデーか判定する。
	 *
	 * @param date
	 * @return 引数の日付がハッピーマンデーの場合<code>true</code>
	 */
	public static boolean isHappyMonday(DateTime date) {
		return getHappyMonday(date) != null;
	}

	private static HappyMonday getHappyMonday(DateTime date) {
		int month = date.getMonth();
		HappyMonday[] happyMondaies = HappyMonday.values();
		for (HappyMonday happyMonday : happyMondaies) {
			if (happyMonday.getMonth() == month) {	//高速化のため、先に月でふるいがけ
				DateTime holiday = happyMonday.getDateTime(date.getYear());
				if (date.equalsByDate(holiday)) {
					return happyMonday;
				}
			}
		}
		return null;
	}

	/**
	 * 指定した日付が春分の日か判定する。
	 *
	 * @param date
	 * @return 指定した日付が春分の日の場合 <code>true</code>
	 */
	public static boolean isShunbunNoHi(DateTime date) {
		if (date.getMonth() == 3) {
			int year = date.getYear();
			double daySource = 21.4471d + (0.242377d * (year - 1900)) - Math.floor((year -1900)/4.0d);
			int day = (int) Math.floor(daySource);
			if (date.getDay() == day) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定した日付が秋分の日か判定する。
	 *
	 * @param date
	 * @return 指定した日付が秋分の日の場合 <code>true</code>
	 */
	public static boolean isShuubunNoHi(DateTime date) {
		if (date.getMonth() == 9) {
			int year = date.getYear();
			double daySource = 23.8896d + (0.242032d * (year - 1900)) - Math.floor((year -1900)/4.0d);
			int day = (int) Math.floor(daySource);
			if (date.getDay() == day) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定した日付が振替休日および国民の休日か判定する。
	 *
	 * 2010年現在の日本の法制度では日曜日と祝日が重なった場合のみ振替休日が発生する。
	 * また、国民の休日は祝日と祝日に挟まれた平日が休日となる制度。
	 *
	 * @param date
	 * @return 指定した日付が振替休日および国民の休日の場合 <code>true</code>
	 */
	public static boolean isActingHoliday(DateTime date) {
		int dayOfWeek = date.getDayOfWeek();
		if (isHighDay(date) || dayOfWeek == DateTime.SUNDAY) {
			return false;
		}

		DateTime lastSunday = date.moveByDay(1 - dayOfWeek);
		//この週の日曜が祝日
		if (isHighDay(lastSunday)) {
			boolean alt = true;
			// 前日から前の日曜までに他の振替休日対象日がないかチェック
			for (int i = -1; 1 - dayOfWeek < i ; i--) {
				if (!isHighDay(date.moveByDay(i))) {
					alt = false;
					break;
				}
			}
			if (alt) {
				return true;
			}
		}

		//祝日に挟まれた平日
		if (isHighDay(date.moveByDay(1)) && isHighDay(date.moveByDay(-1))) {
			return true;
		}
		return false;
	}

	/**
	 * 指定した年の春分の日を取得する。
	 *
	 * @param year
	 * @return 指定した年の春分の日
	 */
	public static DateTime getShunbunNoHi(int year) {
		double daySource = 21.4471d + (0.242377d * (year - 1900)) - Math.floor((year -1900)/4.0d);
		int day = (int) Math.floor(daySource);

		DateTime date = DateTime.defaultInstance();
		date.setYear(year);
		date.setMonth(3);
		date.setDay(day);

		return date.minimumTimeOfDate();
	}

	/**
	 * 指定した年の秋分の日を取得する。
	 *
	 * @param year
	 * @return 指定した年の秋分の日
	 */
	public static DateTime getShuubunNoHi(int year) {
		double daySource = 23.8896d + (0.242032d * (year - 1900)) - Math.floor((year -1900)/4.0d);
		int day = (int) Math.floor(daySource);

		DateTime date = DateTime.defaultInstance();
		date.setYear(year);
		date.setMonth(9);
		date.setDay(day);

		return date.minimumTimeOfDate();
	}

	/**
	 * 日本の固定祝日一覧。
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static enum FixedHoliday {
		/** 元日	*/
		GANJITSU(1, 1, "元日"),

		/** 建国記念の日	*/
		KENKOKU_KINEN_NO_HI(2, 11, "建国記念の日"),

		/** 昭和の日	*/
		SHOWA_NO_HI(4, 29, "昭和の日"),

		/** 憲法記念日	*/
		KENPO_KINENBI(5, 3, "憲法記念日"),

		/** みどりの日	*/
		MIDORI_NO_HI(5, 4, "みどりの日"),

		/** こどもの日	*/
		KODOMO_NO_HI(5, 5, "こどもの日"),

		/** 文化の日	*/
		BUNKA_NO_HI(11, 3, "文化の日"),

		/** 勤労感謝の日	*/
		KINRO_KANSHA_NO_HI(11, 23, "勤労感謝の日"),

		/** 天皇誕生日（天皇が変わると変わるので注意）	*/
		TENNO_TANJOBI(12, 23, "天皇誕生日")
		;

		private DateTime date;
		private int month;
		private int day;
		private String name;

		/**
		 * コンストラクター
		 *
		 * @param month
		 * @param day
		 */
		private FixedHoliday(int month, int day, String name) {
			this.month = month;
			this.day = day;
			this.name = name;
			date = DateTime.defaultInstance();
			date.setMonth(month);
			date.setDay(day);
		}

		/**
		 * この祝日の月、日のみが設定された日付を取得する。年は未設定。
		 *
		 * @return この祝日の月、日のみが設定された日付
		 */
		public DateTime getDateTime() {
			return date;
		}
		
		/**
		 * @return 祝日名
		 */
		public String getName() {
			return name;
		}

		/**
		 * この祝日の指定した年の日付を取得する。
		 *
		 * @param year
		 * @return この祝日の指定した年の日付
		 */
		public DateTime getDateTime(int year) {
			DateTime newDate = date.clone();
			newDate.setYear(year);
			return newDate;
		}

		/**
		 * この祝日の月を取得する。
		 *
		 * @return この祝日の月
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * この祝日の日を取得する。
		 *
		 * @return この祝日の日
		 */
		public int getDay() {
			return day;
		}

	}

	/**
	 * 日本のハッピーマンデー一覧。
	 * ハッピーマンデーは「MM月の第X月曜日」の形で定められており、年によって日付が変わる。
	 * 2003年より前には対応していない。
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static enum HappyMonday {
		/** 成人の日 */
		SEIJIN_NO_HI(1, 2, "成人の日"),
		/** 海の日 */
		UMI_NO_HI(7, 3, "海の日"),
		/** 敬老の日 */
		KEIRO_NO_HI(9, 3, "敬老の日"),
		/** 体育の日 */
		TAIIKU_NO_HI(10, 2, "体育の日")
		;

		final private DateTime date;
		final private int month;
		final private String name;
		/** 第何週目の月曜日か */
		final private int number;

		/**
		 * コンストラクタ
		 *
		 * @param month 月
		 * @param number 第何月曜日か
		 */
		private HappyMonday(int month, int number, String name) {
			this.month = month;
			this.number = number;
			this.name = name;
			date = DateTime.defaultInstance();
			date.setMonth(month);
			date.setDay(1 + (7 * (number - 1)));
		}

		/**
		 * この祝日の月を取得する。
		 *
		 * @return この祝日の月
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * この祝日がその月の何回目の月曜日か取得する。
		 *
		 * @return この祝日の月における月曜日の番号
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * この祝日の指定した年の日付を取得する。
		 *
		 * @param year
		 * @return この祝日の指定した年の日付
		 */
		public DateTime getDateTime(int year) {
			DateTime newDate = date.clone();
			newDate.setYear(year);

			//1:日、2:月、3:火、4:水、5:木、6:金、7:土、8:日、9:月
			int startDay = newDate.getDayOfWeek();
			if (startDay < 3) {
				return newDate.moveByDay(2 - startDay);
			} else {
				return newDate.moveByDay(9 - startDay);
			}
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
	
	public static enum JapaneseHoliday {
		
	}
}