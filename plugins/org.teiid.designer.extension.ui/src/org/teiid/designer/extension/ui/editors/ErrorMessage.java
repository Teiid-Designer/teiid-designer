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

import com.metamatrix.core.util.CoreStringUtil;

/**
 * An error message used in the {@link ModelExtensionDefinitionEditor}.
 */
final class ErrorMessage implements IMessage {

    /**
     * The error message (can be <code>null</code> or empty)
     */
    String message;

    /**
     * The UI control where the error can be fixed.
     */
    Control widget;

    /**
     * Clears the error message.
     */
    void clearMessage() {
        this.message = null;
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
        return this.message;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.IMessageProvider#getMessageType()
     */
    @Override
    public int getMessageType() {
        return (CoreStringUtil.isEmpty(message) ? IMessageProvider.NONE : IMessageProvider.ERROR);
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

}
