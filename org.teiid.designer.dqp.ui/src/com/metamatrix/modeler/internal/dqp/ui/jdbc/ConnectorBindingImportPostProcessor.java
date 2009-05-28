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
import org.teiid.connector.api.ConnectorPropertyNames;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.workspace.ClasspathEditorDialog;
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
    private ConnectorBinding createConnectorBinding( String modelName,
                                                     JdbcSource jdbcSource ) throws Exception {
        ConnectorBindingType bindingType = null;
        WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getWorkspaceConfig();
        final String bindingName = wsConfigMgr.createConnectorBindingName(modelName);
        final Collection<ConnectorBindingType> bindingTypeMatches = wsConfigMgr.findMatchingConnectorBindingTypes(jdbcSource, false);
        int numTypesFound = bindingTypeMatches.size();

        if (numTypesFound == 1) {
            bindingType = bindingTypeMatches.iterator().next();
        } else if (numTypesFound > 1) {
            // ask the user to pick which matching type they want to use which will be set into this array
            final ConnectorBindingType[] chosenType = new ConnectorBindingType[1];

            // make sure to show dialog in UI thread
            UiUtil.runInSwtThread(new Runnable() {

                @Override
                public void run() {
                    ConnectorBindingType[] types = bindingTypeMatches.toArray(new ConnectorBindingType[bindingTypeMatches.size()]);
                    ConnectorTypeSelectionDialog dlg = new ConnectorTypeSelectionDialog(types);
                    int code = dlg.open();

                    if (code == IDialogConstants.OK_ID) {
                        Object[] selection = dlg.getResult();

                        // should always have only one selection but check to be sure
                        if (selection.length == 1) {
                            chosenType[0] = (ConnectorBindingType)selection[0];
                        }
                    }
                }
            }, false);

            // get the choice
            bindingType = chosenType[0];
        } else {
            // no type found
        	// Log as Warning
            String key = I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) + "missingConnectorBindingType.log.msg"; //$NON-NLS-1$
            final String msg = UTIL.getString(key, new Object[] {modelName, jdbcSource.getDriverClass()});
            UTIL.log(IStatus.WARNING, msg);
            
            // Present message in dialog to user because expected result is binding 
            final String dialogMsg = UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) 
            		+ "missingConnectorBindingType.dialog.msg", new Object[] {jdbcSource.getDriverClass(), modelName}); //$NON-NLS-1$
            final String dialogTitle = UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingImportPostProcessor.class) 
            		+ "missingConnectorBindingType.dialog.title"); //$NON-NLS-1$
            
            UiUtil.runInSwtThread(new Runnable() {

                @Override
                public void run() {
                	useDefaultConnectorType = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), dialogTitle, dialogMsg);
                }
            }, false);
            
        }

        if( useDefaultConnectorType ) {
        	Collection<ConnectorBindingType> matchedConnectorTypes = wsConfigMgr.findMatchingConnectorBindingTypes(jdbcSource, true);
        	if( matchedConnectorTypes.size() == 1 ) {
        		bindingType = matchedConnectorTypes.iterator().next();
        	}
        }
        
        
        // create the binding if we have a type
        if (bindingType != null) {
            return DqpPlugin.getInstance().getConfigurationManager().createConnectorBinding(bindingType, bindingName, false);
        }

        // no type found or selected by user so a binding could not be created
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor#postProcess(com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider)
     * @since 5.5.3
     */
    public void postProcess( final IJdbcImportInfoProvider infoProvider ) throws Exception {
        ConnectorBinding binding = null;
        
        final WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getWorkspaceConfig();
        String modelName = infoProvider.getModelName();

        // if item name includes the file extension remove it
        int index = modelName.indexOf('.');

        if (index != -1) {
            modelName = modelName.substring(0, index);
        }

        // see if model already has an assigned binding
        Collection<ConnectorBinding> bindings = wsConfigMgr.getBindingsForModel(modelName);

        // if model does not have a connector binding we need to find/create one
        if (bindings.isEmpty()) {
            JdbcSource jdbcSource = infoProvider.getSource();
            Collection<ConnectorBinding> matchingBindings = wsConfigMgr.findMatchingConnectorBindings(jdbcSource);
            
            if (matchingBindings.isEmpty()) {
                ConnectorBinding newBinding = createConnectorBinding(modelName, jdbcSource);

                // No binding, return
                // This could happen if a driver class doesn't match any existing connector type driver classes
                
                if( newBinding == null ) {
                	return;
                }
                
                // If a new binding was created we need to set its classpath, URL, user, and password properties and create a
                // source binding.
                
                DqpExtensionsHandler extHandler = DqpPlugin.getInstance().getExtensionsHandler();

                // need to set classpath to the jars needed by the JDBC driver
                List jarUris = jdbcSource.getJdbcDriver().getJarFileUris();
                List<String> classpathJarNames = new ArrayList<String>(jarUris.size());

                for (Iterator itr = jarUris.iterator(); itr.hasNext();) {
                    String uri = (String)itr.next();
                    File jarFile;

                    // if the path has a colon we know the path is not relative to this bundle so it is a file system path
                    jarFile = new File(new URI(uri));

                    final String jarName = jarFile.getName();

                    // add jar name to classpath
                    classpathJarNames.add(jarName);

                    // copy to extension modules directory if unique name or if user wants to overwrite existing jar
                    final boolean[] copy = new boolean[] {true};

                    // if jar exists ask for confirmation to overwrite
                    if (extHandler.extensionModuleExists(jarName)) {
                        // if user does not confirm overwrite do not copy
                        UiUtil.runInSwtThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!ClasspathEditorDialog.showConfirmOverwriteDialog(jarName)) {
                                    copy[0] = false;
                                }
                            }
                        }, false);
                    }

                    // if required copy over jar
                    if (copy[0] && !extHandler.addConnectorJar(this, jarFile)) {
                        classpathJarNames.remove(jarName);
                    }
                }

                // set properties
                setConnectorProperties(newBinding, infoProvider, classpathJarNames);

                // finally add binding to configuration since the classpath has been set and the jars copied over (now the binding
                // should start) and create a source binding so that preview will work
                DqpPlugin.getInstance().getConfigurationManager().addBinding(newBinding);
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
     * @param newBinding the new connector binding whose properties are being set
     * @param infoProvider provides JDBC import information
     * @param classpathJarNames the names of the JAR files that should be set on the binding classpath
     * @return errors (never <code>null</code>)
     * @since 6.0.0
     */
    private Collection<Exception> setConnectorProperties( ConnectorBinding newBinding,
                                                          IJdbcImportInfoProvider infoProvider,
                                                          List<String> classpathJarNames ) {
        Collection<Exception> errors = new ArrayList<Exception>();
        JdbcSource jdbcSource = infoProvider.getSource();
        Exception[] temp;

        // set classpath property
        if (!classpathJarNames.isEmpty()) {
            try {
                temp = ModelerDqpUtils.setPropertyValue(newBinding,
                                                        ConnectorPropertyNames.CONNECTOR_CLASSPATH,
                                                        ModelerDqpUtils.getConnectorClassPathPropertValue(classpathJarNames));
                
                // include any errors
                if (!ArrayUtil.isNullOrEmpty(temp)) {
                    errors.addAll(Arrays.asList(temp));
                }
            } catch (Exception e) {
                errors.add(e);
            }
        }

        // set URL
        try {
            temp = ModelerDqpUtils.setPropertyValue(newBinding,
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
            temp = ModelerDqpUtils.setPropertyValue(newBinding,
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
            temp = ModelerDqpUtils.setConnectorBindingPassword(newBinding, infoProvider.getPassword());
            
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

        public ConnectorTypeSelectionDialog( ConnectorBindingType[] theTypes ) {
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
