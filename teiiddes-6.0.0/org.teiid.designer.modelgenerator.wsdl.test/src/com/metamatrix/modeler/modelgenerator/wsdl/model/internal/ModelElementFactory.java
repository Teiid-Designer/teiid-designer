/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;

public class ModelElementFactory {

    public static Service getTestService( String name,
                                          String id ) {
        Service svc = new ServiceImpl();
        svc.setId(id);
        svc.setName(name);
        svc.setPorts(new Port[] {getTestPort("testPort", "testPort", svc)}); //$NON-NLS-1$ //$NON-NLS-2$
        return svc;
    }

    public static Port getTestPort( String name,
                                    String id ) {
        Service svc = getTestService("testService", "testServiceId"); //$NON-NLS-1$ //$NON-NLS-2$
        return getTestPort(name, id, svc);
    }

    public static Port getTestPort( String name,
                                    String id,
                                    Service parent ) {
        Port port = new PortImpl(parent);
        port.setName(name);
        port.setId(id);
        port.setBinding(getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response", port)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return port;
    }

    public static Binding getTestBinding( String name,
                                          String id,
                                          String transport,
                                          String style ) {
        Port port = getTestPort("testPort", "testPortId"); //$NON-NLS-1$ //$NON-NLS-2$
        Binding binding = getTestBinding(name, id, transport, style, port);
        return binding;
    }

    public static Binding getTestBinding( String name,
                                          String id,
                                          String transport,
                                          String style,
                                          Port parent ) {
        Binding binding = new BindingImpl(parent);
        binding.setId(id);
        binding.setName(name);
        binding.setTransportURI(transport);
        binding.setStyle(style);
        binding.setOperations(new Operation[] {getTestOperation("testOperation", "testOperationId", "Request_Response", binding)}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return binding;
    }

    public static Operation getTestOperation( String name,
                                              String id,
                                              String style ) {
        Binding bind = getTestBinding("testBinding", "testBindingId", "http://test/test.wsdl", "Request_Response"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return getTestOperation(name, id, style, bind);
    }

    public static Operation getTestOperation( String name,
                                              String id,
                                              String style,
                                              Binding parent ) {
        Operation oper = new OperationImpl(parent);
        oper.setName(name);
        oper.setId(id);
        oper.setStyle(style);
        return oper;
    }

    public static Fault getTestFault( String name,
                                      String id ) {
        return getTestFault(name, id, getTestOperation("testOperation", "testOperationId", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static Fault getTestFault( String name,
                                      String id,
                                      Operation parent ) {
        Fault fault = new FaultImpl(parent);
        fault.setId(id);
        fault.setName(name);
        fault.setMessage(getTestMessage("testMessage", "testMessageId", fault)); //$NON-NLS-1$ //$NON-NLS-2$
        return fault;

    }

    public static Message getTestMessage( String name,
                                          String id,
                                          int type ) {
        return getTestMessage(name, id, type, getTestOperation("testOperation", "testOperation", "Request_Response")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static Message getTestMessage( String name,
                                          String id,
                                          int type,
                                          Operation parent ) {
        Message message = new MessageImpl(parent);
        setMessageProps(name, id, type, message);
        return message;
    }

    public static Message getTestMessage( String name,
                                          String id,
                                          Fault parent ) {
        Message message = new MessageImpl(parent);
        setMessageProps(name, id, Message.FAULT_TYPE, message);
        return message;
    }

    private static void setMessageProps( String name,
                                         String id,
                                         int type,
                                         Message message ) {
        message.setId(id);
        message.setName(name);
        message.setType(type);
        message.setParts(new Part[] {getTestPart("testPart", "testPart", message)}); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static Part getTestPart( String name,
                                    String id ) {
        return getTestPart(name, id, getTestMessage("testMessage", "testMessageId", Message.REQUEST_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static Part getTestPart( String name,
                                    String id,
                                    Message parent ) {
        Part part = new PartImpl(parent);
        part.setName(name);
        part.setId(id);
        part.setElementName(name);
        part.setElementNamespace("http://www.metamatrix.com/test"); //$NON-NLS-1$
        part.setTypeName("string"); //$NON-NLS-1$
        part.setTypeNamespace("http://www.metamatrix.com/test"); //$NON-NLS-1$
        return part;
    }

}
