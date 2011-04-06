/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui.actions.dialogs;


import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.extension.manager.ModelObjectExtendedProperty;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.table.ResourceEditingSupport;

class ExtensionPropertyEditingSupport extends ResourceEditingSupport {
	
	boolean changed = false;

    /**
     * @param viewer
     * @param vdb
     */
    public ExtensionPropertyEditingSupport( ColumnViewer viewer,
                                   IResource model) {
        super(viewer, model);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.vdb.ui.ResourceEditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit( Object element ) {
    	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;
        return super.canEdit(element) && property.getDefinition().isModifiable();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.vdb.ui.ResourceEditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {
    	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;

        // no editor if not editable
        if (!property.getDefinition().isModifiable()) {
            return null;
        }

        if (property.getDefinition().isMasked()) {
            this.currentEditor = new TextCellEditor((Composite)getViewer().getControl());
            ((Text)currentEditor.getControl()).setEchoChar('*');
            return this.currentEditor;
        }

        return super.getCellEditor(element);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.vdb.ui.ResourceEditingSupport#getElementValue(java.lang.Object)
     */
    @Override
    protected String getElementValue( Object element ) {
    	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;

        if (StringUtilities.isEmpty(property.getValue())) {
            return property.getDefinition().getDefaultValue();
        }

        return property.getValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.vdb.ui.ResourceEditingSupport#refreshItems(java.lang.Object)
     */
    @Override
    protected String[] refreshItems( Object element ) {
    	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;
        return property.getDefinition().getAllowedValues();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.vdb.ui.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
     */
    @Override
    protected void setElementValue( Object element,
                                    String newValue ) {
    	ModelObjectExtendedProperty property = (ModelObjectExtendedProperty)element;
        String currentValue = property.getValue();
        boolean doIt = false;

        if (StringUtilities.isEmpty(newValue)) {
            if (!StringUtilities.isEmpty(currentValue)) {
                doIt = true;
            }
        } else {
            String defaultValue = property.getDefinition().getDefaultValue();

            // new value is not empty
            // current value is empty
            // set value if new value is not the default value
            if (StringUtilities.isEmpty(currentValue)) {
                if (StringUtilities.isEmpty(defaultValue) || !defaultValue.equals(newValue)) {
                    doIt = true;
                }
            } else {
                // new value is not empty
                // current value is not empty
                // set if new value != current value
                // set if new value != default value
                // if new value == default value set to null
                if (!newValue.equals(currentValue)) {
                    doIt = true;

                    if (!StringUtilities.isEmpty(defaultValue) && defaultValue.equals(newValue)) {
                        newValue = null;
                    }
                }
            }
        }

        if (doIt) {
            property.setValue(newValue);
            changed = true;
            // cause a selection event to be fired so that actions can set their enablement
            getViewer().setSelection(new StructuredSelection(element));
        }
    }
    
    public boolean isChanged() {
    	return this.changed;
    }
}