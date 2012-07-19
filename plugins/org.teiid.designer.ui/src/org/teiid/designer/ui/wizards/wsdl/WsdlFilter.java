/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.wizards.wsdl;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.webservice.WebServicePlugin;


/**
 *
 *
 * @since 8.0
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
