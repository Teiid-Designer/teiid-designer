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
import java.util.List;
import net.sourceforge.sqlexplorer.gef.figures.TableFigure;
import net.sourceforge.sqlexplorer.gef.model.Table;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * @author Mazzolini
 */
public class TableEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

    @Override
    protected List getModelSourceConnections() {
        return getTable().getSourceConnections();
    }

    /**
     * Returns a list of connections for which this is the target.
     * 
     * @return List of connections.
     */
    @Override
    protected List getModelTargetConnections() {
        return getTable().getTargetConnections();
    }

    @Override
    protected ConnectionEditPart createConnection( Object obj ) {
        return super.createConnection(obj);
    }

    public Table getTable() {
        return (Table)getModel();
    }

    @Override
    protected void refreshVisuals() {
        Table tb = (Table)getModel();
        TableFigure fig = (TableFigure)getFigure();
        if (tb.isShowQualifiedName()) fig.setLabel(tb.getQualifiedName());
        else fig.setLabel(tb.getSimpleName());
        (fig).setColumnTypeVisible((tb).isShowColumnDetail());
        Point loc = getTable().getLocation();
        Dimension size = getTable().getSize();
        Rectangle r = new Rectangle(loc, size);

        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), r);
        super.refreshSourceConnections();
        super.refreshTargetConnections();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        TableFigure tableFigure = new TableFigure(getTable());
        return tableFigure;

    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new TableComponentEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TableDirectEditPolicy());
    }

    final protected String mapConnectionAnchorToTerminal( ConnectionAnchor c ) {
        return ((TableFigure)getFigure()).getConnectionAnchorName(c);
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

    @Override
    public void activate() {
        if (isActive()) return;
        super.activate();
        ((Table)getModel()).addPropertyChangeListener(this);
    }

    @Override
    public void deactivate() {
        if (!isActive()) return;
        super.deactivate();
        ((Table)getModel()).removePropertyChangeListener(this);

    }

    public void propertyChange( PropertyChangeEvent propertychangeevent ) {
        refresh();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getSourceConnectionAnchor( ConnectionEditPart connection ) {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getTargetConnectionAnchor( ConnectionEditPart connEditPart ) {
        return new ChopboxAnchor(getFigure());
    }

    public ConnectionAnchor getSourceConnectionAnchor( Request request ) {
        return new ChopboxAnchor(getFigure());
    }

    public ConnectionAnchor getTargetConnectionAnchor( Request request ) {
        return new ChopboxAnchor(getFigure());
    }
}
