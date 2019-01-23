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
 * Commons FTPClient�̋@�\�g���ŁB
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
	 * �R���X�g���N�^
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
	 * FTP�T�[�o�[�ɐڑ�����B
	 *
	 * @throws SocketException
	 * @throws IOException
	 */
	private void connect() throws SocketException, IOException {
		ftpClient.connect(host);
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) { // �R�l�N�g�ł������H
			throw new IOException("�ڑ��Ɏ��s���܂����B");
		}
		if (ftpClient.login(user, password) == false) { // ���O�C���ł������H
			throw new IOException("���O�C���Ɏ��s���܂����B");
		}
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * �����̃p�X�Ƀt�@�C���y�уf�B���N�g�������݂��邩���肷��B
	 *
	 * @param path
	 * @return �����̃p�X�Ƀt�@�C���y�уf�B���N�g�������݂���ꍇ<code>true</code>
	 * @throws IOException
	 */
	public boolean exists(String path) throws IOException {
		String stat = getStatus(path);
		int count = 0;
		int start = 0;
		//TODO FTP�T�[�o�ɂ���ăX�e�[�^�X���قȂ邩������Ȃ��̂ŁA�M�p���Ɍ�����
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
	 * ���[�J���f�B���N�g�����w�肷��B
	 *
	 * @param dirPath
	 * @throws IOException
	 */
	public void setLocalDirectory(String dirPath) throws IOException {
		localDirectory = dirPath;
	}

	/**
	 * FTP��ƃf�B���N�g����ύX����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 * @param path
	 * @throws IOException
	 */
	public void moveToDirectory(String path) throws IOException {
		if (!ftpClient.changeWorkingDirectory(path)) {
			throw new IOException("Directory�����݂��܂���B:" + path);
		}
	}

	/**
	 * �����̃p�X���f�B���N�g�������ׂ�B
	 *
	 * @param path
	 * @return �����̃p�X���f�B���N�g���̏ꍇ<code>true</code>
	 * @throws IOException
	 */
	public boolean isDirectory(String path) throws IOException {
		String stat = ftpClient.getStatus(path);
		int count = 0;
		int start = 0;
		//TODO FTP�T�[�o�ɂ���ăX�e�[�^�X���قȂ邩������Ȃ��̂ŁA�M�p���Ɍ�����
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
	 * �e�f�B���N�g���Ɉړ�����B
	 *
	 * @throws IOException
	 */
	public void toParentDirectory() throws IOException {
		if (!ftpClient.changeToParentDirectory()) {
			throw new IOException("�eDirectory�����݂��܂���B");
		}
	}

	/**
	 * �J�����g�f�B���N�g���̃t�@�C�������X�g���擾����B�i".", ".."�͏����j
	 *
	 * @return �J�����g�f�B���N�g���̃t�@�C�����̔z��
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
	 * �J�����g�f�B���N�g���̃t�@�C���A�f�B���N�g���ꗗ���擾����B�i".", ".."�͏����j
	 *
	 * @return �J�����g�f�B���N�g���ɑ��݂���t�@�C������уf�B���N�g���̔z��
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
	 * �J�����g�f�B���N�g�����̃t�@�C���ꗗ���擾����B�i�f�B���N�g�������j
	 * @return �J�����g�f�B���N�g���ɑ��݂���t�@�C���̔z��B�f�B���N�g���͊܂܂Ȃ��B
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
	 * �J�����g�f�B���N�g�����̃f�B���N�g���ꗗ���擾����B�i".", ".."�͏����j
	 * @return �J�����g�f�B���N�g���ɑ��݂���f�B���N�g���̔z��B�f�B���N�g���łȂ��t�@�C���͊܂܂Ȃ��B
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
	 * �J�����g�f�B���N�g���Ƀt�@�C���A����уf�B���N�g�����A�b�v���[�h����B
	 * �f�B���N�g���̏ꍇ�͂��̒��g���܂ߑS�ăA�b�v���[�h����B
	 * ���łɑ��݂���ꍇ�͏㏑������B
	 * @param file
	 * @throws IOException
	 */
	public void put(File file) throws IOException {
		if (file.isFile()) {
			putFile(file);
		} else if (file.isDirectory()) {
			putDirectory(file);
		} else {
			throw new IOException(file.getPath() + "�̓t�@�C���Ƃ��Ă��f�B���N�g���Ƃ��Ă��F���ł��܂���B");
		}
	}

	/**
	 * �J�����g�f�B���N�g���Ƀt�@�C���A����уf�B���N�g�����A�b�v���[�h����B
	 * �f�B���N�g���̏ꍇ�͂��̒��g���܂ߑS�ăA�b�v���[�h����B
	 * ���łɑ��݂���ꍇ�͏㏑������B
	 *
	 * @param localFilePath
	 * @throws IOException
	 */
	public void put(String localFilePath) throws IOException {
		put(new File(localFilePath));
	}

	/**
	 * �J�����g�f�B���N�g���Ƀt�@�C�����A�b�v���[�h����B
	 *
	 * @param file
	 * @throws IOException
	 */
	private void putFile(File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException(file.getAbsolutePath() + "�̓t�@�C���Ƃ��ēǂݎ��܂���");
		}

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			ftpClient.storeFile(file.getName(), fileInput);// �T�[�o�[��
		} finally {
			if (fileInput != null) {try {fileInput.close();} catch (Exception e) {}}
		}
	}

	/**
	 * FTP�p�X���w�肵�āA�t�@�C���A����уf�B���N�g�����A�b�v���[�h����B
	 * �f�B���N�g���̏ꍇ�͂��̒��g���܂ߑS�ăA�b�v���[�h����B
	 * ���łɑ��݂���ꍇ�͏㏑������B
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
			throw new IOException(file.getPath() + "�̓t�@�C���Ƃ��Ă��f�B���N�g���Ƃ��Ă��F���ł��܂���B");
		}
	}

	/**
	 * FTP�p�X���w�肵�ăt�@�C�����A�b�v���[�h����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 * @param file
	 * @param ftpPath
	 * @throws IOException
	 */
	private void putFile(File file, String ftpPath) throws IOException {
		if (!file.isFile()) {
			throw new IOException(file.getAbsolutePath() + "�̓t�@�C���Ƃ��ēǂݎ��܂���");
		}

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
			ftpClient.storeFile(ftpPath, fileInput);// �T�[�o�[��
		} finally {
			if (fileInput != null) {try {fileInput.close();} catch (Exception e) {}}
		}
	}

	/**
	 * �J�����g�f�B���N�g���ɁA�w�肵���f�B���N�g������т��̒��g���A�b�v���[�h����B
	 * �f�B���N�g�������ɑ��݂���ꍇ�͏㏑������B
	 * @param directory
	 * @throws IOException
	 */
	private void putDirectory(File directory) throws IOException {
		if (!directory.isDirectory()) {
			throw new IOException(directory.getAbsolutePath() + "�̓f�B���N�g���Ƃ��ēǂݎ��܂���");
		}

		if (!ftpClient.makeDirectory(directory.getName())) {
			if (!exists(directory.getName())) {
				throw new IOException(getCurrentPath() + "/" + directory.getName() + "���쐬�ł��܂���ł����B");
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
	 * FTP�p�X���w�肵�āA�f�B���N�g������т��̒��g���A�b�v���[�h����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 * �f�B���N�g�������ɑ��݂���ꍇ�͏㏑������B
	 *
	 * @param directory
	 * @param ftpPath
	 * @throws IOException
	 */
	private void putDirectory(File directory, String ftpPath) throws IOException {
		if (!directory.isDirectory()) {
			throw new IOException(directory.getAbsolutePath() + "�̓f�B���N�g���Ƃ��ēǂݎ��܂���");
		}

		String current = getCurrentPath();

		if (!ftpClient.makeDirectory(ftpPath)) {
			if (!exists(ftpPath)) {
				throw new IOException(ftpPath + "���쐬�ł��܂���ł����B");
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
	 * �w�肵���t�@�C�����폜����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 * �w�肵���t�@�C�������݂��Ȃ��ꍇfalse��Ԃ��B
	 *
	 * @param path
	 * @return ����ɍ폜���������ꍇtrue�A�w�肵���t�@�C�������݂��Ȃ��ꍇfalse�B
	 * @throws IOException
	 */
	public boolean deleteFile(String path) throws IOException {
		if (!ftpClient.deleteFile(path)) {
			if (exists(path)) {
				throw new IOException("�폜�Ɏ��s���܂���:" + path);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * �J�����g�f�B���N�g���̎w�肵���f�B���N�g�����폜����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 * �w�肵���f�B���N�g�������݂��Ȃ��ꍇfalse��Ԃ��B
	 *
	 * @param path
	 * @return ����ɍ폜���������ꍇtrue�A�w�肵���f�B���N�g�������݂��Ȃ��ꍇfalse�B
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
				throw new IOException("�폜�Ɏ��s���܂���:" + path);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * �t�@�C�����_�E�����[�h����B
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void get(String fileName) throws IOException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(localDirectory + fileName);
			ftpClient.retrieveFile(fileName, output);// �T�[�o�[��
		} finally {
			if (output != null) {try {output.close();} catch (Exception e) {}}
		}
	}

	/**
	 * FTP�̃J�����g�f�B���N�g�����擾����B
	 * @return �J�����g�f�B���N�g���̃p�X
	 * @throws IOException
	 */
	public String getCurrentPath() throws IOException {
		return ftpClient.printWorkingDirectory();
	}

	/**
	 * �w�肵���p�X�ɑ��݂���t�@�C���̃X�e�[�^�X���擾����B
	 * �J�����g�f�B���N�g������̑��΃p�X�A����ѐ�΃p�X���w��\�B
	 *
	 * @param path
	 * @return �����Ɏw�肵���p�X�̃X�e�[�^�X
	 * @throws IOException
	 */
	public String getStatus(String path) throws IOException {
		return ftpClient.getStatus(path);
	}

	/**
	 * FTP�T�[�o�Ƃ̐ڑ���ؒf����B
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
