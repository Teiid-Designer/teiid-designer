/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;


import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.Server;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.dqp.ui.config.ComponentTypePropertySource;
import com.metamatrix.modeler.internal.ui.properties.ModelPropertySource;


/**
 * @since 4.2
 */
public class ConnectorBindingPropertySourceProvider implements IPropertySourceProvider {

    private boolean connectorBindingsEditable = false;
    private boolean componentTypesEditable = false;

    private ArrayList<IPropertyChangeListener> listenerList = new ArrayList<IPropertyChangeListener>();
    private boolean showExpertProps = false;

    /**
     * Sets the editable state for <strong>both</strong> the connector binding and component type property sources.
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        setEditable(isEditable, isEditable);
    }

    /**
     * @param connectorBindingsEditable the new editable state for the connector binding property source
     * @param componentTypesEditable the new editable state for the component type property source
     */
    public void setEditable(boolean connectorBindingsEditable,
                            boolean componentTypesEditable) {
        this.connectorBindingsEditable = connectorBindingsEditable;
        this.componentTypesEditable = componentTypesEditable;
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if ( ! listenerList.contains(listener) ) {
            listenerList.add(listener);
        }
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        listenerList.remove(listener);
    }

    void propertyChanged(Connector binding) {
        for ( Iterator<IPropertyChangeListener> iter = listenerList.iterator() ; iter.hasNext() ; ) {
            iter.next().propertyChange(null);
        }
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     * @since 4.2
     */
    public IPropertySource getPropertySource(Object object) {
        if ( object instanceof Connector ) {
            ConnectorBindingPropertySource source = new ConnectorBindingPropertySource((Connector) object);
            source.setEditable(this.connectorBindingsEditable);
            source.setProvider(this);
            return source;
        } else  if ( object instanceof ConnectorType ) {
            ComponentTypePropertySource source = new ComponentTypePropertySource((ConnectorType)object);
            source.setEditable(this.componentTypesEditable);
            return source;
        } else if ( object instanceof Server ) {
            // TODO implement ServerPropertySource
            return null;
        } else if( object instanceof SourceModelInfo ) {
            SourceModelInfo smi = (SourceModelInfo)object;
            // Create the project path
            IPath modelPath = new Path(smi.getContainerPath());
            // append the model name
            modelPath = modelPath.append(smi.getName());

            ModelResource mr = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().findModelResource(modelPath);
            
            if( mr != null ) {
                IFile theResource = null;
                
                try {
                    theResource = (IFile)mr.getUnderlyingResource();
                } catch (ModelWorkspaceException theException) {
                    DqpUiConstants.UTIL.log(theException);
                }
                
                if( theResource != null ) {
                    return new ModelPropertySource(theResource);
                }
            }
            
        }
        return null;
    }

    /**
     * Sets if the expert properties should be shown or hidden.
     * @param theShowFlag a flag indicating if the expert properties should be shown
     * @since 5.0.2
     */
    public void setShowExpertProperties(boolean theShowFlag) {
        this.showExpertProps = theShowFlag;
    }

    /**
     * Indicates if the expert properties are being shown.
     * @return <code>true</code> if being shown; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public boolean isShowingExpertProperties() {
        return this.showExpertProps;
    }

}
