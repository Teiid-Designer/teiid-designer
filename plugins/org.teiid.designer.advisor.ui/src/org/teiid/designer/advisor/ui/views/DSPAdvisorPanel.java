/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.core.AdvisorHyperLinkListener;
import org.teiid.designer.advisor.ui.core.InfoPopAction;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class DSPAdvisorPanel extends ManagedForm
	implements DSPStatusListener, AdvisorUiConstants.Images, AdvisorUiConstants.Groups { // IPropertyChangeListener,

    FormToolkit toolkit;

    private ScrolledForm parentForm;
    private DSPStatusSection statusSection;

    private AdvisorHyperLinkListener linkListener;

    private DSPAdvisorActionHandler actionHandler;

    /**
     * @since 4.3
     */
    public DSPAdvisorPanel( Composite parent ) {
        super(parent);

        this.actionHandler = new DSPAdvisorActionHandler();
        this.parentForm = this.getForm();

        initGUI();

        AdvisorUiPlugin.getStatusManager().addListener(this);

        // ResourcesPlugin.getPlugin().getPluginPreferences().addPropertyChangeListener(this);

        for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            if (ModelerCore.hasModelNature(proj)) {
                setCurrentProject(proj);
                break;
            }
        }

    }

    private void initGUI() {
        this.parentForm.setLayout(new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_BOTH);
        this.parentForm.setLayoutData(gd);

        // addProjectComboSelector(this);

        this.toolkit = getToolkit();
        
        Color bkgdColor = toolkit.getColors().getBackground();

        this.linkListener = new AdvisorHyperLinkListener(this.getForm(), this.toolkit, this.actionHandler);

        parentForm.setBackground(bkgdColor);
        // parentForm = toolkit.createForm(this);

        this.parentForm.setText(DSPAdvisorI18n.TeiidProjectAdvisor);

        this.parentForm.setLayout(new GridLayout());
        // parentForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData formGD = new GridData(SWT.FILL, SWT.FILL, false, true);
        formGD.verticalAlignment = SWT.BEGINNING;
        this.parentForm.setLayoutData(formGD);

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(bkgdColor);
        
        Form form = this.parentForm.getForm();
        contributeToToolBar(form.getToolBarManager());
        contributeToMenu(form.getMenuManager());

        // statusSection = createStatusSection(parentForm.getBody());
        this.statusSection = new DSPStatusSection(toolkit, parentForm.getBody(), this.linkListener);

        // Add Simple Separator using a blank label
        //this.toolkit.createLabel(parentForm.getBody(), null);

        Composite body = parentForm.getBody();
		GridLayout gl = new GridLayout(2, false);
		body.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		body.setLayoutData(gd);
		
//		new ActionsSection(toolkit, body);
		
//        new DSPCheatSheetSection(toolkit, parentForm.getBody());

        AdvisorUiPlugin.getStatusManager().updateStatus(true);

    }

    @Override
    public FormToolkit getToolkit() {
        if (this.toolkit == null) {
            Display display = parentForm.getDisplay();
            if (AdvisorUiPlugin.getDefault() != null) {
                this.toolkit = AdvisorUiPlugin.getDefault().getFormToolkit(display);
            } else {
                this.toolkit = new FormToolkit(display);
            }
        }

        return this.toolkit;
    }

    public InfoPopAction[] getInfoPopActions( int groupType ) {
        return this.actionHandler.getActions(groupType);
    }

    public void notifyStatusChanged( final ModelProjectStatus theStatus ) {
    	if( this.statusSection.getSection().isDisposed() ) {
    		return;
    	}
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                updateStatus(theStatus);
            }
        });

    }

    /**
     * This private method handles updating the various status widgets in the summary table Items with problems get an X checkbox,
     * items without get a green checked checkbox. We also set the overall status for the summary section header and description.
     * 
     * @param status
     * @since 4.3
     */
    public void updateStatus( Status theStatus ) {
        CoreArgCheck.isInstanceOf(ModelProjectStatus.class, theStatus);

        this.statusSection.updateStatus(theStatus);

        this.actionHandler.setStatus((ModelProjectStatus)theStatus);
    }

    /**
     * @param currentProject Sets currentProject to the specified value.
     */
    public void setCurrentProject( IProject nextCurrentProject ) {
        if (AdvisorUiPlugin.getStatusManager().setCurrentProject(nextCurrentProject)) {
            forceUpdateStatus();
        }
    }

    private void forceUpdateStatus() {
        AdvisorUiPlugin.getStatusManager().updateStatus(true);
    }


    public void updateStatus( AdvisorStatus status ) {
    }

    private void contributeToMenu( IMenuManager menuMgr ) {
    	AdvisorActionFactory.addActionsLibraryToMenu(menuMgr);
        menuMgr.update(true);
    }

    private void contributeToToolBar( IToolBarManager toolBarMgr ) {
        Action selectProjectAction = new Action() {
            @Override
            public void run() {
                Object[] projects = WidgetUtil.showWorkspaceObjectSelectionDialog("Change Model Project", //$NON-NLS-1$
                        "Select Model Project for Advisor", //$NON-NLS-1$
                        false,
                        null,
                        new ModelingResourceFilter(
                                                   new ModelProjectViewFilter()),
                        new ModelProjectSelectionStatusValidator());
				if (projects.length > 0 && AdvisorUiPlugin.getStatusManager().setCurrentProject(((IProject)projects[0]))) {
					AdvisorUiPlugin.getStatusManager().updateStatus(true);
				}
            }
        };
        selectProjectAction.setToolTipText("Change Model Project"); //$NON-NLS-1$
        selectProjectAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.MODEL_PROJECT));
        toolBarMgr.add(selectProjectAction);
        toolBarMgr.update(true);
    }
    
}
