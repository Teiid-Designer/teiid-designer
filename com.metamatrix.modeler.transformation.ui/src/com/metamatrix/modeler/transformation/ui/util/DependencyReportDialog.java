/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.io.File;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * The <code>DependencyReportDialog</code> is the UI which obtains information used to generate and save a dependency report.
 * 
 * @since 4.2
 */
public class DependencyReportDialog extends Dialog implements UiConstants {

    /*-------- DESIGN NOTES -----------------------------------------------------------------------
     10/26/04 Dan F - When class initially written it was thought to pass in file extensions to
     the FileDialog. But FileDialogdoes not have an API to get the selected file
     extension. I was going to add the selected extension if the user did not
     explicitly add an extension. There is an Eclipse bug (4560) that addresses
     this deficiency. Code pertaining to extensions and dealing with the FileDialog
     has been commented out with the assumption that this bug will someday be fixed.
     ---------------------------------------------------------------------------------------------*/

    /**
     * Constants used in the {@link IDialogSettings}.
     * 
     * @since 4.2
     */
    interface DialogSettingsConstants {

        String SECTION_NAME = DependencyReportDialog.class.getSimpleName();

        String COLUMN_DELIMETER = "columnDelimeter"; //$NON-NLS-1$
        String NULL_VALUE = "nullValue"; //$NON-NLS-1$
        String INCLUDE_INTERMEDIATE = "includeIntermediate"; //$NON-NLS-1$
        String LAST_REPORT = "lastReport"; //$NON-NLS-1$
        String REPLACE_NULLS = "replaceNulls"; //$NON-NLS-1$

    }

    private static final String DEFAULT_DELIMETER = "|"; //$NON-NLS-1$

    private static final String DEFAULT_NULL = "<NULL>"; //$NON-NLS-1$

    private static final int DELIMETER_MAX_LENGTH = DEFAULT_DELIMETER.length();

    private static final boolean INCLUDE_INTERMEDIATE = true;

    private static final boolean REPLACE_NULLS = true;

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DependencyReportDialog.class);

    private static final String REPORT_SUFFIX = getString("reportSuffix"); //$NON-NLS-1$

    private static final String TXT_EXTENSION = ".txt"; //$NON-NLS-1$

    private static final String getString( String theKey ) {
        return Util.getString(PREFIX + theKey);
    }

    private static final String getString( String theKey,
                                           Object[] theParams ) {
        return Util.getString(PREFIX + theKey, theParams);
    }

    private String colDelim = DEFAULT_DELIMETER;

    private String nullVal = DEFAULT_NULL;

    private boolean includeIntermediate = INCLUDE_INTERMEDIATE;

    private boolean replaceNulls = INCLUDE_INTERMEDIATE;

    private String lastReportName;

    private String path;

    private IDialogSettings settings;

    private EObject virtualGroup;

    private Button btnDelReset;

    private Button btnNullReset;

    private Button chkIncludeIntermediate;

    private Button chkReplaceNulls;

    private Text txfColDelim;

    private Text txfNullVal;

    private Text txfFileName;

    /**
     * Constructs a dialog.
     * 
     * @param theShell the parent window
     * @param theVirtualGroup the group whose report is being generated
     */
    public DependencyReportDialog( Shell theShell,
                                   EObject theVirtualGroup ) {
        super(theShell, getString("title")); //$NON-NLS-1$
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setSizeRelativeToScreen(35, 40);

        this.virtualGroup = theVirtualGroup;
    }

    /**
     * Constructs a file name based on the virtual group's name. Also, the extension of the previous report generated is added if
     * it had one.
     */
    private String constructFileName() {
        StringBuffer result = new StringBuffer();

        result.append(ModelerCore.getModelEditor().getName(this.virtualGroup));
        result.append(REPORT_SUFFIX);

        // see if an extension was used last time. use it again.
        if (this.lastReportName != null) {
            String extension = null;
            int index = this.lastReportName.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR_CHAR);

            // make sure extension char is found and is not the last char in the path
            if ((index != -1) && ((index + 1) != this.lastReportName.length())) {
                extension = this.lastReportName.substring(index + 1);
            }

            if (extension != null) {
                result.append(FileUtils.Constants.FILE_EXTENSION_SEPARATOR_CHAR).append(extension);
            }
        } else {
            result.append(TXT_EXTENSION);
        }

        return result.toString();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    protected Control createButtonBar( Composite theParent ) {
        Control c = super.createButtonBar(theParent);

        // initially set OK status. should be false since the output file location/name has not been set.
        updateEnabledState();

        return c;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    protected Control createContents( Composite theParent ) {
        Control c = super.createContents(theParent);
        restoreState();

        return c;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(1, false));

        final int COLUMNS = 3;
        Composite pnlFile = WidgetFactory.createPanel(pnlMain, SWT.NONE, GridData.FILL_HORIZONTAL);
        pnlFile.setLayout(new GridLayout(COLUMNS, false));

        WidgetFactory.createLabel(pnlFile, SWT.NONE, getString("label.fileName")); //$NON-NLS-1$

        this.txfFileName = WidgetFactory.createTextField(pnlFile, GridData.FILL_HORIZONTAL, 1, "", SWT.READ_ONLY); //$NON-NLS-1$

        Button btn = WidgetFactory.createButton(pnlFile, InternalUiConstants.Widgets.BROWSE_BUTTON, GridData.HORIZONTAL_ALIGN_END);
        btn.setToolTipText(getString("button.browse.tip")); //$NON-NLS-1$
        btn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseSelected();
            }
        });

        //
        // create group and contents
        //

        final int GROUP_COLS = 3;
        Group group = WidgetFactory.createGroup(pnlMain, getString("group.settings"), GridData.FILL_BOTH, COLUMNS, GROUP_COLS); //$NON-NLS-1$

        //
        // group row 1
        //

        WidgetFactory.createLabel(group, SWT.NONE, getString("label.includeIntermediate")); //$NON-NLS-1$

        this.chkIncludeIntermediate = WidgetFactory.createCheckBox(group, "", SWT.NONE, (GROUP_COLS - 1)); //$NON-NLS-1$
        this.chkIncludeIntermediate.setToolTipText(getString("checkbox.includeIntermediate")); //$NON-NLS-1$
        this.chkIncludeIntermediate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleIncludeIntermediateSelected(theEvent);
            }
        });

        //
        // group row 2
        //

        WidgetFactory.createLabel(group, SWT.NONE, getString("label.columnDelimeter", //$NON-NLS-1$
                                                             new Object[] {Integer.toString(DELIMETER_MAX_LENGTH)}));

        this.txfColDelim = WidgetFactory.createTextField(group, GridData.FILL_HORIZONTAL);
        this.txfColDelim.setToolTipText(getString("text.columnDelimeter")); //$NON-NLS-1$
        this.txfColDelim.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent theEvent ) {
                handleColumnDelimeterChanged();
            }
        });
        this.txfColDelim.addVerifyListener(new VerifyListener() {

            public void verifyText( VerifyEvent theEvent ) {
                handleVerifyDelimeterChanged(theEvent);
            }
        });

        btnDelReset = WidgetFactory.createButton(group, getString("button.reset")); //$NON-NLS-1$
        btnDelReset.setToolTipText(getString("button.resetColDelimiter.tip", new Object[] {DEFAULT_DELIMETER})); //$NON-NLS-1$
        btnDelReset.setEnabled(false);
        btnDelReset.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleColDelResetSelected();
            }
        });

        //
        // group row 3
        //

        WidgetFactory.createLabel(group, SWT.NONE, getString("label.replaceNulls")); //$NON-NLS-1$

        this.chkReplaceNulls = WidgetFactory.createCheckBox(group, "", SWT.NONE, (GROUP_COLS - 1)); //$NON-NLS-1$
        this.chkReplaceNulls.setToolTipText(getString("checkbox.replaceNulls")); //$NON-NLS-1$
        this.chkReplaceNulls.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleReplaceNullsSelected(theEvent);
            }
        });

        //
        // group row 4
        //

        WidgetFactory.createLabel(group, SWT.NONE, getString("label.nullValue", //$NON-NLS-1$
                                                             null));

        this.txfNullVal = WidgetFactory.createTextField(group, GridData.FILL_HORIZONTAL);
        this.txfNullVal.setToolTipText(getString("text.nullValue")); //$NON-NLS-1$
        this.txfNullVal.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent theEvent ) {
                handleNullValueChanged();
            }
        });

        btnNullReset = WidgetFactory.createButton(group, getString("button.reset")); //$NON-NLS-1$
        btnNullReset.setToolTipText(getString("button.resetNullValue.tip", new Object[] {DEFAULT_NULL})); //$NON-NLS-1$
        btnNullReset.setEnabled(false);
        btnNullReset.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleNullValueResetSelected();
            }
        });

        return pnlFile;
    }

    /**
     * Obtains the column delimeter.
     * 
     * @return the column delimeter
     * @since 4.2
     */
    public String getColumnDelimeter() {
        return this.colDelim;
    }

    /**
     * Obtains the <code>IDialogSettings</code> for this dialog.
     * 
     * @return the settings
     * @since 4.2
     */
    private IDialogSettings getDialogSettings() {
        if (this.settings == null) {
            this.settings = UiPlugin.getDefault().getDialogSettings();
            IDialogSettings temp = this.settings.getSection(DialogSettingsConstants.SECTION_NAME);

            if (temp == null) {
                this.settings = this.settings.addNewSection(DialogSettingsConstants.SECTION_NAME);
            } else {
                this.settings = temp;
            }
        }

        return this.settings;
    }

    /**
     * Obtains the report file name including path information.
     * 
     * @return the file name
     * @since 4.2
     */
    public String getReportFileName() {
        return this.path;
    }

    /**
     * Handler for when the browse button is selected. The browse button is used to identify a file system location and file name
     * for the report.
     * 
     * @since 4.2
     */
    void handleBrowseSelected() {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText(getString("dialog.title")); //$NON-NLS-1$
        // dialog.setFilterExtensions(EXTENSIONS);
        dialog.setFileName(constructFileName());

        if (dialog.open() != null) {
            String name = dialog.getFileName();

            if ((name != null) && (name.length() > 0)) {
                String directory = dialog.getFilterPath();
                StringBuffer buffer = new StringBuffer();
                buffer.append(directory).append(File.separatorChar).append(name);
                if (name.indexOf('.') < 0) {
                    buffer.append(TXT_EXTENSION);
                }

                this.path = buffer.toString();

                // there currently is no way to get the selected file filter. see Eclipse bug 4560.
                // would be nice to add the selected extension if user did not add an extension.
                // check to see if path has an extension. if not add one corresponding to the dialog setting
                // if (FileUtils.getExtension(this.path) == null) {
                // String extension = dialog.getFilterExtension();
                // this.path.append(extension);
                // }

                this.txfFileName.setText(this.path);
                this.txfFileName.setToolTipText(this.path);
                updateEnabledState();
            }
        }
    }

    /**
     * Handler for when the column delimeter field is changed.
     * 
     * @since 4.2
     */
    void handleColumnDelimeterChanged() {
        this.colDelim = this.txfColDelim.getText();
        updateEnabledState();
    }

    /**
     * Handler for when the null value field is changed.
     * 
     * @since 4.2
     */
    void handleNullValueChanged() {
        this.nullVal = this.txfNullVal.getText();
        updateEnabledState();
    }

    /**
     * Handler for when the include checkbox is selected/deselected.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void handleIncludeIntermediateSelected( SelectionEvent theEvent ) {
        this.includeIntermediate = ((Button)theEvent.getSource()).getSelection();
    }

    /**
     * Handler for when the replace nulls checkbox is selected/deselected.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void handleReplaceNullsSelected( SelectionEvent theEvent ) {
        this.replaceNulls = ((Button)theEvent.getSource()).getSelection();
        this.txfNullVal.setEnabled(replaceNulls);
    }

    /**
     * Handler for when the col reset button is selected.
     * 
     * @since 4.2
     */
    void handleColDelResetSelected() {
        this.txfColDelim.setText(DEFAULT_DELIMETER);
        this.txfColDelim.setFocus();
        this.txfColDelim.selectAll();
    }

    /**
     * Handler for when the null reset button is selected.
     * 
     * @since 4.2
     */
    void handleNullValueResetSelected() {
        this.txfNullVal.setText(DEFAULT_NULL);
        this.txfNullVal.setFocus();
        this.txfNullVal.selectAll();
    }

    /**
     * Handler to determine if the characters being added or subtracted from the column delimeter field should be allowed. If not
     * allowed then the event's doit property is changed to <code>false</code>.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void handleVerifyDelimeterChanged( VerifyEvent theEvent ) {
        boolean doit = true;

        // only care if text has been added
        if (theEvent.text.length() > 0) {
            String current = this.txfColDelim.getText();
            int currLength = current.length();

            if (currLength > 0) {
                StringBuffer buf = new StringBuffer(this.txfColDelim.getText());
                int start = theEvent.start;
                int end = theEvent.end;

                if ((start < currLength) && (end < currLength)) {
                    // replacing existing text
                    buf.replace(theEvent.start, theEvent.end, theEvent.text);
                } else if (start >= currLength) {
                    // adding text
                    buf.append(theEvent.text);
                } else if ((start + theEvent.text.length() - 1) >= DELIMETER_MAX_LENGTH) {
                    // replacing existing text and adding text is too big
                    doit = false;
                }

                // don't allow if too many characters
                if (doit) {
                    doit = (buf.length() <= DELIMETER_MAX_LENGTH);
                }
            } else {
                // no current text. make sure added text is not too big
                doit = (theEvent.text.length() <= DELIMETER_MAX_LENGTH);
            }
        }

        if (!doit) {
            theEvent.doit = false;
        }
    }

    /**
     * Indicates if intermediate virtual mappings should be included in the report.
     * 
     * @return <code>true</code> if should be included; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isIncludeIntermediate() {
        return this.includeIntermediate;
    }

    /**
     * Indicates if replace nulls with user string should be included in the report.
     * 
     * @return <code>true</code> if should be replaced <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isReplaceNulls() {
        return this.replaceNulls;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.2
     */
    @Override
    protected void okPressed() {
        saveState();
        super.okPressed();
    }

    /**
     * Restores previous dialog settings.
     * 
     * @since 4.2
     */
    private void restoreState() {
        IDialogSettings dialogSettings = getDialogSettings();

        // include intermediate levels
        String value = null;
        value = dialogSettings.get(DialogSettingsConstants.INCLUDE_INTERMEDIATE);

        // if setting is present, restore last value else set to default
        final boolean checked = (value == null) ? INCLUDE_INTERMEDIATE : settings.getBoolean(DialogSettingsConstants.INCLUDE_INTERMEDIATE);
        final Button btn = this.chkIncludeIntermediate;

        // do this so the event handlers react
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                btn.setSelection(checked);
                Event event = new Event();
                event.widget = btn;
                btn.notifyListeners(SWT.Selection, event);
            }
        });

        value = dialogSettings.get(DialogSettingsConstants.REPLACE_NULLS);
        // if setting is present, restore last value else set to default
        final boolean nullButtonChecked = (value == null) ? REPLACE_NULLS : settings.getBoolean(DialogSettingsConstants.REPLACE_NULLS);
        final Button nullBtn = this.chkReplaceNulls;

        // do this so the event handlers react
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                nullBtn.setSelection(nullButtonChecked);
                Event event = new Event();
                event.widget = nullBtn;
                nullBtn.notifyListeners(SWT.Selection, event);
            }
        });

        // column delimeter
        value = dialogSettings.get(DialogSettingsConstants.COLUMN_DELIMETER);

        if ((value == null) || (value.length() == 0)) {
            value = DEFAULT_DELIMETER;
        } else if (value.length() > DELIMETER_MAX_LENGTH) {
            // make sure the value is not too big. the max length could've changed.
            value = value.substring(0, DELIMETER_MAX_LENGTH);
        }

        final String colText = value;
        final Text colTxf = this.txfColDelim;

        // null value
        value = dialogSettings.get(DialogSettingsConstants.NULL_VALUE);

        if ((value == null) || (value.length() == 0)) {
            value = DEFAULT_NULL;
        }

        final String nullText = value;
        final Text nullTxf = this.txfNullVal;

        // do this so the event handlers react
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                colTxf.setText(colText);
                nullTxf.setText(nullText);
                nullTxf.setEnabled(nullButtonChecked);
            }
        });

        // report location & name
        value = dialogSettings.get(DialogSettingsConstants.LAST_REPORT);

        if ((value != null) && (value.length() != 0)) {
            this.lastReportName = value;
            this.txfFileName.setText(this.lastReportName);
            this.path = this.lastReportName;
        }
    }

    /**
     * Saves the dialog settings.
     * 
     * @since 4.2
     */
    private void saveState() {
        IDialogSettings dialogSettings = getDialogSettings();

        dialogSettings.put(DialogSettingsConstants.INCLUDE_INTERMEDIATE, this.includeIntermediate);
        dialogSettings.put(DialogSettingsConstants.REPLACE_NULLS, this.replaceNulls);
        dialogSettings.put(DialogSettingsConstants.COLUMN_DELIMETER, this.colDelim);
        dialogSettings.put(DialogSettingsConstants.NULL_VALUE, this.nullVal);
        dialogSettings.put(DialogSettingsConstants.LAST_REPORT, this.path);
    }

    /**
     * Updates the enabled state of the OK and Reset buttons.
     * 
     * @since 4.2
     */
    private void updateEnabledState() {
        // OK button
        boolean enable = true;

        if ((this.txfFileName.getText().length() == 0) || (this.txfColDelim.getText().length() == 0)) {
            enable = false;
        }

        getButton(IDialogConstants.OK_ID).setEnabled(enable);

        // col delimiter reset button
        enable = true;

        if (this.txfColDelim.getText().equals(DEFAULT_DELIMETER)) {
            enable = false;
        }

        this.btnDelReset.setEnabled(enable);

        // null value button
        enable = true;

        if (this.txfNullVal.getText().equals(DEFAULT_NULL)) {
            enable = false;
        }

        this.btnNullReset.setEnabled(enable);

    }

    /**
     * Obtains the null value.
     * 
     * @return the null value
     * @since 4.2
     */
    public String getNullValue() {
        if (replaceNulls) return this.nullVal;

        return ""; //$NON-NLS-1$
    }
}
