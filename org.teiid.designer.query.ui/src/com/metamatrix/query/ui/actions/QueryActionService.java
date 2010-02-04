/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui.actions;

//import org.eclipse.jface.action.IAction;
//import org.eclipse.ui.IFileEditorInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;

/**
 * The <code>QueryActionService</code> class is the Modeler Plugin's action service. It is responsible for
 * managing all actions for this plugin.
 */
public final class QueryActionService extends AbstractActionService 
                                      implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>QueryActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * @param theWindow the associated workbench window
     */
    public QueryActionService(IWorkbenchPage page) {
        super(UiPlugin.getDefault(), page);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#getDefaultAction(java.lang.String)
     */
    public IAction getDefaultAction(String theActionId) {
        return null;
    }

    
}
