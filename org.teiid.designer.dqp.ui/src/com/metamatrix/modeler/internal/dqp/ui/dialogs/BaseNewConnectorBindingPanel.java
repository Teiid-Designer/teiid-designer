/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/** 
 * @since 4.3
 */
public abstract class BaseNewConnectorBindingPanel extends Composite
                                                   implements DqpUiConstants,
                                                              IChangeNotifier {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates an <code>IStatus</code> using the specified severity and message. 
     * @param theSeverity the status severity
     * @param theMessage the message
     * @return
     * @since 4.3
     */
    protected static IStatus createStatus(int theSeverity,
                                          String theMessage) {
        return new Status(theSeverity, PLUGIN_ID, IStatus.OK, theMessage, null);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected ListenerList changeListeners;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public BaseNewConnectorBindingPanel(Composite theParent) {
        super(theParent, SWT.NONE);
        this.changeListeners = new ListenerList(ListenerList.IDENTITY);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener(IChangeListener theListener) {
        this.changeListeners.add(theListener);
    }
    
    /**
     * Notifies all registered listeners of a state change. 
     * @since 4.3
     */
    protected void fireChangeEvent() {
        Object[] listeners = this.changeListeners.getListeners();
        
        for (int i = 0; i < listeners.length; ++i) {
            ((IChangeListener)listeners[i]).stateChanged(this);
        }
    }
    
    /**
     * Obtains the currently selected binding. 
     * @return the binding or <code>null</code>
     * @since 4.3
     */
    public abstract ConnectorBinding getConnectorBinding() throws Exception;

    /**
     * Obtains the properties key prefix. 
     * @return the prefix
     * @since 4.3
     */
    protected abstract String getI18nPrefix();
    
    /**
     * Obtains the current status. 
     * @return the status
     * @since 4.3
     */
    protected abstract IStatus getStatus();
    
    /**
     * Obtains a localized title. 
     * @return the panel title
     * @since 4.3
     */
    protected String getTitle() {
        return UTIL.getStringOrKey(getI18nPrefix() + "title"); //$NON-NLS-1$
    }
    
    /**
     * Obtains a properties value for the specified key. Uses the I8N key prefix. 
     * @param theKey the key whose value is being requested.
     * @return
     * @since 4.3
     * @see #getString(String)
     */
    protected String getString(String theKey) {
        return UTIL.getStringOrKey(getI18nPrefix() + theKey);
    }
    
    /** 
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener(IChangeListener theListener) {
        this.changeListeners.remove(theListener);
    }
    
}
