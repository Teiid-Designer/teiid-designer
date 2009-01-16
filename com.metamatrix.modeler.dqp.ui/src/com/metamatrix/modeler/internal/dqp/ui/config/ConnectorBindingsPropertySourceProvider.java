/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.dqp.ui.config;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;


/** 
 * @since 4.2
 */
public class ConnectorBindingsPropertySourceProvider implements IPropertySourceProvider {
    
    private InternalVdbEditingContext vdbContext;
    private VdbContextEditor vdbContextEditor;
    private boolean editable = false;
    private ArrayList<IPropertyChangeListener> listenerList = new ArrayList<IPropertyChangeListener>();
    private boolean showExpertProps = false;
    
    /** 
     * 
     * @since 4.2
     */
    public ConnectorBindingsPropertySourceProvider(InternalVdbEditingContext theContext) {
        this.vdbContext = theContext;
        this.vdbContextEditor = null;
    }
    public ConnectorBindingsPropertySourceProvider(VdbContextEditor theContext) {
        this.vdbContext = null;
        this.vdbContextEditor = theContext;
    }

    public ConnectorBindingsPropertySourceProvider() {
    }
    
    public void setEditable(boolean isEditable) {
        this.editable = isEditable;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        if ( ! listenerList.contains(listener) ) {
            listenerList.add(listener);
        }
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        listenerList.remove(listener);
    }

    void propertyChanged(ConnectorBinding binding) {
        for ( Iterator<IPropertyChangeListener> iter = listenerList.iterator() ; iter.hasNext() ; ) {
            iter.next().propertyChange(null);
        }
    }
    
    /** 
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     * @since 4.2
     */
    public IPropertySource getPropertySource(Object object) {
        if ( object instanceof BasicVDBModelDefn ) {
            ConnectorBinding binding = null;
            if (this.vdbContext != null) {
                binding = DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContext).getFirstConnectorBinding((BasicVDBModelDefn) object);
            } else if (this.vdbContextEditor != null) {
                binding = DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContextEditor).getFirstConnectorBinding((BasicVDBModelDefn) object);
            }
            
            ConnectorBindingsPropertySource source = new ConnectorBindingsPropertySource(binding);
            source.setEditable(editable);
            source.setProvider(this);
            return source;
        } else if ( object instanceof ConnectorBinding ) {
            ConnectorBindingsPropertySource source = new ConnectorBindingsPropertySource((ConnectorBinding) object);
            source.setEditable(editable);
            source.setProvider(this);
            return source;
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
