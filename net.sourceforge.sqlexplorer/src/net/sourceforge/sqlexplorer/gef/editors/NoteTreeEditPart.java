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
import java.util.Collections;
import java.util.List;

import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.gef.model.Note;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NoteTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener{
	static Image image=ImageDescriptor.createFromURL(SqlexplorerImages.getAboutIcon()).createImage();
	/**
	 * @param model
	 */
	public NoteTreeEditPart(Object model) {
		
		super(model);
	}

	
	@Override
    public void activate(){
		super.activate();
		getLogicSubpart().addPropertyChangeListener(this);
	}
	@Override
    protected void createEditPolicies() {
		EditPolicy component=null;
		//if (getModel() instanceof LED)
		//	component = new LEDEditPolicy();
		//else
			component = new ComponentEditPolicy(){};
		installEditPolicy(EditPolicy.COMPONENT_ROLE, component);
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new LogicTreeEditPolicy());
	}

	@Override
    public void deactivate(){
		getLogicSubpart().removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Returns the model of this as a LogicSubPart.
	 *
	 * @return Model of this.
	 */
	protected Note getLogicSubpart() {
		return (Note)getModel();
	}

	/**
	 * Returns <code>null</code> as a Tree EditPart holds
	 * no children under it.
	 *
	 * @return <code>null</code>
	 */
	@Override
    protected List getModelChildren() {
		return Collections.EMPTY_LIST;
	}

	public void propertyChange(PropertyChangeEvent change){
		
		refreshVisuals();
	}

	/**
	 * Refreshes the Widget of this based on the property
	 * given to update. All major properties are updated
	 * irrespective of the property input.
	 *
	 * @param property  Property to be refreshed.
	 */
	@Override
    protected void refreshVisuals(){

		if (getWidget() instanceof Tree)
			return;

		TreeItem item = (TreeItem)getWidget();
		if (image != null)
			image.setBackground(item.getParent().getBackground());
		setWidgetImage(image);
		setWidgetText(getLogicSubpart().toString());
	}	

}
