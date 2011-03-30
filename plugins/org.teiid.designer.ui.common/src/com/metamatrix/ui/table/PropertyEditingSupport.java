/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.properties.PropertyDefinition;

/**
 * An editing support implementation for a {@link PropertyDefinition}.
 */
public abstract class PropertyEditingSupport extends ResourceEditingSupport {

    /**
     * @param viewer the table viewer (may not be <code>null</code>)
     * @param resource the resource (may not be <code>null</code>)
     */
    public PropertyEditingSupport( ColumnViewer viewer,
                                   IResource resource ) {
        super(viewer, resource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit( Object element ) {
        PropertyDefinition propDefn = getPropertyDefinition(element);
        return (super.canEdit(element) && propDefn.isModifiable());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {
        PropertyDefinition propDefn = getPropertyDefinition(element);

        // no editor if not editable
        if (!propDefn.isModifiable()) {
            return null;
        }

        // mask value if needed
        if (propDefn.isMasked()) {
            this.currentEditor = new TextCellEditor((Composite)getViewer().getControl());
            ((Text)this.currentEditor.getControl()).setEchoChar('*');
            return this.currentEditor;
        }

        return super.getCellEditor(element);
    }

    /**
     * @param element the element whose <code>PropertyDefinition</code> is being requested
     * @return the appropriate property definition
     */
    protected PropertyDefinition getPropertyDefinition( Object element ) {
        return (PropertyDefinition)element;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#refreshItems(java.lang.Object)
     */
    @Override
    protected String[] refreshItems( Object element ) {
        PropertyDefinition propDefn = getPropertyDefinition(element);
        return propDefn.getAllowedValues();
    }

}
