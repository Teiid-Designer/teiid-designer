/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core.status;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * 
 */
public class StatusRow {
    private IStatusRowProvider rowProvider;
    private Label image;
    private Label label;
    private ImageHyperlink link;

    private Color dimFGColor;
    private Color defFGColor;

    /**
     * 
     */
    public StatusRow( IStatusRowProvider rowProvider,
                      FormToolkit toolkit,
                      final Composite body,
                      final IHyperlinkListener listener ) {
        super();
        this.rowProvider = rowProvider;
        this.image = toolkit.createLabel(body, null);
        this.image.setImage(rowProvider.getImage(null));
        this.label = toolkit.createLabel(body, rowProvider.getText(null), SWT.NONE);
        this.link = toolkit.createImageHyperlink(body, SWT.WRAP);
        this.link.setImage(rowProvider.getLinkImage(null));
        this.link.addHyperlinkListener(listener);
        this.link.setData(rowProvider.getId());

        this.dimFGColor = toolkit.getColors().getColor(IFormColors.TB_TOGGLE_HOVER);
        this.defFGColor = toolkit.getColors().getColor(IFormColors.TB_FG);
    }

    public void update( IStatus status ) {
    	if( isNotDisposed() ) {
	        this.image.setImage(rowProvider.getImage(status));
	        this.image.setToolTipText(rowProvider.getImageTooltip(status));

	        this.label.setText(rowProvider.getText(status));
	        this.label.setToolTipText(rowProvider.getTextTooltip(status));

	        this.link.setImage(rowProvider.getLinkImage(status));
	        this.link.setToolTipText(rowProvider.getLinkTooltip(status));

	        redraw();
        }
    }

    public void dim() {
    	if( isNotDisposed() ) {
    		this.label.setForeground(dimFGColor);
    	}
    }

    public void brighten() {
    	if( isNotDisposed() ) {
    		this.label.setForeground(defFGColor);
    	}
    }

    public void redraw() {
    	if( isNotDisposed() ) {
	        this.image.redraw();
	        this.label.redraw();
	        this.link.redraw();
    	}
    }
    
    private boolean isNotDisposed() {
    	return !this.image.isDisposed() && ! this.label.isDisposed() && !this.link.isDisposed();
    }
}
