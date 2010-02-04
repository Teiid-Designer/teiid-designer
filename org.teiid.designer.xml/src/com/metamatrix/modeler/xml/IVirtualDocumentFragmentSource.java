/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xml;

import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * @author PForhan
 */
public interface IVirtualDocumentFragmentSource {
    /** Get the fragments this source has to offer.
      *  It is up to the implementation whether calling this method
      *  will make permanent changes to data or whether this is a 
      *  simple accessor.
      * @param monitor IProgressMonitor to use
      * @return an array of Fragments and Documents.
      */
    public XmlFragment[] getFragments(ModelResource modelResource, IProgressMonitor monitor);

    /** Refresh fragment information from the source, if any.
     * 
     * @param isVisible whether the results of the update will be visible to the user
     * @param monitor The monitor to pay attention to.
     */
   public void updateSourceFragments(boolean isVisible, IProgressMonitor monitor);
}
