/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * The DatatypeFinder interface defines the methods to locate existing datatype objects.
 * Different implementations might locate them from the workspace, from a working set,
 * from a list of models, from a single model, etc.  Each implementation is also responsible
 * for handling any ambiguities
 */
public interface DatatypeFinder {
    
    /**
     * Locate an existing datatype object given it's name.  If the name is ambiguous within
     * the context of this selector, then the implementation either chooses one of the
     * datatypes to return, returns null, or throws an exception.  If this methods returns
     * null, then the calling component may attempt to find all datatypes with the supplied
     * name (see {@link #findAllDatatypes(String)}), and choose a datatype itself.
     * @param name the name of the datatype; null if the "default" datatype is to be returned
     * @return the datatype that has the supplied name, or null if no (single) datatype could
     * be found with that name
     * @throws CoreException if there is an error while obtaining the datatype
     */
    EObject findDatatype( String name ) throws CoreException;
    
    /**
     * Locate an existing datatype object given it's {@link URI}.  If the URI is ambiguous within
     * the context of this selector, then the implementation either chooses one of the
     * datatypes to return, returns null, or throws an exception.  If this methods returns
     * null, then the calling component may attempt to find all datatypes with the supplied
     * URI (see {@link #findAllDatatypes(URI)}), and choose a datatype itself.
     * @param uri the URI of the datatype; null if the "default" datatype is to be returned
     * @return the datatype that has the supplied URI, or null if no (single) datatype could
     * be found with that URI
     * @throws CoreException if there is an error while obtaining the datatype
     */
    EObject findDatatype( URI uri ) throws CoreException;

    /**
     * Locate all datatypes that have the supplied name.  The result is generally ordered by
     * the implementation so that the first object in the list is the same object returned
     * by {@link #findDatatype(String)} with the same name supplied as an argument.
     * @param name the name of the datatype; null if the list of "default" datatype is to be returned
     * @return the datatype instances with the supplied name, ordered such that the first datatype
     * in the list is also that returned by {@link #findDatatype(String) findDatatype(name)}.
     * @throws CoreException if there is an error while obtaining the datatypes
     */
    List findAllDatatypes( String name ) throws CoreException;
    
    /**
     * Locate all datatypes that have the supplied {@link URI}.  The result is generally ordered by
     * the implementation so that the first object in the list is the same object returned
     * by {@link #findDatatype(URI)} with the same URI supplied as an argument.
     * @param uri the URI of the datatype; null if the list of "default" datatype is to be returned
     * @return the datatype instances with the supplied URI, ordered such that the first datatype
     * in the list is also that returned by {@link #findDatatype(URI) findDatatype(uri)}.
     * @throws CoreException if there is an error while obtaining the datatypes
     */
    List findAllDatatypes( URI uri ) throws CoreException;
}
