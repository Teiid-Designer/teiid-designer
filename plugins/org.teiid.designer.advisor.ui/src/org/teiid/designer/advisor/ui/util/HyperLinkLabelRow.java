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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

/**
 * This class provides the Advisor a simple way to display an image and a label and have the image be wired as a hyper-link.
 */
public class HyperLinkLabelRow {
    private Label label;
    private ImageHyperlink link;

    private Color dimFGColor;
    private Color defFGColor;

    /**
     * 
     */
    @SuppressWarnings("unused")
	public HyperLinkLabelRow( final int id,
                              FormToolkit toolkit,
                              final Composite body,
                              final String mainLabel,
                              final IHyperlinkListener listener ) {
        super();

        LINK : {
	        link = toolkit.createImageHyperlink(body, SWT.WRAP);
	        link.setImage(AdvisorUiPlugin.getImageHelper().UNCHECKED_BOX_IMAGE);
	        link.addHyperlinkListener(listener);
	        link.setData(id);
	        GridData gd = new GridData(GridData.FILL);
	        gd.horizontalAlignment=GridData.BEGINNING;
	        gd.verticalAlignment = GridData.CENTER;
	        link.setLayoutData(gd);
        }
        
        LABEL : {
	        label = toolkit.createLabel(body, mainLabel, SWT.NONE);
	
	        GridData gd = new GridData(GridData.FILL);
	        gd.grabExcessHorizontalSpace = true;
	        gd.grabExcessVerticalSpace = false;
	        gd.horizontalAlignment=GridData.BEGINNING;
	        gd.verticalAlignment = GridData.CENTER;
	        label.setLayoutData(gd);
        }
        
        dimFGColor = toolkit.getColors().getColor(IFormColors.TB_TOGGLE_HOVER);
        defFGColor = toolkit.getColors().getColor(IFormColors.TB_FG);
    }

    public void setImage( Image image ) {
    	if( !this.link.getImage().isDisposed() ) {
    		this.link.setImage(image);
    	}
    }
    
    public void setText( String label ) {
    	if( !this.label.isDisposed() ) {
    		this.label.setText(label);
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

    public void update( Image linkImage,
    					String labelTT,
                        String linkTT ) {

        if (labelTT != null) {
            setLabelTooltip(labelTT);
        }

        if( linkImage != null ) {
        	link.setImage(linkImage);
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
        this.link.redraw();
    }
}
