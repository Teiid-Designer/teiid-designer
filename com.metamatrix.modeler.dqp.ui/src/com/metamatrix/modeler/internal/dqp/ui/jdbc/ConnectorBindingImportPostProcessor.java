/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.WorkspaceConfigurationManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5.3
 */
public class ConnectorBindingImportPostProcessor implements
                                                DqpUiConstants,
                                                IJdbcImportPostProcessor {

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor#postProcess(com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider)
     * @since 5.5.3
     */
    public void postProcess(final IJdbcImportInfoProvider infoProvider) throws Exception {
        final WorkspaceConfigurationManager wsConfigMgr = DqpPlugin.getWorkspaceConfig();
        Collection<ConnectorBinding> bindings = wsConfigMgr.getBindingsForModel(infoProvider.getModelName());
        // if model does not have a connector binding we need to find/create one
        if (bindings.isEmpty()) {
            WorkspaceConfigurationManager.BindingAssignmentResult result = wsConfigMgr.assignConnectorBinding(infoProvider.getModelResource(),
                                                                                                              infoProvider.getSource(),
                                                                                                              infoProvider.getPassword());
            if (result == WorkspaceConfigurationManager.BindingAssignmentResult.MULTIPLE_TYPES) {
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {

                        // if an assignment wasn't made into the configuration because a matching binding wasn't found and multipe
                        // connector binding types were found, we need to ask the user which type they want to use before creating
                        // the
                        // new binding
                        Collection<ConnectorBindingType> bindingTypeMatches = wsConfigMgr.findMatchingConnectorBindingTypes(infoProvider.getSource());
                        ConnectorBindingType[] types = bindingTypeMatches.toArray(new ConnectorBindingType[bindingTypeMatches.size()]);
                        ConnectorTypeSelectionDialog dlg = new ConnectorTypeSelectionDialog(types);
                        int code = dlg.open();

                        if (code == IDialogConstants.OK_ID) {
                            Object[] selection = dlg.getResult();

                            if (selection.length == 1) {
                                ConnectorBindingType bindingType = (ConnectorBindingType)selection[0];
                                WorkspaceConfigurationManager.BindingAssignmentResult result = wsConfigMgr.assignConnectorBinding(infoProvider.getModelResource(),
                                                                                                                                  bindingType,
                                                                                                                                  infoProvider.getSource(),
                                                                                                                                  infoProvider.getPassword());
                                if (result == WorkspaceConfigurationManager.BindingAssignmentResult.SUCCESS) {
                                    // make sure ModelExplorer tree shows the connector binding
                                    ModelerUiViewUtils.refreshModelExplorerResourceNavigatorTree();
                                }
                            }
                            }
                    }
                });
            }
        }
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

        public ConnectorTypeSelectionDialog(ConnectorBindingType[] theTypes) {
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
        public Object[] getElements(Object inputElement) {
            return this.types;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 5.0
         */
        public void inputChanged(Viewer viewer,
                                 Object oldInput,
                                 Object newInput) {
        }
    }
}
