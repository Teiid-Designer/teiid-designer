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

package com.metamatrix.modeler.internal.core.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;

/**
 * This command cuts the supplied objects as well as "related" objects (e.g., {@link Annotation},
 * {@link com.metamatrix.metamodels.transformation.TransformationMapping} instances) to the clipboard.
 * The "related" objects are not placed on the clipboard per se, but instead are placed into
 * the {@link com.metamatrix.modeler.internal.core.container.ContainerEditingDomain#getClipboardModelContents(boolean) ContainerEditingDomain's clipboard ModelContents}
 * object (which is reset each time the clipboard contents are set).
 * @see com.metamatrix.modeler.internal.core.container.PasteWithRelatedFromClipboardCommand
 */
public class CutWithRelatedToClipboardCommand extends CompoundCommand {

    private final Collection originals;
    private final Command mainCommand;
    private final EditingDomain domain;
    private final List additionalObjs;
    private ModelContents clipboardModelContents = null;

    /**
     * Construct an instance of CopyToClipboardCommandWithMapping.
     * @param domain
     * @param collection
     */
    public CutWithRelatedToClipboardCommand( final Command command, final EditingDomain domain, final Collection collection) {
        super();
        this.originals = collection;
        this.domain = domain;
        this.mainCommand = command;


        // gather up related objects for the objects in the originals collection
        this.additionalObjs = ((ModelEditorImpl) ModelerCore.getModelEditor()).findRelatedObjects(this.originals, this.domain);
        try {
            // remove references to objects in the collection; create and append
            // remove commands for the objects that need to be removed in the cut
            handleReferencingObjects();
        } catch (ModelerCoreException err) {
        }
      if ( this.originals != null && this.originals.size() != 0 ) {
          // create add commands for related objects (in the additionalObjects list)
          this.clipboardModelContents = createAddCommandsForRelatedObjects();
      }

      // append the main command (passed into the constructor; most likely another cut command for the main EObject
      // that is being removed and added to the clipboard)
      append(this.mainCommand);
    }

    protected void handleReferencingObjects() throws ModelerCoreException {
        ModelEditor modelEditor = ModelerCore.getModelEditor();

        final List originalsToProcess = new ArrayList(this.originals.size());
        final List additionalCommands = new ArrayList();
        ModelWorkspaceSearch workspaceSearch = new ModelWorkspaceSearch();

        // Process only those objects that are not orphans
        for (final Iterator iter = this.originals.iterator(); iter.hasNext();) {
            final EObject eObj = (EObject)iter.next();
            if (eObj.eResource() != null) {
                originalsToProcess.add(eObj);
            }
        }

        // Find other objects to be deleted ...
        final Collection allDeleted = modelEditor.findOtherObjectsToBeDeleted(originalsToProcess, this.domain, additionalCommands, workspaceSearch);
        // Find references to objects being deleted ...
        modelEditor.findReferencesToObjectsBeingDeleted(allDeleted, this.domain, additionalCommands, workspaceSearch);
        // Add any new commands to the compound command ...
        for (final Iterator cmdIter = additionalCommands.iterator(); cmdIter.hasNext();) {
            append((Command)cmdIter.next());
        }

    }

    protected ModelContents createAddCommandsForRelatedObjects() {
            if (this.additionalObjs.size() != 0) {
                final ContainerEditingDomain containerEdDomain = (ContainerEditingDomain)this.domain;


                //final ModelContents modelContents = containerEdDomain.getClipboardModelContents(true);
                final ModelContents modelContents = containerEdDomain.createClipboardModelContents();

                // Make sure other commands don't set the actual clipboard contents, otherwise the
                // container editing domain will clear out the clipboard model contents.
                doCreateAdditionalCommands(modelContents);
                containerEdDomain.setClipboardMapping(new SelfKnowledgableCopyHelper() );
                return modelContents;
            }
        return null;
    }

    /**
     * Create additional commands for copying the supplied additional "related" objects, and
     * add-and-execute them (via the {@link PasteFromClipboard#appendAndExecute(}).
     * @param additionalObjs the original "related" objects that are to be copied and added to the
     * correct location; never null
     * @param targetContents the content helper for the Resource in which the object is being copied
     * and pasted; never null
     * @return the subset of <code>additionalObjs</code> that were not handled by this implementation;
     * never null
     */
    protected Collection doCreateAdditionalCommands(final ModelContents targetContents) {
        if (this.additionalObjs.isEmpty() ) {
            return this.additionalObjs;
        }

        // Find all of the annotations and transformations ...
        final Set annotations = new HashSet();
        final Set mappingClassSets = new HashSet();
        final Set transformations = new HashSet();
        final List remaining = new ArrayList();
        final Iterator iter = this.additionalObjs.iterator();
        while (iter.hasNext()) {
            final Object additionalObj = iter.next();
            if ( additionalObj instanceof Annotation ) {
                annotations.add(additionalObj);
            } else if ( additionalObj instanceof MappingClassSet ) {
                mappingClassSets.add(additionalObj);
            } else if ( additionalObj instanceof TransformationMappingRoot ) {
                transformations.add(additionalObj);
            } else {
                remaining.add(additionalObj);
            }
        }

        // Create the add command for all of the annotations ...
        if ( annotations.size() != 0 ) {
            // Note:  container will probably be null due to changes in the following underlying method call.
            // This is due to the targetContents' Resource == NULL (see ModelResourceContainerFactory.getAnnotationContainer());
            AnnotationContainer container = targetContents.getAnnotationContainer(true);
            if( container == null ) {
                // it's null, so we'll new one up via the CoreFactory....
                container = CoreFactory.eINSTANCE.createAnnotationContainer();
                // since one wasn't found, we need to create a temporary one and add to contents
                targetContents.getAllRootEObjects().add(container);
            }

            final EStructuralFeature feature = CorePackage.eINSTANCE.getAnnotationContainer_Annotations();
            createAndAppendAddCommand(annotations, container, feature);
        }

        // Create the add command for all of the mapping class sets ...
        if ( mappingClassSets.size() != 0 ) {
            // Note:  container will probably be null due to changes in the following underlying method call.
            // This is due to the targetContents' Resource == NULL (see ModelResourceContainerFactory.getMappingClassSetContainer());
            MappingClassSetContainer container = targetContents.getMappingClassSetContainer(true);
            if( container == null ) {
                // it's null, so we'll new one up via the TransformationFactory....
                container = TransformationFactory.eINSTANCE.createMappingClassSetContainer();
                // since one wasn't found, we need to create a temporary one and add to contents
                targetContents.getAllRootEObjects().add(container);
            }

            final EStructuralFeature feature = TransformationPackage.eINSTANCE.getMappingClassSetContainer_MappingClassSets();
            createAndAppendAddCommand(mappingClassSets, container, feature);
        }

        // Create the add command for all of the transformations ...
        if ( transformations.size() != 0 ) {
            // Note:  container will probably be null due to changes in the following underlying method call.
            // This is due to the targetContents' Resource == NULL (see ModelResourceContainerFactory.getMappingClassSetContainer());
            TransformationContainer container = targetContents.getTransformationContainer(true);
            if( container == null ) {
                // it's null, so we'll new one up via the TransformationFactory....
                container = TransformationFactory.eINSTANCE.createTransformationContainer();
                // since one wasn't found, we need to create a temporary one and add to contents
                targetContents.getAllRootEObjects().add(container);
            }

            final EStructuralFeature feature = TransformationPackage.eINSTANCE.getTransformationContainer_TransformationMappings();
            createAndAppendAddCommand(transformations, container, feature);
        }
        return null;
    }

    protected void createAndAppendAddCommand(final Set objectsToAdd, final EObject container, final EStructuralFeature feature) {
            final Command addCommand = AddCommand.create(this.domain,container,feature,objectsToAdd);
            append(addCommand);
    }

    @Override
    public void execute() {
        // clear out all existing objects (including the ModelContents if one) ...
        this.domain.setClipboard(null);

        // execute the remove commands for the deleted/referenced objects
        super.execute();

        // set the clipboard model contents
        if (this.clipboardModelContents != null && this.domain instanceof ContainerEditingDomain ) {
            final ContainerEditingDomain containerEdDomain = (ContainerEditingDomain) this.domain;
            final ModelContents existingContents = containerEdDomain.getClipboardModelContents(false);
            if (existingContents == null) {
                containerEdDomain.setClipboardModelContents(this.clipboardModelContents);
            } else if (existingContents != this.clipboardModelContents) {
                final String msg = ModelerCore.Util.getString("CutWithRelatedToClipboardCommand.Unexpected_existing_model_contents_on_clipboard"); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.WARNING,msg);
                containerEdDomain.setClipboardModelContents(this.clipboardModelContents);
            }
        }
    }

    public class SelfKnowledgableCopyHelper extends CopyCommand.Helper {
        /**
         */
        private static final long serialVersionUID = 1L;

        /**
         * @see java.util.HashMap#get(java.lang.Object)
         */
        public EObject get( EObject key ) {
			EObject result = super.get(key);
            if ( result == null ) {
                result = key;
            }
            return result;
        }

        /**
         * @see org.eclipse.emf.edit.command.CopyCommand.Helper#getCopy(org.eclipse.emf.ecore.EObject)
         */
        @Override
        public EObject getCopy(EObject object) {
            EObject result = super.getCopy(object);
            if ( result == null ) {
                result = object;
            }
            return result;
        }
    }
}
