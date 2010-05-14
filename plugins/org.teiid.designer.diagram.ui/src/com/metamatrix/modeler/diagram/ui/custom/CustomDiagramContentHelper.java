/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;


/** 
 * Set of Utilities to manage content from a standard custom diagram
 * @since 5.0
 */
public class CustomDiagramContentHelper {

    /**
     * Returns a list of objects that can be added to a diagram based on an input list of EObjects.
     * If input list contains attribute or "child" type objects, the parent is returned so the child can show up on the diagram.
     * @param selectedEObjects
     * @return
     * @since 5.0
     */
    public static List getApplicableDiagramEObjects(final List selectedEObjects) {
        Collection applicableEObjects = new HashSet(selectedEObjects.size());
        for( Iterator iter = selectedEObjects.iterator(); iter.hasNext(); ) {
            EObject nextEObj = (EObject)iter.next();
            int objType = RelationalUmlEObjectHelper.getEObjectType(nextEObj);
            switch( objType ) {
                case RelationalUmlEObjectHelper.UML_ATTRIBUTE:
                case RelationalUmlEObjectHelper.UML_ASSOCIATION: {
                    // Return the container object (i.e. the Classifier)
                    applicableEObjects.add(nextEObj.eContainer());
                } break;
                default:
                    applicableEObjects.add(nextEObj);
                    break;
            }
        }
        return new ArrayList(applicableEObjects);
    }

    public static List getActualObjectsToAdd(List originalObjects, DiagramModelNode diagramRootModelNode) {
        List applicableObjectsToAdd = getApplicableDiagramEObjects(originalObjects);
        List objectsToAdd = new ArrayList(applicableObjectsToAdd.size());
        for( Iterator iter = applicableObjectsToAdd.iterator(); iter.hasNext(); ) {
            EObject nextEObj = (EObject)iter.next();
            if( ! CustomDiagramContentHelper.objectAlreadyInDiagram(nextEObj, diagramRootModelNode) ) {
                objectsToAdd.add(nextEObj);
            }
        }
        return objectsToAdd;        
    }

    public static boolean objectAlreadyInDiagram(EObject eObj, DiagramModelNode diagramRootModelNode) {
        return DiagramUiUtilities.getDiagramModelNode(eObj, diagramRootModelNode) != null;
    }

    /**
     * Method designed to add objects to a custom diagram.
     * If an object already exists on the diagram it will NOT be re-added.
     * If an object is not a Top-level diagram object (i.e. Package, Classifier, etc.) the appropriate parent object will be added
     * instead. 
     * @param diagram
     * @param objectsToAdd
     * @param diagramEditor
     * @param txnSource
     * @return
     * @since 5.0
     */
    public static List addToCustomDiagram(final Diagram diagram, final List objectsToAdd, final DiagramEditor diagramEditor, Object txnSource) {
        List addableObjects = Collections.EMPTY_LIST;
        
        if( diagramEditor != null ) {
    
            // Need to get the current diagram
            DiagramModelNode diagramNode = diagramEditor.getCurrentModel();
            // Need to get ahold of the CustomDiagramModelFactory
            CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)diagramEditor.getModelFactory();
            // And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            addableObjects = getActualObjectsToAdd(objectsToAdd, diagramNode);
            if( !addableObjects.isEmpty() && diagramNode != null && modelFactory != null ) {
                boolean handleConstruction = !DiagramEditorUtil.isDiagramUnderConstruction(diagram);
                boolean requiredStart = false;
                boolean succeeded = false;
                try {
                    if( handleConstruction ) {
                        DiagramEditorUtil.setDiagramUnderConstruction(diagram);
                    }
                    //------------------------------------------------- 
                    // Let's wrap this in a transaction!!! 
                    //------------------------------------------------- 
    
                    requiredStart = ModelerCore.startTxn(true, false, "Add To Custom Diagram", txnSource); //$NON-NLS-1$$
    
                    modelFactory.add(addableObjects, diagramNode);
                    
                    succeeded = true;
                }  catch (Exception ex){
                    DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ": addToCustomDiagram()"); //$NON-NLS-1$  
                } finally {
                    if(requiredStart){
                        if ( succeeded ) {
                            ModelerCore.commitTxn( );
                        } else {
                            ModelerCore.rollbackTxn( );
                        }
                    }
                    if( handleConstruction ) {
                        DiagramEditorUtil.setDiagramConstructionComplete(diagram, true);
                    }
                }
            }
        }
        return addableObjects;
    }



}
