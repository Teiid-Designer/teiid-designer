/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.teiid.designer.runtime.Connector;
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
    private Connector binding;
    private JdbcSource jdbcSource;
    private PropertySheetPage propertyPage;
    private IPropertySourceProvider propertySourceProvider;
    
    // ----------------------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------------------
    /**
     * Base Constructor
     * @param connector
     * @param jdbcSource
     * @since 5.0
     */
    public ConnectorBindingSourceWrapper(Connector connector, JdbcSource jdbcSource) {
        
        
        
        this.binding = connector;
        this.jdbcSource = jdbcSource;
    }
    
    /**
     * getter for the delegate connector binding 
     * @return
     * @since 5.0
     */
    public Connector getConnector() {
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
            propertySourceProvider = new RuntimePropertySourceProvider();
            propertyPage = new PropertySheetPage();
            propertyPage.setPropertySourceProvider(propertySourceProvider);
            
        }
        ((RuntimePropertySourceProvider)propertySourceProvider).setEditable(true);
        ((ConnectorPropertySource)propertySourceProvider.getPropertySource(binding)).setEditable(true);
        
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
