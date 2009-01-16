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

package com.metamatrix.modeler.relationship.ui.navigation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;

/**
 * NavigatorLabelProvider is the label provider for the NavigatorView.  It handles elements of
 * type NavigationNode and NavigationLink.
 */
public class NavigatorLabelProvider extends LabelProvider {

//    private static final String PATH_SEP = " - "; //$NON-NLS-1$
    private static final String CREATE = "create";  //$NON-NLS-1$
    private static final String ADAPTER = "Adapter";  //$NON-NLS-1$
    private static final Class[] NO_CLASSES = new Class[0];
    private static final Object[] NO_ARGS = new Object[0];

    // cache of icons vs EClass, since finding them is slow
    private static final HashMap iconMap = new HashMap();

    private ComposedAdapterFactory adapterFactory;

    /**
     * Construct an instance of NodeLabelProvider.
     */
    public NavigatorLabelProvider() {
        super();
        this.adapterFactory = (ComposedAdapterFactory) ModelerCore.getMetamodelRegistry().getAdapterFactory();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        if ( element instanceof NavigationNode ) {
            Image result = null;
            EClass eClass = ((NavigationNode) element).getMetaclass();
            if (eClass != null)  {
                result = (Image) iconMap.get(eClass);
                if ( result == null ) {
                    Adapter adapter = null;
                    EPackage ePackage = eClass.getEPackage();
                    Collection types = new ArrayList();
                    types.add(ePackage);
                    types.add(IItemLabelProvider.class);
                    AdapterFactory delegateAdapterFactory = adapterFactory.getFactoryForTypes(types);
                    if (delegateAdapterFactory != null) {
                        String methodName = CREATE + eClass.getName() + ADAPTER;
                        Method m = null;
                        try {
                            m = delegateAdapterFactory.getClass().getMethod(methodName, NO_CLASSES);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        if ( m != null ) {
                            try {
                                adapter = (Adapter) m.invoke(delegateAdapterFactory, NO_ARGS);
                            } catch (IllegalArgumentException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            }
                            if ( adapter instanceof ItemProviderAdapter ) {
                            	
	                            Object o = null;
	                            // For most cases, the item providers can handle an eClass.
	                            // However some may not.  In this case, like the XSDSimpleTypeDefinitionItemProvider,
	                            // it cannot.  This will cause a ClassCastException. In these cases, we new up a temporary
	                            // eObject and pass it in to satisfy the method.
								try {
									  o = ((ItemProviderAdapter)adapter).getImage(eClass);
								} catch (ClassCastException cce) {
									EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
									o = ((ItemProviderAdapter)adapter).getImage(eObject);
								}
                                
                                if ( o instanceof Image ) {
                                    result = (Image) o;                                    
                                } else if ( o instanceof URL ) {
                                    result = ExtendedImageRegistry.getInstance().getImage(o);
                                }
                                
                            }
                        }
                    }
                    if ( result != null ) {
                        iconMap.put(eClass, result);                    
                    }
                }
            }
            return result;
        } else if ( element instanceof NavigationLink ) {
            return null;
        }
        return super.getImage(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if ( element instanceof NavigationNode ) {
            return ((NavigationNode) element).getLabel();// + PATH_SEP + ((NavigationNode) element).getPathInModel();
        } else if ( element instanceof NavigationLink ) {
            return ((NavigationLink) element).getLabel();
        }
        return super.getText(element);
    }

}
