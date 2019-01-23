package jp.co.altonotes.webapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jp.co.altonotes.webapp.GatewayInterceptor;

/**
 * HttpRequest�̎󂯌��ƂȂ�N���X�A���\�b�h�ɂ���A�m�e�[�V�����B
 * ���N�G�X�gURL�̃p�X�A���N�G�X�g���\�b�h�A���N�G�X�g�p�����[�^�[���w��ł���B
 *
 * @author Yamamoto Keita
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gateway {
	// class: path="/aaa" method: path="/bbb" �� /aaa/bbb
	// class: path�w��Ȃ� mthod: path="/aaa" �� /aaa
	// class: path="aaa" mthod: path�w��Ȃ� �� /aaa
	// *�̓��C���h�J�[�h

	//TODO path��method���z��ɂ���ׂ��ł́H

	/**
	 * @return �N���X����у��\�b�h�ƕR�Â��p�X
	 */
	String path() default "";

	/**
	 * @return �N���X����у��\�b�h�ƕR�Â�HTTP���N�G�X�g���\�b�h
	 */
	String method() default "";

	/**
	 * @return �N���X����у��\�b�h�ƕR�Â�HTTP���N�G�X�g�̃p�����[�^�[
	 */
	String[] params() default {};
	
	/**
	 * @return ���\�b�h�̑O�����A�㏈��
	 */
	Class<? extends GatewayInterceptor> intercept() default GatewayInterceptor.class;
}
