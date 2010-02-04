/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.util;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;

/**
 * MessageTableDialog
 */
public class MessageTableDialog extends MessageDialog
                                implements IRoseUiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(MessageTableDialog.class);
    
    private static final String DIALOG_MSG = UTIL.getString(PREFIX + "message"); //$NON-NLS-1$
    
    private static final String DIALOG_TITLE = UTIL.getString(PREFIX + "title"); //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static void openDialog(Shell theShell,
                                   int theDialogType,
                                   List theMessages) {
        MessageTableDialog dialog = new MessageTableDialog(theShell, DIALOG_TITLE, null, DIALOG_MSG, theDialogType);
        dialog.setMessages(theMessages);
        dialog.open();
        return;
    }
    
    /** 
     * @param theShell the parent window
     * @param theMessages the collection of messages
     * @since 4.1
     */
    public static void openError(Shell theShell,
                                 List theMessages) {
        openDialog(theShell, ERROR, theMessages);
    }
    
    /** 
     * @param theShell the parent window
     * @param theMessages the collection of messages
     * @since 4.1
     */
    public static void openInformation(Shell theShell,
                                       List theMessages) {
        openDialog(theShell, INFORMATION, theMessages);
    }
    
    /** 
     * @param theShell the parent window
     * @param theMessages the collection of messages
     * @since 4.1
     */
    public static void openWarning(Shell theShell,
                                   List theMessages) {
        openDialog(theShell, WARNING, theMessages);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private List messages = Collections.EMPTY_LIST;

    private MessageTableViewForm msgTable;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * Constructs a <code>MessageTableViewForm</code>.
     * @param theShell the parent window
     * @param theTitle the localized dialog title
     * @param theImage the dialog title image
     * @param theMessage the dialog message
     * @param theDialogImageType the dialog image type
     * @since 4.1
     */
    public MessageTableDialog(Shell theShell,
                              String theTitle,
                              Image theImage,
                              String theMessage,
                              int theDialogImageType) {
        super(theShell, theTitle, null, theMessage, theDialogImageType, new String[] {IDialogConstants.OK_LABEL}, 0);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea(Composite theParent) {
        this.msgTable = new MessageTableViewForm(theParent);
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.msgTable.setLayoutData(gd);
        this.msgTable.setMessages(this.messages); 

        return this.msgTable;
    }
    
    /** 
     * Sets the collection of messages being displayed.
     * @param theMessages the messages
     * @since 4.1
     */
    public void setMessages(List theMessages) {
        this.messages = (theMessages == null) ? Collections.EMPTY_LIST
                                              : theMessages;
    }

}
