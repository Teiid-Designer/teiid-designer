/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.ui.product.IVetoableShutdownListener;


/**
 * The <code>DqpUiShutdownListener</code> class ensures the at shutdown, the SqlExplorer plugin is told to
 * close any connections and to remove the perpective from the session. This fixes Defect 21949 and insures that 
 * restarting Dimension will never display the SQL Explorer perspective on TOP
 * @since 5.0
 */
public class DqpUiShutdownListener implements
                                  IVetoableShutdownListener {
    private static final String SQL_EXPLORER_PLUGIN_ID = "JdbcClientPluginPerspective";//$NON-NLS-1$
    private IWorkbenchWindow window;
    
    /** 
     * 
     * @since 5.0
     */
    public DqpUiShutdownListener() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.ui.product.IVetoableShutdownListener#continueShutdown()
     * @since 5.0
     */
    public boolean continueShutdown() {
        DqpUiPlugin.getDefault().getVdbConnectionMgr().closeAllConnections();
        if( this.window != null ) {
            // if no perspectives are open there will not be an active page
            if (this.window.getActivePage() != null) {
                IPerspectiveDescriptor[] perspectives = this.window.getActivePage().getOpenPerspectives();
                for( int i=0; i<perspectives.length; i++ ) {
                    if( perspectives[i].getId().equalsIgnoreCase(SQL_EXPLORER_PLUGIN_ID)) {
                        this.window.getActivePage().closePerspective(perspectives[i], false, false);
                        break;
                    }
                }
            }
        }
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.ui.product.IVetoableShutdownListener#setWindow(org.eclipse.ui.IWorkbenchWindow)
     * @since 5.0
     */
    public void setWindow(IWorkbenchWindow theWindow) {
        this.window = theWindow;
    }

}
