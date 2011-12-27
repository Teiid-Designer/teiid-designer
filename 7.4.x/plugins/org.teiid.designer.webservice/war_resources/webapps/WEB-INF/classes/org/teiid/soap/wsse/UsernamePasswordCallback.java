
package org.teiid.soap.wsse;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;;

public class UsernamePasswordCallback implements CallbackHandler {

	
	/*
	 * This class should be updated to use implementation specific authentication. it is
	 * not recommended to use for production as-is.
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		if (!("${username}".equals(pc.getIdentifier()) && "${password}".equals(pc //$NON-NLS-1$ //$NON-NLS-2$
				.getPassword())))
			throw new SecurityException("User '" + pc.getIdentifier() //$NON-NLS-1$
					+ "' with password '" + pc.getPassword() + "' not allowed.");  //$NON-NLS-1$//$NON-NLS-2$

	}
}
