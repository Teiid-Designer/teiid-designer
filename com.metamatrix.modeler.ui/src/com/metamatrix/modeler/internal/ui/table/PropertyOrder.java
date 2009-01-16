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

/**
 * @author SDelap Wrapper for column order and visibility
 */
public class PropertyOrder implements Comparable, Cloneable {
    private String name;
    private boolean visible = true;
    private int order;
    
    public PropertyOrder( String name,
                          int order ) {
        this.name = name;
        this.order = order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public String getName() {
        return this.name;
    }
        
    public int compareTo(Object o) {
        int result = -1;
        if (o instanceof PropertyOrder) {
           PropertyOrder col2 = (PropertyOrder) o; 
           result = this.getName().compareTo(col2.getName());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyOrder) {
            PropertyOrder col2 = (PropertyOrder) obj;
            return this.name.equals(col2.getName());
        }
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.name);
        buffer.append(" - "); //$NON-NLS-1$
        buffer.append(this.order);
        return buffer.toString();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        PropertyOrder columnOrder = new PropertyOrder(this.getName(), this.getOrder());
        columnOrder.setVisible(this.isVisible());
        return columnOrder;
    }
}
