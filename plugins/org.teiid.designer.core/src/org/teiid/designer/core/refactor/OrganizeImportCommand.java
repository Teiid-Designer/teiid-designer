/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.ModelAnnotation;


/**
 * OrganizeImportCommand
 *
 * @since 8.0
 */
public class OrganizeImportCommand implements ModelRefactorCommand {

    private OrganizeImportCommandHelper organizeImportCommandHelper;
    private CoreFactory factory;
    private Resource resource;
    private OrganizeImportHandler handler;
    private Collection<PathPair> paths;
    private final Object factoryLock = new Object();

    /**
     * @since 4.3
     */
    public OrganizeImportCommand() {
        // for test only
        resetOrganizeImportHelper();
    }

    /**
     * @param resource
     * @since 4.3
     */
    public void setResource( final Resource resource ) {
        this.resource = resource;
    }

    /**
     * @return Resource
     * @since 4.3
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Set the {@link OrganizeImportHandler handler} that should be used to post questions when organizing imports. Example of
     * questions include choosing between ambiguous resources.
     * 
     * @param handler the handler
     */
    public void setHandler( final OrganizeImportHandler handler ) {
        this.handler = handler;
    }

    /**
     * Return the {@link OrganizeImportHandler handler} that is used to post questions when organizing imports. Example of
     * questions include choosing between ambiguous resources.
     * 
     * @return the handler
     */
    public OrganizeImportHandler getHandler() {
        return handler;
    }

    /**
     * Return the factory that should be used.
     * 
     * @return the factory; never null
     */
    public CoreFactory getFactory() {
        // return organizeImportCommandHelper.getFactory();

        if (this.factory == null) {
            synchronized (this.factoryLock) {
                if (this.factory == null) {
                    this.factory = CoreFactory.eINSTANCE;
                }
            }
        }
        return this.factory;
    }

    /**
     * Set the CoreFactory instance that should be used.
     * 
     * @param factory the factory that should be used; null signals that the default {@link CoreFactory#eINSTANCE CoreFactory
     *        instance} should be used.
     */
    public void setFactory( final CoreFactory factory ) {
        this.factory = factory;
    }

    /**
     * @return Returns the includeDiagramReferences.
     * @since 4.2
     */
    public boolean isIncludeDiagramReferences() {
        return organizeImportCommandHelper.isIncludeDiagramReferences();
    }

    /**
     * @param includeDiagramReferences The includeDiagramReferences to set.
     * @since 4.2
     */
    public void setIncludeDiagramReferences( boolean includeDiagramReferences ) {
        organizeImportCommandHelper.setIncludeDiagramReferences(includeDiagramReferences);
    }

    /**
     * @see org.teiid.designer.core.refactor.ModelRefactorCommand#canExecute()
     * @since 4.3
     */
    @Override
	public IStatus canExecute() {
        if (this.resource == null) {
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Organizing_imports_must_be_performed_on_a_Resource"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, OrganizeImportCommandHelper.PID,
                                              OrganizeImportCommandHelper.ERROR_MISSING_RESOURCE, msg, null);
            return status;
        }

        IResource iResource = WorkspaceResourceFinderUtil.findIResource(this.resource);
        if (iResource != null && ModelUtil.isIResourceReadOnly(iResource)) {
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.0", iResource.getFullPath()); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, OrganizeImportCommandHelper.PID,
                                              OrganizeImportCommandHelper.ERROR_MISSING_RESOURCE, msg, null);
            return status;
        }

        final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Ready_to_organize_import", new Object[] {this.resource.getURI()}); //$NON-NLS-1$

        return new Status(IStatus.OK, OrganizeImportCommandHelper.PID, OrganizeImportCommandHelper.CAN_EXECUTE, msg, null);
    }

    /**
     * @see org.teiid.designer.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
	public IStatus execute( final IProgressMonitor monitor ) {
        resetOrganizeImportHelper();
        return organizeImportCommandHelper.execute(monitor);
    }

    /**
     * @return List
     * @since 4.3
     */
    public List getModelImports() {
        return organizeImportCommandHelper.getModelImports();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#canUndo()
     */
    @Override
	public boolean canUndo() {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#getAffectedObjects()
     */
    @Override
	public Collection getAffectedObjects() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#getDescription()
     */
    @Override
	public String getDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#getLabel()
     */
    @Override
	public String getLabel() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#getPostExecuteMessages()
     */
    @Override
	public Collection getPostExecuteMessages() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#getResult()
     */
    @Override
	public Collection getResult() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#redo()
     */
    @Override
	public void redo() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#undo()
     */
    @Override
	public void undo() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.refactor.ModelRefactorCommand#canRedo()
     */
    @Override
	public boolean canRedo() {
        return false;
    }

    /**
     * @param paths
     * @since 4.3
     */
    protected void setRefactoredPaths(Collection<PathPair> paths ) {
        this.paths = paths;
    }

    /**
     * Return the pattern match string that could be used to match a UUID in an index record. All index records contain a header
     * portion of the form: recordType|pathInModel|UUID|nameInSource|parentObjectID|
     * 
     * @param name The UUID for whichthe pattern match string is to be constructed.
     * @return The pattern match string of the form: recordType|*|uuid|*
     */
    protected String getUUIDMatchPattern( final char recordType,
                                          final String uuid ) {
        CoreArgCheck.isNotNull(uuid);
        // construct the pattern string
        String patternStr = null;
        if (IndexConstants.RECORD_TYPE.DATATYPE == recordType) {
            patternStr = "" //$NON-NLS-1$
                         + recordType + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER + uuid
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR;
        } else {
            patternStr = "" //$NON-NLS-1$
                         + recordType + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER + uuid
                         + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR;
        }

        return patternStr;
    }

    /**
     * @param resource
     * @return
     * @since 4.3
     */
    protected ModelAnnotation getModelAnnotation( final Resource resource ) {
        if (resource instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)resource;
            return emfResource.getModelAnnotation();
        }
        return null;
    }

    /**
     * @param resourceUri
     * @return
     * @since 4.3
     */
    protected String getModelName( final URI resourceUri ) {
        final String modelNameWithExt = resourceUri.lastSegment();
        final String extension = resourceUri.fileExtension();
        if (extension != null) {
            final int index = modelNameWithExt.indexOf(extension);
            if (index > 1) {
                return modelNameWithExt.substring(0, index - 1); // also remove the "."
            }
        }
        return modelNameWithExt;
    }

    /**
     * @return OrganizeImportCommandHelper
     * @since 4.3
     */
    private void resetOrganizeImportHelper() {

        // Only reset the helper if the resource type changes
        if (this.resource instanceof XSDResourceImpl) {
            if (organizeImportCommandHelper == null || !(organizeImportCommandHelper instanceof OrganizeImportCommandHelperXsd)) {
                organizeImportCommandHelper = new OrganizeImportCommandHelperXsd();
            }

        } else {
            if (organizeImportCommandHelper == null
                || !(organizeImportCommandHelper instanceof OrganizeImportCommandHelperNonXsd)) {
                organizeImportCommandHelper = new OrganizeImportCommandHelperNonXsd();
            }
        }

        organizeImportCommandHelper.setResource(resource);
        organizeImportCommandHelper.setHandler(handler);
        organizeImportCommandHelper.setFactory(factory);
        organizeImportCommandHelper.setRefactoredPaths(paths);
    }
}
