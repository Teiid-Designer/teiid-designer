/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;


/** 
 * This class provides the ability of connector binding information to be displayed. In particular, in conjunction with the
 * <code>IExtendedModelObject</code> interface and corresponding extension point, the connector binding can be contributed as a 
 * child node of a JdbcSource object.  @see also <code>ConnectorBindingModelLabelProvider</code>
 * @since 5.0
 */
public class ConnectorBindingModelContentProvider implements ITreeContentProvider {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];
    
    /** 
     * 
     * @since 5.0
     */
    public ConnectorBindingModelContentProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public synchronized Object[] getChildren(Object parentElement) {

        Object[] children = NO_CHILDREN;
        
        if ( parentElement instanceof EObject && parentElement instanceof JdbcSource ) {
            // check if ConnectorBinding exists for model??
            ModelResource mr = ModelUtilities.getModelResource(parentElement);
            if( mr != null ) {
                if( DqpPlugin.getWorkspaceConfig().modelIsMappedToSource(mr) ) {
                    Object[] cb = DqpPlugin.getWorkspaceConfig().getBindingsForModel(mr.getItemName()).toArray();
                    Collection<ConnectorBindingSourceWrapper> wrappedCBs = new ArrayList<ConnectorBindingSourceWrapper>();
                    for( int i=0; i<cb.length; i++ ) {
                        wrappedCBs.add(new ConnectorBindingSourceWrapper((ConnectorBinding)cb[i], (JdbcSource)parentElement));
                    }
                    children = wrappedCBs.toArray();
                }
            }
        }
        
        return children;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof ConnectorBindingSourceWrapper ) {
            // Find the JDBC Source object in ModelResource
            result = ((ConnectorBindingSourceWrapper)element).getParent();

        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        Object[] children= getChildren(element);
        return (children != null) && children.length > 0;
    }

}
