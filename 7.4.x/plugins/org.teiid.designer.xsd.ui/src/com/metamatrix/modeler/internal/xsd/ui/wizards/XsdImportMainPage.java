/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 5.5
 */
public class XsdImportMainPage extends AbstractWizardPage {
    private static final String I18N_PREFIX = "XsdImportMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    private static final int IMPORT_FROM_FILE = 1;
    private static final int IMPORT_FROM_URL = 2;
    int importFrom = -1;

    public XsdImportMainPage() {
        super(XsdImportMainPage.class.getSimpleName(), getString("title"));//$NON-NLS-1$
    }

    private static String getString( final String id ) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);
        createImportSelectionPanel(pnlMain);

        setPageStatus();
    }

    @Override
    public boolean canFlipToNextPage() {
        return (this.importFrom != -1) && super.canFlipToNextPage();
    }

    boolean isImportFromUrl() {
        return importFrom == IMPORT_FROM_URL;
    }

    void setPageStatus() {
        if (this.importFrom == -1) {
            setMessage(getString("selectImportFrom"), IStatus.ERROR);//$NON-NLS-1$
            return;
        }

        setMessage(null, IStatus.OK);

        // update enabled state of next, finish
        getContainer().updateButtons();
    }

    private void createImportSelectionPanel( Composite theParent ) {
        Button fromFile = WidgetFactory.createRadioButton(theParent, getString("importFromFile"));//$NON-NLS-1$
        fromFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                importFrom = IMPORT_FROM_FILE;
                setPageStatus();
            }
        });
        Button fromURL = WidgetFactory.createRadioButton(theParent, getString("importFromURL"));//$NON-NLS-1$
        fromURL.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                importFrom = IMPORT_FROM_URL;
                setPageStatus();
            }
        });
    }
}
