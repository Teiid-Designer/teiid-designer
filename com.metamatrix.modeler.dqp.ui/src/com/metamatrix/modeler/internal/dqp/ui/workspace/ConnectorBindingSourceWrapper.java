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

package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;


/** 
 * This class provides the ability to show a source-to-connector mapping in the ConnectorsView
 * 
 * This class implements <code>IExtendedModelObject</code> which enables displaying this object in the ModelExplorer within a
 * model's JdbcSource node (if it exists).
 * @since 5.0
 */
public class ConnectorBindingSourceWrapper implements IExtendedModelObject {
    // ----------------------------------------------------------------------------------
    // Static Constants
    // ----------------------------------------------------------------------------------
    
    private static final String BINDING_PREFIX = "ConnectorBindingSourceWrapper.statusLabel"; //$NON-NLS-1$
    
    // ----------------------------------------------------------------------------------
    // Variables
    // ----------------------------------------------------------------------------------
    private ConnectorBinding binding;
    private JdbcSource jdbcSource;
    private PropertySheetPage propertyPage;
    private IPropertySourceProvider propertySourceProvider;
    
    // ----------------------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------------------
    /**
     * Base Constructor
     * @param binding
     * @param jdbcSource
     * @since 5.0
     */
    public ConnectorBindingSourceWrapper(ConnectorBinding binding, JdbcSource jdbcSource) {
        
        
        
        this.binding = binding;
        this.jdbcSource = jdbcSource;
    }
    
    /**
     * getter for the delegate connector binding 
     * @return
     * @since 5.0
     */
    public ConnectorBinding getConnectorBinding() {
        return binding;
    }
    
    /**
     *  
     * @return
     * @since 5.0
     */
    public Object getParent() {
        return jdbcSource;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject#getPropertySource()
     * @since 5.0
     */
    public IPropertySource getPropertySource() {
        if( propertyPage == null ) {
            propertySourceProvider = new ConnectorBindingPropertySourceProvider();
            propertyPage = new PropertySheetPage();
            propertyPage.setPropertySourceProvider(propertySourceProvider);
            
        }
        ((ConnectorBindingPropertySourceProvider)propertySourceProvider).setEditable(true);
        ((ConnectorBindingPropertySource)propertySourceProvider.getPropertySource(binding)).setEditable(true);
        
        return propertySourceProvider.getPropertySource(binding);
    }

    public String getStatusLabel() {
        return DqpUiConstants.UTIL.getString(BINDING_PREFIX, getSourceModelName(), binding.getName());
    }
    
    private String getSourceModelName() {
        ModelResource res = ModelUtilities.getModelResourceForModelObject(jdbcSource);
        
        return res.getItemName();
    }

    public boolean overrideContextMenu() {
        return false;
    }
    
    public void fillContextMenu(IMenuManager theMenu) {
    }

    
    
}
