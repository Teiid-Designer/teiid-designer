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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.XmlDocumentResultsModel;
import com.metamatrix.modeler.internal.ui.actions.workers.ExportTextToFileWorker;

/**
 * @since 5.5.3
 */
public class SaveXmlResultsToFileAction extends Action implements
                                                      DqpUiConstants {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private IResultsProvider resultsProvider;

    private String fileName;

    private boolean success; // indicates if the last run was successful

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 5.5.3
     */
    public SaveXmlResultsToFileAction(IResultsProvider resultsProvider) {
        super(UTIL.getString(I18nUtil.getPropertyPrefix(SaveXmlResultsToFileAction.class) + "saveToFileAction"), IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SAVE_TO_FILE_ICON));
        setToolTipText(UTIL.getString(I18nUtil.getPropertyPrefix(SaveXmlResultsToFileAction.class) + "saveToFileAction.tip")); //$NON-NLS-1$
        setEnabled(true);

        this.resultsProvider = resultsProvider;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private String createHeader(XmlDocumentResultsModel model) {
        String prefix = I18nUtil.getPropertyPrefix(SaveXmlResultsToFileAction.class);
        DateFormat formatter = new SimpleDateFormat(UTIL.getString(prefix + "header.datePattern")); //$NON-NLS-1$
        String date = formatter.format(new Date(System.currentTimeMillis()));

        StringBuffer buf = new StringBuffer(model.getSql().length() * 2);
        buf.append(UTIL.getString(prefix + "header.line_1")); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_2") + date + StringUtil.Constants.LINE_FEED_CHAR); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_3")); //$NON-NLS-1$
        buf.append(model.getSql()).append(StringUtil.Constants.LINE_FEED_CHAR);
        buf.append(UTIL.getString(prefix + "header.line_4")); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_5")); //$NON-NLS-1$

        return buf.toString();
    }

    /**
     * @return the name of the file where the last successful save occurred or <code>null</code>
     * @since 5.5.3
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.5
     */
    @Override
    public void run() {
        this.fileName = null;
        this.success = false;
        XmlDocumentResultsModel model = (XmlDocumentResultsModel)this.resultsProvider.getResults();

        if (model == null) {
            setEnabled(false);
        } else {
            String prefix = I18nUtil.getPropertyPrefix(SaveXmlResultsToFileAction.class);
            
            // The ExportTextToFileWorker inserts the header at the beginning of the file. Since an XML file has a standard
            // first line identifying it as and XML the header must go after this first line. So we ask the model to do this
            // and pass in a null header to the exporter.
            model.setHeaderComment(createHeader(model));
            ExportTextToFileWorker expWorker = new ExportTextToFileWorker(UTIL.getString(prefix + "exportFileWorker.title"), //$NON-NLS-1$
                                                                          UTIL.getString(prefix
                                                                                         + "exportFileWorker.defaultFileName"), //$NON-NLS-1$
                                                                          UTIL.getString(prefix
                                                                                         + "exportFileWorker.defaultExtension"), //$NON-NLS-1$
                                                                          null, model.getResultsAsText());
            this.fileName = expWorker.getFileName();
            this.success = !expWorker.export();
        }
    }

    /**
     * @return <code>true</code> if save was successful
     * @since 5.5.3
     */
    public boolean wasSaveSuccessful() {
        return this.success;
    }
}
