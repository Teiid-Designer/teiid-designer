/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDParticle;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * StatusBarUpdater
 */
public class StatusBarUpdater
implements ISelectionChangedListener, UiConstants {

    private IStatusLineManager statusLineManager;

    /**
     * Construct an instance of StatusBarUpdater.
     * 
     */
    public StatusBarUpdater(IStatusLineManager statusLineManager) {
        this.statusLineManager= statusLineManager;
    }
        
    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        String statusBarMessage= formatMessage(selection);
        statusLineManager.setMessage(statusBarMessage);
    }
    
    
    protected String formatMessage(ISelection sel) {
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            IStructuredSelection selection= (IStructuredSelection) sel;
            
            int nElements= selection.size();
            if (nElements > 1) {
                return Util.getString("StatusBarUpdater.num_elements_selected", String.valueOf(nElements)); //$NON-NLS-1$
            } 
            Object elem= selection.getFirstElement();
            if (elem instanceof EObject) {
                return formatEObjectMessage((EObject) elem);
            } else if (elem instanceof IResource) {
                return formatResourceMessage((IResource) elem);
            } else if (elem instanceof ImportContainer) {
                return ((ImportContainer)elem).toString();
            } else if(elem instanceof IExtendedModelObject ) {
                return ((IExtendedModelObject)elem).getStatusLabel();
            } else {
                return elem.getClass().getName();
            }
        }
        return "";  //$NON-NLS-1$
    }
        
    public static String formatEObjectMessage(EObject element) {
        String result;
        if ( element instanceof ENamedElement ) {
            result = element.eClass().getName();
            result += ": " + ((ENamedElement) element).getName(); //$NON-NLS-1$
        } else {
            if (element instanceof XSDParticle) {
                result = ((XSDParticle)element).getTerm().eClass().getName();
            } else if (element instanceof XSDAttributeUse) {
                result = ((XSDAttributeUse)element).getAttributeDeclaration().eClass().getName();
            } else {
                result = element.eClass().getName();
            }
            result += ": "; //$NON-NLS-1$
            result += ModelerCore.getModelEditor().getModelRelativePathIncludingModel(element);
        }
        
        return result;
    }
        
    private String formatResourceMessage(IResource element) {
        IContainer parent= element.getParent();
        if (parent != null && parent.getType() != IResource.ROOT)
            //return element.getFullPath() + " class = " + element.getClass().getName();
            return element.getRawLocation().toString();
        return element.getName() + Util.getString("StatusBarUpdater._class____4") + element.getClass().getName(); //$NON-NLS-1$
    }   
}
