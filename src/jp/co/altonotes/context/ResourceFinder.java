package jp.co.altonotes.context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 指定したパッケージ内のリソース、クラスを取得する
 *
 * @author Yamamoto Keita
 *
 */
public class ResourceFinder {

	/** ファイルシステムのURLプレフィックス */
	public static final String FILE_URL_PREFIX = "file:";

	/** ファイルシステムのURLプロトコル */
	public static final String URL_PROTOCOL_FILE = "file";

	/** JARエントリーのURLプロトコル */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** ZIPエントリーのURLプロトコル */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** JBossのJARファイルのURLプロトコル */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** JBossのVFSリソースのURLプロトコル */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** WebSphereのJARファイルのURLプロトコル */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** Oracle Containers for J2EE（OC4J）のJARファイルのURLプロトコル */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** JAR内のJAR URL とファイルパスのセパレーター */
	public static final String JAR_URL_SEPARATOR = "!/";

	/** Equinox OSGiの"bundleresource:"、"bundleentry:" 形式のURLのリゾルバー */
	private static Method equinoxResolveMethod;

	static {
		// OSGiのbundle URLのresolverを探す(ex. WebSphere 6.1)
		try {
			Class<?> fileLocatorClass = ResourceFinder.class.getClassLoader().loadClass("org.eclipse.core.runtime.FileLocator");
			equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
		} catch (Throwable ex) {
			equinoxResolveMethod = null;
		}
	}

	/**
	 * 指定したパッケージ以下のクラスをサブパッケージも含め全て取得する。
	 * TODO クラスを取得した際staticの処理が走り、そこでExceptionが発生すると落ちる。
	 *
	 * @param packageName
	 * @return クラスのList
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
	public List<Class<?>> getClassList(String packageName) throws IOException, ClassNotFoundException {
		List<URI> resources = findResources(packageName, "class");
		String packagePath = packageToResourcePath(packageName);

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

		int idx;
		String relativePath;
		String resourcePath;
		for (URI uri : resources) {
			resourcePath = uri.toString();
			idx = resourcePath.lastIndexOf(packagePath);
			relativePath = resourcePath.substring(idx + packagePath.length());

			if (relativePath.contains("$")) {//TODO インナークラスはスキップ
				continue;
			}

			relativePath = relativePath.substring(0, relativePath.length() -6);// .classを削除
			relativePath = relativePath.replace('/', '.');

			String className = packageName + "." + relativePath;
			Class<?> klass = null;
			try {
				klass = getClassLoader().loadClass(className);

				//getResurcesによって同じクラスが重複して入る可能性があるためチェック
				if (!classMap.containsKey(className)) {
					classMap.put(className, klass);
				}
			} catch (Throwable e) {//TODO static初期化子のエラーをどうする？
				e.printStackTrace();
			}
		}

		List<Class<?>> classList = new ArrayList<Class<?>>();
		Collection<Class<?>> collection = classMap.values();
		for (Class<?> class1 : collection) {
			classList.add(class1);
		}

		return classList;
	}

	/**
	 * 指定したパッケージ内の、指定した拡張子の全てのリソースのURIを取得する
	 *
	 * @param rootPackage
	 * @param extention
	 * @return 該当するURIのLIST
	 * @throws IOException
	 */
	public List<URI> findResources(String rootPackage, String extention) throws IOException {

		String path = packageToResourcePath(rootPackage);
		List<URL> rootDirResources = findResources(path);

		List<URI> result = new ArrayList<URI>(16);
		for (URL rootDirResource : rootDirResources) {
			rootDirResource = resolveBundleResource(rootDirResource);

			if (isJarURL(rootDirResource)) {
				result.addAll(findJarResources(rootDirResource, extention));
			} else if (isVFSURL(rootDirResource)) {
				result.addAll(findVFSResources(rootDirResource, extention));
			} else {
				result.addAll(findFileSystemResources(rootDirResource, extention));
			}
		}
		return result;

	}

	/**
	 * 指定したパスのリソースを全て取得する。
	 *
	 * @param path
	 * @return 指定したパスのリソースのURLのList
	 * @throws IOException
	 */
	public List<URL> findResources(String path) throws IOException {
		Enumeration<URL> resourceUrls = getClassLoader().getResources(path);
		List<URL> list = new ArrayList<URL>(16);
		while (resourceUrls.hasMoreElements()) {
			URL url = resourceUrls.nextElement();
			list.add(url);
		}
		return list;
	}

	/**
	 * ルートURL以下の指定拡張子のURIを全て取得する。
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 */
	protected List<URI> findFileSystemResources(URL rootURL, String extention) {

		List<URI> result = new ArrayList<URI>();

		File rootDir = null;
		try {
			rootDir = new File(toURI(rootURL).getSchemeSpecificPart()).getAbsoluteFile();
		} catch (URISyntaxException e) {
			return result;
		}

		if (!rootDir.exists()) {
			return result;
		}
		//存在するがディレクトリじゃない場合はファッキングシット。
		if (!rootDir.isDirectory()) {
			return result;
		}
		if (!rootDir.canRead()) {
			return result;
		}

		findMatchingFiles(rootDir, extention, result);

		return result;
	}

	/**
	 * ルートディレクトリ以下の指定拡張子のURIを全て取得する。
	 *
	 * @param dir
	 * @param extention
	 * @param result
	 */
	protected void findMatchingFiles(File dir, String extention, List<URI> result) {
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			return;
		}
		for (File content : dirContents) {
			if (content.isDirectory()) {
				if (content.canRead()) {
					findMatchingFiles(content, extention, result);
				}
			} else if (content.isFile() && extentionMath(content, extention)) {
				result.add(content.toURI());
			}
		}
	}

	/**
	 * JARファイル内のリソースを取得する。
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 * @throws IOException
	 */
	protected List<URI> findJarResources(URL rootURL, String extention) throws IOException {

		URLConnection con = rootURL.openConnection();
		JarFile jarFile;
		String jarFileUrl;
		String rootEntryPath;
		boolean newJarFile = false;

		if (con instanceof JarURLConnection) { //普通のJARファイルの場合
			JarURLConnection jarCon = (JarURLConnection) con;
			jarCon.setUseCaches(false);
			jarFile = jarCon.getJarFile();
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath = jarEntry != null ? jarEntry.getName() : "";
		} else { //JarURLConnectionが無い場合
			String urlFile = rootURL.getFile();
			int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
			if (separatorIndex != -1) { // "jar:path!/entry"フォーマットの場合
				jarFileUrl = urlFile.substring(0, separatorIndex);
				rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
				jarFile = getJarFile(jarFileUrl);
			} else { //その他（"file:"プレフィックスがある場合、無い場合両方を含む）
				jarFile = new JarFile(urlFile);
				rootEntryPath = "";
			}
			newJarFile = true;
		}

		try {
			// 適切なマッチングを行うためルートエントリーパスはスラッシュで終わる必要がある。
			// BEA JRockitはスラッシュを付けるが、SunのJREはスラッシュを付けないためここで加える。
			if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
				rootEntryPath = rootEntryPath + "/";
			}

			List<URI> result = new ArrayList<URI>(8);
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(rootEntryPath)) {
					String relativePath = entryPath.substring(rootEntryPath.length());
					if (extentionMatch(relativePath, extention)) {
						URL url = createRelative(rootURL, relativePath);
						try {
							result.add(toURI(url));
						} catch (URISyntaxException e) {
						}
					}
				}
			}
			return result;
		} finally {
			// JarURLConnectionはファイルの参照をキャッシュするかもしれないので、
			// JarURLConnectionを使わずJarFileインスタンスを新規作成した場合のみクローズする。
			if (newJarFile) {
				jarFile.close();
			}
		}
	}

	/**
	 * VFSのリソースを取得する。
	 *
	 * @param rootURL
	 * @param extention
	 * @return
	 * @throws IOException
	 */
	private List<URI> findVFSResources(URL rootURL, String extention) throws IOException {
		Object root = VfsPatternUtils.findRoot(rootURL);
		PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), extention);
		VfsPatternUtils.visit(root, visitor);
		return visitor.getResources();
	}


	/**
	 * @return クラスローダー
	 */
	public ClassLoader getClassLoader() {
		ClassLoader loader = null;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) { }// スレッドのコンテキストクラスローダーにアクセスできない場合は、システムクラスローダーを使う

		if (loader == null) {
			loader = ResourceFinder.class.getClassLoader();
		}
		return loader;
	}

	/**
	 * VFS visitor for path matching purposes.
	 */
	private static class PatternVirtualFileVisitor implements InvocationHandler {

		private final String extention;

		private final String rootPath;

		private final List<URI> resources = new ArrayList<URI>();

		public PatternVirtualFileVisitor(String rootPath, String extention) {
			this.extention = extention;
			this.rootPath = (rootPath.length() == 0 || rootPath.endsWith("/") ? rootPath : rootPath + "/");
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (Object.class.equals(method.getDeclaringClass())) {
				if (methodName.equals("equals")) {
					// Only consider equal when proxies are identical.
					return (proxy == args[0]);
				} else if (methodName.equals("hashCode")) {
					return System.identityHashCode(proxy);
				}
			} else if ("getAttributes".equals(methodName)) {
				return getAttributes();
			} else if ("visit".equals(methodName)) {
				visit(args[0]);
				return null;
			} else if ("toString".equals(methodName)) {
				return toString();
			}

			throw new IllegalStateException("Unexpected method invocation: " + method);
		}

		public void visit(Object vfsResource) throws IOException {
			String path = VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length());
			if (extentionMatch(path, extention)) {
				URL url = VfsUtils.getURL(vfsResource);
				try {
					resources.add(toURI(url));
				} catch (URISyntaxException e) {
				}
			}
		}

		public Object getAttributes() {
			return VfsPatternUtils.getVisitorAttribute();
		}


		public List<URI> getResources() {
			return this.resources;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("sub-pattern: ").append(this.extention);
			sb.append(", resources: ").append(this.resources);
			return sb.toString();
		}
	}

	/**
	 * 指定した拡張子を持っているか判定する。
	 *
	 * @param relativePath
	 * @param extention
	 * @return
	 */
	private static boolean extentionMatch(String relativePath, String extention) {
		return relativePath.endsWith(extention);
	}

	/**
	 * 指定した拡張子を持っているか判定する。
	 *
	 * @param content
	 * @param extention
	 * @return
	 */
	private static boolean extentionMath(File content, String extention) {
		return content.isFile() && content.getName().endsWith(extention);
	}

	/**
	 * ルートURLと相対パスよりURLを作成する
	 *
	 * @param rootURL
	 * @param relativePath
	 * @return 相対パス
	 * @throws MalformedURLException
	 */
	public static URL createRelative(URL rootURL, String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new URL(rootURL, relativePath);
	}

	/**
	 * Equinox OSGiの"bundleresource:"、"bundleentry:" 形式のURLを標準jarファイルのURLに変換する
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	protected static URL resolveBundleResource(URL url) {
		if (equinoxResolveMethod != null) {
			if (url.getProtocol().startsWith("bundle")) {

				try {
					Object returnValue = equinoxResolveMethod.invoke(null, url);
					return (URL) returnValue;
				} catch (InvocationTargetException e) {
					throw (IllegalStateException) new IllegalStateException().initCause(e.getCause());
				} catch (Exception e) {
					throw (IllegalStateException) new IllegalStateException().initCause(e);
				}
			}
		}
		return url;
	}

	/**
	 * パッケージ名をリソースパスに変換する
	 *
	 * @param packageName
	 * @return リソースパス
	 */
	public static String packageToResourcePath(String packageName) {
		return packageName.replace('.', '/') + "/";
	}

	/**
	 * jarファイルのURLか判定する。
	 *
	 * @param url
	 * @return 引数のurlがjarファイルを指す場合<code>true</code>
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) ||
				URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_WSJAR.equals(protocol) ||
				(URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
	}

	/**
	 * VFSファイルのURLか判定する。
	 *
	 * @param url
	 * @return 引数のURLがVFSファイルを指す場合<code>true</code>
	 */
	public static boolean isVFSURL(URL url) {
		return url.getProtocol().startsWith(URL_PROTOCOL_VFS);
	}

	/**
	 * JARファイルを取得する。
	 *
	 * @param jarFileUrl
	 * @return
	 * @throws IOException
	 */
	protected static JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			try {
				return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// URLが無効な場合の保険（ほぼ起こらないはず）
				return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	/**
	 * URLをURIに変換する
	 *
	 * @param url
	 * @return URLから作成されたURI
	 * @throws URISyntaxException
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * StringをURIに変換する
	 *
	 * @param location
	 * @return 引数の文字列から作成されたURI
	 * @throws URISyntaxException
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(location.replace(" ", "%20"));
	}

	/**
	 * パス内のファイルシステムセパレータをスラッシュに変換する。
	 *
	 * @param path
	 * @return セパレーターをスラッシュに変換したパス
	 */
	public static String separatorToSlash(String path) {
		if (path == null || path.length() == 0) {
			return path;
		}
		return path.replace(File.separator, "/");
	}

}
