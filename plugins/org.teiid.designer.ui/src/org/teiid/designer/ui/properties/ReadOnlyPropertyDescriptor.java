/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties;

import static org.teiid.designer.ui.PluginConstants.Prefs.General.AUTO_OPEN_EDITOR_IF_NEEDED;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.editors.ModelEditorManager;


/**
 * ReadOnlyPropertyDescriptor is a wrapper around an IPropertyDescriptor to make it read only.
 * The class offers three types of wrapper: READ_ONLY_PROPERTY, which silently disallows editing the 
 * property; READ_ONLY_RESOURCE, which announces to the user that the model file is read-only; and
 * NO_MODEL_EDITOR, which announces that the property cannot be modified unless the model is opened
 * in a Model Editor, and offers the option to open the model.
 *
 * @since 8.0
 */
public class ReadOnlyPropertyDescriptor implements IPropertyDescriptor {

    public static final String READ_ONLY_TITLE = UiConstants.Util.getString("ReadOnlyPropertyDescriptor.readOnlyModelTitle"); //$NON-NLS-1$
    public static final String READ_ONLY_MESSAGE = UiConstants.Util.getString("ReadOnlyPropertyDescriptor.readOnlyModelMessage"); //$NON-NLS-1$

    /** disable editing of this property */
    public static final int READ_ONLY_PROPERTY = 0;
    
    /** if user attempts to edit this property, announce that the resource is read-only */
    public static final int READ_ONLY_RESOURCE = 1;

    /** if user attempts to edit this property, offer to open the model file in an editor */
    public static final int NO_MODEL_EDITOR = 2;

    private IPropertyDescriptor delegate;
    private int status = READ_ONLY_PROPERTY;
    private IFile modelFile;

    /**
     * Construct an instance of ReadOnlyPropertyDescriptor.
     */
    public ReadOnlyPropertyDescriptor(IPropertyDescriptor delegate, int type) {
        this.delegate = delegate;
        this.status = type;
    }

    /**
     * Construct an instance of ReadOnlyPropertyDescriptor.
     */
    public ReadOnlyPropertyDescriptor(IPropertyDescriptor delegate, int type, IFile modelToOpen) {
        this.delegate = delegate;
        this.status = type;
        this.modelFile = modelToOpen;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        if ( status ==  READ_ONLY_RESOURCE ) {
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            MessageDialog.openError(shell, READ_ONLY_TITLE, READ_ONLY_MESSAGE);
        } else if ( status == NO_MODEL_EDITOR && modelFile != null ) {
            // one last check to see if the model is open
            if ( ! ModelEditorManager.isOpen(modelFile) ) {
                // get preference value for auto-open-editor
                String autoOpen = UiPlugin.getDefault().getPreferenceStore().getString(AUTO_OPEN_EDITOR_IF_NEEDED);

                // if the preference is to auto-open, then set forceOpen so we don't prompt the user
                boolean forceOpen = MessageDialogWithToggle.ALWAYS.equals(autoOpen);

                // If no preference, prompt the user
                if (!forceOpen) {
                    Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                    forceOpen = ModelEditorManager.showDialogShouldOpenEditor(shell);
                }
                
                if(forceOpen) {
                    ModelEditorManager.activate(modelFile, true);
                }

                return null;
            }
            return this.delegate.createPropertyEditor(parent);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     */
    @Override
    public String getCategory() {
        return delegate.getCategory();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     */
    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     */
    @Override
    public String[] getFilterFlags() {
        return delegate.getFilterFlags();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getHelpContextIds()
     */
    @Override
    public Object getHelpContextIds() {
        return delegate.getHelpContextIds();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     */
    @Override
    public Object getId() {
        return delegate.getId();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     */
    @Override
    public ILabelProvider getLabelProvider() {
        return delegate.getLabelProvider();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#isCompatibleWith(org.eclipse.ui.views.properties.IPropertyDescriptor)
     */
    @Override
    public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
        return delegate.isCompatibleWith(anotherProperty);
    }

}
