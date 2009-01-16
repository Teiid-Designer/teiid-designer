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

package com.metamatrix.modeler.internal.dqp.ui.config;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.metamatrix.ui.internal.util.WidgetFactory;


/** 
 * @since 4.2
 */
public class ConnectorBindingPasswordDescriptor extends TextPropertyDescriptor {

    /**
     * Creates an property descriptor with the given id and display name.
     * 
     * @param id the id of the property
     * @param displayName the name to display for the property
     */
    public ConnectorBindingPasswordDescriptor(Object id, String displayName) {
    	super(id, displayName);
    }
    
    /** 
     * @see org.eclipse.ui.views.properties.TextPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public CellEditor createPropertyEditor(Composite theParent) {
        CellEditor result = super.createPropertyEditor(theParent);

        if (result.getControl() instanceof Text) {
            ((Text)result.getControl()).setEchoChar(WidgetFactory.PASSWORD_ECHO_CHAR);
        }

        return result;
    }

}
