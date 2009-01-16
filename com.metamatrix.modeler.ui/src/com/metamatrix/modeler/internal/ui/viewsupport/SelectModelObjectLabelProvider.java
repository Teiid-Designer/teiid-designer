/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
