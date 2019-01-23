package jp.co.altonotes.testtool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.altonotes.http.HttpClient;
import jp.co.altonotes.http.RequestHeaderFactory;
import jp.co.altonotes.util.StopWatch;

/**
 * �P�u���E�U�łP��A�w�肵���y�[�W�ւ̃A�N�Z�X���s���B
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
	 * �R���X�g���N�^�[�B
	 *
	 * @param results
	 */
	public WebAccess(AccessResults results) {
		this.results = results;
	}

	/**
	 * ���C�������ǉ�����B
	 *
	 * @param operation
	 */
	public void addMainOperation(ClientOperation operation) {
		mainOperation.add(operation);
	}

	/**
	 * �T�u�����ǉ�����B
	 *
	 * @param operation
	 */
	public void addSubOperation(ClientOperation operation) {
		subOperation.add(operation);
	}

	/**
	 * �e�X�^�[���Z�b�g����B
	 *
	 * @param tester
	 */
	public void setTester(ResponseTester tester) {
		this.tester = tester;
	}

	/**
	 * HttpClient���擾����B
	 *
	 * @return
	 */
	public HttpClient getHttpClient() {
		return client;
	}

	/**
	 * ��������s����B
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
