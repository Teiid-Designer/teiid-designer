/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.actions;

import org.eclipse.swt.widgets.Composite;

/**
 * The <code>DeleteAction</code> class deletes expressions and criteria found in the builders.
 */
public class NotCriteriaAction extends AbstractButtonAction {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>DeleteAction</code>. Properties are set using the plugin's
     * <code>i18n.properties</code> file.
     * @param theButtonParent the container of the button that is created
     * @param theDeleteHandler the handler that executes the delete method
     */
    public NotCriteriaAction(Composite theButtonParent,
                        Runnable theDeleteHandler) {
        super(theButtonParent, theDeleteHandler);
    }

}
