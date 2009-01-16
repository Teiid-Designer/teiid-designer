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

package com.metamatrix.modeler.internal.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

    
    public class FindObjectLabelProvider extends LabelProvider {
    
        public static final int OBJECT      = 0; 
        public static final int CONTAINER   = 1;
        
        private int iStyle = 0;
    
        
        public FindObjectLabelProvider( int iStyle ) {
            super();
            this.iStyle = iStyle;
        }

        @Override
        public String getText( Object element ) {
            String sText = " Unknown "; //$NON-NLS-1$
            if ( element instanceof String ) {
                sText = (String)element;
            }
            else 
            if ( element instanceof ResourceObjectRecord ) {


                ResourceObjectRecord ror = (ResourceObjectRecord)element;
                if ( iStyle == OBJECT ) {
                    sText = ror.getName();
                }
                else 
                if ( iStyle == CONTAINER ) {

                    EObject eo = getEObjectForRecord( ror );
                    if ( eo != null ) {
                        IPath path = ModelerCore.getModelEditor().getModelRelativePath( eo );
                        sText = ror.getResourcePath() + path.makeAbsolute().toString();
                    } else {
                        sText = ror.getResourcePath();
                    }
                }
            }
            
            return sText;        
        }
    
        @Override
        public Image getImage( Object element ) {
            Image imgResult = null;

            if ( element instanceof ResourceObjectRecord ) {
                ResourceObjectRecord ror = (ResourceObjectRecord)element;
                
                 try {
                    final Container container = ModelerCore.getModelContainer();
                    final URI theUri = URI.createURI( ror.getMetaclassURI() );
                    EObject eoClass = container.getEObject( theUri, true );
                    
                    if( eoClass != null && !(eoClass instanceof EClass) )
                        eoClass = eoClass.eClass();
                    
                    if( eoClass != null )
                        imgResult 
                            = ModelObjectUtilities.getImage( (EClass)eoClass );
                } catch ( Exception ce ) {
                    ModelerCore.Util.log( IStatus.ERROR, ce, ce.getMessage() );                                       
                } 
            }
            
            return imgResult;
        }   
        
        public EObject getEObjectForRecord( ResourceObjectRecord ror ) {
                                
            EObject eObj = null;
            try {
                URI uri = URI.createURI( ror.getObjectURI() );
                // Fix for Defect 16700 to avoid NPE in getEObject() call
                if( uri.fragment() != null )
                    eObj = ModelerCore.getModelContainer().getEObject( uri, true );
            
            } catch ( CoreException ce ) {
                ModelerCore.Util.log(IStatus.ERROR, ce, ce.getMessage());                    
            }

            return eObj;  
        }
    }   

