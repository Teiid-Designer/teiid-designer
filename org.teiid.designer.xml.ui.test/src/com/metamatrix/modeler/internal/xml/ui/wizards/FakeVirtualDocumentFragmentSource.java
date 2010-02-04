/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;

public class FakeVirtualDocumentFragmentSource implements IVirtualDocumentFragmentSource {

    public XmlFragment[] getFragments(ModelResource modelResource, IProgressMonitor monitor) {
        return new XmlFragment[0];
    }

    public void updateSourceFragments(boolean isVisible, IProgressMonitor monitor) {
        // do nothing
    }

}
