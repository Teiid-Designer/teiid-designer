/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datasources.ui.panels.DataSourcePanel;
import org.teiid.designer.datasources.ui.wizard.TeiidDataSourceManager;


public class DataSourcesSection {
    private FormToolkit toolkit;
    
    private Label serverLabel;
    
    ConnectionProfilesPanel dataSourcesPanel;
    
    private static final int SEPARATOR_HEIGHT = 3;

    // ------------ TEXT ----------------------------

    /**
     * 
     */
    public DataSourcesSection( FormToolkit toolkit,
                             Composite parent) {
        super();
        this.toolkit = toolkit;
        
        createSection(parent);
    }

	private void createSection(Composite parent) {
    	
//        Composite pnlTop = new Composite(parent, SWT.BORDER);
//        pnlTop.setLayout(new GridLayout());
//        pnlTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlTop);
        
//        createDataSourcesTab(tabFolder);
        
        createProfilesTab(parent);
        
        setEnabledState();

    }
    
    @SuppressWarnings("unused")
    private void createProfilesTab(Composite parent ) { //CTabFolder tabFolder) {
    	Section section;
    	Composite sectionBody;
    	
//        CTabItem theTab = new CTabItem(tabFolder, SWT.NONE);
//      modelsTab.setImage(VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.MODEL_ICON));
//        theTab.setText("Connections"); //$NON-NLS-1$
//      modelsTab.setToolTipText(i18n("modelsTabToolTip")); //$NON-NLS-1$
      
      int nColumns = 2;
      
      SECTION : {
	        section = this.toolkit.createSection(parent, 
	        		ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT ); //| Section.TWISTIE | Section.EXPANDED  );
	        
	        section.setExpanded(false);
	        section.setText("Connections");
	        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
	        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true); //GridData.FILL_HORIZONTAL); // | GridData.HORIZONTAL_ALIGN_BEGINNING);
	        gd.horizontalSpan = 2;
	        section.setLayoutData(gd);
	
	        sectionBody = toolkit.createComposite(section);
	
	        GridLayout layout = new GridLayout(2, false);
	        layout.numColumns = nColumns;
	        layout.verticalSpacing = 3;
	        layout.horizontalSpacing = 3;
	        sectionBody.setLayout(layout);

	        GridData bodyGD = new GridData(GridData.FILL_BOTH);
	        bodyGD.verticalAlignment = GridData.CENTER;
	        sectionBody.setLayoutData(bodyGD);
	        
//	        theTab.setControl(section);
      }
      
		SECTION_TOOLBAR : {
//          // configure section toolbar
//          Button[] buttons = FormUtil.createSectionToolBar(section, toolkit,
//                                                           new String[] { "Refresh" },
//                                                           new int[] { SWT.FLAT } );
//
//          Button refreshButton = buttons[0];
//          //this.enableStatusButton.setText("");
//          refreshButton.setSelection(true);
//          refreshButton.addSelectionListener(new SelectionAdapter() {
//
//              /**
//               * {@inheritDoc}
//               * 
//               * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//               */
//              @Override
//              public void widgetSelected( SelectionEvent e ) {
//            	  // call a refresh of the  running server
//            	  RefreshServerAction action = new RefreshServerAction();
//              }
//          });
//          refreshButton.setToolTipText("Refresh data sources deployed on your default server and connection profiles");
		}

//      PREJECT_LABEL : {
//	        
//	        serverLabel = new Label(sectionBody, SWT.NONE);
//	        serverLabel.setText("Connection Profiles");
//	        serverLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
//      }
      
      STATUS_ROWS : {
//	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

	        dataSourcesPanel = new ConnectionProfilesPanel(sectionBody);
	        
//	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

      }
      sectionBody.pack(true);
      section.setClient(sectionBody);

      section.setExpanded(true);
    }
    
    @SuppressWarnings("unused")
    private void createDataSourcesTab(CTabFolder tabFolder) {
    	Section section;
    	Composite sectionBody;
    	
        CTabItem theTab = new CTabItem(tabFolder, SWT.NONE);
//      modelsTab.setImage(VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.MODEL_ICON));
        theTab.setText("Data Sources"); //$NON-NLS-1$
//      modelsTab.setToolTipText(i18n("modelsTabToolTip")); //$NON-NLS-1$
      
      int nColumns = 2;
      
      SECTION : {
	        section = this.toolkit.createSection(tabFolder, 
	        		ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT ); //| Section.TWISTIE | Section.EXPANDED  );
	        
	        section.setExpanded(false);
	        section.setText("Data Sources");
	        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
	        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true); //GridData.FILL_HORIZONTAL); // | GridData.HORIZONTAL_ALIGN_BEGINNING);
	        gd.horizontalSpan = 2;
	        section.setLayoutData(gd);
	
	        sectionBody = toolkit.createComposite(section);
	
	        GridLayout layout = new GridLayout(2, false);
	        layout.numColumns = nColumns;
	        layout.verticalSpacing = 3;
	        layout.horizontalSpacing = 3;
	        sectionBody.setLayout(layout);

	        GridData bodyGD = new GridData(GridData.FILL_BOTH);
	        bodyGD.verticalAlignment = GridData.CENTER;
	        sectionBody.setLayoutData(bodyGD);
	        
	        theTab.setControl(section);
      }

      PREJECT_LABEL : {
	        
	        serverLabel = new Label(sectionBody, SWT.NONE);
	        String serverStr = "Server: <undefined>";
	        if( ModelerCore.getTeiidServerManager().isStarted() ) {
	        	serverStr = "SERVER: " + ModelerCore.getDefaultServerName();
	        }
	        serverLabel.setText(serverStr);
	        serverLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
      }
      
      STATUS_ROWS : {
//	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

	        TeiidDataSourceManager importManager  = new TeiidDataSourceManager();
	        if( importManager.isValidImportServer() ) {
	        	new DataSourcePanel(sectionBody, 10, importManager);
	        } else {
		        Label noServerLabel = new Label(sectionBody, SWT.NONE);
		        noServerLabel.setText("Server does not exist or is not connected");
		        noServerLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        }
	        
//	        createSeparator(sectionBody, nColumns, SEPARATOR_HEIGHT);

      }
      sectionBody.pack(true);
      section.setClient(sectionBody);

      section.setExpanded(true);
    }

    private void createSeparator( Composite parent,
                                  int nColumns,
                                  int height ) {
        Composite bottomSep = toolkit.createCompositeSeparator(parent);
        //TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, nColumns);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = nColumns;
        layoutData.heightHint = height;
        bottomSep.setLayoutData(layoutData);
    }

    
    private void setEnabledState() {

    }
    
    public void refresh() {
    	dataSourcesPanel.refresh();
    }
}
