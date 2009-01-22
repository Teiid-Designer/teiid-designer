/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author dfuglsang
 *
 */
public interface StreamWriter{
        
    void write(OutputStream outputstream, Map options, Resource resource) throws IOException;
    void write(OutputStream outputstream, Map options, Collection objects) throws IOException;
}
