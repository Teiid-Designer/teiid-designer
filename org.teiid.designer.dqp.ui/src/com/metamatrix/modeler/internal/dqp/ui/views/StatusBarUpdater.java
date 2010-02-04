/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * StatusBarUpdater
 */
public class StatusBarUpdater implements DqpUiConstants,
                                         ISelectionChangedListener {

    private IStatusLineManager statusLineManager;

    /**
     * Construct an instance of StatusBarUpdater.
     * 
     */
    public StatusBarUpdater(IStatusLineManager statusLineManager) {
        this.statusLineManager= statusLineManager;
    }
        
    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        String statusBarMessage= formatMessage(selection);
        statusLineManager.setMessage(statusBarMessage);
    }
    
    
    protected String formatMessage(ISelection sel) {
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            IStructuredSelection selection= (IStructuredSelection) sel;
            
            int nElements= selection.size();
            if (nElements > 1) {
                return UTIL.getString("StatusBarUpdater.num_elements_selected", String.valueOf(nElements)); //$NON-NLS-1$
            } 
            Object elem= selection.getFirstElement();
            if (elem instanceof ConnectorBinding) {
                return ((ConnectorBinding) elem).getFullName();
            } else if (elem instanceof ComponentType) {
                return ((ComponentType) elem).getFullName();
            }
            
            return elem.getClass().getName();
        }
        return "";  //$NON-NLS-1$
    }

}
