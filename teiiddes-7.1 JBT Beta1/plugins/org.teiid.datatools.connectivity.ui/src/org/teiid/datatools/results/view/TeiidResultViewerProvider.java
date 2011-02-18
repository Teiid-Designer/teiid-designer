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
