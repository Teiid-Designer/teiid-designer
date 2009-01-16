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

package com.metamatrix.modeler.internal.ui.properties.udp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySource;

/**
 * UserDefinedPropertySource is the property source for user-defined properties on EObjects.
 */
public class UserDefinedPropertySource implements IPropertySource {

    private EObject object;
    private Collection tags;

    /**
     * Construct an instance of UserDefinedPropertySource.
     */
    public UserDefinedPropertySource(EObject object) {
        this.object = object;
        this.tags = ModelerCore.getModelEditor().getTags(object);
    }

    public boolean isUserDefinedProperty(Object id) {
        return id instanceof EStringToStringMapEntryImpl;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return this.object;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        final Collection temp = new ArrayList();
        final Iterator iter = tags.iterator();
        while(iter.hasNext() ) {
            EcoreEMap map = (EcoreEMap) iter.next();
            final Iterator entries = map.entrySet().iterator();
            while(entries.hasNext() ){
                temp.add(entries.next() );
            }
        }

        IPropertyDescriptor[] result = new IPropertyDescriptor[temp.size()];
        
        final Iterator tempIt = temp.iterator();
        int i = 0;
        while(tempIt.hasNext() ){
            Object next = tempIt.next();
            if(next instanceof EStringToStringMapEntryImpl){
                final EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)next;
                result[i++] = new UserDefinedPropertyDescriptor(entry, entry.getKey());
            }                
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        if ( id instanceof EStringToStringMapEntryImpl ) {
            return ((EStringToStringMapEntryImpl) id).getValue();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet(Object id) {
        return getPropertyValue(id) != null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue(Object id) {
        if ( id instanceof EStringToStringMapEntryImpl ) {
            EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl) id;
            String name = entry.getKey();
            boolean started = ModelerCore.startTxn(ModelObjectPropertySource.RESET + name, this);
            boolean succeeded = false;
            try {
                ((EStringToStringMapEntryImpl) id).setValue(null);
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

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
        if ( id instanceof EStringToStringMapEntryImpl ) {

            EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl) id;
            String name = entry.getKey();

            boolean started = ModelerCore.startTxn(ModelObjectPropertySource.SET + name, this);
            boolean succeeded = false;
            try {
                ((EStringToStringMapEntryImpl) id).setValue((String)value);
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

}
