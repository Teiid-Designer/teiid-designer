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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

public class TeiidResultViewerProvider extends ExternalResultSetViewerProvider {

    private boolean splitterSet = false;
    private static int LHS_WEIGHT = 1;
    private static int RHS_WEIGHT = 6;

	@Override
	public void configureViewer() {
		// The Results View splitter will be initially to maximize the RHS Results Table. If the user resets the splitter, the resize will 'stick'.
		// However, if the resultsView is closed and re-opened, the splitter will go back to the original weighting.
		if(!splitterSet) {
		    splitterSet = true;
		    Composite sashForm = resultViewControl.getControl();       
		    if(sashForm!=null && sashForm instanceof SashForm) {
		        int[] weights = new int[]{LHS_WEIGHT,RHS_WEIGHT};
		        ((SashForm)sashForm).setWeights(weights);
		        
                // flag is reset if resultsView is closed
		        sashForm.addDisposeListener(new DisposeListener() {
		            @Override
                    public void widgetDisposed(DisposeEvent event) {
		                splitterSet = false;
		            }
		        });
		    }
		}
	        		
        tableViewer = new TeiidResultSetViewer(parentComposite, tableStyle, resultInstance,
                                               resultSetObject, showRowCount, resultViewControl);

		menuManager = ((ExternalResultSetViewer)tableViewer).getMenuManager();
	}
	
}
