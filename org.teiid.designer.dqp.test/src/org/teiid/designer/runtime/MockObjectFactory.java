/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import org.eclipse.core.runtime.Path;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 *
 */
public class MockObjectFactory {

    public static ModelResource createModelResource( String name,
                                                     String parentPath ) {
        ModelResource parent = mock(ModelResource.class);
        stub(parent.getPath()).toReturn(new Path(parentPath));

        ModelResource modelResource = mock(ModelResource.class);
        stub(modelResource.getItemName()).toReturn(name);
        stub(modelResource.getParent()).toReturn(parent);

        return modelResource;
    }

    private MockObjectFactory() {
        // nothing to do
    }

}
