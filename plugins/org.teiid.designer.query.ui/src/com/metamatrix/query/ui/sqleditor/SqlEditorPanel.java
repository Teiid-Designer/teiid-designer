/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui.sqleditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.teiid.language.SQLConstants;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.util.ElementSymbolOptimizer;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.query.internal.ui.builder.CriteriaBuilder;
import com.metamatrix.query.internal.ui.builder.ExpressionBuilder;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.internal.ui.sqleditor.SqlEditorInternalEvent;
import com.metamatrix.query.internal.ui.sqleditor.SqlTextViewer;
import com.metamatrix.query.internal.ui.sqleditor.actions.DownFont;
import com.metamatrix.query.internal.ui.sqleditor.actions.ExpandSelect;
import com.metamatrix.query.internal.ui.sqleditor.actions.ExportToFile;
import com.metamatrix.query.internal.ui.sqleditor.actions.ImportFromFile;
import com.metamatrix.query.internal.ui.sqleditor.actions.LaunchCriteriaBuilder;
import com.metamatrix.query.internal.ui.sqleditor.actions.LaunchExpressionBuilder;
import com.metamatrix.query.internal.ui.sqleditor.actions.ToggleMessage;
import com.metamatrix.query.internal.ui.sqleditor.actions.ToggleOptimizer;
import com.metamatrix.query.internal.ui.sqleditor.actions.UpFont;
import com.metamatrix.query.internal.ui.sqleditor.actions.Validate;
import com.metamatrix.query.internal.ui.sqleditor.component.AliasSymbolDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DeleteDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNodeConstants;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNodeUtils;
import com.metamatrix.query.internal.ui.sqleditor.component.FromDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.GroupSymbolFinder;
import com.metamatrix.query.internal.ui.sqleditor.component.QueryDisplayComponent;
import com.metamatrix.query.internal.ui.sqleditor.component.QueryDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.SelectDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.SetQueryDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.SqlIndexLocator;
import com.metamatrix.query.internal.ui.sqleditor.component.UpdateDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.component.WhereDisplayNode;
import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.text.ScaledFontManager;
import com.metamatrix.ui.text.StyledTextEditor;
import com.metamatrix.ui.text.TextFontManager;

public class SqlEditorPanel extends SashForm
    implements UiConstants, DisplayNodeConstants, SelectionListener, ISelectionChangedListener, IPropertyChangeListener,
    KeyListener, MouseListener {

    /** Changes Pending Message */
    private static final String QUERY_CHANGES_PENDING_MESSAGE = UiPlugin.getDefault().getPluginUtil().getString("SqlEditorPanel.changesPendingMsg"); //$NON-NLS-1$
    private static final String IMPORT_PROBLEM = "SqlEditorPanel.importProb"; //$NON-NLS-1$
    private static final String EXPORT_PROBLEM = "SqlEditorPanel.exportProb"; //$NON-NLS-1$
    private static final String EXPORT_SQL_DIALOG_TITLE = "SqlEditorPanel.exportSqlDialog.title"; //$NON-NLS-1$
    private static final String IMPORT_SQL_DIALOG_TITLE = "SqlEditorPanel.importSqlDialog.title"; //$NON-NLS-1$
    private static final String IMPORT_SQL_PROBLEM_DIALOG_TITLE = "SqlEditorPanel.importSqlProblemDialog.title"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILENAME = "SqlEditorPanel.exportDefaultFile.text"; //$NON-NLS-1$
    private static final String EXPORT_DEFAULT_FILEEXT = "SqlEditorPanel.exportDefaultExtension.text"; //$NON-NLS-1$
    private static final String MONITOR_VALIDATING_SQL = "SqlEditorPanel.validatingSQL"; //$NON-NLS-1$

    public static final String ACTION_ID_VALIDATE = "Validate"; //$NON-NLS-1$   // Validate validateAction
    public static final String ACTION_ID_LAUNCH_CRITERIA_BUILDER = "LaunchCriteriaBuilder"; //$NON-NLS-1$   // LaunchCriteriaBuilder launchCriteriaBuilderAction;
    public static final String ACTION_ID_LAUNCH_EXPRESSION_BUILDER = "LaunchExpressionBuilder"; //$NON-NLS-1$   // LaunchExpressionBuilder launchExpressionBuilderAction;
    public static final String ACTION_ID_EXPAND_SELECT = "ExpandSelect"; //$NON-NLS-1$   // ExpandSelect expandSelectAction;
    public static final String ACTION_ID_TOGGLE_MESSAGE = "ToggleMessage"; //$NON-NLS-1$   // ToggleMessage toggleMessageAction;
    public static final String ACTION_ID_TOGGLE_OPTIMIZER = "ToggleOptimizer"; //$NON-NLS-1$   // ToggleOptimizer toggleOptimizerAction;
    public static final String ACTION_ID_UP_FONT = "UpFont"; //$NON-NLS-1$   // UpFont upFontAction;
    public static final String ACTION_ID_DOWN_FONT = "DownFont"; //$NON-NLS-1$   // DownFont downFontAction;
    public static final String ACTION_ID_IMPORT_FROM_FILE = "ImportFromFile"; //$NON-NLS-1$   // ImportFromFile importFromFileAction;
    public static final String ACTION_ID_EXPORT_TO_FILE = "ExportToFile"; //$NON-NLS-1$   // ExportToFile exportToFileAction;

    public static final String[] DEFAULT_INCLUDED_ACTIONS = new String[] {ACTION_ID_VALIDATE, ACTION_ID_LAUNCH_CRITERIA_BUILDER,
        ACTION_ID_LAUNCH_EXPRESSION_BUILDER, ACTION_ID_EXPAND_SELECT, ACTION_ID_TOGGLE_MESSAGE, ACTION_ID_TOGGLE_OPTIMIZER,
        ACTION_ID_UP_FONT, ACTION_ID_DOWN_FONT, ACTION_ID_IMPORT_FROM_FILE, ACTION_ID_EXPORT_TO_FILE};

    private ColorManager colorManager;
    private Color currentBkgdColor;
    private Color widgetBkgdColor;
    private IVerticalRuler verticalRuler;
    SqlTextViewer sqlTextViewer;
    private StyledTextEditor textEditor;
    // private SqlFormattingStrategy formattingStrategy;
    IDocument sqlDocument;
    private boolean messageShowing = true;
    StyledTextEditor messageArea;
    QueryDisplayComponent queryDisplayComponent;
    ViewForm sqlViewForm;

    // -------------------------------------------------------------------------------------------------------------------
    // DEFECT 23230
    // We need to cache the panelSqlText here to maintain the last setText() value
    // -------------------------------------------------------------------------------------------------------------------
    private String panelSqlText = null;

    boolean validateSelected = false;
    boolean hasPendingChanges = false;
    boolean isCompleteRefresh = false;
    private boolean hasUserError = false;
    private boolean isEditable = true;
    private TextFontManager tfmManager;

    private boolean selectExpansionEnabled = true;
    /** select Drop Enabled status */
    private boolean selectDropsEnabled = true;

    private int caretOffset = -1;
    private int caretXPosition = 0;
    private int caretYPosition = 0;

    // Actions
    private List<IAction> actionList = null;
    Validate validateAction;
    LaunchCriteriaBuilder launchCriteriaBuilderAction;
    LaunchExpressionBuilder launchExpressionBuilderAction;
    ExpandSelect expandSelectAction;
    ToggleMessage toggleMessageAction;
    ToggleOptimizer toggleOptimizerAction;
    UpFont upFontAction;
    DownFont downFontAction;
    ImportFromFile importFromFileAction;
    ExportToFile exportToFileAction;

    List includedActionsList;

    /** The width of the vertical ruler. */
    private Object eventSource;

    /** The width of the vertical ruler. */
    protected final static int VERTICAL_RULER_WIDTH = 0;

    /** The editor's text listener. */
    // private ITextListener textListener= new TextListener();
    /** List of listeners registered for this panels events */
    private List eventListeners;
    /** List of listeners registered for this panels internal events */
    private List internalEventListeners;

    private Collection externalBuilderGroups = null;
    String savedSql = BLANK;
    String currentMessage = BLANK;
    private List setQueryStates;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public SqlEditorPanel( Composite parent,
                           QueryValidator queryValidator,
                           int queryType ) {
        super(parent, SWT.VERTICAL);
        init(queryValidator, queryType);
        includedActionsList = getDefaultActionList();
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public SqlEditorPanel( Composite parent,
                           QueryValidator queryValidator,
                           int queryType,
                           List actionsList ) {
        super(parent, SWT.VERTICAL);
        init(queryValidator, queryType);
        this.includedActionsList = actionsList;
    }

    /**
     * Initialize the panel.
     */
    private void init( QueryValidator queryValidator,
                       int queryType ) {
        // this.formattingStrategy =
        // new SqlFormattingStrategy();

        queryDisplayComponent = new QueryDisplayComponent(queryValidator, queryType);

        colorManager = new ColorManager();
        currentBkgdColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        widgetBkgdColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        // ... Do NOT create a vertical ruler at this time (4.0, beta1...GA?)
        // This is used to carry line by line marks, like the error and warning decorations
        // used in the eclipse java ide (jdt). We will not have such features for the
        // forseeable future and until then this space is wasted (see also Defect 10366)
        // verticalRuler= new VerticalRuler(VERTICAL_RULER_WIDTH);

        // add message area to ViewForm to get Eclipse view look
        sqlViewForm = new ViewForm(this, SWT.BORDER);
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;
        sqlTextViewer = new SqlTextViewer(sqlViewForm, verticalRuler, styles, colorManager);
        sqlViewForm.setContent(sqlTextViewer.getControl());

        sqlTextViewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey( VerifyEvent event ) {
                if ((event.stateMask == SWT.CTRL && event.character == 127)
                    || (event.stateMask == SWT.CTRL && event.character == ' ')) {
                    event.doit = false;
                    sqlTextViewer.showAssistance();
                }
            }
        });

        sqlTextViewer.getTextWidget().addMouseListener(new MouseListener() {
            public void mouseDoubleClick( MouseEvent event ) {
                sqlTextViewer.handleDoubleClick();
            }

            public void mouseUp( MouseEvent event ) {
            }

            public void mouseDown( MouseEvent event ) {
                captureCaretInfo();
            }
        });

        sqlTextViewer.getTextWidget().addExtendedModifyListener(new ExtendedModifyListener() {
            // This method is invoked every time the text changes
            public void modifyText( ExtendedModifyEvent event ) {
                fireEditorInternalEvent(SqlEditorInternalEvent.TEXT_CHANGED);
            }
        });

        this.textEditor = new StyledTextEditor(this.sqlTextViewer);
        // sqlDocument.set("SELECT * FROM TABLE");
        sqlDocument = textEditor.getDocument();
        sqlTextViewer.setEditable(true);
        isEditable = true;

        // Initialize optimization
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        boolean optimizationOn = prefStore.getBoolean(UiConstants.Prefs.SQL_OPTIMIZATION_ON);
        queryDisplayComponent.setOptimizerOn(optimizationOn);

        sqlTextViewer.setRangeIndicator(new DefaultRangeIndicator());

        // Set overall grid layout
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData);
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        // textArea.addExtendedModifyListener(modifyListener);

        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        textEditor.setLayoutData(gridData);

        // add message area to ViewForm to get Eclipse view look
        ViewForm msgViewForm = new ViewForm(this, SWT.BORDER);
        int messageAreaStyle = SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP;
        messageArea = StyledTextEditor.createReadOnlyEditor(msgViewForm, messageAreaStyle);
        messageArea.setLayoutData(gridData);
        messageArea.setBackground(widgetBkgdColor);
        msgViewForm.setContent(messageArea.getTextWidget());

        sqlTextViewer.getTextWidget().addSelectionListener(this);
        sqlTextViewer.addSelectionChangedListener(this);
        sqlTextViewer.getTextWidget().addKeyListener(this);
        sqlTextViewer.getTextWidget().addMouseListener(this);

        int[] wts = {4, 1};
        setWeights(wts);

        showMessageArea(false);

        prefStore.addPropertyChangeListener(this);

        // Add Document Listener to for notification of text changes
        sqlDocument.addDocumentListener(new DocumentChangeListener());
    }

    /**
     * Clears the undo/redo history of the SQL text editor.
     * 
     * @since 5.5.3
     */
    public void resetUndoRedoHistory() {
        this.textEditor.resetUndoRedoHistory();
    }

    /**
     * handle preference change. This only responds to change in formatting preference.
     */
    public void propertyChange( PropertyChangeEvent e ) {
        String propStr = e.getProperty();
        if (propStr != null
            && (propStr.equals(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE) || propStr.equals(UiConstants.Prefs.INDENT_CLAUSE_CONTENT))) {
            if (!this.isDisposed() && this.isVisible()) {
                setText(getText());
            }
        }
    }

    public void setQueryValidator( QueryValidator validator ) {
        this.queryDisplayComponent.setQueryValidator(validator);
    }

    /**
     * get the SQL Text from the panel
     * 
     * @return the SQL text from the panel
     */
    public String getText() {
        return sqlDocument.get();
    }

    void setTextInTransaction( final String proposedSqlText,
                               final Object source,
                               final boolean doResolveAndValidate,
                               final QueryValidationResult result,
                               final IProgressMonitor monitor ) {
        boolean requiredStart = false;
        boolean succeeded = false;
        // System.out.println("  SqlEditorPanel.setTextInTxn():  hasPendingChanges = " + hasPendingChanges + "   CURRENT SQL = " +
        // panelSqlText);
        try {
            requiredStart = ModelerCore.startTxn(false, false, "Setting Sql Text", source); //$NON-NLS-1$$
            // --------------------------------------------------------
            this.eventSource = source;

            // -------------------------------------------------------------------------------------------------------------------
            // DEFECT 23230
            // Added String check here to prevent unnecessary validation if SQL doesn't change.
            // -------------------------------------------------------------------------------------------------------------------
            boolean setSqlText = hasPendingChanges;

            // Have to check for hasPendingChanges because their may have been changes in the "Hidden" display nodes.
            if (!setSqlText) {
                if (panelSqlText == null) {
                    if (proposedSqlText != null) {
                        setSqlText = true;
                    }
                } else if (!panelSqlText.equalsIgnoreCase(proposedSqlText)) {
                    setSqlText = true;
                }
            }

            // System.out.println("         ---------------   SQL Changed = " + setSqlText + "   SQL = " + proposedSqlText);
            if (setSqlText) {
                panelSqlText = proposedSqlText;
                // System.out.println("  >> SEP.setText() Called:"); // calling queryDisplayComponent.setText() SQL = " + sql);
                queryDisplayComponent.setText(panelSqlText, doResolveAndValidate, result, monitor);

                // Refresh the EditorPanel, using the queryDisplayComponent
                refreshWithDisplayComponent();
                monitor.worked(10);

                // Fire Editor State to rest of Editor
                // DO NOT fire EditorEvent
                // fireEditorInternalEvent(SqlEditorInternalEvent.TEXT_RESET);

                // After setting text, set eventSource to this.
                this.eventSource = this;
            }
            succeeded = true;
        } catch (Exception ex) {
            UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName()
                                                    + ":" + this.getClass().getName() + ".setTextInTransaction()"); //$NON-NLS-1$  //$NON-NLS-2$
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Sets the SQL Statement on the Panel
     * 
     * @param SQLString the SQL String to set on the panel
     */
    public void setText( final String sql,
                         final Object source,
                         final boolean doResolveAndValidate,
                         final QueryValidationResult theResult ) {
        if (doResolveAndValidate) {
            final IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( final IProgressMonitor theMonitor ) {
                    theMonitor.beginTask(MONITOR_VALIDATING_SQL, 100);
                    theMonitor.worked(20);

                    setTextInTransaction(sql, source, doResolveAndValidate, theResult, theMonitor);

                    theMonitor.done();
                }
            };
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                UiConstants.Util.log(e.getTargetException());
            } catch (Exception e) {
                UiConstants.Util.log(e.getMessage());
            }
        } else {
            setTextInTransaction(sql, source, doResolveAndValidate, theResult, new NullProgressMonitor());
        }
    }

    public void clear() {
        savedSql = BLANK;
        panelSqlText = null;
        queryDisplayComponent.reset();
        refreshWithDisplayComponent();
    }

    /*
     * Private method to set sql string text. Only used by methods internal to SqlEditorPanel
     * Assumes that validation will be run on new sql string
     */
    private void setText( final String sql,
                          Object source ) {
        setText(sql, source, true, null);
    }

    /**
     * Sets the SQL Statement on the Panel
     * 
     * @param SQLString the SQL String to set on the panel
     */
    public void setText( final String sql ) {
        this.eventSource = this;
        // If null sql, use blank string
        String theSql = sql;
        if (sql == null) theSql = BLANK;

        queryDisplayComponent.setText(theSql, true, null);

        // Refresh the EditorPanel, using the queryDisplayComponent
        refreshWithDisplayComponent();
    }

    /**
     * Method executed to Validate the current SQL text.
     */
    public void validate() {
        String panelText = getText();

        // Set validate Selected
        validateSelected = true;

        // Reset Text on Panel to reformat

        setText(panelText, this);
    }

    public boolean threadIsNotDisplayThread() {
        return (this.getDisplay() != null && Thread.currentThread() != this.getDisplay().getThread());
    }

    /**
     * Refreshes the Query JTextPane with the contents of the queryDisplayComponent
     */
    private void refreshWithDisplayComponent() {
        hasPendingChanges = false;
        isCompleteRefresh = true;
        // point the font manager to the current editor's text viewer
        if (threadIsNotDisplayThread()) {
            this.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    setMessage(queryDisplayComponent);
                    // Set the SQL Text displayed.
                    sqlDocument.set(queryDisplayComponent.toDisplayString());

                    setQueryTextBackground();
                }
            });
        } else {
            setMessage(queryDisplayComponent);

            // Set the SQL Text displayed.
            sqlDocument.set(queryDisplayComponent.toDisplayString());

            setQueryTextBackground();
        }
    }

    /**
     * Fire a SqlEditorEvent to the registered listeners, based on the state of SQL displayed
     * (CHANGES_PENDING,CHANGED,PARSABLE,RESOLVABLE,VALIDATABLE, CARET_CHANGED).
     */
    void fireEditorEvent() {
        // Event source is this panel if null
        if (eventSource == null) {
            eventSource = this;
        }

        boolean isParsable = queryDisplayComponent.isParsable();
        boolean isResolvable = queryDisplayComponent.isResolvable();
        boolean isValidatable = queryDisplayComponent.isValidatable();
        SqlEditorEvent event = null;
        // Has Pending Changes
        if (hasPendingChanges) {
            event = new SqlEditorEvent(eventSource, SqlEditorEvent.CHANGES_PENDING);
            // Sql changed but not parsable
        } else if (!isParsable) {
            event = new SqlEditorEvent(eventSource, getText(), SqlEditorEvent.CHANGED);
            // Sql changed, parsable but not resolvable
        } else if (!isResolvable) {
            event = new SqlEditorEvent(eventSource, getCommand(), SqlEditorEvent.PARSABLE);
            // Sql changed, resolvable but not validatable
        } else if (!isValidatable) {
            // Command for this event should be deoptimized
            Command theCommand = (Command)getCommand().clone();
            if (isOptimizerOn()) {
                ElementSymbolOptimizer.fullyQualifyElements(theCommand);
            }
            // fire the event
            event = new SqlEditorEvent(eventSource, theCommand, SqlEditorEvent.RESOLVABLE);
            // Sql changed, validatable
        } else {
            // Command for this event should be deoptimized
            Command theCommand = (Command)getCommand().clone();
            if (isOptimizerOn()) {
                ElementSymbolOptimizer.fullyQualifyElements(theCommand);
            }
            // fire the event
            event = new SqlEditorEvent(eventSource, theCommand, SqlEditorEvent.VALIDATABLE);
        }
        notifyEventListeners(event);
    }

    /**
     * Fire a SqlEditorEvent to the registered listeners indicating CARET_CHANGED on displayed SQL
     * 
     * @since 4.2
     */

    private void notifyCaretChanged() {
        notifyEventListeners(new SqlEditorEvent(this, SqlEditorEvent.CARET_CHANGED));
    }

    /**
     * Fire a SqlEditorInternalEvent to the registered listeners, based on the eventType
     * (SqlEditorInternalEvent.TEXT_RESET,TEXT_INSERT,TEXT_REMOVE,READONLY_CHANGED,CARET_CHANGED
     * 
     * @param eventType the type of internal event to fire
     */
    protected void fireEditorInternalEvent( int eventType ) {
        SqlEditorInternalEvent event = new SqlEditorInternalEvent(this, eventType);
        notifyInternalEventListeners(event);
    }

    /**
     * This method will register the listener for all SqlEditorEvents
     * 
     * @param listener the listener to be registered
     */
    public void addEventListener( EventObjectListener listener ) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        eventListeners.add(listener);
    }

    /**
     * This method will un-register the listener for all SqlEditorEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeEventListener( EventObjectListener listener ) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a SqlEditorEvent
     */
    private void notifyEventListeners( EventObject event ) {
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(event);
                }
            }
        }
    }

    /**
     * This method will register the listener for all SqlEditorInternalEvents
     * 
     * @param listener the listener to be registered
     */
    public void addInternalEventListener( EventObjectListener listener ) {
        if (internalEventListeners == null) {
            internalEventListeners = new ArrayList();
        }
        internalEventListeners.add(listener);
    }

    /**
     * This method will un-register the listener for all SqlEditorEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeInternalEventListener( EventObjectListener listener ) {
        if (internalEventListeners != null) {
            internalEventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a SqlEditorEvent
     */
    private void notifyInternalEventListeners( EventObject event ) {
        if (internalEventListeners != null) {
            Iterator iterator = internalEventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(event);
                }
            }
        }
    }

    /**
     * Get the Command for the currently displayed SQL
     * 
     * @return the command, null if the query is not both parseable and resolvable
     */
    public Command getCommand() {
        return queryDisplayComponent.getCommand();
    }

    public void showMessageArea( final boolean show ) {
        if (!isDisposed()) {
            if (show != messageShowing) {
                if (threadIsNotDisplayThread()) {
                    this.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            if (show) {
                                setMaximizedControl(null);
                                setMessage(currentMessage);
                            } else {
                                setMaximizedControl(sqlViewForm);
                            }
                        }
                    });
                } else {
                    if (show) {
                        setMaximizedControl(null);
                        setMessage(currentMessage);
                    } else {
                        setMaximizedControl(sqlViewForm);
                    }
                }
                messageShowing = !messageShowing;
            }
            fireEditorInternalEvent(SqlEditorInternalEvent.MESSAGE_VISIBILITY_CHANGED);
        }
    }

    public boolean isMessageAreaVisible() {
        return messageShowing;
    }

    /**
     * Set the Text in the Message for a QueryDisplayComponent.
     * 
     * @param displayComponent the QueryDisplayComponent
     */
    void setMessage( QueryDisplayComponent displayComponent ) {
        setMessage(queryDisplayComponent.getStatusMessage());
    }

    /**
     * Set the Text in the Message for a QueryDisplayComponent.
     * 
     * @param displayComponent the QueryDisplayComponent
     */
    public void setMessage( String messageText ) {
        currentMessage = messageText;
        if (!messageArea.isDisposed()) {
            // point the font manager to the current editor's text viewer
            if (threadIsNotDisplayThread()) {
                this.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        if (currentMessage != null) {
                            messageArea.setText(currentMessage);
                        } else {
                            messageArea.setText(""); //$NON-NLS-1$
                        }
                    }
                });
            } else {
                if (currentMessage != null) {
                    messageArea.setText(currentMessage);
                } else {
                    messageArea.setText(""); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Set enabled status of drops into a SELECT on the SQLTextPanel. Default is 'true'
     * 
     * @param status 'true' if drops are enabled, 'false' if not.
     */
    public void setSelectDropsEnabled( boolean status ) {
        selectDropsEnabled = status;
    }

    /**
     * Set enabled status of expansion of the SELECT on the SQLTextPanel
     * 
     * @param status 'true' if expansion is enabled, 'false' if not.
     */
    public void setSelectExpansionEnabled( boolean status ) {
        selectExpansionEnabled = status;
    }

    /**
     * Set the SetQuery reconciled states - whether each part of the SetQuery is reconciled to the target or not. Used for editor
     * panel highlighting.
     * 
     * @param reconciledStates the list of boolean states
     */
    public void setSetQueryReconciledStates( List reconciledStates ) {
        setQueryStates = reconciledStates;
        setTextViewerBackgroundColors(getCaretOffset());
    }

    /**
     * Method to set background color when the query is not parsable or resolvable
     */
    void setQueryTextBackground() {
        StyledText sqlTextArea = sqlTextViewer.getTextWidget();
        if (sqlTextArea != null) {
            Color bkgdColor = widgetBkgdColor;
            if (hasPendingChanges || hasUserError) {
                bkgdColor = colorManager.getColor(ColorManager.BACKGROUND_INVALID);
            } else {
                if (isEditable()) {
                    if (queryDisplayComponent.isValidatable()) {
                        bkgdColor = colorManager.getColor(ColorManager.BACKGROUND_VALID);
                    } else {
                        bkgdColor = colorManager.getColor(ColorManager.BACKGROUND_INVALID);
                    }
                }
            }
            setSqlTextAreaBackgroundColor(bkgdColor);
            // Need to set the Line Background color to the same here because it may have been
            // Set in the OTHER method
            StyledText styledText = this.sqlTextViewer.getTextWidget();
            int textLength = styledText.getText().length();
            // get start and end lines for entire text
            int wholeStartLine = 0;
            int wholeEndLine = styledText.getLineAtOffset(textLength);
            int nAllLines = wholeEndLine - wholeStartLine + 1;
            styledText.setLineBackground(wholeStartLine, nAllLines, bkgdColor);
        }
    }

    private void setSqlTextAreaBackgroundColor( Color bkgdColor ) {
        StyledText sqlTextArea = sqlTextViewer.getTextWidget();
        if (sqlTextArea != null) {
            if (!currentBkgdColor.getRGB().equals(bkgdColor.getRGB())) {
                currentBkgdColor = bkgdColor;
                sqlTextArea.setBackground(bkgdColor);
            }
        }
    }

    /**
     * Sets whether the displayed query can be edited
     * 
     * @param status true if the query can be edited, false if not
     */
    public void setEditable( final boolean status ) {
        // point the font manager to the current editor's text viewer

        isEditable = status;

        if (threadIsNotDisplayThread()) {
            this.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    sqlTextViewer.setEditable(status);
                    setQueryTextBackground();
                }
            });
        } else {
            sqlTextViewer.setEditable(status);
            setQueryTextBackground();
        }

        fireEditorInternalEvent(SqlEditorInternalEvent.READONLY_CHANGED);
    }

    /**
     * Set any external groups that the builder will need for it's treeView groups. Example of this is when the transformation
     * target is a MappingClass. Then, the builders need access to the InputSet groups (nested under MappingClass). This is
     * different than 3.1 because the InputSets are no longer in the Query FROM clause.
     * 
     * @param groups the collection of external groups to add to the builder trees.
     */
    public void setExternalBuilderGroups( Collection groups ) {
        this.externalBuilderGroups = groups;
    }

    /**
     * Enable the SQL optimization.
     * 
     * @param status the desired optimizer status, 'true' = enabled, 'false' = disabled
     */
    public void setOptimizerEnabled( boolean status ) {
        boolean isEnabled = isOptimizerEnabled();
        if (status != isEnabled) {
            queryDisplayComponent.setOptimizerEnabled(status);
            // Fire Editor State to rest of Editor
            fireEditorInternalEvent(SqlEditorInternalEvent.OPTIMIZER_STATE_CHANGED);
        }
    }

    /**
     * Turn the SQL optimization on or off.
     * 
     * @param state the optimizer state, 'true' = on, 'false' = off
     */
    public void setOptimizerOn( boolean status ) {
        // Sets the optimization preference - the panel in turn responds to preference change
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        boolean currentValue = prefStore.getBoolean(UiConstants.Prefs.SQL_OPTIMIZATION_ON);
        if (status != currentValue) {
            prefStore.setValue(UiConstants.Prefs.SQL_OPTIMIZATION_ON, status);
        }

        boolean isEnabled = isOptimizerEnabled();

        boolean isOn = isOptimizerOn();
        if (isEnabled) {
            // If desired state is opposite of current state, set on queryDisplayComponent
            if (status != isOn) {
                queryDisplayComponent.setOptimizerOn(status);
                if (!hasPendingChanges()) {
                    refreshWithDisplayComponent();
                }
                // Fire Editor State to rest of Editor
                fireEditorInternalEvent(SqlEditorInternalEvent.OPTIMIZER_STATE_CHANGED);
            }
        }
    }

    /**
     * Determine if the SQL optimizer is enabled.
     * 
     * @return the optimizer enabled status - 'true' is enabled, 'false' is disabled.
     */
    public boolean isOptimizerEnabled() {
        return queryDisplayComponent.isOptimizerEnabled();
    }

    /**
     * Determine if SQL optimization is on.
     * 
     * @return the optimizer on state - 'true' is on, 'false' is off
     */
    public boolean isOptimizerOn() {
        return queryDisplayComponent.isOptimizerOn();
    }

    /**
     * Determine if the optimizer can be used for the current SQL.
     * 
     * @return 'true' if the optimizer can be used, 'false' is not.
     */
    public boolean canOptimize() {
        return queryDisplayComponent.canOptimize();
    }

    /**
     * Determine whether the displayed query can be edited
     * 
     * @return true if the query can be edited, false if not
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Tests whether the editor panel contains a valid SQL statement. Parsable means that the statement is valid SQL.
     * 
     * @return the boolean status of the SQL statement
     */
    public boolean isParsable() {
        if (hasPendingChanges) {
            return false;
        }
        return queryDisplayComponent.isParsable();
    }

    /**
     * Tests whether the editor panel contains a resolvable sql statment.
     * 
     * @return the boolean status of the SQL statement
     */
    public boolean isResolvable() {
        if (hasPendingChanges) {
            return false;
        }
        return queryDisplayComponent.isResolvable();
    }

    /**
     * Tests whether the editor panel contains a valid sql statment. Valid in this case means that it is parsable, resolvable and
     * validatable SQL.
     * 
     * @return the boolean status of the SQL statement
     */
    public boolean isValid() {
        if (hasPendingChanges) {
            return false;
        }
        return queryDisplayComponent.isValidatable();
    }

    /**
     * Tests whether the editor panel contains the default SQL statement.
     * 
     * @return the boolean status of the SQL statement
     */
    public boolean isDefaultQuery() {
        return queryDisplayComponent.isDefaultQuery();
    }

    /**
     * Method to set whether the editorPanel has errors
     * 
     * @param status true if theres an error, false if not
     */
    public void setHasError( boolean status ) {
        hasUserError = status;
        // Set error background
        setQueryTextBackground();
    }

    /**
     * This method determines whether the Expression Builder Dialog can be launched at the current caret position.
     * 
     * @return 'true' if the ExpressionBuilder can be launched, 'false' if not.
     */
    public boolean canUseExpressionBuilder() {
        // Cannot Use ExpressionBuilder under these conditions (may change)
        if (!isEditable() || !isParsable()) {
            return false;
        }
        // Can use ExpressionBuilder if currently in an expression or its valid to insert new
        int caretIndex = getCorrectedCaretOffset();

        if (isIndexWithin(caretIndex, EXPRESSION) || isInsertAllowed(caretIndex, EXPRESSION)) {
            return true;
        }
        return false;
    }

    /**
     * This method determines whether the Criteria Builder Dialog can be launched at the current caret position.
     * 
     * @return 'true' if the CriteriaBuilder can be launched, 'false' if not.
     */
    public boolean canUseCriteriaBuilder() {
        // Cannot Use CriteriaBuilder under these conditions (may change)
        if (!isEditable() || !isParsable()) {
            return false;
        }
        // Can use CriteriaBuilder if currently in a criteria, if the current command has
        // a WHERE clause, or its valid to insert new
        int caretIndex = getCorrectedCaretOffset();

        if (isIndexWithin(caretIndex, EDITABLE_CRITERIA) || commandHasEditableCriteriaClause(caretIndex)
            || isInsertAllowed(caretIndex, CRITERIA)) {
            return true;
        }
        return false;
    }

    private ExpressionBuilder getExpressionBuilder() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        ExpressionBuilder expressionBuilder = new ExpressionBuilder(shell);
        expressionBuilder.create();
        return expressionBuilder;
    }

    private CriteriaBuilder getCriteriaBuilder() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder(shell);
        criteriaBuilder.create();
        return criteriaBuilder;
    }

    /**
     * This method launches the Expression Builder Dialog. If the current SQLTextPanel caret is currently within an Expression,
     * the Expression Builder is launched with the supplied expression. The supplied expression is replaced with the modified
     * expression when the function builder is dismissed. If the caret is not within an expression, the Function Builder is
     * launched without an expression. The resulting expression is inserted into the editor panel.
     */
    public void showExpressionBuilder() {
        int index = getCorrectedCaretOffset();
        // -----------------------------------------------------------
        // Get Expression at the index
        // -----------------------------------------------------------
        DisplayNode expressionNode = getExpressionAtIndex(index);

        int startIndex = 0;
        int endIndex = 0;
        boolean replaceMode = false;
        // ------------------------------------------------------------------------------
        // If the index is within an expression, replace the expression, else insert it
        // ------------------------------------------------------------------------------
        if (expressionNode != null) {
            startIndex = expressionNode.getStartIndex();
            endIndex = expressionNode.getEndIndex();
            replaceMode = true;
        }

        // load the Groups that are in context into the ElementViewerFactory
        List groups = getGroupsForBuilderTree(true);
        while (groups.contains(null) && groups.size() > 0) {
            groups.remove(null);
        }
        ElementViewerFactory.setViewerInput(groups);

        // ----------------------------------------------------------------------------
        // If the caret is within an expression, construct panel with the expression
        // Otherwise, construct panel for insert
        // ----------------------------------------------------------------------------
        ExpressionBuilder builder = getExpressionBuilder();

        // launch Epression Builder with the selected language object or with
        // null to start off with undefined language object
        builder.setLanguageObject((replaceMode) ? (Expression)expressionNode.getLanguageObject() : null);

        // -------------------------------------------------------------------------
        // Display the Dialog
        // -------------------------------------------------------------------------
        int status = builder.open();

        // -------------------------------------------------------------------------
        // Insert or Replace when Dialog is OK'd, do nothing if cancelled
        // -------------------------------------------------------------------------
        if (status == Window.OK) {
            LanguageObject langObj = builder.getLanguageObject();
            String langString = SQLStringVisitor.getSQLString(langObj);

            // ------------------
            // Replace Mode
            // ------------------
            if (replaceMode) {
                StringBuffer currentSQL = new StringBuffer(queryDisplayComponent.toString());
                currentSQL.replace(startIndex, endIndex + 1, langString);
                setText(currentSQL.toString(), this);
                // ------------------
                // Insert Mode
                // ------------------
            } else {
                // Handle SELECT * (special case - it must be expanded first)
                if (queryDisplayComponent.isIndexWithin(index, DisplayNodeConstants.SELECT)) {
                    // Get Select clause DisplayNode
                    DisplayNode clauseNode = queryDisplayComponent.getQueryClauseAtIndex(index);
                    LanguageObject selectObj;
                    try {
                        selectObj = clauseNode.getLanguageObject();
                        if (selectObj != null && selectObj instanceof Select) {
                            // Check whether this is a "SELECT *"
                            if (((Select)selectObj).isStar()) {
                                // First check if the cursor is after the *
                                boolean isAtClauseEnd = DisplayNodeUtils.isIndexAtClauseEnd(clauseNode, index);
                                // Expand the Select
                                queryDisplayComponent.expandSelect(index);
                                // If the cursor was after the *, adjust the cursor index after expansion
                                if (isAtClauseEnd) {
                                    DisplayNode newClauseNode = queryDisplayComponent.getQueryClauseAtIndex(index);
                                    index = newClauseNode.getEndIndex();
                                }
                            }
                        }

                    } catch (NullPointerException npe) {
                        UiPlugin.getDefault().getPluginUtil().log(IStatus.ERROR, npe, "Clause Node was null at index: " + index); //$NON-NLS-1$
                        UiPlugin.getDefault().getPluginUtil().log(IStatus.ERROR, npe, "Text was: " + getText()); //$NON-NLS-1$
                    }
                }

                // Get currently displayed SQL
                StringBuffer currentSQL = new StringBuffer(queryDisplayComponent.toString());

                // Adjust the original index so that existing symbols arent broken
                int newIndex = adjustIndexForInsert(index);

                // Adjust the expression String to place commas before/after as appropriate
                String newExpressionString = adjustStringForInsert(langString, newIndex);

                // Insert Expression String at the index
                currentSQL.insert(newIndex, newExpressionString);

                // Reset the new SQL Text
                setText(currentSQL.toString(), this);
            }
            setCaretOffset(0);
            // Fires Editor Event based on state of queryDisplayComponent
            // fireEditorEvent();
        }
    }

    /**
     * This method launches the Criteria Builder Dialog. If the current SQLTextPanel caret is currently within a criteria, the
     * Criteria Builder is launched with the supplied criteria. The supplied criteria is replaced with the modified criteria when
     * the builder is dismissed. If the caret is not within a criteria, the Criteria Builder is launched without a criteria. The
     * resulting criteria is inserted into the editor panel.
     * 
     * @param index the index to launch the builder from.
     */
    public void showCriteriaBuilder() {
        int index = getCorrectedCaretOffset();
        // -----------------------------------------------------------
        // Get Criteria at the index
        // -----------------------------------------------------------
        DisplayNode criteriaNode = getCriteriaAtIndex(index, false);
        int startIndex = 0;
        int endIndex = 0;
        boolean replaceMode = false;
        // -------------------------------------------------------------------------
        // If the index is within a criteria, replace the criteria, else insert it
        // -------------------------------------------------------------------------
        if (criteriaNode != null) {
            startIndex = criteriaNode.getStartIndex();
            endIndex = criteriaNode.getEndIndex();
            replaceMode = true;
        }

        // load the Groups that are in context into the ElementViewerFactory
        List groups = getGroupsForBuilderTree(true);

        // this gets into loop if groups is empty
        while (groups.contains(null) && groups.size() > 0) {
            groups.remove(null);
        }
        ElementViewerFactory.setViewerInput(groups);

        // -------------------------------------------------------------------------
        // If the caret is within a criteria, construct panel with the criteria
        // Otherwise, construct panel for insert
        // -------------------------------------------------------------------------
        CriteriaBuilder builder = getCriteriaBuilder();

        // launch Criteria Builder with the selected language object or with
        // null to start off with undefined language object
        builder.setLanguageObject((replaceMode) ? (Criteria)criteriaNode.getLanguageObject() : null);

        // -------------------------------------------------------------------------
        // Display the Dialog
        // -------------------------------------------------------------------------
        int status = builder.open();

        // -------------------------------------------------------------------------
        // Insert or Replace when Dialog is OK'd, do nothing if cancelled
        // -------------------------------------------------------------------------
        if (status == Window.OK) {
            LanguageObject newCriteria = builder.getLanguageObject();
            String criteriaString = SQLStringVisitor.getSQLString(newCriteria);
            // ------------------
            // Replace Mode
            // ------------------
            if (replaceMode) {
                StringBuffer currentSQL = new StringBuffer(queryDisplayComponent.toString());
                currentSQL.replace(startIndex, endIndex + 1, criteriaString);
                setText(currentSQL.toString(), this);
                // --------------------------------------------------------------------------------
                // Insert Mode - We should now only be inserting when there is not a WHERE clause
                // --------------------------------------------------------------------------------
            } else {
                // Reset the insert index to the end of the FROM
                DisplayNode commandNode = queryDisplayComponent.getCommandDisplayNodeAtIndex(index);
                if (commandNode != null) {
                    if (commandNode instanceof QueryDisplayNode) {
                        DisplayNode whereNode = ((QueryDisplayNode)commandNode).getClauseDisplayNode(WHERE);
                        DisplayNode fromNode = ((QueryDisplayNode)commandNode).getClauseDisplayNode(FROM);
                        if (fromNode != null && whereNode == null) {
                            index = fromNode.getEndIndex();
                        }
                    } else if (commandNode instanceof DeleteDisplayNode) {
                        DisplayNode optionNode = ((DeleteDisplayNode)commandNode).getClauseDisplayNode(OPTION);
                        if (optionNode != null) {
                            index = optionNode.getStartIndex();
                        } else {
                            index = commandNode.getEndIndex();
                        }
                    } else if (commandNode instanceof UpdateDisplayNode) {
                        DisplayNode optionNode = ((UpdateDisplayNode)commandNode).getClauseDisplayNode(OPTION);
                        if (optionNode != null) {
                            index = optionNode.getStartIndex();
                        } else {
                            index = commandNode.getEndIndex();
                        }
                    }
                }

                // Get currently displayed SQL
                StringBuffer currentSQL = new StringBuffer(queryDisplayComponent.toString());

                // Insert \n before inserting SQL to preserve \n formatting.
                while ((currentSQL.charAt(index) == '\n') || (currentSQL.charAt(index) == ';')) {
                    index--;
                }

                // Insert Criteria String. Need to add one to index since index is pointing to last character
                // of the display node.
                currentSQL.insert(index + 1, SPACE + WHERE_STR + SPACE + criteriaString);

                setText(currentSQL.toString(), this);
            }
            setCaretOffset(0);
            // Fires Editor Event based on state of queryDisplayComponent
            // fireEditorEvent();
        }
    }

    /**
     * Get the Group symbols for the command that the current cursor is within. This method superscedes the original method
     * getGroupsForBuilderTree() below which was insufficient to filtering or provided appropriate group symbols.
     * 
     * @return List of group symbols in scope for the query that the index is within.
     */
    public List getGroupsForBuilderTree( boolean forExpression ) {
        SqlIndexLocator indexLocator = new SqlIndexLocator(queryDisplayComponent, getCorrectedCaretOffset());
        GroupSymbolFinder finder = new GroupSymbolFinder(indexLocator, externalBuilderGroups);

        return finder.find();
    }

    /**
     * Method to determine if the panel has pending changes to the query. This just returns the status of the saveButton.
     * 
     * @return true if the query has pending changes, false if not.
     */
    public boolean hasPendingChanges() {
        return hasPendingChanges;
    }

    /**
     * Method to insert a group symbol string at the specified index, as the result of a drop. This method will check whether the
     * query isParsable, and warn the user if it is not before dropping.
     * 
     * @param groupName the new group name to insert
     * @param index the index location to insert the group
     */
    public void insertDroppedGroup( String groupName,
                                    int index,
                                    Object source ) {
        // -----------------------------------------------------------------
        // Current SQL is Parsable, can be more rigid about where to drop
        // -----------------------------------------------------------------
        if (queryDisplayComponent.isParsable() && !hasPendingChanges()) {
            List groupList = new ArrayList(1);
            groupList.add(groupName);
            insertGroups(groupList, index, source);
            // -----------------------------------------------------------------
            // Current SQL not Parsable, just do the drop
            // TODO: Add User Warning
            // -----------------------------------------------------------------
        } else {
            // int ans = JOptionPane.showConfirmDialog(this, propMgr.getText("stp.dropGroupWarningMsg"),
            // propMgr.getText("stp.dropWarningDialogTitle"),JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            // if(ans == JOptionPane.YES_OPTION) {
            StringBuffer sb = new StringBuffer(getText());
            sb.insert(index, SPACE + groupName + SPACE);
            setText(sb.toString(), source);
            // fireEditorEvent();
            // }
        }
    }

    /**
     * Method to insert a group symbol string at the specified index. If the clause that the index is in will not accept a group,
     * nothing is done.
     * 
     * @param groupName the new group name to insert
     * @param index the index location to insert the group
     */
    public void insertGroup( String groupName,
                             int index,
                             Object source ) {
        List groupList = new ArrayList(1);
        groupList.add(groupName);
        insertGroups(groupList, index, source);
    }

    /**
     * Method to insert a list of group symbols at the specified index. If the clause that the index is in will not accept a
     * group, nothing is done.
     * 
     * @param groupNames the list of new group names to insert
     * @param index the index location to insert the group
     */
    public void insertGroups( List groupNames,
                              int index,
                              Object source ) {
        eventSource = source;
        // Dont allow insert within Expression - must use ExpressionBuilder
        boolean okToInsert = isInsertOK(index);
        if (!okToInsert) {
            return;
        }

        queryDisplayComponent.insertGroups(groupNames, index);

        // Refresh the EditorPanel, using the queryDisplayComponent
        refreshWithDisplayComponent();

        // Fire Editor Event based on parsable state of query
        // fireEditorEvent();
    }

    /**
     * Method to insert a list of group symbols at the end of the FROM clause.
     * 
     * @param groupNames the list of new group names to insert
     */
    public void insertGroupsAtEndOfFrom( List groupNames,
                                         Object source ) {
        FromDisplayNode fromNode = queryDisplayComponent.getFromDisplayNode();
        if (fromNode != null) {
            int fromEndIndex = fromNode.getEndIndex();
            insertGroups(groupNames, fromEndIndex - 1, source);
        } else if (isDefaultQuery()) {
            String currentQuery = getText();
            int insertIndex = currentQuery.toUpperCase().indexOf(SQLConstants.Reserved.FROM)
                              + SQLConstants.Reserved.FROM.length();
            insertGroups(groupNames, insertIndex, source);
        } else if (getText().trim().length() == 0) {
            StringBuffer sb = new StringBuffer("SELECT * FROM"); //$NON-NLS-1$
            Iterator iter = groupNames.iterator();
            while (iter.hasNext()) {
                String grpName = (String)iter.next();
                sb.append(SPACE + grpName);
                if (iter.hasNext()) {
                    sb.append(COMMA);
                }
            }
            setText(sb.toString(), source);
        }
    }

    /**
     * Method to insert a group symbol string at the end of the FROM clause (if there is a FROM clause).
     * 
     * @param groupName the new group name to insert
     */
    public void insertGroupAtEndOfFrom( String groupName,
                                        Object source ) {
        FromDisplayNode fromNode = queryDisplayComponent.getFromDisplayNode();
        if (fromNode != null) {
            int fromEndIndex = fromNode.getEndIndex();
            insertGroup(groupName, fromEndIndex - 1, source);
        } else if (isDefaultQuery()) {
            String currentQuery = getText();
            int insertIndex = currentQuery.toUpperCase().indexOf(SQLConstants.Reserved.FROM)
                              + SQLConstants.Reserved.FROM.length();
            insertGroup(groupName, insertIndex, source);
        } else if (getText().trim().length() == 0) {
            setText("SELECT * FROM " + groupName, source); //$NON-NLS-1$
        }
    }

    /**
     * Method to insert an element symbol string at the specified index, as the result of a drop. This method will check whether
     * the query isParsable, and warn the user if it is not before dropping.
     * 
     * @param elementName the new element name to insert
     * @param parentName the name of the elements parent
     * @param index the index location to insert the element
     */
    public void insertDroppedElement( String elementName,
                                      String parentName,
                                      int index,
                                      Object source ) {
        // -----------------------------------------------------------------
        // Current SQL is Parsable, can be more rigid about where to drop
        // -----------------------------------------------------------------
        if (queryDisplayComponent.isParsable() && !hasPendingChanges()) {
            List elementList = new ArrayList(1);
            List parentList = new ArrayList(1);
            elementList.add(elementName);
            parentList.add(parentName);
            insertElements(elementList, parentList, index, source);
            // -----------------------------------------------------------------
            // Current SQL not Parsable, just do the drop
            // TODO: Add User Warning
            // -----------------------------------------------------------------
        } else {
            // int ans = JOptionPane.showConfirmDialog(this, propMgr.getText("stp.dropElementWarningMsg"),
            // propMgr.getText("stp.dropWarningDialogTitle"),JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            // if(ans == JOptionPane.YES_OPTION) {
            StringBuffer sb = new StringBuffer(getText());
            sb.insert(index, SPACE + elementName + SPACE);
            setText(sb.toString(), source);
            // }
        }
    }

    /**
     * Method to insert an element symbol string at the specified index. If the clause that the index is in will not accept an
     * element, nothing is done.
     * 
     * @param elementName the new element name to insert
     * @param parentName the name of the elements parent
     * @param index the index location to insert the element
     */
    public void insertElement( String elementName,
                               String parentName,
                               int index,
                               Object source ) {
        List elementList = new ArrayList(1);
        List parentList = new ArrayList(1);
        elementList.add(elementName);
        parentList.add(parentName);
        insertElements(elementList, parentList, index, source);
    }

    /**
     * Method to insert a list of element symbols at the specified index. If the clause that the index is in will not accept an
     * element, nothing is done.
     * 
     * @param elementNames the list of new element names to insert
     * @param parentNames the list of corresponding parents for each element
     * @param index the index location to insert the element
     */
    public void insertElements( List elementNames,
                                List parentNames,
                                int index,
                                Object source ) {
        eventSource = source;
        // Dont allow insert within Expression - must use ExpressionBuilder
        boolean okToInsert = isInsertOK(index);
        if (!okToInsert) {
            return;
        }

        queryDisplayComponent.insertElements(elementNames, parentNames, index);

        // Refresh the EditorPanel, using the queryDisplayComponent
        refreshWithDisplayComponent();

        // Fire Editor Event based on parsable state of query
        // fireEditorEvent();
    }

    /**
     * Method to insert a list of element symbols at the end of the SELECT clause.
     * 
     * @param elementNames the list of new element names to insert
     * @param parentNames the list of corresponding parents for each element
     */
    public void insertElementsAtEndOfSelect( List elementNames,
                                             List parentNames,
                                             Object source ) {
        SelectDisplayNode selectNode = queryDisplayComponent.getSelectDisplayNode();
        if (selectNode != null) {
            int selectEndIndex = selectNode.getEndIndex();
            insertElements(elementNames, parentNames, selectEndIndex + 1, source);
        } else if (isDefaultQuery()) {
            String currentQuery = getText();
            int insertIndex = currentQuery.toUpperCase().indexOf(SQLConstants.Reserved.SELECT)
                              + SQLConstants.Reserved.SELECT.length();
            insertElements(elementNames, parentNames, insertIndex, source);
        } else if (getText().trim().length() == 0) {
            StringBuffer sb = new StringBuffer(SQLConstants.Reserved.SELECT);
            // ------------------------------------------
            // Add the list of Elements to the SELECT
            // ------------------------------------------
            Iterator iter = elementNames.iterator();
            while (iter.hasNext()) {
                String elemName = (String)iter.next();
                sb.append(SPACE + elemName);
                if (iter.hasNext()) {
                    sb.append(COMMA);
                }
            }
            sb.append(SPACE + SQLConstants.Reserved.FROM);
            // ------------------------------------------
            // Add the list of Groupss to the FROM
            // ------------------------------------------
            HashSet uniqueNames = new HashSet();
            uniqueNames.addAll(parentNames);
            iter = uniqueNames.iterator();
            while (iter.hasNext()) {
                String grpName = (String)iter.next();
                sb.append(SPACE + grpName);
                if (iter.hasNext()) {
                    sb.append(COMMA);
                }
            }
            setText(sb.toString(), source);
        }
    }

    /**
     * Method to insert an element symbol string at the end of the SELECT clause.
     * 
     * @param elementName the new element name to insert
     * @param parentName the name of the elements parent
     * @param index the index location to insert the element
     */
    public void insertElementAtEndOfSelect( String elementName,
                                            String parentName,
                                            Object source ) {
        SelectDisplayNode selectNode = queryDisplayComponent.getSelectDisplayNode();
        if (selectNode != null) {
            int selectEndIndex = selectNode.getEndIndex();
            insertElement(elementName, parentName, selectEndIndex - 1, source);
        } else if (isDefaultQuery()) {
            String currentQuery = getText();
            int insertIndex = currentQuery.toUpperCase().indexOf(SQLConstants.Reserved.SELECT)
                              + SQLConstants.Reserved.SELECT.length();
            insertElement(elementName, parentName, insertIndex, source);
        } else if (getText().trim().length() == 0) {
            StringBuffer sb = new StringBuffer(SQLConstants.Reserved.SELECT);
            sb.append(elementName + SPACE + SQLConstants.Reserved.FROM + SPACE + parentName);
            setText(sb.toString(), source);
        }
    }

    /**
     * Check whether it is OK to insert at this location. Will display info message, saying that insert into expression is not
     * allowed.
     * 
     * @param index the index location to check.
     * @return true if the insert is OK, false if not
     */
    public boolean isInsertOK( int index ) {
        // Dont allow insert within Expression - must use ExpressionBuilder
        List nodes = queryDisplayComponent.getDisplayNodesAtIndex(index);
        if (nodes.size() == 1) {
            DisplayNode node = (DisplayNode)nodes.get(0);
            if (DisplayNodeUtils.isWithinSelect(node) && selectDropsEnabled == false) {
                return false;
            } else if (node.isInExpression()) {
                DisplayNode expressionNode = DisplayNodeUtils.getExpressionForNode(node);
                if (expressionNode != null && !(expressionNode instanceof AliasSymbolDisplayNode)) {
                    // JOptionPane.showMessageDialog(this, propMgr.getText("stp.insertExpressionNotAllowedMsg"),
                    // propMgr.getText("stp.insertNotAllowedTitle"),JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }
        } else if (nodes.size() == 2) {
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            if ((DisplayNodeUtils.isWithinSelect(node1) || DisplayNodeUtils.isWithinSelect(node1)) && selectDropsEnabled == false) {
                return false;
            } else if (node1.isInExpression() && node2.isInExpression()) {
                DisplayNode expressionNode = DisplayNodeUtils.getExpressionForNode(node1);
                if (expressionNode != null && !(expressionNode instanceof AliasSymbolDisplayNode)) {
                    // JOptionPane.showMessageDialog(this, propMgr.getText("stp.insertExpressionNotAllowedMsg"),
                    // propMgr.getText("stp.insertNotAllowedTitle"),JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines whether the current caret is within the SELECT clause
     * 
     * @return true if the current caret is within the SELECT clause, false if not
     */
    public boolean isCurrentCaretWithinSelect() {
        return isIndexWithin(getCorrectedCaretOffset(), DisplayNodeConstants.SELECT);
    }

    /**
     * Determines whether the current caret is within the FROM clause
     * 
     * @return true if the current caret is within the FROM clause, false if not
     */
    public boolean isCurrentCaretWithinFrom() {
        return isIndexWithin(getCorrectedCaretOffset(), FROM);
    }

    /**
     * Determines whether the index is anywhere within a DisplayNode of the specified type.
     * 
     * @param index the cursor index
     * @param nodeType the type of DisplayNode
     * @return true if the cursor index is within the specified type, false if not
     */
    public boolean isIndexWithin( int index,
                                  int nodeType ) {
        if (!isParsable()) {
            return false;
        }
        return queryDisplayComponent.isIndexWithin(index, nodeType);
    }

    /**
     * Determines whether the command that the cursor index is within has a Criteria Clause
     * 
     * @param index the cursor index
     * @return true if the command has a Criteria clause, false if not
     */
    public boolean commandHasCriteriaClause( int index ) {
        boolean result = false;
        if (isParsable()) {
            DisplayNode commandNode = queryDisplayComponent.getCommandDisplayNodeAtIndex(index);
            if (commandNode instanceof QueryDisplayNode) {
                if (((QueryDisplayNode)commandNode).getClauseDisplayNode(WHERE) != null) {
                    result = true;
                }
            } else if (commandNode instanceof UpdateDisplayNode) {
                if (((UpdateDisplayNode)commandNode).getClauseDisplayNode(WHERE) != null) {
                    result = true;
                }
            } else if (commandNode instanceof DeleteDisplayNode) {
                if (((DeleteDisplayNode)commandNode).getClauseDisplayNode(WHERE) != null) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Determines whether the current editor command is a Union (SetQuery)
     * 
     * @return true if the command is a Union, false if not
     */
    public boolean isCommandUnion() {
        boolean result = false;
        if (isParsable()) {
            Command command = queryDisplayComponent.getCommand();
            if (command instanceof SetQuery) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets the index of the current Union segment. If the cursor is not currently within a segment, or the QueryDisplayComponent
     * is not a Union query, the returned index is -1.
     * 
     * @return the current segment index, -1 for no segment.
     */
    public int getCurrentUnionCommandSegmentIndex() {
        int index = -1;
        if (isCommandUnion()) {
            // Get the Union Command that the cursor is within
            DisplayNode currentCommandDN = queryDisplayComponent.getCommandDisplayNodeAtIndex(getCorrectedCaretOffset());
            if (currentCommandDN != null && currentCommandDN instanceof QueryDisplayNode) {
                QueryCommand query = (QueryCommand)currentCommandDN.getLanguageObject();
                SetQuery sQuery = (SetQuery)queryDisplayComponent.getCommand();
                List queries = sQuery.getQueryCommands();
                Iterator iter = queries.iterator();
                int indx = 0;
                while (iter.hasNext()) {
                    QueryCommand qc = (QueryCommand)iter.next();
                    if (qc != null && qc.equals(query)) {
                        return indx;
                    }
                    indx++;
                }
            }

        }
        return index;
    }

    public boolean isSubQuerySelected() {
        // Get the Select Command that the cursor is within
        DisplayNode currentCommandDN = queryDisplayComponent.getCommandDisplayNodeAtIndex(getCorrectedCaretOffset());
        if (currentCommandDN != null && currentCommandDN instanceof QueryDisplayNode) {
            QueryCommand query = (QueryCommand)currentCommandDN.getLanguageObject();

            List queries = null;
            Iterator iter = null;
            if (queryDisplayComponent.getCommand() instanceof SetQuery) {
                SetQuery sQuery = (SetQuery)queryDisplayComponent.getCommand();
                queries = sQuery.getQueryCommands();
                iter = queries.iterator();

                while (iter.hasNext()) {
                    QueryCommand qc = (QueryCommand)iter.next();
                    if (qc != null && qc.equals(query)) {
                        return false;
                    } else if (isSubQuery(qc, query)) {
                        return true;
                    }
                }
            } else if (queryDisplayComponent.getCommand() instanceof Query) {
                Query thisQuery = (Query)queryDisplayComponent.getCommand();
                queries = thisQuery.getSubCommands();
                iter = queries.iterator();
                while (iter.hasNext()) {
                    Command nextComm = (Command)iter.next();
                    if (nextComm != null && nextComm.equals(query)) {
                        return true;
                    }
                    if (isSubQuery(nextComm, query)) return true;
                }
            }
        }

        return false;
    }

    public boolean isSubQuery( Command displayCommand,
                               QueryCommand targetCommand ) {
        List queries = new ArrayList();
        if (displayCommand instanceof SetQuery) {
            SetQuery sQuery = (SetQuery)displayCommand;
            queries = sQuery.getQueryCommands();
        } else if (displayCommand instanceof Query) {
            Query thisQuery = (Query)displayCommand;

            List sCommands = thisQuery.getSubCommands();
            if (sCommands != null && !sCommands.isEmpty()) queries.addAll(sCommands);

            Criteria criteria = thisQuery.getCriteria();
            if (criteria instanceof ExistsCriteria) {
                Command command = ((ExistsCriteria)criteria).getCommand();
                if (command != null) {
                    queries.add(command);
                }
            }
        }

        Iterator iter = queries.iterator();

        while (iter.hasNext()) {
            QueryCommand qc = (QueryCommand)iter.next();
            if (qc != null && qc.equals(targetCommand)) {
                return true;
            } else if (isSubQuery(qc, targetCommand)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the command that the cursor index is within an editable Criteria Clause
     * 
     * @param index the cursor index
     * @return true if the command has an editable Criteria clause, false if not
     */
    public boolean commandHasEditableCriteriaClause( int index ) {
        // defect 14321 - Criteria Builder disabled when query has a where clause
        return commandHasCriteriaClause(index) && isEditable();
    }

    /**
     * Determines whether the specified type can be inserted into the current displayComponent at the specified index.
     * 
     * @param index the cursor index
     * @param type the type to insert
     * @return true if the type can be inserted at the index, false if not
     */
    public boolean isInsertAllowed( int index,
                                    int type ) {
        if (!isParsable()) {
            return false;
        }
        return queryDisplayComponent.isInsertAllowed(index, type);
    }

    /**
     * Gets the ExpressionDisplayNode object at the specified position
     * 
     * @return ExpressionDisplayNode at specified index, null if there is none
     */
    public DisplayNode getExpressionAtIndex( int index ) {
        DisplayNode result = null;
        // --------------------------------------------------------------------
        // Get the expression that the index is within, null if there is none.
        // --------------------------------------------------------------------
        if (isParsable()) {
            List displayNodes = queryDisplayComponent.getDisplayNodeList();
            result = DisplayNodeUtils.getNodeTypeAtIndex(displayNodes, index, EXPRESSION);
            // If an AliasSymbol is found, get the aliased expression
            if (result instanceof AliasSymbolDisplayNode) {
                result = result.getChildren().get(0);
            }
        }
        return result;
    }

    /**
     * Gets the CriteriaDisplayNode object at the current caret position. If the criteria is within another criteriaNode, the
     * getOuterMost parameter will walk the heirarchy to get the outermost criteria that the criteria is within.
     * 
     * @param index the cursor index
     * @param getOuterMost 'true' to get outerMost criteria, 'false' if not
     * @return CriteriaDisplayNode at current caret, null if there is none
     */
    public DisplayNode getCriteriaAtIndex( int index,
                                           boolean getOuterMost ) {
        DisplayNode result = null;
        if (isParsable()) {
            // ------------------------------------------------------------------
            // If index is within a criteria, get it.
            // ------------------------------------------------------------------
            List displayNodes = queryDisplayComponent.getDisplayNodeList();
            result = DisplayNodeUtils.getNodeTypeAtIndex(displayNodes, index, CRITERIA);
            // ------------------------------------------------------------------
            // If getOuterMost is desired, walk up through the heirarchy
            // ------------------------------------------------------------------
            if (getOuterMost) {
                while (result != null) {
                    DisplayNode parent = result.getParent();
                    if (parent != null && parent.getLanguageObject() instanceof Criteria) {
                        result = parent;
                    } else {
                        break;
                    }
                }
            }
            // ---------------------------------------------------------------------------
            // If criteria not found yet, look for a WHERE clause in the current command
            // ---------------------------------------------------------------------------
            if (result == null) {
                DisplayNode commandNode = queryDisplayComponent.getCommandDisplayNodeAtIndex(index);
                if (commandNode != null && commandNode instanceof QueryDisplayNode) {
                    DisplayNode clauseNode = ((QueryDisplayNode)commandNode).getClauseDisplayNode(WHERE);
                    if (clauseNode != null) {
                        result = ((WhereDisplayNode)clauseNode).getCriteria();
                        if (DisplayNodeUtils.isEditableCriteria(result)) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<IAction> getActions() {

        /*
         * jh note (8/22/2003): Find and Replace have been removed from this toolbar.
         *                      We are using Eclipse's Find/Replace action (in the Edit menu).
         */

        if (actionList == null) {
            actionList = new ArrayList<IAction>(11);
            if (includedActionsList.contains(ACTION_ID_VALIDATE)) {
                validateAction = new Validate(this);
                actionList.add(validateAction);
            }
            if (includedActionsList.contains(ACTION_ID_LAUNCH_CRITERIA_BUILDER)) {
                launchCriteriaBuilderAction = new LaunchCriteriaBuilder(this);
                actionList.add(launchCriteriaBuilderAction);
            }
            if (includedActionsList.contains(ACTION_ID_LAUNCH_EXPRESSION_BUILDER)) {
                launchExpressionBuilderAction = new LaunchExpressionBuilder(this);
                actionList.add(launchExpressionBuilderAction);
            }
            if (includedActionsList.contains(ACTION_ID_EXPAND_SELECT)) {
                expandSelectAction = new ExpandSelect(this);
                actionList.add(expandSelectAction);
            }
            if (includedActionsList.contains(ACTION_ID_UP_FONT)) {
                upFontAction = new UpFont(this);
                actionList.add(upFontAction);
            }
            if (includedActionsList.contains(ACTION_ID_DOWN_FONT)) {
                downFontAction = new DownFont(this);
                actionList.add(downFontAction);
            }
            if (includedActionsList.contains(ACTION_ID_TOGGLE_MESSAGE)) {
                toggleMessageAction = new ToggleMessage(this);
                actionList.add(toggleMessageAction);
            }
            if (includedActionsList.contains(ACTION_ID_TOGGLE_OPTIMIZER)) {
                toggleOptimizerAction = new ToggleOptimizer(this);
                actionList.add(toggleOptimizerAction);
            }
            if (includedActionsList.contains(ACTION_ID_IMPORT_FROM_FILE)) {
                importFromFileAction = new ImportFromFile(this);
                actionList.add(importFromFileAction);
            }
            if (includedActionsList.contains(ACTION_ID_EXPORT_TO_FILE)) {
                exportToFileAction = new ExportToFile(this);
                actionList.add(exportToFileAction);
            }
        }

        return actionList;
    }

    public static List getDefaultActionList() {
        return new ArrayList(Arrays.asList(DEFAULT_INCLUDED_ACTIONS));
    }

    @Override
    public void dispose() {
        // remove prefStore listener
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        prefStore.removePropertyChangeListener(this);

        super.dispose();
    }

    public int getCaretOffset() {
        return caretOffset;
    }

    public void setCaretOffset( int posn ) {
        // Prevent setting caret outside of current text
        if (posn < 0) {
            posn = 0;
        }
        int textLength = getText().length();
        if (posn > textLength) {
            posn = textLength;
        }
        // set the caret offset
        this.caretOffset = posn;
        sqlTextViewer.getTextWidget().setCaretOffset(posn);
        setTextViewerBackgroundColors(posn);
        fireEditorInternalEvent(SqlEditorInternalEvent.CARET_CHANGED);
    }

    /*
     * Set the TextViewer Background Colors.  If there are sub commands in the editor, the
     * selected command is shown in white, with the rest of the editor grayed out.  Otherwise
     * the editor is completely white.
     * @param caretPosn the current caret offset position
     */

    private void setTextViewerBackgroundColors( int caretPosn ) {
        // If it is NOT EDITABLE, we should do NOTHING here except delegate to the
        // primary bkgd coloring method
        if (!isEditable()) {
            setQueryTextBackground();
            return;
        }

        // Is the caretPosn within a command
        if (isIndexWithin(caretPosn, COMMAND)) {
            // StyledText widget
            StyledText styledText = this.sqlTextViewer.getTextWidget();
            int textLength = styledText.getText().length();

            // Get the Command DisplayNode that its within
            DisplayNode wholeCommandNode = getQueryDisplayComponent().getDisplayNode();

            // Get the Command DisplayNode that its within
            DisplayNode commandNode = getQueryDisplayComponent().getCommandDisplayNodeAtIndex(caretPosn);

            if (!commandNode.equals(wholeCommandNode)) {
                // get start and end lines for entire text
                int wholeStartLine = 0;
                int wholeEndLine = styledText.getLineAtOffset(textLength);
                int nAllLines = wholeEndLine - wholeStartLine + 1;
                // get start and end lines for selected command
                int startIndex = commandNode.getStartIndex();
                int endIndex = commandNode.getEndIndex();
                int startLine = wholeStartLine;
                int endLine = wholeEndLine;
                if (startIndex >= 0 && endIndex <= textLength) {
                    startLine = styledText.getLineAtOffset(startIndex);
                    endLine = styledText.getLineAtOffset(endIndex);
                }
                int nLines = endLine - startLine + 1;
                // set background colors
                setSqlTextAreaBackgroundColor(colorManager.getColor(ColorManager.BACKGROUND_UNFOCUSED));
                styledText.setLineBackground(wholeStartLine, nAllLines, colorManager.getColor(ColorManager.BACKGROUND_UNFOCUSED));
                styledText.setLineBackground(startLine, nLines, colorManager.getColor(ColorManager.BACKGROUND_FOCUSED));
            } else {
                // get start and end lines for entire text
                int wholeStartLine = 0;
                int wholeEndLine = styledText.getLineAtOffset(textLength);
                int nAllLines = wholeEndLine - wholeStartLine + 1;
                setSqlTextAreaBackgroundColor(colorManager.getColor(ColorManager.BACKGROUND_FOCUSED));
                styledText.setLineBackground(wholeStartLine, nAllLines, colorManager.getColor(ColorManager.BACKGROUND_FOCUSED));
            }

            // Union Query Highlighting
            if (wholeCommandNode instanceof SetQueryDisplayNode) {
                // get start and end lines for entire text
                List queryNodes = ((SetQueryDisplayNode)wholeCommandNode).getQueryDisplayNodes();
                if (this.setQueryStates != null && this.setQueryStates.size() == queryNodes.size()) {
                    // Iterate union query segments
                    for (int i = 0; i < queryNodes.size(); i++) {
                        boolean isReconciled = ((Boolean)this.setQueryStates.get(i)).booleanValue();
                        // if current segment is not reconciled, it's highlighted differently
                        if (!isReconciled) {
                            DisplayNode node = (DisplayNode)queryNodes.get(i);
                            // get start and end lines for selected command
                            int startIndex = node.getStartIndex();
                            int endIndex = node.getEndIndex();
                            // ensure that indices are legal
                            if (startIndex >= 0 && endIndex <= textLength) {
                                int startLine = styledText.getLineAtOffset(startIndex);
                                int endLine = styledText.getLineAtOffset(endIndex);
                                int nLines = endLine - startLine + 1;
                                if (node.equals(commandNode)) {
                                    styledText.setLineBackground(startLine,
                                                                 nLines,
                                                                 colorManager.getColor(ColorManager.NON_RECD_UNION_QUERY_FOCUSED));
                                } else {
                                    styledText.setLineBackground(startLine,
                                                                 nLines,
                                                                 colorManager.getColor(ColorManager.NON_RECD_UNION_QUERY_UNFOCUSED));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    void captureCaretInfo() {
        caretOffset = sqlTextViewer.getTextWidget().getCaretOffset();
        caretYPosition = sqlTextViewer.getTextWidget().getLineAtOffset(caretOffset);
        caretXPosition = caretOffset - sqlTextViewer.getTextWidget().getOffsetAtLine(caretYPosition);
        setTextViewerBackgroundColors(caretOffset);
        fireEditorInternalEvent(SqlEditorInternalEvent.CARET_CHANGED);

        notifyCaretChanged();
    }

    /**
     * Public method that ANY call to check if the cursor index is within a given node type needs to use this method. Invisible
     * nodes may be present (see OperationObjectEditorPage) that will make the visible cursor index not representative of the
     * actual index.
     * 
     * @return
     * @since 5.0
     */
    public int getCorrectedCaretOffset() {
        return getCorrectedCaretOffset(caretOffset);
    }

    /**
     * public method that ANY call to check if the cursor index is within a given node type needs to use this method. Invisible
     * nodes may be present (see OperationObjectEditorPage) that will make the visible cursor index not representative of the
     * actual index.
     * 
     * @return
     * @since 5.0
     */
    public int getCorrectedCaretOffset( int visibleCaretOffset ) {
        return queryDisplayComponent.getCorrectedIndex(visibleCaretOffset);
    }

    public TextViewer getTextViewer() {
        return sqlTextViewer;
    }

    public QueryDisplayComponent getQueryDisplayComponent() {
        return queryDisplayComponent;
    }

    public TextFontManager getFontManager() {

        if (tfmManager == null) {
            tfmManager = new TextFontManager(sqlTextViewer, new ScaledFontManager());
        }
        return tfmManager;
    }

    public IUndoManager getUndoManager() {
        return this.textEditor.getUndoManager();
    }

    /**
     * Expand the SELECT of the current query to include all elements. The current query is the query that the cursor is currently
     * within.
     */
    public void expandCurrentSelect() {
        if (canExpandCurrentSelect()) {
            this.eventSource = this;

            int index = getCorrectedCaretOffset();
            queryDisplayComponent.expandSelect(index);

            // Refresh the EditorPanel, using the queryDisplayComponent
            refreshWithDisplayComponent();

            // Fires Editor Event based on state of queryDisplayComponent
            // fireEditorEvent();
        }
    }

    /**
     * Tests whether the editor panel can expand the SELECT of the query that the cursor is currently in. The Query must be a
     * Select *, and the projected symbols of it cannot be zero.
     * 
     * @return true if expandable, false otherwise
     */
    public boolean canExpandCurrentSelect() {
        if (hasPendingChanges || selectExpansionEnabled == false) {
            return false;
        }
        int index = getCorrectedCaretOffset();
        return queryDisplayComponent.canExpandSelect(index);
    }

    /**
     * Adjust the index where a string will be inserted into the query display component. If the arg index is within a word, index
     * will be moved to the end of the word. If the moved index is within an aliased symbol, index will be moved to end of aliased
     * symbol.
     * 
     * @param index the desired index to do the insert
     * @return the adjusted index
     */
    private int adjustIndexForInsert( int index ) {
        if (hasPendingChanges || !queryDisplayComponent.isParsable()) {
            return index;
        }
        // Get the clause that the index is within
        DisplayNode clauseNode = queryDisplayComponent.getQueryClauseAtIndex(index);
        if (clauseNode != null) {
            // ------------------------------------------------------------
            // Get the DisplayNode that the original Index is in
            // ------------------------------------------------------------
            List nodesAtIndex = DisplayNodeUtils.getDisplayNodesAtIndex(clauseNode.getDisplayNodeList(), index);
            int nNodes = nodesAtIndex.size();
            DisplayNode node = null;
            if (nNodes == 1 || nNodes == 2) {
                node = ((DisplayNode)nodesAtIndex.get(0));
            } else {
                return index;
            }
            // ------------------------------------------------------------
            // If the node is within an AliasSymbol, dont break it up
            // ------------------------------------------------------------
            if (node.getParent() != null && node.getParent() instanceof AliasSymbolDisplayNode) {
                node = node.getParent();
            }
            // ------------------------------------------------------------
            // Set the adjusted index after the node
            // ------------------------------------------------------------
            return node.getEndIndex() + 1;
        }
        return index;
    }

    /**
     * Adjusts the string to be inserted based on the position of the index. The string is returned with commas before or after it
     * based on where it is being inserted.
     * 
     * @param insertString the desired string to insert
     * @param index the index location to do the insert
     * @return the adjusted insert string, with leading or trailing commas
     */
    private String adjustStringForInsert( String insertString,
                                          int index ) {
        if (hasPendingChanges || !queryDisplayComponent.isParsable()) {
            return insertString;
        }
        // Get the clause the the index is within
        DisplayNode clauseNode = queryDisplayComponent.getQueryClauseAtIndex(index);
        if (clauseNode != null) {
            // Get list of display nodes for this clause
            List displayNodes = clauseNode.getDisplayNodeList();

            boolean isAtClauseStart = DisplayNodeUtils.isIndexAtClauseStart(clauseNode, index);
            boolean isAtClauseEnd = DisplayNodeUtils.isIndexAtClauseEnd(clauseNode, index);
            boolean isRightBeforeComma = DisplayNodeUtils.isIndexRightBeforeComma(displayNodes, index);
            boolean isRightAfterComma = DisplayNodeUtils.isIndexRightAfterComma(displayNodes, index);
            // ------------------------------------------------------------------------------------
            // If inserting at Start of Clause, use leading space and trailing comma
            // ------------------------------------------------------------------------------------
            if (isAtClauseStart) {
                return SPACE + insertString + COMMA;
                // ------------------------------------------------------------------------------------
                // If inserting right after comma, use trailing comma
                // ------------------------------------------------------------------------------------
            } else if (isRightAfterComma) {
                return insertString + COMMA;
                // ------------------------------------------------------------------------------------
                // If inserting at End of Clause or right before a Comma, use only a leading comma
                // ------------------------------------------------------------------------------------
            } else if (isAtClauseEnd || isRightBeforeComma) {
                return COMMA + insertString;
                // ------------------------------------------------------------------------------------
                // Else just insert the string
                // ------------------------------------------------------------------------------------
            } else {
                return insertString;
            }
        }
        return insertString;
    }

    public static int getVerticalRulerWidth() {
        return VERTICAL_RULER_WIDTH;
    }

    /**
     * Export the current string content of the sql display to a user-selected file
     */
    public void exportToFile() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
        dlg.setText(UiConstants.Util.getString(EXPORT_SQL_DIALOG_TITLE));
        dlg.setFileName(UiConstants.Util.getString(EXPORT_DEFAULT_FILENAME));
        String fileStr = dlg.open();
        // If there is no file extension, add .sql
        if (fileStr != null && fileStr.indexOf('.') == -1) {
            fileStr = fileStr + "." + UiConstants.Util.getString(EXPORT_DEFAULT_FILEEXT); //$NON-NLS-1$
        }
        if (fileStr != null) {
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileStr);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                String sqlText = getText();
                pw.write(sqlText);

            } catch (Exception e) {
                PluginUtil pluginUtil = UiPlugin.getDefault().getPluginUtil();
                String msg = pluginUtil.getString(EXPORT_PROBLEM);
                pluginUtil.log(IStatus.ERROR, e, msg);
            } finally {
                pw.close();
                try {
                    out.close();
                } catch (java.io.IOException e) {
                }
                try {
                    fw.close();
                } catch (java.io.IOException e) {
                }
            }
        }
    }

    /**
     * Import into the sql display from a user-selected file
     */
    public void importFromFile() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setFilterExtensions(new String[] {"*.sql;*.txt", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ 
        dlg.setText(UiConstants.Util.getString(IMPORT_SQL_DIALOG_TITLE));
        String fileStr = dlg.open();
        if (fileStr != null) {
            FileReader fr = null;
            BufferedReader in = null;
            try {
                fr = new FileReader(fileStr);
                in = new BufferedReader(fr);
                String str;
                StringBuffer all = new StringBuffer();
                String delimiter = sqlTextViewer.getTextWidget().getLineDelimiter();
                while ((str = in.readLine()) != null) {
                    all.append(str);
                    all.append(delimiter);
                }
                String sqlText = all.toString();
                setText(sqlText, this);

            } catch (Exception e) {
                PluginUtil pluginUtil = UiPlugin.getDefault().getPluginUtil();
                String msg = pluginUtil.getString(IMPORT_PROBLEM);
                pluginUtil.log(IStatus.ERROR, e, msg);
                String dialogMessage = msg + "\n" + e.getMessage(); //$NON-NLS-1$
                displayError(shell, UiConstants.Util.getString(IMPORT_SQL_PROBLEM_DIALOG_TITLE), dialogMessage);
            } finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (java.io.IOException e) {
                    PluginUtil pluginUtil = UiPlugin.getDefault().getPluginUtil();
                    String msg = pluginUtil.getString(IMPORT_PROBLEM);
                    pluginUtil.log(IStatus.ERROR, e, msg);
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (java.io.IOException e) {
                    PluginUtil pluginUtil = UiPlugin.getDefault().getPluginUtil();
                    String msg = pluginUtil.getString(IMPORT_PROBLEM);
                    pluginUtil.log(IStatus.ERROR, e, msg);
                }

            }
        }
    }

    /**
     * Opens an error dialog to display the given message.
     * 
     * @param message the error message to show
     */
    private void displayError( final Shell shell,
                               final String dialogTitle,
                               final String message ) {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(shell, dialogTitle, message);
            }
        });
    }

    public void widgetSelected( SelectionEvent e ) {
        captureCaretInfo();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        captureCaretInfo();
    }

    public void selectionChanged( SelectionChangedEvent e ) {
    }

    public void mouseUp( MouseEvent e ) {
        captureCaretInfo();
    }

    public void mouseDown( MouseEvent e ) {
    }

    public void mouseDoubleClick( MouseEvent e ) {
        captureCaretInfo();
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        captureCaretInfo();
    }

    // Handler for DocumentEvents -
    // Whenever the document changes, check vs previous SQL unless already pending changes
    class DocumentChangeListener implements IDocumentListener {
        public void documentAboutToBeChanged( DocumentEvent event ) {
        }

        public void documentChanged( DocumentEvent event ) {
            // --------------------------------------------------------------
            // If this is a validation, fire event regardless of SQL
            // --------------------------------------------------------------
            if (validateSelected || isCompleteRefresh) {
                validateSelected = false;
                isCompleteRefresh = false;
                hasPendingChanges = false;
                savedSql = getText();
                fireEditorInternalEvent(SqlEditorInternalEvent.TEXT_RESET);
                fireEditorEvent();
                // --------------------------------------------------------------
                // Determine whether changes Pending need to be fired
                // --------------------------------------------------------------
            } else if (!hasPendingChanges) {
                // Check whether the new Sql has changed
                String newSql = getText();
                boolean sqlChanged = hasSqlChanged(newSql.trim());
                savedSql = newSql;
                // SqlChanged
                if (sqlChanged) {
                    setHasPendingChanges();
                }
            }
        }

        boolean hasSqlChanged( String newSql ) {
            boolean hasChanged = false;
            if (!newSql.equalsIgnoreCase(savedSql.trim())) {
                hasChanged = true;
            }
            return hasChanged;
        }
    }

    public int getCaretYPosition() {
        return this.caretYPosition;
    }

    public int getCaretXPosition() {
        return this.caretXPosition;
    }

    public void setHasPendingChanges() {
        hasPendingChanges = true;
        setMessage(QUERY_CHANGES_PENDING_MESSAGE);
        fireEditorInternalEvent(SqlEditorInternalEvent.TEXT_CHANGED);
        fireEditorEvent();
    }
}
