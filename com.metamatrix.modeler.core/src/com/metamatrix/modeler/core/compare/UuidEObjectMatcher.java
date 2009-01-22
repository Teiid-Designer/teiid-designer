/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * UuidEObjectMatcher
 */
public class UuidEObjectMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UuidEObjectMatcher.
     */
    public UuidEObjectMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List,
     *      org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots( final List inputs,
                                     final List outputs,
                                     final Mapping mapping,
                                     final MappingFactory factory ) {
        // do nothing for roots ...
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List,
     *      java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference,
                             final List inputs,
                             final List outputs,
                             final Mapping mapping,
                             final MappingFactory factory ) {
        // Loop over the inputs and accumulate the UUIDs ...
        final Map idToInput = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            final Object id = getObjectId(obj);
            if (id != null) {
                idToInput.put(id, obj);
            }
        }

        // Loop over the outputs and compare the UUIDs ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            final Object id = getObjectId(output);

            final EObject input = (EObject)idToInput.get(id);
            if (input != null) {
                /*
                 * If either object is a proxy we have previously retrieved its
                 *  UUID [see getObjectId( EObject )] and made sure that both objects
                 *  had the same UUID. Since they have the same UUID, we assume they are
                 *  the same object and the proxied object lives externally to the models
                 *  we are differencing. We will skip the 'Metaclass.equals(Metaclass)' test
                 *  because it would require us to load the model the proxy points to, which
                 *  would be expensive.
                 */
                if (input.eIsProxy() || output.eIsProxy()) {
                    inputs.remove(input);
                    outputIter.remove();
                    addMapping(input, output, mapping, factory);

                } else {
                    final EClass inputMetaclass = input.eClass();
                    final EClass outputMetaclass = output.eClass();
                    if (inputMetaclass.equals(outputMetaclass)) {
                        inputs.remove(input);
                        outputIter.remove();
                        addMapping(input, output, mapping, factory);
                    }
                }
            }
        }
    }

    /*
     * getObjectId  returns the object id of an EObject whether it is a proxy
     * or a real EObject.
     */
    private ObjectID getObjectId( EObject eo ) {

        ObjectID uuid = null;

        if (eo == null) {
            return uuid;
        }

        if (eo.eIsProxy()) {
            if (eo instanceof EObjectImpl) {
                URI uri = ((EObjectImpl)eo).eProxyURI();
                String sUUIDFrag = uri.fragment();

                uuid = getObjectIDFromString(sUUIDFrag);
            }
        } else {
            uuid = ModelerCore.getObjectId(eo);
        }
        return uuid;
    }

    private ObjectID getObjectIDFromString( final String uuidString ) {
        if (uuidString == null || uuidString.length() == 0) {
            return null;
        }
        try {
            return IDGenerator.getInstance().stringToObject(uuidString);
        } catch (InvalidIDException e) {
            // do nothing ...
        }
        return null;
    }

}
