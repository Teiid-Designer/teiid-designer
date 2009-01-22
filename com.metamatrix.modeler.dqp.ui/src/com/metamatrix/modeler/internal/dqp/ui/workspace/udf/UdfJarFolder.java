/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.AddExistingUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ImportUdfJarsAction;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * @since 5.0
 */
public class UdfJarFolder implements IExtendedModelObject {
    private final static IAction importJarsAction = new ImportUdfJarsAction();
    private final static IAction addJarsAction = new AddExistingUdfJarsAction();

    private ModelResource udfResource;
    private static final String FOLDER_LABEL = DqpUiConstants.UTIL.getString("UdfJarFolder.folderLabel"); //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public UdfJarFolder( ModelResource theResource ) {
        super();
        udfResource = theResource;
    }

    public ModelResource getUdfResource() {
        return udfResource;
    }

    public String getLabel() {
        return FOLDER_LABEL;
    }

    public IPropertySource getPropertySource() {
        return null;
    }

    public String getStatusLabel() {
        return DqpUiConstants.UTIL.getString("UdfJarFolder.statusLabel"); //$NON-NLS-1$
    }

    public boolean overrideContextMenu() {
        return true;
    }

    public void fillContextMenu( IMenuManager theMenu ) {
        // TODO: Add DELETE & IMPORT JARS actions
        theMenu.add(importJarsAction);
        theMenu.add(addJarsAction);
    }
}
