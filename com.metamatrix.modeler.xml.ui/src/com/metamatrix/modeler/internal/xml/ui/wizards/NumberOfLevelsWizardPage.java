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
package com.metamatrix.modeler.internal.xml.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.ui.internal.widget.IntegerSpinner;

/**
 * NumberOfLevelsWizardPage
 */
public class NumberOfLevelsWizardPage extends WizardPage implements ModelerXmlUiConstants {
    public static final int MIN_LEVELS = 1;
    public static final int MAX_LEVELS = 5;

    private NumberOfLevelsPanel panel;

    public NumberOfLevelsWizardPage() {
        super("numberOfLevelsWizardPage"); //$NON-NLS-1$
        String title = Util.getString("NumberOfLevelsWizardPage.title"); //$NON-NLS-1$
        setTitle(title);
        String description = Util.getString("NumberOfLevelsWizardPage.description"); //$NON-NLS-1$
        setDescription(description);
    }

    public void createControl( Composite parent ) {
        panel = new NumberOfLevelsPanel(parent);
        setControl(panel);
    }

    public int getValue() {
        return panel.getSpinnerValue();
    }
}// end NumberOfLevelsWizardPage

class NumberOfLevelsPanel extends Composite implements ModelerXmlUiConstants {
    private IntegerSpinner spinner;
    private Button allLevelsChk;

    public NumberOfLevelsPanel( Composite parent ) {
        super(parent, SWT.NONE);
        initialize();
    }

    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        GridData overallGridData = new GridData(GridData.FILL_HORIZONTAL);
        this.setLayoutData(overallGridData);
        Composite spinnerRow = new Composite(this, SWT.NONE);
        GridLayout spinnerRowLayout = new GridLayout();
        spinnerRow.setLayout(spinnerRowLayout);
        spinnerRowLayout.numColumns = 2;
        spinnerRowLayout.marginHeight = 10;
        GridData spinnerRowGridData = new GridData(GridData.FILL_HORIZONTAL);
        spinnerRow.setLayoutData(spinnerRowGridData);
        spinnerRowGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
        Label spinnerLabel = new Label(spinnerRow, SWT.NONE);
        String labelText = Util.getString("NumberOfLevelsWizardPage.numberOfLevels"); //$NON-NLS-1$
        spinnerLabel.setText(labelText);
        spinner = new IntegerSpinner(spinnerRow, NumberOfLevelsWizardPage.MIN_LEVELS, NumberOfLevelsWizardPage.MAX_LEVELS);

        allLevelsChk = new Button(spinnerRow, SWT.CHECK);
        allLevelsChk.setText(Util.getString("NumberOfLevelsWizardPage.btnTxt")); //$NON-NLS-1$
        allLevelsChk.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                allLevelsChkSelected();
            }

        });
    }

    public int getSpinnerValue() {
        if (allLevelsChk.getSelection()) {
            return -1;
        }

        return spinner.getIntegerValue();
    }

    void allLevelsChkSelected() {
        if (this.allLevelsChk.getSelection()) {
            this.spinner.setEnabled(false);
        } else {
            this.spinner.setEnabled(true);
        }
    }
}// end NumberOfLevelsPanel
