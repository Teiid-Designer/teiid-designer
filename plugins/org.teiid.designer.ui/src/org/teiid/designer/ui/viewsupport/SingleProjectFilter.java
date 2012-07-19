/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.viewsupport;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Simple filter for insuring that only the project defined in the properties is displayed
 *
 * @since 8.0
 */
public class SingleProjectFilter extends ViewerFilter {
	
	Properties properties;

    public SingleProjectFilter(Properties properties) {
		super();
		this.properties = properties;
	}

	/**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParentElement,
                          Object theElement) {
        boolean result = true;
        
        if( this.properties != null ) {
        	IProject proj = DesignerPropertiesUtil.getProject(this.properties);
        	if( proj != null ) {
		        if( theElement instanceof IProject ) {
			        result = proj.getName().equalsIgnoreCase( ((IProject)theElement).getName());
		        }
        	}
        }

        return result;
    }
}