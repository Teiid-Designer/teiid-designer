/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.view;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;

public class TableOrViewSelectionValidator implements ISelectionStatusValidator {

    private final static IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);

    private final static String NOTHING_SELECTED = UiConstants.Util.getString(
            "ModelResourceSelectionValidator.noSelection"); //$NON-NLS-1$
    private final static String MUST_SELECT_TABLE_OR_VIEW = 
    		UiConstants.Util.getString("TableOrViewSelectionValidator.invalidSelectionMessage"); //$NON-NLS-1$

    /**
     * Construct an instance of ModelResourceMetamodelValidator that will only pass
     * selections of metamodel files.
     */
    public TableOrViewSelectionValidator() {
    	super();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    @Override
	public IStatus validate(Object[] selection) {
        IStatus result = OK_STATUS; 
        
        if ( selection == null || selection.length == 0 ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }

        if ( selection.length == 1 && (selection[0] instanceof BaseTable ||  selection[0] instanceof View) ) {
            return result;
        } else {
        	return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, MUST_SELECT_TABLE_OR_VIEW);
        }
    }
    



}
