/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * This class provides an easy means for developers to present a radio-button dialog.
 * 
 * @author pforhan
 */
public class RadioMessageDialog extends MessageDialog {

    private String[] radioEntries;
    private String radioTitle;
    private Integer selected;
    private int dftRadio;
    private IStatus[] errorMessages;
    private MessageLabel ml;

    /**
     * @param parentShell
     * @param dialogTitle
     * @param dialogTitleImage
     * @param dialogMessage
     * @param dialogImageType
     * @param dialogButtonLabels
     * @param defaultIndex
     */
    public RadioMessageDialog( Shell parentShell,
                               String dialogTitle,
                               Image dialogTitleImage,
                               String dialogMessage,
                               int dialogImageType,
                               String dialogRadioTitle,
                               String[] dialogRadioLabelsAndValues,
                               int defaultRadioIndex,
                               String[] dialogButtonLabels,
                               int defaultButtonIndex ) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultButtonIndex);
        radioTitle = dialogRadioTitle;
        radioEntries = dialogRadioLabelsAndValues;
        dftRadio = defaultRadioIndex;
        selected = new Integer(defaultRadioIndex);
    }

    /**
     * @param parent
     * @param title
     * @param object
     * @param message
     * @param warning
     * @param optionsTitle
     * @param options
     * @param defaultRadioButton
     * @param errorMessages
     * @param strings
     * @param defaultButtonIndex
     */
    public RadioMessageDialog( Shell parentShell,
                               String dialogTitle,
                               Image dialogTitleImage,
                               String dialogMessage,
                               int dialogImageType,
                               String dialogRadioTitle,
                               String[] dialogRadioLabelsAndValues,
                               IStatus[] errorMessages,
                               int defaultRadioIndex,
                               String[] dialogButtonLabels,
                               int defaultButtonIndex ) {
        this(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogRadioTitle,
             dialogRadioLabelsAndValues, defaultRadioIndex, dialogButtonLabels, defaultButtonIndex);
        this.errorMessages = errorMessages;
    }

    /**
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createMessageArea( Composite parent ) {
        super.createMessageArea(parent);
        new Composite(parent, SWT.NONE); // blank filler
        return getRadioControl(parent, radioTitle, radioEntries, dftRadio);
    }

    @Override
    protected Control createCustomArea( Composite parent ) {
        super.createCustomArea(parent);
        if (errorMessages == null) {
            errorMessages = new IStatus[radioEntries.length];
        } // endif
        ml = new MessageLabel(parent);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        ml.setLayoutData(data);

        setSelected(selected); // since selected has already been set, this triggers error status text
        return ml;
    }

    void setSelected( Integer which ) {
        selected = which;
        IStatus err = errorMessages[which.intValue()];
        if (err != null) {
            ml.setErrorStatus(err);
            ml.redraw();
            // TODO we should probably disable OK if we get an error status.
        } else {
            ml.setErrorStatus(null);
        } // endif

    }

    public Composite getRadioControl( Composite parent,
                                      String groupTitle,
                                      String[] radioNames,
                                      int defaultRadio ) {
        Font font = parent.getFont();

        Group radioGroup = new Group(parent, SWT.NONE);
        GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
                                     | GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        radioGroup.setLayoutData(data);

        radioGroup.setFont(font);
        if (groupTitle != null) radioGroup.setText(groupTitle);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 8;
        layout.numColumns = 1;
        radioGroup.setLayout(layout);

        for (int i = 0; i < radioNames.length; i++) {
            Button radio = new Button(radioGroup, SWT.RADIO | SWT.LEFT);
            radio.setFont(font);
            radio.setData(new Integer(i)); // offset
            radio.setText(radioNames[i]);
            radio.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent event ) {
                    setSelected((Integer)event.widget.getData());
                }
            });
            if (i == defaultRadio) {
                radio.setSelection(true);
            } // endif
        }

        return radioGroup;
    }

    /**
     * @return Returns the selected.
     */
    public int getSelected() {
        return selected.intValue();
    }

    /**
     * Convenience method to open a simple radio question dialog, with a single button.
     * 
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return the index of the selected radio button
     */
    public static int openQuestion( Shell parent,
                                    String title,
                                    String message,
                                    String optionsTitle,
                                    String[] options ) {
        return openMulti(parent, QUESTION, title, message, optionsTitle, options, 0); // first is default
    }

    /**
     * Convenience method to open a simple radio warning dialog, with a single button.
     * 
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return the index of the selected radio button
     */
    public static int openWarning( Shell parent,
                                   String title,
                                   String message,
                                   String optionsTitle,
                                   String[] options ) {
        return openMulti(parent, WARNING, title, message, optionsTitle, options, 0); // first is default
    }

    /**
     * Convenience method to open a simple radio warning dialog, with a single button.
     * 
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param type the type of dialog... use one of the constants: QUESTION, WARNING, INFORMATION, ERROR
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return the index of the selected radio button
     */
    public static int openMulti( Shell parent,
                                 int type,
                                 String title,
                                 String message,
                                 String optionsTitle,
                                 String[] options,
                                 int defaultRadioButton ) {
        RadioMessageDialog dialog = new RadioMessageDialog(parent, title,
                                                           null, // accept the default window icon
                                                           message, type, optionsTitle, options, defaultRadioButton,
                                                           new String[] {IDialogConstants.OK_LABEL}, 0); // yes is the default
        dialog.open();
        return dialog.getSelected();
    }

    /**
     * Convenience method to open a simple radio warning dialog, with a single button.
     * 
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param type the type of dialog... use one of the constants: QUESTION, WARNING, INFORMATION, ERROR
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @param options the allowed radio button options
     * @param errorMessages the problems with each radio button.
     * @return the index of the selected radio button
     */
    public static int openMulti( Shell parent,
                                 int type,
                                 String title,
                                 String message,
                                 String optionsTitle,
                                 String[] options,
                                 IStatus[] errorMessages,
                                 int defaultRadioButton ) {
        RadioMessageDialog dialog = new RadioMessageDialog(parent, title,
                                                           null, // accept the default window icon
                                                           message, type, optionsTitle, options, errorMessages,
                                                           defaultRadioButton, new String[] {IDialogConstants.OK_LABEL}, 0); // OK
                                                                                                                             // is
                                                                                                                             // the
                                                                                                                             // default
        dialog.open();
        return dialog.getSelected();
    }
}
