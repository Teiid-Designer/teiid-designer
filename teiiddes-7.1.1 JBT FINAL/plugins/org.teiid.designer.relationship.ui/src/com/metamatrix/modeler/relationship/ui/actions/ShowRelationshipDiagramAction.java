/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.diagram.RelationshipDiagramUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShowRelationshipDiagramAction extends RelationshipAction implements UiConstants{
    
	//============================================================================================================================
	// Constants

	//============================================================================================================================
	// Constructors

	/**
	 * Construct an instance of ShowDependencyDiagramAction.
	 * 
	 */
	public ShowRelationshipDiagramAction() {
		super(null);
		this.setUseWaitCursor(false);
	}

	//============================================================================================================================
	// ISelectionListener Methods

	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 * @since 4.0
	 */
	@Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		super.selectionChanged(part, selection);
		determineEnablement();
	}

	//============================================================================================================================
	// Action Methods

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 * @since 4.0
	 */
	@Override
    protected void doRun() {
		final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
		if( eObject != null ) {
			// See if it has a transformation object:
			ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
			if( modelResource != null ) {
				ModelEditor editor = getModelEditorForObject(eObject, true);      
            
				if( editor != null ) {
					Diagram depDiagram = getRelationshipDiagram(modelResource, eObject);
					if( depDiagram != null )
						editor.openModelObject(depDiagram);
				}

			}
		}
		determineEnablement();
	}

	private Diagram getRelationshipDiagram(ModelResource modelResource, EObject eObject) {
		Diagram depDiagram = RelationshipDiagramUtil.getRelationshipDiagram(eObject);
    
		return depDiagram;
	}

	//============================================================================================================================
	// Declared Methods

	/**
	 * @since 4.0
	 */
	private void determineEnablement() {
		final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
		if( eObject != null && RelationshipDiagramUtil.isRelationshipObject(eObject) ) {
			setEnabled(true);
			return;
		}

		setEnabled(false);
	}

	private static ModelEditor getModelEditorForObject(EObject object, boolean forceOpen) {
		ModelEditor result = null;
    
		IFile file = null; 
		ModelResource mdlRsrc = ModelUtilities.getModelResourceForModelObject(object);
		if ( mdlRsrc != null){
			file = (IFile) mdlRsrc.getResource();
			result = getModelEditorForFile(file, forceOpen);
		}
		return result;
	}

	// =============================================
	// Private Methods

	private static ModelEditor getModelEditorForFile(IFile file, boolean forceOpen) {
		ModelEditor result = null;
		if ( file != null ) {
			IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
			if ( page != null ) {
				// look through the open editors and see if there is one available for this model file.
				IEditorReference[] editors = page.getEditorReferences();
				for ( int i=0 ; i<editors.length ; ++i ) {
					IEditorPart editor = editors[i].getEditor(false);
					if ( editor != null ) {
						IEditorInput input = editor.getEditorInput();
						if ( input instanceof IFileEditorInput ) {
							if ( file.equals(((IFileEditorInput) input).getFile()) ) {
								// found it;
								if ( editor instanceof ModelEditor ) {
									result = (ModelEditor) editor;
								}
								break;
							}
						}
					}
				}
        
				if ( result == null && forceOpen) {

					// there is no editor open for this object.  Open one and hand it the double-click target.
					try {
                
						IEditorPart editor = IDE.openEditor(page, file);
						if ( editor instanceof ModelEditor ) {
							result = (ModelEditor) editor;
						}

					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
}
