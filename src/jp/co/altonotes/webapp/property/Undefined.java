package jp.co.altonotes.webapp.property;

/**
 * 未定義のオブジェクトを表す。
 * シングルトンの VALUE 定数を使用するだけで、オブジェクトの生成はできない。
 * 
 * @author Yamamoto Keita
 */
final class Undefined {
	final static Undefined VALUE = new Undefined();
	private Undefined() {}
}
