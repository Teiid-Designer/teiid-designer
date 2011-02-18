/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.paste;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.uml2.uml.Element;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.ModelerModelGeneratorUiPlugin;
import com.metamatrix.modeler.modelgenerator.ui.wizards.PasteSpecialUmlToRelationalWizard;
import com.metamatrix.modeler.ui.actions.IPasteSpecialContributor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * Uml2RelationalPasteContributor is the IPasteSpecialContributor for generating virtual
 * Relational model objects from UML2 model objects.
 */
public class Uml2RelationalPasteContributor implements IPasteSpecialContributor,ModelGeneratorUiConstants {

    private boolean enable = false;
    private ModelResource virtualModelResource;
    private EObject virtualModelObject;

    /**
     * Construct an instance of Uml2RelationalPasteContributor.
     */
    public Uml2RelationalPasteContributor() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IPasteSpecialContributor#canPaste()
     */
    public boolean canPaste() {
        if ( enable ) {
            Object target = virtualModelObject;
            if ( target == null ) target = virtualModelResource;

            try {
                Collection clipboardContents = ModelerCore.getModelEditor().getClipboardContents(target);
                // If nothing on clipboard, disable
                if(clipboardContents.isEmpty()) {
                    enable = false;
                // Check all clipboard contents to make sure they are all UML entities
                } else {
                    Iterator iter = clipboardContents.iterator();
                    while (iter.hasNext() && enable) {
                        Object o = iter.next();
                        if ( ! (o instanceof Element) ) {
                            enable = false;
                            break;
                        }
                    }
                }

            } catch (ModelerCoreException e) {
                Util.log(e);
            }

        }
        return enable;
    }

    /**
     * checks the target selection and determines if it is valid.  It must be a Relational Model.  Can
     * either be a physical or virtual model (no longer reqd to be virtual).
     * If valid, sets the virtualModelResource, virtualModelObject fields and sets enable to true.  If not
     * valid, sets the virtualModelResource, virtualModelObject fields to null and sets enable to false
     */
    private void resetTargetSelection(ISelection selection) {
        this.virtualModelResource = null;
        this.virtualModelObject = null;
        this.enable = false;

        Object obj = SelectionUtilities.getSelectedObject(selection);
        // selection must be a relational model
        if ( ( obj instanceof IFile ) ) {
            if ( ! ((IFile) obj).isReadOnly() && ModelUtilities.isModelFile((IFile) obj) ) {
                try {
                    ModelResource modelResource = ModelUtil.getModelResource((IFile)obj, false);
                    MetamodelDescriptor descriptor = modelResource.getPrimaryMetamodelDescriptor();
                    if ( descriptor != null ) {
                        final String uri = descriptor.getNamespaceURI();
                        if ( uri != null && uri.equals(RelationalPackage.eNS_URI) ) {
                            virtualModelResource = modelResource;
                            enable = true;
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    Util.log(e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        resetTargetSelection(selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        try {
            IWorkbenchWindow window = ModelerModelGeneratorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            List originalObjs = getClipboardOriginalObjects();
            PasteSpecialUmlToRelationalWizard wizard = new PasteSpecialUmlToRelationalWizard(this.virtualModelResource,originalObjs);
            wizard.init(window.getWorkbench(), null);
            WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
            dialog.open();
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        resetTargetSelection(selection);
    }

    /*
     * get the original copied objects from the clipboard.  Uses the original to copies map to get the originals.
     * @return the list of original objects
     */
    private List getClipboardOriginalObjects() {
        List resultList = new ArrayList();

        // Get the Clipboard Contents
        Object target = virtualModelObject;
        if ( target == null ) target = virtualModelResource;
        Map theMap = null;
        try {
            // Use the Map to get the original copied objects
            theMap = ModelerCore.getModelEditor().getClipboardContentsCopyToOriginalMapping(target);
        } catch (ModelerCoreException e) {
            Util.log(e);
        }

        Iterator iter = theMap.keySet().iterator();
        while(iter.hasNext()) {
            Object keyObj = iter.next();
            resultList.add(theMap.get(keyObj));
        }

        return resultList;
    }
}
