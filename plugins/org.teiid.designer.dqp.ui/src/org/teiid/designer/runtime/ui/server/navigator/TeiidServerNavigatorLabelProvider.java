/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.TeiidViewTreeProvider;

/**
 *
 * @since 8.0
 */
public class TeiidServerNavigatorLabelProvider implements ILabelProvider {

    private TeiidViewTreeProvider delegate = new TeiidViewTreeProvider();

    @Override
    public void addListener(ILabelProviderListener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return delegate.isLabelProperty(element, property);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        delegate.removeListener(listener);
    }

    @Override
    public Image getImage(Object element) {
        return delegate.getImage(element);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TeiidServer) {
            return "Teiid Server"; //$NON-NLS-1$
        }
        return delegate.getText(element);
    }

}
