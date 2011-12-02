/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties.extension;

import static com.metamatrix.modeler.ui.UiConstants.PLUGIN_ID;
import static org.teiid.designer.extension.ExtensionPlugin.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * 
 */
public class ModelExtensionPropertyDescriptor extends PropertyDescriptor implements Comparable<ModelExtensionPropertyDescriptor> {

    // TODO see references to ExtensionPropertyDescriptor to see if references to this class is needed???

    private static ICellEditorValidator createValidator( final ModelExtensionPropertyDefinition propDefn ) {
        return new ICellEditorValidator() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
             */
            @Override
            public String isValid( Object value ) {
                return propDefn.isValidValue((String)value);
            }
        };
    }

    private final EObject eObject;
    private final ILabelProvider labelProvider;
    private final ModelExtensionPropertyDefinition propDefn;

    public ModelExtensionPropertyDescriptor( EObject eObject,
                                             ModelExtensionPropertyDefinition propDefn ) {
        super(propDefn.getId(), (CoreStringUtil.isEmpty(propDefn.getDisplayName()) ? propDefn.getId()
                                                                                  : propDefn.getNamespacePrefix() + ':'
                                                                                          + propDefn.getDisplayName()));

        CoreArgCheck.isNotNull(eObject, "eObject is null"); //$NON-NLS-1$
        this.eObject = eObject;
        this.propDefn = propDefn;
        this.labelProvider = new ModelExtensionDescriptorLabelProvider();

        setCategory(Messages.modelExtensionPropertyCategory);
        setDescription(this.propDefn.getDescription());
        setLabelProvider(this.labelProvider);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( ModelExtensionPropertyDescriptor thatDescriptor ) {
        return this.propDefn.getId().compareTo(thatDescriptor.propDefn.getId());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor( Composite parent ) {
        CellEditor editor = null; // a null cell editor is a readonly editor
        ModelResource modelResource = null;

        try {
            modelResource = ModelUtil.getModifiableModel(this.eObject);

            if (modelResource == null || (!this.propDefn.isModifiable())) {
                return null;
            }
        } catch (ModelWorkspaceException e) {
            Util.log(e);
            return null;
        }

        // editor must be open to edit property
        IFile file = (IFile)modelResource.getResource();

        // workspace file not found
        if (file == null) {
            // really shouldn't happen if we have a model resource
            Util.log(IStatus.ERROR, NLS.bind(Messages.workspaceFileNotFound, modelResource.getItemName()));
            return null;
        }

        // if model editor is not open ask user if they want it opened
        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            IFileEditorInput fileInput = new FileEditorInput(file);
            IEditorPart modelEditor = page.findEditor(fileInput);

            if (modelEditor == null) {
                if (!ModelEditorManager.autoOpen(window.getShell(), this.eObject, true)) {
                    // user chose not to open editor
                    return null;
                }
            }
        } catch (Exception e) {
            Util.log(e);
            return null;
        }

        // editor is open so create editable cell editor
        final String[] allowedValues = this.propDefn.getAllowedValues();

        // use combobox editor if there are allowed values
        if ((allowedValues != null) && (allowedValues.length != 0)) {
            editor = new ComboBoxCellEditor(parent, allowedValues, SWT.READ_ONLY);
        } else {
            // use text editor since there are not known values
            editor = new TextCellEditor(parent);
            editor.setValidator(createValidator(this.propDefn));

            // mask value if needed
            if (this.propDefn.isMasked()) {
                ((Text)editor.getControl()).setEchoChar('*');
            }

            // TODO need to find a way to clear the error message (status bar) after editor is deactivated
        }

        return editor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getDescription()
     */
    @Override
    public String getDescription() {
        return this.propDefn.getDescription();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getId()
     */
    @Override
    public Object getId() {
        return this;
    }

    public String getPropDefnId() {
        return this.propDefn.getId();
    }

    ModelObjectExtensionAssistant getModelExtensionAssistant( String propId ) {
        String namespacePrefix = ModelExtensionPropertyDefinition.Utils.getNamespacePrefix(propId);

        if (CoreStringUtil.isEmpty(namespacePrefix)) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.namespacePrefixIsEmpty, propId)));
            return null;
        }

        ModelExtensionAssistant assistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(namespacePrefix);

        if (assistant == null) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.modelExtensionAssistantNotFound, namespacePrefix)));
        }

        return ((assistant instanceof ModelObjectExtensionAssistant) ? (ModelObjectExtensionAssistant)assistant : null);
    }

    /**
     * @return the model object (never <code>null</code>)
     */
    protected EObject getModelObject() {
        return this.eObject;
    }

    /**
     * @return the property definition (never <code>null</code>)
     */
    protected ModelExtensionPropertyDefinition getPropertyDefinition() {
        return this.propDefn;
    }

    Object getPropertyValue() {
        String propId = this.propDefn.getId();
        ModelObjectExtensionAssistant assistant = getModelExtensionAssistant(propId);

        if (assistant != null) {
            try {
                String value = assistant.getPropertyValue(this.eObject, propId);

                // must convert to empty string if null as TextCellEditor requires non-null value
                if (CoreStringUtil.isEmpty(value)) {
                    value = CoreStringUtil.Constants.EMPTY_STRING;
                } else {
                    // a value that is an allowed values must be converted to the index
                    String[] allowedValues = propDefn.getAllowedValues();

                    if ((allowedValues != null) && (allowedValues.length != 0)) {
                        for (int i = 0; i < allowedValues.length; ++i) {
                            if (allowedValues[i].equalsIgnoreCase(value)) {
                                return i;
                            }
                        }

                        // value is not an allowed value
                        if (this.propDefn.isRequired()) {
                            log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.valueIsNotAnAllowedValueFirstValueUsed,
                                                                              new Object[] { value, propId, allowedValues[0] })));
                            value = allowedValues[0];
                        } else {
                            log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.valueIsNotAnAllowedValue, value, propId)));
                        }
                    }
                }

                return value;
            } catch (Exception e) {
                String msg = NLS.bind(Messages.errorObtainingPropertyFromAssistant, propId, assistant.getClass().getName());
                log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
            }
        }

        log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.unexpectedPropertySourceId, propId)));
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    void log( IStatus status ) {
        ExtensionPlugin.getInstance().getLog().log(status);
    }

    void setPropertyValue( Object value ) {
        String propId = this.propDefn.getId();
        ModelObjectExtensionAssistant assistant = getModelExtensionAssistant(propId);

        if (assistant != null) {
            if (value instanceof Integer) {
                int index = (Integer)value;

                // only could be an index to an allowed value so convert to the value at that index
                String[] allowedValues = propDefn.getAllowedValues();

                if ((allowedValues != null) && (allowedValues.length == 0)) {
                    if ((index < 0) || (index > (allowedValues.length - 1))) {
                        // set to default value
                        value = null;
                    }
                } else {
                    value = allowedValues[index];
                }
            }

            if (value instanceof String) {
                try {
                    assistant.setPropertyValue(this.eObject, propId, (String)value);
                } catch (Exception e) {
                    String msg = NLS.bind(Messages.errorObtainingPropertyFromAssistant, propId, assistant.getClass().getName());
                    log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
                }
            } else {
                log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.unexpectedPropertyValueType, propId)));
            }
        } else {
            log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.unexpectedPropertySourceId, propId.getClass().getName())));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * Used only for the value column in the properties view.
     */
    protected class ModelExtensionDescriptorLabelProvider extends LabelProvider {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            PropertyDefinition propDefn = getPropertyDefinition();
            Object value = getPropertyValue();

            if (value instanceof String) {
                String errorMsg = propDefn.isValidValue((String)value);

                // add error icon
                if (!CoreStringUtil.isEmpty(errorMsg)) {
                    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
                }

                String stringValue = (String)value;
                String defaultValue = propDefn.getDefaultValue();

                if ((CoreStringUtil.isEmpty(stringValue) && CoreStringUtil.isEmpty(defaultValue))
                        || CoreStringUtil.equals(stringValue, defaultValue)) {
                    return null;
                }

                // add icon to show value is different than the default
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
            }

            // integer values are indexes into allowed values collection
            if (value instanceof Integer) {
                String stringValue = propDefn.getAllowedValues()[(Integer)value];

                if (!CoreStringUtil.equals(stringValue, propDefn.getDefaultValue())) {
                    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (getPropertyDefinition().isMasked()) {
                return "*****"; //$NON-NLS-1$
            }

            if (element instanceof Integer) {
                String[] allowedValues = getPropertyDefinition().getAllowedValues();

                if ((allowedValues != null) && (allowedValues.length != 0)) {
                    return allowedValues[(Integer)element];
                }
            }

            return super.getText(element);
        }
    }

}
