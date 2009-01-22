/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.emf.edit.command.CutToClipboardCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.mapping.MappingRoot;
import org.eclipse.emf.mapping.domain.MappingDomain;

import com.metamatrix.modeler.core.util.ModelContents;


/**
 * ContainerEditingDomain
 */
public class ContainerEditingDomain extends AdapterFactoryEditingDomain implements MappingDomain {

    private Map clipboardContentsKeyedByOriginals;
    private ModelContents clipboardModelContents;

    /**
     * Construct an instance of ContainerEditingDomain.
     * @param adapterFactory
     * @param commandStack
     */
    public ContainerEditingDomain( final AdapterFactory adapterFactory,  final CommandStack commandStack) {
        super(adapterFactory, commandStack);
    }

    /**
     * Construct an instance of ContainerEditingDomain.
     * @param adapterFactory
     * @param commandStack
     * @param resourceSet
     */
    public ContainerEditingDomain( final AdapterFactory adapterFactory, final CommandStack commandStack,
                                   final ResourceSet resourceSet) {
        super(adapterFactory, commandStack, resourceSet);

    }
    
    /**
     * @see org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain#createCommand(java.lang.Class, org.eclipse.emf.edit.command.CommandParameter)
     */
    @Override
    public Command createCommand(Class commandClass, CommandParameter commandParameter) {
        if (commandClass == AddCommand.class) {
            if ( commandParameter.owner instanceof Resource ) {
                // We're trying to add objects to the root, so use the special form of the AddCommand ...
                final Resource resource = (Resource)commandParameter.owner;
                return new AddCommand(this,resource.getContents(),commandParameter.getCollection(),commandParameter.getIndex());
            }
            // else let the super handle it ...
        }
        if (commandClass == RemoveCommand.class) {
            if ( commandParameter.owner instanceof Resource ) {
                // We're trying to remove objects from the root, so use the special form of the RemoveCommand ...
                final Resource resource = (Resource)commandParameter.owner;
                return new RemoveCommand(this,resource.getContents(),commandParameter.getCollection());
            }
            // else let the super handle it ...
        }
        if (commandClass == MoveCommand.class) {
            if ( commandParameter.owner instanceof Resource ) {
                // We're trying to remove objects from the root, so use the special form of the RemoveCommand ...
                final Resource resource = (Resource)commandParameter.owner;
                final int index = commandParameter.getIndex();

                Object value = commandParameter.getCollection();
                if ( value == null ) {
                    value = commandParameter.getValue();
                }
                return new MoveCommand(this,resource.getContents(),value,index);
            }
            // else let the super handle it ...
        }
        if (commandClass == CutToClipboardCommand.class) {
            final Command actualCommand = super.createCommand(commandClass, commandParameter);
            return new CutWithRelatedToClipboardCommand(actualCommand, this, commandParameter.getCollection());
        }
        if (commandClass == CopyToClipboardCommand.class) {
          return new CopyWithRelatedToClipboardCommand(this, commandParameter.getCollection());
        }
        if (commandClass == PasteFromClipboardCommand.class) {
            return new PasteWithRelatedFromClipboardCommand(this,commandParameter.owner,commandParameter.feature,commandParameter.index);
        }
        return super.createCommand(commandClass, commandParameter);
    }

    /**
     * @see org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain#setClipboard(java.util.Collection)
     */
    @Override
    public void setClipboard(Collection clipboard) {
        super.setClipboard(clipboard);
        // Clear the mapping ...
        this.clipboardContentsKeyedByOriginals = null;
        this.clipboardModelContents = null;
    }
    
    public void setClipboardMapping( final Map clipboardContentsKeyedByOriginals ) {
        this.clipboardContentsKeyedByOriginals = clipboardContentsKeyedByOriginals;
    }
    
    public Map getClipboardContentsOriginalToCopyMapping() {
        return this.clipboardContentsKeyedByOriginals;
    }
    
    public Map getClipboardContentsCopyToOriginalMapping() {
        final Map result = new HashMap();
        final Iterator iter = this.clipboardContentsKeyedByOriginals.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry element = (Map.Entry)iter.next();
            result.put(element.getValue(),element.getKey());
        }
        return result;
    }
    
    public ModelContents getClipboardModelContents( final boolean forceCreate ) {
        if ( this.clipboardModelContents == null && forceCreate ) {
            this.clipboardModelContents = new ClipboardModelContents();
        }
        return this.clipboardModelContents;
    }
    
    public ModelContents createClipboardModelContents() {
        return new ClipboardModelContents();
    }
    
    public void setClipboardModelContents( final ModelContents contents ) {
        this.clipboardModelContents = contents;
    }


    /**
     * The ContainerEditingDomain is made a MappingDoman because thats what is being assumed by
     * some of the EMF code. 
     */
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getMappingEnablementFlags()
     * @since 4.2
     */
    public int getMappingEnablementFlags() {
        return 0;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getMappingRoot()
     * @since 4.2
     */
    public MappingRoot getMappingRoot() {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getName(java.lang.Object)
     * @since 4.2
     */
    public String getName(Object object) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getOutputMetaObject(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public EObject getOutputMetaObject(EObject inputMetaObject) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getOutputName(java.lang.String)
     * @since 4.2
     */
    public String getOutputName(String inputName) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getOutputTypeClassifier(java.lang.Object)
     * @since 4.2
     */
    public Object getOutputTypeClassifier(Object inputTypeClassifier) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#getTypeClassifier(java.lang.Object)
     * @since 4.2
     */
    public Object getTypeClassifier(Object mappedObject) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#parseInputName(java.lang.String)
     * @since 4.2
     */
    public List parseInputName(String inputName) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#parseOutputName(java.lang.String)
     * @since 4.2
     */
    public List parseOutputName(String outputName) {
        return null;
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#setMappingRoot(org.eclipse.emf.mapping.MappingRoot)
     * @since 4.2
     */
    public void setMappingRoot(MappingRoot root) {
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#setName(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public void setName(Object object,
                        String name) {
    }
    /** 
     * @see org.eclipse.emf.mapping.domain.MappingDomain#setTypeClassifier(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setTypeClassifier(Object mappedObject,
                                  Object typeClassifier) {
    }
    protected class ClipboardModelContents extends ModelContents {
        private final List rootObjects;
        public ClipboardModelContents() {
            super();
            rootObjects = new ArrayList();
        }
        @Override
        public List getAllRootEObjects() {
            return rootObjects;
        }

        @Override
        protected URI getUri() {
            return URI.createURI("Clipboard contents"); //$NON-NLS-1$
        }

        @Override
        protected void setModified(boolean modified) {
            // do nothing
        }
    }
    

}
