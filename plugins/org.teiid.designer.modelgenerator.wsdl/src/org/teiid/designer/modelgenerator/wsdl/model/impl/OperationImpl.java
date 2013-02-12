/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import org.teiid.designer.modelgenerator.wsdl.SoapBindingInfo;
import org.teiid.designer.modelgenerator.wsdl.model.Binding;
import org.teiid.designer.modelgenerator.wsdl.model.Fault;
import org.teiid.designer.modelgenerator.wsdl.model.Message;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;


/**
 * @since 8.0
 */
public class OperationImpl extends WSDLElementImpl implements Operation {

    private Binding m_binding;
    private Message m_input;
    private Message m_output;
    private String m_style;
    private Fault[] m_faults;
    private String m_soapAction;
    private boolean m_canModel;
    private ArrayList m_messages;
    private SoapBindingInfo m_bindingInfo;

    public OperationImpl( Binding binding ) {
        m_binding = binding;
        m_faults = new Fault[0];
        m_canModel = true;
        m_messages = new ArrayList();
    }

    @Override
	public Binding getBinding() {
        return m_binding;
    }

    @Override
	public Operation copy() {
        Operation oper = new OperationImpl(getBinding());
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

    @Override
	public Message getInputMessage() {
        return m_input;
    }

    @Override
	public void setInputMessage( Message inputMsg ) {
        m_input = inputMsg;

    }

    @Override
	public Message getOutputMessage() {
        return m_output;
    }

    @Override
	public void setOutputMessage( Message outputMsg ) {
        m_output = outputMsg;

    }

    @Override
	public String getStyle() {
        return m_style;
    }

    @Override
	public void setStyle( String style ) {
        m_style = style;

    }

    @Override
	public Fault[] getFaults() {
        return m_faults;
    }

    @Override
	public void setFaults( Fault[] faults ) {
        m_faults = faults;
    }

    @Override
	public void setSOAPAction( String action ) {
        m_soapAction = action;
    }

    @Override
	public String getSOAPAction() {
        return m_soapAction;
    }

    @Override
	public boolean canModel() {
        return m_canModel;
    }

    @Override
	public void setCanModel( boolean canModel ) {
        m_canModel = canModel;
    }

    @Override
	public void addProblemMessage( String message ) {
        m_messages.add(message);
    }

    @Override
	public String[] getProblemMessages() {
        String[] retVal = new String[m_messages.size()];
        m_messages.toArray(retVal);
        return retVal;
    }

    @Override
	public SoapBindingInfo getSoapBindingInfo() {
        return m_bindingInfo;
    }

    @Override
	public void setSoapBindingInfo( SoapBindingInfo info ) {
        m_bindingInfo = info;
    }

}
