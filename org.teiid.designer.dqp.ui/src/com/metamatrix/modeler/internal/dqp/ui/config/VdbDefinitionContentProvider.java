/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.common.vdb.api.VDBDefn;


/** 
 * @since 4.2
 */
public class VdbDefinitionContentProvider implements
                                         IStructuredContentProvider {

    /** 
     * 
     * @since 4.2
     */
    public VdbDefinitionContentProvider() {
        super();
    }

    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof VDBDefn ) {
            Collection models = ((VDBDefn) inputElement).getModels(); 
            return models.toArray();
        }
        return new Object[0];
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

}
