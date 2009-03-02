/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.DragEditPartsTracker;

import com.metamatrix.modeler.diagram.ui.notation.uml.part.SetAssociationCommand;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
}
