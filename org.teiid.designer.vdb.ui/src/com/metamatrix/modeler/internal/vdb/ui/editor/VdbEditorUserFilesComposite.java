/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.teiid.designer.vdb.VdbEntry;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants.Widgets;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.widget.TablePanel;

/**
 * The User Files panel.
 * 
 * @since 5.3.3
 */
public final class VdbEditorUserFilesComposite
    implements ListPanel.Constants, StringUtil.Constants, VdbUiConstants, VdbEditor.Constants, Widgets {

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

    /**
     * Method to show file selection dialog, then add the selected file using the vdbEditor context.
     * 
     * @since 5.3.3
     */
    Object[] addFiles() {
        // get file paths from the file dialog.
        final String[] paths = getFileNamesFromDialog();

        // List for the added files
        final List addedFiles = new ArrayList();

        if (paths != null) for (final String path : paths) {
            final VdbEntry addedFile = this.editor.getVdb().addEntry(Path.fromPortableString(path));
            if (addedFile != null) addedFiles.add(addedFile.getName());
        }
        if (!addedFiles.isEmpty()) this.editor.update();
        return addedFiles.toArray();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.3.3
     */
    public Control createPartControl( final Composite parent ) {
        tableProvider = new VdbEditorUserFilesTableProvider(this.editor);

        // pre-filter the model list, showing only PUBLIC models
        final Collection fileList = this.editor.getVdb().getEntries();

        this.filesPanel = new TablePanel(parent, FILES_GROUP, new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addFiles();
            }

            @Override
            public Object[] removeButtonSelected( final IStructuredSelection selection ) {
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
            public void selectionChanged( final SelectionChangedEvent event ) {
                final ISelectionProvider provider = editor.getSite().getSelectionProvider();
                if (event.getSelection() instanceof StructuredSelection) {
                    final StructuredSelection selection = (StructuredSelection)event.getSelection();
                    if (selection.size() == 1) provider.setSelection(event.getSelection());
                    else provider.setSelection(new StructuredSelection());
                } else provider.setSelection(event.getSelection());
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
        final String filterPath = fileSelectDialog.getFilterPath();
        if (filterPath == null || filterPath.trim().length() == 0) fileSelectDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());

        refresh();

        return filesPanel;
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

            final String[] shortNames = fileSelectDialog.getFileNames();
            if (shortNames != null) {
                final String path = fileSelectDialog.getFilterPath();
                final String[] fullNames = new String[shortNames.length];
                for (int i = 0; i < shortNames.length; i++)
                    fullNames[i] = path + File.separator + shortNames[i];
                return fullNames;
            }
        }

        return null;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 5.3.3
     */
    public void gotoMarker( final IMarker marker ) {
    }

    public void layout() {
        this.filesPanel.layout(true);
    }

    void refresh() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (filesPanel.getTableViewer() != null && !filesPanel.getTableViewer().getTable().isDisposed()) filesPanel.getTableViewer().refresh();
            }
        });
    }

    /**
     * @since 5.3.3
     */
    Object[] removeFiles( final IStructuredSelection selection ) {
        final List removedFiles = new ArrayList(selection.size());
        final List selected = selection.toList();
        for (final Iterator i = selected.iterator(); i.hasNext();) {
            final VdbEntry entry = (VdbEntry)i.next();
            this.editor.getVdb().removeEntry(entry);
            removedFiles.add(entry);
        }

        if (!removedFiles.isEmpty()) {
            this.editor.update();
            refresh();
        }
        return removedFiles.toArray();
    }

    public void resetColumnWidths() {
        tableProvider.resetColumnWidths(filesPanel.getTableViewer());
    }

    public void setEnabledState( final boolean isEnabled ) {
        this.filesPanel.setEnabled(isEnabled);
    }
}
