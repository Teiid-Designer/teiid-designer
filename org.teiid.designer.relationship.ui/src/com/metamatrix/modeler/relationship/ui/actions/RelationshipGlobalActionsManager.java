/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * TransformationGlobalActionsManager is a class of static utility methods that can:
 * 
 * Determine if objects support the standard global edit actions: delete, cut, copy, paste, and clone.
 * Execute these actions on the selected object(s).
 * 
 * It is intended to work with diagrams of type Transformation. 
 * 
 */
public class RelationshipGlobalActionsManager {

// =======================================
//	Delete
// =======================================
 
	/*
	 * Can delete the following:
	 * 
	 * Only children of a Virtual Group.
	 * We shouldn't have any other criteria here.
	 */
	public static boolean canDelete(List sourceEObjects) {
		boolean canDelete = false;        
		if ( objectsWriteable(sourceEObjects) && allRelationshipObjects(sourceEObjects)) {
			canDelete = true;
		} 
		return canDelete;
	} 

	public static void delete( List sourceEObjects ) 
		throws ModelerCoreException {
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
 
			Iterator iter = sourceEObjects.iterator();
			Object nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = iter.next();
				if ( nextEObject instanceof EObject ) {                
					ModelerCore.getModelEditor().delete( (EObject)nextEObject );
				}
			}
		}        
	}

//	=======================================
//	 Cut
//	=======================================

	public static boolean canCut(List sourceEObjects) {
		boolean canCut = false;        
//		if ( transformationEObject!=null && !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
//			canCut = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
//		} 
		return canCut;
	}
    
	public static void cut( List sourceEObjects ) 
		throws ModelerCoreException {
   
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
 
			Iterator iter = sourceEObjects.iterator();
			Object nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = iter.next();
				if ( nextEObject instanceof EObject ) {                
					ModelerCore.getModelEditor().cutToClipboard( (EObject)nextEObject );
				}
			}
		}        
	}
    
//	=======================================
//	 Copy
//	=======================================

	public static boolean canCopy(List sourceEObjects) {
		boolean canCopy = false;        
//		if ( transformationEObject!=null && !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
//			canCopy = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
//			if( !canCopy ) {
//				if( allObjectsInTarget(transformationEObject, sourceEObjects))
//					canCopy = true;
//			}
//		} 
		return canCopy;
	} 

	public static void copy( List sourceEObjects ) 
		throws ModelerCoreException {
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
 
			Iterator iter = sourceEObjects.iterator();
			Object nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = iter.next();
				if ( nextEObject instanceof EObject ) {                
					ModelerCore.getModelEditor().copyToClipboard( (EObject)nextEObject );
				}
			}
		}        
	}
    
//	=======================================
//	 Paste
//	=======================================

	public static boolean canPaste(List sourceEObjects) {

//		if (!ModelObjectUtilities.isReadOnly(transformationEObject) &&
//			transformationEObject instanceof SqlTransformationMappingRoot && 
//			sourceEObjects != null && 
//			!sourceEObjects.isEmpty() ) {
//            
//			SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;
//
//			List outputEObjects = mappingRoot.getOutputs();
//			EObject eoFirstOutput = null;
//			if ( !outputEObjects.isEmpty()  ) {
//				eoFirstOutput = (EObject)outputEObjects.get( 0 );
//				if (  sourceEObjects.contains( eoFirstOutput ) ) {
//					if ( ModelerCore.getModelEditor().isValidPasteParent( eoFirstOutput ) ) {
//						return true;
//					}
//				}                
//			}
//		}
        
		return false;
	} 

	public static void paste( List sourceEObjects ) 
		throws ModelerCoreException {
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
 
			Iterator iter = sourceEObjects.iterator();
			Object nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = iter.next();
				if ( nextEObject instanceof EObject ) {                
					ModelerCore.getModelEditor().pasteFromClipboard( nextEObject );
				}
			}
		}        
	}
    
//	=======================================
//	 Clone
//	=======================================

	public static boolean canClone(List sourceEObjects) {
		boolean canClone = false;        
//		if ( transformationEObject!=null && !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
//			canClone = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
//			if( !canClone ) {
//				if( allObjectsInTarget(transformationEObject, sourceEObjects))
//					canClone = true;
//			}
//		} 
		return canClone;
	} 

	public static void clone( List sourceEObjects ) 
		throws ModelerCoreException {
        
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
 
			Iterator iter = sourceEObjects.iterator();
			Object nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = iter.next();
				if ( nextEObject instanceof EObject ) {                
					ModelerCore.getModelEditor().clone( (EObject)nextEObject );
				}
			}
		}
	}

//	=======================================
//	 Utility Methods
//	=======================================

    
	public static boolean allSourceObjectsAreDiagramEntities( List sourceEObjects ) {
        
		if ( sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {

			Iterator iter = sourceEObjects.iterator();
			Object nextObject = null;
			boolean bAllSourceObjectsDES = true;
            
			// test each selected object against the set of input objects
			while( iter.hasNext() && bAllSourceObjectsDES ) {
				nextObject = iter.next();
				if( !DiagramUiUtilities.isDrawingObject((EObject)nextObject) )
					bAllSourceObjectsDES = false;
			}
			return bAllSourceObjectsDES;
		}
		return false;
	}
	
	public static boolean objectsWriteable(List selectedEObjects) {
		if ( selectedEObjects != null && 
			!selectedEObjects.isEmpty() && selectedEObjects.size() >= 1 ) {
			EObject nextEObject = (EObject)selectedEObjects.get(0);
			return ! ModelObjectUtilities.isReadOnly(nextEObject);
		}
		return false;
	}
	
	public static boolean allRelationshipObjects(List selectedEObjects) {
		if ( selectedEObjects != null && 
			!selectedEObjects.isEmpty() ) {
			Iterator iter = selectedEObjects.iterator();
			EObject nextEObject = null;
			while( iter.hasNext() ) {
				nextEObject = (EObject)iter.next();
				if( !(nextEObject instanceof Relationship) && 
					!(nextEObject instanceof RelationshipType) &&
					!(nextEObject instanceof RelationshipFolder))
					return false;
			}
			return true;
		}
		return false;
	}
}
