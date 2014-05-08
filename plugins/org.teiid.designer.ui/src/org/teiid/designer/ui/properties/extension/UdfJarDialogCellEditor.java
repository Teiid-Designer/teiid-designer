/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.properties.extension;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class UdfJarDialogCellEditor extends DialogCellEditor {

    private EObject eObject;
    
    UdfJarDialogCellEditor(Composite parent, EObject eObj) {
        super(parent);
        this.eObject = eObj;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
     */
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        // Determine if there is a lib folder under the project
        IProject proj = getProject(this.eObject);
        
        Object value = getValue();
        
        String selectedFile = VdbFileDialogUtil.selectFile(cellEditorWindow.getShell(), proj, VdbFolders.UDF);
        
        if( value != null && (selectedFile == null ||  selectedFile.length() == 0) ) {
        	selectedFile = (String)value;
        }
        
        return selectedFile;
    }
    
    /*
     * Get project for the supplied EOjbect
     * @param eObj the supplied EObject
     * @return the project that the EObject is within
     */
    private IProject getProject(EObject eObj) {
        IProject project = null;
        if(eObj!=null) {
            ModelResource mdlResrc = ModelUtilities.getModelResource(eObj);
            project = mdlResrc.getModelProject().getProject();
        }
        return project;
    }
        
}
