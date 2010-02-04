/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.compare.selector.ModelSelector;

/**
 * XsdDatatypeFinder
 */
public class XsdDatatypeFinder implements DatatypeFinder {

    private final ModelSelector datatypeModelSelector;
    private final DatatypeFinder bidtSelector;
    private boolean initialized;
    private final Map datatypeByName;
//    private final Map datatypeByUri;

    /**
     * Construct an instance of XsdDatatypeFinder.
     * 
     */
    public XsdDatatypeFinder( final ModelSelector modelSelector, final DatatypeFinder bidtSelector ) {
        super();
        ArgCheck.isNotNull(modelSelector);
        this.datatypeModelSelector = modelSelector;
        this.bidtSelector = bidtSelector != null ? bidtSelector : new NullDatatypeFinder();
        this.initialized = false;
        this.datatypeByName = new HashMap();
//        this.datatypeByUri = new HashMap();
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype(final String name) throws CoreException {
        if ( !initialized ) {
            initialize();
        }
        if ( name != null ) {
            final LinkedList cached = (LinkedList) this.datatypeByName.get(name);
            if ( cached != null && cached.size() != 0 ) {
                return (EObject) cached.getFirst();
            }
        }
        return this.bidtSelector.findDatatype(name);
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype(final URI uri) throws CoreException {
        if ( !initialized ) {
            initialize();
        }
        if ( uri != null ) {
            final LinkedList cached = (LinkedList) this.datatypeByName.get(uri.toString());
            if ( cached != null && cached.size() != 0 ) {
                return (EObject) cached.getFirst();
            }
        }
        return this.bidtSelector.findDatatype(uri);
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes(final String name) throws CoreException {
        if ( !initialized ) {
            initialize();
        }
        final List result = new LinkedList();
        if ( name != null ) {
            final LinkedList cached = (LinkedList) this.datatypeByName.get(name);
            result.addAll(cached);
        }
        final List bitypes = this.bidtSelector.findAllDatatypes(name);
        if ( bitypes != null && bitypes.size() != 0 ) {
            result.addAll(bitypes);
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes(final URI uri) throws CoreException {
        if ( !initialized ) {
            initialize();
        }
        final List result = new LinkedList();
        if ( uri != null ) {
            final LinkedList cached = (LinkedList) this.datatypeByName.get(uri.toString());
            result.addAll(cached);
        }
        final List bitypes = this.bidtSelector.findAllDatatypes(uri);
        if ( bitypes != null && bitypes.size() != 0 ) {
            result.addAll(bitypes);
        }
        return result;
    }
    
    protected synchronized void initialize() throws CoreException {
        // Get all of the global SDTs ...
        final List roots = this.datatypeModelSelector.getRootObjects();
        final Iterator iter = roots.iterator();
        while (iter.hasNext()) {
            final EObject root = (EObject)iter.next();
            if ( root instanceof XSDSchema ) {
                final XSDSchema schema = (XSDSchema) root;
                final List typeDefns = schema.getTypeDefinitions();
                final Iterator typeIter = typeDefns.iterator();
                while (typeIter.hasNext()) {
                    final XSDTypeDefinition typeDefn = (XSDTypeDefinition)typeIter.next();
                    if ( typeDefn instanceof XSDSimpleTypeDefinition ) {
                        final XSDSimpleTypeDefinition sdt = (XSDSimpleTypeDefinition) typeDefn;
                        // Register the global SDT ...
                        final String name = sdt.getName();
                        if ( name != null && name.trim().length() != 0 ) {
                            final List sdts = findListInMap(this.datatypeByName,name);
                            sdts.add(sdt);
                        }
                        final URI uri = EcoreUtil.getURI(sdt);
                        if ( uri != null ) {
                            final String uriString = uri.toString();
                            final List sdts = findListInMap(this.datatypeByName,uriString);
                            sdts.add(sdt);
                        }
                    }
                    
                }
            }
        }
    }
    
    protected LinkedList findListInMap( final Map map, final Object key ) {
        final Object value = map.get(key);
        if ( value == null ) {
            final LinkedList result = new LinkedList();
            map.put(key,result);
            return result;
        }
        if ( value instanceof List ) {
            return (LinkedList)value;
        }
        final LinkedList result = new LinkedList();
        result.add(value);
        map.put(key,result);
        return result;
    }

}
