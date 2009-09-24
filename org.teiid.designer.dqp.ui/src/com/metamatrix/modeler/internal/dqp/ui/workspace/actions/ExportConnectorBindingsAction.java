/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.util.ConfigurationPropertyNames;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.actions.workers.ExportTextToFileWorker;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class ExportConnectorBindingsAction extends ConfigurationManagerAction implements DqpUiConstants {

    /**
     * @since 5.0
     */
    public ExportConnectorBindingsAction() {
        super(""); //$NON-NLS-1$
        this.setText(DqpUiConstants.UTIL.getString("ExportConnectorBindingsAction.label")); //$NON-NLS-1$
        this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EXPORT_CONNECTORS_ICON));
        this.setToolTipText(DqpUiConstants.UTIL.getString("ExportConnectorBindingsAction.tooltip")); //$NON-NLS-1$
        setEnabled(isValidSelection());
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        // Get Selection
        if (isValidSelection()) {
            String prefix = I18nUtil.getPropertyPrefix(ExportConnectorBindingsAction.class);
            List selectedObjects = getSelectedObjects();

            try {
                List<String> bindingXmlAndJars = getConnectorXmlAndRequiredJars(selectedObjects);
                ExportTextToFileWorker expWorker = new ExportTextToFileWorker(UTIL.getString(prefix + "exportFileWorker.title"), //$NON-NLS-1$
                                                                              UTIL.getString(prefix
                                                                                             + "exportFileWorker.defaultFileName"), //$NON-NLS-1$
                                                                              UTIL.getString(prefix
                                                                                             + "exportFileWorker.defaultExtension"), //$NON-NLS-1$
                                                                              StringUtil.Constants.EMPTY_STRING,
                                                                              bindingXmlAndJars.get(0));
                if (!expWorker.export()) {
                    DqpExtensionsHandler handler = DqpPlugin.getInstance().getExtensionsHandler();
                    String destination = expWorker.getFolder();
                    int jarsNotCopied = 0;

                    // copy one at a time so that an overwrite confirmation dialog can be shown if needed
                    for (int size = bindingXmlAndJars.size(), i = 1; i < size; ++i) {
                        String moduleName = bindingXmlAndJars.get(i);
                        File jarToCopy = new Path(destination).append(moduleName).toFile();
                        boolean copyJar = true;

                        if (jarToCopy.exists()) {
                            copyJar = WidgetUtil.confirmOverwrite(jarToCopy);
                        }

                        if (copyJar) {
                            // make sure file exists
                            if (handler.extensionModuleExists(moduleName)) {
                                handler.copyExtensionModule(moduleName, destination);
                            } else {
                                ++jarsNotCopied;

                                if (!DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equals(moduleName)) {
                                    // jar in classpath connector classpath is not found in extension module directory
                                    UTIL.log(IStatus.WARNING, UTIL.getString(prefix + "missingConnectorJar", moduleName)); //$NON-NLS-1$
                                }
                            }
                        } else {
                            ++jarsNotCopied;
                        }
                    }

                    // tell user that jars were also copied
                    int numJars = (bindingXmlAndJars.size() - 1) - jarsNotCopied;

                    if (numJars != 0) {
                        String msg = null;

                        if (numJars == 1) {
                            msg = UTIL.getString(prefix + "exportFileWorker.oneJarExported", expWorker.getFileName()); //$NON-NLS-1$
                        } else {
                            msg = UTIL.getString(prefix + "exportFileWorker.multipleJarsExported", //$NON-NLS-1$
                                                 expWorker.getFileName(),
                                                 numJars);
                        }

                        MessageDialog.openInformation(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                      UTIL.getString(prefix + "exportFileWorker.exportingJars.title"), //$NON-NLS-1$
                                                      msg);
                    }
                }
            } catch (MetaMatrixComponentException theException) {
                UTIL.log(theException);
                MessageDialog.openError(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                        UTIL.getString(prefix + "exportConnectorsProblem"), theException.toString()); //$NON-NLS-1$
            } catch (Exception e) {
                UTIL.log(e);
                String msg = e.getLocalizedMessage();

                if (StringUtil.isEmpty(msg)) {
                    msg = UTIL.getString(prefix + "exceptionExportingConnectorJarsMsg"); //$NON-NLS-1$
                }

                MessageDialog.openError(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                        UTIL.getString(prefix + "exportConnectorJarsProblem"), msg); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean enable = isValidSelection();

        setEnabled(enable);
    }

    /*
     * Returns TRUE if all selected objects are connector bindings
     */
    private boolean isValidSelection() {
        boolean result = false;
        if (!isEmptySelection()) {
            Object[] selectedObjects = getSelectedObjects().toArray();
            if (selectedObjects != null) {
                result = true;
                for (int i = 0; i < selectedObjects.length; i++) {
                    if (!(selectedObjects[i] instanceof ConnectorBinding)) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Converts the connector bindings supplied into known CDK (XML) format and places that in the first element of the returned
     * list. The remaining elements of the returned collection correspond to the names of the jars required by those bindings.
     *
     * @param bindings th connector bindings being processed
     * @return the CDK XML of the bindings and their required jar names
     * @since 4.3
     */
    private List<String> getConnectorXmlAndRequiredJars( List<ConnectorBinding> bindings ) throws MetaMatrixComponentException {

        List<String> result = new ArrayList<String>();
        DqpExtensionsHandler handler = DqpPlugin.getInstance().getExtensionsHandler();
        Set<ConnectorBindingType> types = new HashSet<ConnectorBindingType>(bindings.size());
        Set<String> requiredJars = new HashSet<String>();

        for (ConnectorBinding binding : bindings) {
            ConnectorBindingType type = (ConnectorBindingType)this.getConfigurationManager().getConnectorType(binding.getComponentTypeID());
            types.add(type);

            // get the required jars from the binding or from the type
            String[] classpathJars = handler.getConnectorBindingExtensionModules(binding);

            if (ArrayUtil.isNullOrEmpty(classpathJars)) {
                // need to get required jars from type
                classpathJars = handler.getConnectorTypeExtensionModules(type);
            }

            // should always have at least one jar but check just in case
            if (classpathJars.length != 0) {
                // filter out duplicates
                for (String jarName : classpathJars) {
                    requiredJars.add(jarName);
                }
            }
        }

        try {
            XMLConfigurationImportExportUtility exporter = new XMLConfigurationImportExportUtility();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            exporter.exportConnectorBindings(bos,
                                             bindings.toArray(new ConnectorBinding[bindings.size()]),
                                             types.toArray(new ConnectorBindingType[types.size()]),
                                             getPropertiesForExporting());
            String contents = bos.toString();
            bos.close();
            result.add(contents);

            // add the jars to the result
            result.addAll(requiredJars);
            return result;
        } catch (Exception e) {
            throw new MetaMatrixComponentException(e);
        }
    }

    /**
     * @return
     * @since 4.3
     */
    static Properties getPropertiesForExporting() {
        Properties properties = new Properties();
        properties.put(ConfigurationPropertyNames.APPLICATION_CREATED_BY, "Designer"); //$NON-NLS-1$
        properties.put(ConfigurationPropertyNames.APPLICATION_VERSION_CREATED_BY, "5.5.3"); //$NON-NLS-1$
        properties.put(ConfigurationPropertyNames.USER_CREATED_BY, "designer"); //$NON-NLS-1$
        return properties;
    }
}
