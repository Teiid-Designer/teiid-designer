/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.ConnectionFactory;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 *
 */
public class TestExecutionAdmin extends ExecutionAdmin {

    private Map<String, String> connectorNameConnectorTypeNameMap;
    private Map<String, String> modelNameConectorNameMap;

    public TestExecutionAdmin( Server server,
                               EventManager eventManager,
                               Map<String, String> connectorNameConnectorTypeNameMap,
                               Map<String, String> modelNameConectorNameMap ) throws Exception {
        super(mock(Admin.class), server, eventManager);
        this.connectorNameConnectorTypeNameMap = connectorNameConnectorTypeNameMap;
        this.modelNameConectorNameMap = modelNameConectorNameMap;

        // super called refresh but the maps were empty so call again
        refresh();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ExecutionAdmin#refreshConnectors(java.util.Collection)
     */
    @Override
    protected void refreshConnectors( Collection<ConnectionFactory> connectorBindings ) {
        if (this.connectorNameConnectorTypeNameMap != null) {
            for (Entry<String, String> entry : this.connectorNameConnectorTypeNameMap.entrySet()) {
                ConnectorType type = getConnectorType(entry.getValue());
                ConnectionFactory binding = mock(ConnectionFactory.class);
                stub(binding.getName()).toReturn(entry.getKey());
                this.connectorByNameMap.put(binding.getName(), new Connector(binding, type));
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ExecutionAdmin#refreshConnectorTypes(java.util.Set)
     */
    @Override
    protected void refreshConnectorTypes( Set<String> connectorTypeNames ) throws Exception {
        if (this.connectorNameConnectorTypeNameMap != null) {
            for (String connectorName : this.connectorNameConnectorTypeNameMap.values()) {
                if (!this.connectorTypeByNameMap.containsKey(connectorName)) {
                    ConnectorType connectorType = new ConnectorType(connectorName, new ArrayList<PropertyDefinition>(), this);
                    this.connectorTypeByNameMap.put(connectorType.getName(), connectorType);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ExecutionAdmin#refreshSourceBindings()
     */
    @Override
    protected void refreshSourceBindings() throws Exception {
        if (this.modelNameConectorNameMap != null) {
            SourceBindingsManager bindingMgr = getSourceBindingsManager();

            for (String modelName : this.modelNameConectorNameMap.keySet()) {
                ModelResource modelResource = MockObjectFactory.createModelResource(modelName, "testdata");
                Connector connector = getConnector(this.modelNameConectorNameMap.get(modelName));
                bindingMgr.createSourceBinding(modelResource, connector);
            }
        }
    }

}
