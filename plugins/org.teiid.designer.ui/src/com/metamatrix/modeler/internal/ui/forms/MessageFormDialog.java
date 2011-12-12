/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import static com.metamatrix.modeler.ui.UiConstants.Util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;

/**
 * A form-based message dialog.
 */
public class MessageFormDialog extends FormDialog {

    private enum Kind {
        ERROR, QUESTION, INFORMATION, WARNING
    }

    private static String getMessageTitle( Kind kind ) {
        if (Kind.ERROR == kind) {
            return Util.getString(I18nUtil.getPropertyPrefix(MessageFormDialog.class) + "errorTitle"); //$NON-NLS-1$
        }

        if (Kind.INFORMATION == kind) {
            return Util.getString(I18nUtil.getPropertyPrefix(MessageFormDialog.class) + "infoTitle"); //$NON-NLS-1$
        }

        if (Kind.QUESTION == kind) {
            return Util.getString(I18nUtil.getPropertyPrefix(MessageFormDialog.class) + "questionTitle"); //$NON-NLS-1$
        }

        if (Kind.WARNING == kind) {
            return Util.getString(I18nUtil.getPropertyPrefix(MessageFormDialog.class) + "warningTitle"); //$NON-NLS-1$
        }

        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    private static int getMessageType( Kind kind ) {
        if (Kind.ERROR == kind) {
            return IMessageProvider.ERROR;
        }

        if ((Kind.INFORMATION == kind) || (Kind.QUESTION == kind)) {
            return IMessageProvider.INFORMATION;
        }

        if (Kind.WARNING == kind) {
            return IMessageProvider.WARNING;
        }

        return IMessageProvider.NONE;
    }

    public static boolean openError( Shell parent,
                                     String msgTitle,
                                     Image titleImage,
                                     String message ) {
        return open(Kind.ERROR, parent, msgTitle, titleImage, message);
    }

    public static boolean openInfo( Shell parent,
                                    String msgTitle,
                                    Image titleImage,
                                    String message ) {
        return open(Kind.INFORMATION, parent, msgTitle, titleImage, message);
    }

    public static boolean openQuestion( Shell parent,
                                        String msgTitle,
                                        Image titleImage,
                                        String message ) {
        return open(Kind.QUESTION, parent, msgTitle, titleImage, message);
    }

    public static boolean openWarning( Shell parent,
                                       String msgTitle,
                                       Image titleImage,
                                       String message ) {
        return open(Kind.WARNING, parent, msgTitle, titleImage, message);
    }

    private static boolean open( Kind kind,
                                 Shell parent,
                                 String msgTitle,
                                 Image titleImage,
                                 String message ) {
        FormDialog dialog = new MessageFormDialog(kind, parent, msgTitle, titleImage, message);
        dialog.create();
        dialog.getShell().pack();
        return (dialog.open() == Window.OK);
    }

    private final Image titleImage;
    private final String message;
    private final String msgTitle;
    private final Kind kind;

    private MessageFormDialog( Kind kind,
                               Shell parent,
                               String msgTitle,
                               Image titleImage,
                               String message ) {
        super(parent);
        this.kind = kind;
        this.msgTitle = msgTitle;
        this.titleImage = titleImage;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);

        newShell.setText(getMessageTitle(this.kind));

        if (this.titleImage != null) {
            newShell.setImage(this.titleImage);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        if (this.kind == Kind.QUESTION) {
            if (Window.OK == id) {
                label = IDialogConstants.YES_LABEL;
            } else if (Window.CANCEL == id) {
                label = IDialogConstants.NO_LABEL;
            }
        }

        return super.createButton(parent, id, label, defaultButton);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

        if (this.kind == Kind.QUESTION) {
            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.setText(this.msgTitle);
        scrolledForm.setMessage(CoreStringUtil.Constants.EMPTY_STRING, getMessageType(this.kind));

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = scrolledForm.getBody();
        body.setLayout(new TableWrapLayout());
        toolkit.createLabel(body, this.message, SWT.WRAP);
    }
}