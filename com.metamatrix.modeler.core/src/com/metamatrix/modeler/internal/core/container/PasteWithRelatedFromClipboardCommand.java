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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.IPasteContributor;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * This command copies the objects on the clipboard (as well as related objects) 
 * and adds them in the specified location(s).  Related objects include 
 * {@link com.metamatrix.metamodels.core.Annotation annotations} and
 * {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot transformations}.
 * <p>
 * This class is actually a compound command that is comprise of an actual
 * {@link org.eclipse.emf.edit.command.PasteFromClipboardCommand} as well as commands to 
 * copy the related objects and place into their appropriate locations.
 * </p><p>
 * Note that the command framework is written to always uses the {@link Command} interface,
 * and never concrete commands.  Therefore, it is possible for this class to 
 * extend {@link org.eclipse.emf.common.command.CompoundCommand} rather than
 * {@link org.eclipse.emf.edit.command.PasteFromClipboardCommand}.
 * </p>
 */
public class PasteWithRelatedFromClipboardCommand extends CompoundCommand {
    
    // ===========================================================================================================================
    // Constants
    
    /**
     * This caches the label.
     */
    protected static final String LABEL = ModelerCore.Util.getString("PasteWithRelatedFromClipboardCommand.Label"); //$NON-NLS-1$

    /**
     * This caches the description.
     */
    protected static final String DESCRIPTION = ModelerCore.Util.getString("PasteWithRelatedFromClipboardCommand.Description"); //$NON-NLS-1$
    
    private static final String PASTE_CONTRIBUTOR_EXT_PT = "pasteContributor"; //$NON-NLS-1$
    private static final String NAME_ATTR = "name"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Static Controller Methods
    
    /**
     * This creates a command to add copies from the clipboard to the specified feature of the owner.
     */
    public static Command create(EditingDomain domain, Object owner, Object feature) {
      return create(domain, owner, feature, CommandParameter.NO_INDEX);
    }

    /**
     * This creates a command to add copies from the clipboard to the specified feature of the owner and at the given index.
     */
    public static Command create(EditingDomain domain, Object owner, Object feature, int index) {
      if (domain == null)
      {
        return new PasteWithRelatedFromClipboardCommand(domain, owner, feature, index, true);
      }
      Command command = 
          domain.createCommand(PasteFromClipboardCommand.class, new CommandParameter(owner, feature, Collections.EMPTY_LIST, index));
      return command;
    }

    // ===========================================================================================================================
    // Variables
    
    /**
     * This it the editing domain.
     */
    protected EditingDomain domain;

    /**
     * This is object where the clipboard copy is pasted.
     */
    protected Object owner;

    /**
     * This is feature of the owner where the clipboard copy is pasted.
     */
    protected Object feature;

    /**
     * This is index in the feature of the owner where the clipboard copy is pasted.
     */
    protected int index;

    /**
     * This controls whether or not to optimize the canExecute (prepare)
     */
    protected boolean optimize;
    
    protected PasteFromClipboardWithMappingCommand pasteCommand;

    // ===========================================================================================================================
    // Constructors
    
    /**
     * This constructs an instance from the domain, which provides access the clipboard collection 
     * via {@link EditingDomain#getCommandStack}.
     */
    public PasteWithRelatedFromClipboardCommand(final EditingDomain domain, final Object owner, 
                                               final Object feature, final int index) {
      this(domain, owner, feature, index, true);
    }

    public PasteWithRelatedFromClipboardCommand(final EditingDomain domain, final Object owner, 
                                               final Object feature, final int index, final boolean optimize) {
        super(MERGE_COMMAND_ALL, LABEL, DESCRIPTION);

        // Initialize the variables ...
        this.domain = domain;
        this.owner = owner;
        this.feature = feature;
        this.index = index;
        this.optimize = optimize;
      
        // Create and add the main PasteFromClipboardCommand ...
        this.pasteCommand = new PasteFromClipboardWithMappingCommand(this.domain,this.owner,this.feature,this.index);
        append(this.pasteCommand);
    }

    // ===========================================================================================================================
    // Controller Methods
    
    /**
     * @see org.eclipse.emf.common.command.CompoundCommand#execute()
     */
    @Override
    public void execute() {
        // Execute any existing commands ...
        super.execute();
        
        // ------------------------------------------------------
        // Now, see if there are additional things to be done ...
        // ------------------------------------------------------
        ModelContents clipboardModelContents = null;
        if ( this.domain instanceof ContainerEditingDomain ) {
            final ContainerEditingDomain ctnrDomain = (ContainerEditingDomain)this.domain;
            clipboardModelContents = ctnrDomain.getClipboardModelContents(false);

            if ( clipboardModelContents != null ) {
                // Find the content helper for the target location ...
                Resource targetResource = null;
                if ( this.owner instanceof Resource ) {
                    targetResource = (Resource)this.owner;
                } else if ( this.owner instanceof EObject ) {
                    targetResource = ((EObject)this.owner).eResource();
                } else if ( this.owner instanceof ModelResource ) {
                    try {
                        targetResource = ((ModelResource)this.owner).getEmfResource();
                    } catch (ModelWorkspaceException e1) {
                        ModelerCore.Util.log(e1);   // do nothing else
                    }
                }
            
                // Continue with the processing only if the target resource was found
                if ( targetResource != null && targetResource instanceof EmfResource ) {
                    final ModelContents targetContents = ((EmfResource)targetResource).getModelContents();

                    doCreateAdditionalCommands(clipboardModelContents, targetContents);

                    // Check if objects were copied (vs. cut)
                    Map map = ctnrDomain.getClipboardContentsOriginalToCopyMapping();
                    if (map != null) {
                        // Allow paste contributors to contribute ...
                        final IConfigurationElement[] elems = 
                            Platform.getExtensionRegistry().getConfigurationElementsFor(ModelerCore.PLUGIN_ID, 
                                                                                        PASTE_CONTRIBUTOR_EXT_PT);
                        if (elems != null) {
                            // Create map from original to pasted objects
                            map = ctnrDomain.getClipboardContentsCopyToOriginalMapping();
                            final CopyCommand.Helper helper = this.pasteCommand.getHelper();
                            final Map origPastedMap = new HashMap(map.size());
                            for (final Iterator iter = helper.entrySet().iterator(); iter.hasNext();) {
                                final Entry entry = (Entry)iter.next();
                                origPastedMap.put(map.get(entry.getKey()), entry.getValue());
                            }
                            // Pass map & target metamodel URI to contributors
                            for (int ndx = elems.length; --ndx >= 0;) {
                                try {
                                    final Object obj = elems[ndx].createExecutableExtension(NAME_ATTR);
                                    if (obj instanceof IPasteContributor) {
                                        targetContents.getAnnotationContainer(true);
                                        final String uri = targetContents.getModelAnnotation().getPrimaryMetamodelUri();
                                        ((IPasteContributor)obj).contribute(origPastedMap, uri);
                                    }
                                } catch (final CoreException err) {
                                    ModelerCore.Util.log(err);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Create additional commands for copying the supplied additional "related" objects, and
     * add-and-execute them (via the {@link PasteFromClipboard#appendAndExecute(}).
     * @param clipboardModelContents the content helper for the clipboard; never null
     * @param targetContents the content helper for the Resource in which the object is being copied
     * and pasted; never null
     */
    protected void doCreateAdditionalCommands(final ModelContents clipboardModelContents, final ModelContents targetContents) {

        // Create the copy command for all of the annotations ...
        final AnnotationContainer clipboardAnnContainer = clipboardModelContents.getAnnotationContainer(false);
        if ( clipboardAnnContainer != null && clipboardAnnContainer.getAnnotations().size() != 0 ) {
            final AnnotationContainer container = targetContents.getAnnotationContainer(true);
            final EStructuralFeature feature = CorePackage.eINSTANCE.getAnnotationContainer_Annotations();
            copyAndAdd(clipboardAnnContainer.getAnnotations(), container, feature);
        }
        
        // If target in PHYSICAL model (i.e. not virtual) then we don't want to do these next two steps
        if( targetContents.getModelAnnotation().getModelType() == ModelType.VIRTUAL_LITERAL) {
            // Create the copy command for all of the mapping classes ...
            final MappingClassSetContainer clipboardMCSetContainer = clipboardModelContents.getMappingClassSetContainer(false);
            if ( clipboardMCSetContainer != null && clipboardMCSetContainer.getMappingClassSets().size() != 0 ) {
                final MappingClassSetContainer container = targetContents.getMappingClassSetContainer(true);
                final EStructuralFeature feature = TransformationPackage.eINSTANCE.getMappingClassSetContainer_MappingClassSets();
                copyAndAdd(clipboardMCSetContainer.getMappingClassSets(), container, feature);
            }
            
            // Create the copy command for all of the transformations ...
            final TransformationContainer clipboardTransContainer = clipboardModelContents.getTransformationContainer(false);
            if ( clipboardTransContainer != null && clipboardTransContainer.getTransformationMappings().size() != 0 ) {
                final TransformationContainer container = targetContents.getTransformationContainer(true);
                final EStructuralFeature feature = TransformationPackage.eINSTANCE.getTransformationContainer_TransformationMappings();
                copyAndAdd(clipboardTransContainer.getTransformationMappings(), container, feature);
            }
        }
    }

    protected void copyAndAdd(final Collection additionalObjects, final EObject container, final EStructuralFeature feature) {
        final CopyCommand.Helper helper = this.pasteCommand.getHelper();
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
                final URI uri = EcoreUtil.getURI(container);
                final Object[] params = new Object[]{new Integer(copiedAnnotations.size()),
                                                     feature.getName(),
                                                     uri != null ? uri.trimFragment().toString() : "<unknown>"}; //$NON-NLS-1$
                final String msg = ModelerCore.Util.getString("PasteWithRelatedFromClipboardCommand.Failed_to_paste_{0}_{1}_from_clipboard_to_{2}",params); //$NON-NLS-1$
                ModelerCore.Util.log(msg);      
            }
        }
    }
}
