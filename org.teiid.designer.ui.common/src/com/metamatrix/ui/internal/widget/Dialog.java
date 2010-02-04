/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * A resizable dialog
 * @since 4.0
 */
public class Dialog extends org.eclipse.jface.dialogs.Dialog {
    //============================================================================================================================
	// Variables
    
    private int widthPercentage = -1;

    private int heightPercentage = -1;
    
    private boolean centerOnDisplay = false;

    private String title;
    
    //============================================================================================================================
	// Constructors

	/**<p>
	 * </p>
	 * @param parent
	 * @since 4.0
	 */
	public Dialog(final Shell parent, final String title) {
		super(parent);
        setTitle(title);
	}

    //============================================================================================================================
	// Overridden Methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#constrainShellSize()
     */
    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();
		        
        if ((widthPercentage >= 1) && (widthPercentage <= 100) && (heightPercentage >= 1) &&
        		(heightPercentage <= 100)) {
            Shell shell = getShell();
            Rectangle bounds = shell.getDisplay().getClientArea();
			int scaledWidth = (bounds.width * widthPercentage / 100);
			int scaledHeight = (bounds.height * heightPercentage / 100);
            shell.setSize(scaledWidth, scaledHeight);
            Point size = shell.getSize();

			int x;
			int y;
			if (this.centerOnDisplay) {
				int excessX = bounds.width - size.x;
				int excessY = bounds.height - size.y;
				x = bounds.x + (excessX / 2);
				y = bounds.y + (excessY / 2);
			} else {
		        // move the shell origin as required
		        Point loc = shell.getLocation();
		
		        //Choose the position between the origin of the client area and 
		        //the bottom right hand corner
		        x = Math.max(bounds.x, Math.min(loc.x, bounds.x + bounds.width - size.x));
		        y = Math.max(bounds.y, Math.min(loc.y, bounds.y + bounds.height - size.y));
			}
			shell.setLocation(x, y);
        }
    }

    /**<p>
     * </p>
     * @see org.eclipse.jface.window.Window#create()
     * @since 4.0
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
        getShell().setText(this.title); 
        this.title = null;
    }
    
    //============================================================================================================================
	// Property Methods

    /**
     * Sets the percentage of the screen that the dialog width and height should be. If the given percentages
     * are negative or greater than 100, the percentage is set to 50.
     * @param theWidthPercentage the width percentage
     * @param theHeightPercentage the height percentage
     */
    public void setSizeRelativeToScreen(int theWidthPercentage, int theHeightPercentage) {
        widthPercentage = ((theWidthPercentage < 1) || (theWidthPercentage > 100)) ? 50
                                                                                   : theWidthPercentage;

        heightPercentage = ((theHeightPercentage < 1) || (theHeightPercentage > 100)) ? 50
                                                                                      : theHeightPercentage;
    }

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	public String getTitle() {
        final Shell shell = getShell();
		return (shell == null ? this.title : getShell().getText());
	}
    
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	public void setTitle(final String title) {
        ArgCheck.isNotNull(title);
        final Shell shell = getShell();
        if (shell == null) {
            this.title = title;
        } else {
            shell.setText(title);
        }
	}

	/**
	 * Set the centerOnDisplay flag
	 * @param flag   true if this dialog is to be centered on the Display, false otherwise; only takes effect if setSizeRelativeToScreen() has been called
	 */
	public void setCenterOnDisplay(boolean flag) {
		this.centerOnDisplay = flag;
	}
	
	/**
	 * Get the centerOnDisplay flag
	 * @return  true if this dialog is to be centered on the Display, false otherwise; only takes effect if setSizeRelativeToScreen() has been called
	 */
	public boolean getCenterOnDisplay() {
		return this.centerOnDisplay;
	}
}
