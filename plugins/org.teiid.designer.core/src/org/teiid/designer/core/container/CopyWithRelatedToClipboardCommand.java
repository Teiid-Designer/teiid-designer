/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.FindRelatedObjectsToBeCopied;
import org.teiid.designer.core.ModelEditorImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.CorePackage;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.metamodels.transformation.MappingClassSetContainer;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationPackage;


/**
 * This command copies the supplied objects as well as "related" objects (e.g., {@link Annotation},
 * {@link org.teiid.designer.metamodels.transformation.TransformationMapping} instances) to the clipboard.
 * The "related" objects are not placed on the clipboard per se, but instead are placed into 
 * the {@link org.teiid.designer.core.container.ContainerEditingDomain#getClipboardModelContents(boolean) ContainerEditingDomain's clipboard ModelContents}
 * object (which is reset each time the clipboard contents are set).
 * @see org.teiid.designer.core.container.PasteWithRelatedFromClipboardCommand
 *
 * @since 8.0
 */
public class CopyWithRelatedToClipboardCommand extends CompoundCommand {

    private final Collection originals;
    private final CopyToClipboardCommandWithMapping mainCopyCommand;
    private final EditingDomain domain;

    /**
     * Construct an instance of CopyToClipboardCommandWithMapping.
     * @param domain
     * @param collection
     */
    public CopyWithRelatedToClipboardCommand(final EditingDomain domain, final Collection collection) {
        super();
        this.originals = collection;
        this.domain = domain;
        this.mainCopyCommand = new CopyToClipboardCommandWithMapping(domain,collection);
        append(this.mainCopyCommand);
    }
    
    @Override
    public void execute() {
        super.execute();
        if ( this.originals != null && this.originals.size() != 0 ) {
            doCopyRelatedObjects();
        }
    }
    
    protected void doCopyRelatedObjects() {
        if ( this.domain instanceof ContainerEditingDomain ) {
            // ------------------------------------------------------
            // Now, see if there are additional things to be done ...
            // ------------------------------------------------------
        
            // Iterate through the original objects, and find any additional objects that should be copied ...
            final FindRelatedObjectsToBeCopied visitor = new FindRelatedObjectsToBeCopied();
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(this.originals,ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(e);
            }
        
            // See if there are any additional objects to be copied ...
            final Collection additionalObjs = visitor.getAdditionalObjects();
            if ( additionalObjs.size() != 0 ) {
                final ModelContents modelContents = ((ContainerEditingDomain)this.domain).getClipboardModelContents(true);
                this.doCreateAdditionalCommands(additionalObjs,modelContents);
            }
        }
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
    protected Collection doCreateAdditionalCommands(final Collection additionalObjs, final ModelContents targetContents) {
        if ( additionalObjs.isEmpty() ) {
            return additionalObjs;
        }
        
        // Find all of the annotations and transformations ...
        final Set annotations = new HashSet();
        final Set transformations = new HashSet();
        final Set mappingClassSets = new HashSet();
        final List remaining = new LinkedList();
        final Iterator iter = additionalObjs.iterator();
        while (iter.hasNext()) {
            final Object additionalObj = iter.next();
            if ( additionalObj instanceof Annotation ) {
                annotations.add(additionalObj);
            } else if ( additionalObj instanceof TransformationMappingRoot ) {
                transformations.add(additionalObj);
            } else if ( additionalObj instanceof MappingClassSet ) {
                mappingClassSets.add(additionalObj);
            } else {
                remaining.add(additionalObj);
            }
        }
        
        // Create the copy command for all of the annotations ...
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
            copyAndAdd(annotations, container, feature);
        }

        // Create the copy command for all of the mapping class sets ...
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
            copyAndAdd(mappingClassSets, container, feature);
        }

        // Create the copy command for all of the transformations ...
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
            copyAndAdd(transformations, container, feature);
        }
        return null;
    }

    protected void copyAndAdd(final Set additionalObjects, final EObject container, final EStructuralFeature feature) {
        final CopyCommand.Helper helper = this.mainCopyCommand.getCopyKeyedByOriginalMap();
        final Command copyCommand = ModelEditorImpl.createCopyCommand(this.domain,additionalObjects,helper);
        final boolean copied = this.appendAndExecute(copyCommand);
        if ( copied ) {
            // Create the add command to place all of the annotations into the annotation container ...
            final Collection copiedAnnotations = copyCommand.getResult();
            final Command addCommand = AddCommand.create(this.domain,container, feature, copiedAnnotations);
            final boolean added = this.appendAndExecute(addCommand); 
            if ( !added ) {
                // Failed, but the copied objects will be left without a parent 
                // and will simply be garbage collected.  However, log anyway ...
                final Object[] params = new Object[]{new Integer(copiedAnnotations.size()),feature.getName()};
                final String msg = ModelerCore.Util.getString("CopyWithRelatedToClipboardCommand.Failed_to_add_{0}_copied_{1}_to_clipboard",params); //$NON-NLS-1$
                ModelerCore.Util.log(msg);      
            }
        }
    }



}
