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
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.udf.AddExistingUdfJarsDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class AddExistingUdfJarsAction extends Action implements DqpUiConstants {
    public static final String ACTION_ID = "AddExistingUdfJarsAction"; //$NON-NLS-1$

    private final static String PREFIX = I18nUtil.getPropertyPrefix(AddExistingUdfJarsAction.class);

    /**
     * @since 5.5.3
     */
    public AddExistingUdfJarsAction() {
        setText(UTIL.getString(PREFIX + "label")); //$NON-NLS-1$
        setToolTipText(UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.ADD_UDF_JAR_ICON));
        setId(ACTION_ID);
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.5.3
     */
    @Override
    public void run() {
        Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();

        AddExistingUdfJarsDialog dialog = new AddExistingUdfJarsDialog(shell);

        int result = dialog.open();

        if (result == Window.OK) {
            ISelection theSelection = dialog.getSelection();
            List selObjs = SelectionUtilities.getSelectedObjects(theSelection);
            List<File> jarFiles = new ArrayList<File>(selObjs.size());
            for (Object obj : selObjs) {
                if (obj instanceof File) {
                    jarFiles.add((File)obj);
                }
            }

            File[] jFiles = jarFiles.toArray(new File[jarFiles.size()]);

            DqpPlugin.getInstance().getExtensionsHandler().addUdfJars(this, jFiles);
        }
    }
}
