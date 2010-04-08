/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.ReferencesRecord;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;

/**
 * This class finds all references to the deleted object and it's contents, and removes or unsets those references.
 * 
 * @see com.metamatrix.modeler.internal.core.FindRelatedObjectsToDeleted
 * @see com.metamatrix.modeler.core.ModelEditor#delete(EObject)
 */
public class ClearReferencesUponDelete implements ModelVisitor {

    private final EditingDomain editingDomain;
    private final List additionalCommands;
    private final Collection allDeletedObjects;
    private final HashMap featureValueRemoves; // key = referencing EObject, value = Map (key = feature, value = list of values)
    private final ModelWorkspaceSearch workspaceSearch;

    /**
     * Cache of features by owner that have unset commands already created. Keyed by EObject. Value is a list of features.
     */
    private Map objectFeatureUnsetMap = new HashMap();

    /**
     * Construct an instance of FindRelatedObjectsToDeleted.
     */
    public ClearReferencesUponDelete( final Collection allDeletedObjects,
                                      final EditingDomain editingDomain ) {
        this(allDeletedObjects, editingDomain, new ModelWorkspaceSearch());
    }

    /**
     * Construct an instance of FindRelatedObjectsToDeleted.
     */
    public ClearReferencesUponDelete( final Collection allDeletedObjects,
                                      final EditingDomain editingDomain,
                                      final ModelWorkspaceSearch workspaceSearch ) {
        CoreArgCheck.isNotNull(editingDomain);
        CoreArgCheck.isNotNull(allDeletedObjects);
        CoreArgCheck.isNotNull(workspaceSearch);
        this.additionalCommands = new LinkedList();
        this.editingDomain = editingDomain;
        this.allDeletedObjects = allDeletedObjects;
        this.featureValueRemoves = new HashMap();
        this.workspaceSearch = workspaceSearch;
    }

    /**
     * Determines if a remove command is needed for the specified referencing object (owner) by checking to see if the specified
     * value for the specified feature has already been processed.
     * 
     * @param theReferencingObject the object whose feature is referenceing the specified value
     * @param theFeature the feature
     * @param theValue the value being deleted
     * @return <code>true</code>if a remove command should be created; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isRemoveCommandNeeded( EObject theReferencingObject,
                                           EStructuralFeature theFeature,
                                           Object theValue ) {
        boolean result = false;
        Map featureMap = (Map)this.featureValueRemoves.get(theReferencingObject);
        List values = null;

        if (featureMap == null) {
            // first time this referencing object has had a feature value removed
            featureMap = new HashMap();
            this.featureValueRemoves.put(theReferencingObject, featureMap);
        }

        if (featureMap.containsKey(theFeature)) {
            // previous remove processed for this feature
            values = (List)featureMap.get(theFeature);
        } else {
            // first time this feature has had a value removed
            values = new ArrayList(1);
            featureMap.put(theFeature, values);
        }

        if (!values.contains(theValue)) {
            // feature value has not been processed
            values.add(theValue);
            result = true;
        }

        return result;
    }

    /**
     * Removes the deleted object from a list of referenced objects if the specified feature has multiple values. If the feature
     * is single valued the feature is unset.
     * 
     * @param theReferencingObject the referencing object that references the object being deleted
     * @param theFeatureReference the referencing feature
     * @param theDeletedObject the object being removed
     * @since 4.2
     */
    private void removeReference( Object theReferencingObject,
                                  EReference theFeatureReference,
                                  EObject theDeletedObject ) {
        if ((theReferencingObject instanceof EObject) && !this.allDeletedObjects.contains(theReferencingObject)) {
            // remove the reference to the object being deleted or unset if not multivalued
            if (theFeatureReference.isMany() && !theFeatureReference.isVolatile()) {
                remove((EObject)theReferencingObject, theFeatureReference, theDeletedObject);
            } else {
                unset((EObject)theReferencingObject, theFeatureReference);
            }
        }
    }

    /**
     * Visit a deleted object and remove/unset references from non-deleted objects to it.
     * 
     * @param object one of the objects being deleted
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        // Find all references ...
        final EClass eclass = object.eClass();
        final Collection allRefs = eclass.getEAllReferences();

        for (final Iterator iter = allRefs.iterator(); iter.hasNext();) {
            final EReference reference = (EReference)iter.next();
            final EReference opposite = reference.getEOpposite();

            // Process only non-containment references ...
            // remove reference to deleted object from opposite
            if (!reference.isContainment() && opposite != null && !opposite.isContainment()) {
                if (reference.isMany()) {
                    Collection values = (Collection)object.eGet(reference);

                    for (final Iterator valueIter = values.iterator(); valueIter.hasNext();) {
                        removeReference(valueIter.next(), opposite, object);
                    }
                } else if (opposite.isChangeable()) {
                    removeReference(object.eGet(reference), opposite, object);
                }
            }
        }

        // clean up unidirectional references
        ModelEditorImpl modelEditor = (ModelEditorImpl)ModelerCore.getModelEditor();
        // get objectID and find uni-directional references for that ID
        String objID = modelEditor.getSearchIndexObjectID(object);
        if (objID != null) {
            // unidirectional references for each object to clean up
            Collection refRecords = workspaceSearch.getUniDirectionalReferencesTo(objID);
            if (refRecords != null && !refRecords.isEmpty()) {
                for (final Iterator refIter = refRecords.iterator(); refIter.hasNext();) {
                    ReferencesRecord refRecord = (ReferencesRecord)refIter.next();
                    String refUUID = refRecord.getUUID();
                    EObject refrencingObj = modelEditor.findObject(refUUID);
                    if (refrencingObj != null) {
                        cleanupUniDirectionalReferences(refrencingObj, object);
                    }
                }
            }
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public boolean visit( Resource resource ) {
        return resource != null;
    }

    /*
     * Clean up uni-directional references from the referencing object to the referenced object.
     */
    private void cleanupUniDirectionalReferences( final EObject referencingObj,
                                                  final EObject referencedObj ) {
        // if the referencing object is getting deleted then there is nothing to clean
        if (this.allDeletedObjects.contains(referencingObj)) {
            return;
        }
        // get all unidirectional references from referencing obj
        final EClass eclass = referencingObj.eClass();
        final Collection allRefs = eclass.getEAllReferences();
        for (final Iterator iter = allRefs.iterator(); iter.hasNext();) {
            final EReference reference = (EReference)iter.next();
            // need to look at unidirectional, non-containment , non-volatile references
            if (reference.getEOpposite() != null || reference.isVolatile() || !reference.isChangeable()
                || reference.isContainment()) {
                continue;
            }

            ModelEditor modelEditor = ModelerCore.getModelEditor();
            if (reference.isMany()) {
                final Collection values = (Collection)referencingObj.eGet(reference);
                for (final Iterator valueIter = values.iterator(); valueIter.hasNext();) {
                    Object value = valueIter.next();
                    // If this value is is same as the referenced object
                    if (value instanceof EObject && modelEditor.equals((EObject)value, referencedObj)) {
                        // remove the reference to the referenced object
                        remove(referencingObj, reference, value);
                    }
                }
            } else {
                final Object value = referencingObj.eGet(reference);
                // If this value is is same as the referenced object
                if (value instanceof EObject && modelEditor.equals((EObject)value, referencedObj)) {
                    // remove the reference to the referenced object
                    unset(referencingObj, reference);
                }
            }
        }
    }

    /**
     * Removes the specified referenced value from the specified referencing object for the specified feature.
     * 
     * @param theReferencingObject the object referencing another object
     * @param theFeature the reference feature
     * @param theValue the feature value which is the referenced object (i.e., the object being deleted)
     * @since 4.2
     */
    protected void remove( EObject theReferencingObject,
                           EReference theFeature,
                           Object theValue ) {
        // only perform delete if none of referencing object's parents are being deleted
        boolean doDelete = true;
        EObject parent = theReferencingObject.eContainer();

        while ((parent != null) && doDelete) {
            if (this.allDeletedObjects.contains(parent)) {
                doDelete = false;
            } else {
                parent = parent.eContainer();
            }
        }

        // Verify that we have not already created a remove command for this feature / value combination.
        // A volatile feature is one that has no storage directly associated with it.
        // It's value is generally derived from the values of other features so it
        // therefore cannot be reset. Fix for defect 12328.
        if (doDelete && !theFeature.isVolatile() && isRemoveCommandNeeded(theReferencingObject, theFeature, theValue)) {
            final Command removeCommand = RemoveCommand.create(this.editingDomain, theReferencingObject, theFeature, theValue);
            this.additionalCommands.add(removeCommand);
        }
    }

    protected void unset( final EObject owner,
                          final EStructuralFeature feature ) {
        // A volatile feature is one that has no storage directly associated with it.
        // It's value is generally derived from the values of other features so it
        // therefore cannot be reset. Fix for defect 12328.
        if (!this.allDeletedObjects.contains(owner) && !feature.isVolatile() && isUnsetCommandNeeded(owner, feature)) {
            final Command setCommand = SetCommand.create(this.editingDomain, owner, feature, null);
            this.additionalCommands.add(setCommand);
        }
    }

    /**
     * Indicates if the specified feature requires to be unset. Only returns <code>true</code> the first time it is called for the
     * specified owner/feature pair. So the caller is required to created the command when the return is a <code>true</code>.
     * Subsequent returns will always return <code>false</code>.
     * 
     * @param theOwner the owner of the feature
     * @param theFeature the feature being checked to see if it needs an unset command
     * @return <code>true</code>if the feature needs to be unset; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isUnsetCommandNeeded( EObject theOwner,
                                          EStructuralFeature theFeature ) {
        /*
         * Need to make sure that an unset is executed only once for an object/feature pair.
         * The problem occurs when the unset is undone. If the opposite feature of the one being set is a multivalued
         * feature (like primary key to foreign keys), the undo adds the value to the list. If the add is performed
         * more than once, a duplicate constraint violation occurs. Ref Defect 17011.
         */
        boolean result = false;
        Set features = (Set)this.objectFeatureUnsetMap.get(theOwner);

        if (features == null) {
            features = new HashSet();
            this.objectFeatureUnsetMap.put(theOwner, features);
        }

        if (!features.contains(theFeature)) {
            features.add(theFeature);
            result = true;
        }

        return result;
    }

    /**
     * @return
     */
    public List getAdditionalCommands() {
        return additionalCommands;
    }

}
