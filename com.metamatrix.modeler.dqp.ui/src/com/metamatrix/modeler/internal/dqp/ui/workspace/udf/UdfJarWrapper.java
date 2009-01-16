/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import java.io.File;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.AddExistingUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.DeleteUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ImportUdfJarsAction;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * @since 5.0
 */
public class UdfJarWrapper implements IExtendedModelObject {
    private static IAction addJarsAction;
    private static IAction importJarsAction;
    private static IAction deleteJarsAction;

    private File jarFile;

    /**
     * @since 5.0
     */
    public UdfJarWrapper( File theJar ) {
        super();
        jarFile = theJar;
    }

    /**
     * @return
     * @since 5.0
     */
    public String getLabel() {
        return jarFile.getName();
    }

    /**
     * @return
     * @since 5.0
     */
    public File getJarFile() {
        return this.jarFile;
    }

    public IPropertySource getPropertySource() {
        return null;
    }

    public String getStatusLabel() {
        return DqpUiConstants.UTIL.getString("UdfJarWrapper.statusLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject#overrideContextMenu()
     * @since 5.0
     */
    public boolean overrideContextMenu() {
        return true;
    }

    private IAction getDeleteAction() {
        if (deleteJarsAction == null) {
            deleteJarsAction = UdfModelUtil.getDeleteUdfJarsAction();
        }
        return deleteJarsAction;
    }

    private IAction getImportAction() {
        if (importJarsAction == null) {
            importJarsAction = UdfModelUtil.getImportUdfJarsAction();
        }
        return importJarsAction;
    }

    private IAction getAddAction() {
        if (addJarsAction == null) {
            addJarsAction = UdfModelUtil.getAddUdfJarsAction();
        }
        return addJarsAction;
    }

    /**
     * @see com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     * @since 5.0
     */
    public void fillContextMenu( IMenuManager theMenu ) {
        // TODO: Add DELETE & IMPORT JARS actions
        if (theMenu.find(DeleteUdfJarsAction.ACTION_ID) == null) {
            theMenu.add(getDeleteAction());
        }

        theMenu.add(new Separator());

        if (theMenu.find(ImportUdfJarsAction.ACTION_ID) == null) {
            theMenu.add(getImportAction());
        }
        if (theMenu.find(AddExistingUdfJarsAction.ACTION_ID) == null) {
            theMenu.add(getAddAction());
        }
    }
}
