/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.IActionConstants;
import com.metamatrix.ui.actions.IActionConstants.EclipseGlobalActions;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class DiagramKeyHandler extends GraphicalViewerKeyHandler {

    /**
     * @param viewer
     */
    public DiagramKeyHandler( GraphicalViewer viewer ) {
        super(viewer);

    }

    /**
     * @see org.eclipse.gef.KeyHandler#keyPressed(org.eclipse.swt.events.KeyEvent)
     */
    @Override
    public boolean keyPressed( KeyEvent event ) {
        switch (event.keyCode) {
            case SWT.DEL: {
                Object action = ((DiagramViewer)getViewer()).getEditor().getActionBarContributor().getGlobalActions().get(IActionConstants.EclipseGlobalActions.DELETE);
                if (action != null && action instanceof AbstractAction) {
                    AbstractAction deleteAction = (AbstractAction)action;
                    if (deleteAction.isEnabled()) deleteAction.run();
                } else if (action == null) {
                    // action = ((DiagramEditor)((DiagramViewer)getViewer()).getEditor()).getAction( EclipseGlobalActions.DELETE
                    // );

                    // ModelerActionService mas = (ModelerActionService)DiagramUiPlugin.getDefault().getActionService(
                    // getWorkbenchWindow() );
                    ActionService actionService = DiagramUiPlugin.getDefault().getActionService(DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage());
                    try {
                        action = actionService.getAction(EclipseGlobalActions.DELETE);

                        if (((IAction)action).isEnabled()) ((IAction)action).run();
                    } catch (CoreException e) {
                        // XXX Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
                break;
        }
        // BML 1/21/05 Defect 15800
        // This is a hack to fix the ArrayIndexOutOfBounds which can occur when a diagram editor is in a state
        // of building and someone does a cntl-shift-F4. AbstractEditPartViewer.getFocusEditPart() is throwing
        // an AIOOB exception because it assumes this will only happen after full construction including
        // setting contents of the editor.
        List selectedEPs = ((DiagramViewer)getViewer()).getSelectedEditParts();
        if (selectedEPs == null || selectedEPs.isEmpty() || selectedEPs.size() == 0) return false;

        return super.keyPressed(event);
    }

}
