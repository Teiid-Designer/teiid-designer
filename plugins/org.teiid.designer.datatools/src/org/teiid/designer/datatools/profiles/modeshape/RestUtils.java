package org.teiid.designer.datatools.profiles.modeshape;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.metamatrix.core.util.StringUtilities;

public class RestUtils {

	public List<String> getRepositoryList(URL url, String user, String pass)
			throws Exception {
		Authenticator basicAuth = new BasicAuthenticator(user, pass);
		Authenticator.setDefault(basicAuth);
		String jsonResponse = getRepositories(url);
		Authenticator.setDefault(null);
		return parseRepositories(jsonResponse);

	}

	private static String getRepositories(URL url) throws Exception {
		String result = StringUtilities.SPACE;
		URLConnection conn;
		conn = url.openConnection();

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		result = sb.toString();
		return result;
	}

	private static List<String> parseRepositories(String jsonResponse)
			throws Exception {
		List<String> result = new ArrayList<String>();
		if (!jsonResponse.isEmpty()) {
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser jp = jsonFactory.createJsonParser(jsonResponse);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jp);
			for (JsonNode reposNode : rootNode) {
				result.add(reposNode.path("repository").path("name") //$NON-NLS-1$ //$NON-NLS-2$
						.getTextValue());
			}
		}
		return result;
	}

	private class BasicAuthenticator extends Authenticator {

		private String username;
		private String password;

		public BasicAuthenticator(String user, String pass) {
			username = user;
			password = pass;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password.toCharArray());
		}

	}

}
