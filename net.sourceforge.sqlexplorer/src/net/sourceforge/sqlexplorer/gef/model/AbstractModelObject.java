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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;



/**
 * @author Mazzolini
 *
 */
public abstract class AbstractModelObject implements Cloneable, Serializable,PropertyChangeListener,IGefObject,IPropertySource{
	protected ArrayList inputs = new ArrayList (7);
	protected ArrayList  outputs  = new ArrayList (4);
	public static final String
		//CHILDREN = "Children", 	//$NON-NLS-1$
		INPUTS = "inputs",	//$NON-NLS-1$
		OUTPUTS = "outputs";
		
	public void connectInput(Link w) {
		inputs.add(w);
		//update();
		firePropertyChange(INPUTS,null,w);
		//firePropertyChange(INPUTS, w);
	}
	
	public void connectOutput(Link w) {
		outputs.add(w);
		firePropertyChange(OUTPUTS,null,w);
		//update();
		//fireStructureChange(OUTPUTS, w);
	}

	public void disconnectInput(Link w) {
		inputs.remove(w);
		firePropertyChange(INPUTS,null,w);
		//update();
		//fireStructureChange(INPUTS,w);
	}

	public void disconnectOutput(Link w) {
		outputs.remove(w);
		firePropertyChange(OUTPUTS,null,w);
		//update();
		//fireStructureChange(OUTPUTS,w);
	}
	static final long serialVersionUID = 1;
	public Object getEditableValue(){
		return this;
	}
	public void setPropertyValue(Object id, Object value){
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors(){
		
		return new IPropertyDescriptor[0];
	}
	public void resetPropertyValue(Object obj){
	}
	
	public Object getPropertyValue(Object propName){
		return null;
	}

	final Object getPropertyValue(String propName){
		return null;
	}

	public boolean isPropertySet(Object propName){
		return isPropertySet((String)propName);
	}

	final boolean isPropertySet(String propName){
		return true;
	}

	public AbstractModelObject(){
		pcs=new PropertyChangeSupport(this);

	}
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		
		
	}
	private PropertyChangeSupport pcs;
	public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
	{
		pcs.addPropertyChangeListener(propertychangelistener);
	}

	public void addPropertyChangeListener(String s, PropertyChangeListener propertychangelistener)
	{
		pcs.addPropertyChangeListener(s, propertychangelistener);
	}

	public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
	{
		pcs.removePropertyChangeListener(propertychangelistener);
	}

	public void removePropertyChangeListener(String s, PropertyChangeListener propertychangelistener)
	{
		pcs.removePropertyChangeListener(s, propertychangelistener);
	}

	

	protected void firePropertyChange(PropertyChangeEvent propertychangeevent)
	{
		//;
		pcs.firePropertyChange(propertychangeevent);
	}

	protected void firePropertyChange(String s, Object obj, Object obj1)
	{
		//System.out.println("firePropertyChange "+pcs.getPropertyChangeListeners().length);
//		for(int i=0;i<pcs.getPropertyChangeListeners().length;i++)
//		{
			//System.out.println(pcs.getPropertyChangeListeners()[i].getClass());
			
//		}
		pcs.firePropertyChange(s, obj, obj1);
	}

	protected void firePropertyChange(String s, int i, int j)
	{
		pcs.firePropertyChange(s, i, j);
	}

	protected void firePropertyChange(String s, boolean flag, boolean flag1)
	{
		pcs.firePropertyChange(s, flag, flag1);
	}

	@Override
    protected Object clone()
	{
		try
		{
			AbstractModelObject abstractmodelobject = (AbstractModelObject)super.clone();
			abstractmodelobject.pcs = new PropertyChangeSupport(abstractmodelobject);
			return abstractmodelobject;
		}
		catch(CloneNotSupportedException clonenotsupportedexception)
		{
			throw new InternalError(clonenotsupportedexception.toString());
		}
	}
		
	/**
	 * @param point
	 */
	abstract public void setLocation(Point point) ;
	/**
	 * @param point
	 */
	abstract public void setSize(Dimension dim) ;
	/**
	 * @return
	 */
	abstract public Dimension getSize();
	/**
	 * @return
	 */
	abstract public Point getLocation() ;
	/**
	 * @param tb
	 */
	abstract public boolean removeChild(AbstractModelObject tb) ;
	/**
	 * @param tb
	 */
	abstract  public void addChild(AbstractModelObject tb) ;
	abstract public void addChild(int i, AbstractModelObject inamedobject) ;
}
