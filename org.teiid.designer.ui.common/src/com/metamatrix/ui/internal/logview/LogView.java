/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.logview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.PluginImages;

/**
 * @since 4.3
 */
public class LogView extends ViewPart implements ILogListener {
    public static final String P_LOG_WARNING = "warning"; //$NON-NLS-1$
    public static final String P_LOG_ERROR = "error"; //$NON-NLS-1$
    public static final String P_LOG_INFO = "info"; //$NON-NLS-1$
    public static final String P_LOG_LIMIT = "limit"; //$NON-NLS-1$
    public static final String P_USE_LIMIT = "useLimit"; //$NON-NLS-1$
    public static final String P_SHOW_ALL_SESSIONS = "allSessions"; //$NON-NLS-1$
    private static final String P_COLUMN_1 = "column2"; //$NON-NLS-1$
    private static final String P_COLUMN_2 = "column3"; //$NON-NLS-1$
    private static final String P_COLUMN_3 = "column4"; //$NON-NLS-1$
    public static final String P_ACTIVATE = "activate"; //$NON-NLS-1$
    public static final String P_ORDER_TYPE = "orderType"; //$NON-NLS-1$
    public static final String P_ORDER_VALUE = "orderValue"; //$NON-NLS-1$

    public final static byte MESSAGE = 0x0;
    public final static byte PLUGIN = 0x1;
    public final static byte DATE = 0x2;
    public static int ASCENDING = 1;
    public static int DESCENDING = -1;

    int msgOrder;
    int pluginOrder;
    int dateOrder;

    ArrayList fLogs = new ArrayList();

    private Clipboard fClipboard;

    IMemento fMemento;
    File fInputFile;
    private String fDirectory;

    Comparator comparator;
    ViewerSorter sorter;

    // hover text
    private boolean canOpenTextShell;
    private Text textLabel;
    private Shell textShell;

    private boolean fFirstEvent = true;
    private boolean logFileWasLoadedOnStartup = false;

    private TreeColumn fColumn1;
    private TreeColumn fColumn2;
    private TreeColumn fColumn3;

    Tree fTree;
    TreeViewer fTreeViewer;

    Action fPropertiesAction;
    Action fDeleteLogAction;
    Action fReadLogAction;
    Action fCopyAction;
    Action fActivateViewAction;
    Action fOpenLogAction;
    Action fExportAction;

    public LogView() {
        fLogs = new ArrayList();
        fInputFile = Platform.getLogFileLocation().toFile();
    }

    @Override
    public void createPartControl( Composite parent ) {
        logFileWasLoadedOnStartup = true;
        readLogFile();
        createViewer(parent);
        createActions();
        fClipboard = new Clipboard(fTree.getDisplay());
        fTree.setToolTipText(""); //$NON-NLS-1$
        getSite().setSelectionProvider(fTreeViewer);
        initializeViewerSorter();

        Platform.addLogListener(this);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(fTree, IHelpContextIds.LOG_VIEW);
    }

    private void createActions() {
        IActionBars bars = getViewSite().getActionBars();

        fCopyAction = createCopyAction();
        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);

        IToolBarManager toolBarManager = bars.getToolBarManager();

        fExportAction = createExportAction();
        toolBarManager.add(fExportAction);

        final Action importLogAction = createImportLogAction();
        toolBarManager.add(importLogAction);

        toolBarManager.add(new Separator());

        final Action clearAction = createClearAction();
        toolBarManager.add(clearAction);

        fDeleteLogAction = createDeleteLogAction();
        toolBarManager.add(fDeleteLogAction);

        fOpenLogAction = createOpenLogAction();
        toolBarManager.add(fOpenLogAction);

        fReadLogAction = createReadLogAction();
        toolBarManager.add(fReadLogAction);

        toolBarManager.add(new Separator());

        IMenuManager mgr = bars.getMenuManager();
        mgr.add(createFilterAction());
        mgr.add(new Separator());

        fActivateViewAction = createActivateViewAction();
        mgr.add(fActivateViewAction);

        createPropertiesAction();

        MenuManager popupMenuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        IMenuListener listener = new IMenuListener() {
            public void menuAboutToShow( IMenuManager manager ) {
                manager.add(fCopyAction);
                manager.add(new Separator());
                manager.add(clearAction);
                manager.add(fDeleteLogAction);
                manager.add(fOpenLogAction);
                manager.add(fReadLogAction);
                manager.add(new Separator());
                manager.add(fExportAction);
                manager.add(importLogAction);
                manager.add(new Separator());
                ((EventDetailsDialogAction)fPropertiesAction).setComparator(comparator);
                manager.add(fPropertiesAction);
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        };
        popupMenuManager.addMenuListener(listener);
        popupMenuManager.setRemoveAllWhenShown(true);
        getSite().registerContextMenu(popupMenuManager, getSite().getSelectionProvider());
        Menu menu = popupMenuManager.createContextMenu(fTree);
        fTree.setMenu(menu);
    }

    private Action createActivateViewAction() {
        Action action = new Action(LogViewMessages.activate) {
            @Override
            public void run() {
                fMemento.putString(P_ACTIVATE, isChecked() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
        action.setChecked(fMemento.getString(P_ACTIVATE).equals("true")); //$NON-NLS-1$
        return action;
    }

    private Action createClearAction() {
        Action action = new Action(LogViewMessages.clear_text) {
            @Override
            public void run() {
                handleClear();
            }
        };
        action.setImageDescriptor(PluginImages.DESC_CLEAR);
        action.setDisabledImageDescriptor(PluginImages.DESC_CLEAR_DISABLED);
        action.setToolTipText(LogViewMessages.clear_tooltip);
        action.setText(LogViewMessages.clear_text);
        return action;
    }

    private Action createCopyAction() {
        Action action = new Action(LogViewMessages.copy_text) {
            @Override
            public void run() {
                copyToClipboard(fTreeViewer.getSelection());
            }
        };
        action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        return action;
    }

    private Action createDeleteLogAction() {
        Action action = new Action(LogViewMessages.delete_text) {
            @Override
            public void run() {
                doDeleteLog();
            }
        };
        action.setToolTipText(LogViewMessages.delete_tooltip);
        action.setImageDescriptor(PluginImages.DESC_REMOVE_LOG);
        action.setDisabledImageDescriptor(PluginImages.DESC_REMOVE_LOG_DISABLED);
        action.setEnabled(fInputFile.exists() && fInputFile.equals(Platform.getLogFileLocation().toFile()));
        return action;
    }

    private Action createExportAction() {
        Action action = new Action(LogViewMessages.export) {
            @Override
            public void run() {
                handleExport();
            }
        };
        action.setToolTipText(LogViewMessages.export_tooltip);
        action.setImageDescriptor(PluginImages.DESC_EXPORT);
        action.setDisabledImageDescriptor(PluginImages.DESC_EXPORT_DISABLED);
        action.setEnabled(fInputFile.exists());
        return action;
    }

    private Action createFilterAction() {
        Action action = new Action(LogViewMessages.filter) {
            @Override
            public void run() {
                handleFilter();
            }
        };
        action.setToolTipText(LogViewMessages.filter);
        action.setImageDescriptor(PluginImages.DESC_FILTER);
        action.setDisabledImageDescriptor(PluginImages.DESC_FILTER_DISABLED);
        return action;
    }

    private Action createImportLogAction() {
        Action action = new Action(LogViewMessages.import_text) {
            @Override
            public void run() {
                handleImport();
            }
        };
        action.setToolTipText(LogViewMessages.import_tooltip);
        action.setImageDescriptor(PluginImages.DESC_IMPORT);
        action.setDisabledImageDescriptor(PluginImages.DESC_IMPORT_DISABLED);
        return action;
    }

    private Action createOpenLogAction() {
        Action action = new Action(LogViewMessages.view_currentLog) {
            @Override
            public void run() {
                if (fInputFile.exists()) {
                    if (fInputFile.length() > LogReader.MAX_FILE_LENGTH) {
                        OpenLogDialog openDialog = new OpenLogDialog(getViewSite().getShell(), fInputFile);
                        openDialog.create();
                        openDialog.open();
                        return;
                    }
                    if (!Program.launch(fInputFile.getAbsolutePath())) {
                        Program p = Program.findProgram(".txt"); //$NON-NLS-1$
                        if (p != null) p.execute(fInputFile.getAbsolutePath());
                        else {
                            OpenLogDialog openDialog = new OpenLogDialog(getViewSite().getShell(), fInputFile);
                            openDialog.create();
                            openDialog.open();
                        }
                    }
                }
            }
        };
        action.setImageDescriptor(PluginImages.DESC_OPEN_LOG);
        action.setDisabledImageDescriptor(PluginImages.DESC_OPEN_LOG_DISABLED);
        action.setEnabled(fInputFile.exists());
        action.setToolTipText(LogViewMessages.view_currentLog_tooltip);
        return action;
    }

    private void createPropertiesAction() {
        fPropertiesAction = new EventDetailsDialogAction(fTree.getShell(), fTreeViewer);
        fPropertiesAction.setImageDescriptor(PluginImages.DESC_PROPERTIES);
        fPropertiesAction.setDisabledImageDescriptor(PluginImages.DESC_PROPERTIES_DISABLED);
        fPropertiesAction.setToolTipText(LogViewMessages.properties_tooltip);
        fPropertiesAction.setEnabled(false);
    }

    private Action createReadLogAction() {
        Action action = new Action(LogViewMessages.readLog_restore) {
            @Override
            public void run() {
                fInputFile = Platform.getLogFileLocation().toFile();
                reloadLog();
            }
        };
        action.setToolTipText(LogViewMessages.readLog_restore_tooltip);
        action.setImageDescriptor(PluginImages.DESC_READ_LOG);
        action.setDisabledImageDescriptor(PluginImages.DESC_READ_LOG_DISABLED);
        return action;
    }

    private void createViewer( Composite parent ) {
        fTreeViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
        fTree = fTreeViewer.getTree();
        createColumns(fTree);
        fTreeViewer.setContentProvider(new LogViewContentProvider(this));
        fTreeViewer.setLabelProvider(new LogViewLabelProvider());
        fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent e ) {
                handleSelectionChanged(e.getSelection());
                if (fPropertiesAction.isEnabled()) ((EventDetailsDialogAction)fPropertiesAction).resetSelection();
            }
        });
        fTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent event ) {
                ((EventDetailsDialogAction)fPropertiesAction).setComparator(comparator);
                fPropertiesAction.run();
            }
        });
        fTreeViewer.setInput(this);
        addMouseListeners();
    }

    private void createColumns( Tree tree ) {
        fColumn1 = new TreeColumn(tree, SWT.LEFT);
        fColumn1.setText(LogViewMessages.column_message);
        fColumn1.setWidth(fMemento.getInteger(P_COLUMN_1).intValue());
        fColumn1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                msgOrder *= -1;
                sorter = getViewerSorter(MESSAGE);
                fTreeViewer.setSorter(sorter);
                boolean isComparatorSet = ((EventDetailsDialogAction)fPropertiesAction).resetSelection(MESSAGE, msgOrder);
                setComparator(MESSAGE);
                if (!isComparatorSet) ((EventDetailsDialogAction)fPropertiesAction).setComparator(comparator);
                fMemento.putInteger(P_ORDER_VALUE, msgOrder);
                fMemento.putInteger(P_ORDER_TYPE, MESSAGE);
            }
        });

        fColumn2 = new TreeColumn(tree, SWT.LEFT);
        fColumn2.setText(LogViewMessages.column_plugin);
        fColumn2.setWidth(fMemento.getInteger(P_COLUMN_2).intValue());
        fColumn2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                pluginOrder *= -1;
                sorter = getViewerSorter(PLUGIN);
                fTreeViewer.setSorter(sorter);
                boolean isComparatorSet = ((EventDetailsDialogAction)fPropertiesAction).resetSelection(PLUGIN, pluginOrder);
                setComparator(PLUGIN);
                if (!isComparatorSet) ((EventDetailsDialogAction)fPropertiesAction).setComparator(comparator);
                fMemento.putInteger(P_ORDER_VALUE, pluginOrder);
                fMemento.putInteger(P_ORDER_TYPE, PLUGIN);
            }
        });

        fColumn3 = new TreeColumn(tree, SWT.LEFT);
        fColumn3.setText(LogViewMessages.column_date);
        fColumn3.setWidth(fMemento.getInteger(P_COLUMN_3).intValue());
        fColumn3.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if (dateOrder == ASCENDING) {
                    dateOrder = DESCENDING;
                } else {
                    dateOrder = ASCENDING;
                }
                sorter = getViewerSorter(DATE);
                fTreeViewer.setSorter(sorter);
                setComparator(DATE);
                ((EventDetailsDialogAction)fPropertiesAction).setComparator(comparator);
                fMemento.putInteger(P_ORDER_VALUE, dateOrder);
                fMemento.putInteger(P_ORDER_TYPE, DATE);
            }
        });

        tree.setHeaderVisible(true);
    }

    private void initializeViewerSorter() {
        ViewerSorter sorter = getViewerSorter(fMemento.getInteger(P_ORDER_TYPE).byteValue());
        fTreeViewer.setSorter(sorter);
    }

    @Override
    public void dispose() {
        writeSettings();
        Platform.removeLogListener(this);
        fClipboard.dispose();
        LogReader.reset();
        super.dispose();
    }

    void handleImport() {
        FileDialog dialog = new FileDialog(getViewSite().getShell());
        dialog.setFilterExtensions(new String[] {"*.log"}); //$NON-NLS-1$
        if (fDirectory != null) dialog.setFilterPath(fDirectory);
        String path = dialog.open();
        if (path != null && new Path(path).toFile().exists()) {
            fInputFile = new Path(path).toFile();
            fDirectory = fInputFile.getParent();
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( IProgressMonitor monitor ) {
                    monitor.beginTask(LogViewMessages.operation_importing, IProgressMonitor.UNKNOWN);
                    readLogFile();
                }
            };
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(getViewSite().getShell());
            try {
                pmd.run(true, true, op);
            } catch (InvocationTargetException e) {
            } catch (InterruptedException e) {
            } finally {
                fReadLogAction.setText(LogViewMessages.readLog_reload);
                fReadLogAction.setToolTipText(LogViewMessages.readLog_reload);
                asyncRefresh(false);
                resetDialogButtons();
            }
        }
    }

    void handleExport() {
        FileDialog dialog = new FileDialog(getViewSite().getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] {"*.log"}); //$NON-NLS-1$
        if (fDirectory != null) dialog.setFilterPath(fDirectory);
        String path = dialog.open();
        if (path != null) {
            if (!path.endsWith(".log")) //$NON-NLS-1$
            path += ".log"; //$NON-NLS-1$
            File outputFile = new Path(path).toFile();
            fDirectory = outputFile.getParent();
            if (outputFile.exists()) {
                String message = LogViewMessages.getString("LogView_confirmOverwrite_message", outputFile.toString()); //$NON-NLS-1$
                if (!MessageDialog.openQuestion(getViewSite().getShell(), LogViewMessages.exportLog, message)) return;
            }
            copy(fInputFile, outputFile);
        }
    }

    private void copy( File inputFile,
                       File outputFile ) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8")); //$NON-NLS-1$
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")); //$NON-NLS-1$
            while (reader.ready()) {
                writer.write(reader.readLine());
                writer.write(System.getProperty("line.separator")); //$NON-NLS-1$
            }
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e1) {
            }
        }
    }

    void handleFilter() {
        FilterDialog dialog = new FilterDialog(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), fMemento);
        dialog.create();
        dialog.getShell().setText(LogViewMessages.FilterDialog_title);
        if (dialog.open() == Window.OK) reloadLog();
    }

    void doDeleteLog() {
        String title = LogViewMessages.confirmDelete_title;
        String message = LogViewMessages.confirmDelete_message;
        if (!MessageDialog.openConfirm(fTree.getShell(), title, message)) return;
        if (fInputFile.delete() || fLogs.size() > 0) {
            fLogs.clear();
            asyncRefresh(false);
            resetDialogButtons();
        }
    }

    public void fillContextMenu( IMenuManager manager ) {
    }

    public LogEntry[] getLogs() {
        return (LogEntry[])fLogs.toArray(new LogEntry[fLogs.size()]);
    }

    protected void handleClear() {
        BusyIndicator.showWhile(fTree.getDisplay(), new Runnable() {
            public void run() {
                fLogs.clear();
                asyncRefresh(false);
                resetDialogButtons();
            }
        });
    }

    protected void reloadLog() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor ) {
                monitor.beginTask(LogViewMessages.operation_reloading, IProgressMonitor.UNKNOWN);
                readLogFile();
            }
        };
        ProgressMonitorDialog pmd = new ProgressMonitorDialog(getViewSite().getShell());
        try {
            pmd.run(true, true, op);
        } catch (InvocationTargetException e) {
        } catch (InterruptedException e) {
        } finally {
            fReadLogAction.setText(LogViewMessages.readLog_restore);
            fReadLogAction.setToolTipText(LogViewMessages.readLog_restore);
            asyncRefresh(false);
            resetDialogButtons();
        }
    }

    void readLogFile() {
        fLogs.clear();
        if (!fInputFile.exists()) return;
        LogReader.parseLogFile(fInputFile, fLogs, fMemento);
    }

    public void logging( IStatus status,
                         String plugin ) {
        if (!fInputFile.equals(Platform.getLogFileLocation().toFile())) return;
        if (fFirstEvent) {
            if (!logFileWasLoadedOnStartup) {
                readLogFile();
            }
            asyncRefresh();
            fFirstEvent = false;
        } else {
            pushStatus(status);
        }
    }

    private void pushStatus( IStatus status ) {
        LogEntry entry = new LogEntry(status);
        LogReader.addEntry(entry, fLogs, fMemento, true);
        asyncRefresh();
    }

    private void asyncRefresh() {
        asyncRefresh(true);
    }

    void asyncRefresh( final boolean activate ) {
        if (fTree.isDisposed()) return;
        Display display = fTree.getDisplay();
        final ViewPart view = this;
        if (display != null) {
            display.asyncExec(new Runnable() {
                public void run() {
                    if (!fTree.isDisposed()) {
                        fTreeViewer.refresh();
                        fDeleteLogAction.setEnabled(fInputFile.exists()
                                                    && fInputFile.equals(Platform.getLogFileLocation().toFile()));
                        fOpenLogAction.setEnabled(fInputFile.exists());
                        fExportAction.setEnabled(fInputFile.exists());
                        if (activate && fActivateViewAction.isChecked()) {
                            IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                            if (page != null) page.bringToTop(view);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setFocus() {
        if (fTree != null && !fTree.isDisposed()) fTree.setFocus();
    }

    void handleSelectionChanged( ISelection selection ) {
        updateStatus(selection);
        fCopyAction.setEnabled(!selection.isEmpty());
        fPropertiesAction.setEnabled(!selection.isEmpty());
    }

    private void updateStatus( ISelection selection ) {
        IStatusLineManager status = getViewSite().getActionBars().getStatusLineManager();
        if (selection.isEmpty()) status.setMessage(null);
        else {
            LogEntry entry = (LogEntry)((IStructuredSelection)selection).getFirstElement();
            status.setMessage(((LogViewLabelProvider)fTreeViewer.getLabelProvider()).getColumnText(entry, 0));
        }
    }

    void copyToClipboard( ISelection selection ) {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        if (selection.isEmpty()) return;
        LogEntry entry = (LogEntry)((IStructuredSelection)selection).getFirstElement();
        entry.write(pwriter);
        pwriter.flush();
        String textVersion = writer.toString();
        try {
            pwriter.close();
            writer.close();
        } catch (IOException e) {
        }
        if (textVersion.trim().length() > 0) {
            // set the clipboard contents
            fClipboard.setContents(new Object[] {textVersion}, new Transfer[] {TextTransfer.getInstance()});
        }
    }

    @Override
    public void init( IViewSite site,
                      IMemento memento ) throws PartInitException {
        super.init(site, memento);
        if (memento == null) this.fMemento = XMLMemento.createWriteRoot("LOGVIEW"); //$NON-NLS-1$
        else this.fMemento = memento;
        readSettings();

        // initialize column ordering
        final byte type = this.fMemento.getInteger(P_ORDER_TYPE).byteValue();
        switch (type) {
            case DATE:
                dateOrder = this.fMemento.getInteger(P_ORDER_VALUE).intValue();
                msgOrder = -1;
                pluginOrder = -1;
                break;
            case MESSAGE:
                msgOrder = this.fMemento.getInteger(P_ORDER_VALUE).intValue();
                dateOrder = -1;
                pluginOrder = -1;
                break;
            case PLUGIN:
                pluginOrder = this.fMemento.getInteger(P_ORDER_VALUE).intValue();
                msgOrder = -1;
                dateOrder = -1;
                break;
            default:
                dateOrder = -1;
                msgOrder = -1;
                pluginOrder = -1;
        }
        setComparator(fMemento.getInteger(P_ORDER_TYPE).byteValue());
    }

    private void initializeMemento() {
        if (fMemento.getString(P_USE_LIMIT) == null) fMemento.putString(P_USE_LIMIT, "true"); //$NON-NLS-1$
        if (fMemento.getInteger(P_LOG_LIMIT) == null) fMemento.putInteger(P_LOG_LIMIT, 50);
        if (fMemento.getString(P_LOG_INFO) == null) fMemento.putString(P_LOG_INFO, "true"); //$NON-NLS-1$
        if (fMemento.getString(P_LOG_WARNING) == null) fMemento.putString(P_LOG_WARNING, "true"); //$NON-NLS-1$
        if (fMemento.getString(P_LOG_ERROR) == null) fMemento.putString(P_LOG_ERROR, "true"); //$NON-NLS-1$
        if (fMemento.getString(P_SHOW_ALL_SESSIONS) == null) fMemento.putString(P_SHOW_ALL_SESSIONS, "true"); //$NON-NLS-1$
        Integer width = fMemento.getInteger(P_COLUMN_1);
        if (width == null || width.intValue() == 0) fMemento.putInteger(P_COLUMN_1, 300);
        width = fMemento.getInteger(P_COLUMN_2);
        if (width == null || width.intValue() == 0) fMemento.putInteger(P_COLUMN_2, 150);
        width = fMemento.getInteger(P_COLUMN_3);
        if (width == null || width.intValue() == 0) fMemento.putInteger(P_COLUMN_3, 150);
        if (fMemento.getString(P_ACTIVATE) == null) fMemento.putString(P_ACTIVATE, "true"); //$NON-NLS-1$

        fMemento.putInteger(P_ORDER_VALUE, -1);
        fMemento.putInteger(P_ORDER_TYPE, DATE);
    }

    @Override
    public void saveState( IMemento memento ) {
        if (this.fMemento == null || memento == null) return;
        this.fMemento.putInteger(P_COLUMN_1, fColumn1.getWidth());
        this.fMemento.putInteger(P_COLUMN_2, fColumn2.getWidth());
        this.fMemento.putInteger(P_COLUMN_3, fColumn3.getWidth());
        this.fMemento.putString(P_ACTIVATE, fActivateViewAction.isChecked() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
        memento.putMemento(this.fMemento);
        writeSettings();
    }

    private void addMouseListeners() {
        Listener tableListener = new Listener() {
            public void handleEvent( Event e ) {
                switch (e.type) {
                    case SWT.MouseMove:
                        onMouseMove(e);
                        break;
                    case SWT.MouseHover:
                        onMouseHover(e);
                        break;
                    case SWT.MouseDown:
                        onMouseDown(e);
                        break;
                }
            }
        };
        int[] tableEvents = new int[] {SWT.MouseDown, SWT.MouseMove, SWT.MouseHover};
        for (int i = 0; i < tableEvents.length; i++) {
            fTree.addListener(tableEvents[i], tableListener);
        }
    }

    private void makeHoverShell() {
        textShell = new Shell(fTree.getShell(), SWT.NO_FOCUS | SWT.ON_TOP);
        Display display = textShell.getDisplay();
        textShell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        GridLayout layout = new GridLayout(1, false);
        int border = ((fTree.getShell().getStyle() & SWT.NO_TRIM) == 0) ? 0 : 1;
        layout.marginHeight = border;
        layout.marginWidth = border;
        textShell.setLayout(layout);
        textShell.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite shellComposite = new Composite(textShell, SWT.NONE);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        shellComposite.setLayout(layout);
        shellComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
        textLabel = new Text(shellComposite, SWT.WRAP | SWT.MULTI);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 100;
        gd.grabExcessHorizontalSpace = true;
        textLabel.setLayoutData(gd);
        Color c = fTree.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        textLabel.setBackground(c);
        c = fTree.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        textLabel.setForeground(c);
        textLabel.setEditable(false);
        textShell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                onTextShellDispose(e);
            }
        });
    }

    void onTextShellDispose( DisposeEvent e ) {
        canOpenTextShell = true;
        setFocus();
    }

    void onMouseDown( Event e ) {
        if (textShell != null && !textShell.isDisposed() && !textShell.isFocusControl()) {
            textShell.close();
            canOpenTextShell = true;
        }
    }

    void onMouseHover( Event e ) {
        if (!canOpenTextShell) return;
        canOpenTextShell = false;
        Point point = new Point(e.x, e.y);
        TreeItem item = fTree.getItem(point);
        if (item == null) return;
        String message = ((LogEntry)item.getData()).getStack();
        if (message == null) return;
        makeHoverShell();
        textLabel.setText(message);
        int x = point.x + 5;
        int y = point.y - (fTree.getItemHeight() * 2) - 20;
        textShell.setLocation(fTree.toDisplay(x, y));
        textShell.setSize(fTree.getColumn(0).getWidth(), 125);
        textShell.open();
        setFocus();
    }

    void onMouseMove( Event e ) {
        if (textShell != null && !textShell.isDisposed()) textShell.close();

        Point point = new Point(e.x, e.y);
        TreeItem item = fTree.getItem(point);
        if (item == null) return;
        Image image = item.getImage();
        LogEntry entry = (LogEntry)item.getData();
        int parentCount = getNumberOfParents(entry);
        int startRange = 20 + Math.max(image.getBounds().width + 2, 7 + 2) * parentCount;
        int endRange = startRange + 16;
        canOpenTextShell = e.x >= startRange && e.x <= endRange;
    }

    private int getNumberOfParents( LogEntry entry ) {
        LogEntry parent = (LogEntry)entry.getParent(entry);
        if (parent == null) return 0;
        return 1 + getNumberOfParents(parent);
    }

    public Comparator getComparator() {
        return comparator;
    }

    void setComparator( byte sortType ) {
        if (sortType == DATE) {
            comparator = new Comparator() {
                public int compare( Object e1,
                                    Object e2 ) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //$NON-NLS-1$
                        Date date1 = formatter.parse(((LogEntry)e1).getDate());
                        Date date2 = formatter.parse(((LogEntry)e2).getDate());
                        if (dateOrder == ASCENDING) return date1.before(date2) ? -1 : 1;
                        return date1.after(date2) ? -1 : 1;
                    } catch (ParseException e) {
                    }
                    return 0;
                }
            };
        } else if (sortType == PLUGIN) {
            comparator = new Comparator() {
                public int compare( Object e1,
                                    Object e2 ) {
                    LogEntry entry1 = (LogEntry)e1;
                    LogEntry entry2 = (LogEntry)e2;
                    return sorter.compare(fTreeViewer, entry1.getPluginId(), entry2.getPluginId()) * pluginOrder;
                }
            };
        } else {
            comparator = new Comparator() {
                public int compare( Object e1,
                                    Object e2 ) {
                    LogEntry entry1 = (LogEntry)e1;
                    LogEntry entry2 = (LogEntry)e2;
                    return sorter.compare(fTreeViewer, entry1.getMessage(), entry2.getMessage()) * msgOrder;
                }
            };
        }
    }

    ViewerSorter getViewerSorter( byte sortType ) {
        if (sortType == PLUGIN) {
            return new ViewerSorter() {
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    LogEntry entry1 = (LogEntry)e1;
                    LogEntry entry2 = (LogEntry)e2;
                    return super.compare(viewer, entry1.getPluginId(), entry2.getPluginId()) * pluginOrder;
                }
            };
        } else if (sortType == MESSAGE) {
            return new ViewerSorter() {
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    LogEntry entry1 = (LogEntry)e1;
                    LogEntry entry2 = (LogEntry)e2;
                    return super.compare(viewer, entry1.getMessage(), entry2.getMessage()) * msgOrder;
                }
            };
        } else {
            return new ViewerSorter() {
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //$NON-NLS-1$
                        Date date1 = formatter.parse(((LogEntry)e1).getDate());
                        Date date2 = formatter.parse(((LogEntry)e2).getDate());
                        if (dateOrder == ASCENDING) return date1.before(date2) ? -1 : 1;
                        return date1.after(date2) ? -1 : 1;
                    } catch (ParseException e) {
                    }
                    return 0;
                }
            };
        }
    }

    void resetDialogButtons() {
        ((EventDetailsDialogAction)fPropertiesAction).resetDialogButtons();
    }

    /**
     * Returns the filter dialog settings object used to maintain state between filter dialogs
     * 
     * @return the dialog settings to be used
     */
    private IDialogSettings getLogSettings() {
        IDialogSettings settings = UiPlugin.getDefault().getDialogSettings();
        return settings.getSection(getClass().getName());
    }

    /**
     * Returns the plugin preferences used to maintain state of log view
     * 
     * @return the plugin preferences
     */
    private Preferences getLogPreferences() {
        return UiPlugin.getDefault().getPluginPreferences();
    }

    private void readSettings() {
        IDialogSettings s = getLogSettings();
        Preferences p = getLogPreferences();
        if (s == null || p == null) {
            initializeMemento();
            return;
        }
        try {
            fMemento.putString(P_USE_LIMIT, s.getBoolean(P_USE_LIMIT) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            fMemento.putInteger(P_LOG_LIMIT, s.getInt(P_LOG_LIMIT));
            fMemento.putString(P_LOG_INFO, s.getBoolean(P_LOG_INFO) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            fMemento.putString(P_LOG_WARNING, s.getBoolean(P_LOG_WARNING) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            fMemento.putString(P_LOG_ERROR, s.getBoolean(P_LOG_ERROR) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            fMemento.putString(P_SHOW_ALL_SESSIONS, s.getBoolean(P_SHOW_ALL_SESSIONS) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            fMemento.putInteger(P_COLUMN_1, p.getInt(P_COLUMN_1) > 0 ? p.getInt(P_COLUMN_1) : 300);
            fMemento.putInteger(P_COLUMN_2, p.getInt(P_COLUMN_2) > 0 ? p.getInt(P_COLUMN_2) : 150);
            fMemento.putInteger(P_COLUMN_3, p.getInt(P_COLUMN_3) > 0 ? p.getInt(P_COLUMN_3) : 300);
            fMemento.putString(P_ACTIVATE, p.getBoolean(P_ACTIVATE) ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
            int order = p.getInt(P_ORDER_VALUE);
            fMemento.putInteger(P_ORDER_VALUE, order == 0 ? -1 : order);
            fMemento.putInteger(P_ORDER_TYPE, p.getInt(P_ORDER_TYPE));
        } catch (NumberFormatException e) {
            fMemento.putInteger(P_LOG_LIMIT, 50);
            fMemento.putInteger(P_COLUMN_1, 300);
            fMemento.putInteger(P_COLUMN_2, 150);
            fMemento.putInteger(P_COLUMN_3, 150);
            fMemento.putInteger(P_ORDER_TYPE, MESSAGE);
            fMemento.putInteger(P_ORDER_VALUE, -1);
        }
    }

    private void writeSettings() {
        writeViewSettings();
        writeFilterSettings();
    }

    private void writeFilterSettings() {
        IDialogSettings settings = getLogSettings();
        if (settings == null) settings = UiPlugin.getDefault().getDialogSettings().addNewSection(getClass().getName());
        settings.put(P_USE_LIMIT, fMemento.getString(P_USE_LIMIT).equals("true")); //$NON-NLS-1$
        settings.put(P_LOG_LIMIT, fMemento.getInteger(P_LOG_LIMIT).intValue());
        settings.put(P_LOG_INFO, fMemento.getString(P_LOG_INFO).equals("true")); //$NON-NLS-1$
        settings.put(P_LOG_WARNING, fMemento.getString(P_LOG_WARNING).equals("true")); //$NON-NLS-1$
        settings.put(P_LOG_ERROR, fMemento.getString(P_LOG_ERROR).equals("true")); //$NON-NLS-1$
        settings.put(P_SHOW_ALL_SESSIONS, fMemento.getString(P_SHOW_ALL_SESSIONS).equals("true")); //$NON-NLS-1$
    }

    private void writeViewSettings() {
        Preferences preferences = getLogPreferences();
        preferences.setValue(P_COLUMN_1, fMemento.getInteger(P_COLUMN_1).intValue());
        preferences.setValue(P_COLUMN_2, fMemento.getInteger(P_COLUMN_2).intValue());
        preferences.setValue(P_COLUMN_3, fMemento.getInteger(P_COLUMN_3).intValue());
        preferences.setValue(P_ACTIVATE, fMemento.getString(P_ACTIVATE).equals("true")); //$NON-NLS-1$
        int order = fMemento.getInteger(P_ORDER_VALUE).intValue();
        preferences.setValue(P_ORDER_VALUE, order == 0 ? -1 : order);
        preferences.setValue(P_ORDER_TYPE, fMemento.getInteger(P_ORDER_TYPE).intValue());
    }
}
