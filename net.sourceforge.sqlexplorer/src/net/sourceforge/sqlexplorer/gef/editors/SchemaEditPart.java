/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.sqlexplorer.gef.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;

/**
 * @author Mazzolini
 */
public class SchemaEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
    @Override
    protected List getModelChildren() {

        Schema schema = (Schema)getModel();
        return Arrays.asList(schema.getElements());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        FreeformLayer freeformlayer = new FreeformLayer();
        freeformlayer.setLayoutManager(new FreeformLayout());
        freeformlayer.setBorder(new MarginBorder(5));
        freeformlayer.setBackgroundColor(ColorConstants.white);
        freeformlayer.setOpaque(true);
        return freeformlayer;

    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new SchemaComponentEditPolicy());
        installEditPolicy(EditPolicy.CONTAINER_ROLE, new SchemaContainerEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new SchemaXYLayoutEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new SchemaDirectEditPolicy());

    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
     */
    @Override
    public DragTracker getDragTracker( Request request ) {
        if (request instanceof SelectionRequest && ((SelectionRequest)request).getLastButtonPressed() == 3) return new DeselectAllTracker(
                                                                                                                                          this);
        return new MarqueeDragTracker();
    }

    public void exec() {

        CommandStack commandstack = getRoot().getViewer().getEditDomain().getCommandStack();
        Command command = getCommand(new DirectEditRequest());
        if (command != null) {
            if (command.canExecute()) commandstack.execute(command);
        }
    }

    @Override
    public void performRequest( Request request ) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            exec();
            return;
        }
        super.performRequest(request);
        return;
    }

    @Override
    protected void refreshVisuals() {
        refreshStructure();
        refreshLayout();
    }

    protected void refreshLayout() {

    }

    protected void refreshStructure() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#activate()
     */
    @Override
    public void activate() {
        if (isActive()) return;
        super.activate();
        ((Schema)getModel()).addPropertyChangeListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#deactivate()
     */
    @Override
    public void deactivate() {
        if (!isActive()) return;
        super.deactivate();
        ((Schema)getModel()).removePropertyChangeListener(this);

    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange( PropertyChangeEvent propertychangeevent ) {
        // String s = propertychangeevent.getPropertyName();
        refresh();
    }

}
