package jp.co.altonotes.util;

import jp.co.altonotes.model.DateTime;

/**
 * 2007�N�ȍ~�̖@���x�ɏ]���A���{�̏j���A�x���𔻒肷��N���X�B
 * 2006�N�ȑO�́u�݂ǂ�̓��v�̓��t���قȂ邽�ߑΉ����Ă��Ȃ��B
 *
 * @author Yamamoto Keita
 *
 */
public class JapaneseCalendar {

	/**
	 * ���̃N���X�ŏ����\�ȍŏ��̔N�x�B<br>
	 * ���̔N���O�͖@���x���قȂ邽�ߑΉ����Ă��Ȃ��B
	 */
	public static final int MINIMUM_YEAR = 2007;
	
	/**
	 * �w�肵�����t���x�������肷��B
	 * �x���͓y���A�j���A�U�֋x����S�Ċ܂ށB
	 *
	 * @param date
	 * @return �����̓��t���x���̏ꍇ<code>true</code>
	 */
	public static boolean isHoliday(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "�N���O�ɂ͑Ή����Ă��܂���B");
		}

		//�y������
		if (date.isWeekHoliday()) {
			return true;
		}

		//�Փ�����
		if (isHighDay(date)) {
			return true;
		}

		//�U��ւ��x���A�����̋x������
		if (isActingHoliday(date)) {
			return true;
		}

		return false;
	}

	/**
	 * �����̓��t�������s��̋x�������肷��B
	 *
	 * @param date
	 * @return �����̓��t�������s��̋x���̏ꍇ<code>true</code>
	 */
	public static boolean isMarketHoliday(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "�N���O�ɂ͑Ή����Ă��܂���B");
		}

		//�y������
		if (date.isWeekHoliday()) {
			return true;
		}

		//�Փ�����
		if (isHighDay(date)) {
			return true;
		}

		//��������
		if (isMarketShogatsu(date)) {
			return true;
		}

		//�U��ւ��x������
		if (isActingHoliday(date)) {
			return true;
		}

		return false;
	}

	/**
	 * �w�肵�����t�������s��̐����x�݂����肷��B
	 *
	 * @param date
	 * @return �����̓��t�������x�݂̏ꍇ<code>true</code>
	 */
	public static boolean isMarketShogatsu(DateTime date) {
		//12��31���`1��3��
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
	 * �w�肵�����t���Փ������肷��B
	 * �U�֋x���͍Փ��ɊY�����Ȃ��B
	 *
	 * @param date
	 * @return �����̓��t���Փ��̏ꍇ<code>true</code>
	 */
	public static boolean isHighDay(DateTime date) {
		if (date.getYear() < MINIMUM_YEAR) {
			throw new IllegalArgumentException(MINIMUM_YEAR + "�N���O�ɂ͑Ή����Ă��܂���B");
		}

		//�Œ�j������
		if (isFixedHoliday(date)) {
			return true;
		}

		//�n�b�s�[�}���f�[����
		if (isHappyMonday(date)) {
			return true;
		}

		//�t���̓��A�H���̓�����
		if (isShunbunNoHi(date) || isShuubunNoHi(date)) {
			return true;
		}

		return false;
	}
	
	/**
	 * �x���̖��O���擾����
	 * @param date
	 * @return �x���̖��O
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
			return "�t���̓�";
		}
		if (isShuubunNoHi(date)) {
			return "�H���̓�";
		}

		//�U��ւ��x������
		if (isActingHoliday(date)) {
			return "�U�֋x��";
		}

		return null;
	}

	/**
	 * �w�肵�����t���Œ�j�������肷��B
	 *
	 * @param date
	 * @return �����̓��t���Œ�j���̏ꍇ<code>true</code>
	 */
	public static boolean isFixedHoliday(DateTime date) {
		return getFixedHoliday(date) != null;
	}
	
	public static FixedHoliday getFixedHoliday(DateTime date) {
		int month = date.getMonth();
		//�Œ�j������
		FixedHoliday[] fixedHolidaies = FixedHoliday.values();
		for (FixedHoliday fixedHoliday : fixedHolidaies) {
			if (fixedHoliday.getMonth() == month) {	//�������̂��߁A��Ɍ��łӂ邢����
				DateTime holiday = fixedHoliday.getDateTime(date.getYear());
				if (date.equalsByDate(holiday)) {
					return fixedHoliday;
				}
			}
		}
		return null;
	}

	/**
	 * �w�肵�����t���n�b�s�[�}���f�[�����肷��B
	 *
	 * @param date
	 * @return �����̓��t���n�b�s�[�}���f�[�̏ꍇ<code>true</code>
	 */
	public static boolean isHappyMonday(DateTime date) {
		return getHappyMonday(date) != null;
	}

	private static HappyMonday getHappyMonday(DateTime date) {
		int month = date.getMonth();
		HappyMonday[] happyMondaies = HappyMonday.values();
		for (HappyMonday happyMonday : happyMondaies) {
			if (happyMonday.getMonth() == month) {	//�������̂��߁A��Ɍ��łӂ邢����
				DateTime holiday = happyMonday.getDateTime(date.getYear());
				if (date.equalsByDate(holiday)) {
					return happyMonday;
				}
			}
		}
		return null;
	}

	/**
	 * �w�肵�����t���t���̓������肷��B
	 *
	 * @param date
	 * @return �w�肵�����t���t���̓��̏ꍇ <code>true</code>
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
	 * �w�肵�����t���H���̓������肷��B
	 *
	 * @param date
	 * @return �w�肵�����t���H���̓��̏ꍇ <code>true</code>
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
	 * �w�肵�����t���U�֋x������э����̋x�������肷��B
	 *
	 * 2010�N���݂̓��{�̖@���x�ł͓��j���Əj�����d�Ȃ����ꍇ�̂ݐU�֋x������������B
	 * �܂��A�����̋x���͏j���Əj���ɋ��܂ꂽ�������x���ƂȂ鐧�x�B
	 *
	 * @param date
	 * @return �w�肵�����t���U�֋x������э����̋x���̏ꍇ <code>true</code>
	 */
	public static boolean isActingHoliday(DateTime date) {
		int dayOfWeek = date.getDayOfWeek();
		if (isHighDay(date) || dayOfWeek == DateTime.SUNDAY) {
			return false;
		}

		DateTime lastSunday = date.moveByDay(1 - dayOfWeek);
		//���̏T�̓��j���j��
		if (isHighDay(lastSunday)) {
			boolean alt = true;
			// �O������O�̓��j�܂łɑ��̐U�֋x���Ώۓ����Ȃ����`�F�b�N
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

		//�j���ɋ��܂ꂽ����
		if (isHighDay(date.moveByDay(1)) && isHighDay(date.moveByDay(-1))) {
			return true;
		}
		return false;
	}

	/**
	 * �w�肵���N�̏t���̓����擾����B
	 *
	 * @param year
	 * @return �w�肵���N�̏t���̓�
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
	 * �w�肵���N�̏H���̓����擾����B
	 *
	 * @param year
	 * @return �w�肵���N�̏H���̓�
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
	 * ���{�̌Œ�j���ꗗ�B
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static enum FixedHoliday {
		/** ����	*/
		GANJITSU(1, 1, "����"),

		/** �����L�O�̓�	*/
		KENKOKU_KINEN_NO_HI(2, 11, "�����L�O�̓�"),

		/** ���a�̓�	*/
		SHOWA_NO_HI(4, 29, "���a�̓�"),

		/** ���@�L�O��	*/
		KENPO_KINENBI(5, 3, "���@�L�O��"),

		/** �݂ǂ�̓�	*/
		MIDORI_NO_HI(5, 4, "�݂ǂ�̓�"),

		/** ���ǂ��̓�	*/
		KODOMO_NO_HI(5, 5, "���ǂ��̓�"),

		/** �����̓�	*/
		BUNKA_NO_HI(11, 3, "�����̓�"),

		/** �ΘJ���ӂ̓�	*/
		KINRO_KANSHA_NO_HI(11, 23, "�ΘJ���ӂ̓�"),

		/** �V�c�a�����i�V�c���ς��ƕς��̂Œ��Ӂj	*/
		TENNO_TANJOBI(12, 23, "�V�c�a����")
		;

		private DateTime date;
		private int month;
		private int day;
		private String name;

		/**
		 * �R���X�g���N�^�[
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
		 * ���̏j���̌��A���݂̂��ݒ肳�ꂽ���t���擾����B�N�͖��ݒ�B
		 *
		 * @return ���̏j���̌��A���݂̂��ݒ肳�ꂽ���t
		 */
		public DateTime getDateTime() {
			return date;
		}
		
		/**
		 * @return �j����
		 */
		public String getName() {
			return name;
		}

		/**
		 * ���̏j���̎w�肵���N�̓��t���擾����B
		 *
		 * @param year
		 * @return ���̏j���̎w�肵���N�̓��t
		 */
		public DateTime getDateTime(int year) {
			DateTime newDate = date.clone();
			newDate.setYear(year);
			return newDate;
		}

		/**
		 * ���̏j���̌����擾����B
		 *
		 * @return ���̏j���̌�
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * ���̏j���̓����擾����B
		 *
		 * @return ���̏j���̓�
		 */
		public int getDay() {
			return day;
		}

	}

	/**
	 * ���{�̃n�b�s�[�}���f�[�ꗗ�B
	 * �n�b�s�[�}���f�[�́uMM���̑�X���j���v�̌`�Œ�߂��Ă���A�N�ɂ���ē��t���ς��B
	 * 2003�N���O�ɂ͑Ή����Ă��Ȃ��B
	 *
	 * @author Yamamoto Keita
	 *
	 */
	public static enum HappyMonday {
		/** ���l�̓� */
		SEIJIN_NO_HI(1, 2, "���l�̓�"),
		/** �C�̓� */
		UMI_NO_HI(7, 3, "�C�̓�"),
		/** �h�V�̓� */
		KEIRO_NO_HI(9, 3, "�h�V�̓�"),
		/** �̈�̓� */
		TAIIKU_NO_HI(10, 2, "�̈�̓�")
		;

		final private DateTime date;
		final private int month;
		final private String name;
		/** �扽�T�ڂ̌��j���� */
		final private int number;

		/**
		 * �R���X�g���N�^
		 *
		 * @param month ��
		 * @param number �扽���j����
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
		 * ���̏j���̌����擾����B
		 *
		 * @return ���̏j���̌�
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * ���̏j�������̌��̉���ڂ̌��j�����擾����B
		 *
		 * @return ���̏j���̌��ɂ����錎�j���̔ԍ�
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * ���̏j���̎w�肵���N�̓��t���擾����B
		 *
		 * @param year
		 * @return ���̏j���̎w�肵���N�̓��t
		 */
		public DateTime getDateTime(int year) {
			DateTime newDate = date.clone();
			newDate.setYear(year);

			//1:���A2:���A3:�΁A4:���A5:�؁A6:���A7:�y�A8:���A9:��
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