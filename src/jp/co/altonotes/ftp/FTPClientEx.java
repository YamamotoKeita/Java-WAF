package jp.co.altonotes.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import jp.co.altonotes.net.ftp.FTP;
import jp.co.altonotes.net.ftp.FTPClient;
import jp.co.altonotes.net.ftp.FTPFile;
import jp.co.altonotes.net.ftp.FTPReply;

/**
 * Commons FTPClientの機能拡張版。
 *
 * @author Yamamoto Keita
 *
 */
public class FTPClientEx {

	private FTPClient ftpClient = new FTPClient();
	private String host;
	private String user;
	private String password;
	private String localDirectory;

	/**
	 * コンストラクタ
	 * @param host
	 * @param user
	 * @param password
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPClientEx(String host, String user, String password) throws SocketException, IOException {
		this.host = host;
		this.user = user;
		this.password = password;
		connect();
	}

	/**
	 * FTPサーバーに接続する。
	 *
	 * @throws SocketException
	 * @throws IOException
	 */
	private void connect() throws SocketException, IOException {
		ftpClient.connect(host);
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) { // コネクトできたか？
			throw new IOException("接続に失敗しました。");
		}
		if (ftpClient.login(user, password) == false) { // ログインできたか？
			throw new IOException("ログインに失敗しました。");
		}
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * 引数のパスにファイル及びディレクトリが存在するか判定する。
	 *
	 * @param path
	 * @return 引数のパスにファイル及びディレクトリが存在する場合<code>true</code>
	 * @throws IOException
	 */
	public boolean exists(String path) throws IOException {
		String stat = getStatus(path);
		int count = 0;
		int start = 0;
		//TODO FTPサーバによってステータスが異なるかもしれないので、信用性に欠ける
		while ((start = stat.indexOf("\n", start) + 1) > 0) {
			count ++;
		}
		if (count >= 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ローカルディレクトリを指定する。
	 *
	 * @param dirPath
	 * @throws IOException
	 */
	public void setLocalDirectory(String dirPath) throws IOException {
		localDirectory = dirPath;
	}

	/**
	 * FTP作業ディレクトリを変更する。
	 * カレントディレクトリからの相対パス、および絶対パスを指定可能。
	 * @param path
	 * @throws IOException
	 */
	public void moveToDirectory(String path) throws IOException {
		if (!ftpClient.changeWorkingDirectory(path)) {
			throw new IOException("Directoryが存在しません。:" + path);
		}
	}

	/**
	 * 引数のパスがディレクトリか調べる。
	 *
	 * @param path
	 * @return 引数のパスがディレクトリの場合<code>true</code>
	 * @throws IOException
	 */
	public boolean isDirectory(String path) throws IOException {
		String stat = ftpClient.getStatus(path);
		int count = 0;
		int start = 0;
		//TODO FTPサーバによってステータスが異なるかもしれないので、信用性に欠ける
		while ((start = stat.indexOf("\n", start) + 1) > 0) {
			count ++;
		}
		if (count > 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 親ディレクトリに移動する。
	 *
	 * @throws IOException
	 */
	public void toParentDirectory() throws IOException {
		if (!ftpClient.changeToParentDirectory()) {
			throw new IOException("親Directoryが存在しません。");
		}
	}

	/**
	 * カレントディレクトリのファイル名リストを取得する。（".", ".."は除く）
	 *
	 * @return カレントディレクトリのファイル名の配列
	 * @throws IOException
	 */
	public String[] listNames() throws IOException {
		ArrayList<String> temp = new ArrayList<String>();
		String[] fullList = ftpClient.listNames();
		for (String name : fullList) {
			if (!name.equals(".") && !name.equals("..")) {
				temp.add(name);
			}
		}
		return temp.toArray(new String[temp.size()]);
	}

	/**
	 * カレントディレクトリのファイル、ディレクトリ一覧を取得する。（".", ".."は除く）
	 *
	 * @return カレントディレクトリに存在するファイルおよびディレクトリの配列
	 * @throws IOException
	 */
	public FTPFile[] listAll() throws IOException {
		ArrayList<FTPFile> temp = new ArrayList<FTPFile>();
		FTPFile[] fullList = ftpClient.listFiles();
		for (FTPFile ftpFile : fullList) {
			if (!ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")) {
				temp.add(ftpFile);
			}
		}
		return temp.toArray(new FTPFile[temp.size()]);
	}

	/**
	 * カレントディレクトリ内のファイル一覧を取得する。（ディレクトリ除く）
	 * @return カレントディレクトリに存在するファイルの配列。ディレクトリは含まない。
	 * @throws IOException
	 */
	public FTPFile[] listFiles() throws IOException {
		ArrayList<FTPFile> temp = new ArrayList<FTPFile>();
		FTPFile[] fullList = ftpClient.listFiles();
		for (FTPFile ftpFile : fullList) {
			if (ftpFile.isFile()) {
				temp.add(ftpFile);
			}
		}
		return temp.toArray(new FTPFile[temp.size()]);
	}

	/**
	 * カレントディレクトリ内のディレクトリ一覧を取得する。（".", ".."は除く）
	 * @return カレントディレクトリに存在するディレクトリの配列。ディレクトリでないファイルは含まない。
	 * @throws IOException
	 */
	public FTPFile[] listDirectories() throws IOException {
		ArrayList<FTPFile> temp = new ArrayList<FTPFile>();
		FTPFile[] fullList = ftpClient.listFiles();
		for (FTPFile ftpFile : fullList) {
			if (ftpFile.isDirectory()) {
				if (!ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")) {
					temp.add(ftpFile);
				}
			}
		}
		return temp.toArray(new FTPFile[temp.size()]);
	}

	/**
	 * カレントディレクトリにファイル、およびディレクトリをアップロードする。
	 * ディレクトリの場合はその中身も含め全てアップロードする。
	 * すでに存在する場合は上書きする。
	 * @param file
	 * @throws IOException
	 */
	public void put(File file) throws IOException {
		if (file.isFile()) {
			putFile(file);
		} else if (file.isDirectory()) {
			putDirectory(file);
		} else {
			throw new IOException(file.getPath() + "はファイルとしてもディレクトリとしても認識できません。");
		}
	}

	/**
	 * カレントディレクトリにファイル、およびディレクトリをアップロードする。
	 * ディレクトリの場合はその中身も含め全てアップロードする。
	 * すでに存在する場合は上書きする。
	 *
	 * @param localFilePath
	 * @throws IOException
	 */
	public void put(String localFilePath) throws IOException {
		put(new File(localFilePath));
	}

	/**
	 * カレントディレクトリにファイルをアップロードする。
	 *
	 * @param file
	 * @throws IOException
	 */
	private void putFile(File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException(file.getAbsolutePath() + "はファイルとして読み取れません");
		}

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			ftpClient.storeFile(file.getName(), fileInput);// サーバー側
		} finally {
			if (fileInput != null) {try {fileInput.close();} catch (Exception e) {}}
		}
	}

	/**
	 * FTPパスを指定して、ファイル、およびディレクトリをアップロードする。
	 * ディレクトリの場合はその中身も含め全てアップロードする。
	 * すでに存在する場合は上書きする。
	 *
	 * @param file
	 * @param ftpPath
	 * @throws IOException
	 */
	public void put(File file, String ftpPath) throws IOException {
		if (file.isFile()) {
			putFile(file, ftpPath);
		} else if (file.isDirectory()) {
			putDirectory(file, ftpPath);
		} else {
			throw new IOException(file.getPath() + "はファイルとしてもディレクトリとしても認識できません。");
		}
	}

	/**
	 * FTPパスを指定してファイルをアップロードする。
	 * カレントディレクトリからの相対パス、および絶対パスを指定可能。
	 * @param file
	 * @param ftpPath
	 * @throws IOException
	 */
	private void putFile(File file, String ftpPath) throws IOException {
		if (!file.isFile()) {
			throw new IOException(file.getAbsolutePath() + "はファイルとして読み取れません");
		}

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			ftpClient.storeFile(ftpPath, fileInput);// サーバー側
		} finally {
			if (fileInput != null) {try {fileInput.close();} catch (Exception e) {}}
		}
	}

	/**
	 * カレントディレクトリに、指定したディレクトリおよびその中身をアップロードする。
	 * ディレクトリが既に存在する場合は上書きする。
	 * @param directory
	 * @throws IOException
	 */
	private void putDirectory(File directory) throws IOException {
		if (!directory.isDirectory()) {
			throw new IOException(directory.getAbsolutePath() + "はディレクトリとして読み取れません");
		}

		if (!ftpClient.makeDirectory(directory.getName())) {
			if (!exists(directory.getName())) {
				throw new IOException(getCurrentPath() + "/" + directory.getName() + "を作成できませんでした。");
			}
		}

		moveToDirectory(directory.getName());

		File[] children = directory.listFiles();
		for (File child : children) {
			if (child.isDirectory()) {
				putDirectory(child);
			} else {
				putFile(child);
			}
		}

		toParentDirectory();
	}

	/**
	 * FTPパスを指定して、ディレクトリおよびその中身をアップロードする。
	 * カレントディレクトリからの相対パス、および絶対パスを指定可能。
	 * ディレクトリが既に存在する場合は上書きする。
	 *
	 * @param directory
	 * @param ftpPath
	 * @throws IOException
	 */
	private void putDirectory(File directory, String ftpPath) throws IOException {
		if (!directory.isDirectory()) {
			throw new IOException(directory.getAbsolutePath() + "はディレクトリとして読み取れません");
		}

		String current = getCurrentPath();

		if (!ftpClient.makeDirectory(ftpPath)) {
			if (!exists(ftpPath)) {
				throw new IOException(ftpPath + "を作成できませんでした。");
			}
		}

		moveToDirectory(ftpPath);

		File[] children = directory.listFiles();
		for (File child : children) {
			if (child.isDirectory()) {
				putDirectory(child);
			} else {
				putFile(child);
			}
		}

		moveToDirectory(current);
	}

	/**
	 * 指定したファイルを削除する。
	 * カレントディレクトリからの相対パス、および絶対パスが指定可能。
	 * 指定したファイルが存在しない場合falseを返す。
	 *
	 * @param path
	 * @return 正常に削除完了した場合true、指定したファイルが存在しない場合false。
	 * @throws IOException
	 */
	public boolean deleteFile(String path) throws IOException {
		if (!ftpClient.deleteFile(path)) {
			if (exists(path)) {
				throw new IOException("削除に失敗しました:" + path);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * カレントディレクトリの指定したディレクトリを削除する。
	 * カレントディレクトリからの相対パス、および絶対パスが指定可能。
	 * 指定したディレクトリが存在しない場合falseを返す。
	 *
	 * @param path
	 * @return 正常に削除完了した場合true、指定したディレクトリが存在しない場合false。
	 * @throws IOException
	 */
	public boolean deleteDirectory(String path) throws IOException {
		String current = ftpClient.printWorkingDirectory();

		try {
			moveToDirectory(path);
		} catch (IOException e) {
			if (!exists(path)) {
				return false;
			} else {
				throw e;
			}
		}

		FTPFile[] list = listAll();
		for (FTPFile file : list) {
			if (file.isDirectory()) {
				deleteDirectory(file.getName());
			} else {
				deleteFile(file.getName());
			}
		}
		moveToDirectory(current);
		if (!ftpClient.removeDirectory(path)) {
			if (exists(path)) {
				throw new IOException("削除に失敗しました:" + path);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * ファイルをダウンロードする。
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void get(String fileName) throws IOException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(localDirectory + fileName);
			ftpClient.retrieveFile(fileName, output);// サーバー側
		} finally {
			if (output != null) {try {output.close();} catch (Exception e) {}}
		}
	}

	/**
	 * FTPのカレントディレクトリを取得する。
	 * @return カレントディレクトリのパス
	 * @throws IOException
	 */
	public String getCurrentPath() throws IOException {
		return ftpClient.printWorkingDirectory();
	}

	/**
	 * 指定したパスに存在するファイルのステータスを取得する。
	 * カレントディレクトリからの相対パス、および絶対パスを指定可能。
	 *
	 * @param path
	 * @return 引数に指定したパスのステータス
	 * @throws IOException
	 */
	public String getStatus(String path) throws IOException {
		return ftpClient.getStatus(path);
	}

	/**
	 * FTPサーバとの接続を切断する。
	 */
	public void disconnect() {
		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
