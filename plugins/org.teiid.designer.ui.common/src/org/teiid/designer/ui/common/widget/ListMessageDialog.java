/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.widget;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.ui.common.util.UiUtil;


/** 
 * A <code>MessageDialog</code> that shows a list of items.
 * @since 8.0
 */
public class ListMessageDialog extends MessageDialog {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String[] DEFAULT_BUTTONS  = new String[] {IDialogConstants.OK_LABEL};
    private static final String[] QUESTION_BUTTONS = new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL};
    
    private static boolean allowResize;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs and opens a dialog.
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theDialogType the dialog type (error, warning, info)
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @return the return code
     */
    private static int openDialog(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   int theDialogType,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider) {
        return openDialog(theShell, theTitle, theImage, theMessage, theDialogType, theItems, theLabelProvider, DEFAULT_BUTTONS, false);
    }
    
    /**
     * Constructs and opens a dialog.
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theDialogType the dialog type (error, warning, info)
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @param allowResizing allow dialog to be resized
     * @return the return code
     */
    private static int openDialog(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   int theDialogType,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider,
                                   boolean allowResizing) {
    	return openDialog(theShell, theTitle, theImage, theMessage, theDialogType, theItems, theLabelProvider, DEFAULT_BUTTONS, allowResizing);
    }
    
    /**
     * Constructs and opens a dialog.
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theDialogType the dialog type (error, warning, info)
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @param dialogButtonLabels the labels to use for buttons
     * @return the return code
     */
    private static int openDialog(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   int theDialogType,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider,
                                   String[] dialogButtonLabels,
                                   boolean allowResizing) {
    	allowResize = allowResizing;
        ListMessageDialog dialog = new ListMessageDialog(theShell, theTitle, theImage, theMessage, theDialogType, dialogButtonLabels);
        dialog.setLabelProvider(theLabelProvider);
        dialog.setItems(theItems);
        int result = dialog.open();
        allowResize = false;
        return result;
    }

    /**
     * Opens an error dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static void openError(Shell theShell,
                                 String theTitle,
                                 Image theImage,
                                 String theMessage,
                                 List theItems,
                                 IBaseLabelProvider theLabelProvider) {
        openDialog(theShell, theTitle, theImage, theMessage, ERROR, theItems, theLabelProvider);
    }
    
    /**
     * Opens an error dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @param allowResizing allow dialog to be resized
     * @since 4.2
     */
    public static void openError(Shell theShell,
                                 String theTitle,
                                 Image theImage,
                                 String theMessage,
                                 List theItems,
                                 IBaseLabelProvider theLabelProvider,
                                 boolean allowResizing) {
        openDialog(theShell, theTitle, theImage, theMessage, ERROR, theItems, theLabelProvider, allowResizing);
    }
    
    /**
     * Opens an information dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static void openInformation(Shell theShell,
                                       String theTitle,
                                       Image theImage,
                                       String theMessage,
                                       List theItems,
                                       IBaseLabelProvider theLabelProvider) {
        openDialog(theShell, theTitle, theImage, theMessage, INFORMATION, theItems, theLabelProvider);
    }
    
    /**
     * Opens an information dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @param allowResizing allow dialog to be resized
     * @since 4.2
     */
    public static void openInformation(Shell theShell,
                                       String theTitle,
                                       Image theImage,
                                       String theMessage,
                                       List theItems,
                                       IBaseLabelProvider theLabelProvider,
                                       boolean allowResizing) {
        openDialog(theShell, theTitle, theImage, theMessage, INFORMATION, theItems, theLabelProvider, allowResizing);
    }
    
    /**
     * Opens a warning dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static void openWarning(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider) {
        openDialog(theShell, theTitle, theImage, theMessage, WARNING, theItems, theLabelProvider);
    }
    
    /**
     * Opens a warning dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static void openWarning(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider,
                                   boolean allowResizing) {
        openDialog(theShell, theTitle, theImage, theMessage, WARNING, theItems, theLabelProvider, allowResizing);
    }

    /**
     * Opens a confirm dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static boolean openQuestion(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider) {
        return openDialog(theShell, theTitle, theImage, theMessage, QUESTION, theItems, theLabelProvider, QUESTION_BUTTONS, false) == OK;
    }
    
    /**
     * Opens a confirm dialog showing the specified items. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @param allowResizing allow dialog to be resized
     * @since 4.2
     */
    public static boolean openQuestion(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider,
                                   boolean allowResizing) {
        return openDialog(theShell, theTitle, theImage, theMessage, QUESTION, theItems, theLabelProvider, QUESTION_BUTTONS, allowResizing) == OK;
    }

    /**
     * Opens a confirm dialog showing the specified items, using a WARNING icon. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static boolean openWarningQuestion(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider) {
        return openWarningQuestion(theShell, theTitle, theImage, theMessage, theItems, theLabelProvider, false);
    }
    
    /**
     * Opens a confirm dialog showing the specified items, using a WARNING icon. 
     * @param theShell the parent window
     * @param theTitle the dialog title
     * @param theImage the dialog image (may be <code>null</code>)
     * @param theMessage the dialog message
     * @param theItems the items being displayed  (may be <code>null</code>)
     * @param theLabelProvider the list label provider  (may be <code>null</code>)
     * @since 4.2
     */
    public static boolean openWarningQuestion(Shell theShell,
                                   String theTitle,
                                   Image theImage,
                                   String theMessage,
                                   List theItems,
                                   IBaseLabelProvider theLabelProvider,
                                   boolean allowResizing) {
        return openDialog(theShell, theTitle, theImage, theMessage, WARNING, theItems, theLabelProvider, QUESTION_BUTTONS, allowResizing) == OK;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Items being displayed. */
    private List items;
    
    /** List label provider. */
    private IBaseLabelProvider labelProvider;
    
    /** The list viewer. */
    private ListViewer viewer;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected ListMessageDialog(Shell theShell,
                                String theTitle,
                                Image theImage,
                                String theMessage,
                                int theDialogImageType,
                                String[] dialogButtonLabels) {
        super(theShell, theTitle, null, theMessage, theDialogImageType, dialogButtonLabels, 0);
        if( allowResize ) {
        	setShellStyle(getShellStyle() | SWT.RESIZE);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea(Composite theParent) {
        this.viewer = new ListViewer(theParent, SWT.READ_ONLY | SWT.HIDE_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        
        // set input items
        if (this.items == null) {
            this.items = Collections.EMPTY_LIST;
        }
        
        // configure List control
        org.eclipse.swt.widgets.List list = this.viewer.getList();
        if( allowResize ) {
        	int heightHint = items.size() > 5 ? 300 : 200;
        	GridDataFactory.fillDefaults().hint(400, heightHint).grab(true,  true).applyTo(list);
        } else {
        	list.setLayoutData(new GridData(GridData.FILL_BOTH));
        }
        
        // set label provider
        if (this.labelProvider != null) {
            this.viewer.setLabelProvider(this.labelProvider);
        }

        
        this.viewer.add(this.items.toArray());
        
        return list;
    }

    /**
     * Sets the items to display in the dialog.
     * @param theItems the items to display
     * @since 4.2
     */
    protected void setItems(List theItems) {
        List oldItems = this.items;
        this.items = theItems;
        
        if (this.viewer != null) {
            this.viewer.remove(oldItems);
            this.viewer.add(this.items);
        }
    }
    
    /**
     * Sets the {@link ListViewer}'s <code>IBaseLabelProvider</code>. 
     * @param theLabelProvider the label provider being set
     * @since 4.2
     */
    protected void setLabelProvider(IBaseLabelProvider theLabelProvider) {
        this.labelProvider = theLabelProvider;
        
        if (this.viewer != null) {
            this.viewer.setLabelProvider(this.labelProvider);
        }
    }
    
}
