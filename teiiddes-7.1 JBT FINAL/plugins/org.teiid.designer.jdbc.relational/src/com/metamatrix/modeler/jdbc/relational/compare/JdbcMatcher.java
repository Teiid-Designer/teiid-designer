/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.jdbc.JdbcImportOptions;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * UuidEObjectMatcher
 */
public class JdbcMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UuidEObjectMatcher.
     */
    public JdbcMatcher() {
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
        JdbcSource inputSource = null;

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof JdbcSource) {
                inputSource = (JdbcSource)obj;
            }
        }

        if (inputSource == null) {
            return;
        }

        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Object obj = outputIter.next();
            if (obj instanceof JdbcSource) {
                outputIter.remove();
                inputs.remove(inputSource);
                addMapping(inputSource, (EObject)obj, mapping, factory);
            }
        }
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
        JdbcImportSettings inputSettings = null;
        final Map inputOptionByName = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof JdbcImportSettings) {
                inputSettings = (JdbcImportSettings)obj;
            } else if (obj instanceof JdbcImportOptions) {
                final JdbcImportOptions option = (JdbcImportOptions)obj;
                final String name = option.getName();
                inputOptionByName.put(name, option);
            }
        }

        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Object obj = outputIter.next();
            if (obj instanceof JdbcImportSettings) {
                outputIter.remove();
                inputs.remove(inputSettings);
                addMapping(inputSettings, (EObject)obj, mapping, factory);
            } else if (obj instanceof JdbcImportOptions) {
                final JdbcImportOptions option = (JdbcImportOptions)obj;
                final String name = option.getName();
                final JdbcImportOptions inputOption = (JdbcImportOptions)inputOptionByName.get(name);
                if (inputOption != null) {
                    outputIter.remove();
                    inputs.remove(inputOption);
                    addMapping(inputOption, (EObject)obj, mapping, factory);
                }
            }
        }
    }

}
