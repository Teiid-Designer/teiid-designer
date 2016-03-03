/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import java.util.Properties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.teiid.designer.core.loading.ComponentLoadingManager;
import org.teiid.designer.core.loading.IManagedLoading;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * 
 *
 * @since 8.0
 */
public class PreviewModelObjectLabelProvider implements ILightweightLabelDecorator, IManagedLoading {

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

    @Override
    public void manageLoad(Properties args) {
        final EObject eObject = (EObject) args.get(EObject.class);
        final IDecoration decoration = (IDecoration) args.get(IDecoration.class);

        Runnable runnable = new Runnable() {
            public void run() {
                boolean isPreviewable = ModelObjectUtilities.isExecutable(eObject);
                if (isPreviewable) {
                    decoration.addOverlay(UiPlugin.getDefault().getPreviewableDecoratorImage(), IDecoration.BOTTOM_RIGHT);
                }
            }
        };

        UiUtil.runInSwtThread(runnable, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    public void decorate( final Object eObject, final IDecoration decoration ) {

        // Ensure the server manager has been initialised. If necessary wait until it has been
        // correctly restored. This ensures the teiid server version has been correctly set.
        ComponentLoadingManager manager = ComponentLoadingManager.getInstance();
        Properties properties = new Properties();
        properties.put(eObject.getClass(), eObject);
        properties.put(decoration.getClass(), decoration);
        manager.manageLoading(this, properties);
    }
}
