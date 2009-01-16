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


import net.sourceforge.sqlexplorer.gef.model.IGefObject;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mazzolini
 *
 */
public class ModelObjectTreeEditPart
	extends AbstractTreeEditPart
	implements PropertyChangeListener {

	 public ModelObjectTreeEditPart(Object obj)
    {
        super(obj);
     
    }


    @Override
    public void activate()
    {

    }

    @Override
    public Object getAdapter(Class class1)
    {
 
      return   super.getAdapter(class1);

    }

    @Override
    protected void createEditPolicies()
    {
       // installEditPolicy("ContainerEditPolicy", new ModelObjectContainerEditPolicy());
        //installEditPolicy("TreeContainerEditPolicy", new ModelObjectTreeContainerEditPolicy());
        //installEditPolicy("ComponentEditPolicy", new ModelObjectComponentEditPolicy());
        //installEditPolicy("PrimaryDrag Policy", new ModelObjectDragEditPolicy());
    }

    @Override
    public void deactivate()
    {
      
    }

    protected IGefObject getNamedObject()
    {
        return (IGefObject)getModel();
    }

    @Override
    public void setSelected(int i)
    {
    
    }

   

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        refresh();
    }

    @Override
    protected void refreshVisuals()
    {

    }

    @Override
    protected Image getImage()
    {
 
        return null;
    }

   
    public static int c;
}

