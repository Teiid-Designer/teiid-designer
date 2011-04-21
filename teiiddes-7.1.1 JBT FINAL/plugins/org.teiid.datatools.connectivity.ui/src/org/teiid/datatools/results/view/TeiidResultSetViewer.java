/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.results.view;

import org.eclipse.datatools.sqltools.result.IResultSetObject;
import org.eclipse.datatools.sqltools.result.model.IResultInstance;
import org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewer;
import org.eclipse.datatools.sqltools.result.ui.ResultsViewUIPlugin;
import org.eclipse.datatools.sqltools.result.ui.view.ResultsViewControl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;


public class TeiidResultSetViewer extends ExternalResultSetViewer {

	public TeiidResultSetViewer(Composite parent, int style,
			IResultInstance instance, IResultSetObject result,
			boolean showRowCount, ResultsViewControl resultsViewControl) {
		
		super(parent, style, instance, result, showRowCount, resultsViewControl);
		IPreferenceStore store = ResultsViewUIPlugin.getDefault().getPreferenceStore();
		store.setValue("org.eclipse.datatools.sqltools.result.ResultsFilterDialog.unknownProfile", "true"); //$NON-NLS-1$ //$NON-NLS-2$
	} 
	
    
}