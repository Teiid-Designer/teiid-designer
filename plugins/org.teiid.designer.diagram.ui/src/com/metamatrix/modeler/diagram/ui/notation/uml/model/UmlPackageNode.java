/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author mdrilling
 *
 * Model Node for UML Package.
 */
public class UmlPackageNode extends UmlModelNode {

    public UmlPackageNode( Diagram diagramModelObject, EObject modelObject, UmlPackage aspect ) {
        super( diagramModelObject, modelObject, aspect );
        
        // Get the EObject Contents (children) - create appropriate ModelNodes
//        List list = modelObject.eContents();
//        Iterator iter = list.iterator();
//        while(iter.hasNext()) {
//            EObject eObj = (EObject)iter.next();
//            MetamodelAspect mmAspect = UmlModelFactory.getUmlAspect(eObj);
//                        
//            if(mmAspect instanceof UmlClassifier) {
//                // Get a DiagramEntity
//                DiagramEntity diagramEntity = UmlModelFactory.getDiagramEntity(eObj);
//                DiagramModelNode diagramModelNode = new UmlClassifierNode(diagramEntity, eObj, (UmlClassifier)mmAspect);
//                // Add new ClassifierNode as a child of the Package
//                addChild(diagramModelNode);
//            }
//                    
//        }
    }

    @Override
    public String getName() {
        return aspect.getSignature(getModelObject(),UmlPackage.SIGNATURE_NAME);
    }
    
    @Override
    public void setName(String name) {
//        aspect.setSignature(getModelObject(),name);
        ModelObjectUtilities.rename(getModelObject(), name, this);
//        super.setName(name);
    }
    
    public void refreshForNameChange(){
        if( getDiagramModelObject() != null && !isReadOnly() ) {
            String oldName = "xxxXxxx"; //$NON-NLS-1$

            DiagramEntityAdapter.setName(getDiagramModelObject(), getName());
        
            firePropertyChange(DiagramNodeProperties.NAME, oldName, getName());
        }
    }
    
    
    public void refreshForPathChange(){
        update(DiagramNodeProperties.PATH);
    }
    
    @Override
    public String toString() {
        return "UmlPackageNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    public String getLocation() {
        return getPath();
    }
    
    @Override
    public String getPath() {
        // First check to see if package diagram. if so, we return null
        EObject diagramEObject = getParent().getModelObject();
        if( !DiagramUiUtilities.isPackageDiagram(diagramEObject)) {
            String somePath = null;
            
            if( getDiagramModelObject() != null ) {
                Diagram theDiagram = getDiagramModelObject().getDiagram();
                somePath = DiagramUiPlugin.getDiagramTypeManager().getDisplayedPath(theDiagram, getModelObject());
            }
            
            return somePath;
        }
        
        return null;
    }
}


