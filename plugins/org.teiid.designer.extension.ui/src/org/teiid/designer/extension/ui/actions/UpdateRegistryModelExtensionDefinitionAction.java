/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionText;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionToolTip;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.teiid.designer.extension.ui.Activator;

/**
 * 
 */
public final class UpdateRegistryModelExtensionDefinitionAction extends Action {

    public UpdateRegistryModelExtensionDefinitionAction() {
        super(updateMedInRegistryActionText, SWT.FLAT);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
        setToolTipText(updateMedInRegistryActionToolTip);
    }
}
