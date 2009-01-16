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
package com.metamatrix.modeler.core.refactor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Element;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.util.ConcurrentModelVisitorProcessor;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelCopyCommand is a RefactorCommand implementation that can generate a new copy of a model in memory. The command assumes
 * that there may be existing changes to the
 */
public class ModelCopyCommand implements RefactorCommand {

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    public static final int ERROR_MISSING_RESOURCE = 1800;

    /** IStatus code indicating that no destination has been set */
    public static final int ERROR_MISSING_DESTINATION = 1801;

    /** IStatus code indicating that no new model name has been set */
    public static final int ERROR_MISSING_NAME = 1802;

    /** IStatus code indicating that the destination is not in a model project */
    public static final int ERROR_PROJECT_CLOSED = 1805;

    /** IStatus code indicating that the destination is not in a model project */
    public static final int ERROR_PROJECT_NATURE = 1806;

    /** IStatus code indicating that an exception occurred obtaining the destination project nature */
    public static final int EXCEPTION_PROJECT_NATURE = 1807;

    /** IStatus code indicating that a file already exists with the specified new path */
    public static final int ERROR_FILE_ALREADY_EXISTS = 1808;

    /** IStatus code indicating that a file already exists with the specified new path */
    public static final int ERROR_CREATING_FILE = 1809;

    /** IStatus code indicating that the command is ready to execute */
    public static final int READY_TO_EXECUTE = 1810;

    /** IStatus code indicating that the an error occurred in copying the resource contents */
    public static final int ERROR_COPYING_RESOURCE = 1811;

    /** IStatus codes for the result of the execute operation */
    public static final int EXECUTE_WITH_NO_PROBLEMS = 1812;
    public static final int EXECUTE_WITH_WARNINGS = 1813;
    public static final int EXECUTE_WITH_ERRORS = 1814;
    public static final int EXECUTE_WITH_WARNINGS_AND_ERRORS = 1815;
    public static final int EXECUTE_WITH_NO_WARNINGS_AND_ERRORS = 1816;

    static final String PID = ModelerCore.PLUGIN_ID;

    // The name of the attribute in the XSDSimpleTypeDefinition application information
    private static final String UUID_ATTRIBUTE_NAME = "UUID"; //$NON-NLS-1$

    private static Collection REFERENCE_UPDATORS;

    private ModelResource resourceToCopy;
    private IContainer destination;
    private String newModelName;
    private String extension;
    private IFile newIFile;
    private ArrayList problemList = new ArrayList();
    private Collection referencingResources;

    /**
     * Construct an instance of ModelCopyCommand.
     */
    public ModelCopyCommand() {
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    public void setModelToCopy( final ModelResource modelResource ) {
        this.resourceToCopy = modelResource;
        this.extension = modelResource.getResource().getFileExtension();
    }

    public void setNewModelDestination( final IContainer destination,
                                        final String name ) {
        this.newModelName = name;
        this.destination = destination;
    }

    /**
     * Sets a list of models that import the model being copied, such that all object references to the original model should be
     * redirected to the corresponding object in the copy.
     * 
     * @param modelResourceList a collection of ModelResource objects.
     * @since 4.2
     */
    public void setModelsToRedirect( final Collection modelResourceList ) {
        this.referencingResources = modelResourceList;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canExecute()
     */
    public IStatus canExecute() {
        if (this.newModelName.length() == 0) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.No_new_model_name"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_MISSING_NAME, msg, null);
        }

        if (this.resourceToCopy == null) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.No_resource_has_been_selected"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_MISSING_RESOURCE, msg, null);
        }

        if (this.destination == null) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.No_destination_has_been_selected"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_READONLY_RESOURCE, msg, null);
        }

        if (!this.destination.getProject().isOpen()) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_project_closed"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_PROJECT_CLOSED, msg, null);
        }

        // can't save into non-model projects
        try {
            if (this.destination.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Cannot_move_to_non_model_project"); //$NON-NLS-1$
                return new Status(IStatus.ERROR, PID, ERROR_PROJECT_NATURE, msg, null);
            }
        } catch (CoreException e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Cannot_determine_project_nature"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, EXCEPTION_PROJECT_NATURE, msg, e);
        }

        // check the validity of the name
        final ValidationResultImpl result = new ValidationResultImpl(this.newModelName);
        CoreValidationRulesUtil.validateStringNameChars(result, this.newModelName, null);
        if (result.hasProblems()) {
            ValidationProblem problem = result.getProblems()[0];
            return new Status(problem.getSeverity(), PID, problem.getCode(), problem.getMessage(), null);
        }

        // check for siblings
        if (fileAlreadyExists(this.newModelName)) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Name_already_exists_in_container"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_FILE_ALREADY_EXISTS, msg, null);
        }

        final String msg = ModelerCore.Util.getString("ModelCopyCommand.Ready_to_execute"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus execute( final IProgressMonitor monitor ) {
        this.problemList.clear();

        // create the new file
        this.newIFile = this.destination.getFile(new Path(this.newModelName + '.' + this.extension));

        // Create copy of an XML schema model file ...
        if (this.isXsdResource(this.newIFile)) {
            executeXsdCopy(monitor);
        }
        // Create copy of an MetaMatrix xmi model file ...
        else {
            executeXmiCopy(monitor);
        }

        // Put all of the problems into a single IStatus ...
        IStatus resultStatus = null;
        if (problemList.isEmpty()) {
            final int code = EXECUTE_WITH_NO_PROBLEMS;
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Model_copy_complete_1"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, PID, code, msg, null);
            resultStatus = status;
        } else if (problemList.size() == 1) {
            resultStatus = (IStatus)problemList.get(0);
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            for (final Iterator problemIter = problemList.iterator(); problemIter.hasNext();) {
                final IStatus aStatus = (IStatus)problemIter.next();
                if (aStatus.getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (aStatus.getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            final IStatus[] statusArray = (IStatus[])problemList.toArray(new IStatus[problemList.size()]);
            if (numWarnings != 0 && numErrors == 0) {
                final int code = EXECUTE_WITH_WARNINGS;
                final Object[] params = new Object[] {new Integer(numWarnings)};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Model_copy_with_warnings_8", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, code, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                final int code = EXECUTE_WITH_ERRORS;
                final Object[] params = new Object[] {new Integer(numErrors)};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Model_copy_with_errors_9", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, code, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                final int code = EXECUTE_WITH_WARNINGS_AND_ERRORS;
                final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Model_copy_with_warnings_and_errors_10", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, code, statusArray, msg, null);
            } else {
                final int code = EXECUTE_WITH_NO_WARNINGS_AND_ERRORS;
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Model_copy_with_no_warnings_or_errors_11"); //$NON-NLS-1$
                resultStatus = new MultiStatus(PID, code, statusArray, msg, null);
            }
        }

        return resultStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canUndo()
     */
    public boolean canUndo() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canRedo()
     */
    public boolean canRedo() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#undo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus undo( final IProgressMonitor monitor ) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#redo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus redo( final IProgressMonitor monitor ) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getResult()
     */
    public Collection getResult() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getAffectedObjects()
     */
    public Collection getAffectedObjects() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getLabel()
     */
    public String getLabel() {
        return ModelerCore.Util.getString("ModelCopyCommand.label"); //$NON-NLS-1$;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getDescription()
     */
    public String getDescription() {
        return ModelerCore.Util.getString("ModelCopyCommand.description"); //$NON-NLS-1$;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getPostExecuteMessages()
     */
    public Collection getPostExecuteMessages() {
        return problemList;
    }

    /**
     * @return
     */
    public IFile getNewIFile() {
        return newIFile;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Create a copy of the Xsd resource. Different logic is used to copy an XSD resource because XSD models don't have all the
     * right EStructuralFeatures set to be derived to avoid generically copying things that should not be copied because they are
     * computed. An alternative would be to use cloneConcreteComponent to do the copy but since we want a copy of the DOM itself
     * we instead chose to use Resource.save with an output stream for to the new resource location. That new resource is then
     * loaded and the UUIDs to the simple datatypes are recreated.
     * 
     * @param monitor
     */
    protected void executeXsdCopy( final IProgressMonitor monitor ) {

        // Copy the contents from the original resource into the new resource
        Resource source = null;
        try {
            // get the Emf resource for the original
            source = this.resourceToCopy.getEmfResource();
            if (source == null) {
                final Object[] params = new Object[] {this.resourceToCopy};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_for_2", params); //$NON-NLS-1$
                this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
                return;
            }
        } catch (Throwable e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_references_4"); //$NON-NLS-1$
            this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // since we are processing an XSD resource then set the incremental build state
        // to false to improve performance
        final boolean incrementalBuildState = this.getXsdIncrementalBuild(source);
        this.setXsdIncrementalBuild(source, false);

        // get the modified state of the resource before copying it
        final boolean modifiedState = source.isModified();

        // create an output stream and write the resource to a new file ...
        final IPath newFilePath = this.newIFile.getLocation();
        final File newFile = newFilePath.toFile();
        OutputStream fos = null;
        OutputStream bos = null;
        try {
            fos = new FileOutputStream(newFile);
            bos = new BufferedOutputStream(fos);
            source.save(bos, Collections.EMPTY_MAP);
        } catch (Throwable t) {
            final Object[] params = new Object[] {newFile};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Exception_saving_XSD_resource_to_0_1", params); //$NON-NLS-1$
            this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, t));
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
        }

        // create the ModelResource for the new file
        final ModelResource modelResource = ModelerCore.create(this.newIFile);
        if (modelResource == null) {
            final Object[] params = new Object[] {this.newModelName};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_creating_ModelResource_for_1", params); //$NON-NLS-1$
            this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
            return;
        }

        Resource target = null;
        try {
            // get the Emf resource for the destination
            target = modelResource.getEmfResource();
            if (target == null) {
                final Object[] params = new Object[] {modelResource};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_for_3", params); //$NON-NLS-1$
                this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
                return;
            }

            // Create new UUIDs for all XSDSimpleTypeDefinition entities in the model copy
            resetXsdSimpleTypeUuids(target);

            // Resave the EMF resource
            target.save(Collections.EMPTY_MAP);
            target.setModified(false);

        } catch (Throwable e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_references_4"); //$NON-NLS-1$
            this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // Save the resulting target resource
        try {
            modelResource.save(monitor, true);
        } catch (Throwable e) {
            final Object[] params = new Object[] {modelResource};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_saving_new_model_resource_7", params); //$NON-NLS-1$
            this.problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // reset the incremental build state to what it was prior to the copy operation
        this.setXsdIncrementalBuild(source, incrementalBuildState);

        // reset the modified state of the resource to what is was prior to the copy operation
        source.setModified(modifiedState);

        // collect the originals to copied map
        Map originalsToCopies = getOriginalToCopiesMap(source, target);
        // Process the referencing resources and save ...
        updateReferencingResources(monitor, originalsToCopies, modelResource);
    }

    protected Map getOriginalToCopiesMap( final Resource source,
                                          final Resource target ) {
        Map originalToCopied = new HashMap();

        Map uriFragmentToObject = new HashMap();

        for (final Iterator iter = source.getAllContents(); iter.hasNext();) {
            EObject originalObj = (EObject)iter.next();
            String uri = EcoreUtil.getURI(originalObj).toString();
            // get the uri fragment from the uri
            int beginIndex = uri.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) + 1;
            String uriFragment = (beginIndex > 0 ? uri.substring(beginIndex) : uri);
            if (uriFragment != null) {
                uriFragmentToObject.put(uriFragment.toUpperCase(), originalObj);
            }
        }

        for (final Iterator iter = target.getAllContents(); iter.hasNext();) {
            EObject copiedObj = (EObject)iter.next();
            String uri = EcoreUtil.getURI(copiedObj).toString();
            // get the uri fragment from the uri
            int beginIndex = uri.indexOf(DatatypeConstants.URI_REFERENCE_DELIMITER) + 1;
            String uriFragment = (beginIndex > 0 ? uri.substring(beginIndex) : uri);
            if (uriFragment != null) {
                EObject originalObject = (EObject)uriFragmentToObject.get(uriFragment.toUpperCase());
                if (originalObject != null) {
                    originalToCopied.put(originalObject, copiedObj);
                }
            }
        }

        return originalToCopied;
    }

    /**
     * Create a copy of the Xmi resource
     * 
     * @param monitor
     */
    protected void executeXmiCopy( final IProgressMonitor monitor ) {

        // create the ModelResource for the new file
        final ModelResource modelResource = ModelerCore.create(this.newIFile);
        if (modelResource == null) {
            final Object[] params = new Object[] {this.newModelName};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_creating_ModelResource_for_1", params); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
            return;
        }

        // Copy the contents from the original resource into the new resource
        Resource source = null;
        Resource target = null;
        try {
            // get the Emf resource for the original
            source = resourceToCopy.getEmfResource();
            if (source == null) {
                final Object[] params = new Object[] {resourceToCopy};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_for_2", params); //$NON-NLS-1$
                problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
                return;
            }

            // get the Emf resource for the destination
            target = modelResource.getEmfResource();
            if (target == null) {
                final Object[] params = new Object[] {modelResource};
                final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_for_3", params); //$NON-NLS-1$
                problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, null));
                return;
            }
        } catch (Throwable e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_getting_Emf_resource_references_4"); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // copy the ModelAnnotation information from the source to the target
        final Map originalsToCopies = new HashMap();
        try {
            if (source instanceof EmfResource && target instanceof EmfResource) {
                ModelAnnotation sourceAnnot = ((EmfResource)source).getModelAnnotation();
                ModelAnnotation targetAnnot = ((EmfResource)target).getModelAnnotation();
                // If the ModelAnnotation does not yet exist in the target then simple copy it
                if (targetAnnot == null) {
                    targetAnnot = (ModelAnnotation)this.copyEObject(sourceAnnot);
                    target.getContents().add(targetAnnot);
                } else {
                    // Set the ModelAnnotation properties on the target
                    targetAnnot.setDescription(sourceAnnot.getDescription());
                    targetAnnot.setExtensionPackage(sourceAnnot.getExtensionPackage());
                    targetAnnot.setMaxSetSize(sourceAnnot.getMaxSetSize());
                    targetAnnot.setModelType(sourceAnnot.getModelType());
                    targetAnnot.setNameInSource(sourceAnnot.getNameInSource());
                    targetAnnot.setPrimaryMetamodelUri(sourceAnnot.getPrimaryMetamodelUri());
                    targetAnnot.setSupportsDistinct(sourceAnnot.isSupportsDistinct());
                    targetAnnot.setSupportsJoin(sourceAnnot.isSupportsJoin());
                    targetAnnot.setSupportsOrderBy(sourceAnnot.isSupportsOrderBy());
                    targetAnnot.setSupportsOuterJoin(sourceAnnot.isSupportsOuterJoin());
                    targetAnnot.setSupportsWhereAll(sourceAnnot.isSupportsWhereAll());
                    targetAnnot.setVisible(sourceAnnot.isVisible());

                    // Copy the model imports ...
                    targetAnnot.eContents().clear();
                    for (final Iterator iter = sourceAnnot.eContents().iterator(); iter.hasNext();) {
                        EObject sourceImport = (EObject)iter.next();
                        if (sourceImport != null) {
                            EObject targetImport = this.copyEObject(sourceImport);
                            targetAnnot.getModelImports().add(targetImport);
                            originalsToCopies.put(sourceImport, targetImport);
                        }
                    }
                    originalsToCopies.put(sourceAnnot, targetAnnot);
                }
            }
        } catch (Throwable e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_copying_ModelAnnotation_information_5"); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // Copy the remaining roots from the source to the target
        try {
            final List sourceRootsToCopy = new ArrayList(source.getContents());
            for (final Iterator iter = sourceRootsToCopy.iterator(); iter.hasNext();) {
                EObject sourceRoot = (EObject)iter.next();
                if (sourceRoot instanceof ModelAnnotation) {
                    iter.remove();
                }
            }

            final Collection targetRoots = this.copyEObject(sourceRootsToCopy, originalsToCopies);

            // Process the copied root EObjects ...
            for (final Iterator iter = targetRoots.iterator(); iter.hasNext();) {
                EObject targetRoot = (EObject)iter.next();
                // Check the copied EObjects for references to entities in the original
                // model. If found, reset the reference from the original to the copy.
                ResetReferencesVisitor visitor = new ResetReferencesVisitor(originalsToCopies, false);
                ConcurrentModelVisitorProcessor processor = new ConcurrentModelVisitorProcessor(visitor);
                processor.walk(targetRoot, ModelVisitorProcessor.DEPTH_INFINITE);
                // Add the root to the target resource
                target.getContents().add(targetRoot);
            }
        } catch (Throwable e) {
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_copying_model_roots_6"); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // Save the resulting target resource
        try {
            modelResource.save(monitor, true);
        } catch (Throwable e) {
            final Object[] params = new Object[] {modelResource};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.Error_saving_new_model_resource_7", params); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }

        // Process the referencing resources and save ...
        updateReferencingResources(monitor, originalsToCopies, modelResource);
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Visit the resource or eObject to check if it or any of its children has references to any of eObjects in the original
     * model. If found, reset the reference value from the original to the copy.
     */
    private class ResetReferencesVisitor implements ModelVisitor {

        private final Map originalsToCopies;

        private final Collection updatedExternalObjects = new HashSet();

        private final boolean isExternalResource;

        /**
         * ResetReferencesVisitor
         * 
         * @param originalsToCopies the map of original EObject to copy
         * @since 4.2
         */
        public ResetReferencesVisitor( final Map originalsToCopies,
                                       final boolean isExternalResource ) {
            this.originalsToCopies = originalsToCopies;
            this.isExternalResource = isExternalResource;
        }

        /**
         * @param eObj the EObject whose contents will be checked for references to the original model and updated to new model
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         * @since 4.2
         */
        public boolean visit( final EObject eObj ) {
            // Get all EReference features for this EClass
            final List refs = eObj.eClass().getEAllReferences();
            for (final Iterator iter1 = refs.iterator(); iter1.hasNext();) {
                final EReference eReference = (EReference)iter1.next();
                // since we are going to visit all children
                // need not visit container references
                if (eReference.isContainment() && eReference.isContainer() && eReference.isVolatile()) {
                    continue;
                }
                final Object value = eObj.eGet(eReference);
                if (eReference.isMany()) {
                    // multi-valued feature ...
                    final List values = (List)value;
                    if (!values.isEmpty()) {
                        final Collection newValues = new ArrayList(values.size());
                        boolean resetList = false;
                        // Check each value in the List for references to
                        // EObjects in the original model. If the value
                        // is found in the map then the EObject was copied
                        // so the reference needs to be reset
                        for (final Iterator iter2 = values.iterator(); iter2.hasNext();) {
                            final Object valueInList = iter2.next();
                            if (valueInList != null && valueInList instanceof EObject) {
                                final EObject orig = (EObject)valueInList;
                                final EObject copy = (EObject)originalsToCopies.get(orig);
                                if (copy != null) {
                                    newValues.add(copy);
                                    resetList = true;
                                } else {
                                    // copy does not exist,
                                    // values on the object may need to be updated
                                    updateReference(orig);
                                    newValues.add(orig);
                                }
                            }
                        }
                        if (resetList) {
                            // always fails (see SetCommand.java:340):
                            // eObj.eSet(eReference,newValues);
                            ModelerCore.getModelEditor().setPropertyValue(eObj, newValues, eReference);
                        }
                    }
                } else {
                    // There may be 0..1 value ...
                    if (value != null && value instanceof EObject) {
                        final EObject orig = (EObject)value;
                        final EObject copy = (EObject)originalsToCopies.get(orig);
                        if (copy != null) {
                            eObj.eSet(eReference, copy);
                        } else {
                            // copy does not exist,
                            // values on the object may need to be updated
                            updateReference(orig);
                        }
                    }
                }
            }
            return true;
        }

        private void updateReference( final EObject eObject ) {
            if (isExternalResource && !this.updatedExternalObjects.contains(eObject)) {
                for (final Iterator iter = getReferenceUpdators().iterator(); iter.hasNext();) {
                    ReferenceUpdator updator = (ReferenceUpdator)iter.next();
                    updator.updateEObject(eObject, this.originalsToCopies);
                }
                this.updatedExternalObjects.add(eObject);
            }
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         * @since 4.2
         */
        public boolean visit( final Resource resource ) {
            return (resource != null);
        }
    }

    /**
     * Update references on the resources that reference the original model that is copied.
     * 
     * @since 4.2
     */
    public void updateReferencingResources( final IProgressMonitor monitor,
                                            final Map originalsToCopies,
                                            final ModelResource referencedResource ) {
        if (this.referencingResources == null || this.referencingResources.isEmpty()) {
            return;
        }

        // reset references on external resources
        ResetReferencesVisitor visitor = new ResetReferencesVisitor(originalsToCopies, true);
        // Process the referencing resources and save ...
        try {
            for (final Iterator iter = ModelCopyCommand.this.referencingResources.iterator(); iter.hasNext();) {
                ModelResource referenceResource = (ModelResource)iter.next();
                // process the model resource to reset references
                ConcurrentModelVisitorProcessor processor = new ConcurrentModelVisitorProcessor(visitor);
                processor.walk(referenceResource, ModelVisitorProcessor.DEPTH_INFINITE);
                // Save the resulting resource
                referenceResource.save(monitor, true);
            }
        } catch (Throwable e) {
            final Object[] params = new Object[] {referencedResource};
            final String msg = ModelerCore.Util.getString("ModelCopyCommand.0", params); //$NON-NLS-1$
            problemList.add(new Status(IStatus.ERROR, PID, ERROR_COPYING_RESOURCE, msg, e));
        }
    }

    static Collection getReferenceUpdators() {
        if (REFERENCE_UPDATORS == null) {
            // Find all extensions of the notifiers extension point
            final String id = ModelerCore.EXTENSION_POINT.REFERENCE_UPDATOR.UNIQUE_ID;
            final IExtension[] extensions = PluginUtilities.getExtensions(id);
            // initialize the validators array
            REFERENCE_UPDATORS = new ArrayList(extensions.length);
            for (int i = 0; i < extensions.length; ++i) {
                final IExtension extension = extensions[i];
                final String element = ModelerCore.EXTENSION_POINT.REFERENCE_UPDATOR.ELEMENTS.CLASS;
                final String attribute = ModelerCore.EXTENSION_POINT.REFERENCE_UPDATOR.ATTRIBUTES.NAME;
                try {
                    final Object instance = PluginUtilities.createExecutableExtension(extension, element, attribute);
                    if (instance instanceof ReferenceUpdator) {
                        REFERENCE_UPDATORS.add(instance);
                    } else {
                        final String message = ModelerCore.Util.getString("ModelCopyCommand.0"); //$NON-NLS-1$
                        ModelerCore.Util.log(message);
                    }
                } catch (CoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }

        return REFERENCE_UPDATORS;
    }

    /**
     * Determine the path for the target resource if the name were to change to the proposed name. Takes into account the
     * extension, if a file and the extension exists.
     * 
     * @param proposedName
     * @return
     */
    private String getModifiedPathString( final String proposedName ) {
        final String parentPath = this.destination.getFullPath().toString();
        String newPath = parentPath + '/' + proposedName;
        if (this.extension != null && this.extension.length() > 0) {
            newPath += ('.' + this.extension);
        }
        return newPath;
    }

    /**
     * Determine if the target resource were changed to the proposed name, is there another resource in the same container already
     * named the proposed name.
     * 
     * @param proposedName
     * @return true if the proposed name clashes with a sibling; otherwise, false.
     */
    private boolean fileAlreadyExists( final String proposedName ) {
        final String newPath = getModifiedPathString(proposedName);
        final IWorkspaceRoot workspaceRoot = this.resourceToCopy.getResource().getWorkspace().getRoot();
        return (workspaceRoot.findMember(newPath) != null);
    }

    private EObject copyEObject( final EObject eObject ) {
        // return ModelerCore.getModelEditor().copy(sourceRoot);
        // For some reason the ModelEditor.copy operation is not producing
        // a deep copy of some root entities so use EcoreUtil.copy. We can
        // use EcoreUtil.copy since the command result is not undoable.
        return EcoreUtil.copy(eObject);
    }

    // private Collection copyEObject(final Collection eObjects) {
    // //return ModelerCore.getModelEditor().copy(sourceRoot);
    // // For some reason the ModelEditor.copy operation is not producing
    // // a deep copy of some root entities so use EcoreUtil.copy. We can
    // // use EcoreUtil.copy since the command result is not undoable.
    // return EcoreUtil.copyAll(eObjects);
    // }

    private Collection copyEObject( final List eObjects,
                                    final Map originalsToCopies ) throws Exception {
        return ModelerCore.getModelEditor().copyAll(eObjects, originalsToCopies);
    }

    private boolean isXsdResource( final IFile resource ) {
        return ModelUtil.isXsdFile(resource);
    }

    private boolean getXsdIncrementalBuild( final Resource resource ) {
        if (resource instanceof XSDResourceImpl) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
            final XSDSchema schema = xsdResource.getSchema();
            if (schema != null) {
                return schema.isIncrementalUpdate();
            }
        }
        return false;
    }

    private void resetXsdSimpleTypeUuids( final Resource resource ) throws Exception {
        if (resource instanceof XSDResourceImpl) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
            final XSDSchema schema = xsdResource.getSchema();
            if (schema != null) {
                for (final Iterator iter = schema.getContents().iterator(); iter.hasNext();) {
                    EObject eObj = (EObject)iter.next();

                    // Only process global simple type definitions ...
                    if (eObj instanceof XSDSimpleTypeDefinition) {
                        final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObj;

                        // Get the application information ...
                        final XSDAnnotation annotation = type.getAnnotation();

                        // If no annotation exists then no UUID attribute exists to reset ...
                        if (annotation == null) {
                            continue;
                        }
                        for (final Iterator appInfos = annotation.getApplicationInformation().iterator(); appInfos.hasNext();) {
                            final Element appInfo = (Element)appInfos.next();
                            String uuid = appInfo.getAttribute(UUID_ATTRIBUTE_NAME);
                            if (uuid != null) {
                                uuid = IDGenerator.getInstance().create().toString();
                                appInfo.setAttribute(UUID_ATTRIBUTE_NAME, uuid);
                                uuid = appInfo.getAttribute(UUID_ATTRIBUTE_NAME);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setXsdIncrementalBuild( final Resource resource,
                                         final boolean isIncrementalUpdate ) {
        if (resource instanceof XSDResourceImpl) {
            final XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
            final XSDSchema schema = xsdResource.getSchema();
            if (schema != null) {
                schema.setIncrementalUpdate(isIncrementalUpdate);
            }
        }
    }
}
