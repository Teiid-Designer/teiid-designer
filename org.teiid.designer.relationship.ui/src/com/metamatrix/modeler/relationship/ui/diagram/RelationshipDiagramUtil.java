/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramUtil {

	public static Diagram getRelationshipDiagram(
		final EObject relationshipEObject,
		final Object source,
        final boolean forceCreate) {
		Diagram relationshipDiagram = null;

		if (relationshipEObject instanceof Relationship) {
			ModelResource modelResource =
				ModelUtilities.getModelResourceForModelObject(relationshipEObject);
			if (modelResource != null && isRelationshipModelResource(modelResource)) {
				relationshipDiagram = getRelationshipDiagram(modelResource, relationshipEObject, source, forceCreate);
			}

		}

		return relationshipDiagram;
	}
	
	public static Diagram getRelationshipRelationshipDiagram(
		final EObject relationshipEObject,
		final Object source,
        final boolean forceCreate) {
		Diagram relationshipDiagram = null;

		if (relationshipEObject instanceof Relationship) {
			ModelResource modelResource =
				ModelUtilities.getModelResourceForModelObject(relationshipEObject);
			if (modelResource != null && isRelationshipModelResource(modelResource)) {
				relationshipDiagram = getExistingRelationshipDiagram(modelResource, relationshipEObject, source);
				if (relationshipDiagram == null) {
                    if( forceCreate ) {
					   relationshipDiagram = createRelationshipDiagram( modelResource, relationshipEObject, true, source);
                    } else {   
                       relationshipDiagram = new DiagramProxy(relationshipEObject, PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID, modelResource);
                    }
                }
			}

		}

		return relationshipDiagram;
	}

	public static boolean isRelationshipModelResource(final ModelResource modelResource) {
		boolean result = false;
		if( modelResource != null ) {
			MetamodelDescriptor descriptor = null;
	
			try {
				descriptor = modelResource.getPrimaryMetamodelDescriptor();
			} catch (ModelWorkspaceException e) {
				e.printStackTrace();
			}
	
			if (descriptor != null && descriptor.getNamespaceURI().equals(RelationshipPackage.eNS_URI)) {
				result = true;
			}
		}
		return result;
	}
	
	public static boolean isRelationshipModelResource(final EObject eObject) {
		boolean result = false;
		ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
		if( modelResource != null ) {
			MetamodelDescriptor descriptor = null;
	
			try {
				descriptor = modelResource.getPrimaryMetamodelDescriptor();
			} catch (ModelWorkspaceException e) {
				e.printStackTrace();
			}
	
			if (descriptor != null && descriptor.getNamespaceURI().equals(RelationshipPackage.eNS_URI)) {
				result = true;
			}
		}
		return result;
	}
	

	public static boolean isRelationshipDiagram(final EObject eObject) {
		if( eObject instanceof Diagram &&
			((Diagram)eObject).getType() != null &&
			((Diagram)eObject).getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID))
			return true;
            
		return false;
	}
	
	public static boolean isCustomRelationshipDiagram(final EObject eObject) {
		if( eObject instanceof Diagram &&
			((Diagram)eObject).getType() != null &&
			((Diagram)eObject).getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID))
			return true;
            
		return false;
	}

	public static Diagram createRelationshipDiagram(
		final ModelResource modelResource,
		final EObject targetEObject,
		final boolean desiredPersistance,
		Object source) {
		Diagram result = null;
		boolean requiresStart = false;
		boolean succeeded = false;

		boolean persist = desiredPersistance;
		if (persist && modelResource != null && modelResource.getResource() != null) {
			if (ModelUtil.isIResourceReadOnly(modelResource.getResource()))
				persist = false;
		}

		try {
			requiresStart = ModelerCore.startTxn(false, true, "Create Relationship Diagram", source); //$NON-NLS-1$
			if (targetEObject instanceof ModelAnnotation) {
				result = modelResource.getModelDiagrams().createNewDiagram(null, persist);
			} else
				result = modelResource.getModelDiagrams().createNewDiagram(targetEObject, persist);

			result.setType(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID);
			succeeded = true;
		} catch (ModelWorkspaceException e) {
			String message = UiConstants.Util.getString("RelationshipDiagramUtil.createRelationshipDiagramError", modelResource.toString()); //$NON-NLS-1$
			UiConstants.Util.log(IStatus.ERROR, e, message);
		} finally {
			if (requiresStart) {
				if (succeeded) {
					ModelerCore.commitTxn();
				} else {
					ModelerCore.rollbackTxn();
				}
			}
		}
		return result;
	}

	public static Diagram getRelationshipDiagram( final ModelResource modelResource, final EObject eObj, Object source, final boolean forceCreate) {

		Diagram relationshipDiagram = null;
		EObject targetObject = null;
		
		if( eObj != null ) {
			targetObject = getHighestDiagramTarget(eObj);
		}
		

		try {
			List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(targetObject));
			Iterator iter = diagramList.iterator();
			Diagram nextDiagram = null;
			while (iter.hasNext()) {
				nextDiagram = (Diagram)iter.next();
				if (nextDiagram != null
					&& nextDiagram.getType() != null
					&& nextDiagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID))
					relationshipDiagram = nextDiagram;
			}
		} catch (ModelWorkspaceException e) {
			String message = UiConstants.Util.getString("RelationshipDiagramUtil.getTransformationDiagramError", modelResource.toString()); //$NON-NLS-1$
			UiConstants.Util.log(IStatus.ERROR, e, message);
		}
		
		if( relationshipDiagram == null ) {
			if( targetObject != null && targetObject.eContainer() != null && targetObject.eContainer() instanceof Relationship ) {
				EObject parent = targetObject.eContainer();
				relationshipDiagram = getRelationshipDiagram(parent);
			} else if( targetObject != null && targetObject instanceof RelationshipFolder && targetObject.eContainer() == null ) {
				// This is a folder under a model
				relationshipDiagram = 
					createRelationshipDiagram(
							modelResource,
				targetObject,
							true,
							source);
			} else if( targetObject != null && targetObject.eContainer() == null ) {
                relationshipDiagram = getRelationshipDiagram(modelResource, null, source, forceCreate);
            } else {
                if( forceCreate ) {
                    relationshipDiagram = 
                        createRelationshipDiagram(
							modelResource,
							targetObject,
							true,
							source);
                } else {
                    relationshipDiagram = new DiagramProxy(targetObject, PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID, modelResource);
                }
			}
		}
		return relationshipDiagram;
	}
	
	public static Diagram getExistingRelationshipDiagram( final ModelResource modelResource, final EObject eObject, Object source) {

		Diagram relationshipDiagram = null;

		try {
			List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
			Iterator iter = diagramList.iterator();
			Diagram nextDiagram = null;
			while (iter.hasNext()) {
				nextDiagram = (Diagram)iter.next();
				if (nextDiagram != null
					&& nextDiagram.getType() != null
					&& nextDiagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID))
					relationshipDiagram = nextDiagram;
			}
		} catch (ModelWorkspaceException e) {
			String message = UiConstants.Util.getString("RelationshipDiagramUtil.getTransformationDiagramError", modelResource.toString()); //$NON-NLS-1$
			UiConstants.Util.log(IStatus.ERROR, e, message);
		}
		
		return relationshipDiagram;
	}
	
	
	

	public static Diagram getRelationshipDiagram(final EObject eObject) {
		Diagram relationshipDiagram = null;
		
		ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
		
		if( modelResource != null ) {
			try {
				List diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
				Iterator iter = diagramList.iterator();
				Diagram nextDiagram = null;
				while (iter.hasNext()) {
					nextDiagram = (Diagram)iter.next();
					if (nextDiagram != null
						&& nextDiagram.getType() != null
						&& nextDiagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID))
						relationshipDiagram = nextDiagram;
				}
			} catch (ModelWorkspaceException e) {
				String message = UiConstants.Util.getString("RelationshipDiagramUtil.getTransformationDiagramError", modelResource.toString()); //$NON-NLS-1$
				UiConstants.Util.log(IStatus.ERROR, e, message);
			}
			if( relationshipDiagram == null ) {
				if( eObject.eContainer() != null ) {
					EObject parent = eObject.eContainer();
					relationshipDiagram = getRelationshipDiagram(parent);
				}
			}
		}

		return relationshipDiagram;
	}
	
	public static void openDiagram(final Diagram relationshipDiagram) {
		// all we have is a relationship diagram object??
		// Need to get the
		ModelEditorManager.open(relationshipDiagram, false);
	}
	
	public static boolean isRelationshipObject(final EObject eObject) {
		ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
		if( modelResource != null && isRelationshipModelResource(modelResource))
			return true;
			
		return false;
	}
	
	public static List getRelationshipsForType(DiagramModelNode diagramNode, EObject relationshipType) {
		List relList = new ArrayList();
		
		Iterator iter = diagramNode.getChildren().iterator();
		DiagramModelNode nextDMN = null;
		RelationshipType currentType = null;
		Relationship currentRel = null;
		while( iter.hasNext() ) {
			nextDMN = (DiagramModelNode)iter.next();
			if( nextDMN instanceof RelationshipModelNode ) {
				currentRel = (Relationship)nextDMN.getModelObject();
				currentType = currentRel.getType();
				if( currentType != null && currentType.equals(relationshipType) )
					relList.add(nextDMN);
			}
		}
		
		if( relList.isEmpty() )
			return Collections.EMPTY_LIST;
		return relList;
	}
	
	public static EObject getHighestDiagramTarget(final Object input) {
		if( input instanceof RelationshipFolder )
			return (EObject)input;
			
		EObject diagramTargetEObject = null;
        
		if( input instanceof EObject ) {
			EObject eObject = (EObject)input;
            
			Object parent = eObject.eContainer();
            
			if( parent != null && parent instanceof EObject ) {
				if( parent instanceof RelationshipFolder )
					diagramTargetEObject = (EObject)parent;
				else
					diagramTargetEObject = getHighestDiagramTarget(parent);
			}
		}
        
		return diagramTargetEObject;
	}
}
