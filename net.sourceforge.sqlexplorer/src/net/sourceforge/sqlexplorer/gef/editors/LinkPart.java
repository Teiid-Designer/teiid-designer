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

import net.sourceforge.sqlexplorer.gef.commands.BendpointCommand;
import net.sourceforge.sqlexplorer.gef.commands.CreateBendpointCommand;
import net.sourceforge.sqlexplorer.gef.commands.DeleteBendpointCommand;
import net.sourceforge.sqlexplorer.gef.commands.MoveBendpointCommand;
import net.sourceforge.sqlexplorer.gef.model.Link;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.requests.BendpointRequest;


public class LinkPart extends AbstractConnectionEditPart implements PropertyChangeListener{

	/**
	 * 
	 */
	public LinkPart() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new WireEndpointEditPolicy());
			//Note that the Connection is already added to the diagram and knows its Router.
		refreshBendpointEditPolicy();
		//installEditPolicy(EditPolicy.CONNECTION_ROLE,new WireEditPolicy());
	}
		private void refreshBendpointEditPolicy(){
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
		else
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new WireBendpointEditPolicy());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	//protected IFigure createFigure() {
		/*PolylineConnection polylineconnection = new PolylineConnection();
		polylineconnection.setConnectionRouter(new ManhattanConnectionRouter());
		polylineconnection.setTargetDecoration(new PolygonDecoration());
		polylineconnection.setForegroundColor(ColorConstants.black);
		return polylineconnection;*/
		@Override
        protected IFigure createFigure() {
			if (getLink() == null)
				return null;
			//PolylineConnection conn = new PolylineConnection();
			//return conn;
			
			PolylineConnection polylineconnection = new PolylineConnection();
			polylineconnection.setConnectionRouter(new ManhattanConnectionRouter());
			polylineconnection.setTargetDecoration(new PolygonDecoration());
			polylineconnection.setForegroundColor(ColorConstants.blue);
			return polylineconnection;

			
		}
	public Link getLink()
	{
		return (Link)getModel();
	}
	@Override
    public void deactivate(){
		getLink().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
    public void deactivateFigure(){
		getFigure().removePropertyChangeListener("connection", this);//$NON-NLS-1$
		super.deactivateFigure();
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
}
/*class WireEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEditPolicy
{

protected Command getDeleteCommand(GroupRequest request) {
	ConnectionCommand c = new ConnectionCommand();
	c.setLink((Link)getHost().getModel());
	return c;
}

}*/
class WireEndpointEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy 
{

@Override
protected void addSelectionHandles(){
	super.addSelectionHandles();
	getConnectionFigure().setLineWidth(2);
}

protected PolylineConnection getConnectionFigure(){
	return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
}

@Override
protected void removeSelectionHandles(){
	super.removeSelectionHandles();
	getConnectionFigure().setLineWidth(1);
}

}
class WireBendpointEditPolicy 
	extends org.eclipse.gef.editpolicies.BendpointEditPolicy
{

@Override
protected Command getCreateBendpointCommand(BendpointRequest request) {
	CreateBendpointCommand com = new CreateBendpointCommand();
	Point p = request.getLocation();
	Connection conn = getConnection();
	
	conn.translateToRelative(p);
	
	com.setLocation(p);
	Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
	Point ref2 = getConnection().getTargetAnchor().getReferencePoint();
	
	conn.translateToRelative(ref1);
	conn.translateToRelative(ref2);
	
	
	com.setRelativeDimensions(p.getDifference(ref1),
					p.getDifference(ref2));
	com.setLink((Link)request.getSource().getModel());
	com.setIndex(request.getIndex());
	return com;
}

@Override
protected Command getMoveBendpointCommand(BendpointRequest request) {
	MoveBendpointCommand com = new MoveBendpointCommand();
	Point p = request.getLocation();
	Connection conn = getConnection();
	
	conn.translateToRelative(p);
	
	com.setLocation(p);
	
	Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
	Point ref2 = getConnection().getTargetAnchor().getReferencePoint();
	
	conn.translateToRelative(ref1);
	conn.translateToRelative(ref2);
	
	com.setRelativeDimensions(p.getDifference(ref1),
					p.getDifference(ref2));
	com.setLink((Link)request.getSource().getModel());
	com.setIndex(request.getIndex());
	return com;
}

@Override
protected Command getDeleteBendpointCommand(BendpointRequest request) {
	BendpointCommand com = new DeleteBendpointCommand();
	Point p = request.getLocation();
	com.setLocation(p);
	com.setLink((Link)request.getSource().getModel());
	com.setIndex(request.getIndex());
	return com;
}

}