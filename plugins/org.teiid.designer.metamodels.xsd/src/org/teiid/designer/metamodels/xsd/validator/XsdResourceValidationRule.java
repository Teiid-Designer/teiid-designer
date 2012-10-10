/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xsd.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.common.xsd.XsdHeader;
import org.teiid.designer.common.xsd.XsdHeaderReader;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ResourceValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.xsd.XsdPlugin;


/**
 * @since 8.0
 */
public class XsdResourceValidationRule implements ResourceValidationRule {

    /**
     * @see org.teiid.designer.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource,
     *      org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate( final Resource resource,
                          final ValidationContext context ) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(context);

        if (!(resource instanceof XSDResourceImpl)) {
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
                            declarations.addAll(Arrays.asList(header.getImportSchemaLocations()));
                            declarations.addAll(Arrays.asList(header.getIncludeSchemaLocations()));

                            for (Iterator i = declarations.iterator(); i.hasNext();) {
                                String location = (String)i.next();
                                if (CoreStringUtil.isEmpty(location) || location.startsWith("http")) { //$NON-NLS-1$
                                    continue;
                                }
                                URI baseUri = URI.createFileURI(f.getAbsolutePath());
                                URI locationUri = URI.createURI(location);
                                if (baseUri.isHierarchical() && !baseUri.isRelative() && locationUri.isRelative()) {
                                    locationUri = locationUri.resolve(baseUri);
                                }
                                String uriString = (locationUri.isFile() ? locationUri.toFileString() : URI.decode(locationUri.toString()));
                                File importFile = new File(uriString);
                                if (!importFile.exists()) {
                                    final String msg = XsdPlugin.Util.getString("XsdResourceValidationRule.Schema_directive_resolves_to_nonexistent_file", location); //$NON-NLS-1$
                                    final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
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
