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

package com.metamatrix.modeler.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
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
public class TransformationGlobalActionsManager {

// =======================================
//  Delete
// =======================================
 
    /*
     * Can delete the following:
     * 
     * Only children of a Virtual Group.
     * We shouldn't have any other criteria here.
     */
    public static boolean canDelete(EObject transformationEObject, List sourceEObjects) {
        boolean canDelete = false;        
        if ( transformationEObject!=null && 
             !ModelObjectUtilities.isStale(transformationEObject) &&
             !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
            canDelete = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
            if( !canDelete ) {
            	if( allObjectsInTarget(transformationEObject, sourceEObjects))
            		canDelete = true;
            	else if( allSourceObjectsAreDiagramEntities(sourceEObjects) )
            		canDelete = true;
            }
        } 
        return canDelete;
    } 

    public static void delete( List sourceEObjects ) 
        throws ModelerCoreException {
        if ( sourceEObjects != null && !sourceEObjects.isEmpty() ) {
            ModelerCore.getModelEditor().delete( sourceEObjects );
        }        
    }

//  =======================================
//   Cut
//  =======================================

    public static boolean canCut(EObject transformationEObject, List sourceEObjects) {
        boolean canCut = false;        
        if ( transformationEObject!=null && 
             !ModelObjectUtilities.isStale(transformationEObject) &&
             !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
            canCut = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
        } 
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
    
//  =======================================
//   Copy
//  =======================================

    public static boolean canCopy(EObject transformationEObject, List sourceEObjects) {
        boolean canCopy = false;        
        if ( transformationEObject!=null && !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
            canCopy = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
            if( !canCopy ) {
				if( allObjectsInTarget(transformationEObject, sourceEObjects))
					canCopy = true;
            }
        } 
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
    
//  =======================================
//   Paste
//  =======================================

    public static boolean canPaste(EObject transformationEObject, List sourceEObjects) {

        if (transformationEObject != null &&
            !ModelObjectUtilities.isStale(transformationEObject) &&
            !ModelObjectUtilities.isReadOnly(transformationEObject) &&
            transformationEObject instanceof SqlTransformationMappingRoot && 
            sourceEObjects != null && 
            !sourceEObjects.isEmpty() ) {
            
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            List outputEObjects = mappingRoot.getOutputs();
            EObject eoFirstOutput = null;
            if ( !outputEObjects.isEmpty()  ) {
                eoFirstOutput = (EObject)outputEObjects.get( 0 );
                if (  sourceEObjects.contains( eoFirstOutput ) ) {
                    if ( ModelerCore.getModelEditor().isValidPasteParent( eoFirstOutput ) ) {
                        return true;
                    }
                }                
            }
        }
        
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
    
//  =======================================
//   Clone
//  =======================================

    public static boolean canClone(EObject transformationEObject, List sourceEObjects) {
        boolean canClone = false;        
        if ( transformationEObject!=null && 
             !ModelObjectUtilities.isStale(transformationEObject) &&
             !ModelObjectUtilities.isReadOnly( transformationEObject ) ) {
            canClone = allSourceObjectsAreAttributes( transformationEObject, sourceEObjects );
			if( !canClone ) {
				if( allObjectsInTarget(transformationEObject, sourceEObjects))
					canClone = true;
			}
        } 
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

//  =======================================
//   Utility Methods
//  =======================================

    public static boolean allSourceObjectsAreAttributes( EObject transformationEObject, List sourceEObjects ) {
        
        if (!ModelObjectUtilities.isReadOnly(transformationEObject) &&
            transformationEObject instanceof SqlTransformationMappingRoot && 
            sourceEObjects != null && 
            !sourceEObjects.isEmpty() ) {
                /*
                 * Strategy (per Barry)
                 *   1. get the output's EObject (should just be one for now)
                 *   2. get this EObject's eContents (an array of EObjects)
                 *   3. determine if all of the selected objects are in this array:
                 *       if they are, return true, else don't
                 * 
                 * Business rule:  We expect that the eContents list on an output are 'attributes'.
                 */
            
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // 1. Get current Outputs
            List outputEObjects = mappingRoot.getOutputs();
            EObject eoFirstOutput = null;
            if ( !outputEObjects.isEmpty() ) {
                eoFirstOutput = (EObject)outputEObjects.get( 0 );
                
                List lstContents = eoFirstOutput.eContents();
                
                // walk source objects to determine if all are in the eContents list
                Iterator iter = sourceEObjects.iterator();
                Object oTemp = null;
                EObject eoTemp = null;
                while( iter.hasNext() ) {
                    oTemp = iter.next();
                    
                    if ( oTemp instanceof EObject ) {
                        eoTemp = (EObject)oTemp;
                        if ( !lstContents.contains( eoTemp) ) {
                        
                            // at least one of the objects is not in the list
                            return false;
                        }
                    }
                }
                
                // success: if we make it this far, all of the selected objects are in the eContents list
                return true;
                         
            }
            // no output objects; can't do it
            return false;
        }        
        // couldn't even check
        return false;
    }
    
    private static boolean allObjectsInTarget(EObject transformationEObject, List sourceEObjects ) {
    	boolean isOK = false;
    	
		if (!ModelObjectUtilities.isReadOnly(transformationEObject) &&
			transformationEObject instanceof SqlTransformationMappingRoot && 
			sourceEObjects != null && 
			!sourceEObjects.isEmpty() ) {
				/*
				 * Strategy (per Barry)
				 *   1. get the output's EObject (should just be one for now)
				 *   2. get this EObject's eContents (an array of EObjects)
				 *   3. determine if all of the selected objects are in this array:
				 *       if they are, return true, else don't
				 * 
				 * Business rule:  We expect that the eContents list on an output are 'attributes'.
				 */
            
			SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

			// 1. Get current Outputs
			List outputEObjects = mappingRoot.getOutputs();
			EObject eoFirstOutput = null;
			if ( !outputEObjects.isEmpty() ) {
				eoFirstOutput = (EObject)outputEObjects.get( 0 );
				// THIS IS THE TARGET VIRTUAL GROUP
				// Now we walk through the sourceEObjects and find their top-level Classifier
				// If the classifier is the "Target" in all cases, we are fine.
				if( eoFirstOutput!= null ) {
					Iterator innerIter = sourceEObjects.iterator();
					isOK = true;
					EObject nextEObject = null;
					EObject topClassifier = null;
					while(innerIter.hasNext() && isOK ) {
						nextEObject = (EObject)innerIter.next();
						topClassifier = DiagramUiUtilities.getParentClassifier(nextEObject);
						
						if( eoFirstOutput.equals(nextEObject) || topClassifier == null || !eoFirstOutput.equals(topClassifier)) {
							// selected object not in virtual classifier child tree
							// return false;
							isOK = false;
						}
					}
				}
				
			}
			
		}
    	return isOK;
    }

    public static boolean allSourceObjectsAreInputs( EObject transformationEObject, List sourceEObjects ) {
        
        if (!ModelObjectUtilities.isReadOnly(transformationEObject) &&
            transformationEObject instanceof SqlTransformationMappingRoot && 
            sourceEObjects != null && 
            !sourceEObjects.isEmpty() ) {
            
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            List inputEObjects = mappingRoot.getInputs();
           
            Iterator iter = sourceEObjects.iterator();
            Object nextObject = null;
            boolean bAllSourceObjectsAreInputs = true;
            
            // test each selected object against the set of input objects
            while( iter.hasNext() && bAllSourceObjectsAreInputs) {
                nextObject = iter.next();
                if( !inputEObjects.contains( nextObject ) )
                    bAllSourceObjectsAreInputs = false;
            }
            return bAllSourceObjectsAreInputs;
        }
        return false;
    }

            
    public static boolean anySourceObjectsAreInputs( EObject transformationEObject, List sourceEObjects ) {
        
        if (!ModelObjectUtilities.isReadOnly(transformationEObject) &&
            transformationEObject instanceof SqlTransformationMappingRoot && 
            sourceEObjects != null && 
            !sourceEObjects.isEmpty() ) {
            
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Let's get current Input's
            List inputEObjects = mappingRoot.getInputs();
            // Now let's check to see if any exist as current inputs
           
            Iterator iter = sourceEObjects.iterator();
            Object nextObject = null;
            boolean bAllSourceObjectsAreInputs = true;
            
            while( iter.hasNext() ) {
                nextObject = iter.next();
                if( !inputEObjects.contains(nextObject) )
                    bAllSourceObjectsAreInputs = false;
            }
            return bAllSourceObjectsAreInputs;
        }
        return false;
    }
    
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

}
