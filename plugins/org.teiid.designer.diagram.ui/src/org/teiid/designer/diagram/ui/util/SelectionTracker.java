/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.jface.viewers.deferred.LazySortedCollection;
import org.eclipse.swt.SWT;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.notation.uml.part.SetAssociationCommand;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlClassifierContainerEditPart;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 *
 * @since 8.0
 */
public class SelectionTracker extends DragEditPartsTracker {
    
    /**
     * @param owner
     */
    public SelectionTracker(EditPart owner ) {
        super(owner);
    }
    
    @Override
    protected boolean handleButtonDown(int button) {
        return super.handleButtonDown(button);
    }

    @Override
    protected boolean handleButtonUp(int button) {
//       System.out.println("===>> [SelectionTracker.handleButtonUp()]  EditPart = " + getSourceEditPart() + "  Location = " + getLocation() ); //$NON-NLS-2$ //$NON-NLS-1$
        if (isInState(STATE_DRAG)) {
//            performSelection();
            if (button == 1 && getSourceEditPart().getSelected() != EditPart.SELECTED_NONE)
                getCurrentViewer().reveal(getSourceEditPart());
            setState(STATE_TERMINAL);
            if( getDragMoveDelta().width < 2 && getDragMoveDelta().height < 2 ) {
                // Didn't move and we just clicked.
                getSourceEditPart().performRequest(new Request(RequestConstants.REQ_SELECTION));
            }
            return true;
        }
        if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
            eraseSourceFeedback();
            eraseTargetFeedback();
            performDrag();
            return true;
        }
        return false;
    }

    @Override
    protected boolean handleDoubleClick(int button) {
        // Let's rely on the edit part to make the decision.
        if( getSourceEditPart() instanceof DiagramEditPart ) {
            Request request = new Request(RequestConstants.REQ_DIRECT_EDIT);
            getSourceEditPart().performRequest(request);
        }

        return true;
    }

    @Override
    protected boolean handleDragStarted() {
        return super.handleDragStarted();
    }

    @Override
    protected boolean hasSelectionOccurred() {
        return super.hasSelectionOccurred();
    }
    
    /**
     * Used to cache a command obtained from {@link #getCommand()}.
     * @param c the command
     * @see #getCurrentCommand()
     */
    @Override
    protected void setCurrentCommand(Command c) {
        super.setCurrentCommand(c);
        if( containsSetFKCommand(c) ) {
            setCursor(SharedCursors.CURSOR_TREE_ADD);
        }
    }
    
    private boolean containsSetFKCommand(Command c ) {
        if( c != null && c instanceof CompoundCommand ) {
            CompoundCommand cc = (CompoundCommand)c;
            List allCommands = cc.getCommands();
            Iterator iter = allCommands.iterator();
            Object nextCommand = null;
            while( iter.hasNext() ) {
                nextCommand = iter.next();
                if( nextCommand instanceof SetAssociationCommand )
                    return true;
            }
        } else if( c != null && c instanceof SetAssociationCommand ) {
            return true;
        }
        return false;
        
    }
    
    public Point getLastMouseLocation() {
        return super.getLocation();
    }
    
	/**
	 * Performs the appropriate selection action based on the selection state of
	 * the source and the modifiers (CTRL and SHIFT). If no modifier key is
	 * pressed, the source will be set as the only selection. If the CTRL key is
	 * pressed and the edit part is already selected, it will be deselected. If
	 * the CTRL key is pressed and the edit part is not selected, it will be
	 * appended to the selection set. If the SHIFT key is pressed, the source
	 * will be appended to the selection.
	 */
    @Override
	protected void performSelection() {
		if (hasSelectionOccurred())
			return;
		setFlag(FLAG_SELECTION_PERFORMED, true);
		EditPartViewer viewer = getCurrentViewer();
		List selectedObjects = viewer.getSelectedEditParts();

		if (getCurrentInput().isModKeyDown(SWT.MOD1)) {
			if (selectedObjects.contains(getSourceEditPart()))
				viewer.deselect(getSourceEditPart());
			else
				viewer.appendSelection(getSourceEditPart());
		} else if (getCurrentInput().isShiftKeyDown()) {
			// With shift down, we want check if the parent is a UmlClassifierContainerEditPart
			// and perform previous select to new selection inclusive selection
			handleShiftSelection(viewer, selectedObjects);
		} else
			viewer.select(getSourceEditPart());
	}
    
    private void handleShiftSelection(EditPartViewer viewer, List selectedObjects) {
		int nSelections = selectedObjects.size();
		if( nSelections > 0 ) {
			// find last object
			EditPart lastPart = (EditPart)selectedObjects.get(nSelections-1);

			if( lastPart.getParent() instanceof UmlClassifierContainerEditPart ) {
				EditPart srcPart = getSourceEditPart();
				
				List allChildParts = lastPart.getParent().getChildren();

				int indexOfLastPart = getAttributeIndex(allChildParts, lastPart);
				int indexOfSourcePart = getAttributeIndex(allChildParts, srcPart);
				
				boolean inclusiveSelection = false;
				
				// Analyze the current selections
				// If sequencial, then set inclusive and maybe append selection rather than delselect all
				int indexOfFirstSelection = -1;
				int indexOfLastSelection = -1;
				for( int i=0; i < selectedObjects.size(); i++) {
					int index = getAttributeIndex(allChildParts, (EditPart)selectedObjects.get(i));
					if( index == -1 ) { // Should NOT GET HERE
						break;
					}
					if( i == 0 ) indexOfFirstSelection = index;
					if( i == selectedObjects.size()-1 ) indexOfLastSelection = index;
				}
				
				if( indexOfFirstSelection > -1 && indexOfLastSelection > -1 ) inclusiveSelection = true;

				if( indexOfLastPart > -1 && indexOfSourcePart > -1) {
					if( !inclusiveSelection ) {
						viewer.deselectAll();
					}
					// now select all children index to index

					if( indexOfLastPart > indexOfSourcePart ) {
						int nSelect = indexOfLastPart - indexOfSourcePart;
						for( int i = indexOfSourcePart; i < (indexOfSourcePart + nSelect); i++ ) {
							viewer.appendSelection((EditPart)allChildParts.get(i));
						}
					} else {
						int nSelect = indexOfSourcePart - indexOfLastPart + 1;
						for( int i = indexOfLastPart; i < (indexOfLastPart + nSelect); i++ ) {
							viewer.appendSelection((EditPart)allChildParts.get(i));
						}
					}
				} else {
					viewer.appendSelection(getSourceEditPart());
				}
			}
		} else {
			viewer.appendSelection(getSourceEditPart());
		}
    }
    
    private int getAttributeIndex(List attributes, EditPart partToFind) {
		for(int i=0; i< attributes.size(); i++) {
			EditPart childPart = (EditPart)attributes.get(i);
			if( childPart == partToFind)  return i;
		}
		
		return -1;
    }
}

    