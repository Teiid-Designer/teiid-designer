/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.execution;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStream;


/** 
 * @since 4.3
 */
public class FakeVdbDefn implements
                        VDBDefn {
    
    private Map modelBindingMap = new HashMap(); 

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getVDBStream()
     * @since 4.3
     */
    public VDBStream getVDBStream() {
        return new VDBStream() {
            public byte[] toByteArray() {
                return null;
            }
            public InputStream getInputStream() {
                // TODO: to be Implemented
                return null;
            }
            public File getFile() {
                // TODO: to be Implemented
                return null;
            }            
        };
    }
    
    
    
    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorBindingByName(java.lang.String)
     * @since 4.3
     */
    public ConnectorBinding getConnectorBindingByName(String connectorBindingName) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorBindingByRouting(java.lang.String)
     * @since 4.3
     */
    public ConnectorBinding getConnectorBindingByRouting(String routingUUID) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getVersion()
     * @since 4.3
     */
    public String getVersion() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorTypes()
     * @since 4.3
     */
    public Map getConnectorTypes() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorType(java.lang.String)
     * @since 4.3
     */
    public ComponentType getConnectorType(String componentTypeName) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorBindings()
     * @since 4.3
     */
    public Map getConnectorBindings() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getConnectorBinding(java.lang.String)
     * @since 4.3
     */
//    public ConnectorBinding getConnectorBinding(String routingUUID) {
//        return null;
//    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getModelToBindingMappings()
     * @since 4.3
     */
    public Map getModelToBindingMappings() {
        return this.modelBindingMap;
    }
    
    public void setModelBinding(String model, String connectorBinding) {
        this.modelBindingMap.put(model, Collections.singletonList(connectorBinding));
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getModelNames()
     * @since 4.3
     */
    public Collection getModelNames() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getMatertializationModel()
     * @since 4.3
     */
    public ModelInfo getMatertializationModel() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getStatus()
     * @since 4.3
     */
    public short getStatus() {
        return 0;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#isActiveStatus()
     * @since 4.3
     */
    public boolean isActiveStatus() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getName()
     * @since 4.3
     */
    public String getName() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getUUID()
     * @since 4.3
     */
    public String getUUID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getDescription()
     * @since 4.3
     */
    public String getDescription() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getDateCreated()
     * @since 4.3
     */
    public Date getDateCreated() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getCreatedBy()
     * @since 4.3
     */
    public String getCreatedBy() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getModels()
     * @since 4.3
     */
    public Collection getModels() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getFileName()
     * @since 4.3
     */
    public String getFileName() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#hasWSDLDefined()
     * @since 4.3
     */
    public boolean hasWSDLDefined() {
        return false;
    }

    
    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#doesVDBHaveValidityError()
     * @since 4.2
     */
    public boolean doesVDBHaveValidityError() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBInfo#getModel(java.lang.String)
     * @since 4.3
     */
    public ModelInfo getModel(String name) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#isVisible(java.lang.String)
     * @since 4.3
     */
    public boolean isVisible(String resourcePath) {
        return false;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getVDBValidityErrors()
     * @since 4.3
     */
    public String[] getVDBValidityErrors() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.vdb.api.VDBDefn#getDataRoles()
     */
    public char[] getDataRoles() {
        return null;
    }



	public Properties getHeaderProperties() {
		// rameshTODO Auto-generated method stub
		return null;
	}

}
