/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder;

/**
 * FakeDatatypeFinder
 */
public class FakeDatatypeFinder implements DatatypeFinder {

    public Map dataTypeToSimpleDatatype;

    /**
     * Construct an instance of FakeDatatypeFinder.
     */
    public FakeDatatypeFinder() {
        dataTypeToSimpleDatatype = new HashMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findDatatype(java.lang.String)
     */
    public EObject findDatatype( String name ) {
        return getDatatypeForString(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findDatatype(org.eclipse.emf.common.util.URI)
     */
    public EObject findDatatype( URI uri ) {

        return getDatatypeForString(uri.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findAllDatatypes(java.lang.String)
     */
    public List findAllDatatypes( String name ) {
        return getDatatypesForString(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder#findAllDatatypes(org.eclipse.emf.common.util.URI)
     */
    public List findAllDatatypes( URI uri ) {
        return getDatatypesForString(uri.toString());
    }

    public void addMapping( String dataTypeName,
                            EObject simpleDatatype ) {
        List simpleDatatypes = (List)dataTypeToSimpleDatatype.get(dataTypeName);
        if (null == simpleDatatypes) {
            simpleDatatypes = new LinkedList();
            simpleDatatypes.add(simpleDatatype);
            dataTypeToSimpleDatatype.put(dataTypeName, simpleDatatypes);
        } else {
            simpleDatatypes.add(simpleDatatype);
        }

    }

    private EObject getDatatypeForString( String string ) {
        List simpleDatatypes = (List)dataTypeToSimpleDatatype.get(string);
        if (null == simpleDatatypes || simpleDatatypes.size() == 0) {
            return null;
        }
        return (EObject)simpleDatatypes.get(0);
    }

    private List getDatatypesForString( String string ) {
        List simpleDatatypes = (List)dataTypeToSimpleDatatype.get(string);
        if (null == simpleDatatypes || simpleDatatypes.size() == 0) {
            return null;
        }
        return simpleDatatypes;
    }
}
