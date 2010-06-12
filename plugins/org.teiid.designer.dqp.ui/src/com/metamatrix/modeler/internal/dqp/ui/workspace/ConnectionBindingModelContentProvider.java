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

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.connection.ConnectionInfoHelper;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * This class provides the ability of connector binding information to be displayed. In particular, in conjunction with the
 * <code>IExtendedModelObject</code> interface and corresponding extension point, the connector binding can be contributed as a
 * child node of a JdbcSource object. @see also <code>ConnectorBindingModelLabelProvider</code>
 * 
 * @since 5.0
 */
public class ConnectionBindingModelContentProvider implements ITreeContentProvider {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];
    
    private ConnectionInfoHelper connectionInfoHelper;

    /**
     * @since 5.0
     */
    public ConnectionBindingModelContentProvider() {
        super();
        this.connectionInfoHelper = new ConnectionInfoHelper();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public synchronized Object[] getChildren( Object parentElement ) {

        Object[] children = NO_CHILDREN;

        if (parentElement instanceof EObject && parentElement instanceof JdbcSource) {
            // check if ConnectorBinding exists for model
            ModelResource mr = ModelUtilities.getModelResource(parentElement);

            if (mr != null) {
            	IConnectionProfile profile = this.connectionInfoHelper.getConnectionProfile(mr);
            	// TODO: Replace with code that looks at model, to find ConnectionInfo annotation and shows properties.
            	if( profile != null ) {
	                Collection<TeiidTranslator> connectors = new ArrayList<TeiidTranslator>(); //.getInstance().getServerManager().getConnectorsForModel(mr.getItemName());
	
	                if (!connectors.isEmpty()) {
	                    Collection<ConnectionBindingSourceWrapper> wrappedCBs = new ArrayList<ConnectionBindingSourceWrapper>();
	
	                    for (TeiidTranslator connector : connectors) {
	                        wrappedCBs.add(new ConnectionBindingSourceWrapper(connector, (JdbcSource)parentElement));
	                    }
	
	                    children = wrappedCBs.toArray();
	                }
            	}
            }
        }

        return children;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element ) {
        Object result = null;
        if (element instanceof ConnectionBindingSourceWrapper) {
            // Find the JDBC Source object in ModelResource
            result = ((ConnectionBindingSourceWrapper)element).getParent();

        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element ) {
        Object[] children = getChildren(element);
        return (children != null) && children.length > 0;
    }

}
