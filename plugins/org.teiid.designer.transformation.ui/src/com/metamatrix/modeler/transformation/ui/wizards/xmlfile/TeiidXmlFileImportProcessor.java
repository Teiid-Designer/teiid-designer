/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidMetadataImportProcessor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

public class TeiidXmlFileImportProcessor extends TeiidMetadataImportProcessor implements UiConstants {
	
	public TeiidXmlFileImportProcessor(TeiidMetadataImportInfo info, Shell shell) {
		super(info, shell);
	}

    protected ModelResource createViewsInExistingModel(String relationalModelName) throws ModelerCoreException  {
    	if( getInfo().getViewModelLocation() != null && getInfo().getViewModelName() != null ) {
    		IPath modelPath = getInfo().getViewModelLocation().append(getInfo().getViewModelName());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
            ModelEditor editor = ModelEditorManager.getModelEditorForFile( (IFile)item.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();
                XmlFileViewModelFactory factory = new XmlFileViewModelFactory();
                
                for( TeiidXmlFileInfo info : this.getInfo().getXmlFileInfos()) {
                	if( info.doProcess() ) {
                		factory.createViewTable(editor.getModelResource(), info, relationalModelName);
                	}
                }
                
                editor.getModelResource().save(null, true);
                
                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                
                return editor.getModelResource();
            }
    	}
    	
    	return null;
    }
    
    protected ModelResource createViewsInNewModel(String sourceModelName) throws ModelerCoreException {
    	XmlFileViewModelFactory factory = new XmlFileViewModelFactory();
    	
    	ModelResource modelResource = factory.createViewRelationalModel(this.getInfo().getViewModelLocation(), this.getInfo().getViewModelName());
        for( TeiidXmlFileInfo info : this.getInfo().getXmlFileInfos()) {
        	if( info.doProcess() ) {
        		factory.createViewTable(modelResource, info, sourceModelName);
        	}
        }

        return modelResource;
    }
}
