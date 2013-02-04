/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * 
 *
 * @since 8.0
 */
public class PreviewModelObjectLabelProvider implements ILightweightLabelDecorator {

    // ============================================================================================================================
    // Overridden methods

    /**
     * 
     */
    public PreviewModelObjectLabelProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
    public void addListener( ILabelProviderListener arg0 ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean isLabelProperty( Object arg0,
                                    String arg1 ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
    public void removeListener( ILabelProviderListener arg0 ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    public void decorate( Object eObject,
                          IDecoration decoration ) {

        // Ensure the server manager has been initialised, which ensures the teiid
        // server version has been correctly set.
        DqpPlugin.getInstance().getServerManager();
        
        boolean isPreviewable = ModelObjectUtilities.isExecutable((EObject)eObject);
        if (isPreviewable) {
            decoration.addOverlay(UiPlugin.getDefault().getPreviewableDecoratorImage(), IDecoration.BOTTOM_RIGHT);
        }
    }
}
