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

package com.metamatrix.modeler.diagram.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class UmlRelationshipHelper {
    private static final String DUMMY_VALUE = "x"; //$NON-NLS-1$

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEndCount(java.lang.Object)
     */
    public static int getEndCount( final UmlRelationship relationshipAspect,
                                   final EObject eObject ) {
        int endCount = 1;
        if (relationshipAspect instanceof UmlAssociation) {
            endCount = ((UmlAssociation)relationshipAspect).getEndCount(eObject);
        } else if (relationshipAspect instanceof UmlDependency) {

        } else if (relationshipAspect instanceof UmlGeneralization) {

        }
        return endCount;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getRoleName(java.lang.Object, int)
     */
    public static String getRoleName( final UmlRelationship relationshipAspect,
                                      final EObject eObject,
                                      final int end ) {
        String roleName = null;
        if (relationshipAspect instanceof UmlAssociation) {
            roleName = ((UmlAssociation)relationshipAspect).getRoleName(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return roleName;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getMultiplicity(java.lang.Object, int)
     */
    public static String getMultiplicity( final UmlRelationship relationshipAspect,
                                          final EObject eObject,
                                          final int end ) {
        String multiplicity = null;
        if (relationshipAspect instanceof UmlAssociation) {
            multiplicity = ((UmlAssociation)relationshipAspect).getMultiplicity(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return multiplicity;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getAggregation(java.lang.Object, int)
     */
    public static int getAggregation( final UmlRelationship relationshipAspect,
                                      final EObject eObject,
                                      final int end ) {
        int aggregation = 0;
        if (relationshipAspect instanceof UmlAssociation) {
            aggregation = ((UmlAssociation)relationshipAspect).getAggregation(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {

        } else if (relationshipAspect instanceof UmlGeneralization) {

        }
        return aggregation;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getProperties(java.lang.Object, int)
     */
    public static String[] getProperties( final UmlRelationship relationshipAspect,
                                          final EObject eObject,
                                          final int end ) {
        String[] properties = null;
        if (relationshipAspect instanceof UmlAssociation) {
            properties = ((UmlAssociation)relationshipAspect).getProperties(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return properties;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getNavigability(java.lang.Object, int)
     */
    public static int getNavigability( final UmlRelationship relationshipAspect,
                                       final EObject eObject,
                                       final int end ) {
        int navigability = UmlAssociation.NAVIGABILITY_NONE;
        if (relationshipAspect instanceof UmlAssociation) {
            navigability = ((UmlAssociation)relationshipAspect).getNavigability(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {

        } else if (relationshipAspect instanceof UmlGeneralization) {

        }
        return navigability;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEnd(java.lang.Object, int)
     */
    public static EObject getEnd( final UmlRelationship relationshipAspect,
                                  final EObject eObject,
                                  final int end ) {
        EObject endEObject = null;

        if (relationshipAspect instanceof UmlAssociation) {
            endEObject = ((UmlAssociation)relationshipAspect).getEnd(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {

        } else if (relationshipAspect instanceof UmlGeneralization) {

        }
        return endEObject;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEndTarget(java.lang.Object, int)
     */
    public static EObject getEndTarget( final UmlRelationship relationshipAspect,
                                        final EObject eObject,
                                        final int end ) {
        EObject endETargetObject = null;

        if (relationshipAspect instanceof UmlAssociation) {
            endETargetObject = ((UmlAssociation)relationshipAspect).getEndTarget(eObject, end);
        } else if (relationshipAspect instanceof UmlDependency) {
            endETargetObject = eObject;
        } else if (relationshipAspect instanceof UmlGeneralization) {
            if (end == BinaryAssociation.SOURCE_END) endETargetObject = ((UmlGeneralization)relationshipAspect).getSpecific(eObject);
            else endETargetObject = ((UmlGeneralization)relationshipAspect).getGeneral(eObject);
        }
        return endETargetObject;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public static IStatus setRoleName( final UmlRelationship relationshipAspect,
                                       final EObject eObject,
                                       final int end,
                                       final String name ) {
        IStatus iStatus = null;
        if (relationshipAspect instanceof UmlAssociation) {
            iStatus = ((UmlAssociation)relationshipAspect).setRoleName(eObject, end, name);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return iStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public static IStatus setMultiplicity( final UmlRelationship relationshipAspect,
                                           final EObject eObject,
                                           final int end,
                                           final String mult ) {
        IStatus iStatus = null;
        if (relationshipAspect instanceof UmlAssociation) {
            iStatus = ((UmlAssociation)relationshipAspect).setMultiplicity(eObject, end, mult);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return iStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public static IStatus setProperties( final UmlRelationship relationshipAspect,
                                         final EObject eObject,
                                         final int end,
                                         final String[] props ) {
        IStatus iStatus = null;
        if (relationshipAspect instanceof UmlAssociation) {
            iStatus = ((UmlAssociation)relationshipAspect).setProperties(eObject, end, props);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return iStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setNavigability(java.lang.Object, int, int)
     */
    public static IStatus setNavigability( final UmlRelationship relationshipAspect,
                                           final EObject eObject,
                                           final int end,
                                           final int navigability ) {
        IStatus iStatus = null;
        if (relationshipAspect instanceof UmlAssociation) {
            iStatus = ((UmlAssociation)relationshipAspect).setNavigability(eObject, end, navigability);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return iStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getVisibility(java.lang.Object)
     */
    public static int getVisibility( final UmlRelationship relationshipAspect,
                                     final EObject eObject ) {
        int visibility = UmlDiagramAspect.VISIBILITY_DEFAULT;

        if (relationshipAspect instanceof UmlAssociation) {
            visibility = ((UmlAssociation)relationshipAspect).getVisibility(eObject);
        } else if (relationshipAspect instanceof UmlDependency) {

        } else if (relationshipAspect instanceof UmlGeneralization) {

        }
        return visibility;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getStereotype(java.lang.Object)
     */
    public static String getStereotype( final UmlRelationship relationshipAspect,
                                        final EObject eObject ) {
        String stereotype = null;

        if (relationshipAspect instanceof UmlAssociation) {
            stereotype = ((UmlAssociation)relationshipAspect).getStereotype(eObject);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return stereotype;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getSignature(java.lang.Object, int)
     */
    public static String getSignature( final UmlRelationship relationshipAspect,
                                       final EObject eObject,
                                       final int showMask ) {
        String signature = null;

        if (relationshipAspect instanceof UmlAssociation) {
            signature = ((UmlAssociation)relationshipAspect).getSignature(eObject, showMask);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return signature;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEditableSignature(java.lang.Object)
     */
    public static String getEditableSignature( final UmlRelationship relationshipAspect,
                                               final EObject eObject ) {
        String signature = null;

        if (relationshipAspect instanceof UmlAssociation) {
            signature = ((UmlAssociation)relationshipAspect).getEditableSignature(eObject);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return signature;
    }

    /* (non-Javadoc) 
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setSignature(java.lang.Object, java.lang.String)
     */
    public static IStatus setSignature( final UmlRelationship relationshipAspect,
                                        final EObject eObject,
                                        final String newSignature ) {
        IStatus iStatus = null;

        if (relationshipAspect instanceof UmlAssociation) {
            iStatus = ((UmlAssociation)relationshipAspect).setSignature(eObject, newSignature);
        } else if (relationshipAspect instanceof UmlDependency) {
            // Do nothing
        } else if (relationshipAspect instanceof UmlGeneralization) {
            // Do Nothing
        }
        return iStatus;
    }

    public static int getType( final UmlRelationship relationshipAspect ) {
        if (relationshipAspect != null) {
            if (relationshipAspect instanceof UmlAssociation) {
                return BinaryAssociation.TYPE_UML_ASSOCIATION;
            } else if (relationshipAspect instanceof UmlDependency) {
                return BinaryAssociation.TYPE_UML_DEPENDENCY;
            } else if (relationshipAspect instanceof UmlGeneralization) {
                return BinaryAssociation.TYPE_UML_GENERALIZATION;
            }
        }

        return BinaryAssociation.TYPE_UNKNOWN_RELATIONSHIP;
    }

    public static int getType( final EObject eObject ) {
        UmlRelationship relationshipAspect = getRelationshipAspect(eObject);
        return getType(relationshipAspect);
    }

    public static UmlRelationship getRelationshipAspect( final EObject eObj ) {
        MetamodelAspect theAspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(eObj);

        if (theAspect instanceof UmlRelationship) {
            return (UmlRelationship)theAspect;
        }

        return null;
    }

    public static boolean isUmlModel( final EObject eObject ) {
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
        if (modelResource != null && isUmlModelResource(modelResource)) return true;

        return false;
    }

    public static boolean isUmlModelResource( final ModelResource modelResource ) {
        boolean result = false;

        MetamodelDescriptor descriptor = null;

        try {
            descriptor = modelResource.getPrimaryMetamodelDescriptor();
        } catch (ModelWorkspaceException e) {
            e.printStackTrace();
        }

        if (descriptor != null) { // && descriptor.getURI().equals(RelationshipPackage.eNS_URI)) {
            result = true;
        }

        return result;
    }

    public static List getRelatedObjects( List eObjects ) {
        HashMap relatedObjects = new HashMap();

        Iterator iter = eObjects.iterator();
        EObject nextEObj = null;
        EObject nextEObj_2 = null;
        while (iter.hasNext()) {
            nextEObj = (EObject)iter.next();
            if (nextEObj != null) {
                List relObjs = getRelatedObjects(nextEObj);
                if (!relObjs.isEmpty()) {
                    Iterator iter_2 = relObjs.iterator();
                    while (iter_2.hasNext()) {
                        nextEObj_2 = (EObject)iter_2.next();
                        if (nextEObj_2 != null) relatedObjects.put(nextEObj_2, DUMMY_VALUE);
                    }
                }
            }
        }

        if (relatedObjects.isEmpty()) return Collections.EMPTY_LIST;

        return new ArrayList(relatedObjects.keySet());
    }

    public static List getRelatedObjects( EObject eObject ) {
        List binaryAss = getRelationships(eObject);
        if (!binaryAss.isEmpty()) {
            HashMap relatedObjects = new HashMap();
            Iterator iter = binaryAss.iterator();
            BinaryAssociation nextBass = null;
            EObject sourceEObject = null;
            EObject targetEObject = null;
            while (iter.hasNext()) {
                nextBass = (BinaryAssociation)iter.next();

                sourceEObject = getSourceEnd(nextBass);
                if (sourceEObject != null && !(sourceEObject == eObject)) relatedObjects.put(sourceEObject, DUMMY_VALUE);

                targetEObject = getTargetEnd(nextBass);
                if (targetEObject != null && !(targetEObject == eObject)) relatedObjects.put(targetEObject, DUMMY_VALUE);
            }

            if (relatedObjects.isEmpty()) return Collections.EMPTY_LIST;

            return new ArrayList(relatedObjects.keySet());
        }

        return Collections.EMPTY_LIST;
    }

    public static List getRelationships( EObject targetEObject ) {
        MetamodelAspect classifierAspect = getAspect(targetEObject);
        List allBasses = new ArrayList();
        if (classifierAspect instanceof UmlClassifier) {

            Collection relationships = ((UmlClassifier)classifierAspect).getRelationships(targetEObject);
            Collection superTypes = ((UmlClassifier)classifierAspect).getSupertypes(targetEObject);

            List allAssociations = new ArrayList();
            if (relationships != null && !relationships.isEmpty()) {
                Iterator iter = relationships.iterator();
                Object nextObject = null;
                while (iter.hasNext()) {
                    nextObject = iter.next();
                    if (!allAssociations.contains(nextObject)) allAssociations.add(nextObject);
                }
            }

            if (superTypes != null && !superTypes.isEmpty()) {
                Iterator iter = superTypes.iterator();
                Object nextObject = null;
                while (iter.hasNext()) {
                    nextObject = iter.next();
                    if (!allAssociations.contains(nextObject)) allAssociations.add(nextObject);
                }
            }

            // Object obj = null;

            allBasses = UmlRelationshipFactory.getBinaryAssociations(allAssociations, targetEObject);
        }

        return allBasses;
    }

    public static EObject getSourceEnd( BinaryAssociation bAss ) {
        EObject sourceEObject = bAss.getEndTarget(BinaryAssociation.SOURCE_END);
        if (sourceEObject != null) {
            EObject sourceParent = getTopClassifier(sourceEObject);
            if (sourceParent != null && sourceParent != sourceEObject) {
                return sourceParent;
            }

            return sourceEObject;
        }

        return null;
    }

    public static EObject getTargetEnd( BinaryAssociation bAss ) {
        EObject targetEObject = bAss.getEndTarget(BinaryAssociation.TARGET_END);
        if (targetEObject != null) {
            EObject targetParent = getTopClassifier(targetEObject);
            if (targetParent != null && targetParent != targetEObject) {
                return targetParent;
            }

            return targetEObject;
        }

        return null;
    }

    public static List getRelationshipEnds( BinaryAssociation bAss ) {
        HashMap assMap = new HashMap(); // Holds a list of all association ends.

        EObject sourceEObject = bAss.getEndTarget(BinaryAssociation.SOURCE_END);
        EObject targetEObject = bAss.getEndTarget(BinaryAssociation.TARGET_END);
        if (sourceEObject != null && targetEObject != null && !sourceEObject.equals(targetEObject)) {
            // Make sure these aren't nested in any way. i.e. get the top level classifier node.
            EObject sourceParent = getTopClassifier(sourceEObject);
            if (sourceParent != null) {
                if (sourceParent == sourceEObject) {
                    assMap.put(sourceEObject, DUMMY_VALUE);
                } else {
                    assMap.put(sourceParent, DUMMY_VALUE);
                }
            } else {
                assMap.put(sourceEObject, DUMMY_VALUE);
            }

            EObject targetParent = getTopClassifier(targetEObject);
            if (targetParent != null) {
                if (targetParent == targetEObject) {
                    assMap.put(targetEObject, DUMMY_VALUE);
                } else {
                    assMap.put(targetParent, DUMMY_VALUE);
                }
            } else {
                assMap.put(targetEObject, DUMMY_VALUE);
            }
        }

        if (assMap.isEmpty()) return Collections.EMPTY_LIST;

        return new ArrayList(assMap.keySet());
    }

    public static EObject getTopClassifier( EObject targetEObject ) {
        return DiagramUiUtilities.getParentClassifier(targetEObject);
    }

    public static MetamodelAspect getAspect( EObject eObj ) {
        return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(eObj);
    }
}
