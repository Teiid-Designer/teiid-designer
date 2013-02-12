/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.proc.wsdl.model;


/**
 * This class represents an Operation as defined in the WSDL It does not contain any information about the messages that are used
 * by the operation as they are of no interest until it is time to actually create an MM model
 *
 * @since 8.0
 */
public interface IOperation extends IWsdlElement {

    /**
     * @return the binding that contains this operation
     */
    IBinding getBinding();

    /**
     * @return the name of the input message
     */
    IMessage getInputMessage();

    /**
     * @return the name of the output message
     */
    IMessage getOutputMessage();

    /**
     * @return the style of the operation
     */
    String getStyle();

    /**
     * @return an array of the names of possible faults
     */
    IFault[] getFaults();

    String getSOAPAction();

    boolean canModel();

    String[] getProblemMessages();
}
