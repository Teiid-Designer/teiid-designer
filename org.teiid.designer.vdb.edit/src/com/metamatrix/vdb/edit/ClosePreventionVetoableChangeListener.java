/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

public class ClosePreventionVetoableChangeListener implements VetoableChangeListener {
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (VdbEditingContext.CLOSING.equals(evt.getPropertyName())) {
            // note: string below not visible to users.
            throw new PropertyVetoException("VDB in use", evt); //$NON-NLS-1$
        } // endif
    }
}
