/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.reader.StreamReader;

/**
 * @author dfuglsang
 */
public class XmiStreamReader implements StreamReader {

    /**
     * Constructor for XmiStreamReader.
     */
    public XmiStreamReader() {
        super();
        // System.err.println("Created instance of the XmiStreamReader");
    }

    /**
     * @see com.metamatrix.api.mtk.core.reader.MtkStreamReader#read(java.io.InputStream, java.util.Map)
     */
    public Collection read( InputStream inputStream,
                            Map options ) throws IOException {
        if (inputStream == null) {
            final String msg = ModelerCore.Util.getString("XmiStreamReader.The_InputStream_reference_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (options == null) {
            final String msg = ModelerCore.Util.getString("XmiStreamReader.The_Map_reference_may_not_be_null_2"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        List result = Collections.EMPTY_LIST;
        try {
            // Create a temporary container to hold the resource
            final String tmpCntrName = DateUtil.getCurrentDateAsString() + "TempContainer"; //$NON-NLS-1$
            Container container = ModelerCore.createContainer(tmpCntrName);

            // Create a temporary resource in the container
            Resource temp = container.createResource(URI.createURI("XmiStreamReader.xmi")); //$NON-NLS-1$
            temp.load(inputStream, options);

            // Remove contents from temp resource
            result = new ArrayList(temp.getContents());
            for (Iterator iter = result.iterator(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                EcoreUtil.remove(eObject);
            }

            // Clean up temporary resources
            container.getResources().remove(temp);
            temp = null;
            ModelerCore.getRegistry().unregister(tmpCntrName);
            container = null;

        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("XmiStreamReader.Error_loading_resource_into_a_temporary_container_1")); //$NON-NLS-1$
            throw new IOException(e.getMessage());
        }

        return result;
    }

    /**
     * A ResourceSet implementation that utilizes the MetamodelRegistry for resource references that represent a metamodel
     */
    class DelegatedResourceSet extends ResourceSetImpl {
        private MetamodelRegistry registry;

        public DelegatedResourceSet( MetamodelRegistry registry ) {
            super();
            if (registry == null) {
                final String msg = ModelerCore.Util.getString("XmiStreamReader.The_Map_reference_may_not_be_null_2"); //$NON-NLS-1$
                throw new IllegalArgumentException(msg);
            }
            this.registry = registry;
        }

        @Override
        protected Resource delegatedGetResource( URI uri,
                                                 boolean loadOnDemand ) {
            // System.err.println(">> DelegatedResourceSet.delegatedGetResource: URI= " + uri + ", loadOnDemand= " +
            // loadOnDemand);
            if (this.registry != null && this.registry.containsURI(uri)) {
                final Resource resource = this.registry.getResource(uri);
                // System.err.println(">> Returning Resource in the delegatedGetResource with URI \"" + uri + "\"");
                return resource;
            }
            return super.delegatedGetResource(uri, loadOnDemand);
        }
    }

    public Collection read( InputStream inputStream,
                            Map options,
                            Resource resource ) {
        throw new UnsupportedOperationException(ModelerCore.Util.getString("XmiStreamReader.Can_not_perform_operation_6")); //$NON-NLS-1$
    }

    public Collection read( InputStream inputStream,
                            Map options,
                            EObject parent ) {
        throw new UnsupportedOperationException(ModelerCore.Util.getString("XmiStreamReader.Can_not_perform_operation_7")); //$NON-NLS-1$
    }

}
