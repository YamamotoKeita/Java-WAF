package jp.co.altonotes.webapp;

import jp.co.altonotes.webapp.handler.GatewayInvoker.GatewayInvocation;


/**
 * Gateway の処理をインターセプトする場合に、
 * このクラスを継承して、各メソッドをオーバーライドする。
 * 
 * @author Yamamoto Keita
 *
 */
public interface GatewayInterceptor {

	/**
	 * Gateway処理の開始前に実行する処理
	 */
	public void before();
	
	/**
	 * Gateway処理の完了後に実行する処理
	 */
	public void after();
	
	/**
	 * Gateway処理を挟み込む形で挿入する処理
	 * 
	 * @param invocation
	 * @return 遷移先のパス
	 * @throws Throwable
	 */
	public String around(GatewayInvocation invocation) throws Throwable;
}
