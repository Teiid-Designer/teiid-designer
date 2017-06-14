/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v8;

import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;

public class ResultCallback {
	
	void onSuccess(ModelNode outcome, ModelNode result) throws AdminException {
	    // Nothinq required
	}
	void onFailure(String msg) throws AdminProcessingException {
		throw new AdminProcessingException(msg);
	}
}
