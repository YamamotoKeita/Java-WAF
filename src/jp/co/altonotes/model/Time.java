package jp.co.altonotes.model;

import java.io.Serializable;
import java.util.Calendar;


/**
 * 特定の時刻を表す
 *
 * @author Yamamoto Keita
 *
 */
public class Time implements Serializable, Cloneable{

	private static final long serialVersionUID = -5388341441282311043L;

	/** 時、分などの値が不定の状態を表す定数	*/
	public static int UNDEFINED = -1;

	private int hours = UNDEFINED;
	private int minutes = UNDEFINED;
	private int seconds = UNDEFINED;
	private int milliSeconds = UNDEFINED;
	private boolean roopMode = true;

	/**
	 * コンストラクター
	 *
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param milliSeconds
	 */
	public Time(int hours, int minutes, int seconds, int milliSeconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliSeconds = milliSeconds;
	}

	/**
	 * 現在時刻を取得する
	 * @return 現在時刻
	 */
	public static Time now() {
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		int milliSeconds = c.get(Calendar.MILLISECOND);
		return new Time(hours, minutes, seconds, milliSeconds);
	}

	/**
	 * 時、分、秒、ミリ秒が全てセットされているか判定する
	 * @return 時、分、秒、ミリ秒が全てセットされている場合 <code>true</code>
	 */
	public boolean isDefined() {
		return hours != UNDEFINED
				&& minutes != UNDEFINED
				&& seconds != UNDEFINED
				&& milliSeconds != UNDEFINED;
	}

	/**
	 * 一日の最小の時刻（0時0分0秒0ミリ秒）を返す。
	 * @return 0時0分0秒0ミリ秒
	 */
	public static Time minimum() {
		return new Time(0,0,0,0);
	}

	/**
	 * 一日の最大の時刻（23時59分59秒999ミリ秒）を返す。
	 * @return 23時59分59秒999ミリ秒
	 */
	public static Time maximum() {
		return new Time(23,59,59,999);
	}

	/**
	 * 引数の時刻より遅い時刻か判定する。
	 * @param time
	 * @return 引数の時刻より遅い時刻の場合 <code>true</code>
	 */
	public boolean after(Time time) {
		if (time.hours < hours) {
			return true;
		} else if (hours < time.hours) {
			return false;
		}

		if (time.minutes < minutes) {
			return true;
		} else if (minutes < time.minutes) {
			return false;
		}

		if (time.seconds < seconds) {
			return true;
		} else if (seconds < time.seconds) {
			return false;
		}

		if (time.milliSeconds < milliSeconds) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 引数の時刻より早い時刻か判定する。
	 * @param time
	 * @return 引数の時刻より早い時刻の場合 <code>true</code>
	 */
	public boolean before(Time time) {
		if (hours < time.hours) {
			return true;
		} else if (time.hours < hours) {
			return false;
		}

		if (minutes < time.minutes) {
			return true;
		} else if (time.minutes < minutes) {
			return false;
		}

		if (seconds < time.seconds) {
			return true;
		} else if (time.seconds < seconds) {
			return false;
		}

		if (milliSeconds < time.milliSeconds) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 時間単位で時刻を変更する
	 * @param hours
	 * @return 時間を移動したオブジェクト
	 */
	public Time moveByHour(int hours) {
		Time other = (Time) clone();
		int h = this.hours + hours;
		if (24 <= h) {
			if (roopMode) {
				other.setHours(h % 24);
			} else {
				other.setHours(23);
			}
		} else if (h < 0){
			if (roopMode) {
				other.setHours(24 - (h % 24));
			} else {
				other.setHours(0);
			}
		} else {
			other.setHours(h);
		}
		return other;
	}

	/**
	 * 引数の時刻以降か判定する
	 * @param time
	 * @return 引数の時刻以降の場合 <code>true</code>
	 */
	public boolean afterOrEquals(Time time) {
		return !before(time);
	}

	/**
	 * 引数の時刻以前か判定する
	 * @param time
	 * @return 引数の時刻以前の場合 <code>true</code>
	 */
	public boolean beforeOrEquals(Time time) {
		return !after(time);
	}

	/**
	 * <code>true</code>をセットすると、時間を進めたときに24:00で0:00に戻るようになる。<br>
	 * デフォルト状態は <code>true</code>
	 * @param flag
	 */
	public void setRoopMode(boolean flag) {
		roopMode = flag;
	}

	/**
	 * @return hours
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * @param hours セットする hours
	 */
	public void setHours(int hours) {
		this.hours = hours;
	}

	/**
	 * @return minutes
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @param minutes セットする minutes
	 */
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	/**
	 * @return seconds
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * @param seconds セットする seconds
	 */
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	/**
	 * @return milliSeconds
	 */
	public int getMilliSeconds() {
		return milliSeconds;
	}

	/**
	 * @param milliSeconds セットする milliSeconds
	 */
	public void setMilliSeconds(int milliSeconds) {
		this.milliSeconds = milliSeconds;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toLen2(hours)  + ":" + toLen2(minutes) + " " + toLen2(seconds) + "," + toLen3(milliSeconds);
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hours;
		result = prime * result + milliSeconds;
		result = prime * result + minutes;
		result = prime * result + seconds;
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
		Time other = (Time) obj;
		if (hours != other.hours)
			return false;
		if (minutes != other.minutes)
			return false;
		if (seconds != other.seconds)
			return false;
		if (milliSeconds != other.milliSeconds)
			return false;
		return true;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		Time clone = null;
		try {
			clone = (Time) super.clone();
		} catch (CloneNotSupportedException e) {
			throw (IllegalStateException) new IllegalStateException().initCause(e);
		}
		return clone;
	}

	/**
	 * 数値の左に0をつけて2桁の文字列にする
	 *
	 * @param num
	 * @return 2桁の文字列
	 */
	private static String toLen2(int num) {
		String src = String.valueOf(num);
		if (src.length() == 2) {
			return src;
		} else if (src.length() == 1) {
			return "0" + src;
		}
		throw new IllegalArgumentException(src);
	}

	/**
	 * 数値の左に0をつけて3桁の文字列にする
	 *
	 * @param num
	 * @return 3桁の文字列
	 */
	private static String toLen3(int num) {
		String src = String.valueOf(num);
		if (src.length() == 3) {
			return src;
		} else if (src.length() == 2) {
			return "0" + src;
		} else if (src.length() == 1) {
			return "00" + src;
		}
		throw new IllegalArgumentException(src);
	}

}
