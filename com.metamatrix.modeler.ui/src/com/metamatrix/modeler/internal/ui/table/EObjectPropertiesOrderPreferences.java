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

package com.metamatrix.modeler.internal.ui.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * @author SDelap
 */

public class EObjectPropertiesOrderPreferences implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map eObjectMap;
    private ModelObjectPropertySourceProvider propertySourceProvider;
    private final String newLine = "\n"; //$NON-NLS-1$
    private List listeners = new ArrayList();

    public EObjectPropertiesOrderPreferences() {
        this.eObjectMap = new HashMap();
    }

    public Set getInitializedEObjects() {
        return this.eObjectMap.keySet();
    }

    public void setProperty( String eObject,
                             String property ) {
        Set propertiesSet = getOrderedPropertiesSet(eObject);
        if (propertiesSet == null) {
            propertiesSet = new TreeSet();
            this.eObjectMap.put(eObject, propertiesSet);
        }
        PropertyOrder columnOrder = new PropertyOrder(property, propertiesSet.size() + 1);
        if (!propertiesSet.contains(columnOrder)) {
            propertiesSet.add(columnOrder);
        }
    }

    private TreeSet getOrderedPropertiesSet( String eObjectName ) {
        TreeSet propertyMap = (TreeSet)this.eObjectMap.get(eObjectName);
        return propertyMap;
    }

    public void replaceColumnsList( String eObject,
                                    ArrayList list ) {
        Set propertiesSet = getOrderedPropertiesSet(eObject);
        if (propertiesSet == null) {
            propertiesSet = new TreeSet();
            this.eObjectMap.put(eObject, propertiesSet);
        } else {
            propertiesSet.clear();
        }
        propertiesSet.addAll(list);
    }

    public void removeEObject( String eObject ) {
        this.eObjectMap.remove(eObject);
    }

    public ArrayList getOrderedPropertyList( String eObject ) {
        Set propertiesSet = getOrderedPropertiesSet(eObject);
        ArrayList columns;
        if (propertiesSet == null) {
            columns = new ArrayList();
        } else {
            columns = new ArrayList(propertiesSet);
        }
        Collections.sort(columns, new SortedPropertyOrderComparator());
        return columns;
    }

    public void addOrUpdateEObject( String eObject,
                                    EObject propertiesEObject ) {
        IPropertySource propertySource = getPropertySource(propertiesEObject);
        IPropertyDescriptor[] propertyDescriptors = propertySource.getPropertyDescriptors();

        // need to add 2 to the properties list since location and description are not properties
        for (int size = propertyDescriptors.length, i = 0; i < size; ++i) {
            setProperty(eObject, propertyDescriptors[i].getDisplayName());
        }
    }

    public void addOrUpdateProperty( String eObject,
                                     String column ) {
        setProperty(eObject, column);
    }

    private IPropertySource getPropertySource( EObject modelObject ) {
        if (propertySourceProvider == null) {
            propertySourceProvider = ModelUtilities.getPropertySourceProvider(); // new ModelObjectPropertySourceProvider();
        }
        return propertySourceProvider.getPropertySource(modelObject);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        Iterator keyIterator = getInitializedEObjects().iterator();
        while (keyIterator.hasNext()) {
            String eObject = (String)keyIterator.next();
            List properties = getOrderedPropertyList(eObject);
            if (properties.size() > 0) {
                Iterator propertiesIterator = properties.iterator();
                while (propertiesIterator.hasNext()) {
                    buffer.append(eObject);
                    buffer.append(newLine);
                    PropertyOrder propertyOrder = (PropertyOrder)propertiesIterator.next();
                    buffer.append(propertyOrder.getName());
                    buffer.append(newLine);
                    buffer.append(propertyOrder.getOrder());
                    buffer.append(newLine);
                    buffer.append(String.valueOf(propertyOrder.isVisible()));
                    buffer.append(newLine);
                }
            }
        }
        return buffer.toString();
    }

    public void initializeFromString( String stringRep ) {
        this.eObjectMap.clear();
        List lines = StringUtil.split(stringRep, newLine);
        int count = 0;
        while (count < lines.size()) {
            String eObject = (String)lines.get(count);
            String property = (String)lines.get(count + 1);
            String order = (String)lines.get(count + 2);
            String visible = (String)lines.get(count + 3);
            PropertyOrder propertyOrder = new PropertyOrder(property, Integer.parseInt(order));
            propertyOrder.setVisible(Boolean.valueOf(visible).booleanValue());
            Set columnSet = getOrderedPropertiesSet(eObject);
            if (columnSet == null) {
                columnSet = new TreeSet();
                this.eObjectMap.put(eObject, columnSet);
            }
            columnSet.add(propertyOrder);
            count += 4;
        }
    }

    class SortedPropertyOrderComparator implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare( Object o1,
                            Object o2 ) {
            int result = -1;
            if ((o1 instanceof PropertyOrder) & (o2 instanceof PropertyOrder)) {
                PropertyOrder p1 = (PropertyOrder)o1;
                PropertyOrder p2 = (PropertyOrder)o2;
                if (p1.getOrder() > p2.getOrder()) {
                    result = 1;
                } else if (p1.getOrder() < p2.getOrder()) {
                    result = -1;
                } else {
                    result = p1.getName().compareTo(p2.getName());
                }
            }
            return result;
        }
    }

    public void addEObjectPropertiesOrderPreferencesListener( EObjectPropertiesOrderPreferencesListener listener ) {
        this.listeners.add(listener);
    }

    public void removeEObjectPropertiesOrderPreferencesListener( EObjectPropertiesOrderPreferencesListener listener ) {
        this.listeners.remove(listener);
    }

    public void firePropertiesChanged( List changedEObjects ) {
        for (int i = 0; i < this.listeners.size(); i++) {
            ((EObjectPropertiesOrderPreferencesListener)this.listeners.get(i)).propertiesChanged(changedEObjects);
        }
    }
}
