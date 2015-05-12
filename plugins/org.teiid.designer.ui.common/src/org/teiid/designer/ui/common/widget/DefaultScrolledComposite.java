/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.ui.common.widget;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *  Utility class that simplifies using a scrolled composite in a dialog or wizard
 *  
 *  Note that for the horizontal scroll to appear at least one sub-panel (child of the internalPanle) Composite needs 
 *  to have a minimum width or width hint set on it.
 *  
 *  i.e.     ((GridData)someGroup.getLayoutData()).minimumWidth = 400;
 */
public class DefaultScrolledComposite extends ScrolledComposite {
	Composite internalPanel;
	
	public DefaultScrolledComposite(Composite parent, int style) {
		super(parent, style);
		
		initialize();
	}
	
	public DefaultScrolledComposite(Composite parent) {
		this(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	}
	
	private void initialize() {
      GridLayoutFactory.fillDefaults().margins(20, 20).applyTo(this);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
        
        // customize scroll bars to give better scrolling behavior
        ScrollBar bar = getHorizontalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }

        bar = getVerticalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }
        
        internalPanel = new Composite(this, SWT.NONE);
        setContent(internalPanel);
	}
	
	/**
	 * this method should be called after the contents of the internal panel has been created
	 * 
	 */
	public void sizeScrolledPanel() {
        // need to size scroll panel
        Point pt = internalPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        setMinWidth(pt.x);
        setMinHeight(pt.y);
        setExpandHorizontal(true);
        setExpandVertical(true);
	}
	
	/**
	 * 
	 * @return
	 */
	public Composite getPanel() {
		return internalPanel;
	}
}
