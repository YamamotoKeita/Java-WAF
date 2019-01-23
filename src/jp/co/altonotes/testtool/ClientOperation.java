package jp.co.altonotes.testtool;

import java.io.IOException;

import jp.co.altonotes.http.HttpClient;

/**
 * HttpClientに対する操作を表す。
 *
 * @author Yamamoto Keita
 *
 */
public class ClientOperation {
	private static final int TYPE_LOAD_LOCATION = 1;
	private static final int TYPE_PUSH_LINK = 2;
	private static final int TYPE_FILL_FORM = 3;
	private static final int TYPE_SUBMIT_FORM = 4;

	private int operationType;

	private String url;
	private String inputName;
	private String inputValue;

	/**
	 * この操作をHttpClientに対して実行する。
	 *
	 * @param client
	 * @throws IOException
	 */
	public void operate(HttpClient client) throws IOException {
		switch (operationType) {
		case TYPE_LOAD_LOCATION:
			client.accessByLocation(url);
			break;
		case TYPE_PUSH_LINK:
			client.accessByLink(url);
			break;
		case TYPE_FILL_FORM:
			client.fillForm(inputName, inputValue);
			break;
		case TYPE_SUBMIT_FORM:
			client.submitForm(url);
			break;
		}
	}

	/**
	 * ページロードの操作を作成する。
	 * @param url
	 * @return
	 */
	public static ClientOperation loadLocation(String url) {
		ClientOperation operation = new ClientOperation();
		operation.operationType = TYPE_LOAD_LOCATION;
		operation.url = url;
		return operation;
	}

	/**
	 * リンク押下のオペレーションを作成する。
	 *
	 * @param url
	 * @return
	 */
	public static ClientOperation linkOperation(String url) {
		ClientOperation operation = new ClientOperation();
		operation.operationType = TYPE_PUSH_LINK;
		operation.url = url;
		return operation;
	}

	/**
	 * フォーム入力の操作を作成する。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public static ClientOperation fillForm(String name, String value) {
		ClientOperation operation = new ClientOperation();
		operation.operationType = TYPE_FILL_FORM;
		operation.inputName = name;
		operation.inputValue = value;
		return operation;
	}

	/**
	 * フォーム送信の操作を作成する。
	 *
	 * @param actionURL
	 * @return
	 */
	public static ClientOperation submitForm(String actionURL) {
		ClientOperation operation = new ClientOperation();
		operation.operationType = TYPE_SUBMIT_FORM;
		operation.url = actionURL;
		return operation;
	}

}
