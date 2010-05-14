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
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorTemplate;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.properties.ModelPropertySource;

/**
 * @since 4.2
 */
public class RuntimePropertySourceProvider implements IPropertySourceProvider {

    private boolean connectorsEditable = false;
    private boolean connectorTypesEditable = false;

    private ArrayList<IPropertyChangeListener> listenerList = new ArrayList<IPropertyChangeListener>();
    private boolean showExpertProps = false;

    public void addPropertyChangeListener( IPropertyChangeListener listener ) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     * @since 4.2
     */
    public IPropertySource getPropertySource( Object object ) {
        if (object instanceof ConnectorTemplate) {
            ConnectorPropertySource source = new ConnectorPropertySource((Connector)object);
            source.setEditable(this.connectorsEditable);
            source.setProvider(this);
            return source;
        } else if (object instanceof Connector) {
            ConnectorPropertySource source = new ConnectorPropertySource(new ConnectorTemplate((Connector)object));
            source.setEditable(this.connectorsEditable);
            source.setProvider(this);
            return source;
        } else if (object instanceof ConnectorType) {
            ConnectorTypePropertySource source = new ConnectorTypePropertySource((ConnectorType)object);
            source.setEditable(this.connectorTypesEditable);
            return source;
        } else if (object instanceof SourceBinding) {
            SourceBinding binding = (SourceBinding)object;
            // Create the project path
            IPath modelPath = new Path(binding.getContainerPath());
            // append the model name
            modelPath = modelPath.append(binding.getName());

            ModelResource mr = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().findModelResource(modelPath);

            if (mr != null) {
                IFile theResource = null;

                try {
                    theResource = (IFile)mr.getUnderlyingResource();
                } catch (ModelWorkspaceException theException) {
                    DqpUiConstants.UTIL.log(theException);
                }

                if (theResource != null) {
                    return new ModelPropertySource(theResource);
                }
            }

        }
        return null;
    }

    /**
     * Indicates if the expert properties are being shown.
     * 
     * @return <code>true</code> if being shown; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public boolean isShowingExpertProperties() {
        return this.showExpertProps;
    }

    void propertyChanged( PropertyChangeEvent event ) {
        for (Iterator<IPropertyChangeListener> iter = listenerList.iterator(); iter.hasNext();) {
            iter.next().propertyChange(event);
        }
    }

    public void removePropertyChangeListener( IPropertyChangeListener listener ) {
        listenerList.remove(listener);
    }

    /**
     * Sets the editable state for <strong>both</strong> the connector binding and component type property sources.
     * 
     * @param isEditable
     */
    public void setEditable( boolean isEditable ) {
        setEditable(isEditable, isEditable);
    }

    /**
     * @param connectorsEditable the new editable state for the connector binding property source
     * @param connectorTypesEditable the new editable state for the component type property source
     */
    public void setEditable( boolean connectorBindingsEditable,
                             boolean componentTypesEditable ) {
        this.connectorsEditable = connectorBindingsEditable;
        this.connectorTypesEditable = componentTypesEditable;
    }

    /**
     * Sets if the expert properties should be shown or hidden.
     * 
     * @param theShowFlag a flag indicating if the expert properties should be shown
     * @since 5.0.2
     */
    public void setShowExpertProperties( boolean theShowFlag ) {
        this.showExpertProps = theShowFlag;
    }

}
