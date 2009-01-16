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

package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertySource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * ModelPropertySource
 */
public class ModelPropertySource implements IPropertySource {

    // ===================================
    // Static attributes

    private static final AdapterFactoryContentProvider annotationPropertySourceProvider 
        = ModelUtilities.getEmfAdapterFactoryContentProvider();


    // ===================================
    // Instance varialbes

    private IFile modelFile;
    private ModelResource modelResource;
    private ModelAnnotation annotation;
    private IPropertySource delegate;
    private ExtensionPropertySource extensionDelegate;

    // ===================================
    // Constructors

    /**
     * Construct an instance of ModelPropertySource.
     */
    public ModelPropertySource(IFile modelFile) {
        this.modelFile = modelFile;
        // look it up in the Platform's AdapterManager
        IAdapterManager manager = Platform.getAdapterManager();
        delegate = (IPropertySource) manager.getAdapter(modelFile, IPropertySource.class);
        
        boolean requiredStart = ModelerCore.startTxn(false,false,"Adapter Property Source",this); //$NON-NLS-1$
        boolean succeeded = false;
        
        try {
            this.modelResource = ModelUtilities.getModelResource(modelFile, false);
            if ( modelResource != null && modelResource.isOpen() ) {
                try {
                    this.annotation = modelResource.getModelAnnotation();
                } catch (ModelWorkspaceException err) {
                    if ( !modelResource.hasErrors() ) {
                        // No errors, so we should log this exception ...
                        UiConstants.Util.log(err);
                    }
                }
                if ( this.annotation != null ) {
                    annotationPropertySourceProvider.getAdapterFactory().adapt(this.annotation, IItemPropertySource.class);
                    extensionDelegate = new ExtensionPropertySource(this.annotation);
                }
            }
            succeeded = true;
        } catch (ModelerCoreException e){
            UiConstants.Util.log(e);
        }  finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Obtain an array of IPropertyDescriptor instances representing the model annotation
     * @return
     */
    private IPropertyDescriptor[] getAnnotationPropertyDescriptors() {
        IPropertyDescriptor[] result = null; 
        if ( this.annotation != null ) {
            // get the property descriptors off the extension object
            result = extensionDelegate.getPropertyDescriptors();
        }

        return result;
    }


    // ===================================
    // IPropertySource methods

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return delegate.getEditableValue();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if ( this.annotation == null ) {
            return delegate.getPropertyDescriptors();
        }
        // add the annotation's property descriptors to the array
        IPropertyDescriptor[] fileProperties = delegate.getPropertyDescriptors();
        IPropertyDescriptor[] annotationProperties = getAnnotationPropertyDescriptors();
        if ( annotationProperties == null || annotationProperties.length == 0 ) {
            return fileProperties;
        }
        int index = 0;
        IPropertyDescriptor[] result = new IPropertyDescriptor[fileProperties.length + annotationProperties.length];
        for ( int i=0 ; i<fileProperties.length ; ++i ) {
            result[index++] = fileProperties[i];
        }
        // use ReadOnlyPropertyDescriptor if the model is not open in an Editor.
        boolean readOnly = this.modelFile.isReadOnly();
        boolean editorOpen = ModelEditorManager.isOpen(this.modelFile);
        for ( int i=0 ; i<annotationProperties.length ; ++i ) { 
            if ( readOnly ) {
                result[index++] = new ReadOnlyPropertyDescriptor(annotationProperties[i], ReadOnlyPropertyDescriptor.READ_ONLY_RESOURCE);
            } else if ( ! editorOpen ) {
                result[index++] = new ReadOnlyPropertyDescriptor(annotationProperties[i], ReadOnlyPropertyDescriptor.NO_MODEL_EDITOR, this.modelFile);
            } else {
                result[index++] = annotationProperties[i];
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        if ( extensionDelegate != null && extensionDelegate.isExtensionProperty(id) ) {
            return extensionDelegate.getPropertyValue(id);
        }
        
        return delegate.getPropertyValue(id);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet(Object id) {
        if ( extensionDelegate != null && extensionDelegate.isExtensionProperty(id) ) {
            return extensionDelegate.isPropertySet(id);
        }
        
        return delegate.isPropertySet(id);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue(Object id) {
        if ( extensionDelegate != null && extensionDelegate.isExtensionProperty(id) ) {
            extensionDelegate.resetPropertyValue(id);
        } else {
            delegate.resetPropertyValue(id);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
        if ( extensionDelegate != null && extensionDelegate.isExtensionProperty(id) ) {
            extensionDelegate.setPropertyValue(id, value);
        } else {
            delegate.setPropertyValue(id, value);
        }
    }

}
