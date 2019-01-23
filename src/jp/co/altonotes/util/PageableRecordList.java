package jp.co.altonotes.util;

import java.util.ArrayList;
import java.util.List;

/**
 * �y�[�W���O�\�ȃ��R�[�h���X�g�B
 *
 * @author Yamamoto Keita
 * @param <RecordType> 
 *
 */
public class PageableRecordList <RecordType> {

	/** �S���R�[�h		*/
	private RecordType[] allRecords;

	/** �S���R�[�h�̐�	*/
	private int recordCount = 0;

	/** ���݂̃y�[�W�ԍ�	*/
	private int currentPageNumber = 1;

	/** �����̃y�[�W�ԍ�	*/
	private int maxPageNumber = 0;

	/** 1�y�[�W�ɕ\�����郌�R�[�h�̍ő吔	*/
	private int maxRecordOfaPage = 0;

	/** �\������y�[�W�A���J�[�i�e�y�[�W�փW�����v���邽�߂̃����N�j�̍ő吔	*/
	private int maxAnchorCount = 5;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param maxRecordOfaPage  1�y�[�W�ɕ\�����郌�R�[�h�̍ő吔
	 * @param allRecords �ێ�����S���R�[�h
	 */
	public PageableRecordList(int maxRecordOfaPage, RecordType[] allRecords) {
		this.allRecords = allRecords;
		this.maxRecordOfaPage = maxRecordOfaPage;
		this.recordCount = allRecords.length;

		if (recordCount == 0) {
			maxPageNumber = 1;
		} else if ((recordCount % maxRecordOfaPage) == 0) {
			maxPageNumber = allRecords.length / maxRecordOfaPage;
		} else {
			maxPageNumber = (allRecords.length / maxRecordOfaPage) + 1;
		}
	}

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param maxRecordOfaPage  1�y�[�W�ɕ\�����郌�R�[�h�̍ő吔
	 * @param maxAnchorCount
	 * @param allRecords �ێ�����S���R�[�h
	 */
	public PageableRecordList(int maxRecordOfaPage, int maxAnchorCount, RecordType[] allRecords) {
		this.allRecords = allRecords;
		this.maxRecordOfaPage = maxRecordOfaPage;
		this.recordCount = allRecords.length;
		this.maxAnchorCount = maxAnchorCount;

		if (recordCount == 0) {
			maxPageNumber = 1;
		} else if ((recordCount % maxRecordOfaPage) == 0) {
			maxPageNumber = allRecords.length / maxRecordOfaPage;
		} else {
			maxPageNumber = (allRecords.length / maxRecordOfaPage) + 1;
		}
	}

	/**
	 * ���݂̃y�[�W���ێ����郌�R�[�h���擾����B
	 *
	 * @return ���݂̃y�[�W���ێ����郌�R�[�h�z��
	 */
	public List<RecordType> getCurrentRecords() {
		ArrayList<RecordType> result = new ArrayList<RecordType>();

		int startIndex = maxRecordOfaPage * (currentPageNumber - 1);

		if (currentPageNumber == maxPageNumber) {
			for (int i = 0; (startIndex + i) < recordCount; i++) {
				result.add(allRecords[startIndex + i]);
			}
		} else {
			for (int i = 0; i < maxRecordOfaPage; i++) {
				result.add(allRecords[startIndex + i]);
			}
		}
		return result;
	}

	/**
	 * �w�肵���y�[�W�Ɉړ�����B
	 * 0�ȉ��̃y�[�W�ԍ����w�肵���ꍇ�A1�y�[�W�ֈړ��B
	 * �ő�y�[�W���傫�ȃy�[�W�ԍ����w�肵���ꍇ�A�ő�y�[�W�ֈړ�����B
	 *
	 * @param pageNumber
	 * @return �w�肵���y�[�W�ԍ������݂��Ȃ��ꍇfalse
	 */
	public boolean movePage(int pageNumber) {
		currentPageNumber = pageNumber;
		if (currentPageNumber < 1) {
			currentPageNumber = 1;
			return false;
		} else if (currentPageNumber > maxPageNumber) {
			currentPageNumber = maxPageNumber;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ���̃y�[�W�ֈړ�����B
	 *
	 * @return ���̃y�[�W�����݂����ړ��ł��Ȃ��ꍇfalse�A�ړ��ł����ꍇ��true�B
	 */
	public boolean nextPage() {
		if (hasMoreRightPage()) {
			currentPageNumber++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��O�̃y�[�W�ֈړ�����B
	 *
	 * @return ��O�̃y�[�W�����݂����ړ��ł��Ȃ��ꍇfalse�A�ړ��ł����ꍇ��true�B
	 */
	public boolean previousPage() {
		if (hasMoreLeftPage()) {
			currentPageNumber--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �\������y�[�W�A���J�[�̍��[�̔ԍ����擾����B
	 *
	 * @return �\������y�[�W�A���J�[�̍��[�̔ԍ��B
	 */
	public int getFirstAnchorNumber() {
		//�\���A���J�[������̏ꍇ�A���݃y�[�W�́u(�\����/2) + 1�v�Ԗڂ̈ʒu�ɁA�����̏ꍇ�́u�\����/2�v�Ԗڂ̈ʒu�ɂ���
		int num = currentPageNumber - (maxAnchorCount / 2);
		if (num > maxPageNumber - maxAnchorCount + 1) {
			num = maxPageNumber - maxAnchorCount + 1;
		}
		if (num < 1) {
			num = 1;
		}
		return num;
	}

	/**
	 * �\������y�[�W�A���J�[�̉E�[�̔ԍ����擾����B
	 *
	 * @return �\������y�[�W�A���J�[�̉E�[�̔ԍ��B
	 */
	public int getLastAnchorNumber() {
		//�\���A���J�[������̏ꍇ�A���݃y�[�W�́u(�\����/2) + 1�v�Ԗڂ̈ʒu�ɁA�����̏ꍇ�́u�\����/2�v�Ԗڂ̈ʒu�ɂ���
		int num = currentPageNumber + ((maxAnchorCount - 1) / 2);
		if (num < maxAnchorCount) {
			num = maxAnchorCount;
		}
		if (num > maxPageNumber) {
			num = maxPageNumber;
		}
		return num;
	}

	/**
	 * ���݂̃y�[�W�ԍ����擾����B
	 * �y�[�W�ԍ���1���n�܂�B
	 *
	 * @return ���݂̃y�[�W�ԍ�
	 */
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	/**
	 * ��O�̃y�[�W�ԍ���Ԃ��B
	 *
	 * @return ��O�̃y�[�W�ԍ�
	 */
	public int getPreviousPageNumber() {
		int i = currentPageNumber - 1;
		if (i < 1) {
			return 1;
		} else {
			return i;
		}
	}

	/**
	 * ����̃y�[�W�ԍ���Ԃ��B
	 *
	 * @return ����̃y�[�W�ԍ�
	 */
	public int getNextPageNumber() {
		int i = currentPageNumber + 1;
		if (maxPageNumber < i) {
			return maxPageNumber;
		} else {
			return i;
		}
	}

	/**
	 * ���݂̃y�[�W��荶�̃y�[�W�����݂���ꍇtrue��Ԃ��B
	 *
	 * @return ���݂̃y�[�W��荶�̃y�[�W�����݂���ꍇ true
	 */
	public boolean hasMoreLeftPage() {
		return currentPageNumber > 1;
	}

	/**
	 * ���݂̃y�[�W���E�̃y�[�W�����݂���ꍇtrue��Ԃ��B
	 *
	 * @return ���݂̃y�[�W���E�̃y�[�W�����݂���ꍇ true
	 */
	public boolean hasMoreRightPage() {
		return currentPageNumber < maxPageNumber;
	}

	/**
	 * @return �P�y�[�W���̍ő僌�R�[�h��
	 */
	public int getMaxRecordOfaPage() {
		return maxRecordOfaPage;
	}

	/**
	 * �P�y�[�W���̍ő僌�R�[�h�����Z�b�g����
	 * @param maxRecordOfaPage
	 */
	public void setMaxRecordOfaPage(int maxRecordOfaPage) {
		this.maxRecordOfaPage = maxRecordOfaPage;
	}

	/**
	 * @return �y�[�W�A���J�[�i�e�y�[�W�փW�����v���邽�߂̃����N�j�̍ő吔
	 */
	public int getMaxAnchorCount() {
		return maxAnchorCount;
	}

	/**
	 * �y�[�W�A���J�[�i�e�y�[�W�փW�����v���邽�߂̃����N�j�̍ő吔���Z�b�g����
	 * @param maxAnchorCount
	 */
	public void setMaxAnchorCount(int maxAnchorCount) {
		this.maxAnchorCount = maxAnchorCount;
	}

	/**
	 * @return ���R�[�h��
	 */
	public int getRecordCount() {
		return recordCount;
	}

}
