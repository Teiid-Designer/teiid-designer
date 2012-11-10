/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.teiid.designer.runtime.ui.DqpUiPlugin;

/**
 * @since 8.0
 */
public class TeiidViewDecoratingLabelProvider extends DecoratingLabelProvider implements IDescriptionProvider {

    /**
     * Create a new instance
     */
    public TeiidViewDecoratingLabelProvider() {
        super(new TeiidServerLabelProvider(), DqpUiPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator());
    }
    
    @Override
    public String getDescription(Object element) {
        TeiidServerLabelProvider labelDecorator = (TeiidServerLabelProvider) getLabelProvider();
        return labelDecorator.getDescription(element);
    }
}