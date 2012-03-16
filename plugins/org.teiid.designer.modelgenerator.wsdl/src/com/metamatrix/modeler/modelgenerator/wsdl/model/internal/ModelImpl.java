/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;

public class ModelImpl implements Model {

    private Service[] m_services;
    private Map m_namespaces;
    private Map m_reverseNamespaceLookup;
    private Map<String, Service> serviceNameToService;
    private Map<String, Port> portNameToPort;
    private Map<String, Operation> operationNameToOperation;
    private XSDSchema[] m_schemas;
    
    private Map<String, Map<String, Operation>> modelableOperationsMaps;

    public ModelImpl() {
        serviceNameToService = new HashMap<String, Service>();
        portNameToPort = new HashMap<String, Port>();
        operationNameToOperation = new HashMap<String, Operation>();
        modelableOperationsMaps = new HashMap<String, Map<String, Operation>>(); 
    }

    public Service[] getServices() {
        // defensive copy of Service array
        Service[] retSvc = new Service[m_services.length];
        for (int i = 0; i < m_services.length; i++) {
            retSvc[i] = (Service)m_services[i].copy();
        }
        return retSvc;
    }

    public void setServices( Service[] services ) {
        m_services = services;
        for (int i = 0; i < services.length; i++) {
            Service service = services[i];
            serviceNameToService.put(service.getName(), service);
            Port[] ports = service.getPorts();
            for (int j = 0; j < ports.length; j++) {
                Port port = ports[j];
                portNameToPort.put(port.getName(), port);
                Operation[] operations = port.getBinding().getOperations();
                for (int k = 0; k < operations.length; k++) {
                    Operation operation = operations[k];
                    operationNameToOperation.put(operation.getName(), operation);
                    addOperationToMap(operation);
                }
            }
        }
    }
    
    private void addOperationToMap(Operation operation) {
    	if( operation.canModel() ) {
    		Map<String, Operation> existingMap = modelableOperationsMaps.get(operation.getBinding().getPort().getName());
    		if( existingMap == null ) {
    			existingMap = new HashMap<String, Operation>();
    			modelableOperationsMaps.put(operation.getBinding().getPort().getName(), existingMap);
    		}
    		existingMap.put(operation.getName(), operation);
    	}
    }
    
    public Operation[] getModelableOperations(String portName) {
    	Map<String, Operation> existingMap = modelableOperationsMaps.get(portName);
    	
    	int nValues = existingMap.values().size();
    	List<Operation> ops = new ArrayList<Operation>(existingMap.values());
    	Collections.sort(ops, new OperationComparator());
    	return ops.toArray(new Operation[nValues]);
    }
    
    public String[] getModelablePortNames() {
    	int nPorts = modelableOperationsMaps.keySet().size();
    	
    	return modelableOperationsMaps.keySet().toArray(new String[nPorts]);
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < m_services.length; i++) {
            buff.append("\n"); //$NON-NLS-1$
            buff.append(m_services[i].toString());
        }
        buff.append("\n"); //$NON-NLS-1$
        return buff.toString();
    }

    public XSDSchema[] getSchemas() {
        return m_schemas;
    }

    public void setSchemas( XSDSchema[] schemas ) {
        m_schemas = schemas;
    }

    public Map getNamespaces() {
        return m_namespaces;
    }

    public void setNamespaces( Map collection ) {
        m_namespaces = collection;
        m_reverseNamespaceLookup = new HashMap(collection.size());
        for (Iterator nsIter = collection.keySet().iterator(); nsIter.hasNext();) {
            Object key = nsIter.next();
            Object payload = collection.get(key);
            m_reverseNamespaceLookup.put(payload, key);
        }
    }

    public void addNamespaceToMap( Namespace ns ) {
        String uri = ns.getURI();
        String prefix = ns.getPrefix();
        addNamespaceToMap(prefix, uri);
    }

    public void addNamespaceToMap( String prefix,
                                   String namespaceURI ) {
        if (m_reverseNamespaceLookup.get(namespaceURI) != null) return;
        if (m_namespaces.get(prefix) != null) {
            int pre = 0;
            final String nsPre = "mmn"; //$NON-NLS-1$
            while (m_namespaces.get(nsPre + pre) != null)
                ++pre;
            prefix = nsPre + pre;
        }
        m_namespaces.put(prefix, namespaceURI);
        m_reverseNamespaceLookup.put(namespaceURI, prefix);
    }

    public void addNamespaceToMap( String namespaceURI ) {
        addNamespaceToMap("mmn0", namespaceURI); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.modelgenerator.wsdl.model.Model#getService(java.lang.String)
     */
    @Override
    public Service getService( String name ) {
        return serviceNameToService.get(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.modelgenerator.wsdl.model.Model#getPort(java.lang.String)
     */
    @Override
    public Port getPort( String name ) {
        return portNameToPort.get(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.modelgenerator.wsdl.model.Model#getOperation(java.lang.String)
     */
    @Override
    public Operation getOperation( String name ) {
        return operationNameToOperation.get(name);
    }
    
    /**
     * Compare IResources in a collection and order them as IRoot, IProject, IFolder and IFile.
     */
    private class OperationComparator implements Comparator {

        public OperationComparator() {
            super();
        }

        public int compare( final Object oper1,
                            final Object oper2 ) {
            CoreArgCheck.isInstanceOf(Operation.class, oper1);
            CoreArgCheck.isInstanceOf(Operation.class, oper2);

            final Operation op1 = (Operation)oper1;
            final Operation op2 = (Operation)oper2;
            return op1.getName().compareTo(op2.getName());
        }

        @Override
        public boolean equals( final Object anObject ) {
            if (this == anObject) return true;
            if (anObject == this) return true;
            if (anObject == null || anObject.getClass() != this.getClass()) return false;
            return true;
        }
    }

}
