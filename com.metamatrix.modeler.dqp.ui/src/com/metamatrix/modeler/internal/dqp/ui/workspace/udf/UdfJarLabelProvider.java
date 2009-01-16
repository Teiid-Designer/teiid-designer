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

package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;


/** 
 * @since 5.0
 */
public class UdfJarLabelProvider extends LabelProvider {
    //============================================================================================================================
    // Variables

    private ILabelProvider defaultProvider;
    
    /** 
     * 
     * @since 5.0
     */
    public UdfJarLabelProvider() {
        super();
    }
    
    //============================================================================================================================
    // Property methods
    
    /**
     * @since 4.0
     */
    private ILabelProvider getDefaultProvider() {
        if ( defaultProvider == null ) { 
            defaultProvider = new WorkbenchLabelProvider();
        }
        return defaultProvider;
    }
    //============================================================================================================================
    // Overridden methods

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.0
     */
    @Override
    public Image getImage(Object element) {
        try {        

            if ( element instanceof UdfJarFolder ) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.UDF_JAR_FOLDER_ICON);
            } else if( element instanceof UdfJarWrapper ) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.UDF_JAR_ICON);
            }
        
        } catch (final Exception err) {
            DqpUiConstants.UTIL.log(err);
        }
                
        return super.getImage(element);
    }

    /**
     * Figures out which provider to delegate to.
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.0
     */
    @Override
    public String getText(Object element) {

        if ( element instanceof UdfJarFolder ) {
            return ((UdfJarFolder)element).getLabel();
        } else if( element instanceof UdfJarWrapper ) {
            return ((UdfJarWrapper)element).getLabel();
        }
        
        return getDefaultProvider().getText(element);
        
    }

}
