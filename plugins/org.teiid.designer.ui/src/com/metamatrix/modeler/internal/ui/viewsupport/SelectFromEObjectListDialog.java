/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.Collection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @since 5.0
 */
public class SelectFromEObjectListDialog extends ElementListSelectionDialog {

    private String theTitle;

    /**
     * This simple dialog provides a way to query users to select Objects/elements from a list of pre-determined objects.
     * @since 5.0
     */
    public SelectFromEObjectListDialog(Shell parent,
                                       Collection elements,
                                       boolean allowMultiple,
                                       String dialogTitle,
                                       String initialMessage) {
        super(parent, new SelectModelObjectLabelProvider());
        this.theTitle = dialogTitle;
        setElements(elements.toArray());
        setMultipleSelection(allowMultiple);
        setMessage(initialMessage);
    }

    public SelectFromEObjectListDialog(Shell parent,
                                       Collection elements,
                                       boolean allowMultiple,
                                       String dialogTitle,
                                       String initialMessage,
                                       LabelProvider labelProvider) {
        super(parent, labelProvider);
        this.theTitle = dialogTitle;
        setElements(elements.toArray());
        setMultipleSelection(allowMultiple);
        setMessage(initialMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.ElementListSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control control = super.createDialogArea(parent);
        getShell().setText(this.theTitle);
        return control;
    }
}
