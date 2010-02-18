/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.teiid.adminapi.ConnectorBinding;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.CloneConnectorBindingDialog;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class CloneConnectorBindingAction extends ConfigurationManagerAction {

    /** 
     * 
     * @since 5.0
     */
    public CloneConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString("CloneConnectorBindingAction.label")); //$NON-NLS-1$
    }
    
    /**
     *  
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        //System.out.println("  CloneConnectorBindingAction.run()   ====>>> ");
        // Get Selection
        ConnectorBinding theBinding = (ConnectorBinding)getSelectedObject();
        
        if( theBinding != null ) {
            try {
                theBinding = DqpPlugin.getInstance().getWorkspaceConfig().
                cloneConnectorBinding(theBinding, 
                                      generateUniqueBindingName(theBinding.getName()), 
                                      false);
                CloneConnectorBindingDialog dialog = new CloneConnectorBindingDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), theBinding) {
                    
                    /** 
                     * @see com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog#close()
                     * @since 5.5.3
                     */
                    @Override
                    public boolean close() {
                        if (getReturnCode() == Window.OK) {
                            ConnectorBinding newBinding = getNewConnectorBinding();
                            if( newBinding != null ) {
                                //System.out.println("  NewConnectorBindingAction.run() NEW BINDING = " + newBinding.getName());
                                try {
                                    getAdmin().addConnectorBinding(newBinding, getNewConnectorBindingName());
                                } catch (Exception error) {
                                    DqpUiPlugin.showErrorDialog(getShell(), error);
                                    return false;
                                }
                            }
                        }
                        return super.close();
                    }
                };
    
                dialog.open();
            } catch (final Exception error) {
                UiUtil.runInSwtThread(new Runnable() {

                    public void run() {
                        DqpUiPlugin.showErrorDialog(Display.getCurrent().getActiveShell(), error);
                    }
                }, false);
            }
        }
    }
    
    private String generateUniqueBindingName(String originalName) {
        String proposedName = originalName;

        boolean validName = false;
        int iVersion = 1;
        
        while(!validName) { 
            
            if (!ModelerDqpUtils.isUniqueBindingName(proposedName)) {
                proposedName = originalName + "_" + iVersion; //$NON-NLS-1$
                iVersion++;
            } else {
                validName = true;
            }
        }
        
        return proposedName;
    }

    /**
     *  
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = false;
        if( !isMultiSelection() && !isEmptySelection() ) {
            Object selectedObject = getSelectedObject();
            if( selectedObject instanceof ConnectorBinding) {
                result = true;
            }
        }
        
        setEnabled(result);
    }
}
