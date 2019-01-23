package jp.co.altonotes.webapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jp.co.altonotes.webapp.GatewayInterceptor;

/**
 * HttpRequestの受け口となるクラス、メソッドにつけるアノテーション。
 * リクエストURLのパス、リクエストメソッド、リクエストパラメーターを指定できる。
 *
 * @author Yamamoto Keita
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gateway {
	// class: path="/aaa" method: path="/bbb" → /aaa/bbb
	// class: path指定なし mthod: path="/aaa" → /aaa
	// class: path="aaa" mthod: path指定なし → /aaa
	// *はワイルドカード

	//TODO pathとmethodも配列にするべきでは？

	/**
	 * @return クラスおよびメソッドと紐づくパス
	 */
	String path() default "";

	/**
	 * @return クラスおよびメソッドと紐づくHTTPリクエストメソッド
	 */
	String method() default "";

	/**
	 * @return クラスおよびメソッドと紐づくHTTPリクエストのパラメーター
	 */
	String[] params() default {};
	
	/**
	 * @return メソッドの前処理、後処理
	 */
	Class<? extends GatewayInterceptor> intercept() default GatewayInterceptor.class;
}
