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
import net.sourceforge.sqlexplorer.gef.figures.NoteFigure;
import net.sourceforge.sqlexplorer.gef.model.Note;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * @author MAZZOLINI To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class NotePart extends AbstractGraphicalEditPart implements PropertyChangeListener {
    @Override
    protected void refreshVisuals() {
        ((NoteFigure)getFigure()).setText(((Note)getModel()).getText());
        Point loc = getNote().getLocation();
        Dimension size = getNote().getSize();
        if (size == null) size = new Dimension(20, 20);
        if (loc == null) loc = new Point(10, 10);
        Rectangle r = new Rectangle(loc, size);

        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), r);

    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        NoteFigure noteFigure = new NoteFigure(getNote());
        return noteFigure;
    }

    public Note getNote() {
        return (Note)getModel();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableComponentEditPolicy());
    }

    @Override
    public void activate() {
        if (isActive()) return;
        super.activate();
        ((Note)getModel()).addPropertyChangeListener(this);
    }

    @Override
    public void deactivate() {
        if (!isActive()) return;
        super.deactivate();
        ((Note)getModel()).removePropertyChangeListener(this);

    }

    public void propertyChange( PropertyChangeEvent propertychangeevent ) {
        // String s = propertychangeevent.getPropertyName();
        refresh();
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
        if (request.getType() == RequestConstants.REQ_OPEN) {
            exec();
            return;
        }
        super.performRequest(request);
        return;
    }

}
