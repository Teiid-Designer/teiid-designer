package com.metamatrix.modeler.relational.ui.flatfile;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class FlatFileRelationalModelWizardPage extends WizardPage implements UiConstants{
	
	private Button generateGetFilesCB;
	private Button generateGetTextFilesCB;
	private Button generateSaveFileCB;
	
	boolean generateGetFiles;
	boolean generateGetTextFiles;
	boolean generateSaveFile;
	
	////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////
	/**
     * Construct an instance of FlatFileRelationalModelWizardPage.
     * @param pageName
     */
    public FlatFileRelationalModelWizardPage(String pageName) {
        super(pageName);

        setTitle(Util.getString("FlatFileRelationalModelWizardPage.title")); //$NON-NLS-1$
        setDescription(Util.getString("FlatFileRelationalModelWizardPage.description")); //$NON-NLS-1$
    }

	@Override
	public void createControl(Composite parent) {
		// 
		
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        mainPanel.setLayout(new GridLayout(2, false));

        final Group optionsGroup = WidgetFactory.createGroup(mainPanel, 
        		Util.getString("FlatFileRelationalModelWizardPage.optionsGroup.title"),  //$NON-NLS-1$
        		GridData.FILL_HORIZONTAL, 2, 1);
        
        generateGetFilesCB = WidgetFactory.createCheckBox(optionsGroup, "getFiles('path/*.ext') return blob", 0, 2, true); //$NON-NLS-1$
        generateGetFilesCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	generateGetFiles = generateGetFilesCB.getSelection();
                //validateInputs();
            }
        });
        
        generateGetTextFilesCB = WidgetFactory.createCheckBox(optionsGroup, "getTextFiles('path/*.ext') return clob", 0, 2, true); //$NON-NLS-1$
        generateGetTextFilesCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	generateGetTextFiles = generateGetTextFilesCB.getSelection();
                //validateInputs();
            }
        });
        
        generateSaveFileCB = WidgetFactory.createCheckBox(optionsGroup, "saveFile('path', value) return void", 0, 2, true); //$NON-NLS-1$
        generateSaveFileCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	generateSaveFile = generateSaveFileCB.getSelection();
                //validateInputs();
            }
        });

        super.setControl(mainPanel);
        
        generateGetFiles = generateGetFilesCB.getSelection();
        generateGetTextFiles = generateGetTextFilesCB.getSelection();
        generateSaveFile = generateSaveFileCB.getSelection();
		
	}

	public boolean doGenerateGetFiles() {
		return generateGetFiles;
	}

	public boolean doGenerateGetTextFiles() {
		return generateGetTextFiles;
	}

	public boolean doGenerateSaveFile() {
		return generateSaveFile;
	}
}
