/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.teiid.designer.wsdl.soap.SoapBindingInfo;

/**
 * This class represents an Operation as defined in the WSDL It does not contain any information about the messages that are used
 * by the operation as they are of no interest until it is time to actually create an MM model
 */
public class Operation extends WSDLElement  {

    private Binding m_binding;
    private Message m_input;
    private Message m_output;
    private String m_style;
    private Fault[] m_faults;
    private String m_soapAction;
    private boolean m_canModel;
    private ArrayList m_messages;
    private SoapBindingInfo m_bindingInfo;

    public Operation( Binding binding ) {
        m_binding = binding;
        m_faults = new Fault[0];
        m_canModel = true;
        m_messages = new ArrayList();
    }

    public Binding getBinding() {
        return m_binding;
    }

    public WSDLElement copy() {
        Operation oper = new Operation(getBinding());
        oper.setName(getName());
        oper.setId(getId());
        oper.setStyle(getStyle());
        oper.setCanModel(canModel());
        oper.setSOAPAction(getSOAPAction());
        for (Iterator iter = m_messages.iterator(); iter.hasNext();) {
            oper.addProblemMessage((String)iter.next());
        }
        if (m_input != null) {
            oper.setInputMessage((Message)m_input.copy());
        }
        if (m_output != null) {
            oper.setOutputMessage((Message)m_output.copy());
        }
        Fault[] newFaults = new Fault[m_faults.length];
        for (int i = 0; i < m_faults.length; i++) {
            newFaults[i] = (Fault)m_faults[i].copy();
        }
        oper.setFaults(newFaults);
        return oper;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("<operation name='"); //$NON-NLS-1$
        buff.append(getName());
        buff.append("' id='"); //$NON-NLS-1$
        buff.append(getId());
        buff.append("' style='"); //$NON-NLS-1$
        buff.append(getStyle());
        buff.append("' soapAction='"); //$NON-NLS-1$
        buff.append(getSOAPAction());
        buff.append("' canModel='"); //$NON-NLS-1$
        buff.append(canModel());
        buff.append("'>"); //$NON-NLS-1$
        if (getInputMessage() != null) buff.append(getInputMessage().toString());
        if (getOutputMessage() != null) buff.append(getOutputMessage().toString());
        Fault[] faults = getFaults();
        if (faults != null) {
            for (int i = 0; i < faults.length; i++) {
                buff.append(faults[i].toString());
            }
        }
        buff.append("</operation>"); //$NON-NLS-1$
        return buff.toString();
    }

    public Message getInputMessage() {
        return m_input;
    }

    public void setInputMessage( Message inputMsg ) {
        m_input = inputMsg;

    }

    public Message getOutputMessage() {
        return m_output;
    }

    public void setOutputMessage( Message outputMsg ) {
        m_output = outputMsg;

    }

    public String getStyle() {
        return m_style;
    }

    public void setStyle( String style ) {
        m_style = style;

    }

    public Fault[] getFaults() {
        return m_faults;
    }

    public void setFaults( Fault[] faults ) {
        m_faults = faults;
    }

    public void setSOAPAction( String action ) {
        m_soapAction = action;
    }

    public String getSOAPAction() {
        return m_soapAction;
    }

    public boolean canModel() {
        return m_canModel;
    }

    public void setCanModel( boolean canModel ) {
        m_canModel = canModel;
    }

    public void addProblemMessage( String message ) {
        m_messages.add(message);
    }

    public String[] getProblemMessages() {
        String[] retVal = new String[m_messages.size()];
        m_messages.toArray(retVal);
        return retVal;
    }

    public SoapBindingInfo getSoapBindingInfo() {
        return m_bindingInfo;
    }

    public void setSoapBindingInfo( SoapBindingInfo info ) {
        m_bindingInfo = info;
    }

}
