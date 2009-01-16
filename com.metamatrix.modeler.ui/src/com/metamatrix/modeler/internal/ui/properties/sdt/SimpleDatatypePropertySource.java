/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.properties.sdt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.views.properties.ComboBoxLabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.metamodels.xsd.aspects.sql.XsdSimpleTypeDefinitionAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * SimpleDatatypePropertySource is a property source for adding runtime type properties to
 * XSDSimpleTypeDefinitions.  It works by wrapping a delegate PropertySource from EMF and 
 * adding the runtime type DOM Node properties.  This class creates property descriptors for
 * the DOM Node properties and handles the get/set of property values.  All other EObject 
 * properties on XSDSimpleTypeDefinition are handled by the EMF property source.
 */
public class SimpleDatatypePropertySource implements IPropertySource {

    private static final String TYPE_NAME = UiConstants.Util.getString("RuntimeTypePropertyDescriptor.runtimeTypeName"); //$NON-NLS-1$    
    private static final String TYPE_FIXED = UiConstants.Util.getString("RuntimeTypePropertyDescriptor.runtimeTypeFixed"); //$NON-NLS-1$
    
    public static final String TYPE_NAME_DESCRIPTION = UiConstants.Util.getString("RuntimeTypePropertyDescriptor.runtimeTypeDescription"); //$NON-NLS-1$    
    public static final String TYPE_FIXED_DESCRIPTION = UiConstants.Util.getString("RuntimeTypePropertyDescriptor.runtimeTypeFixedDescription"); //$NON-NLS-1$
       
    public static String[] runtimeTypeFixedArray = new String[2];
    static {
        runtimeTypeFixedArray[0] = Boolean.TRUE.toString();
        runtimeTypeFixedArray[1] = Boolean.FALSE.toString();
    }    

    public static String[] runtimeTypeArray = (String[]) DatatypeConstants.getRuntimeTypeNames().toArray(runtimeTypeFixedArray);    
    
    private XSDSimpleTypeDefinition datatype;    
    private IPropertySource delegate;
    private List descriptorList;
    private ILabelProvider runtimeTypeLabelProvider;
    private ILabelProvider runtimeTypeFixedLabelProvider;


    /**
     * Construct an instance of SimpleDatatypePropertySource.
     */
    public SimpleDatatypePropertySource(XSDSimpleTypeDefinition datatype, IPropertySource emfPropertySource) {
        this.delegate = emfPropertySource;
        this.descriptorList = new ArrayList();
        this.runtimeTypeLabelProvider = new ComboBoxLabelProvider(runtimeTypeArray);
        this.runtimeTypeFixedLabelProvider = new ComboBoxLabelProvider(runtimeTypeFixedArray);
        this.datatype = datatype;
    }

    /*
     * Update the list of property descriptors for the XSDSimpleTypeDefinition.  
     * @since 4.2
     */
    private void updateDescriptorList() {
        // build the descriptor list
        EnterpriseDatatypeInfo edtInfo = ModelerCore.getDatatypeManager(datatype, true).getEnterpriseDatatypeInfo(datatype);
        Element element = datatype.getElement();
        Document doc = element.getOwnerDocument();

        Node node = doc.createAttribute(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME);
        Object uuid = (edtInfo != null && edtInfo.getUuid() != null ) ? (Object) edtInfo.getUuid() : ModelerCore.getDatatypeManager(datatype).getUuid(datatype);
        node.setNodeValue(uuid != null ? uuid.toString() : null);                        
        descriptorList.add(new NodePropertyDescriptor(datatype, node));

        // cannot edit any property of builtin types
        boolean isEditable = !ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype(datatype); 

        node = doc.createAttribute(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME);
        node.setNodeValue(edtInfo.getRuntimeType());
        ModelResourceComboBoxPropertyDescriptor descriptor = new ModelResourceComboBoxPropertyDescriptor(datatype, node, TYPE_NAME, runtimeTypeArray);
        descriptor.setEditable(isEditable);
        descriptor.setDescription(TYPE_NAME_DESCRIPTION);
        descriptor.setLabelProvider(runtimeTypeLabelProvider);
        descriptorList.add(descriptor);

        node = doc.createAttribute(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME);
        Boolean runtimeTypeFixed = edtInfo.getRuntimeTypeFixed();
        node.setNodeValue(runtimeTypeFixed != null ? runtimeTypeFixed.toString() : null);                        
        descriptor = new ModelResourceComboBoxPropertyDescriptor(datatype, node, TYPE_FIXED, runtimeTypeFixedArray);
        descriptor.setEditable(isEditable);
        descriptor.setDescription(TYPE_FIXED_DESCRIPTION);
        descriptor.setLabelProvider(runtimeTypeFixedLabelProvider);
        descriptorList.add(descriptor);        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return delegate.getEditableValue();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if(descriptorList.isEmpty()) {
            updateDescriptorList();
        }
        IPropertyDescriptor[] emfDescriptors = delegate.getPropertyDescriptors();
        
        IPropertyDescriptor[] result = new IPropertyDescriptor[emfDescriptors.length + descriptorList.size()];
        int index = 0;
        for ( ; index<emfDescriptors.length ; ++index ) {
            result[index] = emfDescriptors[index];
        }
        for ( Iterator nodeIter = descriptorList.iterator() ; nodeIter.hasNext() ; ++index ) {
            result[index] = (IPropertyDescriptor) nodeIter.next();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        if (id instanceof Node) {
            Object value = ((Node) id).getNodeValue();
            String nodeName = ((Node) id).getNodeName(); 
            if (nodeName.equals(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME)) {
                // runtime type value must return an integer in the list so the combo box works. stupid, huh?
                value = new Integer(findValueInArray(runtimeTypeArray, value));
            } else if (nodeName.equals(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME)) {
                value = new Integer(findValueInArray(runtimeTypeFixedArray, value));                
            }
            return value; 
        }
        return delegate.getPropertyValue(id);
    }

    private int findValueInArray(Object[] array, Object value) {
        if (array != null && value != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet(Object id) {
        if ( id instanceof Node ) {
            return ((Node) id).getNodeValue() != null;
        }
        return delegate.isPropertySet(id);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue(Object id) {
        if (id instanceof Node) {
            setPropertyValue(id, null);
            ((Node) id).setNodeValue(null);
        } else {        
            boolean started = ModelerCore.startTxn(ModelObjectPropertySource.SET + id.toString(), this);
            boolean succeeded = false;
            try {
                delegate.resetPropertyValue(id);
                succeeded = true;
            } finally {
                if (started) {
                    if ( succeeded ) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
        boolean started = ModelerCore.startTxn(ModelObjectPropertySource.SET + id.toString(), this);
        boolean succeeded = false;
        try {
            if (id instanceof Node) {
                final Object owner = this.delegate.getEditableValue();
                // The owning object should be a simple type, as nodes are used only when we're dealing
                // with enterprise attributes (remnants from the enterprise extensions formerly existing
                // as attributes in the annonation's application information tag). We only allow editing
                // of the runtime type and runtime type fixed (no uuid modifications). If the key of the node
                // is not one of these two's attribute name, we will do nothing.
                if (owner instanceof XSDSimpleTypeDefinition) {                              
                    XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) ModelObjectUtilities.getRealEObject((XSDSimpleTypeDefinition) owner);
                    EnterpriseDatatypeInfo edtInfo = ModelerCore.getDatatypeManager(simpleType).getEnterpriseDatatypeInfo(simpleType);
                    String nodeName = ((Node) id).getNodeName();
                    boolean isValidSet = false;
                    String newValue = ""; //$NON-NLS-1$
                    if (nodeName.equals(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME)) {                            
                        // runtime type value must convert from Integer to String
                        // because that's how the combo box works. stupid, huh?
                        edtInfo.setRuntimeType(value != null ? this.runtimeTypeLabelProvider.getText(value) : ""); //$NON-NLS-1$
                        newValue = edtInfo.getRuntimeType();
                        isValidSet = true;
                    } else if (nodeName.equals(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME)) {                                                
                        edtInfo.setRuntimeTypeFixed(Boolean.valueOf(value != null ? this.runtimeTypeFixedLabelProvider.getText(value) : "")); //$NON-NLS-1$
                        newValue = edtInfo.getRuntimeTypeFixed().toString();
                        isValidSet = true;
                    }
                    if (isValidSet) {
                        ModelerCore.getModelEditor().setEnterpriseDatatypePropertyValue(simpleType, edtInfo);
                        ((Node) id).setNodeValue(newValue);
                    }
                }                        
            } else {
                delegate.setPropertyValue(id, value);
            }
            succeeded = true;            
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }         
    }

}
