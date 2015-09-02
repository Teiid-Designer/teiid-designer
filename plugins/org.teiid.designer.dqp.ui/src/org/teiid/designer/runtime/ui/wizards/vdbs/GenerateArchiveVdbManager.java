/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the archive vdb
 */
public class GenerateArchiveVdbManager extends AbstractGenerateVdbManager {

    private class ResourceRecorder implements IResourceChangeListener {

        private final Set<IResource> resources = new HashSet<IResource>();

        private void addResource(IResourceDelta resourceDelta) {
            if (resourceDelta == null)
                return;

            for (IResourceDelta childDelta : resourceDelta.getAffectedChildren()) {
                addResource(childDelta);
            }

            if (IResourceDelta.ADDED != resourceDelta.getKind())
                return;

            IResource resource = resourceDelta.getResource();
            if (! ModelUtil.isModelFile(resource))
                return;

            if (getOutputLocation().getFullPath().isPrefixOf(resource.getFullPath()))
                resources.add(resourceDelta.getResource());
        }

        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            if (delta == null)
                return;

            addResource(delta);
        }

        /**
         * @return unmodifiable set of changed resources
         */
        public Set<IResource> getResources() {
            return Collections.unmodifiableSet(resources);
        }

        /**
         * Clear the recorded resources
         */
        public void clear() {
            resources .clear();
        }
    }

    private final ResourceRecorder resourceRecorder = new ResourceRecorder();

    private IFile dynamicVdbFile;

//    private String delegateArchiveVdbName;

    /**
     * @param wizard 
     * @param dynamicVdbFile
     * @throws Exception
     */
    public GenerateArchiveVdbManager(AbstractWizard wizard, IFile dynamicVdbFile) throws Exception {
        super(wizard);
        this.dynamicVdbFile = dynamicVdbFile;
        setDynamicVdb(new DynamicVdb(this.dynamicVdbFile));

        String dynName = getDynamicVdb().getName();
        setOutputVdbName(dynName);
//        delegateArchiveVdbName = dynName + UNDERSCORE + getVersion();
        setOutputVdbFileName(dynName + ITeiidVdb.VDB_DOT_EXTENSION);
        setOutputLocation(this.dynamicVdbFile.getParent());
    }

    /**
    * @return dynamic vdb name
    */
    public IFile getDynamicVdbFile() {
        return dynamicVdbFile;
    }

    /**
     * @param dynamicVdbFile
     */
    public void setDynamicVdbFile(IFile dynamicVdbFile) {
        this.dynamicVdbFile = dynamicVdbFile;
    }

    /**
     * @return whether archive vdb generation is required
     */
    public boolean isGenerateRequired() {
        return getArchiveVdb() == null;
    }

    /**
     * Generate the archive vdb from the dynamic vdb
     */
    public void generate() {
        if (!isGenerateRequired())
            return;

        resourceRecorder.clear();

        if (getDynamicVdb() == null)
            return;

        GeneratorCallback<XmiVdb> callback = new GeneratorCallback<XmiVdb>() {

            @Override
            public DynamicVdb getSourceVdb() {
                return getDynamicVdb();
            }

            @Override
            public Class<XmiVdb> getTargetType() {
                return XmiVdb.class;
            }

            @Override
            public void aboutToRun() {
                ModelerCore.getWorkspace().addResourceChangeListener(resourceRecorder,
                                                                     IResourceChangeEvent.POST_CHANGE
                                                                     | IResourceChangeEvent.PRE_CLOSE
                                                                     | IResourceChangeEvent.PRE_BUILD
                                                                     | IResourceChangeEvent.POST_BUILD
                                                                     | IResourceChangeEvent.PRE_REFRESH);
            }

            @Override
            public void onCompletion(IStatus status) {
                try {
                    XmiVdb xmiVdb = getResult();

                    if (status.isOK())
                        setArchiveVdb(xmiVdb);

                } finally {
                    ModelerCore.getWorkspace().removeResourceChangeListener(resourceRecorder);
                }
            }
        };

        generateVdbJob(callback);
    }

    /**
     * Validate the manager's settings
     */
    @Override
    public void validate() {
        super.validate();

	    if (getStatus().getSeverity() > IStatus.WARNING)
	        return;

        if (getOutputVdbFileName().contains(DOT) && !getOutputVdbFileName().toLowerCase().endsWith(ITeiidVdb.VDB_DOT_EXTENSION)) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateArchiveVdbWizard_validation_vdbMissingVdbExtension));
            return;
        }

        if (!isGenerateRequired() && getArchiveVdb() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateArchiveVdbWizard_validation_noArchiveVdbGenerated));
            return;
        }
    }

    @Override
    public void write() throws Exception {
        //
        // Save the vdb
        //
        if (isGenerateRequired())
            return;

        getArchiveVdb().save();

        super.write();
    }

    /**
     * Wizard being cancelled so cleanup and remove created models
     */
    public void cancel() {
        String name = Messages.GenerateArchiveVdbWizard_cancelJobName;
        final Set<IResource> resources = resourceRecorder.getResources();
        if (resources.isEmpty())
            return;

        WorkspaceJob job = new WorkspaceJob(name) {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) {
                IStatus status = Status.OK_STATUS;

                for (IResource resource : resources) {
                    if (resource == null || ! resource.exists())
                        continue;

                    try {
                        resource.delete(true, monitor);
                    } catch (CoreException ex) {
                        if (status == Status.OK_STATUS)
                            status = new MultiStatus(VdbConstants.PLUGIN_ID, IStatus.ERROR, EMPTY_STRING, null);

                        ((MultiStatus)status).add(ex.getStatus());
                    }
                }

                return status;
            }
        };

        job.schedule();
    }

    /**
     * Dispose the manager
     */
    public void dispose() {
        IWorkspace workspace = ModelerCore.getWorkspace();
        if (workspace == null)
            return;

        workspace.removeResourceChangeListener(resourceRecorder);
    }
}
