/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.builder.ResourceValidator;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * XsdResourceValidator
 */
public class XsdResourceValidator implements ResourceValidator {

    // ===========================================================================================================================
    // Variables

    private int prefStatus = IStatus.WARNING;
    private Set unloaded, xsds;
    private Set allResources, modifiedResources;

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================
    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isValidatorForObject( final Object obj ) {
        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;
            if (ModelUtil.isXsdFile(iResource)) {
                return true;
            }

        } else if (obj instanceof Resource) {
            final Resource eResource = (Resource)obj;
            if (ModelUtil.isXsdFile(eResource)) {
                return true;
            }

        } else if (obj instanceof File) {
            final File f = (File)obj;
            if (f.exists() && ModelFileUtil.isXsdFile(f)) {
                return true;
            }

        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.core.runtime.IProgressMonitor,
     *      java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate( final IProgressMonitor monitor,
                          final Object obj,
                          final ValidationContext context ) throws ModelerCoreException {

        if (!isValidatorForObject(obj)) {
            final Object[] params = new Object[] {this.getClass().getName(), (obj != null ? obj.getClass().getName() : null)};
            final String msg = XsdPlugin.Util.getString("XsdResourceValidator.validator_cannot_be_used_to_validate_the_object", params); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }
        final IProgressMonitor progressMonitor = (monitor != null ? monitor : new NullProgressMonitor());

        if (obj instanceof IResource) {
            final IResource iResource = (IResource)obj;

            final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            final ModelResource mResource = workspace.findModelResource(iResource);

            // jh 6/9/2006 Defect 21047 Fixing unreported NPE
            if (mResource != null) {
                final Resource eResource = mResource.getEmfResource();
                this.validate(progressMonitor, eResource, context);
            }

        } else if (obj instanceof Resource) {
            final Resource eResource = (Resource)obj;
            this.validate(progressMonitor, eResource, context);

        } else if (obj instanceof File) {
            try {
                final File f = (File)obj;
                final URI uri = URI.createURI(f.getAbsolutePath());
                final Resource eResource = ModelerCore.getModelContainer().getResource(uri, false);
                this.validate(progressMonitor, eResource, context);
            } catch (Exception err) {
                throw new ModelerCoreException(err);
            }

        }

    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#addMarkers(com.metamatrix.modeler.core.validation.ValidationContext,
     *      org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    public void addMarkers( final ValidationContext context,
                            final IResource iResource ) throws ModelerCoreException {
        if (context != null && context.hasResults()) {
            final List results = context.getValidationResults();
            try {
                for (final Iterator iter = results.iterator(); iter.hasNext();) {
                    final ValidationResult result = (ValidationResult)iter.next();
                    if (result != null && result.hasProblems()) {
                        ValidationProblem[] problems = result.getProblems();
                        String locationPath = result.getLocationPath();
                        String locationUri = result.getLocationUri();
                        String targetUri = result.getTargetUri();
                        for (int probCnt = 0; probCnt < problems.length; probCnt++) {
                            createProblemMarker(locationPath, locationUri, targetUri, problems[probCnt], iResource);
                        }
                        if (result.isFatalResource()) {
                            return;
                        }
                    }
                }
            } catch (CoreException err) {
                throw new ModelerCoreException(err);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#isValidatorForResource(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean isValidatorForResource( final IResource iResource ) {
        if (ModelUtil.isXsdFile(iResource)) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource)
     */
    public void validate( final IProgressMonitor monitor,
                          final Resource resource,
                          final IResource iResource,
                          final ValidationContext context ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(iResource);

        if (!ModelUtil.isXsdFile(iResource)) {
            final String msg = XsdPlugin.Util.getString("XsdResourceValidator.XsdResourceValidator_may_only_be_used_to_validate_XsdResources_1"); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }

        this.validate(monitor, resource, context);
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validationStarted(java.util.Collection)
     * @since 4.3.2
     */
    public void validationStarted( final Collection resources,
                                   final ValidationContext context ) {
        // If the validation preferences is IGNORE then return
        if (!canValidate(context)) {
            return;
        }

        this.unloaded = new HashSet();
        this.xsds = new HashSet();
        allResources = new HashSet();
        modifiedResources = new HashSet();
        final ModelWorkspace workspace = ModelerCore.getModelWorkspace();

        // Load all XSD resources to track down dependencies, remembering which resources started out unloaded
        ResourceSet ctnr = null;
        for (final Iterator iter = resources.iterator(); iter.hasNext();) {
            final Object obj = iter.next();
            Resource emfResrc = null;
            try {
                if (obj instanceof IResource) {
                    final ModelResource model = workspace.findModelResource((IResource)obj);
                    emfResrc = (model != null ? model.getEmfResource() : null);
                } else if (obj instanceof Resource) {
                    emfResrc = (Resource)obj;
                }
                if (!ModelUtil.isXsdFile(emfResrc)) {
                    continue;
                }
                // All XSD resources must be saved prior to validation.
                if (emfResrc instanceof XSDResourceImpl && emfResrc.isModified()) {
                    final String msg = XsdPlugin.Util.getString("XsdResourceValidator.Can_not_perform_validation_on_unsaved_Xsd_Resources.__Please_save_and_revalidate_1"); //$NON-NLS-1$
                    // Add a marker to the schema and return. Don't try to perform validation as it will
                    // reload the schema causing any unsaved changes to be lost.
                    this.addProblem(((XSDResourceImpl)emfResrc).getSchema(), 0, IStatus.ERROR, msg, context);
                    return;
                }
                // Add resource to list of XSD resources to unload/reload. Continue working with resource only if it wasn't
                // already in the list.
                if (this.xsds.add(emfResrc)) {
                    allResources.add(emfResrc);
                    // Determine container if not already known
                    if (ctnr == null) {
                        ctnr = emfResrc.getResourceSet();
                        if (ctnr == null) {
                            ctnr = ModelerCore.getModelContainer();
                        }
                        if (ctnr != null) {
                            // Record which resources started out as unloaded
                            for (Iterator iter2 = ctnr.getResources().iterator(); iter2.hasNext();) {
                                Resource r = (Resource)iter2.next();
                                if (!r.isLoaded()) {
                                    this.unloaded.add(r);
                                }
                                allResources.add(r);
                                if (r.isModified()) {
                                    modifiedResources.add(r);
                                }
                            }
                        }
                    }
                    // Load resource if unloaded
                    if (!emfResrc.isLoaded()) {
                        emfResrc.load(null);
                        // Remember resource started out unloaded
                        // this.unloaded.add(emfResrc);
                    }
                    // Load referenced XSD resources
                    loadReferencedXsdResources(emfResrc, this.xsds, ctnr);
                }
            } catch (final Exception err) {
                XsdPlugin.Util.log(err);
            }
        }
        // Unload all XSD resources
        for (final Iterator iter = this.xsds.iterator(); iter.hasNext();) {
            ((Resource)iter.next()).unload();
        }
        // Load all XSD resources
        for (final Iterator iter = this.xsds.iterator(); iter.hasNext();) {
            try {
                ((Resource)iter.next()).load(null);
            } catch (final IOException err) {
                XsdPlugin.Util.log(err);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validationEnded()
     * @since 4.3.2
     */
    public void validationEnded( final ValidationContext context ) {
        // If the validation preferences is IGNORE then return
        if (!canValidate(context)) {
            return;
        }
        // Unload resources that started out unloaded before validation.
        for (final Iterator iter = this.unloaded.iterator(); iter.hasNext();) {
            ((Resource)iter.next()).unload();
        }
        this.unloaded = this.xsds = null;
        // reset modified status
        Iterator iter = allResources.iterator();
        while (iter.hasNext()) {
            Resource resource = (Resource)iter.next();
            if (modifiedResources.contains(resource)) {
                resource.setModified(true);
            } else {
                resource.setModified(false);
            }
        }
        modifiedResources = allResources = null;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private void loadReferencedXsdResources( Resource resource,
                                             final Set resources,
                                             final ResourceSet container ) throws Exception {

        // Get a ResourceFinder to use when resolving dependent resource references
        CoreArgCheck.isNotNull(ModelerCore.getContainer(resource));
        ResourceFinder finder = ModelerCore.getContainer(resource).getResourceFinder();

        if (!resource.isLoaded()) {
            ResourceSet ctnr = resource.getResourceSet();
            if (ctnr == null) {
                ctnr = ModelerCore.getModelContainer();
            }
            final Map options = (ctnr == null ? null : ctnr.getLoadOptions());
            resource.load(options);
        }

        XSDSchema schema = ((XSDResourceImpl)resource).getSchema();

        for (final Iterator contents = schema.getContents().iterator(); contents.hasNext();) {
            final Object obj = contents.next();
            if (obj instanceof XSDSchemaDirective) {
                resource = finder.findByImport((XSDSchemaDirective)obj, false);
                if (resource != null && resource.getResourceSet() == container) {
                    // Load referenced XSD resources
                    if (resources.add(resource)) {
                        // Recurse to get the referenced resource
                        loadReferencedXsdResources(resource, resources, container);
                    }
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.builder.ResourceValidator#validate(org.eclipse.emf.ecore.resource.Resource)
     */
    private void validate( final IProgressMonitor monitor,
                           final Resource resource,
                           final ValidationContext context ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(resource);

        // If not already done via multiple resources being validated simultaneously,
        // determine all XSD's involved, and unload & reload them.
        final boolean reload = (this.xsds == null);
        if (reload) {
            final HashSet xsds = new HashSet(1);
            xsds.add(resource);
            validationStarted(xsds, context);
        }
        // If the validation preferences is IGNORE then return
        if (!canValidate(context)) {
            return;
        }

        try {
            if (!(resource instanceof XSDResourceImpl)) {
                final String msg = XsdPlugin.Util.getString("XsdResourceValidator.XsdResource_validator_may_only_be_used_to_validate_instances_of_XsdResourceImpl_1"); //$NON-NLS-1$
                throw new ModelerCoreException(msg);
            }
            final XSDResourceImpl xsdResource = (XSDResourceImpl)resource;

            // Get the list of all XSD resources in the workspace ...
            final ResourceSet resourceSet = xsdResource.getResourceSet();
            List xsdResources = getXsdResources(resourceSet);

            // Store the current modified state of those XSD resources so that their state
            // can be reset to the correct value after unloading and then reloading the
            // resource being validated. This is necessary since reloading an XSD
            // that contains an XSDImport the imported resource will get marked as modified
            // even if it was originally unmodified.
            final Map xsdResourceModifiedState = new HashMap();
            for (Iterator iter = xsdResources.iterator(); iter.hasNext();) {
                final Resource r = (Resource)iter.next();
                final String rUri = r.getURI().toString();
                if (r.isModified()) {
                    xsdResourceModifiedState.put(rUri, Boolean.TRUE);
                } else {
                    xsdResourceModifiedState.put(rUri, Boolean.FALSE);
                }
            }

            final XSDSchema schema = xsdResource.getSchema();
            if (schema == null) {
                final String msg = XsdPlugin.Util.getString("XsdResourceValidator.Error_processing_XSD_file_during_validation_1"); //$NON-NLS-1$
                throw new ModelerCoreException(msg);
            }

            // Validate the schema, generating all new diagnostics
            if (monitor != null && monitor.isCanceled()) {
                return;
            }
            schema.validate();

            final List diagnostics = schema.getAllDiagnostics();
            for (final Iterator iter = diagnostics.iterator(); iter.hasNext();) {
                final XSDDiagnostic next = (XSDDiagnostic)iter.next();
                this.addProblem(next, context);
            }

            xsdResource.setModified(false);

            // Get the list of all XSD resources in the workspace ...
            xsdResources = getXsdResources(resourceSet);

            // Reset the modified state of the XSD resource to what it was prior
            // to unloading and then reloading the resource being validated
            for (Iterator iter = xsdResources.iterator(); iter.hasNext();) {
                final Resource r = (Resource)iter.next();
                final String rUri = r.getURI().toString();
                r.setModified(false);
                if (xsdResourceModifiedState.containsKey(rUri)) {
                    Boolean modifiedState = (Boolean)xsdResourceModifiedState.get(rUri);
                    if (modifiedState == Boolean.TRUE) {
                        r.setModified(true);
                    }
                }
            }
        } finally {
            if (reload) {
                validationEnded(context);
            }
        }
    }

    private boolean canValidate( final ValidationContext context ) {
        // See what the preference is ...
        this.prefStatus = context.getPreferenceStatus(ValidationPreferences.XSD_MODEL_VALIDATION, IStatus.WARNING);

        // If the validation preference is IGNORE an IStatus.OK is returned
        if (this.prefStatus == IStatus.OK) {
            return false;
        }
        return true;
    }

    private static List getXsdResources( final ResourceSet resourceSet ) {
        final List result = new ArrayList();
        if (resourceSet != null) {
            for (Iterator iter = resourceSet.getResources().iterator(); iter.hasNext();) {
                final Resource resource = (Resource)iter.next();
                if (resource instanceof XSDResourceImpl) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    private void addProblem( final Object object,
                             final int code,
                             final int severity,
                             final String msg,
                             final ValidationContext context ) {
        ValidationProblem problem = new ValidationProblemImpl(code, severity, msg);
        ValidationResult result = new ValidationResultImpl(object);
        result.addProblem(problem);
        context.addResult(result);
    }

    private void addProblem( final XSDDiagnostic diagnostic,
                             final ValidationContext context ) {
        final EObject target = diagnostic.getPrimaryComponent();
        if (target != null) {
            int severity = Math.min(getStatusSeverity(diagnostic), this.prefStatus);
            this.addProblem(target, 0, severity, diagnostic.getMessage(), context);
        }
    }

    /**
     * Create a marker given a validationProblem
     */
    private void createProblemMarker( final String locationPath,
                                      final String locationUri,
                                      final String targetUri,
                                      final ValidationProblem problem,
                                      final IResource resource ) throws CoreException {

        IMarker marker = resource.createMarker(IMarker.PROBLEM);
        marker.setAttribute(IMarker.LOCATION, locationPath);
        marker.setAttribute(ModelerCore.MARKER_URI_PROPERTY, locationUri);
        marker.setAttribute(ModelerCore.TARGET_MARKER_URI_PROPERTY, targetUri);
        marker.setAttribute(IMarker.MESSAGE, problem.getMessage());

        setMarkerSeverity(marker, problem.getSeverity());
    }

    /**
     * Get the severity given the XSDDiagnostic.
     */
    private int getStatusSeverity( final XSDDiagnostic diagnostic ) {
        final int severity = diagnostic.getSeverity().getValue();
        switch (severity) {
            case XSDDiagnosticSeverity.ERROR:
                return IStatus.ERROR;
            case XSDDiagnosticSeverity.WARNING:
                return IStatus.WARNING;
            case XSDDiagnosticSeverity.INFORMATION:
                return IStatus.INFO;
            default:
                return IStatus.OK;
        }
    }

    /**
     * Sets the severity on the specified marker using the specified validation problem severity and the current user preference
     * setting.
     * 
     * @param marker the marker
     * @param theSeverity the {@link ValidationProblem} severity
     */
    private void setMarkerSeverity( final IMarker marker,
                                    int theSeverity ) throws CoreException {
        // adjust severity if necessary based on what the validate schema user preference is set to
        if (theSeverity > this.prefStatus) {
            do {
                --theSeverity;
            } while (theSeverity > this.prefStatus);
        }

        switch (theSeverity) {
            case IStatus.ERROR:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                break;
            case IStatus.WARNING:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                break;
            case IStatus.INFO:
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                break;
            default:
                return;
        }
    }

}
