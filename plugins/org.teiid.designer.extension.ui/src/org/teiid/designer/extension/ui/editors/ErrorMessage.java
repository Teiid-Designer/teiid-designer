/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.extension.definition.MedStatus;
import org.teiid.designer.extension.definition.ValidationStatus;


/**
 * An error message used in the {@link ModelExtensionDefinitionEditor}.
 */
final class ErrorMessage implements IMessage {
    
    private static int getMessageType(final MedStatus medStatus) {
        if (medStatus.isError()) {
            return IMessageProvider.ERROR;
        }
        
        if (medStatus.isWarning()) {
            return IMessageProvider.WARNING;
        }
        
        if (medStatus.isInfo()) {
            return IMessageProvider.INFORMATION;
        }

        return IMessageProvider.NONE;
    }

    private MedStatus status;

    /**
     * The UI control where the error can be fixed.
     */
    private Control widget;

    /**
     * Clears the error message.
     */
    public void clearMessage() {
        setMessage(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getControl()
     */
    @Override
    public Control getControl() {
        return this.widget;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getData()
     */
    @Override
    public Object getData() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getKey()
     */
    @Override
    public Object getKey() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IMessageProvider#getMessage()
     */
    @Override
    public String getMessage() {
        return this.status.getMessage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IMessageProvider#getMessageType()
     */
    @Override
    public int getMessageType() {
        return getMessageType(this.status);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getPrefix()
     */
    @Override
    public String getPrefix() {
        return null;
    }

    /**
     * @return <code>true</code> if the validation status has an error severity
     */
    public boolean isError() {
        return this.status.isError();
    }

    /**
     * @return <code>true</code> if the validation status has an information severity
     */
    public boolean isInfo() {
        return this.status.isInfo();
    }

    /**
     * @return <code>true</code> if the validation status has an OK severity
     */
    public boolean isOk() {
        return this.status.isOk();
    }

    /**
     * @return <code>true</code> if the validation status has a warning severity
     */
    public boolean isWarning() {
        return this.status.isWarning();
    }

    /**
     * @param newControl the new control (can be <code>null</code>)
     */
    public void setControl( Control newControl ) {
        this.widget = newControl;
    }

    /**
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setMessage( String newMessage ) {
        setStatus(CoreStringUtil.isEmpty(newMessage) ? ValidationStatus.OK_STATUS : ValidationStatus.createErrorMessage(newMessage));
    }

    public void setStatus( final MedStatus status ) {
        this.status = status;
    }

    /**
     * @param msgMgr the message manager where the message should be updated (cannot be <code>null</code>)
     */
    public void update(final IMessageManager msgMgr) {
        msgMgr.removeMessages(getControl());

        if (!this.status.isOk()) {
            if (this.status.isMulti()) {
                int i = 0;

                for (final MedStatus medStatus : this.status.getChildren()) {
                    msgMgr.addMessage(i++, medStatus.getMessage(), null, getMessageType(medStatus), getControl());
                }
            } else {
                msgMgr.addMessage(getKey(), getMessage(), null, getMessageType(), getControl());
            }
        }
    }

}
