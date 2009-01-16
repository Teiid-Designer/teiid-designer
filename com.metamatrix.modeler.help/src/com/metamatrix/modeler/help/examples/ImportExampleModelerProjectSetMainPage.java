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
package com.metamatrix.modeler.help.examples;

import java.io.File;
import java.io.FilenameFilter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.WorkbenchException;
import com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * Page 1 of the base resource import-from-file-system Wizard
 */
public class ImportExampleModelerProjectSetMainPage extends ImportModelerProjectSetMainPage {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private static final String SUFFIX = "_modelProjectSet.zip"; //$NON-NLS-1$

    static final String SUFFIX_LC = SUFFIX.toLowerCase();

    private static final FilenameFilter FILTER = new FilenameFilter() {
        public boolean accept( File dir,
                               String name ) {
            return name.toLowerCase().endsWith(SUFFIX_LC);
        }
    };

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    private static String trimName( String longName ) {
        return longName.substring(0, longName.length() - SUFFIX.length());
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private final File examplesDirectory;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param examplesDirectory the directory where the example model project sets are located
     * @since 6.0.0
     */
    public ImportExampleModelerProjectSetMainPage( File examplesDirectory ) {
        super(HelpExamplesMessages.WizardPageTitle, StructuredSelection.EMPTY);
        this.examplesDirectory = examplesDirectory;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);
        setMessage(HelpExamplesMessages.WizardPageMessage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#createRootDirectoryGroup(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    protected void createRootDirectoryGroup( Composite parent ) {
        Composite sourceContainerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        sourceContainerGroup.setLayout(layout);
        sourceContainerGroup.setFont(parent.getFont());
        sourceContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label groupLabel = new Label(sourceContainerGroup, SWT.NONE);
        groupLabel.setText(getSourceLabel());
        groupLabel.setFont(parent.getFont());

        // source name entry field
        this.sourceNameField = new Combo(sourceContainerGroup, SWT.BORDER | SWT.READ_ONLY);
        GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);

        this.sourceNameField.setLayoutData(data);
        this.sourceNameField.setFont(parent.getFont());
        this.sourceNameField.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent e ) {
                updateStatus();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
                widgetSelected(e);
            }
        });

        refreshComboFromFiles();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#finish()
     * @since 6.0.0
     */
    @Override
    public boolean finish() {
        if (super.finish()) {
            // show designer perspective
            try {
                UiUtil.getWorkbench().showPerspective(UiConstants.Extensions.PERSPECTIVE,
                                                      UiUtil.getWorkbenchWindowOnlyIfUiThread());
            } catch (WorkbenchException e) {
                UiConstants.Util.log(e);
            }

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#getSourceLabel()
     * @since 6.0.0
     */
    @Override
    protected String getSourceLabel() {
        return HelpExamplesMessages.WizardPageSourcePrompt;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#getSourceName()
     * @since 6.0.0
     */
    @Override
    protected String getSourceName() {
        return new File(this.examplesDirectory, super.getSourceName() + SUFFIX).toString();
    }

    private void refreshComboFromFiles() {
        if ((this.sourceNameField != null) && (this.examplesDirectory != null)) {
            // gui part init'd, and examples dir exists:
            this.sourceNameField.removeAll();

            // set filenames history
            String[] exampleFiles = this.examplesDirectory.list(FILTER);

            for (String name : exampleFiles) {
                this.sourceNameField.add(trimName(name));
            }

            if (exampleFiles.length > 0) {
                this.sourceNameField.setText(this.sourceNameField.getItem(0));

                // run later to allow for construction finish:
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        updateStatus();
                    }
                });
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#restoreWidgetValues()
     * @since 6.0.0
     */
    @Override
    protected void restoreWidgetValues() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.wizards.ImportModelerProjectSetMainPage#saveWidgetValues()
     * @since 6.0.0
     */
    @Override
    protected void saveWidgetValues() {
    }

    /**
     * Sets the button enablement and messages.
     * 
     * @since 6.0.0
     */
    void updateStatus() {
        setCompletionStatus();
    }
}
