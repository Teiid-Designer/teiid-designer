/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.results.view;

import org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewer;
import org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewerProvider;

public class TeiidResultViewerProvider extends ExternalResultSetViewerProvider {

	@Override
	public void configureViewer() {
		tableViewer = new TeiidResultSetViewer(parentComposite, tableStyle, resultInstance,
				resultSetObject, showRowCount, resultViewControl);
		menuManager = ((ExternalResultSetViewer)tableViewer).getMenuManager();
	}

}
