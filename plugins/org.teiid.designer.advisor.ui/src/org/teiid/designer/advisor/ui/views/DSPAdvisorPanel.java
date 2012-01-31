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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.AdvisorHyperLinkListener;
import org.teiid.designer.advisor.ui.core.InfoPopAction;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
public class DSPAdvisorPanel extends ManagedForm
	implements DSPStatusListener, AdvisorUiConstants.Images, AdvisorUiConstants.Groups { // IPropertyChangeListener,

    FormToolkit toolkit;

    private ScrolledForm parentForm;
    private DSPStatusSection statusSection;
    // private DSPCheatSheetSection cheatSheetSection;

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

        this.linkListener = new AdvisorHyperLinkListener(this.getForm(), this.toolkit, this.actionHandler);

        parentForm.setBackground(toolkit.getColors().getBackground());
        // parentForm = toolkit.createForm(this);

        this.parentForm.setText(DSPAdvisorI18n.TeiidProjectAdvisor);

        this.parentForm.setLayout(new GridLayout());
        // parentForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.parentForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        FormUtil.tweakColors(toolkit, parentForm.getDisplay());
        this.parentForm.setBackground(toolkit.getColors().getBackground());

        // statusSection = createStatusSection(parentForm.getBody());
        this.statusSection = new DSPStatusSection(toolkit, parentForm.getBody(), this.linkListener);

        // Add Simple Separator using a blank label
        this.toolkit.createLabel(parentForm.getBody(), null);

        // this.cheatSheetSection =
        new DSPCheatSheetSection(toolkit, parentForm.getBody());

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

}
