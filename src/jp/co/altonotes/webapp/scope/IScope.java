package jp.co.altonotes.webapp.scope;

/**
 * Request, Session, Page, Application��e��X�R�[�v�����I�Ɉ������߂̃C���^�[�t�F�[�X�B
 *
 * @author Yamamoto Keita
 *
 */
public interface IScope {
	/**
	 * @param name
	 * @return �����̖��O��attribute
	 */
	public Object getAttribute(String name);
}
