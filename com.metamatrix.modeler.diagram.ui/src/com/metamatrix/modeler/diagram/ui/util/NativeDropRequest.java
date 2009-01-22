/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.gef.Request;

public class NativeDropRequest extends Request {

    private Object data;

    public static final String ID = "$Native Drop Request"; //$NON-NLS-1$

    public NativeDropRequest() {
        super(ID);
    }

    public NativeDropRequest(Object type) {
        super(type);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
