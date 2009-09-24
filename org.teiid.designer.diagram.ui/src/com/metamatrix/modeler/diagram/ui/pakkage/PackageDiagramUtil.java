/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;


/** 
 * @since 4.3
 */
public class PackageDiagramUtil {
    private static boolean PERSIST_PACKAGE_DIAGRAMS = true;
    private static boolean DO_NOT_PERSIST_PACKAGE_DIAGRAMS = false;
    
    public static  Diagram createPackageDiagram(EObject target, ModelResource modelResource) {
        Diagram result = null;
        boolean requiresStart = false;
        boolean succeeded = false;
        
        boolean persist = PERSIST_PACKAGE_DIAGRAMS;
        if( modelResource != null && modelResource.getResource() != null ){
            if(ModelUtil.isIResourceReadOnly(modelResource.getResource()))
                persist = DO_NOT_PERSIST_PACKAGE_DIAGRAMS;
        }
        
        try {
            requiresStart = ModelerCore.startTxn(false, true, "Create Package Diagram", target);  //$NON-NLS-1$
            
            result = modelResource.getModelDiagrams().createNewDiagram(target, persist);
            result.setType(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID);
            succeeded = true;
        } catch (ModelWorkspaceException e) {
            if ( modelResource == null || !modelResource.hasErrors() ) {
                // Unexpected ...
                String message = DiagramUiConstants.Util.getString("PackageDiagramContentProvider.createPackageDiagramError", modelResource.toString());  //$NON-NLS-1$
                DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
            }
        } finally {
            if( requiresStart ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
        
        return result;
    }
}
