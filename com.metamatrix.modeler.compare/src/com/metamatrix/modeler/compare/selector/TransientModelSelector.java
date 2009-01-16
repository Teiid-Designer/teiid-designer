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

package com.metamatrix.modeler.compare.selector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;


/**
 * The TransientModelSelector is not backed by an {@link org.eclipse.emf.ecore.resource.Resource EMF Resource}, and so can be
 * used only in limited ways. Currently, the only functionality not supported is {@link #rebuildModelImports()}, and of course no
 * persistence mechanism is available.
 * 
 * @since 4.1
 */
public class TransientModelSelector extends URIModelSelector {
    //============================================================================================================================
    // Constructors
    
    /**
     * @since 4.1
     */
    public TransientModelSelector(final String uri) {
        this(URI.createURI(uri));
    }
    
    /**
     * @since 4.1
     */
    public TransientModelSelector(final URI uri) {
        super(uri);
    }
    
    //============================================================================================================================
    // Overridden Methods
    
    /** 
     * @see com.metamatrix.modeler.compare.selector.URIModelSelector#open()
     * @since 4.1
     */
    @Override
    public void open() {
        if (this.resource == null) {
            // Defect 23340 - somehow we lost the change to use MtkXmiResourceImpl instead of EmfResource
            // fixing it again.
            final ResourceSet set = new ResourceSetImpl();
            final MtkXmiResourceImpl resrc = new MtkXmiResourceImpl(getUri());
            set.getResources().add(resrc);
            this.contents = resrc.getModelContents();
            this.resource = resrc;
        }
    }
}
