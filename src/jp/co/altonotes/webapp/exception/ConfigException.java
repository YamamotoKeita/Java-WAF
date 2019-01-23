package jp.co.altonotes.webapp.exception;

/**
 * アプリケーション起動時のコンフィグによる設定エラー
 *
 * @author Yamamoto Keita
 *
 */
public class ConfigException extends RuntimeException{
	private static final long serialVersionUID = 7875714318101991278L;

	/**
	 * コンストラクター
	 *
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
	}
}
