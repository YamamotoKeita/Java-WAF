package jp.co.altonotes.model;

import java.io.Serializable;
import java.util.Calendar;


/**
 * ����̎�����\��
 *
 * @author Yamamoto Keita
 *
 */
public class Time implements Serializable, Cloneable{

	private static final long serialVersionUID = -5388341441282311043L;

	/** ���A���Ȃǂ̒l���s��̏�Ԃ�\���萔	*/
	public static int UNDEFINED = -1;

	private int hours = UNDEFINED;
	private int minutes = UNDEFINED;
	private int seconds = UNDEFINED;
	private int milliSeconds = UNDEFINED;
	private boolean roopMode = true;

	/**
	 * �R���X�g���N�^�[
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
	 * ���ݎ������擾����
	 * @return ���ݎ���
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
	 * ���A���A�b�A�~���b���S�ăZ�b�g����Ă��邩���肷��
	 * @return ���A���A�b�A�~���b���S�ăZ�b�g����Ă���ꍇ <code>true</code>
	 */
	public boolean isDefined() {
		return hours != UNDEFINED
				&& minutes != UNDEFINED
				&& seconds != UNDEFINED
				&& milliSeconds != UNDEFINED;
	}

	/**
	 * ����̍ŏ��̎����i0��0��0�b0�~���b�j��Ԃ��B
	 * @return 0��0��0�b0�~���b
	 */
	public static Time minimum() {
		return new Time(0,0,0,0);
	}

	/**
	 * ����̍ő�̎����i23��59��59�b999�~���b�j��Ԃ��B
	 * @return 23��59��59�b999�~���b
	 */
	public static Time maximum() {
		return new Time(23,59,59,999);
	}

	/**
	 * �����̎������x�����������肷��B
	 * @param time
	 * @return �����̎������x�������̏ꍇ <code>true</code>
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
	 * �����̎�����葁�����������肷��B
	 * @param time
	 * @return �����̎�����葁�������̏ꍇ <code>true</code>
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
	 * ���ԒP�ʂŎ�����ύX����
	 * @param hours
	 * @return ���Ԃ��ړ������I�u�W�F�N�g
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
	 * �����̎����ȍ~�����肷��
	 * @param time
	 * @return �����̎����ȍ~�̏ꍇ <code>true</code>
	 */
	public boolean afterOrEquals(Time time) {
		return !before(time);
	}

	/**
	 * �����̎����ȑO�����肷��
	 * @param time
	 * @return �����̎����ȑO�̏ꍇ <code>true</code>
	 */
	public boolean beforeOrEquals(Time time) {
		return !after(time);
	}

	/**
	 * <code>true</code>���Z�b�g����ƁA���Ԃ�i�߂��Ƃ���24:00��0:00�ɖ߂�悤�ɂȂ�B<br>
	 * �f�t�H���g��Ԃ� <code>true</code>
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
	 * @param hours �Z�b�g���� hours
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
	 * @param minutes �Z�b�g���� minutes
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
	 * @param seconds �Z�b�g���� seconds
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
	 * @param milliSeconds �Z�b�g���� milliSeconds
	 */
	public void setMilliSeconds(int milliSeconds) {
		this.milliSeconds = milliSeconds;
	}

	/* (�� Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toLen2(hours)  + ":" + toLen2(minutes) + " " + toLen2(seconds) + "," + toLen3(milliSeconds);
	}

	/* (�� Javadoc)
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
	 * (�� Javadoc)
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
	 * ���l�̍���0������2���̕�����ɂ���
	 *
	 * @param num
	 * @return 2���̕�����
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
	 * ���l�̍���0������3���̕�����ɂ���
	 *
	 * @param num
	 * @return 3���̕�����
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
