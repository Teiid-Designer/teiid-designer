/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.dse.provider;

import org.eclipse.datatools.connectivity.sqm.core.ui.explorer.virtual.IVirtualNode;
import org.eclipse.datatools.connectivity.sqm.core.ui.services.IDataToolsUIServiceManager;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.datatools.connectivity.ui.Activator;

public class TeiidLableProvider extends LabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element ) {
        if (element instanceof IVirtualNode) {
            return ((IVirtualNode)element).getDisplayName();
        } else if (element instanceof ENamedElement) {
            return ((ENamedElement)element).getName();
        } else {
            return super.getText(element);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element ) {
    	if(element instanceof TeiidDocumentsFolder || element instanceof DocumentColumnFolder) {
    		return Activator.getDefault().getImageRegistry().get(Activator.CLOSED_FOLDER_ID);
    	}
        return IDataToolsUIServiceManager.INSTANCE.getLabelService(element).getIcon();
    }

}
