/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.teiid.designer.core.refactor.AbstractRefactorModelHandler;


/**
 * 
 *
 * @since 8.0
 */
public class VdbRefactorHandler extends AbstractRefactorModelHandler {

    @Override
    public void postProcess( RefactorType type, IResource refactoredResource) throws Exception {
        // only care about renames
        if ((type == RefactorType.RENAME) && (refactoredResource.getType() == IResource.FILE)
                && Vdb.FILE_EXTENSION_NO_DOT.equals(((IFile)refactoredResource).getFileExtension())) {
            // just save VDB to get new manifest written out
            Vdb renamedVdb = new XmiVdb((IFile)refactoredResource);
            renamedVdb.save();
        }
    }
}
