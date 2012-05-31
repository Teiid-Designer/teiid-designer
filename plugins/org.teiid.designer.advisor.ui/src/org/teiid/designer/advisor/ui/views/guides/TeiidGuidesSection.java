/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.views.guides;

import java.util.Properties;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.actions.AdvisorActionInfo;
import org.teiid.designer.advisor.ui.actions.AdvisorActionProvider;
import org.teiid.designer.advisor.ui.actions.AdvisorGuides;

import com.metamatrix.modeler.ui.viewsupport.PropertiesContextManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class TeiidGuidesSection  implements AdvisorUiConstants {
	private FormToolkit toolkit;

	private Section section;
	private Composite sectionBody;
	

	private Combo actionGroupCombo;
    private TreeViewer guidesViewer;
    private Hyperlink executeLink;
    private Text descriptionText;
    private AdvisorActionProvider actionProvider;
    private AdvisorGuides guides;
    
    private final PropertiesContextManager propertiesManager = new PropertiesContextManager();

	/**
	 * @param parent
	 * @param style
	 */
	public TeiidGuidesSection(FormToolkit toolkit, Composite parent) {
		super();
		this.toolkit = toolkit;

        this.actionProvider = new AdvisorActionProvider();
        this.guides = new AdvisorGuides();
        
		createSection(parent);
	}
	
	@SuppressWarnings("unused")
	private void createSection(Composite theParent) {
		SECTION : {
	        section = this.toolkit.createSection(theParent, Section.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED );
	        Color bkgdColor = this.toolkit.getColors().getBackground();
	        section.setText(Messages.ActionSets);
	        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true); //GridData.FILL_HORIZONTAL); // | GridData.HORIZONTAL_ALIGN_BEGINNING);
	        gd.horizontalSpan = 2;
	        section.setLayoutData(gd);
	
	        sectionBody = new Composite(section, SWT.NONE);
	        sectionBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        sectionBody.setBackground(bkgdColor);
	        GridLayout layout = new GridLayout(2, false);
	        layout.horizontalSpacing = 1;
	        layout.marginHeight = 1;
	        layout.marginWidth = 1;
	        sectionBody.setLayout(layout);
		}

		EXECUTE_LINK : {
			
			this.executeLink = this.toolkit.createHyperlink(sectionBody, Messages.ExecuteSelectedAction, SWT.WRAP);
			this.toolkit.adapt(this.executeLink, true, true);
			this.executeLink.setEnabled(false);
			this.executeLink.setToolTipText(Messages.ExecuteSelectedAction_tooltip);
			this.executeLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent theEvent) {
	            	IStructuredSelection selection = (IStructuredSelection)guidesViewer.getSelection();
	            	if( selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof AdvisorActionInfo ) {
						String actionId = ((AdvisorActionInfo)selection.getFirstElement()).getId();
						launchGuidesAction(actionId);
	            	}
				}
			});
			this.executeLink.setBackground(this.section.getBackground());
		}
	
		
		ACTION_COMBO : {
			actionGroupCombo = new Combo(sectionBody, SWT.NONE | SWT.READ_ONLY);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalAlignment = GridData.CENTER;
			gd.horizontalSpan = 2;
			actionGroupCombo.setLayoutData(gd);
			
			WidgetUtil.setComboItems(actionGroupCombo, this.guides.getCategories(), null, true);
			actionGroupCombo.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( SelectionEvent ev ) {
	            	selectComboItem(actionGroupCombo.getSelectionIndex());
	            }
	        });
		}
		
		GUIDES_VIEWER : {
	        guidesViewer =  WidgetFactory.createTreeViewer(sectionBody, SWT.NONE | SWT.SINGLE );
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			gd.heightHint = 180;
			guidesViewer.getControl().setLayoutData(gd);
			
	        guidesViewer.addDoubleClickListener(new IDoubleClickListener() {
				
				@Override
				public void doubleClick(DoubleClickEvent event) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					if( selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof AdvisorActionInfo ) {
						String actionId = ((AdvisorActionInfo)selection.getFirstElement()).getId();
						launchGuidesAction(actionId);
					}
				}
			});
	        guidesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					if( selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof AdvisorActionInfo ) {
						String desc = ((AdvisorActionInfo)selection.getFirstElement()).getDescription();
						if( desc != null ) {
							descriptionText.setText(desc);
						} else {
							descriptionText.setText(Messages.NoActionSelected);
						}
						executeLink.setEnabled(true);
					} else {
						descriptionText.setText(Messages.NoActionSelected);
						executeLink.setEnabled(false);
					}
					
				}
			});
	        guidesViewer.setLabelProvider(this.actionProvider);
	        guidesViewer.setContentProvider(this.actionProvider);
	        guidesViewer.setInput(guides.getChildren(AdvisorGuides.MODEL_JDBC_SOURCE));
	        guidesViewer.getTree().addMouseTrackListener(new MouseTrackAdapter() {
	        	/**
	        	 * Sent when the mouse pointer hovers (that is, stops moving
	        	 * for an (operating system specified) period of time) over
	        	 * a control.
	        	 * The default behavior is to do nothing.
	        	 *
	        	 * @param e an event containing information about the hover
	        	 */
	        	@Override
	        	public void mouseHover(MouseEvent e) {
	        		guidesViewer.getTree().setToolTipText(Messages.DoubleClickToExecuteAction);
	        	}
			});


		}

		
		DESCRIPTION : {
			// Add widgets to page
			Group descriptionGroup = WidgetFactory.createGroup(sectionBody, "Description", GridData.FILL_HORIZONTAL, 3); //$NON-NLS-1$

	        descriptionText = new Text(descriptionGroup,  SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
	        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
	        gd.heightHint = 60;
	        gd.widthHint = 200;
	        gd.horizontalSpan = 2;
	        descriptionText.setLayoutData(gd);
	        descriptionText.setBackground(sectionBody.getBackground());
	        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	        descriptionText.setText(Messages.NoActionSelected);
		}

        section.setClient(sectionBody);
        
        this.executeLink.setEnabled(!guidesViewer.getSelection().isEmpty());
        selectComboItem(getInitialComboSelectionIndex());
        
	}

    private void selectComboItem(int selectionIndex) {
    	if( selectionIndex >=0 ) {
    		actionGroupCombo.select(selectionIndex);
    		String categoryId = actionGroupCombo.getItem(selectionIndex);

    		this.guidesViewer.setInput(guides.getChildren(categoryId));

    	}
    }
    
    private int getInitialComboSelectionIndex() {
    	int index = 0;

    	for( String item : actionGroupCombo.getItems()) {
    		if( AdvisorGuides.MODEL_JDBC_SOURCE.equalsIgnoreCase(item)) {
    			return index; 
    		}
    		index++;
    	}
    	
    	return -1;
    }

    private void launchGuidesAction(String actionId) {
    	String guideId = actionGroupCombo.getText();
    	Properties properties = propertiesManager.getProperties(guideId);
    	AdvisorActionFactory.executeAction(actionId, properties, true);
    }
}
