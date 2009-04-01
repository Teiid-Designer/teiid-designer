/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.object.PropertyDefinition;
import com.metamatrix.common.util.crypto.CryptoException;
import com.metamatrix.common.util.crypto.CryptoUtil;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.5
 */
public class ConnectorBindingPropertySource implements IPropertySource {

    private ConnectorBinding binding;
    private ComponentType type;

    private boolean isEditable = false;
    private ConnectorBindingPropertySourceProvider provider;

    /**
     * @since 4.2
     */
    public ConnectorBindingPropertySource( ConnectorBinding binding ) {
        this.binding = binding;
        if (binding != null) {
            this.type = DqpPlugin.getInstance().getConfigurationManager().getConnectorType(binding.getComponentTypeID());

            // we should always find a type
            if (this.type == null) {
                DqpUiConstants.UTIL.log(IStatus.ERROR,
                                        DqpUiConstants.UTIL.getString("ConnectorBindingsPropertySource.bindingTypeNotFound", //$NON-NLS-1$
                                                                      new Object[] {this.binding.getFullName(),
                                                                          this.binding.getComponentTypeID()}));
            }
        }
    }

    public void setProvider( ConnectorBindingPropertySourceProvider provider ) {
        this.provider = provider;
    }

    public void setEditable( boolean isEditable ) {
        this.isEditable = isEditable;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    public Object getEditableValue() {
        return this.binding;
    }

    /**
     * Obtains all the {@link ComponentTypeDefn}s for the current binding type.
     * 
     * @return the definitions or <code>null</code> if no current binding
     * @since 5.0
     */
    private Collection getAllComponentTypeDefinitions() {
        Collection result = null;

        if (this.type != null) {
            ConfigurationManager configMgr = DqpPlugin.getInstance().getConfigurationManager();
            ConfigurationModelContainer container = configMgr.getDefaultConfig().getCMContainerImpl();
            Assertion.isInstanceOf(this.type.getID(), ComponentTypeID.class, this.type.getID().getClass().getName());
            result = container.getAllComponentTypeDefinitions((ComponentTypeID)this.type.getID());
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] result = new IPropertyDescriptor[0];

        // don't return any descriptors if no binding or no type. this prevents the
        // set and get property methods from being called.
        if ((this.binding != null) && (this.type != null)) {
            Collection typeDefs = getAllComponentTypeDefinitions();

            if ((typeDefs != null) && !typeDefs.isEmpty()) {
                boolean showExpertProps = this.provider.isShowingExpertProperties();
                Collection temp = new ArrayList(typeDefs.size());
                Iterator iter = typeDefs.iterator();

                while (iter.hasNext()) {
                    ComponentTypeDefn typeDefn = (ComponentTypeDefn)iter.next();
                    PropertyDefinition propDefn = typeDefn.getPropertyDefinition();
                    String id = propDefn.getName();
                    String displayName = propDefn.getDisplayName();

                    // don't add if an expert property and expert properties are not being shown
                    if (propDefn.isExpert() && !showExpertProps) {
                        continue;
                    }

                    // don't add hidden definitions or expert props if not showing
                    if (!propDefn.isHidden()) {
                        Object descriptor = null;

                        // make sure readonly definitions are not modifiable
                        if (isEditable && propDefn.isModifiable()) {
                            if (propDefn.isMasked()) {
                                descriptor = new ConnectorBindingPasswordDescriptor(id, displayName);
                            } else if (ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH.equals(id)) {
                                descriptor = new ConnectorClasspathPropertyDescriptor(this.binding, displayName);
                            } else {
                                descriptor = new TextPropertyDescriptor(id, displayName);
                            }
                        } else {
                            descriptor = new PropertyDescriptor(id, displayName);
                        }

                        temp.add(descriptor);
                    }
                }

                if (!temp.isEmpty()) {
                    temp.toArray(result = new IPropertyDescriptor[temp.size()]);
                }
            }

            result = sortPropertyDescriptors(result);
        }

        return result;
    }

    /**
     * Sorts the properties using a custom sorter that puts the URL, user, and password always before other properties.
     * 
     * @param theDescriptors the descriptors being sorted
     * @return the sorted descriptors
     * @since 5.5
     */
    private IPropertyDescriptor[] sortPropertyDescriptors( IPropertyDescriptor[] theDescriptors ) {
        List descriptors = Arrays.asList(theDescriptors);
        Collections.sort(descriptors, new DescriptorSorter());
        return (IPropertyDescriptor[])descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue( Object id ) {
        String result = binding.getProperty((String)id);
        if (result != null) {
            ComponentTypeDefn defn = type.getComponentTypeDefinition((String)id);
            if (defn != null && defn.getPropertyDefinition().isMasked()) {
                try {
                    result = getPasswordDisplay(result);
                } catch (CryptoException theException) {

                    result = "*****"; //$NON-NLS-1$
                    // defect 18986 - can't prompt for password change if not editable
                    if (this.isEditable) {
                        ResetPasswordDialog dialog = new ResetPasswordDialog();
                        dialog.open();
                        if (dialog.getReturnCode() == Window.OK) {
                            setPropertyValue(id, dialog.getPassword());
                            result = (String)getPropertyValue(id);
                        }
                    }
                }
            } else if (ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH.equals(id)) {
                result = ModelerDqpUtils.getConnectorClasspathDisplayValue(result);
            }
        }

        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet( Object id ) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue( Object id ) {
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
        try {
            if (ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH.equals(id)) {
                processClasspathChange((String)value);
            } else {
                ModelerDqpUtils.setPropertyValue(getConnectorBinding(), id, value);
            }

            this.provider.propertyChanged(this.binding);
        } catch (final Exception error) {
            UiUtil.runInSwtThread(new Runnable() {

                public void run() {
                    DqpUiPlugin.showErrorDialog(Display.getCurrent().getActiveShell(), error);
                }
            }, false);
        }
    }

    /**
     * If jars are no longer being used by this connector, or any other connector, delete them from the extension modules
     * directory. Also, if a jar from the file system has been added, copy it over to the extension modules directory.
     * 
     * @since 6.0.0
     */
    private void processClasspathChange( String newValue ) throws Exception {
        // get the old value before setting the new value
        String oldValue = (String)getPropertyValue(ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH);

        // a list of the new jars to put on the classpath
        List<String> classpathJars = null;

        // check to see if classpath has been cleared out
        if (StringUtil.isEmpty(newValue)) {
            newValue = StringUtil.Constants.EMPTY_STRING;
        } else {
            // process the new value
            List<String> temp = new ArrayList<String>(ModelerDqpUtils.getJarNames(newValue));
            classpathJars = new ArrayList<String>(temp.size());

            for (int size = temp.size(), i = 0; i < size; ++i) {
                File jarFile = new File(temp.get(i));

                // a null parent means that the jar being added to the class path is already in the extensions directory
                // so there is no need to copy it
                if (jarFile.getParent() != null) {
                    // copy to extension module folder
                    if (DqpPlugin.getInstance().getExtensionsHandler().addConnectorJar(this, jarFile)) {
                        // change value here to get rid of file system path
                        classpathJars.add(jarFile.getName());
                    }
                } else {
                    classpathJars.add(jarFile.getName());
                }
            }

            // let user know if any problems constructing classpath
            if (temp.size() != classpathJars.size()) {
                WidgetUtil.showError(DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorBindingPropertySource.class)
                                                                   + "errorCopyingJarsMsg")); //$NON-NLS-1$
            }

            // calculate the string representation of the classpath
            newValue = ModelerDqpUtils.getConnectorClassPathPropertValue(classpathJars);
        }

        // set the new value
        Exception[] errors = ModelerDqpUtils.setPropertyValue(getConnectorBinding(),
                                                              ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH,
                                                              newValue);

        // log errors
        for (Exception e : errors) {
            DqpUiConstants.UTIL.log(e);
        }

        // need to clean up jars that are no longer being used
        Collection<String> oldJars = ModelerDqpUtils.getJarNames(oldValue);

        // remove the jars that are still being used by this connector
        if (classpathJars != null) {
            oldJars.removeAll(classpathJars);
        }

        // need to delete from the extension modules directory those remaining jars that are not being used by
        // other connectors or by UDF models
        if (!oldJars.isEmpty()) {
            DqpExtensionsHandler extHandler = DqpPlugin.getInstance().getExtensionsHandler();
            Collection<File> jarsToDelete = new ArrayList<File>(oldJars.size());

            for (String name : oldJars) {
                if (!extHandler.isConnectorJar(name) && !extHandler.isUdfJar(name)) {
                    jarsToDelete.add(new File(DqpPath.getRuntimeExtensionsPath().toFile(), name));
                }
            }

            // finally delete the file(s)
            if (!jarsToDelete.isEmpty()) {
                extHandler.deleteConnectorJars(this, jarsToDelete.toArray(new File[jarsToDelete.size()]));
            }
        }
    }

    public ConnectorBinding getConnectorBinding() {
        return this.binding;
    }

    private String getPasswordDisplay( String encryptedValue ) throws CryptoException {
        String result = encryptedValue;

        if ((encryptedValue != null) && (encryptedValue.length() > 0)) {
            StringBuffer temp = new StringBuffer();
            int numChars = CryptoUtil.getDecryptor().decrypt(encryptedValue).length();

            while (temp.length() < numChars) {
                temp.append('*');
            }

            result = temp.toString();
        }

        return result;
    }

    ConnectorBinding getBinding() {
        return this.binding;
    }

    private class ResetPasswordDialog extends MessageDialog {
        String pwd = ""; //$NON-NLS-1$

        public ResetPasswordDialog() {
            super(
                  UiUtil.getWorkbenchShellOnlyIfUiThread(),
                  DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.title"), //$NON-NLS-1$
                  null,
                  "", // message is set later //$NON-NLS-1$
                  ERROR,
                  new String[] {
                      DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnReset"), DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnCancel")}, //$NON-NLS-1$ //$NON-NLS-2$
                  0);
            setReturnCode(CANCEL);
        }

        /**
         * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String,
         *      boolean)
         * @since 4.3
         */
        @Override
        protected Button createButton( Composite theParent,
                                       int theId,
                                       String theLabel,
                                       boolean theDefaultButton ) {
            Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

            if (theLabel.equals(DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnReset"))) { //$NON-NLS-1$
                btn.setToolTipText(DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnReset.tip")); //$NON-NLS-1$
            } else if (theLabel.equals(DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnCancel"))) { //$NON-NLS-1$
                btn.setToolTipText(DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.btnCancel.tip")); //$NON-NLS-1$
            }

            return btn;
        }

        /**
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         * @since 4.3
         */
        @Override
        protected Control createCustomArea( Composite theParent ) {
            Composite pnl = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH, 1, 2);
            WidgetFactory.createLabel(pnl, DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.lblPwd")); //$NON-NLS-1$

            Text txtPwd = WidgetFactory.createPasswordField(pnl, GridData.FILL_HORIZONTAL, 1);
            txtPwd.addModifyListener(new ModifyListener() {
                public void modifyText( ModifyEvent theEvent ) {
                    ResetPasswordDialog.this.pwd = ((Text)theEvent.widget).getText();
                }
            });

            // set dialog message now because it was too hard to do it in constructor
            ConnectorBinding binding = getBinding();
            String[] params = new String[3];
            params[0] = binding.getFullName();
            params[1] = binding.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL);
            params[2] = binding.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER);

            this.messageLabel.setText(DqpUiConstants.UTIL.getString("ResetPasswordDialog.msg", (Object[])params)); //$NON-NLS-1$

            return pnl;
        }

        public String getPassword() {
            if (getReturnCode() == OK) {
                return this.pwd;
            }

            throw new IllegalStateException(DqpUiConstants.UTIL.getStringOrKey("ResetPasswordDialog.illegalStateMsg")); //$NON-NLS-1$
        }
    }

    /**
     * Sorts property descriptors using there display names but making sure the URL, user, and password (in that order) are always
     * the first 3 properties.
     * 
     * @since 5.5
     */
    class DescriptorSorter implements Comparator {
        public int compare( Object theDescriptor,
                            Object theOtherDescriptor ) {
            int result = 0;
            Object tempId = ((IPropertyDescriptor)theDescriptor).getId();
            Object otherTempId = ((IPropertyDescriptor)theOtherDescriptor).getId();

            if ((tempId instanceof String) && (otherTempId instanceof String)) {
                String id = (String)tempId;
                String otherId = (String)otherTempId;

                // ensure URL comes first, user next, password next, and then sort by display name.
                if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)) {
                    result = -100;
                } else if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER)) {
                    result = (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL) ? 1 : -50);
                } else if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_PASSWORD)) {
                    result = (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)
                              || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER) ? 1 : -25);
                } else if (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)
                           || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER)
                           || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_PASSWORD)) {
                    result = 1;
                } else {
                    result = ((IPropertyDescriptor)theDescriptor).getDisplayName().compareTo(((IPropertyDescriptor)theOtherDescriptor).getDisplayName());
                }
            } else if (tempId instanceof Comparable) {
                result = ((Comparable)tempId).compareTo(otherTempId);
            } else {
                result = ((IPropertyDescriptor)theDescriptor).getDisplayName().compareTo(((IPropertyDescriptor)theOtherDescriptor).getDisplayName());
            }

            return result;
        }
    }

}
