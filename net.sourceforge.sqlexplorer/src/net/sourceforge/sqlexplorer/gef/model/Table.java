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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Mazzolini
 */
public class Table extends AbstractModelObject {
    static final long serialVersionUID = 1;
    private String schemaName, catalogName;
    private String qualifiedName;
    private TablePropertyManager tpm;
    private IGefObject schema;
    private String simpleName;
    private Point _point;
    private Dimension _dimension;
    private boolean showColumnDetail = true;
    private boolean showQualifiedName = true;

    // Inputs
    public static String TERMINAL_1_IN = "A" //$NON-NLS-1$

    ; 
    // Outputs
    public static String TERMINAL_1_OUT = "1"; //$NON-NLS-1$

    private List columns = Collections.EMPTY_LIST;
    private Pk pk = null;
    private List fkList = Collections.EMPTY_LIST;// Actually we should have only one primary key

    public List getSourceConnections() {
        return (List)outputs.clone();
    }

    public List getTargetConnections() {
        return (List)inputs.clone();
    }

    public boolean isPrimaryKey( Column column ) {
        if (pk == null) return false;
        if (pk.containsColumn(column.columnName)) return true;
        return false;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return tpm.getPropertyDescriptors();
    }

    @Override
    public Object getPropertyValue( Object propName ) {
        return tpm.getPropertyValue(propName);
    }

    @Override
    public void setPropertyValue( Object id,
                                  Object value ) {
        tpm.setPropertyValue(id, value);
    }

    public Table( String label ) {
        tpm = new TablePropertyManager(this);
        this.simpleName = label;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * @return
     */
    public String getSimpleName() {
        return simpleName;
    }

    public void setShowColumnDetail( boolean val ) {
        boolean oldVal = showColumnDetail;
        showColumnDetail = val;
        firePropertyChange("showColumnDetail", oldVal, val);
    }

    @Override
    public Point getLocation() {
        return _point;
    }

    @Override
    public Dimension getSize() {
        return _dimension;
    }

    @Override
    public void setLocation( Point point ) {
        setLocation(point, true);
    }

    public void setLocation( Point point,
                             boolean fire ) {
        Point point1 = _point;
        _point = point;

        firePropertyChange("location", point1, point);
    }

    @Override
    public void setSize( Dimension dimension ) {
        Dimension dimension1 = _dimension;
        _dimension = dimension;
        firePropertyChange("size", dimension1, dimension);
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#getName()
     */
    public String getName() {

        return getQualifiedName();
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#getChildAt(int)
     */
    public IGefObject getElementAt( int i ) {

        return null;
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#getChildCount()
     */
    public int getElementsCount() {

        return 0;
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#indexOf(net.sf.ProvaGef.model.INamedObject)
     */
    public int indexOf( IGefObject inamedobject ) {

        return -1;
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#removeChild(net.sf.ProvaGef.model.INamedObject)
     */
    @Override
    public boolean removeChild( AbstractModelObject inamedobject ) {

        return false;
    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#addChild(int, net.sf.ProvaGef.model.INamedObject)
     */
    @Override
    public void addChild( int i,
                          AbstractModelObject inamedobject ) {

    }

    /* (non-Javadoc)
     * @see net.sf.ProvaGef.model.INamedObject#getParent()
     */
    public IGefObject getParent() {
        return schema;
    }

    /**
     * @return
     */
    public String[] getColumnNames() {
        ArrayList ls = new ArrayList();
        Iterator it = columns.iterator();
        while (it.hasNext()) {
            ls.add(((Column)it.next()).columnName);
        }
        return (String[])ls.toArray(new String[ls.size()]);
    }

    public void setColumns( List columns ) {
        this.columns = columns;
    }

    /**
	 * 
	 */
    public Column[] getColumns() {
        return (Column[])columns.toArray(new Column[columns.size()]);

    }

    /**
     * @return
     */
    public boolean isShowColumnDetail() {
        return showColumnDetail;
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
    public void setParent( IGefObject schema ) {
        this.schema = schema;

    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.gef.model.AbstractModelObject#addChild(net.sourceforge.sqlexplorer.gef.model.AbstractModelObject)
     */
    @Override
    public void addChild( AbstractModelObject tb ) {

    }

    /**
     * @param list
     */
    public void setFkList( ArrayList list ) {
        this.fkList = list;

    }

    /**
     * @return
     */
    public List getFkList() {
        return fkList;
    }

    /**
     * @param list
     */
    public void setFkList( List list ) {
        fkList = list;
    }

    /**
     * @param string
     */
    public void setQualifiedName( String string ) {
        qualifiedName = string;

    }

    /**
     * @return
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * @param string
     */
    public void setCatalogName( String string ) {
        catalogName = string;
    }

    /**
     * @param string
     */
    public void setSchemaName( String string ) {
        schemaName = string;
    }

    /**
     * @return
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * @return
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return
     */
    public boolean isShowQualifiedName() {
        return showQualifiedName;
    }

    /**
     * @param b
     */
    public void setShowQualifiedName( boolean val ) {
        boolean oldVal = showQualifiedName;
        showQualifiedName = val;
        firePropertyChange("showQualifiedName", oldVal, val);
    }

    /**
     * @return
     */
    public Pk getPk() {

        return pk;
    }

    /**
     * @param pk
     */
    public void setPk( Pk pk ) {
        this.pk = pk;

    }

    /**
     * @param schema
     */
    public void createLinks( Schema schema ) {
        Pk pk = this.getPk();
        IGefObject[] objects = schema.getElements();
        if (pk != null) {
            // System.out.println("Ha PK:"+pk.pkName);
            for (int j = 0; j < objects.length; j++) {

                IGefObject obj2 = objects[j];
                if (!(obj2 instanceof Table)) continue;
                Table source = (Table)obj2;
                // System.out.println("Confronto con "+source);
                Iterator fkIterator = source.getFkList().iterator();
                while (fkIterator.hasNext()) {
                    Fk fk = (Fk)fkIterator.next();
                    // System.out.println(""+source + " ha Fk "+fk.fkName);
                    // if((fk.tabName.equalsIgnoreCase(this.getSimpleName())) && (fk.pkName.equalsIgnoreCase(pk.pkName))){
                    if (fk.isRelated(pk, this)) {
                        Link wire = new Link(fk);
                        wire.setSource(source);
                        wire.setSourceTerminal(Table.TERMINAL_1_OUT);
                        wire.setTarget(this);
                        wire.setTargetTerminal(Table.TERMINAL_1_IN);
                        wire.attachSource();
                        wire.attachTarget();
                    }
                }
            }
        }
        Iterator itFk = this.getFkList().iterator();
        while (itFk.hasNext()) {
            Fk fk = (Fk)itFk.next();
            for (int j = 0; j < objects.length; j++) {
                IGefObject obj2 = objects[j];
                if (obj2 instanceof Table) {
                    Table source = (Table)obj2;
                    Pk pk_ = source.getPk();
                    if (pk_ != null) {
                        if (fk.isRelated(pk_, source)) {
                            // if((fk.tabName.equalsIgnoreCase(source.getSimpleName())) &&
                            // (fk.pkName.equalsIgnoreCase(pk_.pkName))){
                            j = objects.length;// Found the primary key, so stop searching it, we can go to next foreign key.
                            Link wire = new Link(fk);
                            wire.setSource(this);
                            wire.setSourceTerminal(Table.TERMINAL_1_OUT);
                            wire.setTarget(source);
                            wire.setTargetTerminal(Table.TERMINAL_1_IN);
                            wire.attachSource();
                            wire.attachTarget();
                        }
                    }
                }
            }
        }

    }

    public void removeLinks( Schema schema ) {
        for (int i = 0; i < inputs.size(); i++) {
            Link ln = (Link)inputs.get(i);
            ln.detachSource();
            // ln.detachTarget();
        }
        for (int i = 0; i < outputs.size(); i++) {
            Link ln = (Link)outputs.get(i);
            ln.detachTarget();
            // ln.detachSource();
        }
        inputs.clear();
        outputs.clear();
    }

}
