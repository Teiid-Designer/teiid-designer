package org.teiid.designer.datatools.profiles.modeshape;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * @since 8.0
 */
public class RestUtils {

	/**
	 * @param url the ModeShape server URL
	 * @param user the user ID
	 * @param pass the password
	 * @return a collection of repository names (never <code>null</code>)
	 * @throws Exception if there is a problem obtaining the repository names
	 */
	public List<String> getRepositoryList(URL url, String user, String pass)
			throws Exception {
		Authenticator basicAuth = new BasicAuthenticator(user, pass);
		Authenticator.setDefault(basicAuth);
		String jsonResponse = getRepositories(url);
		Authenticator.setDefault(null);
		return parseRepositories(jsonResponse);

	}

	private static String getRepositories(URL url) throws Exception {
		URLConnection conn;
		conn = url.openConnection();

		 // make sure we get a JSON response
		conn.setRequestProperty("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		return sb.toString();
	}

    private static List<String> parseRepositories(final String jsonResponse) throws Exception {
        if (!jsonResponse.isEmpty()) {
            final JsonFactory jsonFactory = new JsonFactory();
            final JsonParser jp = jsonFactory.createJsonParser(jsonResponse);
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(jp);

            // should only get one repositories node back
            if ((rootNode != null) && (rootNode.size() == 1)) {
                final JsonNode repositoriesNode = rootNode.iterator().next();
                final List<String> result = new ArrayList<String>(repositoriesNode.size());

                for (final JsonNode repoNode : repositoriesNode) {
                    result.add(repoNode.path("name").getTextValue()); //$NON-NLS-1$
                }

                return result;
            }
        }

        return Collections.emptyList();
    }

	private class BasicAuthenticator extends Authenticator {

		private String username;
		private String password;

		public BasicAuthenticator(String user, String pass) {
			username = user;
			password = pass;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password.toCharArray());
		}

	}

}
