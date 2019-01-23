package jp.co.altonotes.testtool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.http.HttpClient;
import jp.co.altonotes.http.RequestHeaderFactory;
import jp.co.altonotes.util.StopWatch;

/**
 * １ブラウザで１回、指定したページへのアクセスを行う。
 *
 * @author Yamamoto Keita
 *
 */
public class WebAccess extends Thread {
	private HttpClient client = new HttpClient(RequestHeaderFactory.TYPE_PC_FIREFOX3);
	private StopWatch stopWatch = new StopWatch();
	private List<ClientOperation> mainOperation = new ArrayList<ClientOperation>();
	private List<ClientOperation> subOperation = new ArrayList<ClientOperation>();
	private AccessResults results;
	private ResponseTester tester;

	/**
	 * コンストラクター。
	 *
	 * @param results
	 */
	public WebAccess(AccessResults results) {
		this.results = results;
	}

	/**
	 * メイン操作を追加する。
	 *
	 * @param operation
	 */
	public void addMainOperation(ClientOperation operation) {
		mainOperation.add(operation);
	}

	/**
	 * サブ操作を追加する。
	 *
	 * @param operation
	 */
	public void addSubOperation(ClientOperation operation) {
		subOperation.add(operation);
	}

	/**
	 * テスターをセットする。
	 *
	 * @param tester
	 */
	public void setTester(ResponseTester tester) {
		this.tester = tester;
	}

	/**
	 * HttpClientを取得する。
	 *
	 * @return
	 */
	public HttpClient getHttpClient() {
		return client;
	}

	/**
	 * 操作を実行する。
	 */
	public void run() {
		byte[] page = null;

		ArrayList<String> errors = new ArrayList<String>();

		stopWatch.start();
		try {
			for (ClientOperation operation : mainOperation) {
				operation.operate(client);
			}

			page = client.getBinaryData();

			for (ClientOperation operation : subOperation) {
				operation.operate(client);
			}
		} catch (IOException e) {
			errors.add(e.toString());
		}
		stopWatch.stop();

		if (tester != null && page != null) {
			List<String> pageErrors = tester.test(page);
			for (String pageError : pageErrors) {
				errors.add(pageError);
			}
		}

		results.addResult(stopWatch.lastTime(), errors);
	}
}
