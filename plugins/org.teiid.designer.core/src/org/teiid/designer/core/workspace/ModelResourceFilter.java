/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import org.eclipse.core.resources.IResource;

/** ResourceFilter that only accepts Resources that are models.
 *  Uses ModelUtil.isModelFile to determine this.
 * @author PForhan
 */
public class ModelResourceFilter implements ResourceFilter {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ResourceFilter#accept(org.eclipse.core.resources.IResource)
     */
    @Override
	public boolean accept(IResource res) {
        return ModelUtil.isModelFile(res,true);
    }

}
