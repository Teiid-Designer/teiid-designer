/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

import com.metamatrix.modeler.modelgenerator.wsdl.SoapBindingInfo;

/**
 * This class represents an Operation as defined in the WSDL It does not contain any information about the messages that are used
 * by the operation as they are of no interest until it is time to actually create an MM model
 */
public interface Operation extends WSDLElement {

    /**
     * @return the binding that contains this operation
     */
    public Binding getBinding();

    /**
     * @return the name of the input message
     */
    public Message getInputMessage();

    /**
     * @param inputMsg the name of the input message
     */
    public void setInputMessage( Message inputMsg );

    /**
     * @return the name of the output message
     */
    public Message getOutputMessage();

    /**
     * @param outputMsg the name of the output message
     */
    public void setOutputMessage( Message outputMsg );

    /**
     * @return the style of the operation
     */
    public String getStyle();

    /**
     * @param style the style of the operation
     */
    public void setStyle( String style );

    /**
     * @return an array of the names of possible faults
     */
    public Fault[] getFaults();

    /**
     * @param faults an array of the names of the possible faults
     */
    public void setFaults( Fault[] faults );

    public void setSOAPAction( String action );

    public String getSOAPAction();

    public boolean canModel();

    public void setCanModel( boolean canModel );

    public void addProblemMessage( String message );

    public String[] getProblemMessages();

    public SoapBindingInfo getSoapBindingInfo();

    public void setSoapBindingInfo( SoapBindingInfo info );
}
