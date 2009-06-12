/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.util.Collection;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;


/** 
 * @since 4.3
 */
public class FakeVdbDefnHelper extends VdbDefnHelper {
    
    public FakeVdbDefnHelper(VDBDefn vdbDefn) throws Exception {
        super(vdbDefn);
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#createConnectorBinding(com.metamatrix.common.config.api.ComponentType, java.lang.String, boolean)
     * @since 5.0
     */
    @Override
    public ConnectorBinding createConnectorBinding(ComponentType theCtConnector,
                                                   String theConnBindName,
                                                   boolean theAddToConfigurationFlag) throws Exception {
        return super.createConnectorBinding(theCtConnector, theConnBindName, theAddToConfigurationFlag);
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#exportDefn(java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public void exportDefn(String fileName,
                           String location) throws Exception {
        super.exportDefn(fileName, location);
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#getConnectorBindings(com.metamatrix.common.vdb.api.ModelInfo)
     * @since 4.3
     */
    @Override
    public Collection getConnectorBindings(ModelInfo modelDefn) {
        return super.getConnectorBindings(modelDefn);
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#getFirstConnectorBinding(com.metamatrix.common.vdb.api.ModelInfo)
     * @since 4.3
     */
    @Override
    public ConnectorBinding getFirstConnectorBinding(ModelInfo modelDefn) {
        return super.getFirstConnectorBinding(modelDefn);
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#getVdbDefn()
     * @since 4.3
     */
    @Override
    public VDBDefn getVdbDefn() {
        return super.getVdbDefn();
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#setConnectorBinding(com.metamatrix.common.vdb.api.VDBModelDefn, com.metamatrix.common.config.api.ConnectorBinding, com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    @Override
    public void setConnectorBinding(ModelInfo modelDefn,
                                    ConnectorBinding binding,
                                    ComponentType type) {
        super.setConnectorBinding(modelDefn, binding, type);
    }

    /** 
     * @see com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper#updateToVdb()
     * @since 4.3
     */
    @Override
    public boolean updateToVdb() {
        return super.updateToVdb();
    }

}
