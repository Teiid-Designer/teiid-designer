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
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author MAZZOLINI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Note extends AbstractModelObject {
	static final long serialVersionUID = 1;
	String text="Note";
	protected static IPropertyDescriptor[] descriptors = null;
		public static String ID_SIZE = "size_property";         //$NON-NLS-1$
		public static String ID_LOCATION = "location_property"; //$NON-NLS-1$
		public static String ID_LABEL = "text_property"; //$NON-NLS-1$
		
		static{
			descriptors = new IPropertyDescriptor[]{
				new PropertyDescriptor(ID_SIZE, "Size"),
				new PropertyDescriptor(ID_LOCATION,"Location"),
				new PropertyDescriptor(ID_LABEL,"Text"),
				
			};
		}
		@Override
        public IPropertyDescriptor[] getPropertyDescriptors() {
			return descriptors;
		}
		@Override
        public Object getPropertyValue(Object propName) {
			if (ID_SIZE.equals(propName))
				return new DimensionPropertySource(getSize());
			else if( ID_LOCATION.equals(propName))
				return new LocationPropertySource(getLocation());
			else if( ID_LABEL.equals(propName))
				return new LabelPropertySource(text);
			return null;
		}
		@Override
        public void setPropertyValue(Object id, Object value){
			if (ID_SIZE.equals(id)){
				DimensionPropertySource dimPS = (DimensionPropertySource)value;
				setSize(new Dimension(dimPS.getValue()));
			}
			else if (ID_LOCATION.equals(id)){
				LocationPropertySource locPS = (LocationPropertySource)value;
				setLocation(new Point(locPS.getValue()));
			}else if (ID_LABEL.equals(id)){
				LabelPropertySource labPS=(LabelPropertySource)value;
				setText(labPS.getValue());
			}
		}
	
	/**
		 * @param string
		 */
		private void setText(String string) {
			String oldText=text;
			text = string;
			firePropertyChange("text", oldText, text);
			
		}
		/**
	 * 
	 */
	public Note() {
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getChildAt(int)
	 */
	public IGefObject getElementAt(int i) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getChildren()
	 */
	public IGefObject[] getElements() {
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
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#removeChild(net.sourceforge.sqlexplorer.gef.model.INamedObject)
	 */
	@Override
    public boolean removeChild(AbstractModelObject inamedobject) {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#addChild(int, net.sourceforge.sqlexplorer.gef.model.INamedObject)
	 */
	@Override
    public void addChild(int i, AbstractModelObject inamedobject) {
		

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getParent()
	 */
	public IGefObject getParent() {
		return schema;
	}

	IGefObject schema;

	/**
	 * @param dimension
	 */
	@Override
    public void setSize(Dimension dimension) {
		Dimension dimension1 = n;
		n = dimension;
		firePropertyChange("size", dimension1, dimension);
		
	}

	/**
	 * @param point
	 */
	@Override
    public void setLocation(Point point) {
		Point point1 = m;
		m = point;

		firePropertyChange("location", point1, point);
	}
	private Point m;
	private Dimension n;
	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#setParent(net.sourceforge.sqlexplorer.gef.model.INamedObject)
	 */
	public void setParent(IGefObject schema) {
		this.schema=schema;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#getSize()
	 */
	@Override
    public Dimension getSize() {
		return n;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#getLocation()
	 */
	@Override
    public Point getLocation() {
		return m;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#addChild(net.sourceforge.sqlexplorer.gef.model.AbstractModelObject)
	 */
	@Override
    public void addChild(AbstractModelObject tb) {
		
		
	}
	/**
	 * @return
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
		
		return text;
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.sqlexplorer.gef.model.INamedObject#getName()
	 */
	public String getName() {
		return text;
	}

}
