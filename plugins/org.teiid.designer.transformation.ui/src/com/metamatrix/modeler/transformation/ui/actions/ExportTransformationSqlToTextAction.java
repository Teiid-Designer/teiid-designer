/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.util.SqlUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public class ExportTransformationSqlToTextAction extends SortableSelectionAction implements UiConstants {

    private static final String EXPORT_PROBLEM = "ExportTransformationSqlToTextAction.exportProb"; //$NON-NLS-1$
    private static final String EXPORT_SQL_DIALOG_TITLE = "ExportTransformationSqlToTextAction.exportSqlDialog.title"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILENAME = "ExportTransformationSqlToTextAction.exportDefaultFile.text"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILEEXT = "ExportTransformationSqlToTextAction.exportDefaultExtension.text"; //$NON-NLS-1$

    private static final char DELIMETER = '|';

    /**
     * @since 5.0
     */
    public ExportTransformationSqlToTextAction() {
        super();
    }

    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return virtualModelSelected(selection);
    }

    @Override
    public void run() {
        ISelection cachedSelection = getSelection();
        if (cachedSelection != null && !cachedSelection.isEmpty()) {
            Object selectedObj = SelectionUtilities.getSelectedObject(cachedSelection);
            if (selectedObj != null && selectedObj instanceof IFile) {
                ModelResource modelResource = null;
                try {
                    modelResource = ModelUtil.getModelResource(((IFile)selectedObj), false);
                    if (modelResource != null) {
                        String fileName = askUserForOutputFilename();
                        if (fileName != null) {
                            String fileOuputString = getSqlOutputString(modelResource);
                            exportSqlToFile(fileName, UiConstants.Util.getString(EXPORT_DEFAULT_FILEEXT), fileOuputString);
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(e);
                }
            }

        }
        selectionChanged(null, new StructuredSelection());
    }

    @Override
    public boolean isApplicable( ISelection selection ) {
        return virtualModelSelected(selection);
    }

    private boolean virtualModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalViewModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Export the current string content of the sql display to a user-selected file
     */
    public File exportSqlToFile( String fileNameString,
                                 String fileExtension,
                                 String fileOuputString ) {

        // PERFORM ARG CHECK
        // Look for NULL or EMPTY strings
        CoreArgCheck.isNotNull(fileNameString);
        CoreArgCheck.isNotNull(fileExtension);
        CoreArgCheck.isNotNull(fileOuputString);

        CoreArgCheck.isNotEmpty(fileNameString);
        CoreArgCheck.isNotEmpty(fileExtension);
        CoreArgCheck.isNotEmpty(fileOuputString);

        // If there is no file extension, add .sql
        if (fileNameString.indexOf('.') == -1 && fileExtension != null) {
            fileNameString = fileNameString + "." + fileExtension; //$NON-NLS-1$
        }

        FileWriter fileWriter = null;
        BufferedWriter outputBufferWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter(fileNameString);
            outputBufferWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(outputBufferWriter);
            printWriter.write(fileOuputString);
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, UiConstants.Util.getString(EXPORT_PROBLEM));
        }

        finally {
            // Clean up writers & buffers
            if (printWriter != null) {
                printWriter.close();
            }

            try {
                if (outputBufferWriter != null) {
                    outputBufferWriter.close();
                }
            } catch (java.io.IOException e) {
            }

            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (java.io.IOException e) {
            }
        }
        return new File(fileNameString);
    }

    public String askUserForOutputFilename() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.txt", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ 
        dlg.setText(UiConstants.Util.getString(EXPORT_SQL_DIALOG_TITLE));
        dlg.setFileName(UiConstants.Util.getString(EXPORT_DEFAULT_FILENAME));

        return dlg.open();
    }

    public String getSqlOutputString( ModelResource modelResource ) throws ModelWorkspaceException {
        List transformations = modelResource.getModelTransformations().getTransformations();
        int nTransforms = transformations.size();
        int initBufferSize = nTransforms * 200;
        StringBuffer sb = new StringBuffer(initBufferSize);
        String relativeTablePath = null;
        Collection invalidQueries = new ArrayList();
        for (Iterator iter = transformations.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            String rowString = null;
            if (obj instanceof SqlTransformationMappingRoot) {
                EObject table = ((SqlTransformationMappingRoot)obj).getTarget();
                relativeTablePath = ModelerCore.getModelEditor().getModelRelativePath(table).toString();

                String userString = TransformationHelper.getSelectSqlString(obj);

                if (userString != null && relativeTablePath != null) {
                    rowString = createRowForFile(QueryValidator.SELECT_TRNS, relativeTablePath, userString);
                    if (rowString != null && rowString.length() > 0) {
                        sb.append(rowString);
                    }
                } else {
                    invalidQueries.add(relativeTablePath);
                }

                // Now check if updates allowed
                boolean tableSupportsUpdates = TransformationHelper.tableSupportsUpdate(table);
                if (tableSupportsUpdates) {
                    if (TransformationHelper.supportsInsert((EObject)obj, null)) {
                        userString = TransformationHelper.getInsertSqlString(obj);

                        if (userString != null && relativeTablePath != null) {
                            rowString = createRowForFile(QueryValidator.INSERT_TRNS, relativeTablePath, userString);
                            if (rowString != null && rowString.length() > 0) {
                                sb.append(rowString);
                            }
                        } else {
                            invalidQueries.add(relativeTablePath);
                        }
                    }
                    if (TransformationHelper.supportsUpdate((EObject)obj, null)) {
                        userString = TransformationHelper.getUpdateSqlString(obj);

                        if (userString != null && relativeTablePath != null) {
                            rowString = createRowForFile(QueryValidator.UPDATE_TRNS, relativeTablePath, userString);
                            if (rowString != null && rowString.length() > 0) {
                                sb.append(rowString);
                            }
                        } else {
                            invalidQueries.add(relativeTablePath);
                        }
                    }
                    if (TransformationHelper.supportsDelete((EObject)obj, null)) {
                        userString = TransformationHelper.getDeleteSqlString(obj);

                        if (userString != null && relativeTablePath != null) {
                            rowString = createRowForFile(QueryValidator.DELETE_TRNS, relativeTablePath, userString);
                            if (rowString != null && rowString.length() > 0) {
                                sb.append(rowString);
                            }
                        } else {
                            invalidQueries.add(relativeTablePath);
                        }
                    }
                }
            }
        }

        if (!invalidQueries.isEmpty()) {
            UiConstants.Util.log(IStatus.ERROR,
                                 UiConstants.Util.getString("ExportTransformationSqlToTextAction.exportQueryProblem", modelResource.getItemName()));//$NON-NLS-1$ 
        }

        return sb.toString();
    }

    private String createRowForFile( int sqlType,
                                     String relativeTablePath,
                                     String theSql ) {
        StringBuffer sb = new StringBuffer(relativeTablePath.length() + theSql.length() + 20);
        theSql = theSql.replaceAll("\\\\", Matcher.quoteReplacement("\\\\")); //$NON-NLS-1$ //$NON-NLS-2$
        theSql = theSql.replaceAll("\\n", Matcher.quoteReplacement("\\n")); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(relativeTablePath).append(DELIMETER).append(getSqlTypeString(sqlType)).append(DELIMETER).append(theSql).append(SqlUtil.CR_CHAR);
        return sb.toString();
    }

    private String getSqlTypeString( int sqlType ) {
        switch (sqlType) {
            case QueryValidator.SELECT_TRNS:
                return SqlConstants.SQL_TYPE_SELECT_STRING;
            case QueryValidator.INSERT_TRNS:
                return SqlConstants.SQL_TYPE_INSERT_STRING;
            case QueryValidator.UPDATE_TRNS:
                return SqlConstants.SQL_TYPE_UPDATE_STRING;
            case QueryValidator.DELETE_TRNS:
                return SqlConstants.SQL_TYPE_DELETE_STRING;
        }
        return SqlConstants.SQL_TYPE_UNKNOWN_STRING;
    }
}
