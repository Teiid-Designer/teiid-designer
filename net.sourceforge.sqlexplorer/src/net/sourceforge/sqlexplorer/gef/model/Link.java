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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Link extends AbstractModelObject {
	String label="FK_1_PROVA";
	Fk fk;
	
	protected static IPropertyDescriptor[] descriptors = null;
		
		
	public static String ID_FK_NAME = "Foreign Key Name";  //$NON-NLS-1$
	public static String ID_FK_TABLE = "FK Table";//$NON-NLS-1$
	public static String ID_PK_TABLE="PK Table";//$NON-NLS-1$
	public static String ID_PK = "PK";//$NON-NLS-1$
	public static String ID_FK_UPDATE_RULE = "FK_update_rule";//$NON-NLS-1$
	public static String ID_FK_DELETE_RULE = "FK_delete_rule";//$NON-NLS-1$
	public static String ID_PK_SCHEMA="PK SCHEMA";
	public static String ID_PK_CATALOG="PK CATALOG";
	public static String ID_FK_SCHEMA="FK_SCHEMA";
	public static String ID_FK_CATALOG="FK CATALOG";
	
	static{
		descriptors = new IPropertyDescriptor[]{
			new PropertyDescriptor(ID_FK_NAME,"Foreign Key"),
			new PropertyDescriptor(ID_FK_TABLE,"Foreign Key Table"),
			new PropertyDescriptor(ID_PK,"Primary Key"),
			new PropertyDescriptor(ID_PK_TABLE,"Primary Key Table"),
			new PropertyDescriptor(ID_FK_UPDATE_RULE, "Foreign Key Update Rule"),
			new PropertyDescriptor(ID_FK_DELETE_RULE, "Foreign Key Delete Rule"),
			new PropertyDescriptor(ID_PK_SCHEMA, "Primary Key Schema"),
			new PropertyDescriptor(ID_PK_CATALOG, "Primary Key Catalog"),
			new PropertyDescriptor(ID_FK_SCHEMA, "Foreign Key Schema"),
			new PropertyDescriptor(ID_FK_CATALOG, "Foreign Key Catalog")
		};
	}
	@Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	@Override
    public Object getPropertyValue(Object propName) {
		if(ID_FK_NAME.equals(propName)){
			return fk.fkName;
		}
		else if(ID_PK.equals(propName)){
			return fk.pkName;
		}else if(ID_PK_TABLE.equals(propName)){
			return fk.tabName;
		}
		else if(ID_FK_DELETE_RULE.equals(propName)){
			return fk.deleteRuleDes;
		}	
		else if(ID_FK_UPDATE_RULE.equals(propName)){
			return fk.updateRuleDes;
		}else if(ID_FK_TABLE.equals(propName)){
			return fk.fkTable;
		}else if(ID_PK_SCHEMA.equals(propName)){
			return fk.pkTableSchema;
		}else if(ID_PK_CATALOG.equals(propName)){
			return fk.pkTableCatalog;
		}
		else if(ID_FK_SCHEMA.equals(propName)){
			return fk.fkTableSchema;
		}else if(ID_FK_CATALOG.equals(propName)){
			return fk.fkTableCatalog;
		}
		return null;
	}
	@Override
    public void setPropertyValue(Object id, Object value){
	}
	
	
	static final long serialVersionUID = 1;
	protected List bendpoints = new ArrayList();
	protected AbstractModelObject	source,	target;
	protected String sourceTerminal,targetTerminal;
	public List getBendpoints() {
		return bendpoints;
	}
	public void insertBendpoint(int index, Bendpoint point) {
		getBendpoints().add(index, point);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void setBendpoint(int index, Bendpoint point) {
		getBendpoints().set(index, point);
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}

	public void setBendpoints(Vector points) {
		bendpoints = points;
		firePropertyChange("bendpoint", null, null);//$NON-NLS-1$
	}
	public String getSourceTerminal(){
		return sourceTerminal;
	}

	public String getTargetTerminal(){
		return targetTerminal;
	}

	/**
	 * 
	 */
	public Link() {
		super();
		//System.out.println("Link creato");
	}

	/**
	 * 
	 */
	public Link(Fk fk) {
		this.fk=fk;
		
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getName()
	 */
	public String getName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getChildAt(int)
	 */
	public IGefObject getElementAt(int i) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getChildCount()
	 */
	public int getElementsCount() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#indexOf(net.sourceforge.sqlexplorer.gef.model.INamedObject)
	 */
	public int indexOf(IGefObject inamedobject) {
		
		return -1;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getParent()
	 */
	public IGefObject getParent() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getChildren()
	 */
	public IGefObject[] getElements() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#setParent(net.sourceforge.sqlexplorer.gef.model.INamedObject)
	 */
	public void setParent(IGefObject schema) {	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#setLocation(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
    public void setLocation(Point point) {
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#setSize(org.eclipse.draw2d.geometry.Dimension)
	 */
	@Override
    public void setSize(Dimension dim) {	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#getSize()
	 */
	@Override
    public Dimension getSize() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#getLocation()
	 */
	@Override
    public Point getLocation() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#removeChild(net.sourceforge.sqlexplorer.gef.model.AbstractModelObject)
	 */
	@Override
    public boolean removeChild(AbstractModelObject tb) {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#addChild(net.sourceforge.sqlexplorer.gef.model.AbstractModelObject)
	 */
	@Override
    public void addChild(AbstractModelObject tb) {
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#addChild(int, net.sourceforge.sqlexplorer.gef.model.AbstractModelObject)
	 */
	@Override
    public void addChild(int i, AbstractModelObject inamedobject) {
		
	}
	public void setSource(AbstractModelObject e){
		Object old = source;
		source = e;
		firePropertyChange("source", old, source);//$NON-NLS-1$
	}

	public void setSourceTerminal(String s){
		Object old = sourceTerminal;
		sourceTerminal = s;
		firePropertyChange("sourceTerminal", old, sourceTerminal);//$NON-NLS-1$
	}

	public void setTarget(AbstractModelObject e){
		target = e;
	}

	public void setTargetTerminal(String s){
		targetTerminal = s;
	}
	public AbstractModelObject getSource(){
		return source;
	}

	public AbstractModelObject getTarget(){
		return target;
	}

	public void attachSource(){
		if (getSource() == null)
			return;
		getSource().connectOutput(this);
	}

	public void attachTarget(){
		if (getTarget() == null)
			return;
		getTarget().connectInput(this);
	}

	public void detachSource(){
		if (getSource() == null)
			return;
		getSource().disconnectOutput(this);
	}

	public void detachTarget(){
		if (getTarget() == null)
			return;
		getTarget().disconnectInput(this);
	}
}
