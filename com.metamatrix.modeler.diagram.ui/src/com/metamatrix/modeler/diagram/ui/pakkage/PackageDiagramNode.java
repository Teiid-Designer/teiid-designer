/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.pakkage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * PackageDiagramNode
 */
public class PackageDiagramNode extends AbstractLocalDiagramModelNode {

    public PackageDiagramNode( EObject modelObject, String diagramName) {
        super( modelObject, diagramName );
    }
        
    @Override
    public String toString() {
        return "PackageDiagramNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    public boolean wasLayedOut() {
        // This method has to override abstract class
        // if any one of it's children answer true to same method
        // then we assume that this diagram had entities defined and
        // was already layed out at one time.
        List children = getChildren();
        Iterator iter = children.iterator();
        DiagramModelNode nextChild = null;
        while( iter.hasNext()) {
            nextChild = (DiagramModelNode)iter.next();
            if( nextChild.wasLayedOut())
                return true;
        }
        
        return false;
    }

    @Override
    public void recoverObjectProperties(){
        // This method has to override abstract class
        // if any one of it's children answer true to wasLayedOut
        // then we assume that this diagram had entities defined and
        // was already layed out at one time.
        // This should be called after the initial layout method only!!!
        List children = getChildren();
        Iterator iter = children.iterator();
        DiagramModelNode nextChild = null;
        while( iter.hasNext()) {
            nextChild = (DiagramModelNode)iter.next();
            if( nextChild.wasLayedOut())
                nextChild.recoverObjectProperties();
        }
    }
    
    public void refreshAssociationLabels() {
        // Remove all labels from diagram
        clearAssociationLabels();
        // set the association SHOW booleans for any DiagramUmlAssociation
        resetAssociationShowPreferences();
        // Add association labels back in
        addAssociationLabels();
    }
    
    public void clearAssociationLabels() {
        // Remove all label children
        List labels = new ArrayList();
        List children = getChildren();
        Iterator iter = children.iterator();
        DiagramModelNode nextChild = null;
        while( iter.hasNext()) {
            nextChild = (DiagramModelNode)iter.next();
            if( nextChild instanceof LabelModelNode ) {
                labels.add(nextChild);
            }
        }
        if( !labels.isEmpty() ) {
            removeChildren(labels, false);
        }
        
    }
    
    private void addAssociationLabels() {
        List currentAssociations = getCurrentAssociations();
        if( !currentAssociations.isEmpty() ) {
            Iterator iter = currentAssociations.iterator();
            NodeConnectionModel nextAssociation = null;
            while( iter.hasNext() ) {
                nextAssociation = (NodeConnectionModel)iter.next();
            
            
                List labelNodes = nextAssociation.getLabelNodes();
                if( labelNodes != null && !labelNodes.isEmpty() ) {
                    Iterator labelIter = labelNodes.iterator();
                    LabelModelNode nextNode = null;
                    while( labelIter.hasNext() ) {
                        nextNode = (LabelModelNode)labelIter.next();
                        addChild(nextNode);
                    }
                }
            }
        }
    }
    
    public List getCurrentAssociations( ) {
        List currentAssociations = new ArrayList();
        
        Iterator iter = getChildren().iterator();
        while( iter.hasNext() ) {
            DiagramModelNode childModelNode = (DiagramModelNode)iter.next();
            List sourceConnections = childModelNode.getSourceConnections();
            // Walk through the source connections and check if the same info.
            NodeConnectionModel nextAssociation = null;
            Iterator sIter = sourceConnections.iterator();
            while( sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if( !currentAssociations.contains(nextAssociation))
                    currentAssociations.add(nextAssociation);
            }
            
            // Walk through the target connections and check if the same info.
            List targetConnections = childModelNode.getTargetConnections();
            sIter = targetConnections.iterator();
            while( sIter.hasNext()) {
                nextAssociation = (NodeConnectionModel)sIter.next();
                if( !currentAssociations.contains(nextAssociation))
                    currentAssociations.add(nextAssociation);
            }
        }
        
        return currentAssociations;
    }
    
    public void resetAssociationShowPreferences() {
        List currentAssociations = getCurrentAssociations();
        if( !currentAssociations.isEmpty() ) {
            IPreferenceStore store = DiagramUiPlugin.getDefault().getPreferenceStore();
            boolean showRoles = store.getBoolean(PluginConstants.Prefs.SHOW_FK_NAME);
            boolean showMulti = store.getBoolean(PluginConstants.Prefs.SHOW_FK_MULTIPLICITY);
            Iterator iter = currentAssociations.iterator();
            NodeConnectionModel nextAssociation = null;
            while( iter.hasNext() ) {
                nextAssociation = (NodeConnectionModel)iter.next();
            
                if( nextAssociation instanceof DiagramUmlAssociation ) {
                    ((DiagramUmlAssociation)nextAssociation).setShowRoles(showRoles);
                    ((DiagramUmlAssociation)nextAssociation).setShowMultiplicity(showMulti);
                }
            }
        }
    }

}
