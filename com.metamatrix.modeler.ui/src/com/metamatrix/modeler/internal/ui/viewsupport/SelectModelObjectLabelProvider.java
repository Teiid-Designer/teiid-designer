/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCore;


/** 
 * Simple label provider which displays a simple String labels, or if EObjects, the EObject's name. In addition, if showPath == true
 * the label is appended with the full path name up to and including the project name.
 * @since 5.0
 */
public class SelectModelObjectLabelProvider extends LabelProvider {
    private ModelObjectLabelProvider delegate = new ModelObjectLabelProvider();
    protected boolean showPath = true;
    /** 
     * 
     * @since 5.0
     */
    public SelectModelObjectLabelProvider() {
        super();
    }
    
    @Override
    public String getText( Object element ) {
        String sText = " Unknown "; //$NON-NLS-1$
        if ( element instanceof String ) {
            sText = (String)element;
        }
        else 
        if ( element instanceof EObject ) {
            EObject eo = (EObject)element;
            sText = ModelerCore.getModelEditor().getName(eo);
            if( showPath ) {
                sText += " : " + ModelerCore.getModelEditor().getFullPathToParent(eo); //$NON-NLS-1$
            }
        }
        
        return sText;        
    }

    @Override
    public Image getImage( Object element ) {
        Image imgResult = delegate.getImage(element);
        
        return imgResult;
    }
    
    public void setShowPath(boolean theShowPath) {
        this.showPath = theShowPath;
    } 
}
