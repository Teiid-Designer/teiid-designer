/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;

/**
 * 
 */
class ModelExtensionDefinitionEditorInput implements IEditorInput {

    private static final ModelExtensionDefinitionParser definitionParser = new ModelExtensionDefinitionParser();

    private final ModelExtensionDefinition med;

    ModelExtensionDefinitionEditorInput( FileEditorInput editorInput ) {
        editorInput.getFile().getFullPath().toFile();
        this.med = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    @Override
    public boolean exists() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    @Override
    public String getName() {
        return this.med.getNamespacePrefix();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return this.med.getDescription();
    }

}
