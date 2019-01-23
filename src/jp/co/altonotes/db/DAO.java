package jp.co.altonotes.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �f�[�^�x�[�X�̃J�������ƃ��f���N���X�̃����o�[�ϐ���R�Â���B
 * 
 * @author Yamamoto Keita
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DAO {

	/**
	 * @return DataSource
	 */
	String source();
	
	/**
	 * @return �e�[�u����
	 */
	String table();
}