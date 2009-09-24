/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.util;

import java.util.List;
import java.util.Set;

import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;

/**
 * RelationalObjectGenerator
 */
public interface RelationalObjectGenerator {

    public List createBaseTablesForClass(
        final Classifier klass,
        final List problems,
        final Set associationsToBeProcessed);

    public List createBaseTablesForAssociation(final Association association, final List problems);

}
