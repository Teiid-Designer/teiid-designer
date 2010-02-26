/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5.3
 */
public class ConnectorBindingImportPostProcessor implements
                                                DqpUiConstants,
                                                IJdbcImportPostProcessor {
	
	boolean useDefaultConnectorType = false;

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @param modelName the name of the model the connector binding is being created for
     * @param jdbcSource the JdbcSource object being used during the JDBC import
     * @return the new binding or <code>null</code>
     * @throws Exception if there is a problem creating the connector binding
     * @since 6.0.0
     */
    private Connector createConnector( String modelName,
                                                     JdbcSource jdbcSource ) throws Exception {
        ConnectorType bindingType = null;
        WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getInstance().getWorkspaceConfig();
        final String bindingName = wsConfigMgr.createConnectorBindingName(modelName);
        final Collection<ConnectorType> bindingTypeMatches = wsConfigMgr.findMatchingConnectorTypes(jdbcSource, false);
        int numTypesFound = bindingTypeMatches.size();

        if (numTypesFound == 1) {
            bindingType = bindingTypeMatches.iterator().next();
        } else if (numTypesFound > 1) {
            // ask the user to pick which matching type they want to use which will be set into this array
            final ConnectorType[] chosenType = new ConnectorType[1];

            // make sure to show dialog in UI thread
            UiUtil.runInSwtThread(new Runnable() {

                @Override
                public void run() {
                    ConnectorType[] types = bindingTypeMatches.toArray(new ConnectorType[bindingTypeMatches.size()]);
                    ConnectorTypeSelectionDialog dlg = new ConnectorTypeSelectionDialog(types);
                    int code = dlg.open();

                    if (code == IDialogConstants.OK_ID) {
                        Object[] selection = dlg.getResult();

                        // should always have only one selection but check to be sure
                        if (selection.length == 1) {
                            chosenType[0] = (ConnectorType)selection[0];
                        }
                    }
                }
            }, false);

            // get the choice
            bindingType = chosenType[0];
        } else {
            // no type found
        	// Log as Warning
            String key = I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) + "missingConnectorType.log.msg"; //$NON-NLS-1$
            final String msg = UTIL.getString(key, new Object[] {modelName, jdbcSource.getDriverClass()});
            UTIL.log(IStatus.WARNING, msg);
            
            // Present message in dialog to user because expected result is binding 
            final String dialogMsg = UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) 
            		+ "missingConnectorType.dialog.msg", new Object[] {jdbcSource.getDriverClass(), modelName}); //$NON-NLS-1$
            final String dialogTitle = UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) 
            		+ "missingConnectorType.dialog.title"); //$NON-NLS-1$
            
            UiUtil.runInSwtThread(new Runnable() {

                @Override
                public void run() {
                	useDefaultConnectorType = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), dialogTitle, dialogMsg);
                }
            }, false);
            
        }

        if( useDefaultConnectorType ) {
        	Collection<ConnectorType> matchedConnectorTypes = wsConfigMgr.findMatchingConnectorTypes(jdbcSource, true);
        	if( matchedConnectorTypes.size() == 1 ) {
        		bindingType = matchedConnectorTypes.iterator().next();
        	}
        }
        
        
        // create the binding if we have a type
        if (bindingType != null) {
            return DqpPlugin.getInstance().getAdmin().createConnector(bindingType, bindingName, false);
        }

        // no type found or selected by user so a binding could not be created
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor#postProcess(com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider)
     * @since 5.5.3
     */
    public void postProcess( final IJdbcImportInfoProvider infoProvider ) throws Exception {
        Connector binding = null;
        
        final WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getInstance().getWorkspaceConfig();
        String modelName = infoProvider.getModelName();

        // if item name includes the file extension remove it
        int index = modelName.indexOf('.');

        if (index != -1) {
            modelName = modelName.substring(0, index);
        }

        // see if model already has an assigned binding
        Collection<Connector> bindings = wsConfigMgr.getBindingsForModel(modelName);

        // if model does not have a connector binding we need to find/create one
        if (bindings.isEmpty()) {
            JdbcSource jdbcSource = infoProvider.getSource();
            Collection<Connector> matchingBindings = wsConfigMgr.findMatchingConnectors(jdbcSource);
            
            if (matchingBindings.isEmpty()) {
                Connector newBinding = createConnector(modelName, jdbcSource);

                // No binding, return
                // This could happen if a driver class doesn't match any existing connector type driver classes
                
                if( newBinding == null ) {
                	return;
                }
                
                // If a new binding was created we need to set its URL, user, and password properties and create a
                // source binding.

                // set properties
                setConnectorProperties(newBinding, infoProvider);

                // finally add binding to configuration since the classpath has been set and the jars copied over (now the binding
                // should start) and create a source binding so that preview will work
                DqpPlugin.getInstance().getAdmin().addBinding(newBinding);
                binding = newBinding;
            } else {
                // found one or more matching bindings so just use the first one
                binding = matchingBindings.iterator().next();
            }

            // create source binding
            wsConfigMgr.createSourceBinding(infoProvider.getModelResource(), binding);
            
            // make sure ModelExplorer tree shows the new connector binding
            ModelerUiViewUtils.refreshModelExplorerResourceNavigatorTree();
        }
    }

    /**
     * Sets the URL, user, and password properties and logs any problems.
     * 
     * @param newConnector the new connector binding whose properties are being set
     * @param infoProvider provides JDBC import information
     * @return errors (never <code>null</code>)
     * @since 6.0.0
     */
    private Collection<Exception> setConnectorProperties( Connector newConnector,
                                                          IJdbcImportInfoProvider infoProvider ) {
        Collection<Exception> errors = new ArrayList<Exception>();
        JdbcSource jdbcSource = infoProvider.getSource();
        Exception[] temp;

        // set URL
        try {
            temp = ModelerDqpUtils.setPropertyValue(newConnector,
                                                    JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL,
                                                    jdbcSource.getUrl());
            
            // include any errors
            if (!ArrayUtil.isNullOrEmpty(temp)) {
                errors.addAll(Arrays.asList(temp));
            }
        } catch (Exception e) {
            errors.add(e);
        }

        // set user
        try {
            temp = ModelerDqpUtils.setPropertyValue(newConnector,
                                                    JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER,
                                                    jdbcSource.getUsername());
            
            // include any errors
            if (!ArrayUtil.isNullOrEmpty(temp)) {
                errors.addAll(Arrays.asList(temp));
            }
        } catch (Exception e) {
            errors.add(e);
        }

        // set password
        try {
            temp = ModelerDqpUtils.setConnectorBindingPassword(newConnector, infoProvider.getPassword());
            
            // include any errors
            if (!ArrayUtil.isNullOrEmpty(temp)) {
                errors.addAll(Arrays.asList(temp));
            }
        } catch (Exception e) {
            errors.add(e);
        }
        
        return errors;
    }

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    private class ConnectorTypeSelectionDialog extends ListDialog implements
                                                                 IStructuredContentProvider {

        // =======================================================================================================================
        // Fields (ConnectorTypeSelectionDialog)
        // =======================================================================================================================

        private Object[] types = null;

        // =======================================================================================================================
        // Constructors (ConnectorTypeSelectionDialog)
        // =======================================================================================================================

        public ConnectorTypeSelectionDialog( ConnectorType[] theTypes ) {
            super(UiUtil.getWorkbenchShellOnlyIfUiThread());

            this.types = theTypes;

            setShellStyle(getShellStyle() | SWT.RESIZE);
            setLabelProvider(new LabelProvider());
            setContentProvider(this);
            setInitialSelections(new Object[] {
                this.types[0]
            });
            setInput(this.types);

            String prefix = I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class);
            setTitle(UTIL.getStringOrKey(prefix + "connectorTypeDialog.title")); //$NON-NLS-1$
            setMessage(UTIL.getStringOrKey(prefix + "connectorTypeDialog.msg")); //$NON-NLS-1$
        }

        // =======================================================================================================================
        // Methods (ConnectorTypeSelectionDialog)
        // =======================================================================================================================

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 5.0
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 5.0
         */
        public Object[] getElements( Object inputElement ) {
            return this.types;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 5.0
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }
    }
}
