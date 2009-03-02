/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ObjectExtension
 */
public class ObjectExtension extends EObjectImpl {

    private static final String DELIM = " "; //$NON-NLS-1$

    private EObject extendedObject;
    private Annotation annotation;
    private ModelEditor editor;

    /**
     * Construct an instance of ObjectExtension.
     * 
     */
    public ObjectExtension() {
        super();
    }
    
    
    public ObjectExtension(final EObject extendedObject, final XClass xclass, final ModelEditor editor) {
        this.editor = editor;
        this.extendedObject = extendedObject;
        if ( this.extendedObject != null && xclass != null ) {
            super.eSetClass(xclass);
        }
    }
    
    public ObjectExtension(final EObject extendedObject, final XClass xclass) {
        this(extendedObject, xclass, null);
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @return
     */
    public EObject getExtendedObject() {
        return extendedObject;
    }

    /**
     * @param annotation
     */
    public void setExtendedObject(EObject extendedObject) {
        this.extendedObject = extendedObject;
    }
    
    /**
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eDynamicSet(org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
     * @generated NOT
     */
    @Override
    public void eDynamicSet(EStructuralFeature eFeature, Object newValue) {
        super.eDynamicSet(eFeature, newValue);
        if ( this.extendedObject != null ) {
            if ( eFeature.isTransient() || !eFeature.isChangeable() ) {
                return;
            }
            doDynamicSet(eFeature, newValue);
        }
    }

    /**
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eDynamicUnset(org.eclipse.emf.ecore.EStructuralFeature)
     * @generated NOT
     */
    @Override
    public void eDynamicUnset(EStructuralFeature eFeature) {
        super.eDynamicUnset(eFeature);
        doDynamicUnset(eFeature);
    }

    /**
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eDynamicGet(org.eclipse.emf.ecore.EStructuralFeature, boolean)
     * @generated NOT
     */
    @Override
    public Object eDynamicGet(EStructuralFeature eFeature, boolean resolve) {
        Object result = null;
        if ( this.extendedObject != null ) {
            result = doDynamicGet(eFeature, result);
        }
        if ( result == null ) {
            result = super.eDynamicGet(eFeature, resolve);
        }
        return result;
    }

    /**
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eDynamicIsSet(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean eDynamicIsSet(EStructuralFeature eFeature) {
        return doDynamicIsSet(eFeature);
    }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    public static AnnotationContainer getAnnotationContainer(final Resource eResource) {
        for (final Iterator iter = eResource.getContents().iterator(); iter.hasNext();) {
            final EObject root = (EObject)iter.next();
            if (root instanceof AnnotationContainer) {
                return (AnnotationContainer)root;
            }
        }
        
        return null;
    }
    
    protected Annotation getAnnotation( final boolean createIfRequired ) {
        if ( this.annotation == null ) {
            if ( this.extendedObject != null ) {
                if(this.editor == null) {
                    final Resource resource = extendedObject.eResource();
                    AnnotationContainer cntr = getAnnotationContainer(resource);
                    if (cntr == null) {                        
                        if(createIfRequired && !ModelUtil.isXsdFile(resource) ) {
                            cntr = CoreFactory.eINSTANCE.createAnnotationContainer();
                            resource.getContents().add(cntr);
                        }else {
                            //In non- ModelEditor mode, can't support XSD
                            return null;
                        }
                    }
                    
                    final Annotation existing = cntr.findAnnotation(extendedObject);
                    if ( existing != null) {
                        return existing;
                    }
                    
                    if(createIfRequired) {
                        final Annotation ann = CoreFactory.eINSTANCE.createAnnotation();
                        ann.setAnnotatedObject(extendedObject);
                        cntr.getAnnotations().add(ann);
                        return ann;
                    }
                    
                    return null;
                }
                
                try {
                    final ModelEditor ed = getModelEditor();
                    this.annotation = ed.getAnnotation(this.extendedObject,createIfRequired);
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(e);
                }
                
            }
        }
        return this.annotation;
    }
    
    protected ModelEditor getModelEditor() {
        if ( this.editor == null ) {
            this.editor = ModelerCore.getModelEditor();
        }
        return this.editor;
    }
    
    /** 
     * @param eFeature
     * @param newValue
     * @since 4.2
     */
    protected void doDynamicSet(EStructuralFeature eFeature, Object newValue) {
        final Annotation annotation = getAnnotation(true);
        if ( annotation != null ) {
            final EMap tags = annotation.getTags();
            final Object key = eFeature.getName();
            if ( newValue == null  || StringUtil.Constants.EMPTY_STRING.equals(newValue)) {
                tags.removeKey(key);
            } else {
                final EDataType dt = (EDataType)eFeature.getEType();
                final EPackage ePackage = dt.getEPackage();
                final EFactory fac = ePackage.getEFactoryInstance();
                String newStringValue = null;
                if ( eFeature.isMany() ) {
                    final List values = (List)newValue;
                    final Iterator iter = values.iterator();
                    while (iter.hasNext()) {
                        final Object value = iter.next();
                        if ( newStringValue == null ) {
                            newStringValue = fac.convertToString(dt, value);
                        } else {
                            newStringValue = newStringValue + DELIM + fac.convertToString(dt, value);
                        }
                    }
                    
                } else {
                    newStringValue = fac.convertToString(dt, newValue);
                }
                tags.put(key,newStringValue);
            }
        }
    }
    
    /** 
     * @param eFeature
     * @since 4.2
     */
    protected void doDynamicUnset(EStructuralFeature eFeature) {
        if ( this.extendedObject != null ) {
            final Annotation annotation = getAnnotation(false);
            if ( annotation != null ) {
                final EMap tags = annotation.getTags();
                final Object key = eFeature.getName();
                tags.removeKey(key);
            }
        }
    }

    /** 
     * @param eFeature
     * @param result
     * @return
     * @since 4.2
     */
    protected Object doDynamicGet(EStructuralFeature eFeature,
                                  Object result) {
        final Annotation annotation = getAnnotation(true);
        if ( annotation != null ) {
            final EMap tags = annotation.getTags();
            final Object key = eFeature.getName();
            final EDataType dt = (EDataType)eFeature.getEType();
            final EPackage ePackage = dt.getEPackage();
            final EFactory fac = ePackage.getEFactoryInstance();
            String value = (String)tags.get(key);
            // if the value is null, get default value, if that is null get the type default value                
            if(value == null || StringUtil.Constants.EMPTY_STRING.equals(value)) {
                Object defaultValue = eFeature.getDefaultValue();
                if(defaultValue != null && !StringUtil.Constants.EMPTY_STRING.equals(defaultValue)) {
                    value = defaultValue.toString();
                } else {
                    Object typeDefault = dt.getDefaultValue();
                    value = typeDefault!= null ? typeDefault.toString() : null;
                }
            }
            if ( eFeature.isMany() ) {
                final List values = new BasicEList();
                result = values;
                final StringTokenizer stringTokenizer = new StringTokenizer(value, DELIM);
                
                if (stringTokenizer.hasMoreTokens()) {
	                while( stringTokenizer.hasMoreTokens() ) {
	                    String token = stringTokenizer.nextToken();

	                    try {
	                        values.add(fac.createFromString(dt, token));
	                    } catch (RuntimeException theException) {
	                        // since we currently don't support multi-valued ObjectExtensions
	                        // I couldn't test this code. Not sure what should be done. Wanted
	                        // to keep all values even if they weren't valid. Validation rules
	                        // will indicate if values are invalid. I chose to return the bad
	                        // values in the return list but have their type be String
	                        // instead of the correct EDataType. This could obviously break
	                        // something at some point in the future when multi-values are supported. (Dan F)
	                        values.add(token);
	                    }
	                }
                }
            } else {
                try {
                    result = fac.createFromString(dt, value);
                } catch (RuntimeException theException) {
                    // just return the string representation.
                    result = value;
                }
            }
        }

        return result;
    }
    
    /** 
     * @param eFeature
     * @return
     * @since 4.2
     */
    protected boolean doDynamicIsSet(EStructuralFeature eFeature) {
        if ( this.extendedObject != null ) {
            final Annotation annotation = getAnnotation(false);
            if ( annotation != null ) {
                final EMap tags = annotation.getTags();
                final Object key = eFeature.getName();
                final String value = (String)tags.get(key);
                return value != null;
            }
        }
        return super.eDynamicIsSet(eFeature);
    }
    
    
    /**
     * Indicates if the specified extension property has a valid default value. 
     * @param theExtensionPropertyId the identifier of the property being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.2
     */
    public boolean isValid(Object theExtensionPropertyId) {
        boolean result = true;
        
        Annotation annotation = this.getAnnotation(false);

        if (annotation == null) {
            // should not happen. log if it does.
            final IPath path = ModelerCore.getModelEditor().getModelRelativePath(this);
            final String msg = ModelerCore.Util.getString("ObjectExtension.NullAnnotation", //$NON-NLS-1$
                                                          path);
            ModelerCore.Util.log(IStatus.ERROR, msg);
        } else {
	        final EMap tags = annotation.getTags();
	        
	        if ((tags != null) && tags.containsKey(theExtensionPropertyId)) {
	            final EClass xclass = eProperties.getEClass();
	            final List attributes = xclass.getEAllAttributes();
	            
	            // loop through all attributes checking to see if the string representation of it's value
	            // is compatible with it's type
	            if ((attributes != null) && !attributes.isEmpty()) {
	                for (int size = attributes.size(), i = 0; i < size; ++i) {
	                    final EAttribute attribute = (EAttribute)attributes.get(i);
	                    
	                    if (attribute.getName().equals(theExtensionPropertyId)) {
	                        final EDataType type = (EDataType)attribute.getEType();
	                        final EPackage ePackage = type.getEPackage();
	                        final EFactory factory = ePackage.getEFactoryInstance();
	                        final String value = (String)tags.get(theExtensionPropertyId);
	                        
	                        if (value != null)	{
	                            // could have many values or just one
		                        final StringTokenizer stringTokenizer = new StringTokenizer(value, DELIM);
		                        
		                        if (stringTokenizer.hasMoreTokens()) {
		        	                while (stringTokenizer.hasMoreTokens()) {
		        	                    String token = stringTokenizer.nextToken();
	
		        	                    try {
				                            factory.createFromString(type, token);
				                        } catch (RuntimeException theException) {
				                            result = false;
				                            break;
				                        }
		        	                }
		                        }
	                        }
	                        
	                        break;
	                    }
	                }
	            }
	        } else {
	            // either no tags or extension property not found
	            final IPath path = ModelerCore.getModelEditor().getModelRelativePath(annotation);
	            final Object[] params = new Object[] {theExtensionPropertyId, path};
	            final String msg = ModelerCore.Util.getString("ObjectExtension.ExtendedPropertyNotFound", //$NON-NLS-1$
	                                                          params);
                ModelerCore.Util.log(IStatus.ERROR, msg);
	        }
        }
        
        return result;
    }
    
}
