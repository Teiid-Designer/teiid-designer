/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.ui.wizards.wsdl;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 *
 */
public class WsdlFilter extends ViewerFilter implements UiConstants {

    @Override
    public boolean select( Viewer theViewer,
                           Object theParentElement,
                           Object theElement ) {
        boolean result = false;

        if (theElement instanceof IContainer) {
            IProject project = ((IContainer)theElement).getProject();

            // check for closed project
            if (project.isOpen()) {
                try {
                    if (project.getNature(ModelerCore.NATURE_ID) != null) {
                        result = true;
                    }
                } catch (CoreException theException) {
                    Util.log(theException);
                }
            }
        } else if (theElement instanceof IFile) {
            result = WebServicePlugin.isWsdlFile((IFile)theElement);
        } else if (theElement instanceof File) {
            return (((File)theElement).isDirectory() || WebServicePlugin.isWsdlFile(((File)theElement)));
        }

        return result;
    }
}
