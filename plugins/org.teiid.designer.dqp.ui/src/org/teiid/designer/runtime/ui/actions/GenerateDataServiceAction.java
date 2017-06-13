/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.preview.GenerateDataServiceWorker;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;

public class GenerateDataServiceAction  extends SortableSelectionAction implements DqpUiConstants {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(GenerateDataServiceAction.class);

    static String getString( String key ) {
        return UTIL.getString(THIS_CLASS + key);
    }
	
    GenerateDataServiceWorker worker;
    
    /**
     * @since 5.0
     */
    public GenerateDataServiceAction() {
        super(getString("label"), SWT.DEFAULT); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.GENERATE_DATA_SERVICE));
        setId(ModelActionConstants.Special.GENERATE_DATA_SERVICE);
        
        worker = new GenerateDataServiceWorker();
    }

    /**
    *
    */
   @Override
   public boolean isApplicable( ISelection selection ) {
       return isValidSelection(selection);
   }
    
   /**
    * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
    * with all Tables and Procedures contained within the current selection.
    * 
    * @return
    * @since 4.1
    */
   @Override
   protected boolean isValidSelection( ISelection selection ) {

       // must have one and only one EObject selected
       EObject eObj = SelectionUtilities.getSelectedEObject(selection);
       if (eObj == null) return false;
       
       return worker.isPreviewableEObject(eObj);

   }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {

        
    	EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
    	if( eObj != null) {
            worker.run(eObj);
    	}
    }
}
