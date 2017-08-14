/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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

        section = this.toolkit.createSection(parent, 
        		ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT | Section.EXPANDED);
        
        section.setText(Messages.LocalAndDeployedConnections);
        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
        gd.horizontalSpan = 2;
        section.setLayoutData(gd);

        sectionBody = toolkit.createComposite(section);

        GridLayoutFactory.swtDefaults().numColumns(2).spacing(3,3).applyTo(sectionBody);
        GridDataFactory.swtDefaults().grab(true, true).align(GridData.BEGINNING,  GridData.CENTER).applyTo(sectionBody);;
  
        dataSourcesPanel = new ConnectionProfilesPanel(sectionBody);
	        
      sectionBody.pack(true);
      section.setClient(sectionBody);

    }

    private void setEnabledState() {

    }
    
    public void refresh() {
    	dataSourcesPanel.refresh();
    }
}
