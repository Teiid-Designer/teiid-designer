/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.diagram.ui.custom.CustomDiagramContentHelper;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramToolBarManager;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.search.SearchPageUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * AddTransformationSource
 *
 * @since 8.0
 */
public class AddToDiagramAction extends DiagramAction {

    private DiagramEditor diagramEditor;
    private ActionContributionItem thisToolItem;
    
    private DiagramToolBarManager toolBarManager;

    private ISelection oldSelection;
    
    private ISelection focusedSelection;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public AddToDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.ADD_TO_DIAGRAM));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        // Save off current selection to old if part is ModelEditor and selection == null
        // This indicates a focus change and part activation.
        if (thePart != null && thePart instanceof ModelEditor) {
            oldSelection = getSelection();
        }
        
        ISelection selection = theSelection;
        // Now we see if the selection is from Search Results?
        List searchResults = SearchPageUtil.getEObjectsFromSearchSelection(theSelection);
        if( searchResults != null ) {
            if( searchResults.isEmpty() ) {
                selection = new StructuredSelection();
            } else {
                selection = new StructuredSelection(searchResults);
            }
        }
        
        // initialize abstract base class info
        super.selectionChanged(thePart, selection);
        
        setFocusedSelection();
        
        setEnabled(shouldEnable());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if( diagramEditor != null ) {
            // Need to get the current diagram
            DiagramModelNode diagramNode = diagramEditor.getCurrentModel();
            Diagram diagram = (Diagram)diagramNode.getModelObject();
            
            CustomDiagramContentHelper.addToCustomDiagram(diagram, SelectionUtilities.getSelectedEObjects(focusedSelection), diagramEditor, this);
        }
            
        // Reset toolBarManager, so noone else will find the focused Item.
        // This should be called because a focused item should be in the toolbar any time you 
        // select a toolbar button.
        toolBarManager.resetFocusedToolItem();
        
        // Need to set the focused selection back to the current selection.
        // This will happen because the toolBarManager's focused item is now null (previous call above).
        setFocusedSelection();
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.diagramEditor = editor;
    }
    
    private boolean shouldEnable() {
        boolean enable = false;
        
        enable = isWritable() && allObjectsAddable();
        
        return enable;
    }
    
    public boolean wasToolBarItemSelected() {
        if( toolBarManager != null && toolBarManager.getFocusedToolItem() != null && thisToolItem != null ) {
            if( thisToolItem.equals(toolBarManager.getFocusedToolItem()))
                return true;
        }
        
        return false;
    }
    
    public void setItem(ActionContributionItem aci) {
        thisToolItem = aci;
//        if( toolBarManager != null ) {
//            thisToolItem = toolBarManager.getActionContributionItem(this);
//        }
    }
    
    private void setFocusedSelection() {
        if( wasToolBarItemSelected() )
            focusedSelection = oldSelection;
        else
            focusedSelection = getSelection();
    }
    
    public void setToolBarManager(DiagramToolBarManager tbManager) {
        toolBarManager = tbManager;
    }
    
    
    private boolean isWritable() {
        if( diagramEditor != null ) {
            DiagramModelNode currentDiagram = diagramEditor.getCurrentModel();
            if( currentDiagram != null ) {
                EObject diagram = currentDiagram.getModelObject();
                if( !ModelObjectUtilities.isReadOnly(diagram)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean allObjectsAddable() {
    	boolean allOK = true;

    	List selectedEObjects = SelectionUtilities.getSelectedEObjects(focusedSelection);
    	if( !selectedEObjects.isEmpty() ) {
            List applicableObjectsToAdd = CustomDiagramContentHelper.getApplicableDiagramEObjects(selectedEObjects);
	    	Object nextObject = null;
	    	Iterator iter = applicableObjectsToAdd.iterator();
			MetamodelAspect someAspect = null;
	    	while( iter.hasNext() && allOK) { 
	    		nextObject = iter.next();
	    		if( nextObject instanceof Diagram ) {
	    			allOK = false;
	    		} else {
					someAspect = ModelObjectUtilities.getUmlAspect((EObject)nextObject);
	        
					if ( someAspect == null || 
						 !(someAspect instanceof UmlPackage ||
						   someAspect instanceof UmlClassifier) ) {
						allOK = false;
					}
                    if( allOK ) {
                        // Check to see if contents already includes this object
                        if( diagramEditor != null && diagramEditor.getCurrentModel() != null ) {
                            allOK = !(DiagramUiUtilities.diagramContainsEObject((EObject)nextObject, diagramEditor.getCurrentModel()));
                        }
                    }
	    		}
	    	}
    	} else
    		allOK = false;
    		
    	return allOK;
    }
    
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if( requiresEditorForRun() ) {
            List allSelectedEObjects = SelectionUtilities.getSelectedEObjects(focusedSelection);
            if( allSelectedEObjects != null &&  !allSelectedEObjects.isEmpty() ) {     
                EObject eObject = diagramEditor.getCurrentModel().getModelObject();
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
                if( mr != null ) {
                    ModelEditorManager.activate(mr, false);
                }
            }
        }
        return true;
    }
}
