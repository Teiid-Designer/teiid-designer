/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties.udp;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.properties.ReadOnlyPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * UserDefinedPropertyDescriptor
 */
public class UserDefinedPropertyDescriptor extends TextPropertyDescriptor {

    public static final String CATEGORY = UiConstants.Util.getString("UserDefinedPropertyDescriptor.category"); //$NON-NLS-1$


    private boolean showReadOnlyDialog = true;
    private EObject entry;

    /**
     * Construct an instance of UserDefinedPropertyDescriptor.
     * @param id
     * @param displayName
     */
    public UserDefinedPropertyDescriptor(EObject entry, String displayName) {
        super(entry, displayName);
        this.entry = entry;
    }

    //============================================================
    // Instance Methods
    //============================================================
    public void setShowReadOnlyDialog(boolean enable) {
        showReadOnlyDialog = enable;
    } 
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        // check failure conditions: ModelResource is null, or read-only, or not open in an editor 
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(entry);
        if (modelResource == null) { // if the modelResource is null, we can't edit the properties
            return null;
        }
        if (ModelUtil.isIResourceReadOnly(modelResource.getResource())) {
            if (showReadOnlyDialog) {
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                MessageDialog.openError(
                    shell,
                    ReadOnlyPropertyDescriptor.READ_ONLY_TITLE,
                    ReadOnlyPropertyDescriptor.READ_ONLY_MESSAGE);
            }
            return null;
        }

        IFile file = (IFile)modelResource.getResource();
        if (file != null) {
            if (!ModelEditorManager.isOpen(file)) {
                // can't modify a property value on an EObject if it's ModelEditor is not open.
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                if (MessageDialog
                    .openQuestion(
                        shell,
                        ModelObjectPropertyDescriptor.OPEN_EDITOR_TITLE,
                        ModelObjectPropertyDescriptor.OPEN_EDITOR_MESSAGE)) {
                    ModelEditorManager.open(entry, true);
                }
                return null;
            }
        }

        return super.createPropertyEditor(parent);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     */
    @Override
    public String getCategory() {
        return CATEGORY;
    }

}
