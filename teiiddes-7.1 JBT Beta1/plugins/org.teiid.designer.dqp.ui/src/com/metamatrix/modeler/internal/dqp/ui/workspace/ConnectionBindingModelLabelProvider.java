/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
public class ConnectionBindingModelLabelProvider extends LabelProvider {

    /**
     *
     * @since 5.0
     */
    public ConnectionBindingModelLabelProvider() {
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

            if ( element instanceof ConnectionBindingSourceWrapper ) {
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
        if (element instanceof ConnectionBindingSourceWrapper) {
            return ((ConnectionBindingSourceWrapper)element).getConnector().getName();
        }

        // must return null to indicate we can't process that element type
        return null;
    }

}
