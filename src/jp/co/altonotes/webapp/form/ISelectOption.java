package jp.co.altonotes.webapp.form;

import java.io.Serializable;

/**
 * �R���{�{�b�N�X�̃I�v�V�����̃C���^�[�t�F�[�X
 *
 * @author Yamamoto Keita
 *
 */
public interface ISelectOption extends Serializable {

	/**
	 * �I�v�V������ value �̒l���擾����B
	 * @return �I�v�V������ value �̒l
	 */
	public String getValue();

	/**
	 * �I�v�V�����̕\�����x�����擾����B
	 * @return �I�v�V�����̕\�����x��
	 */
	public String getLabel();
}
