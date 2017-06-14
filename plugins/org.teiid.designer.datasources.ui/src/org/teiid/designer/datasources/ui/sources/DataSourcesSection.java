/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.datasources.ui.Messages;


public class DataSourcesSection {
    private FormToolkit toolkit;
    
    ConnectionProfilesPanel dataSourcesPanel;

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
        
        createProfilesTab(parent);
        
        setEnabledState();

    }
    
    private void createProfilesTab(Composite parent ) { //CTabFolder tabFolder) {
    	Section section;
    	Composite sectionBody;
      
      int nColumns = 2;

        section = this.toolkit.createSection(parent, 
        		ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT ); //| Section.TWISTIE | Section.EXPANDED  );
        
        section.setExpanded(false);
        section.setText(Messages.LocalAndDeployedConnections);
        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
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

  
        dataSourcesPanel = new ConnectionProfilesPanel(sectionBody);
	        
      sectionBody.pack(true);
      section.setClient(sectionBody);

      section.setExpanded(true);
    }

    private void setEnabledState() {

    }
    
    public void refresh() {
    	dataSourcesPanel.refresh();
    }
}
