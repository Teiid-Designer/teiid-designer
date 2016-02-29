/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.query.ui.UiPlugin;


/**
 * @since 8.0
 */
public class ExportTransformationSqlToTextAction extends SortableSelectionAction implements UiConstants {

    private static final String EXPORT_PROBLEM = "ExportTransformationSqlToTextAction.exportProb"; //$NON-NLS-1$
    private static final String EXPORT_SQL_DIALOG_TITLE = "ExportTransformationSqlToTextAction.exportSqlDialog.title"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILENAME = "ExportTransformationSqlToTextAction.exportDefaultFile.text"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILEEXT = "ExportTransformationSqlToTextAction.exportDefaultExtension.text"; //$NON-NLS-1$

    private static final String DELIMETER = "|"; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String LEFT_BRACKET = "[ "; //$NON-NLS-1$
    private static final String RIGHT_BRACKET = " ]"; //$NON-NLS-1$

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
        boolean hasMissingSelects = false;
        boolean hasMissingInserts = false;
        boolean hasMissingUpdates = false;
        boolean hasMissingDeletes = false;
        
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
                	hasMissingSelects = true;
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
                        	hasMissingInserts = true;
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
                            hasMissingUpdates = true;
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
                            hasMissingDeletes = true;
                        }
                    }
                }
            }
        }

        // If operations are missing, specify in the error message
        if(hasMissingSelects || hasMissingInserts || hasMissingUpdates || hasMissingDeletes) {
        	int count = 0;
        	StringBuffer msgBuff = new StringBuffer(UiConstants.Util.getString("ExportTransformationSqlToTextAction.exportQueryProblem", modelResource.getItemName())); //$NON-NLS-1$
        	msgBuff.append(SPACE+LEFT_BRACKET); 
        	if(hasMissingSelects) {
        		msgBuff.append(ISQLConstants.SQL_TYPE_SELECT_STRING);
        		count++;
        	}
        	if(hasMissingInserts) {
        		if(count>0) msgBuff.append(SPACE+DELIMETER+SPACE);
        		msgBuff.append(ISQLConstants.SQL_TYPE_INSERT_STRING);
        		count++;
        	}
        	if(hasMissingUpdates) {
        		if(count>0) msgBuff.append(SPACE+DELIMETER+SPACE);
        		msgBuff.append(ISQLConstants.SQL_TYPE_UPDATE_STRING);
        		count++;
        	}
        	if(hasMissingDeletes) {
        		if(count>0) msgBuff.append(SPACE+DELIMETER+SPACE);
        		msgBuff.append(ISQLConstants.SQL_TYPE_DELETE_STRING);
        		count++;
        	}
        	msgBuff.append(RIGHT_BRACKET);
        	UiConstants.Util.log(IStatus.ERROR,msgBuff.toString());
        }

        return sb.toString();
    }

    private String createRowForFile( int sqlType,
                                     String relativeTablePath,
                                     String theSql ) {
        StringBuffer sb = new StringBuffer(relativeTablePath.length() + theSql.length() + 20);
        theSql = theSql.replaceAll("\\\\", Matcher.quoteReplacement("\\\\")); //$NON-NLS-1$ //$NON-NLS-2$
        theSql = theSql.replaceAll("\\n", Matcher.quoteReplacement("\\n")); //$NON-NLS-1$ //$NON-NLS-2$
        theSql = theSql.replaceAll("\\r", Matcher.quoteReplacement("\\r")); //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(relativeTablePath).append(DELIMETER).append(getSqlTypeString(sqlType)).append(DELIMETER).append(theSql).append(CoreStringUtil.Constants.CARRIAGE_RETURN_CHAR);
        return sb.toString();
    }

    private String getSqlTypeString( int sqlType ) {
        switch (sqlType) {
            case QueryValidator.SELECT_TRNS:
                return ISQLConstants.SQL_TYPE_SELECT_STRING;
            case QueryValidator.INSERT_TRNS:
                return ISQLConstants.SQL_TYPE_INSERT_STRING;
            case QueryValidator.UPDATE_TRNS:
                return ISQLConstants.SQL_TYPE_UPDATE_STRING;
            case QueryValidator.DELETE_TRNS:
                return ISQLConstants.SQL_TYPE_DELETE_STRING;
        }
        return ISQLConstants.SQL_TYPE_UNKNOWN_STRING;
    }
}
