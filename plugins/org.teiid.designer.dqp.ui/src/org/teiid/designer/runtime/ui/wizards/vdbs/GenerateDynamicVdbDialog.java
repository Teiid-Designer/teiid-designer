/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.style.XmlRegion;
import org.teiid.designer.ui.common.util.style.XmlRegionAnalyzer;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbPlugin;

public class GenerateDynamicVdbDialog extends ScrollableTitleAreaDialog implements DqpUiConstants, StringConstants {

	private GenerateDynamicVdbManager vdbManager;
    
	private TabItem dynamicVdbInfoTab;
	private TabItem optionsTab;
	
    private Text dynamicVdbName;
    //private Label dynamicVdbLocationText;
    private Text dynamicVdbFileName;
    private Text vdbVersionText;
    private Button workspaceRB;
    private Button fileSystemRB;
    private Button browseForLocation;
    private Text locationText;
    private Font monospaceFont;
    private StyledText xmlContentsBox;
    private Button allowEditXmlButton;
    private Button resetButton;
    boolean ignoreSelectionListener;
    
    private Button excludeSourceDdlButton;
    private Button suppressDefaultAttributesOption;
    
	public GenerateDynamicVdbDialog(Shell parentShell) {
		super(parentShell);
	}

	public GenerateDynamicVdbDialog(Shell parentShell, GenerateDynamicVdbManager vdbManager) {
		this(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        
        this.vdbManager = vdbManager;
	}

    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.GenerateDynamicVdbDialog_saveAsVdbXmlFile);
        setMessage(Messages.GenerateDynamicVdbDialog_initialMessage);
    	
        Composite composite = (Composite)super.createDialogArea(parent);

        monospaceFont(parent);
        
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        ((GridData)composite.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData)composite.getLayoutData()).widthHint = 600;
        
        createSummaryGroup(composite);
        
        TabFolder tabFolder = new TabFolder(composite, SWT.TOP | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(tabFolder);
		
        createDynamicVdbInfoTab(tabFolder);
        
        createOptionsTab(tabFolder);
        
        createSaveFilePanel(composite);
        
        createXMLDisplayGroup(composite);
        
        sizeScrolledPanel();

        return composite;
    }
	
    /**
     * @see org.eclipse.jface.window.Window#constrainShellSize()
     */
    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();

        final Shell shell = getShell();
        shell.setText(Messages.GenerateDynamicVdbDialog_saveAsVdbXmlFile);

        { // center on parent
            final Shell parentShell = (Shell)shell.getParent();
            final Rectangle parentBounds = parentShell.getBounds();
            final Point parentCenter = new Point(parentBounds.x + (parentBounds.width/2), parentBounds.y + parentBounds.height/2);

            final Rectangle r = shell.getBounds();
            final Point shellLocation = new Point(parentCenter.x - r.width/2, parentCenter.y - r.height/2);

            shell.setLocation(Math.max(0, shellLocation.x), Math.max(0, shellLocation.y));
        }
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
    }
    
    private void createSummaryGroup(Composite mainPanel) {
        // Selected VDB: MyProject/myFolder/ABC.vdb
        Composite summaryGroup = WidgetFactory.createPanel(mainPanel, SWT.NO_SCROLL);
        summaryGroup.setLayout(new GridLayout(4, false));
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(summaryGroup);

        Label nameLabel = new Label(summaryGroup, SWT.NONE);
        nameLabel.setText(Messages.GenerateDynamicVdbDialog_selectedVdbFile);
        nameLabel.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        Label vdbAndLocation = new Label(summaryGroup, SWT.NONE);
        GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(vdbAndLocation);
        vdbAndLocation.setText(vdbManager.getArchiveVdb().getSourceFile().getFullPath().toString());
        vdbAndLocation.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        // Version #: 25
        Label versionLabel = WidgetFactory.createLabel(summaryGroup, Messages.GenerateDynamicVdbDialog_version);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(versionLabel);
        versionLabel.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        Label vdbVersion = new Label(summaryGroup, SWT.NONE);
        GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(vdbVersion);
        ((GridData)vdbVersion.getLayoutData()).widthHint = 40;
        vdbVersion.setText(vdbManager.getArchiveVdb().getVersion());
        vdbVersion.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        
        ((GridData)summaryGroup.getLayoutData()).widthHint = 400;
    }
    
	private void createDynamicVdbInfoTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createDynamicVdbInfoPanel(folderParent);

        this.dynamicVdbInfoTab = new TabItem(folderParent, SWT.NONE);
        this.dynamicVdbInfoTab.setControl(thePanel);
        this.dynamicVdbInfoTab.setText(Messages.GenerateDynamicVdbDialog_dynamicVdbDefinition);
	}
	
	private void createOptionsTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createOptionsPanel(folderParent);

        this.optionsTab = new TabItem(folderParent, SWT.NONE);
        this.optionsTab.setControl(thePanel);
        this.optionsTab.setText("Options");
	}
	
	private Composite createDynamicVdbInfoPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE);

		thePanel.setLayout(new GridLayout(4, false));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(thePanel);

		// VDB Name: products_info
		WidgetFactory.createLabel(thePanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateDynamicVdbDialog_name);
//				Messages.GenerateDynamicVdbPageOne_dynamicVdbName);
		dynamicVdbName = WidgetFactory.createTextField(thePanel, SWT.NONE, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(dynamicVdbName);
		dynamicVdbName.setText(vdbManager.getOutputVdbName());
		dynamicVdbName.setToolTipText(Messages.GenerateDynamicVdbDialog_dynamicVdbNameTooltip);
		dynamicVdbName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				vdbManager.setOutputVdbName(dynamicVdbName.getText());
				resetDynamicVdb();
				validatePage();
			}
		});

		Label vdbVersionLabel = WidgetFactory.createLabel(thePanel, Messages.GenerateDynamicVdbDialog_version);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(vdbVersionLabel);

		vdbVersionText = WidgetFactory.createTextField(thePanel);
		GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(vdbVersionText);
		((GridData) vdbVersionText.getLayoutData()).widthHint = 40;
		vdbVersionText.setText(vdbManager.getVersion());

		vdbVersionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				vdbManager.setVersion(vdbVersionText.getText());
				resetDynamicVdb();
				validatePage();
			}
		});

		// File Name: ABC-xml.vdb (EDITABLE TEXT FIELD && ... Picker)
		WidgetFactory.createLabel(thePanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateDynamicVdbDialog_fileName);
//				Messages.GenerateDynamicVdbPageOne_dynamicVdbFileName);
		dynamicVdbFileName = WidgetFactory.createTextField(thePanel, SWT.NONE, GridData.FILL_HORIZONTAL);
		GridDataFactory.fillDefaults().span(3, 1).grab(true, false).applyTo(dynamicVdbFileName);
		dynamicVdbFileName.setText(vdbManager.getOutputVdbFileName());
		dynamicVdbFileName.setToolTipText(Messages.GenerateDynamicVdbDialog_dynamicVdbFileNameToolTip);
		dynamicVdbFileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				vdbManager.setOutputVdbFileName(dynamicVdbFileName.getText());
				resetDynamicVdb();
				validatePage();
			}
		});
		
		return thePanel;
	}
	
	private Composite createOptionsPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE);
		
		thePanel.setLayout(new GridLayout(1, false));
        GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(thePanel);
        
        excludeSourceDdlButton = new Button(thePanel, SWT.CHECK);
        excludeSourceDdlButton.setText(Messages.GenerateDynamicVdbDialog_excludeSourceDdlMetadata);
        excludeSourceDdlButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				vdbManager.setExcludeSourceMetadata(excludeSourceDdlButton.getSelection());
				resetDynamicVdb();
				validatePage();
			}
		});

        suppressDefaultAttributesOption = WidgetFactory.createButton(thePanel,
                                                            Messages.GenerateDynamicVdbDialog_suppressDefaultAttributesOption,
                                                            GridData.FILL_HORIZONTAL, 1, SWT.CHECK);

        GridDataFactory.fillDefaults().grab(true, false).applyTo(suppressDefaultAttributesOption);

        final IEclipsePreferences preferences = VdbPlugin.singleton().getPreferences();
        suppressDefaultAttributesOption.setSelection(preferences.getBoolean(Vdb.SUPPRESS_XML_DEFAULT_ATTRIBUTES, true));
        suppressDefaultAttributesOption.setToolTipText(Messages.GenerateDynamicVdbDialog_suppressDefaultAttributesOptionTooltip);
        suppressDefaultAttributesOption.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                preferences.putBoolean(Vdb.SUPPRESS_XML_DEFAULT_ATTRIBUTES, suppressDefaultAttributesOption.getSelection());

                //
                // Reset the dynamic vdb
                //
                resetDynamicVdb();
            }
        });

		return thePanel;
	}
    
    private void resetDynamicVdb() {
        vdbManager.setDynamicVdb(null);
        vdbManager.generate();
		try {
			refreshXml();
		} catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            ErrorHandler.toExceptionDialog(ex);
		}
    }
    
    public void refreshXml() throws Exception  {
    	
        setXmlContents(vdbManager.getDynamicVdbXml());
    }
    
    /**
     * Set the xml content string of the style text box and
     * compute the highlighting colouration using the
     * {@link XmlRegionAnalyzer}
     *
     * @param xml
     */
    private void setXmlContents(String xml) {
        if (xml == null) {
        	this.xmlContentsBox.setText(EMPTY_STRING);
        } else {
	        this.xmlContentsBox.setText(xml);
	
	        if (xml.length() > 0) {
	            XmlRegionAnalyzer analyzer = new XmlRegionAnalyzer();
	            List<XmlRegion> xmlRegions = analyzer.analyzeXml(xml);
	            List<StyleRange> styleRanges = UiUtil.computeStyleRanges(xmlRegions);
	            this.xmlContentsBox.setStyleRanges(styleRanges.toArray(new StyleRange[0]));
	        }
        }
    }

    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup(Composite parent) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.GenerateDynamicVdbDialog_fileContents, GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(theGroup);
        GridDataFactory.fillDefaults().hint(600, 400).span(4, 1).grab(true, true).applyTo(theGroup);

        allowEditXmlButton = new Button(theGroup, SWT.CHECK);
        allowEditXmlButton.setText(Messages.GenerateDynamicVdbDialog_allowEditXml);
        allowEditXmlButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if( ignoreSelectionListener ) return;
				boolean result = false;
				
				boolean theSelection = allowEditXmlButton.getSelection();
				if( allowEditXmlButton.getSelection() ) {
					result = MessageDialog.openConfirm(getShell(), "Allowing VDB XML Editing", "The XML text editor will be enabled.\n\nUncheck this box or click Reset to return to original XML generated from *.vdb");
				} else {
					result = MessageDialog.openConfirm(getShell(), "Disabling VDB XML Editing", "The XML text editor will be disabled and the contents of the XML will be reset to the original XML generated from your *.vdb" +
							"\n\n Click OK to continue or Cancel");
				}
				if( result ) {
					vdbManager.setEditXmlMode(allowEditXmlButton.getSelection());
					setXmlEditiable(allowEditXmlButton.getSelection());
				} else {
					ignoreSelectionListener = true;
					allowEditXmlButton.setSelection(!theSelection);
					ignoreSelectionListener = false;
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        resetButton = new Button(theGroup, SWT.PUSH);
        resetButton.setText(Messages.GenerateDynamicVdbDialog_reset);
        GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).applyTo(resetButton);
        resetButton.setEnabled(false);
        resetButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean result = MessageDialog.openConfirm(getShell(), "Disabling VDB XML Editing", "The XML text editor will be disabled and the contents of the XML will be reset to the original XML generated from your *.vdb" +
						"\n\n Click OK to continue or Cancel");
				
				if( result ) {
					ignoreSelectionListener = true;
					allowEditXmlButton.setSelection(false);
					ignoreSelectionListener = false;
					vdbManager.setEditXmlMode(allowEditXmlButton.getSelection());
					
					setXmlEditiable(allowEditXmlButton.getSelection());
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});


        xmlContentsBox = new StyledText(theGroup, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().align(GridData.CENTER, GridData.END).span(2, 1).grab(true, true).minSize(400, 300).applyTo(xmlContentsBox);

        xmlContentsBox.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        xmlContentsBox.setEditable(false);
        xmlContentsBox.setFont(monospaceFont);
        
//        setXmlContents(vdbManager.getStatus().getMessage());
        
        xmlContentsBox.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( xmlContentsBox.getEditable()) {
					vdbManager.setXmlFileContents(xmlContentsBox.getText());
				}
				
				validatePage();
			}
		});

    }
    
    
    private void setXmlEditiable(boolean canEdit) {
    	if( ignoreSelectionListener ) return;
    	
		xmlContentsBox.setEditable(canEdit);
		if( canEdit ) {
			vdbManager.setEditXmlMode(true);
			xmlContentsBox.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		} else {
			vdbManager.setEditXmlMode(false);
			xmlContentsBox.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		}
		// need to set all other values false

		if( canEdit ) {
			dynamicVdbName.setEditable(false);
			dynamicVdbName.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
			vdbVersionText.setEditable(false);
			vdbVersionText.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		    excludeSourceDdlButton.setEnabled(false);
		    suppressDefaultAttributesOption.setEnabled(false);
			
		} else {
			dynamicVdbName.setEditable(true);
			dynamicVdbName.setBackground(dynamicVdbFileName.getBackground());
			vdbVersionText.setEditable(true);
			vdbVersionText.setBackground(dynamicVdbFileName.getBackground());
		    excludeSourceDdlButton.setEnabled(true);
		    suppressDefaultAttributesOption.setEnabled(true);
		}
		resetButton.setEnabled(canEdit);
		if( !canEdit ) {
			vdbManager.generate();
            //IStatus status = vdbManager.getStatus();
            setXmlContents(getVdbXml());
		}
		resetButton.setEnabled(canEdit);
    }
    
    private String getVdbXml() {
    	try {
			return vdbManager.getDynamicVdbXml();
		} catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            ErrorHandler.toExceptionDialog(ex);
		}
    	
    	return "<ERROR generating Dynamic VDB XML>";
    }
    
    /*
     * Create the VDB Deploy button 
     */
    private void createSaveFilePanel(Composite parent) {
    	
        Composite buttonPanel = new Composite(parent,SWT.NONE);
        buttonPanel.setLayout(new GridLayout(4, false));
        GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(buttonPanel);
        
        workspaceRB = new Button(buttonPanel, SWT.RADIO | SWT.SELECTED);
        workspaceRB.setText(Messages.GenerateDynamicVdbDialog_saveToWorkspace); //$NON-NLS-1$
        workspaceRB.setSelection(vdbManager.isSaveToWorkspace());
        workspaceRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				vdbManager.setSaveToWorkspace(workspaceRB.getSelection());
				if( vdbManager.isSaveToWorkspace() ) {
					if( vdbManager.getOutputLocation() != null ) {
			        	locationText.setText(vdbManager.getOutputLocation().getFullPath().makeAbsolute().toString());
					} else {
						 locationText.setText(Messages.GenerateDynamicVdbDialog_locationUndefined); //$NON-NLS-1$
					}
				}
				validatePage();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        fileSystemRB = new Button(buttonPanel, SWT.RADIO);
        fileSystemRB.setText(Messages.GenerateDynamicVdbDialog_saveToFileSystem); //$NON-NLS-1$
        fileSystemRB.setSelection(!vdbManager.isSaveToWorkspace());
        fileSystemRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				vdbManager.setSaveToWorkspace(workspaceRB.getSelection());
				if( StringUtilities.isNotEmpty(vdbManager.getFileSystemFolder()) ) {
		        	locationText.setText(vdbManager.getFileSystemFolder());
				} else {
					 locationText.setText(Messages.GenerateDynamicVdbDialog_locationUndefined); //$NON-NLS-1$
				}
				validatePage();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
		});

        WidgetFactory.createLabel(buttonPanel, "                              ");
        
        final Button overwriteExistingOption = WidgetFactory.createButton(buttonPanel,
                Messages.GenerateDynamicVdbDialog_overwriteFilesOptionLabel,
                GridData.FILL_HORIZONTAL, 2, SWT.CHECK);
        overwriteExistingOption.setSelection(vdbManager.overwriteExistingFiles());
        
		GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).span(1, 1).grab(true, false).applyTo(overwriteExistingOption);
		
		overwriteExistingOption.setToolTipText(Messages.GenerateDynamicVdbDialog_overwriteVDBOptionTooltip);
		overwriteExistingOption.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			vdbManager.setOverwriteExistingFiles(overwriteExistingOption.getSelection());
			validatePage();
			}
		});
        
        Composite locationPanel = new Composite(buttonPanel,SWT.NONE);
        locationPanel.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().span(4, 1).grab(true, false).applyTo(locationPanel);
        
        WidgetFactory.createLabel(locationPanel, GridData.VERTICAL_ALIGN_CENTER, Messages.GenerateDynamicVdbDialog_location); //$NON-NLS-1$
        final String name = "";
        locationText = WidgetFactory.createTextField(locationPanel, GridData.FILL_HORIZONTAL, 1, name, SWT.READ_ONLY);
        locationText.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        locationText.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(locationText);
        locationText.setText(Messages.GenerateDynamicVdbDialog_locationUndefined); //$NON-NLS-1$
        if( vdbManager.getOutputLocation() != null ) {
        	locationText.setText(vdbManager.getOutputLocation().getFullPath().makeAbsolute().toString());
        }
        
        browseForLocation = new Button(locationPanel, SWT.PUSH);
        browseForLocation.setText(Messages.GenerateDynamicVdbDialog_browse); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).applyTo(browseForLocation);
        browseForLocation.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( workspaceRB.getSelection() ) {
            		browseForWorkspaceContainer();
            	} else {
            		browseForFileSystemFolder();
            	}
            	validatePage();
            }

        });
        
    }
    
    /**
     * Export the current string content of the DDL display to a user-selected file on file system
     */
    public void browseForFileSystemFolder() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*-vdb.xml*"}); //$NON-NLS-1$
        String ddlFileName = vdbManager.getOutputVdbFileName();
        dlg.setText(ddlFileName);
        dlg.setFileName(ddlFileName);
        String fileStr = dlg.open();
        
        if( fileStr != null ) { // Could be cancelled
	        vdbManager.setFileSystemFullPathAndFile(fileStr);
	        if( StringUtilities.isNotEmpty(fileStr) ) {
	        	locationText.setText(vdbManager.getFileSystemFolder());
	        }
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
        	vdbManager.setOutputLocation(container);
        	locationText.setText(container.getFullPath().makeAbsolute().toString());
        }
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
    
    /* 
     * Validate the page
     */
    private void validatePage() {
        this.vdbManager.validate();
        IStatus status = vdbManager.getStatus();
        if (status.getSeverity() == IStatus.ERROR) {
            this.setErrorMessage(status.getMessage());
            this.getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else if (status.getSeverity() == IStatus.WARNING) {
            this.setErrorMessage(null);
            this.getButton(IDialogConstants.OK_ID).setEnabled(true);
            this.setMessage(Messages.GenerateDynamicVdbDialog_okMessage);
        } else {
            setErrorMessage(null);
            this.getButton(IDialogConstants.OK_ID).setEnabled(true);
            this.setMessage(Messages.GenerateDynamicVdbDialog_okMessage);
        }
    }

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Control control = super.createContents(parent);
		
		resetDynamicVdb();
		
		return control;
	}
    
    
}
