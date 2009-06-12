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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.mapping.Mapping;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.search.runtime.ReferencesRecord;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;

/**
 * This class finds related objects that should be deleted when the supplied object is deleted. No delete commands are added for
 * the deleted object nor any of it's contained objects (recursively). </p>
 * <p>
 * However, objects that are deleted (for which delete commands are created) include:
 * </p>
 * <p>
 * <ul>
 * <li>The {@link Annotation} for the deleted object;</li>
 * <li>All {@link DiagramEntity} instances in the same resource that reference the deleted object;</li>
 * <li>All {@link InputBinding} instances that reference the deleted object or objects under the deleted object;</li>
 * <li>All {@link TransformationMappingRoot} instances in the same resource whose {@link TransformationMappingRoot#getTarget()
 * target} is the deleted object or an object under the deleted object;</li>
 * <li>All {@link MappingClass} instances that map to the deleted object or objects under the deleted object;
 * <li>All {@link MappingClassSet} instances in the same resource whose {@link MappingClassSet#getTarget() target} is the deleted
 * object or an object under the deleted object;</li>
 * <li>All persistent or transient {@link Diagram} instances under the deleted object (i.e., whose {@link Diagram#getTarget()
 * target} is the deleted object or an object under the deleted object);</li>
 * <li>All {@link Mapping}s for the deleted object when the mapping does not have outputs or if the mapping is column to column
 * mapping and either the input or output column is deleted.
 * <li>All {@link SqlAlias}s whose target is the deleted object.
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.modeler.internal.core.ClearReferencesUponDelete
 * @see com.metamatrix.modeler.core.ModelEditor#delete(EObject)
 */
public class FindRelatedObjectsToDeleted implements ModelVisitor {

    private final List additionalDeleteCommands;
    private final EditingDomain editingDomain;
    private Collection deletedObjects;
    private LinkedList objectsForDeleteProcess;
    private final ModelWorkspaceSearch workspaceSearch;

    /**
     * Construct an instance of FindRelatedObjectsToDeleted.
     */
    public FindRelatedObjectsToDeleted( final EObject deletedObject,
                                        final EditingDomain editingDomain,
                                        final LinkedList objectsForDeleteProcess,
                                        final Collection deletedObjects ) {
        this(deletedObject, editingDomain, objectsForDeleteProcess, deletedObjects, new ModelWorkspaceSearch());
    }

    /**
     * Construct an instance of FindRelatedObjectsToDeleted.
     */
    public FindRelatedObjectsToDeleted( final EObject deletedObject,
                                        final EditingDomain editingDomain,
                                        final LinkedList objectsForDeleteProcess,
                                        final Collection deletedObjects,
                                        final ModelWorkspaceSearch workspaceSearch ) {
        super();
        Assertion.isNotNull(objectsForDeleteProcess);
        Assertion.isNotNull(editingDomain);
        Assertion.isNotNull(workspaceSearch);
        this.additionalDeleteCommands = new ArrayList();
        this.editingDomain = editingDomain;
        this.objectsForDeleteProcess = objectsForDeleteProcess;
        this.deletedObjects = deletedObjects;
        if (this.deletedObjects == null) {
            this.deletedObjects = new HashSet();
        }
        this.workspaceSearch = workspaceSearch;

        // Try to resolve to a EObject since not all EObjects have proxies
        // all the collections should have eitherdelegate to enable lookup
        if (deletedObject != null) {
            this.deletedObjects.add(deletedObject);
            // Compute the set of deleted objects ...
            for (final Iterator iter = deletedObject.eAllContents(); iter.hasNext();) {
                final Object child = iter.next();
                if (child instanceof EObject) {
                    this.deletedObjects.add(child);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean visit( EObject object ) {
        // clean up unidirectional references
        ModelEditorImpl modelEditor = (ModelEditorImpl)ModelerCore.getModelEditor();
        // get objectID and find uni-directional references for that ID
        String objID = modelEditor.getSearchIndexObjectID(object);

        if (objID != null && workspaceSearch != null) {
            // unidirectional references for each object to clean up
            Collection refRecords = workspaceSearch.getUniDirectionalReferencesTo(objID);
            if (refRecords != null && !refRecords.isEmpty()) {
                for (final Iterator refIter = refRecords.iterator(); refIter.hasNext();) {
                    ReferencesRecord refRecord = (ReferencesRecord)refIter.next();
                    String refUUID = refRecord.getUUID();
                    EObject refrencingObj = modelEditor.findObject(refUUID);
                    if (refrencingObj != null) {
                        try {
                            if (refrencingObj.eIsProxy()) {
                                final ContainerImpl cntr = (ContainerImpl)ModelerCore.getModelContainer();
                                refrencingObj = EcoreUtil.resolve(refrencingObj, cntr.getResourceSet());
                            }
                        } catch (Exception err) {
                            // Do nothing if we couldn't resolve the object, let the deleteAsNeeded proceed.
                        }

                        deleteObjectAsNeeded(refrencingObj);
                    }
                }
            }
        }
        // this object has been processed for delet remove from list
        objectsForDeleteProcess.remove(object);
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public boolean visit( Resource resource ) {
        return false;
    }

    /**
     * Delete the referencing eObject based on various rules.
     * 
     * @param referencingObj
     * @since 4.2
     */
    public void deleteObjectAsNeeded( final EObject referencingObj ) {
        // These are ordered such that the types more likely to be found (because there may be more instances)
        // are nearer the top

        // if the parent is marked to be deleted, delete the child
        EObject parent = referencingObj.eContainer();
        if (deletedObjects.contains(referencingObj) || (parent != null && deletedObjects.contains(parent))) {
            return;
        }

        if (referencingObj instanceof Annotation) {
            final Annotation annotation = (Annotation)referencingObj;
            final EObject target = annotation.getAnnotatedObject();
            if (target == null || deletedObjects.contains(target)) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof DiagramEntity) {
            final DiagramEntity entity = (DiagramEntity)referencingObj;
            final EObject target = entity.getModelObject();
            if (deletedObjects.contains(target)) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof InputBinding) {
            final InputBinding binding = (InputBinding)referencingObj;
            final InputParameter inputParam = binding.getInputParameter();
            final InputSet inputSet = inputParam.getInputSet();
            final MappingClassColumn mcCol = binding.getMappingClassColumn();
            if (deletedObjects.contains(inputParam) || deletedObjects.contains(inputSet) || deletedObjects.contains(mcCol)) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof TransformationMappingRoot) {
            final TransformationMappingRoot mappingRoot = (TransformationMappingRoot)referencingObj;
            final EObject target = mappingRoot.getTarget();
            if (deletedObjects.contains(target)) {
                delete(referencingObj);
                return;
            }
            boolean doDelete = true;
            if (mappingRoot.getOutputs() != null && !mappingRoot.getOutputs().isEmpty()) {
                final Iterator outputs = mappingRoot.getOutputs().iterator();
                while (outputs.hasNext() && doDelete) {
                    Object next = outputs.next();
                    if (next instanceof MappingClass) {
                        MappingClass mc = (MappingClass)next;
                        if (!deletedObjects.contains(mc.getMappingClassSet())) {
                            doDelete = false;
                        }
                    } else if (!deletedObjects.contains(next)) {
                        doDelete = false;
                    }
                }
            }
            if (doDelete) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof MappingClassSet) {
            final MappingClassSet mcSet = (MappingClassSet)referencingObj;
            final EObject target = mcSet.getTarget();
            if (deletedObjects.contains(target)) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof Diagram) {
            final Diagram diagram = (Diagram)referencingObj;
            final EObject target = diagram.getTarget();
            if (deletedObjects.contains(target)) {
                delete(referencingObj);
            }
            return;
        }
        if (referencingObj instanceof Mapping) {
            final Mapping mapping = (Mapping)referencingObj;
            final Collection outputs = mapping.getOutputs();
            final Collection inputs = mapping.getInputs();

            // if outputs are empty delete the mapping
            if (outputs.isEmpty()) {
                delete(referencingObj);
                return;
            }
            // check if this is X-1 mapping
            if (outputs.size() == 1) {
                EObject outputObj = (EObject)outputs.iterator().next();
                // if the output is among deleted objects delete mapping
                if (deletedObjects.contains(outputObj)) {
                    delete(referencingObj);
                    return;
                }
                // check this is column - column mapping
                if (AspectManager.getSqlAspect(outputObj) instanceof SqlColumnAspect) {
                    // no imputs delete the mapping
                    if (inputs.isEmpty()) {
                        delete(referencingObj);
                        return;
                    }
                    // if the input is among deleted objects delete mapping
                    Object inputObj = inputs.iterator().next();
                    if (deletedObjects.contains(inputObj)) {
                        delete(referencingObj);
                        return;
                    }
                }
            }
            return;
        }
        if (referencingObj instanceof SqlAlias) {
            final SqlAlias sqlAlias = (SqlAlias)referencingObj;
            final EObject target = sqlAlias.getAliasedObject();
            if (deletedObjects.contains(target)) {
                delete(referencingObj);
            }
            return;
        }
        return;
    }

    protected void delete( EObject eObject ) {
        // fix for 10856... ensure we don't try to delete same object twice
        if (deletedObjects.contains(eObject)) {
            return;
        }

        final Command command = ModelEditorImpl.createDeleteCommand(editingDomain, eObject);
        if (command != null) {
            this.additionalDeleteCommands.add(command);
        }

        // this object needs to further prcessed to check
        // if any of its references could be deleted, add to list
        this.objectsForDeleteProcess.addLast(eObject);

        // Compute the set of deleted objects ...
        for (final Iterator iter = eObject.eAllContents(); iter.hasNext();) {
            Object child = iter.next();
            if (child instanceof EObject) {
                if (!deletedObjects.contains(child)) {
                    // process child objects deletion
                    this.objectsForDeleteProcess.addLast(child);
                    deletedObjects.add(child);
                }
            }
        }
        // Add the deletedObject to the deletedObjects collection
        deletedObjects.add(eObject);
    }

    /**
     * @return
     */
    public List getAdditionalDeleteCommands() {
        return additionalDeleteCommands;
    }

    public Collection getAllDeletedObjects() {
        return this.deletedObjects;
    }
}
