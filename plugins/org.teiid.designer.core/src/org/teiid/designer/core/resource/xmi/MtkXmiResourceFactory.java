/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.resource.xmi;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * @author Lance Phillips
 *
 * @since 8.0
 */
public class MtkXmiResourceFactory extends ResourceFactoryImpl {
    
    /**
   * Returns a newly allocated default resource {@link ResourceImpl#ResourceImpl(URI) implementation}.
   * @param uri the URI.
   * @return a new resource for the URI.
   */
  @Override
public Resource createResource(URI uri) {
  	MtkXmiResourceImpl resource = new MtkXmiResourceImpl(uri);
    return resource;
  }

}
