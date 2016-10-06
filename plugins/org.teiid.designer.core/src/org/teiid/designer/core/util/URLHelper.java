/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.teiid.core.designer.util.Base64;

/**
 * Helper class to build the URL objects from the strings. Since as an
 * application we do not know if we are embedded or we are in our own server, we
 * can not install the "URLStreamHandlers" in the VM, as they can be only
 * installed once per VM, as an alternative, the stream handler must be
 * specified at the time URL it is constructed. This class will help us to this
 * code at one place. Here inspect the given string and build the correct type
 * of URL with correct handler.
 * 
 * @since 8.0
 */
public class URLHelper {

	/**
	 * Construct the URL based on the String
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @since 4.4
	 */
	public static URL buildURL(String url) throws MalformedURLException {

		if (url == null)
			throw new MalformedURLException();

		url = convertBackSlashes(url);

		final String filename = extractFileName(url);
		if (filename != null) {
			return new File(url).toURI().toURL();
		}
		return new URL(url);
	}

	public static URL buildURL(final URL url) {
		try {
			return buildURL(url.toExternalForm());
		} catch (final MalformedURLException e) {
			// since it came as url it should not have any issues with this
		}
		return null;
	}

	static String convertBackSlashes(final String str) {
		return str.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Given an {@link URLConnection}, set its authorization property using the
	 * given username and password.
	 * 
	 * @param urlConn
	 * @param userName
	 * @param password
	 */
	public static void setCredentials(URLConnection urlConn, String userName, String password) {
		if (userName == null || password == null)
			return;

		if (!(urlConn instanceof HttpURLConnection)) {
			// Should not be authenticating on other types of url connection
			return;
		}

		urlConn.setRequestProperty("Authorization", //$NON-NLS-1$
				"Basic " + Base64.encodeBytes((userName + ':' + password).getBytes())); //$NON-NLS-1$
	}

	/**
	 * Build a {@link java.io.File} from a {@link java.net.URL} object.
	 * 
	 * @param url
	 * @param fileNamePrefix
	 * @param fileNameSuffix
	 * @return File
	 * @since 5.1
	 */
	public static File createFileFromUrl(final URL url, final String fileNamePrefix, final String fileNameSuffix)
			throws MalformedURLException, IOException {

		return createFileFromUrlInternal(url, FileUrl.createTempFile(fileNamePrefix, fileNameSuffix), null, null, true);
	}

	/**
	 * Build a {@link java.io.File} from a {@link java.net.URL} object.
	 * 
	 * @param url
	 * @param fileNamePrefix
	 * @param fileNameSuffix
	 * @return File
	 * @since 5.1
	 */
	public static File createFileFromUrlwithDigest(final URL url, final String fileNamePrefix,
			final String fileNameSuffix) throws MalformedURLException, IOException {

		return createFileFromUrlInternalwithDigest(url, FileUrl.createTempFile(fileNamePrefix, fileNameSuffix), null,
				null, true);
	}

	public static File createFileFromUrl(final URL url, final String fileNamePrefix, final String fileNameSuffix,
			final String userName, final String password) throws MalformedURLException, IOException {

		return createFileFromUrl(url, FileUrl.createTempFile(fileNamePrefix, fileNameSuffix).getAbsolutePath(),
				userName, password, true);
	}

	/**
	 * Download the content from the given URL and save it into the specified
	 * file.
	 * 
	 * @param url
	 *            URL of the file to be saved
	 * @param filePath
	 *            the full path of the file name
	 * @param userName
	 *            user name if authentication is required
	 * @param password
	 *            password if authentication is required
	 * @param verifyHostname
	 *            whether to verify hostname for HTTPS connection
	 * @return the file created
	 * @throws MalformedURLException
	 * @throws IOException
	 * @since 5.5
	 */
	public static File createFileFromUrl(final URL url, final String filePath, final String userName,
			final String password, final boolean verifyHostname) throws MalformedURLException, IOException {
		File file = null;
		final String tempDir = System.getProperty("java.io.tmpdir");//$NON-NLS-1$
		if (filePath.indexOf("/") != -1 || filePath.indexOf("\\") != -1) {//$NON-NLS-1$//$NON-NLS-2$

			int lastPart = filePath.lastIndexOf("/");//$NON-NLS-1$
			if (lastPart == -1)
				lastPart = filePath.lastIndexOf("\\");//$NON-NLS-1$
			final String relativeDir = filePath.substring(0, lastPart);
			final File dir = new File(new File(tempDir), relativeDir);
			if (!dir.exists())
				dir.mkdirs();
			file = new File(dir, filePath.substring(lastPart + 1));
		} else
			file = new File(new File(tempDir), filePath);
		return createFileFromUrlInternal(url, new FileUrl(file.toURI()), userName, password, verifyHostname);
	}

	private static File createFileFromUrlInternal(final URL url, final File file, final String userName,
			final String password, final boolean verifyHostname) throws MalformedURLException, IOException {
		URLConnection urlConn = null;
		InputStreamReader inStream = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.deleteOnExit();
			((FileUrl) file).setOriginalUrlString(url.toString());
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			urlConn = url.openConnection();
			setCredentials(urlConn, userName, password);
			if (!verifyHostname && urlConn instanceof HttpsURLConnection)
				((HttpsURLConnection) urlConn).setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(final String arg, final SSLSession session) {
						return true;
					}
				});
			inStream = new InputStreamReader(urlConn.getInputStream());
			int c;
			while ((c = inStream.read()) != -1) {
				bw.write(c);
			}

		} finally {
			if (inStream != null)
				inStream.close();
			if (bw != null)
				bw.close();
		}

		return file;
	}

	private static File createFileFromUrlInternalwithDigest(final URL url, final File file, final String userName,
			final String password, final boolean verifyHostname) throws MalformedURLException, IOException {
		HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials(userName, password));
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		try {

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate DIGEST scheme object, initialize it and add it to the
			// local
			// auth cache
			DigestScheme digestAuth = new DigestScheme();
			// Suppose we already know the realm name
			// digestAuth.overrideParamter("realm", "some realm");
			// Suppose we already know the expected nonce value
			// digestAuth.overrideParamter("nonce", "whatever");
			authCache.put(target, digestAuth);

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet(url.toString());

			System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
			for (int i = 0; i < 3; i++) {
				CloseableHttpResponse response = httpclient.execute(httpget, localContext);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					HttpEntity entity = response.getEntity();
					InputStream instream = entity.getContent();
					// Header contentCncoding = entity .getContentEncoding();
					AuthState proxyAuthState = localContext.getProxyAuthState();
					System.out.println("Proxy auth state: " + proxyAuthState.getState());
					System.out.println("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
					System.out.println("Proxy auth credentials: " + proxyAuthState.getCredentials());
					AuthState targetAuthState = localContext.getTargetAuthState();
					System.out.println("Target auth state: " + targetAuthState.getState());
					System.out.println("Target auth scheme: " + targetAuthState.getAuthScheme());
					System.out.println("Target auth credentials: " + targetAuthState.getCredentials());
					EntityUtils.consume(response.getEntity());
				} finally {
					response.close();
				}
			}
		} finally {
			httpclient.close();
		}
		// URLConnection urlConn = null;
		// InputStreamReader inStream = null;
		// FileWriter fw = null;
		// BufferedWriter bw = null;
		// try {
		// file.deleteOnExit();
		// ((FileUrl) file).setOriginalUrlString(url.toString());
		// fw = new FileWriter(file);
		// bw = new BufferedWriter(fw);
		// urlConn = url.openConnection();
		// setCredentials(urlConn, userName, password);
		// if (!verifyHostname && urlConn instanceof HttpsURLConnection)
		// ((HttpsURLConnection) urlConn).setHostnameVerifier(new
		// HostnameVerifier() {
		// @Override
		// public boolean verify(final String arg, final SSLSession session) {
		// return true;
		// }
		// });
		// inStream = new InputStreamReader(urlConn.getInputStream());
		// int c;
		// while ((c = inStream.read()) != -1) {
		// bw.write(c);
		// }
		//
		// } finally {
		// if (inStream != null)
		// inStream.close();
		// if (bw != null)
		// bw.close();
		// }

		return file;
	}

	private HashMap<String, String> parseHeader(String headerString) {
		// seperte out the part of the string which tells you which Auth scheme
		// is it
		String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();
		HashMap<String, String> values = new HashMap<String, String>();
		String keyValueArray[] = headerStringWithoutScheme.split(",");
		for (String keyval : keyValueArray) {
			if (keyval.contains("=")) {
				String key = keyval.substring(0, keyval.indexOf("="));
				String value = keyval.substring(keyval.indexOf("=") + 1);
				values.put(key.trim(), value.replaceAll("\"", "").trim());
			}
		}
		return values;
	}

	static String extractFileName(String file) {

		if (file.matches("^(\\w){2,}:.*")) // Handles URLs - No //$NON-NLS-1$
											// conversion necessary
			// http://lib/foo.txt - currently do not support, converts to local
			// host with absolute path
			// file://lib/foo.txt
			// file:///c:/lib/foo.txt
			return null;
		else if (file.matches("^\\/.*")) // Handles absolute //$NON-NLS-1$
											// paths- it can be file or URL
											// depending upon
			// context Conversion needed
			// /lib/foo.txt
			return file;
		else if (file.matches("^\\w:[\\\\,\\/].*")) { //$NON-NLS-1$
			// Handles windows absolute path - no conversion needed
			// c:\\lib\\foo.txt
			// c:/lib.foo.txt
			file = file.replaceAll("\\\\", "\\/"); //$NON-NLS-1$ //$NON-NLS-2$
			return "/" + file; //$NON-NLS-1$
		} else if (file.matches("^(\\.)+\\/.*|^\\w+\\/.*|^\\w+.*")) // Handles //$NON-NLS-1$
																	// relative
																	// paths -
																	// these can
																	// be URLs
																	// or files
																	// -
			// conversion necessary
			// ./lib/foo.txt
			// ../lib/foo.txt
			// lib/foo.txt
			return file;
		return null;
	}

	/**
	 * Determines whether a URL object resolves to a valid url. This will work
	 * for any protocol (file, HTTP, etc.).
	 * 
	 * @param url
	 * @return resolved boolean
	 * @throws MalformedURLException,
	 *             IOException
	 * @since 5.1
	 */
	public static boolean resolveUrl(final URL url) throws MalformedURLException, IOException {
		return resolveUrl(url, true);
	}

	/**
	 * Determines whether a URL object resolves to a valid url. This will work
	 * for any protocol (file, HTTP, etc.).
	 * 
	 * @param url
	 * @param verifyHostname
	 *            whether to verify hostname for HTTPS connection
	 * @return resolved boolean
	 * @throws MalformedURLException,
	 *             IOException
	 * @since 5.5
	 */
	public static boolean resolveUrl(final URL url, final boolean verifyHostname)
			throws MalformedURLException, IOException {
		return resolveUrl(url, null, null, null, verifyHostname);
	}

	/**
	 * Determines whether a URL object resolves to a valid url. This will work
	 * for any protocol (file, HTTP, etc.).
	 * 
	 * @param url
	 * @param userName
	 * @param password
	 * @param connRequestPropMap
	 *            Map of Connection RequestProperties
	 * @param verifyHostname
	 *            whether to verify hostname for HTTPS connection
	 * @return resolved boolean
	 * @throws MalformedURLException,
	 *             IOException
	 * @since 5.1
	 */
	public static boolean resolveUrl(final URL url, final String userName, final String password,
			final Map<String, String> connRequestPropMap, final boolean verifyHostname)
			throws MalformedURLException, IOException {
		boolean resolved = true;
		if (url == null)
			return resolved;
		String nextLine;
		URLConnection urlConn = null;
		InputStreamReader inStream = null;
		BufferedReader buff = null;
		// Add a time-out here....
		final long timeOut = 30000;

		final long startTime = System.currentTimeMillis();
		long deltaTime = 0;
		try {
			urlConn = url.openConnection();
			setCredentials(urlConn, userName, password);
			if (!verifyHostname && urlConn instanceof HttpsURLConnection)
				((HttpsURLConnection) urlConn).setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(final String arg, final SSLSession session) {
						return true;
					}
				});
			// Set any request properties
			urlConn.setDoInput(true);
			if (connRequestPropMap != null) {
				for (String propName : connRequestPropMap.keySet()) {
					urlConn.setRequestProperty(propName, connRequestPropMap.get(propName));
				}
			}

			inStream = new InputStreamReader(urlConn.getInputStream());
			buff = new BufferedReader(inStream);
			boolean keepReading = true;
			// Read and print the lines from index.html
			while (keepReading) {
				nextLine = buff.readLine();
				if (nextLine != null) {

				} else
					break;
				deltaTime = System.currentTimeMillis() - startTime;
				if (deltaTime > timeOut) {
					keepReading = false;
					resolved = false;
				}
			}
		} finally {
			if (inStream != null)
				inStream.close();
		}

		return resolved;
	}

	/**
	 * Determines whether a URL object resolves to a valid url. This will work
	 * for any protocol (file, HTTP, etc.).
	 * 
	 * @param url
	 * @param userName
	 * @param password
	 * @param connRequestPropMap
	 *            Map of Connection RequestProperties
	 * @param verifyHostname
	 *            whether to verify hostname for HTTPS connection
	 * @return resolved boolean
	 * @throws Exception 
	 * @since 5.1
	 */
	public static boolean resolveUrlWithDigest(final URL url, final String userName, final String password,
			final Map<String, String> connRequestPropMap, final boolean verifyHostname)
			throws Exception {
		boolean resolved = true;
		if (url == null)
			return resolved;
		resolved = resolveDigest(url, userName, password, resolved);
		return resolved;
	}

	/**
	 * @param url
	 * @param userName
	 * @param password
	 * @param resolved
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private static boolean resolveDigest(final URL url, final String userName, final String password, boolean resolved)
			throws IOException, ClientProtocolException, Exception {
		HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials(userName, password));
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		try {

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate DIGEST scheme object, initialize it and add it to the
			// local
			// auth cache
			DigestScheme digestAuth = new DigestScheme();
			authCache.put(target, digestAuth);

			// Create and add a CookieStore in case the service requires a
			// cookie (and who doesn't require a cookie? Mmmm.. cookies).
			CookieStore cookieStore = new BasicCookieStore();
			httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
					.setDefaultCredentialsProvider(credsProvider).build();

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet(url.toString());

			CloseableHttpResponse response = httpclient.execute(httpget, localContext);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				if (response.getStatusLine().getStatusCode()!=200){
					resolved = false;
					throw new Exception(response.getStatusLine().getReasonPhrase());
				}
				HttpEntity entity = response.getEntity();
				AuthState proxyAuthState = localContext.getProxyAuthState();
				AuthState targetAuthState = localContext.getTargetAuthState();
				EntityUtils.consume(response.getEntity());
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return resolved;
	}
	
	/**
	 * @param url
	 * @param userName
	 * @param password
	 * @param resolved
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	public static InputStream getWSDLWithDigest(final URL url, final String userName, final String password)
			throws IOException, ClientProtocolException, Exception {
		HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials(userName, password));
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		CloseableHttpResponse response = null;
		try {

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate DIGEST scheme object, initialize it and add it to the
			// local
			// auth cache
			DigestScheme digestAuth = new DigestScheme();
			authCache.put(target, digestAuth);

			// Create and add a CookieStore in case the service requires a
			// cookie (and who doesn't require a cookie? Mmmm.. cookies).
			CookieStore cookieStore = new BasicCookieStore();
			httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
					.setDefaultCredentialsProvider(credsProvider).build();

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet(url.toString());

			response = httpclient.execute(httpget, localContext);
			try {
				if (response.getStatusLine().getStatusCode()!=200){
					throw new Exception(response.getStatusLine().getReasonPhrase());
				}
				HttpEntity entity = response.getEntity();
				AuthState proxyAuthState = localContext.getProxyAuthState();
				AuthState targetAuthState = localContext.getTargetAuthState();
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return response.getEntity().getContent();
	}
}
