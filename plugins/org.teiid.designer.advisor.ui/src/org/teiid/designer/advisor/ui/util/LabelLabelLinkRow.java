/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

/**
 * 
 */
public class LabelLabelLinkRow {
    private Label image;
    private Label label;
    private ImageHyperlink link;

    private Color dimFGColor;
    private Color defFGColor;

    /**
     * 
     */
    public LabelLabelLinkRow( final int id,
                              FormToolkit toolkit,
                              final Composite body,
                              final String mainLabel,
                              // final InfoPopAction[] actions,
                              final IHyperlinkListener listener ) {
        super();
        image = toolkit.createLabel(body, null);
        image.setImage(AdvisorUiPlugin.getImageHelper().UNCHECKED_BOX_IMAGE);
        label = toolkit.createLabel(body, mainLabel, SWT.NONE);
        link = toolkit.createImageHyperlink(body, SWT.WRAP);
        link.setImage(AdvisorUiPlugin.getImageHelper().LIGHTBULB_IMAGE);
        link.addHyperlinkListener(listener);
        link.setData(id);
        // link.addHyperlinkListener(new HyperlinkAdapter() {
        // @Override
        // public void linkActivated( HyperlinkEvent e ) {
        // // ModelerHelpUtil.openInfopop(statusSummaryHelpLink, WebServicesHelpConstants.PAGE_IDS.MODEL_PROBLEMS_HELP_ID);
        // Point point = image.getDisplay().getCursorLocation();
        // AdvisorFixDialog fixDialog = new AdvisorFixDialog(actions, point.x, point.y, toolkit);
        // fixDialog.open();
        // }
        // });

        dimFGColor = toolkit.getColors().getColor(IFormColors.TB_TOGGLE_HOVER);
        defFGColor = toolkit.getColors().getColor(IFormColors.TB_FG);
    }

    public void setImage( Image image ) {
    	if( !this.image.isDisposed() ) {
    		this.image.setImage(image);
    	}
    }

    public void setText( String label ) {
    	if( !this.label.isDisposed() ) {
    		this.label.setText(label);
    	}
    }

    public void setImageTooltip( String text ) {
    	if( !this.image.isDisposed() ) {
    		this.image.setToolTipText(text);
    	}
    }

    public void setLabelTooltip( String text ) {
    	if( !this.label.isDisposed() ) {
    		this.label.setToolTipText(text);
    	}
    }

    public void setLinkTooltip( String text ) {
    	if( !this.link.isDisposed() ) {
    		this.link.setToolTipText(text);
    	}
    }

    public void update( Image image,
                        String imageTT,
                        String labelTT,
                        String linkTT ) {
        if (image != null) {
            setImage(image);
        }

        if (imageTT != null) {
            setImageTooltip(imageTT);
        }

        if (labelTT != null) {
            setLabelTooltip(labelTT);
        }

        if (linkTT != null) {
            setLinkTooltip(linkTT);
        }
    }

    public void dim() {
        this.label.setForeground(dimFGColor);
    }

    public void brighten() {
        this.label.setForeground(defFGColor);
    }

    public void redraw() {
        this.image.redraw();
    }
}
