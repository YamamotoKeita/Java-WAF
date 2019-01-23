package jp.co.altonotes.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * データベースのカラム名とモデルクラスのメンバー変数を紐づける。
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
	 * @return テーブル名
	 */
	String table();
}
