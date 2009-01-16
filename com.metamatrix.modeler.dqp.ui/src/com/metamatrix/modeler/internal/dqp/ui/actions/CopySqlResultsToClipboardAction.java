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
package com.metamatrix.modeler.internal.dqp.ui.actions;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;

/**
 * @since 5.5.3
 */
public class CopySqlResultsToClipboardAction extends Action implements DqpUiConstants {

    final private IResultsProvider provider;

    final private ViewPart view;

    /**
     * @since 5.5.3
     */
    public CopySqlResultsToClipboardAction( IResultsProvider provider,
                                            ViewPart view ) {
        super(
              UTIL.getString(I18nUtil.getPropertyPrefix(CopySqlResultsToClipboardAction.class) + "copyAction"), IAction.AS_PUSH_BUTTON); //$NON-NLS-1$

        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setToolTipText(UTIL.getString(I18nUtil.getPropertyPrefix(CopySqlResultsToClipboardAction.class) + "copyAction.tip")); //$NON-NLS-1$
        setEnabled(false);

        this.provider = provider;
        this.view = view;
    }

    IResultsProvider accessProvider() {
        return this.provider;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.5.3
     */
    @Override
    public void run() {
        Runnable copyOperation = new Runnable() {

            public void run() {
                IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
                String columnDelim = store.getString(IConstants.CLIP_EXPORT_SEPARATOR);
                String lineDelim = System.getProperty("line.separator"); //$NON-NLS-1$
                SqlResultsModel model = (SqlResultsModel)accessProvider().getResults();
                Object[] rows = model.getRows();
                StringBuffer buf = new StringBuffer();

                for (int rowIndex = 0; rowIndex < rows.length; ++rowIndex) {
                    Object[] row = (Object[])rows[rowIndex];

                    for (int colIndex = 0; colIndex < row.length; ++colIndex) {
                        if (colIndex != 0) {
                            buf.append(columnDelim);
                        }

                        buf.append(row[colIndex]);
                    }

                    buf.append(lineDelim);
                }

                // only copy if there is something to copy
                if (buf.toString().length() != 0) {
                    SystemClipboardUtilities.setContents(buf.toString());
                }
            }
        };

        // show busy cursor while copying
        BusyIndicator.showWhile(this.view.getSite().getShell().getDisplay(), copyOperation);
    }
}
