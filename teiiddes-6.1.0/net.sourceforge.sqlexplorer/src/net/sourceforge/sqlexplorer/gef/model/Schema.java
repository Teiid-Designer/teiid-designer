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

package net.sourceforge.sqlexplorer.gef.model;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import java.util.ArrayList;




/**
 * @author Mazzolini
 *
 */
public class Schema extends AbstractModelObject{
	static final long serialVersionUID = 1;
	protected static IPropertyDescriptor[] descriptors = null;
	public static String ID_SIZE = "size_property";         //$NON-NLS-1$
	public static String ID_LOCATION = "location_property"; //$NON-NLS-1$

	

	ArrayList ls=new ArrayList();
	/**
	 * 
	 */
	public Schema() {
		
	}

	public IGefObject[] getElements()
	{
		return (IGefObject[])ls.toArray(new IGefObject[ls.size()]);
	}
	
	@Override
    public void addChild(AbstractModelObject table)
	{
		addChild(ls.size(), table);
	}


	public String getAlias() {
		
		return "";
	}

	public String getDescription() {
		
		return "";
	}

	public String getName() {
		
		return "";
	}

	public IGefObject getElementAt(int i) {
		return (IGefObject)ls.get(i);

	}

	public int getElementsCount() {
		
		return ls.size();
	}

	public int indexOf(IGefObject inamedobject) {
		return ls.indexOf(inamedobject);

	}

	@Override
    public boolean removeChild(AbstractModelObject inamedobject) {
		boolean b=ls.remove(inamedobject);
		this.firePropertyChange("size",ls.size()+1,ls.size());
		return b;
	}

	@Override
    public void addChild(int i, AbstractModelObject iObj) {
		iObj.setParent(this);
		
		iObj.addPropertyChangeListener(this);
		ls.add(i, iObj);
		firePropertyChange("added Child", null, iObj);

	}



	public IGefObject getParent() {
		return null;
	}


	public void setParent(IGefObject schema) {
		
		
	}


	@Override
    public void setLocation(Point point) {
		
		
	}


	@Override
    public void setSize(Dimension dim) {
		
	}


	@Override
    public Dimension getSize() {
		
		return null;
	}


	@Override
    public Point getLocation() {
		
		return null;
	}

}
