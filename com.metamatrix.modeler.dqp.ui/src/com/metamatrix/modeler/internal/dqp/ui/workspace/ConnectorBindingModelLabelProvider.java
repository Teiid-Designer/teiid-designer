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

package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;


/**
 * This class provides the ability of connector binding information to be displayed. In particular, in conjunction with the
 * <code>IExtendedModelObject</code> interface and corresponding extension point, the connector binding can be contributed as a
 * child node of a JdbcSource object.  @see also <code>ConnectorBindingModelContentProvider</code>
 * @since 5.0
 */
public class ConnectorBindingModelLabelProvider extends LabelProvider {

    /**
     *
     * @since 5.0
     */
    public ConnectorBindingModelLabelProvider() {
        super();
    }

    //============================================================================================================================
    // Overridden methods

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.0
     */
    @Override
    public Image getImage(Object element) {
        try {

            if ( element instanceof ConnectorBindingSourceWrapper ) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
            }

        } catch (final Exception err) {
            DqpUiConstants.UTIL.log(err);
        }

        return super.getImage(element);
    }

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.0
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ConnectorBindingSourceWrapper) {
            return ((ConnectorBindingSourceWrapper)element).getConnectorBinding().getName();
        }

        // must return null to indicate we can't process that element type
        return null;
    }

}
