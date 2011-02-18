/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The <code>ExtendedTitleAreaDialog</code> class extends the functionality of the <code>TitleAreaDialog</code>.
 * The <code>Shell</code> is resizable and it's location and size is persisted in the dialog settings by default.
 */
public class ExtendedTitleAreaDialog extends TitleAreaDialog {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Dialog setting key for the shell's x position. */
    protected static final String SHELL_X = "shellX"; //$NON-NLS-1$

    /** Dialog setting key for the shell's y position. */
    protected static final String SHELL_Y = "shellY"; //$NON-NLS-1$

    /** Dialog setting key for the shell's width. */
    protected static final String SHELL_WIDTH = "shellWidth"; //$NON-NLS-1$

    /** Dialog setting key for the shell's height. */
    protected static final String SHELL_HEIGHT = "shellHeight"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private AbstractUIPlugin plugin;
    
    private boolean persistGeometry = true;
    
    private int initialWidthPercentage = -1;

    private int initialHeightPercentage = -1;
    

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Construct an instance of ExtendedTitleAreaDialog.
     * @param parentShell
     */
    public ExtendedTitleAreaDialog(Shell theParentShell,
                                   AbstractUIPlugin thePlugin) {
        super(theParentShell);
        this.plugin = thePlugin;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sets the percentage of the screen that the dialog width and height should be. If the given percentages
     * are negative or greater than 100, the percentage is set to 50.
     * @param theWidthPercentage the width percentage
     * @param theHeightPercentage the height percentage
     */
    public void setInitialSizeRelativeToScreen(int theWidthPercentage, int theHeightPercentage) {
        initialWidthPercentage = ((theWidthPercentage < 1) || (theWidthPercentage > 100)) ? 50
                                                                                   : theWidthPercentage;

        initialHeightPercentage = ((theHeightPercentage < 1) || (theHeightPercentage > 100)) ? 50
                                                                                      : theHeightPercentage;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    @Override
    public boolean close() {
        if (this.persistGeometry) {
            persistShellGeometry();
        }

        return super.close();
    }
    
    /**
     * Returns the name of the section that this dialog stores its settings in.
     * @return the section name
     */
    protected String getDialogSettingsSectionName() {
        return getClass().getName();
    }
    
    /**
     * Returns the dialog settings for this dialog.
     * @return the settings
     */
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = this.plugin.getDialogSettings();
        IDialogSettings section = settings.getSection(getDialogSettingsSectionName());

        if (section == null) {
            section = settings.addNewSection(getDialogSettingsSectionName());
        } 

        return section;
    }
    
    /**
     * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    @Override
    protected Point getInitialLocation(Point theInitialLocation) {
        Point result = null;

        // If persisting geometry, get location from the saved settings
        if (this.persistGeometry) {
            IDialogSettings settings = getDialogSettings();

            try {
                int x = settings.getInt(SHELL_X);
                int y = settings.getInt(SHELL_Y);

                result = new Point(x, y);
            } catch (NumberFormatException theException) {
            }
            // If no saved settings, use intial sizing
            if(result==null) {
                result = getInitialLocationFromRelativeScreen();
            }
        // Not persisting geometry, get location from initial size relative to screen
        } else {
            result = getInitialLocationFromRelativeScreen();
        }
        

        return (result == null) ? super.getInitialLocation(theInitialLocation)
                                : result;
    }
    
    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
     */
    @Override
    protected Point getInitialSize() {
        Point result = null;
        
        // If persisting geometry, get size from the saved settings
        if (this.persistGeometry) {
            IDialogSettings settings = getDialogSettings();

            try {
                int x = settings.getInt(SHELL_WIDTH);
                int y = settings.getInt(SHELL_HEIGHT);

                result = new Point(x, y);
            } catch (NumberFormatException theException) {
            }
            // If no saved settings, use intial sizing
            if(result==null) {
                result = getInitialSizeFromRelativeScreen();
            }
        // Not persisting geometry, get size from initial size relative to screen
        } else {
            result = getInitialSizeFromRelativeScreen();
        }

        return (result == null) ? super.getInitialSize()
                                : result;
    }

    /**
     * Write out this dialog's {@link Shell} size and location to the preference store.
     */
    protected void persistShellGeometry() {
        Point shellLocation = getShell().getLocation();
        Point shellSize = getShell().getSize();

        IDialogSettings settings = getDialogSettings();
        settings.put(SHELL_X, shellLocation.x);
        settings.put(SHELL_Y, shellLocation.y);
        settings.put(SHELL_WIDTH, shellSize.x);
        settings.put(SHELL_HEIGHT, shellSize.y);
    }

    /**
     * Get initial Size from Relative Screen settings, if they are set
     * @return the Initial screen size
     */
    protected Point getInitialSizeFromRelativeScreen() {
        Point result = null;
        if ((initialWidthPercentage >= 1) && (initialWidthPercentage <= 100) && 
            (initialHeightPercentage >= 1) && (initialHeightPercentage <= 100)) {
            
            Shell shell = getShell();
            Rectangle bounds = shell.getDisplay().getClientArea();
            int scaledWidth = (bounds.width * initialWidthPercentage / 100);
            int scaledHeight = (bounds.height * initialHeightPercentage / 100);
            
            result = new Point(scaledWidth,scaledHeight);
        }
        return result;
    }
    
    protected Point getInitialLocationFromRelativeScreen() {
        Point result = null;
        if ((initialWidthPercentage >= 1) && (initialWidthPercentage <= 100) && 
            (initialHeightPercentage >= 1) && (initialHeightPercentage <= 100)) {
            
            Shell shell = getShell();
            Rectangle bounds = shell.getDisplay().getClientArea();
            int scaledWidth = (bounds.width * initialWidthPercentage / 100);
            int scaledHeight = (bounds.height * initialHeightPercentage / 100);

            int excessX = bounds.width - scaledWidth;
            int excessY = bounds.height - scaledHeight;
            
            int x = bounds.x + (excessX / 2);
            int y = bounds.y + (excessY / 2);
            
            result = new Point(x,y);
        }
        return result;
    }
        
}
