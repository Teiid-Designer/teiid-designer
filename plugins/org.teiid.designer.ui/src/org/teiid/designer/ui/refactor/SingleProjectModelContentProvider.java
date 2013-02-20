/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor;

import org.eclipse.core.resources.IProject;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;

/**
 *
 */
public class SingleProjectModelContentProvider  extends ModelExplorerContentProvider {
	IProject project;
	
	
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    /**
	 * @param project
	 */
	public SingleProjectModelContentProvider(IProject project) {
		super();
		this.project = project;
	}


	@Override
    public Object[] getElements( Object inputElement ) {
    	for( Object element : getChildren(inputElement)) {
    		if( element instanceof IProject && ((IProject)element == this.project) ) {
    			Object[] objs = new Object[1];
    			objs[0] = element;
    			return objs;
    		}
    	}
        return new Object[0];
    }
}