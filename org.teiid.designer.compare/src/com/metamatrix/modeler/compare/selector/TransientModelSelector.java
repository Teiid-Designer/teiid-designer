/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
