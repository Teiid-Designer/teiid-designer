/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the archive vdb
 */
public class GenerateArchiveVdbManager extends AbstractGenerateVdbManager {

    private IFile dynamicVdbFile;

    private String delegateArchiveVdbName;

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
        delegateArchiveVdbName = dynName + UNDERSCORE + getVersion();
        setOutputName(dynName + ITeiidVdb.VDB_DOT_EXTENSION);
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
     * @return proposed archive vdb name
     */
    public String getDelegateArchiveVdbName() {
        return delegateArchiveVdbName;
    }

    /**
     * @param delegateArchiveVdbName
     */
    public void setDelegateArchiveVdbName(String delegateArchiveVdbName) {
        this.delegateArchiveVdbName = delegateArchiveVdbName;
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
            public void onCompletion(IStatus status) {
                XmiVdb xmiVdb = getResult();

                if (status.isOK())
                    setArchiveVdb(xmiVdb);
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

        if (! Status.OK_STATUS.equals(getStatus()))
            return; // Something already wrong - no need to check further

        if (getOutputName().contains(DOT) && !getOutputName().toLowerCase().endsWith(ITeiidVdb.VDB_DOT_EXTENSION)) {
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
        refreshOutputLocation();
    }

    /**
     * Wizard being cancelled so cleanup and remove created models
     */
    public void cancel() {
        if (isGenerateRequired())
            return; // nothing already generated

        String name = Messages.GenerateArchiveVdbWizard_cancelJobName;

        WorkspaceJob job = new WorkspaceJob(name) {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) {
                IStatus status = Status.OK_STATUS;
                XmiVdb xmiVdb = getArchiveVdb();
                Set<VdbModelEntry> modelEntries = xmiVdb.getModelEntries();
                if (modelEntries == null || modelEntries.isEmpty())
                    return status;

                for (VdbModelEntry entry : modelEntries) {
                    IFile entryFile = entry.findFileInWorkspace();
                    if (entryFile == null || !entryFile.exists())
                        continue;

                    try {
                        entryFile.delete(true, monitor);
                    } catch (CoreException ex) {
                        if (status == Status.OK_STATUS)
                            status = new MultiStatus(VdbConstants.PLUGIN_ID, IStatus.ERROR, EMPTY_STRING, null);

                        ((MultiStatus)status).add(ex.getStatus());
                    }

                    entry.dispose();
                }

                return status;
            }
        };

        job.schedule();
    }
}
