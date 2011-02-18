/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import com.metamatrix.modeler.tools.textimport.ui.TextImportContributionManager;
import com.metamatrix.modeler.tools.textimport.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.WrappingLabel;

/**
 * @since 4.2
 */
public class ImportTextMainPage extends WizardDataTransferPage implements UiConstants {

    // widgets
    protected Combo importTypeCombo;
    private ListViewer expectedFormatListViewer;
    private WrappingLabel descriptionLabel;

    // A boolean to indicate if the user has typed anything
    boolean entryChanged = false;
    private boolean initializing = false;

    // dialog store id constants
    private static final String I18N_PREFIX = "ImportTextMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private final static String STORE_IMPORT_TYPE_SELECTION_ID = getString("storeImportTypeSelectionId");//$NON-NLS-1$

    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    /**
     * Creates an instance of this class
     * 
     * @param selection IStructuredSelection
     */
    public ImportTextMainPage( IStructuredSelection selection ) {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
    }

    /**
     * The <code>WizardResourceImportPage</code> implementation of this <code>WizardDataTransferPage</code> method returns
     * <code>true</code>. Subclasses may override this method.
     */
    @Override
    protected boolean allowNewContainerName() {
        return true;
    }

    /**
     * Handle all events and enablements for widgets in this dialog
     * 
     * @param event Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            if (event.widget == importTypeCombo) {
                validate = true;
            }

            if (validate) setCompletionStatus();

            updateWidgetEnablements();
        }
    }

    /**
     * Creates a new button with the given id.
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates a standard push button, registers for selection
     * events including button presses and registers default buttons with its shell. The button id is stored as the buttons client
     * data. Note that the parent's layout is assumed to be a GridLayout and the number of columns in this layout is incremented.
     * Subclasses may override.
     * </p>
     * 
     * @param parent the parent composite
     * @param id the id of the button (see <code>IDialogConstants.*_ID</code> constants for standard dialog button ids)
     * @param label the label from the button
     * @param defaultButton <code>true</code> if the button is to be the default button, and <code>false</code> otherwise
     */
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        // increment the number of columns in the button bar
        ((GridLayout)parent.getLayout()).numColumns++;

        Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
        button.setLayoutData(buttonData);

        button.setData(new Integer(id));
        button.setText(label);

        if (defaultButton) {
            Shell shell = parent.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
            button.setFocus();
        }
        return button;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite parent ) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createImportTypeGroup(composite);

        createDescriptionGroup(composite);

        createExpectedFormatListGroup(composite);

        restoreWidgetValues();
        updateWidgetEnablements();

        setPageComplete(true);
        setMessage(INITIAL_MESSAGE);
        setControl(composite);
    }

    /**
     * Method to create description group for displaying description
     * 
     * @param parent
     * @since 4.2
     */
    private void createDescriptionGroup( Composite parent ) {
        descriptionLabel = WidgetFactory.createWrappingLabel(parent, GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        descriptionLabel.setFont(parent.getFont());
    }

    /**
     * Create the group for creating the root directory
     */
    protected void createImportTypeGroup( Composite parent ) {
        Composite typeContainerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        typeContainerGroup.setLayout(layout);
        typeContainerGroup.setFont(parent.getFont());
        typeContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label importTypeLabel = new Label(typeContainerGroup, SWT.NONE);
        importTypeLabel.setText(getString("importTypeLabel")); //$NON-NLS-1$
        importTypeLabel.setFont(parent.getFont());

        // source name entry field
        importTypeCombo = new Combo(typeContainerGroup, SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        importTypeCombo.setLayoutData(data);
        importTypeCombo.setFont(parent.getFont());
        String[] importTypes = TextImportContributionManager.getTypes();

        for (int i = 0; i < importTypes.length; i++) {
            importTypeCombo.add(importTypes[i]);
        }

        importTypeCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                updateFromComboSelection();
                Composite comp = (Composite)getControl();
                comp.layout();
                setCompletionStatus();
            }
        });

        importTypeCombo.addKeyListener(new KeyListener() {

            /*
             * @see KeyListener.keyPressed
             */
            public void keyPressed( KeyEvent e ) {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;
            }

            /*
             * @see KeyListener.keyReleased
             */
            public void keyReleased( KeyEvent e ) {
            }
        });

        importTypeCombo.addFocusListener(new FocusListener() {

            /*
             * @see FocusListener.focusGained(FocusEvent)
             */
            public void focusGained( FocusEvent e ) {
                // Do nothing when getting focus
            }

            /*
             * @see FocusListener.focusLost(FocusEvent)
             */
            public void focusLost( FocusEvent e ) {
                // Clear the flag to prevent constant update
                if (entryChanged) {
                    entryChanged = false;
                }

            }
        });
    }

    /**
     * Method to create List box control group for displaying current zip file project list.
     * 
     * @param parent
     * @since 4.2
     */
    private void createExpectedFormatListGroup( Composite parent ) {
        Label messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setText(getString("expectedFormatListMessage")); //$NON-NLS-1$
        messageLabel.setFont(parent.getFont());

        expectedFormatListViewer = new ListViewer(parent);
        GridData data = new GridData(GridData.FILL_BOTH);
        expectedFormatListViewer.getControl().setLayoutData(data);
    }

    protected void updateFromComboSelection() {
        loadDescriptionText(importTypeCombo.getText());
        clearExpectedFormatListViewer();
        loadExpectedFormatList(importTypeCombo.getText());
    }

    public String getImportType() {
        return importTypeCombo.getText();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
     * @since 4.2
     */
    @Override
    public void setMessage( String newMessage ) {
        super.setMessage(newMessage);
    }

    boolean setCompletionStatus() {
        boolean isValid = true;
        if (isValid) {
            setErrorMessage(null);
            setMessage(INITIAL_MESSAGE);
            setPageComplete(true);
            return true;
        }

        setPageComplete(false);
        return false;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is
     * returned then the wizard will not close.
     * 
     * @return boolean
     */
    public boolean finish() {

        saveWidgetValues();

        return true;
    }

    private void clearExpectedFormatListViewer() {
        org.eclipse.swt.widgets.List contents = expectedFormatListViewer.getList();
        String[] items = contents.getItems();
        expectedFormatListViewer.remove(items);
    }

    private void loadDescriptionText( String typeStr ) {
        if (typeStr != null && typeStr.length() > 0) {
            String desc = TextImportContributionManager.getDescription(typeStr);
            if (desc != null) {
                descriptionLabel.setText(desc);
            }
        }
    }

    private void loadExpectedFormatList( String typeStr ) {
        if (typeStr != null && typeStr.length() > 0) {
            String sampleData = TextImportContributionManager.getSampleData(typeStr);
            if (sampleData != null) {
                expectedFormatListViewer.add(parseList(sampleData));
            }
        }
    }

    /**
     * Parses the vertical bar separated string into an array of strings
     * 
     * @return list
     */
    private static String[] parseList( String listString ) {
        List list = new ArrayList(10);
        StringTokenizer tokenizer = new StringTokenizer(listString, "|"); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            int comboIndex;
            try {
                comboIndex = settings.getInt(STORE_IMPORT_TYPE_SELECTION_ID);
            } catch (NumberFormatException e) {
                // Default - choose first combo index
                comboIndex = 0;
            }
            importTypeCombo.select(comboIndex);
            updateFromComboSelection();
        } else {
            importTypeCombo.select(0);
            updateFromComboSelection();
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    @Override
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // Save ComboBox Selection Index
            settings.put(STORE_IMPORT_TYPE_SELECTION_ID, importTypeCombo.getSelectionIndex());
        }
    }
}
