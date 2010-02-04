/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
