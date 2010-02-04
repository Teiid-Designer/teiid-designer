/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;


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
