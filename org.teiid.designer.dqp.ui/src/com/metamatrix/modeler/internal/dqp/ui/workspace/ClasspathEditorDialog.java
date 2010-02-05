package com.metamatrix.modeler.internal.dqp.ui.workspace;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class ClasspathEditorDialog extends Dialog {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    static final String PREFIX = I18nUtil.getPropertyPrefix(ClasspathEditorDialog.class);

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * Displays a confirmation dialog asking the user to confirm the overwrite of an existing JAR extension module with the
     * specified name. <strong>Caller must be in the UI thread.</strong>
     * 
     * @param jarName the name of the file being overwritten
     * @return <code>true</code> if the user confirms the overwrite
     * @since 6.0.0
     */
    public static boolean showConfirmOverwriteDialog( String jarName ) {
        return WidgetUtil.showConfirmation(UTIL.getString(PREFIX + "addDialog.jarExistsConfirmationMsg", jarName)); //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * All the jars in the extension modules directory.
     * 
     * @since 6.0.0
     */
    private Collection<String> allExtensionJars;

    private Button btnAdd;

    private Button btnAdvanced;

    private Button btnDown;

    private Button btnRemove;

    private Button btnReset;

    private Button btnUp;

    /**
     * The jars currently on the classpath.
     * 
     * @since 6.0.0
     */
    private List<String> classpathJars;

    private final String connectorName;

    /**
     * The value of the classpath before editing.
     * 
     * @since 6.0.0
     */
    private final List<String> originalClasspathJars;

    private TableViewer viewer;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param parent the shell
     * @param connectorName the name of the connector whose classpath is being edited (may not be <code>null</code>)
     * @param classpathJars the names of the jars currently on the classpath (may not be <code>null</code>)
     * @param allExtensionJars the names of all the extension module jars (may not be <code>null</code>)
     * @since 6.0.0
     */
    public ClasspathEditorDialog( Shell parent,
                                  String connectorName,
                                  List<String> classpathJars,
                                  Collection<String> allExtensionJars ) {
        super(parent);

        this.connectorName = connectorName;
        this.classpathJars = classpathJars;
        this.allExtensionJars = allExtensionJars;
        this.originalClasspathJars = new ArrayList<String>(this.classpathJars);

        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#close()
     * @since 6.0.0
     */
    @Override
    public boolean close() {
        boolean closed = super.close();

        // make sure if cancel was pressed that the call to get classpath jars does not return anything
        if (getReturnCode() != Window.OK) {
            this.classpathJars = Collections.emptyList();
        }

        return closed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 6.0.0
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
    }

    private void constructButtonPanel( Composite parent ) {
        Composite pnlButtons = WidgetFactory.createPanel(parent);

        // add
        this.btnAdd = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "addButton"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnAdd.setToolTipText(UTIL.getString(PREFIX + "addButton.toolTip")); //$NON-NLS-1$
        this.btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAdd();
            }
        });

        // up
        this.btnUp = WidgetFactory.createButton(pnlButtons, GridData.FILL_HORIZONTAL);
        this.btnUp.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.UP));
        this.btnUp.setToolTipText(UTIL.getString(PREFIX + "upButton.toolTip")); //$NON-NLS-1$
        // this.btnUp.setEnabled(false);
        this.btnUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleMoveUp();
            }
        });

        // down
        this.btnDown = WidgetFactory.createButton(pnlButtons, GridData.FILL_HORIZONTAL);
        this.btnDown.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.DOWN));
        this.btnDown.setToolTipText(UTIL.getString(PREFIX + "downButton.toolTip")); //$NON-NLS-1$
        // this.btnDown.setEnabled(false);
        this.btnDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleMoveDown();
            }
        });

        // remove
        this.btnRemove = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "removeButton"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnRemove.setToolTipText(UTIL.getString(PREFIX + "removeButton.toolTip")); //$NON-NLS-1$
        // this.btnRemove.setEnabled(false);
        this.btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemove();
            }
        });

        // advanced
        this.btnAdvanced = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "advancedButton"), //$NON-NLS-1$
                                                      GridData.FILL_HORIZONTAL);
        this.btnAdvanced.setToolTipText(UTIL.getString(PREFIX + "advancedButton.toolTip")); //$NON-NLS-1$
        this.btnAdvanced.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAdvanced();
            }
        });
    }

    private void constructClasspathPanel( Composite parent ) {
        Composite pnlJars = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        this.viewer = new TableViewer(pnlJars, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleJarSelection();
            }
        });
        this.viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText( Object element ) {
                return new File(element.toString()).getName();
            }
        });
        this.viewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements( Object inputElement ) {
                return getClasspathJars();
            }

            @Override
            public void dispose() {
                // nothing to do
            }

            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }

        });
        this.viewer.setInput(this);

        // configure table
        Table table = this.viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    private void constructMessage( Composite parent ) {
        StyledText st = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.NO_FOCUS | SWT.WRAP);
        st.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        st.setCaret(null);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessVerticalSpace = false;
        gd.horizontalIndent = 4;
        gd.verticalIndent = 8;
        st.setLayoutData(gd);

        String txt = UTIL.getString(PREFIX + "message", this.connectorName); //$NON-NLS-1$
        int start = txt.indexOf('"');
        txt = txt.substring(0, start) + txt.substring(start + 1);
        int end = txt.indexOf('"');
        st.setText(txt.substring(0, end) + txt.substring(end + 1));

        StyleRange styleRange = new StyleRange();
        styleRange.start = start;
        styleRange.length = end - start;
        styleRange.fontStyle = SWT.BOLD;
        st.setStyleRange(styleRange);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        constructMessage(parent);

        Composite pnlMain = WidgetFactory.createPanel((Composite)super.createDialogArea(parent),
                                                      SWT.NONE,
                                                      GridData.FILL_BOTH,
                                                      1,
                                                      2);

        WidgetFactory.createLabel(pnlMain, GridData.HORIZONTAL_ALIGN_BEGINNING, 2, UTIL.getString(PREFIX + "jarListLabel")); //$NON-NLS-1$

        constructClasspathPanel(pnlMain);
        constructButtonPanel(pnlMain);

        this.btnReset = WidgetFactory.createButton(pnlMain, UTIL.getString(PREFIX + "resetButton"), SWT.NONE, 2); //$NON-NLS-1$
        this.btnReset.setToolTipText(UTIL.getString(PREFIX + "resetButton.toolTip")); //$NON-NLS-1$
        // this.btnReset.setEnabled(false);
        this.btnReset.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleReset();
            }
        });

        // set initial state
        refresh();

        return pnlMain;
    }

    /**
     * @return a list of extension jar names that are currently not on the classpath
     * @since 6.0.0
     */
    private Collection<String> getAvailableJars() {
        Collection<String> availableJars = new ArrayList<String>(this.allExtensionJars);
        availableJars.removeAll(this.classpathJars);
        return availableJars;
    }

    /**
     * @return the jar names currently on the classpath in this editor
     * @since 6.0.0
     */
    public String[] getClasspathJars() {
        return this.classpathJars.toArray(new String[this.classpathJars.size()]);
    }

    /**
     * Handler for when the add button is pressed.
     * 
     * @since 6.0.0
     */
    void handleAdd() {
        // show file chooser here
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.PRIMARY_MODAL | SWT.MULTI);
        dialog.setText(UTIL.getString(PREFIX + "addDialog.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(new String[] {'*' + FileUtil.Extensions.JAR});
        dialog.setFilterNames(new String[] {UTIL.getString(PREFIX + "addDialog.filterNames")}); //$NON-NLS-1$

        // show dialog
        dialog.open();
        String[] jarNames = dialog.getFileNames();
        String filterPath = dialog.getFilterPath();

        if (jarNames.length > 0) {
            for (String jarName : jarNames) {
                processAddedJar(jarName, filterPath);
            }

            refresh();
        }
    }

    /**
     * Handler for when the advanced button is pressed.
     * 
     * @since 6.0.0
     */
    void handleAdvanced() {
        // show dialog
        AvailableJarsDialog dialog = new AvailableJarsDialog(getShell(), getAvailableJars());

        if (dialog.open() == Window.OK) {
            this.classpathJars.addAll(dialog.getSelectedJars());
            refresh();
        }
    }

    /**
     * Handler for when a jar name is selected in the classpath table
     * 
     * @since 6.0.0
     */
    void handleJarSelection() {
        // just update button enablement
        updateState();
    }

    /**
     * Handler for when the down button is pressed.
     * 
     * @since 6.0.0
     */
    void handleMoveDown() {
        int[] indices = this.viewer.getTable().getSelectionIndices();
        Arrays.sort(indices);

        // first pass: find lowest index
        for (int i = indices.length - 1; i >= 0; --i) {
            this.classpathJars.set(indices[i], this.classpathJars.set(indices[i] + 1, this.classpathJars.get(indices[i])));
        }

        refresh();
    }

    /**
     * Handler for when the up button is pressed.
     * 
     * @since 6.0.0
     */
    void handleMoveUp() {
        int[] indices = this.viewer.getTable().getSelectionIndices();
        Arrays.sort(indices);

        for (int index : indices) {
            this.classpathJars.set(index, this.classpathJars.set(index - 1, this.classpathJars.get(index)));
        }

        refresh();
    }

    /**
     * Handler for when the remove button is pressed.
     * 
     * @since 6.0.0
     */
    void handleRemove() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        for (Iterator itr = selection.toList().iterator(); itr.hasNext();) {
            this.classpathJars.remove(itr.next());
        }

        refresh();
    }

    /**
     * Handler for when the remove button is pressed.
     * 
     * @since 6.0.0
     */
    void handleReset() {
        if (WidgetUtil.showConfirmation(UTIL.getString(PREFIX + "resetConfirmationMsg"))) { //$NON-NLS-1$
            this.classpathJars = new ArrayList<String>(this.originalClasspathJars);
            refresh();
        }
    }

    /**
     * @param jarName the name of the jar being added
     * @param directory the directory of where the jar is located
     * @since 6.0.0
     */
    private void processAddedJar( String jarName,
                                  String directory ) {
        boolean processed = false;
        String jarPath = directory + File.separator + jarName;

        // check to see if a jar with that name is already on the classpath
        // jars may have a path if they were added from the file system
        for (String name : this.classpathJars) {
            if (new File(name).getName().equals(jarName)) {
                if (showConfirmOverwriteDialog(jarName)) {
                    // user wants to overwrite
                    this.classpathJars.remove(name);
                    this.classpathJars.add(jarPath);
                }

                processed = true;
                break;
            }
        }

        // check to see if jar name conflicts with jars already available in the extensions folder
        if (!processed) {
            // extension jars will never have a path so just look at name
            if (this.allExtensionJars.contains(jarName)) {
                if (showConfirmOverwriteDialog(jarName)) {
                    this.classpathJars.add(jarPath);
                }

                processed = true;
            }
        }

        // no conflicts just add it to classpath
        if (!processed) {
            this.classpathJars.add(jarPath);
        }
    }

    private void refresh() {
        this.viewer.refresh();
        updateState();
    }

    private void updateState() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        if (selection.isEmpty()) {
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
            btnRemove.setEnabled(false);
        } else {
            int maxIndex = (this.classpathJars.size() - 1);
            boolean enableUp = true;
            boolean enableDown = true;

            for (int index : this.viewer.getTable().getSelectionIndices()) {
                // disable up if first item is selected
                if (index == 0) {
                    enableUp = false;
                }

                // disable down if last item is selected
                if (index == maxIndex) {
                    enableDown = false;
                }

                if (!enableUp && !enableDown) break;
            }

            btnUp.setEnabled(enableUp);
            btnDown.setEnabled(enableDown);
            btnRemove.setEnabled(true);
        }

        // reset is enabled if the classpath jars are the same as the original and in the same order
        boolean enableReset = (this.classpathJars.size() != this.originalClasspathJars.size());

        if (!enableReset) {
            for (int size = this.classpathJars.size(), i = 0; i < size; ++i) {
                if (!this.classpathJars.get(i).equals(this.originalClasspathJars.get(i))) {
                    enableReset = true;
                    break;
                }
            }
        }

        btnReset.setEnabled(enableReset);

        // disable advanced if no more available jars
        btnAdvanced.setEnabled(!getAvailableJars().isEmpty());

        // put focus back on table
        this.viewer.getControl().setFocus();
    }

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    private class AvailableJarsDialog extends Dialog {

        // =======================================================================================================================
        // Inner Fields (AvailableJarsDialog)
        // =======================================================================================================================

        private Collection<String> availableJars;

        private org.eclipse.swt.widgets.List list;

        private Collection<String> selectedJars;

        // =======================================================================================================================
        // Inner Constructors (AvailableJarsDialog)
        // =======================================================================================================================

        public AvailableJarsDialog( Shell parent,
                                    Collection<String> availableJars ) {
            super(parent);

            this.availableJars = availableJars;
            this.selectedJars = Collections.emptyList();

            setShellStyle(getShellStyle() | SWT.RESIZE);
        }

        // =======================================================================================================================
        // Inner Methods (AvailableJarsDialog)
        // =======================================================================================================================

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#close()
         * @since 6.0.0
         */
        @Override
        public boolean close() {
            boolean closed = super.close();

            // make sure if cancel was pressed that the call to get selected jars does not return anything
            if (getReturnCode() != Window.OK) {
                this.selectedJars = Collections.emptyList();
            }

            return closed;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         * @since 6.0.0
         */
        @Override
        protected void configureShell( Shell newShell ) {
            super.configureShell(newShell);
            newShell.setText(UTIL.getString(PREFIX + "availableJarsDialog.title")); //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
         * @since 6.0.0
         */
        @Override
        protected Button createButton( Composite parent,
                                       int id,
                                       String label,
                                       boolean defaultButton ) {
            Button btn = super.createButton(parent, id, label, defaultButton);

            // disable OK initially
            if (id == Window.OK) {
                btn.setEnabled(false);
            }

            return btn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         * @since 6.0.0
         */
        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite pnlMain = WidgetFactory.createPanel((Composite)super.createDialogArea(parent), SWT.NONE, GridData.FILL_BOTH);
            ((GridLayout)pnlMain.getLayout()).verticalSpacing = 10;

            // construct message
            StyledText st = new StyledText(pnlMain, SWT.READ_ONLY | SWT.MULTI | SWT.NO_FOCUS | SWT.WRAP);
            st.setText(UTIL.getString(PREFIX + "availableJarsDialog.message")); //$NON-NLS-1$
            st.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            st.setCaret(null);
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.grabExcessVerticalSpace = false;
            gd.horizontalIndent = 0;
            gd.verticalIndent = 10;
            st.setLayoutData(gd);

            // construct list
            this.list = new org.eclipse.swt.widgets.List(pnlMain, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            this.list.setLayoutData(new GridData(GridData.FILL_BOTH));
            this.list.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleJarSelected();
                }
            });
            this.list.setItems(this.availableJars.toArray(new String[this.availableJars.size()]));

            return pnlMain;
        }

        /**
         * @return the JAR names the user wants added to the classpath (never <code>null</code> and never empty)
         * @since 6.0.0
         */
        public Collection<String> getSelectedJars() {
            return this.selectedJars;
        }

        void handleJarSelected() {
            this.selectedJars = Arrays.asList(this.list.getSelection());
            int count = this.list.getSelectionCount();
            getButton(Window.OK).setEnabled(count != 0);
        }
    }
}
