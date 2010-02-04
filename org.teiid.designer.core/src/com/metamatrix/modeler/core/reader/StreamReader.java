/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author dfuglsang    
 *
 */
public interface StreamReader{
    
    Collection read(InputStream inputStream, Map options, Resource resource) throws IOException;
    
    Collection read(InputStream inputStream, Map options, EObject parent) throws IOException;
    
    Collection read(InputStream inputStream, Map options) throws IOException;

}
