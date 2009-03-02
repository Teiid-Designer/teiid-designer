/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramToolBarManager;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.search.SearchPageUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipAction;
import com.metamatrix.modeler.relationship.ui.custom.CustomDiagramModelFactory;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AddTransformationSource
 */
public class AddToDiagramAction extends RelationshipAction {
    private DiagramEditor editor;
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
        if( editor != null ) {
    
            // Need to get the current diagram
            DiagramModelNode diagramNode = editor.getCurrentModel();
            // Need to get ahold of the CustomDiagramModelFactory
            CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
            // And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            
            if( diagramNode != null && modelFactory != null ) {
                modelFactory.add(SelectionUtilities.getSelectedEObjects(focusedSelection), diagramNode);
            }
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
        this.editor = editor;
    }
    
    private boolean shouldEnable() {
        boolean enable = false;
        
        enable = isWritable() && selectedObjectsAreValid();
        
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
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null ) {
                EObject diagram = currentDiagram.getModelObject();
                if( !ModelObjectUtilities.isReadOnly(diagram)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean selectedObjectsAreValid() {
    	List selectionList = new ArrayList(SelectionUtilities.getSelectedEObjects(focusedSelection));
    	if( selectionList.isEmpty() ) {
    		return false;
    	}
    	Iterator iter = selectionList.iterator();
    	Object nextObject = null;
    	while( iter.hasNext() ) {
    		nextObject = iter.next();
    		if( nextObject instanceof Relationship ||
    			nextObject instanceof RelationshipType ||
    			nextObject instanceof RelationshipFolder ) {
    			// do nothing. This is fine.
    		} else {
    			return false;
    		}
    	}
    	
    	return true;
    }
}
