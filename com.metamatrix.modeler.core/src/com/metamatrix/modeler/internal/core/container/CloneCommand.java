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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.internal.core.FindRelatedObjectsToBeCopied;

/**
 * CloneCommand
 */
public class CloneCommand extends CompoundCommand {

    /**
     * This creates a command that clones the given collection of objects.
     */
    public static Command create(EditingDomain domain, final EObject objectToBeCloned) {
        if (domain == null) {
            CloneCommand command = new CloneCommand(domain, objectToBeCloned);
            return command;
        }
        Command command = domain.createCommand(CloneCommand.class, new CommandParameter(null, null, objectToBeCloned));
        return command;
    }

    /**
     * This creates a command that clones the given collection of objects.
     */
    public static Command create(EditingDomain domain, final Collection objectsToBeCloned) {
        if (domain == null) {
            CloneCommand command = new CloneCommand(domain, objectsToBeCloned);
            return command;
        }
        Command command = domain.createCommand(CloneCommand.class, new CommandParameter(null, null, objectsToBeCloned));
        return command;
    }

    private final EditingDomain domain;
    private final Collection objectsToBeCloned;
    private final CopyCommand.Helper helper;
    private final Collection results;

    /**
     * Construct an instance of CloneCommand.
     * @param domain the editing domain; may not be null
     * @param objectToBeCloned the object that is to be cloned one time
     */
    public CloneCommand( final EditingDomain domain, final EObject objectToBeCloned ) {
        this(domain,Collections.singletonList(objectToBeCloned));
    }

    /**
     * Construct an instance of CloneCommand.
     * @param domain the editing domain; may not be null
     * @param objectsToBeCloned the list of objects that are to be cloned one time
     */
    public CloneCommand( final EditingDomain domain, final Collection objectsToBeCloned ) {
        super();
        this.domain = domain;
        this.objectsToBeCloned = objectsToBeCloned;
        this.helper = new CopyCommand.Helper();
        this.results = new HashSet();
    }
    
    public CopyCommand.Helper getHelper() {
        return this.helper;
    }
    
    /**
     * Construct the commands in this compound command.
     * @see org.eclipse.emf.common.command.CompoundCommand#prepare()
     */
    @Override
    protected boolean prepare() {
        // Ensure there are objects to be cloned ...
        if ( this.objectsToBeCloned == null || this.objectsToBeCloned.isEmpty() ) {
            return false;
        }
        return true;
    }

    
    /**
     * @see org.eclipse.emf.common.command.CompoundCommand#execute()
     */
    @Override
    public void execute() {
        this.commandList.clear();
        addCommandsToClone(this.objectsToBeCloned,true);
        
        if ( this.domain instanceof ContainerEditingDomain ) {
            // ------------------------------------------------------
            // Now, see if there are additional things to be done ...
            // ------------------------------------------------------
        
            // Iterate through the original objects, and find any additional objects that should be copied ...
            final FindRelatedObjectsToBeCopied visitor = new FindRelatedObjectsToBeCopied();
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(this.objectsToBeCloned,ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(e);
            }
        
            // See if there are any additional objects to be copied ...
            final Collection additionalObjs = visitor.getAdditionalObjects();
            if ( additionalObjs.size() != 0 ) {
                addCommandsToClone(additionalObjs,false);
            }
        }
    }
    
    /**
     * @see org.eclipse.emf.common.command.CompoundCommand#getResult()
     */
    @Override
    public Collection getResult() {
        return this.results;
    }


    protected void addCommandsToClone(final Collection objectsToClone, final boolean addToResults ) {
        // Walk through the objects to be cloned, and construct the appropriate command(s) ...
        if (! objectsToClone.isEmpty() ) {            
            // Find all of the annotations and transformations ...
            final Set annotations = new HashSet();
            final Set transformations = new HashSet();
            final Set mappingClassSets = new HashSet();
            final List remaining = new LinkedList();
            
            for (final Iterator it = objectsToClone.iterator(); it.hasNext();) {
                final Object additionalObj = it.next();
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
                cloneAndAdd(annotations, addToResults);
            }

            // Create the copy command for all of the mapping class sets ...
            if ( mappingClassSets.size() != 0 ) {
                cloneAndAdd(mappingClassSets, addToResults);
            }

            // Create the copy command for all of the transformations ...
            if ( transformations.size() != 0 ) {
                cloneAndAdd(transformations, addToResults);
            }

            // Create the copy command for the remaining objects
            if ( remaining.size() != 0 ) {
                cloneAndAdd(remaining, addToResults);
            }            
        }
    }
    
    protected void cloneAndAdd(final Collection objectsToClone, final boolean addToResults) {
        for (final Iterator it = objectsToClone.iterator(); it.hasNext();) {
            final Object obj = it.next();
            if ( obj instanceof EObject ) {
                final EObject eObject = (EObject)obj;
                // Copy this object (use a helper) ...
                final Command copyCommand = domain.createCommand(CopyCommand.class, new CommandParameter(eObject, null, helper));
                final boolean copied = this.appendAndExecute(copyCommand);
                
                if ( copied ) {
                    final Collection copiedObjects = copyCommand.getResult();
                    if ( addToResults ) {
                        this.results.addAll(copiedObjects);
                    }
                    
                    // See where the original object lived
                    final EObject parent = eObject.eContainer();
                    if ( parent != null ) {
                        // And then add the new copy under the parent using the same feature as the original ...
                        final EReference reference = eObject.eContainmentFeature();
                        if(reference != null && reference.isMany() ){
                            final List values = (List) parent.eGet(reference);
                            if(values instanceof EList){
                                final int index = values.indexOf(eObject) + 1;
                                final Command addCommand = AddCommand.create(this.domain,parent,reference,copiedObjects,index);
                                this.appendAndExecute(addCommand);
                            }
                        }
                    } else {
                        // There is no parent, so it might be a root-level object ...
                        final Resource resource = eObject.eResource();
                        if ( resource != null ) {
                            // Simply add the object to the end of the resource ...
                            // (This relies upon the editing domain to handle adding root objects!)
                            final List roots = resource.getContents();
                            final int index = roots.indexOf(eObject)+1;
                            final Command addCommand = AddCommand.create(this.domain,resource,null,copiedObjects,index);
                            this.appendAndExecute(addCommand);
                        } 
                        // else, not in a resource, so don't know what to do with it!
                    }
                }
            }            
        }        
    }
}
