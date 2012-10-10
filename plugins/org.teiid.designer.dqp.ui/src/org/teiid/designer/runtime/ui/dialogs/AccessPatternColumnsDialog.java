/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;


/**
 * @since 8.0
 */
public class AccessPatternColumnsDialog extends TitleAreaDialog
    implements DqpUiConstants, ISelectionChangedListener, IChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AccessPatternColumnsDialog.class);

    private final EObject[] accessPatterns;

    private List<String> columnValues;

    private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

    private Map<EObject, PreviewParameterPanel> patternPanelMap = new HashMap<EObject, PreviewParameterPanel>();

    private Composite pnlParams; // parent of all access pattern panels

    private EObject selectedPattern;

    private StackLayout stackLayout;

    private TableViewer viewer;

    /**
     * @since 5.5.3
     */
    public AccessPatternColumnsDialog( Shell parentShell,
                                       Collection<EObject> accessPatterns ) {
        super(parentShell);
        this.accessPatterns = accessPatterns.toArray(new EObject[accessPatterns.size()]);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    EObject[] accessAccessPatterns() {
        return this.accessPatterns;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     * @since 5.5.3
     */
    @Override
    public boolean close() {
        // save column values
        if (getReturnCode() == OK) {
            this.columnValues = this.patternPanelMap.get(getSelectedAccessPattern()).getColumnValues();
        }

        if (this.labelProvider != null) {
            this.labelProvider.dispose();
        }

        return super.close();
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 5.5.3
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(OK).setEnabled(false);

        // set the first selection so that initial validation state is set (doing it here since the selection handler uses OK
        // button)
        this.viewer.setSelection(new StructuredSelection(this.accessPatterns[0]), false);

        return buttonBar;
    }

    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        SashForm splitter = WidgetFactory.createSplitter((Composite)super.createDialogArea(parent), SWT.HORIZONTAL);

        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        // create panel that is a list that holds access patterns
        createList(splitter);

        // create panel that contains one or more other panels that have one or more textfields
        this.pnlParams = WidgetFactory.createGroup(splitter, UTIL.getString(PREFIX + "requiredColumns")); //$NON-NLS-1$
        this.pnlParams.setLayout(this.stackLayout = new StackLayout());
        this.pnlParams.setLayoutData(new GridData(GridData.FILL_BOTH));

        // position the splitter
        splitter.setWeights(new int[] {4, 6});
        splitter.layout();

        // create first panel (others created when needed)
        this.selectedPattern = this.accessPatterns[0];
        getPanel(this.selectedPattern);

        // set title
        setTitle(UTIL.getString(PREFIX + "header")); //$NON-NLS-1$

        return splitter;
    }

    private void createList( Composite parent ) {
        Composite listPanel = WidgetFactory.createGroup(parent, UTIL.getString(PREFIX + "patternsGroupTitle"), GridData.FILL_BOTH); //$NON-NLS-1$

        this.viewer = new TableViewer(listPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        this.viewer.addSelectionChangedListener(this);
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.setContentProvider(new AccessPatternContentProvider());
        this.viewer.setInput(this);

        // configure table
        Table table = this.viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Obtains the <code>EObject</code>s for the columns referenced by the selected <code>AccessPattern</code>. Throws an
     * exception if called when the dialog was cancelled.
     * 
     * @return the columns
     * @since 5.5.3
     */
    public List getColumns() {
        assert (getReturnCode() == OK);
        return ((SqlColumnSetAspect)SqlAspectHelper.getSqlAspect(getSelectedAccessPattern())).getColumns(getSelectedAccessPattern());
    }

    /**
     * Obtains the ordered column values of the selected access pattern. Throws an exception if called when the dialog was
     * cancelled.
     * 
     * @return the column values
     * @since 5.5.3
     */
    public List<String> getColumnValues() {
        assert (getReturnCode() == OK);
        return this.columnValues;
    }

    private PreviewParameterPanel getPanel( EObject accessPattern ) {
        PreviewParameterPanel panel = this.patternPanelMap.get(accessPattern);

        if (panel == null) {
            panel = new PreviewParameterPanel(this.pnlParams, accessPattern);
            panel.addChangeListener(this);
            this.patternPanelMap.put(accessPattern, panel);
        }

        return panel;
    }

    private EObject getSelectedAccessPattern() {
        return this.selectedPattern;
    }

    private PreviewParameterPanel getSelectedPanel() {
        return getPanel(getSelectedAccessPattern());
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 5.5.3
     */
    @Override
	public void selectionChanged( SelectionChangedEvent event ) {
        StructuredSelection selection = (StructuredSelection)this.viewer.getSelection();

        if (selection.isEmpty()) {
            this.selectedPattern = null;
        } else {
            this.selectedPattern = (EObject)selection.getFirstElement();
            setTopControl(getSelectedPanel());
        }

        updateState();
    }

    /**
     * @see org.teiid.core.designer.event.IChangeListener#stateChanged(org.teiid.core.designer.event.IChangeNotifier)
     * @since 5.5.3
     */
    @Override
	public void stateChanged( IChangeNotifier theSource ) {
        updateState();
    }

    private void setTopControl( Control newTopControl ) {
        this.stackLayout.topControl = newTopControl;
        this.pnlParams.layout(true, true);
    }

    private void updateState() {
        IStatus status = getSelectedPanel().getStatus();

        if (status.getSeverity() == IStatus.ERROR) {
            getButton(OK).setEnabled(false);
            setErrorMessage(status.getMessage());
        } else {
            getButton(OK).setEnabled(true);
            setErrorMessage(null);
            setMessage(UTIL.getString(PREFIX + "okMsg")); //$NON-NLS-1$
        }
    }

    class AccessPatternContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 5.5.3
         */
        @Override
		public Object[] getElements( Object inputElement ) {
            return accessAccessPatterns();
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 5.5.3
         */
        @Override
		public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 5.5.3
         */
        @Override
		public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }
    }
}
