/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.dialogs;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.preview.GenerateDataServiceWorker;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

public class GenerateDataServiceDialog  extends TitleAreaDialog implements DqpUiConstants {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(GenerateDataServiceDialog.class);
	
    static String getString( String key ) {
        return UTIL.getString(THIS_CLASS + key);
    }
	
	private EObject previewableEObject;
	
    private StyledText xmlContentsBox;

	Properties designerProperties;
	
    private Font monospaceFont;
    
    private Text dataServiceName;
    private Text dataServiceVdbName;
    private Text dataServiceVdbVersionString;

    private Button workspaceRB;
    private Button fileSystemRB;
    private Button browseForLocation;
    private Text locationText;
    
    private GenerateDataServiceWorker worker;


	/**
	 * @since 5.5.3
	 */
	public GenerateDataServiceDialog(Shell parentShell, GenerateDataServiceWorker worker) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.worker = worker;
	}

	/**
	 * @since 5.5.3
	 */
	public GenerateDataServiceDialog(Shell parentShell, Properties properties, GenerateDataServiceWorker worker) {
		this(parentShell, worker);
		this.designerProperties = properties;
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(getString("title")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(true);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

        monospaceFont(parent);
        
		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(10,  10).applyTo(pnlOuter);
		GridDataFactory.fillDefaults().grab(true,  true).applyTo(pnlOuter);

		// set title
		setTitle(getString("title"));  //$NON-NLS-1$
		setMessage(getString("initialMessage"));  //$NON-NLS-1$

        // VDB ARCHIVE GROUP

        Composite summaryGroup = WidgetFactory.createGroup(pnlOuter,
                    getString("dataServiceDefinition"),  //$NON-NLS-1$
                    SWT.NO_SCROLL,
                    2);
		summaryGroup.setLayout(new GridLayout(3, false));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(summaryGroup);

		// VDB Name: products_info
		WidgetFactory.createLabel(summaryGroup, GridData.VERTICAL_ALIGN_CENTER,
				getString("dataServiceNameLabel")); //$NON-NLS-1$
		dataServiceName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(dataServiceName);
		dataServiceName.setText(worker.getDataServiceName());
		dataServiceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				worker.setDataServiceName(dataServiceName.getText());
		        IStatus status = worker.getDataServiceStatus(true);
		        xmlContentsBox.setText(status.getMessage());
		        validate();
			}
		});
		
		
        // VDB Name: products_info
        WidgetFactory.createLabel(summaryGroup,
                                  GridData.VERTICAL_ALIGN_CENTER,
                                  getString("dataServiceFileName")); //$NON-NLS-1$
        dataServiceVdbName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(dataServiceVdbName);
        dataServiceVdbName.setText(worker.getDataServiceFileName());
//            dataServiceVdbName.setToolTipText(Messages.GenerateDynamicVdbPageOne_dynamicVdbNameTooltip);
        dataServiceVdbName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
                worker.setDataServiceFileName(dataServiceVdbName.getText());
                IStatus status = worker.getDataServiceStatus(true);
		        xmlContentsBox.setText(status.getMessage());
		        validate();
            }
        });

		Label versionLabel = WidgetFactory.createLabel(summaryGroup, getString("dataServiceVersion")); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(versionLabel);

		dataServiceVdbVersionString = WidgetFactory.createTextField(summaryGroup);
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(dataServiceVdbVersionString);
		((GridData) dataServiceVdbVersionString.getLayoutData()).widthHint = 40;
		dataServiceVdbVersionString.setText(worker.getVersionString());

		dataServiceVdbVersionString.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				worker.setVersionString(dataServiceVdbVersionString.getText());
				IStatus status = worker.getDataServiceStatus(true);
		        xmlContentsBox.setText(status.getMessage());
		        validate();
			}
		});
        
		// Create button panel
	    createButtonPanel(summaryGroup);
		
        createVdbXmlPanel(summaryGroup);
        
		return pnlOuter;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

        getButton(Dialog.OK).setEnabled(false);
        
		return control;
	}

	public EObject getPreviewableEObject() {
		return this.previewableEObject;
	}

    /*
     * Create the VDB Deploy button 
     */
    private void createButtonPanel(Composite parent) {
    	
        Composite buttonPanel = new Composite(parent,SWT.NONE);
        buttonPanel.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(buttonPanel);
        
        workspaceRB = new Button(buttonPanel, SWT.RADIO | SWT.SELECTED);
        workspaceRB.setText(getString("saveToWorkspace")); //$NON-NLS-1$
        workspaceRB.setSelection(worker.isSaveToWorkspace());
        workspaceRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				worker.setSaveToWorkspace(workspaceRB.getSelection());
				if( worker.isSaveToWorkspace() ) {
					if( worker.getWorkspaceLocation() != null ) {
			        	locationText.setText(worker.getWorkspaceLocation().getFullPath().makeAbsolute().toString());
					} else {
						 locationText.setText(getString("locationUndefinedMessage")); //$NON-NLS-1$
					}
				}
				validate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        fileSystemRB = new Button(buttonPanel, SWT.RADIO);
        fileSystemRB.setText(getString("saveToFileSystem")); //$NON-NLS-1$
        fileSystemRB.setSelection(!worker.isSaveToWorkspace());
        fileSystemRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				worker.setSaveToWorkspace(workspaceRB.getSelection());
				if( StringUtilities.isNotEmpty(worker.getFileSystemFolder()) ) {
		        	locationText.setText(worker.getFileSystemFolder());
				} else {
					 locationText.setText(getString("locationUndefinedMessage")); //$NON-NLS-1$
				}
				validate();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});
        
        Composite locationPanel = new Composite(buttonPanel,SWT.NONE);
        locationPanel.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(locationPanel);
        
        WidgetFactory.createLabel(locationPanel, getString("location")); //$NON-NLS-1$
        final String name = "";
        locationText = WidgetFactory.createTextField(locationPanel, GridData.FILL_HORIZONTAL, 1, name, SWT.READ_ONLY);
        locationText.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        locationText.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(locationText);
        locationText.setText(getString("locationUndefinedMessage")); //$NON-NLS-1$
        
        browseForLocation = new Button(locationPanel, SWT.PUSH);
        browseForLocation.setText(getString("browse")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).applyTo(browseForLocation);
        browseForLocation.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( workspaceRB.getSelection() ) {
            		browseForWorkspaceContainer();
            	} else {
            		browseForFileSystemFolder();
            	}
            	validate();
            }

        });
        
    }

    /*
     * Create the SQL Display tab panel
     */
    private Composite createVdbXmlPanel( Composite parent ) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("fileContents"), GridData.FILL_BOTH, 1);  //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(theGroup);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(theGroup);

        xmlContentsBox = new StyledText(theGroup, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).hint(500, 340).applyTo(xmlContentsBox);

        xmlContentsBox.setEditable(false);
        xmlContentsBox.setFont(monospaceFont);
        
        IStatus status = this.worker.getDataServiceStatus(true);
        xmlContentsBox.setText(status.getMessage());
        return theGroup;
    }
    
    
    private Font monospaceFont(Composite composite) {
        if (monospaceFont == null) {
            monospaceFont = new Font(composite.getDisplay(), "Monospace", 12, SWT.NORMAL); //$NON-NLS-1$
            composite.addDisposeListener(new DisposeListener() {

                @Override
                public void widgetDisposed(DisposeEvent e) {
                    if (monospaceFont == null)
                        return;

                    monospaceFont.dispose();
                }
            });
        }

        return monospaceFont;
    }
    
    /**
     * Export the current string content of the DDL display to a user-selected file on file system
     */
    public void browseForFileSystemFolder() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*-vdb.xml*"}); //$NON-NLS-1$
        String ddlFileName = worker.getDataServiceFileName();
        dlg.setText(ddlFileName);
        dlg.setFileName(ddlFileName);
        String fileStr = dlg.open();
        
        worker.setFileSystemFullPathAndFile(fileStr);
        if( StringUtilities.isNotEmpty(fileStr) ) {
        	locationText.setText(worker.getFileSystemFolder());
        }
    }
    
    /**
     * Export the current string content of the DDL display to a user-selected location in their workspace
     */
    private void browseForWorkspaceContainer() {
    	
        final IContainer container = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
        		new ModelingResourceFilter(),
                new ModelProjectSelectionStatusValidator());

        if (container != null && locationText != null) {
        	worker.setWorkspaceLocation(container);
        	locationText.setText(container.getFullPath().makeAbsolute().toString());
        }
        
//        // Show dialog for copying the DataSource
//    	String ddlFileName =  worker.getDataServiceFileName();
//        SelectDataServiceWorkspaceLocationDialog dialog = new SelectDataServiceWorkspaceLocationDialog(getShell(), ddlFileName);
//
//        dialog.open();
//        
//        // If Dialog was OKd, create the DataSource
//        if (dialog.getReturnCode() == Window.OK) {
//        	IContainer targetContainer = dialog.getTargetContainer();
//        	worker.setWorkspaceLocation(targetContainer);
//        	locationText.setText(targetContainer.getFullPath().makeAbsolute().toString());
//        }
    }
    
    private void validate() {
    	IStatus status = worker.validate();
    	
    	if( status.isOK() || IStatus.WARNING == status.getSeverity() || IStatus.INFO == status.getSeverity()) {
    		getButton(Dialog.OK).setEnabled(true);
    		setMessage(getString("okMessage"));//$NON-NLS-1$
    		setErrorMessage(null);
    		return;
    	}
    	
    	setMessage(null);
    	setErrorMessage(status.getMessage());
    	getButton(Dialog.OK).setEnabled(false);
    }
    

}
