/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.modeler.internal.xml.ui.wizards.NumberOfLevelsWizard;
import com.metamatrix.modeler.ui.actions.INewChildAction;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;

/**
 * BuildFromSchemaAction
 */
public class BuildFromSchemaAction extends Action implements INewChildAction,
		ModelerXmlUiConstants {
	private XmlDocumentNode node;

	public BuildFromSchemaAction() {
		super(Util.getString("BuildFromSchemaAction.title")); //$NON-NLS-1$
		setToolTipText(Util.getString("BuildFromSchemaAction.toolTipText")); //$NON-NLS-1$
		setImageDescriptor(ModelerXmlUiPlugin.getDefault().getImageDescriptor(
				ModelerXmlUiConstants.Images.BUILD_FROM_XSD));
	}
	
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    public boolean canCreateChild(EObject parent) {
    	boolean canCreate = false;
		if (parent instanceof XmlDocumentNode) {
			if (parent.eContents().isEmpty()) {
  				XmlDocumentNode docNode = (XmlDocumentNode)parent;
				if (docNode.getXsdComponent() != null) {
    				canCreate = true;
    				this.node = docNode;
    			}
    		}
    	}
    	return canCreate;
    }

	public XmlDocumentNode getNode() {
		return this.node;
	}
		
	@Override
    public void run() {
		//We will just start up a NumberOfLevelsWizard.  The wizard itself handles results
		//processing in its performFinish() method.
		IWorkbenchWindow window = ModelerXmlUiPlugin.getDefault().getCurrentWorkbenchWindow();
		Shell shell = window.getShell();
		NumberOfLevelsWizard wizard = new NumberOfLevelsWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();
	}
}
