/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.metamodels.diagram.Diagram;



/** 
 * @since 8.0
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
            if ( !modelResource.hasErrors() ) {
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
