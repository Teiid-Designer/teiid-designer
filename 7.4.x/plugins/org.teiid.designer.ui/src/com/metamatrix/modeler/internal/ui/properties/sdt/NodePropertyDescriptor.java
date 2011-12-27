/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties.sdt;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.w3c.dom.Node;
import com.metamatrix.metamodels.xsd.aspects.sql.XsdSimpleTypeDefinitionAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.properties.ReadOnlyPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * NodePropertyDescriptor is a TextPropertyDescriptor for handling the DOM Node properties of 
 * XSDSimpleTypeDefinitions.
 */
public class NodePropertyDescriptor extends TextPropertyDescriptor {

    public static final String CATEGORY = UiConstants.Util.getString("RuntimeTypePropertyDescriptor.category"); //$NON-NLS-1$

    private EObject eObject;
    private boolean isEditableProperty = true;
    private boolean showReadOnlyDialog = true;
    private String aDisplayName;
    
    /**
     * Construct an instance of NodePropertyDescriptor.
     */
    public NodePropertyDescriptor(XSDSimpleTypeDefinition datatype, Node node) {
        super(node, node.getNodeName());
        this.eObject = datatype;

        // can never edit UUIDs.
        if (node.getNodeName().equals(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME)) {
            this.isEditableProperty = false;
        }
        // cannot edit any property of builtin types
        if (ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype(datatype)) {
            this.isEditableProperty = false;
        }
    }
    
    /**
     * Construct an instance of NodePropertyDescriptor.
     */
    public NodePropertyDescriptor(XSDSimpleTypeDefinition datatype, Node node, String displayName) {
        this(datatype, node);
        this.aDisplayName = displayName;
    }    
    
    //============================================================
    // Instance Methods
    //============================================================
    public void setShowReadOnlyDialog(boolean enable) {
        showReadOnlyDialog = enable;
    } 
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     */
    @Override
    public String getCategory() {
        return CATEGORY;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public CellEditor createPropertyEditor(Composite parent) {

        if (!isEditableProperty) {
            return null;
        } 
        
        // check failure conditions: ModelResource is null, or read-only, or not open in an editor 
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
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
                    ModelEditorManager.open(eObject, true);
                }
                return null;
            }
        }

        return super.createPropertyEditor(parent);
    }

    @Override
    public String getDisplayName() {
        return this.aDisplayName != null ? this.aDisplayName : super.getDisplayName();
    }

}
