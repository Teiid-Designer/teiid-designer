package org.teiid.datatools.results.view;

import java.util.prefs.Preferences;

import org.eclipse.core.internal.preferences.InstancePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.datatools.sqltools.result.IResultSetObject;
import org.eclipse.datatools.sqltools.result.model.IResultInstance;
import org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewer;
import org.eclipse.datatools.sqltools.result.ui.ResultsViewUIPlugin;
import org.eclipse.datatools.sqltools.result.ui.view.ResultsViewControl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;


public class TeiidResultSetViewer extends ExternalResultSetViewer {

	public TeiidResultSetViewer(Composite parent, int style,
			IResultInstance instance, IResultSetObject result,
			boolean showRowCount, ResultsViewControl resultsViewControl) {
		
		super(parent, style, instance, result, showRowCount, resultsViewControl);
		IPreferenceStore store = ResultsViewUIPlugin.getDefault().getPreferenceStore();
		store.setValue("org.eclipse.datatools.sqltools.result.ResultsFilterDialog.unknownProfile", "true");
		//try setting this in the preview action
		//resultsViewControl.
		//InstancePreferences nodeCore =  (InstancePreferences) new InstanceScope().getNode("org.eclipse.datatools.sqltools.result");
		//if (nodeCore != null) {
		  //nodeCore.put("ResultsFilterDialog.unknownProfile", "true");
		  //nodeCore.put("org.eclipse.jdt.core.compiler.compliance", "1.3");
		  //nodeCore.put("org.eclipse.jdt.core.compiler.problem.assertIdentifier", 
		//"ignore");
		 // nodeCore.put("org.eclipse.jdt.core.compiler.problem.enumIdentifier", 
		//"ignore");
		//}

		//PropertyChangeEvent event = new PropertyChangeEvent(this, "org.eclipse.datatools.sqltools.result.ResultsFilterDialog.unknownProfile", false, true);
		//resultsViewControl.propertyChange(event);
	} 

}