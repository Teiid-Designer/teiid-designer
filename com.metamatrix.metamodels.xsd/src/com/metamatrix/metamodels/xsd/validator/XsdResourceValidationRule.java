/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.xsd.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.xsd.XsdHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeaderReader;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 5.1
 */
public class XsdResourceValidationRule implements ResourceValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final Resource resource, final ValidationContext context) {
	    ArgCheck.isNotNull(resource);
	    ArgCheck.isNotNull(context);

	    if( !(resource instanceof XSDResourceImpl) ) {
	        return;
	    }
        
        // Get the schema object to create the markers on
        XSDSchema schema = ((XSDResourceImpl)resource).getSchema();
        if (schema != null) {
            final ValidationResult result = new ValidationResultImpl(schema);
            
            // Attempt to read the "schemaLocation" values from the XSDSchemaDirective declarations in the file
            final URI uri = resource.getURI();
            if (uri.isFile()) {
                final File f = new File(uri.toFileString());
                if (f.exists()) {
                    try {
                        final XsdHeader header = XsdHeaderReader.readHeader(f);
                        final List declarations = new ArrayList();
                        if (header != null) {
                            declarations.addAll( Arrays.asList(header.getImportSchemaLocations()) );
                            declarations.addAll( Arrays.asList(header.getIncludeSchemaLocations()) );
                            
                            for (Iterator i = declarations.iterator(); i.hasNext();) {
                                String location = (String)i.next();
                                if (StringUtil.isEmpty(location) || location.startsWith("http")) { //$NON-NLS-1$
                                    continue;
                                }
                                URI baseUri     = URI.createFileURI(f.getAbsolutePath());
                                URI locationUri = URI.createURI(location);
                                if (baseUri.isHierarchical() && !baseUri.isRelative() && locationUri.isRelative()) {
                                    locationUri = locationUri.resolve(baseUri);
                                }
                                String uriString = (locationUri.isFile() ? locationUri.toFileString() : URI.decode(locationUri.toString()) );
                                File importFile  = new File(uriString);
                                if (!importFile.exists()) {
                                    final String msg = XsdPlugin.Util.getString("XsdResourceValidationRule.Schema_directive_resolves_to_nonexistent_file",location); //$NON-NLS-1$
                                    final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                                    result.addProblem(problem);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        ModelerCore.Util.log(e);
                    }
                }
            }
            if (result.hasProblems()) {
                context.addResult(result);
            }
        }
        
    }

}
