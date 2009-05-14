/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.EObjectSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspacePanel;
import com.metamatrix.modeler.internal.ui.viewsupport.PropertiesDialog;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * @since 4.2
 */
public class ModelObjectFinderDialog extends SelectionStatusDialog implements IFinderHostDialog {

    private static final String TITLE = UiConstants.Util.getString("ModelObjectFinderDialog.title"); //$NON-NLS-1$
    private static final String PROPERTIES_BUTTON_TEXT = UiConstants.Util.getString("ModelObjectFinderDialog.propertiesButton.text"); //$NON-NLS-1$

    private int PROPERTIES_BUTTON_ID = 10234;
    private Button btnProperties;
    private Object[] oselectedObjects;

    private ISelectionStatusValidator validator;
    private int iReturnCode = Window.CANCEL;

    private TabFolder tabFolder;

    private TabItem tiWorkspaceTab;
    private ModelWorkspacePanel pnlWorkspace;

    private TabItem tiFinderTab;
    private ModelObjectSelectionPanel pnlFinder;

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public ModelObjectFinderDialog( Shell shell ) {
        super(shell);

        init();
    }

    private void init() {

        this.setTitle(TITLE);
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);

        setStatusLineAboveButtons(true);
    }

    private ISelectionStatusValidator getValidator() {
        if (validator == null) {
            validator = new EObjectSelectionValidator();
        }
        return validator;
    }

    /**
     * Sets the Validator for this dialog's TreeViewer
     * 
     * @param filter
     */
    public void setValidator( ISelectionStatusValidator validator ) {
        this.validator = validator;

        updateValidators();
    }

    public void updateValidators() {
        if (pnlFinder != null) {
            pnlFinder.setValidator(getValidator());
        }

        if (pnlWorkspace != null) {
            pnlWorkspace.setValidator(getValidator());
        }
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite composite = (Composite)super.createDialogArea(parent);

        createTabbedPane(composite);

        updateValidators();

        return composite;
    }

    private void createTabbedPane( Composite parent ) {

        // create Tab Folder
        tabFolder = new TabFolder(parent, SWT.TOP);
        GridLayout gridLayout = new GridLayout();
        tabFolder.setLayout(gridLayout);

        GridData gridData1 = new GridData(GridData.FILL_BOTH);
        tabFolder.setLayoutData(gridData1);

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {

                IFinderPanel fpnl = getCurrentPanel();
                fpnl.updateOKStatus();
                resetEnabledState();
            }
        });

        // Workspace Tab
        tiWorkspaceTab = new TabItem(tabFolder, SWT.NONE);
        createWorkspacePanel(tabFolder);
        tiWorkspaceTab.setControl(pnlWorkspace);

        // EObjectFinder Tab
        tiFinderTab = new TabItem(tabFolder, SWT.NONE);
        createFinderPanel(tabFolder);
        tiFinderTab.setControl(pnlFinder);
    }

    private void createWorkspacePanel( Composite parent ) {
        pnlWorkspace = new ModelWorkspacePanel(parent, this);
        GridLayout gridLayout = new GridLayout();
        pnlWorkspace.setLayout(gridLayout);

        GridData gridData1 = new GridData(GridData.FILL_BOTH);
        pnlWorkspace.setLayoutData(gridData1);

        tiWorkspaceTab.setText(pnlWorkspace.getTitle());
        tiWorkspaceTab.setToolTipText(pnlWorkspace.getTitle());

        pnlWorkspace.setValidator(getValidator());
    }

    private void createFinderPanel( Composite parent ) {
        pnlFinder = new ModelObjectSelectionPanel(parent, this, false);

        GridLayout gridLayout = new GridLayout();
        pnlFinder.setLayout(gridLayout);
        GridData gridData1 = new GridData(GridData.FILL_BOTH);
        pnlFinder.setLayoutData(gridData1);

        tiFinderTab.setText(pnlFinder.getTitle());
        tiFinderTab.setToolTipText(pnlFinder.getTitle());

    }

    /* (non-Javadoc)
     * Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        // add our Properties button first
        btnProperties = createButton(parent, PROPERTIES_BUTTON_ID, PROPERTIES_BUTTON_TEXT, false);

        btnProperties.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                computeResult();
                Object oSelectedObject = getResult()[0];
                if (oSelectedObject != null && oSelectedObject instanceof EObject) {
                    PropertiesDialog dlg = new PropertiesDialog((EObject)oSelectedObject, null);
                    dlg.open();
                }
            }
        });

        // then add any buttons the panels want to contribute
        ((IFinderPanel)pnlWorkspace).createButtonsForButtonBar(parent);

        ((IFinderPanel)pnlFinder).createButtonsForButtonBar(parent);

        // then the OK and Cancel buttons
        super.createButtonsForButtonBar(parent);
    }

    void resetEnabledState() {
        btnProperties.setEnabled(getButton(IDialogConstants.OK_ID).getEnabled());
    }

    IFinderPanel getCurrentPanel() {
        TabItem ti = getCurrentTab();
        return (IFinderPanel)ti.getControl();

    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {

        IFinderPanel fpnl = getCurrentPanel();
        Object[] result = fpnl.getResult();

        setTheResult(result);
    }

    private void setTheResult( Object[] result ) {
        oselectedObjects = result;
    }

    @Override
    public Object[] getResult() {
        return oselectedObjects;
    }

    private TabItem getCurrentTab() {
        int iIndex = tabFolder.getSelectionIndex();
        return tabFolder.getItem(iIndex);
    }

    // =================================
    // interface: IFinderHostDialog
    // =================================
    @Override
    public Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }

    @Override
    public Button getCancelButton() {
        return getButton(IDialogConstants.CANCEL_ID);
    }

    @Override
    public void okPressed() {
        super.okPressed();
        iReturnCode = Window.OK;
    }

    @Override
    public void cancelPressed() {
        super.cancelPressed();
        iReturnCode = Window.CANCEL;
    }

    @Override
    public Button createButton( Composite parent,
                                int iId,
                                String sText,
                                boolean isDefault ) {
        return super.createButton(parent, iId, sText, isDefault);
    }

    public void updateTheStatus( IStatus status ) {
        this.updateStatus(status);
        computeResult();
        resetEnabledState();
    }

    @Override
    public int getReturnCode() {
        return iReturnCode;
    }

}
