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

import java.io.Serializable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class TablePropertyManager implements Serializable {

    private static final long serialVersionUID = 1L;

    Table tb;

    TablePropertyManager( Table tb ) {
        this.tb = tb;
    }

    protected static IPropertyDescriptor[] descriptors = null;
    public static String ID_SIZE = "size_property"; //$NON-NLS-1$
    public static String ID_LOCATION = "location_property"; //$NON-NLS-1$
    public static String ID_LABEL = "label_property"; //$NON-NLS-1$
    public static String ID_SHOW_DETAILS = "columns_property";//$NON-NLS-1$
    public static String ID_SHOW_QUALIFIED = "title_property";//$NON-NLS-1$
    public static String ID_SCHEMA = "schema_property";//$NON-NLS-1$
    public static String ID_CATALOG = "catalog_property";//$NON-NLS-1$
    static {
        ComboBoxPropertyDescriptor iProp = new ComboBoxPropertyDescriptor(ID_SHOW_QUALIFIED, "Show Qualified Name", new String[] {
            "false", "true"});
        ComboBoxPropertyDescriptor iProp2 = new ComboBoxPropertyDescriptor(ID_SHOW_DETAILS, "Show Column Types", new String[] {
            "false", "true"});
        descriptors = new IPropertyDescriptor[] {new PropertyDescriptor(ID_LABEL, "Table Name"),
            new PropertyDescriptor(ID_SCHEMA, "Schema"), new PropertyDescriptor(ID_CATALOG, "Catalog"),
            new PropertyDescriptor(ID_SIZE, "Size"), new PropertyDescriptor(ID_LOCATION, "Location"), iProp2,
            // new PropertyDescriptor(ID_SHOW_QUALIFIED,"Show Qualified Name")
            iProp};
        iProp.setLabelProvider(new LabelProvider() {
            @Override
            public String getText( Object obj ) {
                if (obj instanceof Integer) {
                    if (((Integer)obj).intValue() == 1) return "true";
                    return "false";
                }
                return "";
            }
        });
        iProp2.setLabelProvider(new LabelProvider() {
            @Override
            public String getText( Object obj ) {
                if (obj instanceof Integer) {
                    if (((Integer)obj).intValue() == 1) return "true";
                    return "false";
                }
                return "";
            }
        });
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptors;
    }

    public Object getPropertyValue( Object propName ) {
        if (ID_SIZE.equals(propName)) return new DimensionPropertySource(tb.getSize());
        else if (ID_LOCATION.equals(propName)) return new LocationPropertySource(tb.getLocation());
        else if (ID_LABEL.equals(propName))
        // return new LabelPropertySource(label);
        return tb.getQualifiedName();
        else if (ID_SCHEMA.equals(propName)) return tb.getSchemaName();
        else if (ID_CATALOG.equals(propName)) return tb.getCatalogName();
        else if (ID_SHOW_DETAILS.equals(propName)) {
            if (tb.isShowColumnDetail()) return new Integer(1);
            return new Integer(0);
        } else if (ID_SHOW_QUALIFIED.equals(propName)) {
            // return new BooleanPropertySource2(Boolean.valueOf(tb.isShowQualifiedName()));
            if (tb.isShowQualifiedName()) return new Integer(1);
            return new Integer(0);
        }
        return null;
    }

    public void setPropertyValue( Object id,
                                  Object value ) {
        if (ID_SIZE.equals(id)) {
            DimensionPropertySource dimPS = (DimensionPropertySource)value;
            tb.setSize(new Dimension(dimPS.getValue()));
        } else if (ID_LOCATION.equals(id)) {
            LocationPropertySource locPS = (LocationPropertySource)value;
            tb.setLocation(new Point(locPS.getValue()));
        } else if (ID_SHOW_DETAILS.equals(id)) {
            if (value instanceof Integer) {
                if (((Integer)value).intValue() == 1) tb.setShowColumnDetail(true);
                else tb.setShowColumnDetail(false);
            }
        } else if (ID_SHOW_QUALIFIED.equals(id)) {
            if (value instanceof Integer) {
                if (((Integer)value).intValue() == 1) tb.setShowQualifiedName(true);
                else tb.setShowQualifiedName(false);
            }
        }
    }

}
