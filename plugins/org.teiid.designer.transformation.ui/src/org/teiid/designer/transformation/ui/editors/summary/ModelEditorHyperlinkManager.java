/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.transformation.ui.actions.GenerateDependencyReportAction;
import org.teiid.designer.transformation.ui.actions.ShowDependencyDiagramAction;
import org.teiid.designer.ui.actions.ModelResourceActionManager;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.actions.Messages;
import org.teiid.designer.ui.common.actions.ModelActionConstants;

public class ModelEditorHyperlinkManager implements ModelActionConstants, ISelectionChangedListener {
    
	final ModelEditorMainPage editorPage;
	final FormToolkit toolkit;
    final ModelResource modelResource;
    final IResource iResource;
    final boolean isRelational;
    final boolean isVirtual;
    static ShowDependencyDiagramAction showDiagramAction = new ShowDependencyDiagramAction();
    static GenerateDependencyReportAction depReportAction = new GenerateDependencyReportAction();

	public ModelEditorHyperlinkManager(ModelEditorMainPage editorPage) {
		this.editorPage = editorPage;
		this.toolkit = editorPage.toolkit;
		this.modelResource = editorPage.modelResource;
		this.iResource = editorPage.iResource;
		
		isRelational = editorPage.isRelational;
		isVirtual = editorPage.isVirtual;
	}
	
	public void addConnectionHyperLinkActions(Composite parent) {
		
		if( isRelational && ! isVirtual ) {
			// Add Create Data Source and View PRofile actions
			createModelHyperLink(parent, 
					Messages.CREATE_DATA_SOURCE,
					Messages.CREATE_DATA_SOURCE_TOOLTIP, 
					Resource.CREATE_DATA_SOURCE);
			createModelHyperLink(parent, 
					Messages.VIEW_CONNECTION_INFO, 
					Messages.VIEW_CONNECTION_PROFILE_TOOLTIP, 
					Resource.VIEW_CONNECTION_PROFILE);
			createModelHyperLink(parent, 
					Messages.SET_CONNECTION_PROFILE, 
					Messages.SET_CONNECTION_PROFILE_TOOLTIP, 
					Resource.SET_CONNECTION_PROFILE);
			createModelHyperLink(parent, 
					Messages.REMOVE_CONNECTION_INFO, 
					Messages.REMOVE_CONNECTION_INFO_TOOLTIP, 
					Resource.REMOVE_CONNECTION_INFO);
			createModelHyperLink(parent, 
					Messages.SET_JBOSS_DATA_SOURCE_NAME, 
					Messages.SET_JBOSS_DATA_SOURCE_NAME_TOOLTIP, 
					Resource.SET_JBOSS_DATA_SOURCE_NAME);
			createModelHyperLink(parent, 
					Messages.SET_TRANSLATOR_NAME, 
					Messages.SET_TRANSLATOR_NAME_TOOLTIP, 
					Resource.SET_TRANSLATOR_NAME);
			createModelHyperLink(parent, 
					Messages.EDIT_TRANSLATOR_OVERRIDES, 
					Messages.EDIT_TRANSLATOR_OVERRIDES_TOOLTIP, 
					Resource.EDIT_TRANSLATOR_OVERRIDES);
		}

	}
	
	public void addPrimaryHyperLinkActions(Composite parent) {
		createWizardHyperlink(parent,  
				Messages.EXPORT_TEIID_DDL, 
				Messages.EXPORT_TEIID_DDL_TOOLTIP, 
				WizardsIDs.EXPORT_TEIID_DDL);
		createWizardHyperlink(parent,  
				Messages.CREATE_VDB, 
				Messages.NEW_VDB_TOOLTIP, 
				WizardsIDs.NEW_VDB);
		if( isRelational && isVirtual ) {
			createModelHyperLink(parent, 
					Messages.EDIT_TRANSFORMATION, 
					Messages.EDIT_TRANSFORMATION_TOOLTIP, 
					Resource.EDIT_TRANSFORMATION);
		}
	}
	
	public void addGeneralHyperLinkActions(Composite parent) {
		createModelHyperLink(parent, 
				Messages.MANAGE_EXTENSIONS, 
				Messages.MANAGE_MODEL_EXTENSIONS_TOOLTIP, 
				Resource.MANAGE_MODEL_EXTENSIONS);
		createModelHyperLink(parent, 
				Messages.SHOW_MODEL_STATISTICS, 
				Messages.SHOW_MODEL_STATISTICS_ACTION_TOOLTIP, 
				Resource.SHOW_MODEL_STATISTICS_ACTION);
	}
	
	private void createWizardHyperlink(Composite parent, String label, String toolTip, String wizardId) {
		Hyperlink newHL = toolkit.createHyperlink(parent, label, SWT.NONE); //$NON-NLS-1$
		if( toolTip != null ) {
			newHL.setToolTipText(toolTip);
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(newHL);

		newHL.addHyperlinkListener(new LaunchWizardHyperlinkHandler(iResource, wizardId));
	}
	
	private void createModelHyperLink(Composite parent, String label, String toolTip, String actionId) {
		SortableSelectionAction action = ModelResourceActionManager.getAction(actionId);
		
		if( action != null ) {
			Hyperlink newHL = toolkit.createHyperlink(parent, label, SWT.NONE); //$NON-NLS-1$
			if( toolTip != null ) {
				newHL.setToolTipText(toolTip);
			}
			GridDataFactory.fillDefaults().grab(true, false).applyTo(newHL);
	
			newHL.addHyperlinkListener(new IHyperlinkListener() {
				
				@Override
				public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				}
				
				@Override
				public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				}
				
				@Override
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					action.selectionChanged(editorPage, new StructuredSelection(iResource));
					action.run();
				}
			});
		}
	}
	
	public boolean isVirtual() {
		return isVirtual;
	}
	
	public boolean isRelational() {
		return isRelational;
	}

	public static IAction getAction(String actionID) {
		if( actionID.equalsIgnoreCase(ModelActionConstants.Custom.GENERATE_DEPENDENCY_REPORT) ) {
			return depReportAction;
		} else if( actionID.equalsIgnoreCase(ModelActionConstants.Custom.SHOW_DEPENCENCY_DIAGRAM) ) {
			return showDiagramAction;
		}
		
		return null;
	}
	
	public static Collection<IAction> getCustomActions(Object eObject) {
		Collection<IAction> actions = new ArrayList<IAction>();
		if( depReportAction.isEnabled() ) {
			actions.add(depReportAction);
		}
		if( showDiagramAction.isEnabled() ) {
			actions.add(showDiagramAction);
		}
		
		return actions;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		depReportAction.selectionChanged(event);
		showDiagramAction.selectionChanged(event);
	}

}
