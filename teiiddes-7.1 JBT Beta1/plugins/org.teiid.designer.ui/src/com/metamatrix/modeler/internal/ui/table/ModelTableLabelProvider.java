/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;

/**
 * ModelTableLabelProvider
 */
public class ModelTableLabelProvider extends ModelExplorerLabelProvider implements ITableLabelProvider {

    ModelTableLabelProvider() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        Object obj = ((ModelRowElement) element).getValue(columnIndex);
        if (obj != null ) {
            // defect 17935 - Replace unwanted characters with a space.
            return StringUtilities.replaceWhitespace(obj.toString(), true);
        }
        return PluginConstants.EMPTY_STRING;
    }
 
}
