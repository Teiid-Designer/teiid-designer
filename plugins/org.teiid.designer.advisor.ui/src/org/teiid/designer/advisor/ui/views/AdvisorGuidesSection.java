/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.actions.AdvisorActionInfo;
import org.teiid.designer.advisor.ui.actions.AdvisorActionProvider;
import org.teiid.designer.advisor.ui.actions.AdvisorCheatSheets;
import org.teiid.designer.advisor.ui.actions.AdvisorGuides;

import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class AdvisorGuidesSection  implements AdvisorUiConstants {
	private FormToolkit toolkit;

	private Section section;
	private Composite sectionBody;
	
	Button useGuidesRB;
	Button useCheatSheetsRB;

	private Combo actionGroupCombo;
    private TreeViewer guidesViewer;
    private AdvisorActionProvider actionProvider;
    private AdvisorGuides guides;
    private AdvisorCheatSheets cheatSheets;

	/**
	 * @param parent
	 * @param style
	 */
	public AdvisorGuidesSection(FormToolkit toolkit, Composite parent) {
		super();
		this.toolkit = toolkit;

        this.actionProvider = new AdvisorActionProvider();
        this.guides = new AdvisorGuides();
        this.cheatSheets = new AdvisorCheatSheets();
        
		createSection(parent);
	}
	
	@SuppressWarnings("unused")
	private void createSection(Composite theParent) {
		SECTION : {
	        section = this.toolkit.createSection(theParent, Section.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED );
	        Color bkgdColor = this.toolkit.getColors().getBackground();
	        section.setText(Messages.Guides);
	        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true); //GridData.FILL_HORIZONTAL); // | GridData.HORIZONTAL_ALIGN_BEGINNING);
	        gd.horizontalSpan = 2;
	        section.setLayoutData(gd);
	
	        sectionBody = new Composite(section, SWT.NONE);
	        sectionBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        sectionBody.setBackground(bkgdColor);
	        GridLayout layout = new GridLayout();
	        layout.horizontalSpacing = 1;
	        layout.marginHeight = 1;
	        layout.marginWidth = 1;
	        sectionBody.setLayout(layout);
		}
	
		GUIDES_OPTION : {
			Composite panel = WidgetFactory.createPanel(sectionBody);
			GridLayout layout = new GridLayout(2, false);
	        layout.marginHeight = 1;
	        layout.marginWidth = 1;
			panel.setLayout(layout);
			panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// Create Radio Buttons for "Quick Lists" and "Cheat Sheets"
			this.useGuidesRB = WidgetFactory.createRadioButton(panel, Messages.CommonActionSets, true);
			this.useGuidesRB.addSelectionListener(new SelectionAdapter() {

	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	// If this is selected, then need to set guides viewer to:
	        		if( useGuidesRB.getSelection() ) {
	        			WidgetUtil.setComboItems(actionGroupCombo, guides.getCategories(), null, true);
	        			selectComboItem(getInitialComboSelectionIndex());
	        		} 
	            }
	        });
			// Add listener to re-set the content of the "Guides Viewer"
			this.useCheatSheetsRB = WidgetFactory.createRadioButton(panel, Messages.CheatSheets);
			this.useCheatSheetsRB.addSelectionListener(new SelectionAdapter() {

	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	        		if( useCheatSheetsRB.getSelection() ) {
	        			WidgetUtil.setComboItems(actionGroupCombo, cheatSheets.getCategories(), null, true);
	        			selectComboItem(getInitialComboSelectionIndex());
	        		} 
	            }
	        });
		}
		
		GUIDES_VIEWER : {
	        
			actionGroupCombo = new Combo(sectionBody, SWT.NONE | SWT.READ_ONLY);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalAlignment = GridData.CENTER;
			gd.horizontalSpan = 1;
			actionGroupCombo.setLayoutData(gd);
			
			WidgetUtil.setComboItems(actionGroupCombo, this.guides.getCategories(), null, true);
			actionGroupCombo.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( SelectionEvent ev ) {
	            	selectComboItem(actionGroupCombo.getSelectionIndex());
	            }
	        });
	        
	        guidesViewer =  WidgetFactory.createTreeViewer(sectionBody, SWT.NONE | SWT.SINGLE);
	        guidesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					if( selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof AdvisorActionInfo ) {
						String actionId = ((AdvisorActionInfo)selection.getFirstElement()).getId();
						AdvisorActionFactory.executeAction(actionId, true);
					}
					
				}
			});
	        guidesViewer.setLabelProvider(this.actionProvider);
	        guidesViewer.setContentProvider(this.actionProvider);
	        guidesViewer.setInput(guides.getChildren(AdvisorGuides.MODEL_JDBC_SOURCE));

		}

        section.setClient(sectionBody);
        
        selectComboItem(getInitialComboSelectionIndex());
	}

    private void selectComboItem(int selectionIndex) {
    	if( selectionIndex >=0 ) {
    		actionGroupCombo.select(selectionIndex);
    		String categoryId = actionGroupCombo.getItem(selectionIndex);
    		if( this.useGuidesRB.getSelection() ) {
    			this.guidesViewer.setInput(guides.getChildren(categoryId));
    		} else {
    			this.guidesViewer.setInput(cheatSheets.getChildren(categoryId));
    		}
    	}
    }
    
    private int getInitialComboSelectionIndex() {
    	int index = 0;
    	if( this.useGuidesRB.getSelection() ) {
	    	for( String item : actionGroupCombo.getItems()) {
	    		if( AdvisorGuides.MODEL_JDBC_SOURCE.equalsIgnoreCase(item)) {
	    			return index; 
	    		}
	    		index++;
	    	}
    	} else {
	    	for( String item : actionGroupCombo.getItems()) {
	    		if( AdvisorCheatSheets.PROJECT_SETUP.equalsIgnoreCase(item)) {
	    			return index; 
	    		}
	    		index++;
	    	}
    	}
    	
    	return -1;
    }
}
