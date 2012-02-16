/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.actions.AdvisorActionInfo;

import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class ActionsSection implements AdvisorUiConstants{
	private FormToolkit toolkit;

	private Section section;
	Composite stackBodyPanel;
	StackLayout stackLayout;
	
    private Combo actionGroupCombo;
	
	Map<String, Composite> stackedPanels;

	/**
	 * @param parent
	 * @param style
	 */
	public ActionsSection(FormToolkit toolkit, Composite parent) {
		super();
		this.toolkit = toolkit;
		stackedPanels = new HashMap<String, Composite>();
		createSection(parent);
	}
	
	private void createStackLayout(Composite parent) {
    	stackBodyPanel = new Composite(parent, SWT.NONE | SWT.FILL);
    	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    	//gd.horizontalSpan = 2;
    	stackLayout = new StackLayout();
    	stackLayout.marginWidth = 0;
    	stackLayout.marginHeight = 0;
    	stackBodyPanel.setLayout(stackLayout);
    	stackBodyPanel.setLayoutData(gd);
    	stackBodyPanel.setData("name", "stackBodyPanel");  //$NON-NLS-1$//$NON-NLS-2$
	}

	@SuppressWarnings("unused")
	private void createSection(Composite theParent) {

        Section generalSection = this.toolkit.createSection(theParent, Section.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED );

        Color bkgdColor = this.toolkit.getColors().getBackground();
        generalSection.setText(Messages.ActionLibrary);

//        generalSection.setDescription("Aspect Description....");
        
        GridData gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        generalSection.setLayoutData(gd);

        Composite sectionBody = new Composite(generalSection, SWT.NONE);
        sectionBody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        sectionBody.setBackground(bkgdColor);

        sectionBody.setLayout(new GridLayout());
        
        //Composite comboPanel = WidgetFactory.createGroup(sectionBody, "Select Category");
        Group comboPanel = new Group(sectionBody, SWT.SHADOW_ETCHED_IN);
        comboPanel.setText(Messages.SelectCategory);
        comboPanel.setFont(JFaceResources.getBannerFont());
        comboPanel.setLayout(new GridLayout());
        comboPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //WidgetFactory.createLabel(comboPanel, "LABEL_1");
        
		actionGroupCombo = new Combo(comboPanel, SWT.NONE | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.CENTER;
		actionGroupCombo.setLayoutData(gd);
		
		WidgetUtil.setComboItems(actionGroupCombo, Arrays.asList(AdvisorUiConstants.MODELING_ASPECT_LABELS_LIST), null, true);
		actionGroupCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
            	selectComboItem(actionGroupCombo.getSelectionIndex());
            }
        });

        createStackLayout(comboPanel);

        Composite panel1 = createPanel_1(stackBodyPanel);
        stackLayout.topControl = panel1;
        stackedPanels.put(MODELING_ASPECT_IDS.MODEL_DATA_SOURCES, panel1);
        stackedPanels.put(MODELING_ASPECT_IDS.MANAGE_CONNECTIONS, createPanel_2(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.MODEL_PROJECT_MANAGEMENT, createPanel_3(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.MANAGE_VDBS, createPanel_4(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.DEFINE_MODELS, createPanel_5(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.CONSUME_SOAP_WS, createPanel_6(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.CONSUME_REST_WS, createPanel_7(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.TEST, createPanel_8(stackBodyPanel));
        stackedPanels.put(MODELING_ASPECT_IDS.TEIID_SERVER, createPanel_9(stackBodyPanel));

        generalSection.setClient(sectionBody);

        actionGroupCombo.select(getInitialComboSelectionIndex());
        selectComboItem(getInitialComboSelectionIndex());
	}
	
    private void selectComboItem(int selectionIndex) {
    	if( selectionIndex >=0 ) {
    		String aspectId = actionGroupCombo.getItem(selectionIndex);
    		aspectChanged(aspectId);
    	}
    }
    
    private int getInitialComboSelectionIndex() {
    	int index = 0;
    	for( String item : actionGroupCombo.getItems()) {
    		if( AdvisorUiConstants.MODELING_ASPECT_LABELS.MODEL_PROJECT_MANAGEMENT.equalsIgnoreCase(item)) {
    			return index; 
    		}
    		index++;
    	}
    	
    	return -1;
    }
	
	private Composite createPanel_1(Composite parent) {
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        //addHyperlink(panel, COMMAND_LABELS.IMPORT_JDBC, COMMAND_IDS.IMPORT_JDBC);
        addHyperlink(panel, COMMAND_IDS.IMPORT_JDBC, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_DDL, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_FLAT_FILE, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_XML_FILE, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_SALESFORCE, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, false);
        addHyperlink(panel, COMMAND_IDS.IMPORT_WSDL_TO_WS, false);
        addHyperlink(panel, COMMAND_IDS.PREVIEW_DATA, false);
        
        return panel;
	}
	
	private Composite createPanel_2(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, COMMAND_LABELS.OPEN_DATA_SOURCE_EXPLORER_VIEW, COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW);
        
        Group group = WidgetFactory.createGroup(panel, Messages.CreateConnection, GridData.FILL_HORIZONTAL, 2, 2);
        
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_JDBC, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_SALESFORCE, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_MODESHAPE, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_LDAP, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, true);
        addHyperlink(group, COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA, true);

        return panel;
	}
	
	private Composite createPanel_3(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, Messages.CreateTeiidModelProject, COMMAND_IDS.NEW_TEIID_MODEL_PROJECT);

        
        return panel;
	}
	
	private Composite createPanel_4(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, Messages.CreateVdb, COMMAND_IDS.CREATE_VDB);
        addHyperlink(panel, Messages.EditVdb, COMMAND_IDS.EDIT_VDB);
        addHyperlink(panel, Messages.DeployVdb, COMMAND_IDS.DEPLOY_VDB);
        addHyperlink(panel, Messages.ExecuteVdb, COMMAND_IDS.EXECUTE_VDB);
        
        return panel;
	}
	
	private Composite createPanel_5(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        Group group = WidgetFactory.createGroup(panel, Messages.Create, GridData.FILL_HORIZONTAL, 2, 2);
        addHyperlink(group, COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, true);
        addHyperlink(group, COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW, true);
        addHyperlink(group, COMMAND_IDS.NEW_MODEL_WS, true);
        addHyperlink(group, COMMAND_IDS.NEW_MODEL_XML_DOC, true);
        
        addHyperlink(group, COMMAND_IDS.NEW_MODEL_MED, false);
        
        return panel;
	}
	
	private Composite createPanel_6(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, COMMAND_IDS.CREATE_WEB_SRVICES_DATA_FILE, false);
        addHyperlink(panel, COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA, false);
        addHyperlink(panel, COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL, false);
        
        return panel;
	}
	
	private Composite createPanel_7(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, COMMAND_IDS.CREATE_WEB_SRVICES_DATA_FILE, false);
        addHyperlink(panel, COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, false);
        addHyperlink(panel, COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL, false);
        
        return panel;
	}
	
	private Composite createPanel_8(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        addHyperlink(panel, Messages.PreviewData, COMMAND_IDS.PREVIEW_DATA);
        addHyperlink(panel, Messages.ExecuteVdb, COMMAND_IDS.EXECUTE_VDB);
        
        return panel;
	}
	
	private Composite createPanel_9(Composite parent) {
		// 
		Color bkgdColor = this.toolkit.getColors().getBackground();
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        panel.setLayout(new GridLayout());
        GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 2;
        gLayout.marginWidth = 5;
        gLayout.horizontalSpacing = 5;
        gLayout.verticalSpacing = 5;
        panel.setLayout(gLayout);
        panel.setBackground(bkgdColor);
        
        Group group = WidgetFactory.createGroup(panel, Messages.Server, GridData.FILL_HORIZONTAL, 2, 2);
        addHyperlink(group, COMMAND_IDS.NEW_TEIID_SERVER, true);
        addHyperlink(group, COMMAND_IDS.EDIT_TEIID_SERVER, true);
        
        addHyperlink(panel, Messages.CreateDataSource, COMMAND_IDS.CREATE_DATA_SOURCE);
        addHyperlink(panel, Messages.DeployVdb, COMMAND_IDS.DEPLOY_VDB);
        addHyperlink(panel, Messages.ExecuteVdb, COMMAND_IDS.EXECUTE_VDB);
        
        return panel;
	}
	
	public void aspectChanged(String aspectId) {
		if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.MODEL_PROJECT_MANAGEMENT) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.MODEL_PROJECT_MANAGEMENT);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.MANAGE_VDBS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.MANAGE_VDBS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.MODEL_DATA_SOURCES) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.MODEL_DATA_SOURCES);
		//} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.MODEL_VIEWS) ) {
		//	this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.MODEL_VIEWS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.MANAGE_CONNECTIONS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.MANAGE_CONNECTIONS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.CREATE_SOAP_WS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.CREATE_SOAP_WS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.CREATE_REST_WS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.CREATE_REST_WS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.CONSUME_SOAP_WS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.CONSUME_SOAP_WS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.CONSUME_REST_WS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.CONSUME_REST_WS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.DEFINE_MODELS) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.DEFINE_MODELS);
		} else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.TEST) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.TEST);
		}  else if( aspectId.equalsIgnoreCase(MODELING_ASPECT_LABELS.TEIID_SERVER) ) {
			this.stackLayout.topControl = stackedPanels.get(MODELING_ASPECT_IDS.TEIID_SERVER);
		} else {
			// Product Management
		}
		this.stackBodyPanel.layout();
		
	}
	
	private void addHyperlink(Composite parent, String actionId, boolean isSubMenu) {
		AdvisorActionInfo info = AdvisorActionFactory.getActionInfo(actionId);
		if( info != null ) {
			String text = isSubMenu ? info.getShortDisplayName() : info.getDisplayName();
			addHyperlink(parent, text, actionId);
		}
	}
	
	private void addHyperlink(Composite parent, String text, String actionId) {
		Label imageLabel = new Label(parent, SWT.NONE);
		String imageId = AdvisorActionFactory.getImageId(actionId);
		if( imageId != null ) {
			imageLabel.setImage(AdvisorUiPlugin.getDefault().getImage(imageId));
		}
		Hyperlink link = this.toolkit.createHyperlink(parent, text, SWT.WRAP);
		this.toolkit.adapt(link, true, true);

		// create link action
		final IAction action = new LinkAction(actionId);
		link.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent theEvent) {
				action.run();
			}
		});
	}

	/**
	 * @return section
	 */
	public Section getSection() {
		return section;
	}

	private class LinkAction extends Action {
		String linkId;

		public LinkAction(String id) {
			this.linkId = id;
		}

		@Override
		public void run() {

			AbstractHandler action = AdvisorActionFactory.getActionHandler(linkId);
			if( action != null ) {
				try {
					action.execute(null);
				} catch (ExecutionException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
	}
}