/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import java.util.List;

/**
 * @author SDelap
 * Listens for then ModelTableColumnUtils has changed.
 */
public interface EObjectPropertiesOrderPreferencesListener {
    public void propertiesChanged(List changedProperties);
}
