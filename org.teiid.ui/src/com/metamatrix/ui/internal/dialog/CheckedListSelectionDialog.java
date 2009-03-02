/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.MessageLine;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * CheckedListSelectionDialog is an extension of ListSelectionDialog that allows setting an ISelectionStatusValidator. The
 * validator is sent the checked items in the list whenever the check state changes.
 * 
 * @since 4.2
 */
public class CheckedListSelectionDialog extends SelectionDialog {

    // sizing constants
    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;
    private final static int SIZING_SELECTION_WIDGET_WIDTH = 350;
    
    // the root element to populate the viewer with
    private Object inputElement;

    // providers for populating this dialog
    private ILabelProvider labelProvider;
    private IStructuredContentProvider contentProvider;

    // the visual selection widget group
    CheckboxTableViewer listViewer;

    private ISelectionStatusValidator statusValidator;
    private MessageLine statusLine;

    /**
     * @param parentShell
     * @param input
     * @param contentProvider
     * @param labelProvider
     * @param message
     * @since 4.2
     */
    public CheckedListSelectionDialog( Shell parentShell,
                                       Object input,
                                       IStructuredContentProvider contentProvider,
                                       ILabelProvider labelProvider,
                                       String message ) {
        super(parentShell);
        setTitle(InternalUiConstants.Util.getString("ListSelection.title")); //$NON-NLS-1$
        inputElement = input;
        this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
        if (message != null) setMessage(message);
        else setMessage(InternalUiConstants.Util.getString("ListSelection.message")); //$NON-NLS-1$
    }

    public void setSelectionStatusValidator( ISelectionStatusValidator statusValidator ) {
        this.statusValidator = statusValidator;
    }

    /**
     * Add the selection and deselection buttons to the dialog.
     * 
     * @param composite org.eclipse.swt.widgets.Composite
     */
    private void addSelectionButtons( Composite composite ) {

        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
        buttonComposite.setFont(composite.getFont());
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        buttonComposite.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        composite.setData(data);

        Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, InternalUiConstants.Widgets.SELECT_ALL_BUTTON, false);

        SelectionListener listener = new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                listViewer.setAllChecked(true);
                checkStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, InternalUiConstants.Widgets.DESELECT_ALL_BUTTON, false);

        listener = new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                listViewer.setAllChecked(false);
                checkStatus();
            }
        };
        deselectButton.addSelectionListener(listener);

    }

    /**
     * Visually checks the previously-specified elements in this dialog's list viewer.
     */
    private void checkInitialSelections() {
        Iterator itemsToCheck = getInitialElementSelections().iterator();

        while (itemsToCheck.hasNext())
            listViewer.setChecked(itemsToCheck.next(), true);
    }

    /* (non-Javadoc)
     * Method declared in Window.
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        // WorkbenchHelp.setHelp(shell, IHelpContextIds.LIST_SELECTION_DIALOG);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        // page group
        Composite composite = (Composite)super.createDialogArea(parent);

        Font font = parent.getFont();
        composite.setFont(font);

        createMessageArea(composite);

        listViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        listViewer.getTable().setLayoutData(data);

        listViewer.setLabelProvider(labelProvider);
        listViewer.setContentProvider(contentProvider);
        listViewer.getControl().setFont(font);
        listViewer.setSorter(new ViewerSorter() {});

        addSelectionButtons(composite);

        initializeViewer();

        // initialize page
        if (!getInitialElementSelections().isEmpty()) checkInitialSelections();

        getViewer().addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged( CheckStateChangedEvent event ) {
                checkStatus();
            }
        });

        statusLine = new MessageLine(composite);
        statusLine.setAlignment(SWT.LEFT);
        statusLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusLine.setErrorStatus(null);
        statusLine.setFont(parent.getFont());

        return composite;
    }

    void checkStatus() {
        if (statusValidator != null) {
            setStatus(statusValidator.validate(getViewer().getCheckedElements()));
        }
    }

    /**
     * Returns the viewer used to show the list.
     * 
     * @return the viewer, or <code>null</code> if not yet created
     */
    protected CheckboxTableViewer getViewer() {
        return listViewer;
    }

    /**
     * Initializes this dialog's viewer after it has been laid out.
     */
    private void initializeViewer() {
        listViewer.setInput(inputElement);
    }

    /**
     * The <code>ListSelectionDialog</code> implementation of this <code>Dialog</code> method builds a list of the selected
     * elements for later retrieval by the client and closes this dialog.
     */
    @Override
    protected void okPressed() {

        // Get the input children.
        Object[] children = contentProvider.getElements(inputElement);

        // Build a list of selected children.
        if (children != null) {
            ArrayList list = new ArrayList();
            for (int i = 0; i < children.length; ++i) {
                Object element = children[i];
                if (listViewer.getChecked(element)) list.add(element);
            }
            setResult(list);
        }

        super.okPressed();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
    }

    private void setStatus( IStatus status ) {
        statusLine.setErrorStatus(status);
        if (status == null || status.isOK()) {
            getButton(Window.OK).setEnabled(true);
        } else {
            getButton(Window.OK).setEnabled(false);
        }
    }
}
