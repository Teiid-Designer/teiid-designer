/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.modelgenerator.wsdl.SoapBindingInfo;
import org.teiid.designer.query.proc.wsdl.model.IOperation;

/**
 * This class represents an Operation as defined in the WSDL It does not contain any information about the messages that are used
 * by the operation as they are of no interest until it is time to actually create an MM model
 *
 * @since 8.0
 */
public interface Operation extends WSDLElement, IOperation {

    @Override
    Binding getBinding();

    @Override
    Message getInputMessage();

    /**
     * @param inputMsg the name of the input message
     */
    void setInputMessage( Message inputMsg );

    @Override
    Message getOutputMessage();

    /**
     * @param outputMsg the name of the output message
     */
    void setOutputMessage( Message outputMsg );

    /**
     * @param style the style of the operation
     */
    void setStyle( String style );

    @Override
    Fault[] getFaults();

    /**
     * @param faults an array of the names of the possible faults
     */
    void setFaults( Fault[] faults );

    void setSOAPAction( String action );

    void setCanModel( boolean canModel );

    void addProblemMessage( String message );

    SoapBindingInfo getSoapBindingInfo();

    void setSoapBindingInfo( SoapBindingInfo info );
    
    @Override
    Operation copy();
}
