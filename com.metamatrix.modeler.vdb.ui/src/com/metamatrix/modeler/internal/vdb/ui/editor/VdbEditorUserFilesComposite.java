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
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.UriUtil;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants.Widgets;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.widget.TablePanel;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * The User Files panel.
 * 
 * @since 5.3.3
 */
public final class VdbEditorUserFilesComposite
    implements ListPanel.Constants, StringUtil.Constants, VdbUiConstants, UriUtil.Constants, VdbEditor.Constants, Widgets {

    private static final String I18N_PREFIX = "VdbEditorUserFilesPage."; //$NON-NLS-1$
    private static final String FILE_SELECTION_DIALOG_TITLE = getString("fileSelectionDialogTitle"); //$NON-NLS-1$
    private static final String FILES_GROUP = getString("filesGroup"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;
    TablePanel filesPanel;
    private FileDialog fileSelectDialog;
    private VdbEditorUserFilesTableProvider tableProvider;

    /**
     * @since 5.3.3
     */
    VdbEditorUserFilesComposite( final VdbEditor editor ) {
        this.editor = editor;
    }

    public void resetColumnWidths() {
        tableProvider.resetColumnWidths(filesPanel.getTableViewer());
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.3.3
     */
    public Control createPartControl( final Composite parent ) {
        tableProvider = new VdbEditorUserFilesTableProvider(this.editor);

        // pre-filter the model list, showing only PUBLIC models
        Collection fileList = this.editor.getContext().getUserFileNames();

        this.filesPanel = new TablePanel(parent, FILES_GROUP, new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addFiles();
            }

            @Override
            public Object[] removeButtonSelected( IStructuredSelection selection ) {
                return removeFiles(selection);
            }

        }, SWT.MULTI | SWT.FULL_SELECTION, NONE);

        this.filesPanel.getTableViewer().setLabelProvider(tableProvider);
        this.filesPanel.getTableViewer().setContentProvider(tableProvider);
        this.filesPanel.deleteMessageLabel(); // delete the message label

        this.filesPanel.getTableViewer().getTable().setHeaderVisible(true);
        this.filesPanel.getTableViewer().getTable().setLinesVisible(false);
        tableProvider.buildTableColumns(filesPanel.getTableViewer());
        this.filesPanel.getTableViewer().setInput(fileList);
        this.filesPanel.resetSelectionListener();
        this.filesPanel.getTableViewer().setSorter(new ViewerSorter() {});

        this.filesPanel.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                // ---------------------------------------------------------------
                // Defect 22305 required checking if context is really open or not.
                // This prevents a possible IllegalStateException
                // ---------------------------------------------------------------
                if (editor.isVdbContextOpen()) {
                    ISelectionProvider provider = editor.getSite().getSelectionProvider();
                    if (event.getSelection() instanceof StructuredSelection) {
                        StructuredSelection selection = (StructuredSelection)event.getSelection();
                        if (selection.size() == 1) provider.setSelection(event.getSelection());
                        else provider.setSelection(new StructuredSelection());
                    } else {
                        provider.setSelection(event.getSelection());
                    }
                }
            }
        });

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 140;
        gd.minimumHeight = 140;
        this.filesPanel.getTableViewer().getTable().setLayoutData(gd);
        gd = new GridData(GridData.FILL_BOTH);
        gd.minimumHeight = 180;
        filesPanel.setLayoutData(gd);

        // Create file selection dialog
        fileSelectDialog = new FileDialog(this.filesPanel.getShell(), SWT.OPEN | SWT.PRIMARY_MODAL | SWT.MULTI);

        // Init filterPath to workspace root on init
        fileSelectDialog.setText(FILE_SELECTION_DIALOG_TITLE);
        String filterPath = fileSelectDialog.getFilterPath();
        if (filterPath == null || filterPath.trim().length() == 0) {
            fileSelectDialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
        }

        refresh();

        return filesPanel;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 5.3.3
     */
    public void gotoMarker( final IMarker marker ) {
    }

    public void setEnabledState( boolean isEnabled ) {
        this.filesPanel.setEnabled(isEnabled);
    }

    public void layout() {
        this.filesPanel.layout(true);
    }

    /**
     * Method to show file selection dialog, then add the selected file using the vdbEditor context.
     * 
     * @since 5.3.3
     */
    Object[] addFiles() {
        // get file paths from the file dialog.
        String[] paths = getFileNamesFromDialog();

        // List for the added files
        List addedFiles = new ArrayList();

        if (paths != null) {
            try {
                for (int i = 0; i < paths.length; i++) {
                    File fileToAdd = new File(paths[i]);
                    File addedFile = this.editor.getContext().addUserFile(fileToAdd);
                    if (addedFile != null) {
                        addedFiles.add(addedFile.getName());
                    }
                }
            } catch (VdbEditException e) {
                WidgetUtil.showError(e.getMessage());
                VdbUiConstants.Util.log(e);
            }
        }
        if (!addedFiles.isEmpty()) {
            this.editor.setModified();
        }
        return addedFiles.toArray();
    }

    /**
     * Method to show the file selection dialog
     * 
     * @return file name and path
     * @since 5.3.3
     */
    private String[] getFileNamesFromDialog() {
        if (this.fileSelectDialog != null) {
            fileSelectDialog.open();

            String[] shortNames = fileSelectDialog.getFileNames();
            if (shortNames != null) {
                String path = fileSelectDialog.getFilterPath();
                String[] fullNames = new String[shortNames.length];
                for (int i = 0; i < shortNames.length; i++) {
                    fullNames[i] = path + File.separator + shortNames[i];
                }
                return fullNames;
            }
        }

        return null;
    }

    /**
     * @since 5.3.3
     */
    Object[] removeFiles( final IStructuredSelection selection ) {
        final List removedFiles = new ArrayList(selection.size());
        VdbEditingContext context = editor.getContext();
        Assertion.isInstanceOf(context, InternalVdbEditingContext.class, "VdbEditingContext"); //$NON-NLS-1$

        List selected = selection.toList();
        for (Iterator i = selected.iterator(); i.hasNext();) {
            String name = (String)i.next();
            try {
                this.editor.getContext().removeUserFileWithName(name);
                removedFiles.add(name);
            } catch (VdbEditException e) {
                WidgetUtil.showError(e.getMessage());
                VdbUiConstants.Util.log(e);
            }
        }

        if (!removedFiles.isEmpty()) {
            this.editor.setModified();
            refresh();
        }
        return removedFiles.toArray();
    }

    void refresh() {
        if (!editor.isVdbContextOpen()) return;

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (editor.isVdbContextOpen() && filesPanel.getTableViewer() != null
                    && !filesPanel.getTableViewer().getTable().isDisposed()) {

                    filesPanel.getTableViewer().refresh();
                }
            }
        });
    }
}
