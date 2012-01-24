/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * 
 */
public class AdvisorHyperLinkListener extends HyperlinkAdapter {

    private FormToolkit toolkit;
    private IAdvisorActionHandler actionHandler;
    private Composite parent;

    /**
     * 
     */
    public AdvisorHyperLinkListener( Composite parent,
                                       FormToolkit toolkit,
                                       IAdvisorActionHandler actionHandler ) {
        this.parent = parent;
        this.toolkit = toolkit;
        this.actionHandler = actionHandler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
     */
    @Override
    public void linkActivated( HyperlinkEvent e ) {
        if (e.getSource() != null) {
            int groupType = Integer.parseInt(((ImageHyperlink)e.getSource()).getData().toString());
            Point point = parent.getDisplay().getCursorLocation();
            AdvisorFixDialog fixDialog = new AdvisorFixDialog(actionHandler.getActions(groupType), point.x, point.y, toolkit);
            fixDialog.open();
        }
    }

}
