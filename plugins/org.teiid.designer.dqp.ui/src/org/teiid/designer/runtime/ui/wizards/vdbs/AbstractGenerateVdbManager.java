/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.StringWriter;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.CompositeProgressMonitor;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.BasicVdb;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 *
 */
public abstract class AbstractGenerateVdbManager implements UiConstants, StringConstants {

    private class GenerateRunnable implements IRunnableWithProgress {

        private final GeneratorCallback callback;

        private final String jobName;

        public GenerateRunnable(GeneratorCallback callback) {
            CoreArgCheck.isNotNull(callback);
            this.callback = callback;
            this.jobName = NLS.bind(Messages.GenerateVdbWizard_ConvertJobName,
                                    StringUtilities.fromCamelCase(callback.getSourceVdb().getClass().getSimpleName()),
                                    StringUtilities.fromCamelCase(callback.getTargetType().getSimpleName())
            );
        }

        @Override
        public void run(final IProgressMonitor wizardMonitor) {
            WorkspaceJob job = new WorkspaceJob(jobName) {
                @Override
                public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                    CompositeProgressMonitor compositeMonitor = new CompositeProgressMonitor(wizardMonitor, monitor);
                    try {
                        compositeMonitor.beginTask(jobName, 1);

                        callback.execute();

                        compositeMonitor.worked(1);

                        return Status.OK_STATUS;
                    } catch (Exception ex) {
                        throw toCoreException(ex);
                    } finally {
                        compositeMonitor.done();
                    }
                }
            };

            job.addJobChangeListener(new JobChangeAdapter() {

                @Override
                public void aboutToRun(IJobChangeEvent event) {
                    callback.aboutToRun();
                }

                @Override
                public void done(final IJobChangeEvent event) {
                    //
                    // Refresh to display the vdb
                    //
                    try {
                        refreshOutputLocation();
                    } catch (CoreException ex) {
                        // Nothing to do
                    }

                    callback.onCompletion(event.getResult());
                }
            });

            job.schedule();

            //
            // Hold up this thread until the end of the job
            //
            try {
                job.join();
            } catch (InterruptedException ex) {
                CoreException coreException = toCoreException(ex);
                callback.onCompletion(coreException.getStatus());

                // Something went wrong. Eliminate the job just in case
                job.cancel();
            }

            Runnable swtRunnable = new Runnable() {

                @Override
                public void run() {
                    wizard.getContainer().updateButtons();
                }
            };

            UiUtil.runInSwtThread(swtRunnable, true);
        }
    }

    protected abstract class GeneratorCallback<V extends Vdb> {

        private V vdb;

        /**
         * Execute prior callback execution
         */
        public void aboutToRun() {
            // Do nothing by default
        }

        /**
         * Execute the generation
         */
        protected void execute() throws Exception {
            vdb = getSourceVdb().convert(getTargetType(), getDestination(), conversionOptions);
        }

        /**
         * @param status the status of the execution
         */
        public abstract void onCompletion(IStatus status);

        /**
         * @return the source vdb to be generated
         */
        public abstract Vdb getSourceVdb();

        /**
         * @return the target vdb type
         */
        public abstract Class<V> getTargetType();

        protected final V getResult() {
            return vdb;
        }
    }

    protected static final StringNameValidator nameValidator = new StringNameValidator(
                                                                                       StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                       StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
                                                                                       new char[] {'_', '-', '.'});

    private final AbstractWizard wizard;

    private DynamicVdb dynamicVdb;

    private String version = Integer.toString(1);

    private XmiVdb archiveVdb;

    private IStatus status = Status.OK_STATUS;

    private IContainer outputLocation;

    private String outputVdbFileName;
    
    private String outputVdbName;

    private Properties conversionOptions = new Properties();
    
    private boolean overwriteExistingFiles = true;

    /**
     * @param wizard
     */
    public AbstractGenerateVdbManager(AbstractWizard wizard) {
        this.wizard = wizard;
    }

    /**
     * @return dynamic vdb
     */
    public DynamicVdb getDynamicVdb() {
        return dynamicVdb;
    }

    /**
     * @param dynamicVdb the dynamicVdb to set
     */
    protected void setDynamicVdb(DynamicVdb dynamicVdb) {
        this.dynamicVdb = dynamicVdb;
    }

    /**
     * @return archive vdb
     */
    public XmiVdb getArchiveVdb() {
        return archiveVdb;
    }

    /**
     * @param archiveVdb the archiveVdb to set
     */
    protected void setArchiveVdb(XmiVdb archiveVdb) {
        this.archiveVdb = archiveVdb;
    }

    /**
     * @return output location
     */
    public IContainer getOutputLocation() {
        return outputLocation;
    }
    
    /**
     * 
     * @return 
     */
    public String getOutputVdbName() {
        return this.outputVdbName;
    }

    /**
     * @param outputLocation
     */
    protected void setOutputLocation(IContainer outputLocation) {
        this.outputLocation = outputLocation;
    }

    protected void refreshOutputLocation() throws CoreException {
        if (getOutputLocation() == null)
            return;

        if (!getOutputLocation().exists())
            return;

        getOutputLocation().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    }

    /**
     * @return the outputName
     */
    public String getOutputVdbFileName() {
        return this.outputVdbFileName;
    }

    /**
     * @return destination file
     */
    public IFile getDestination() {
        return outputLocation.getFile(new Path(getOutputVdbFileName()));
    }

    /**
     * @param outputName the outputName to set
     */
    protected void setOutputVdbFileName(String outputName) {
        this.outputVdbFileName = outputName;
    }
    
    /**
     * @param outputName the outputVdbName to set
     */
    protected void setOutputVdbName(String outputVdbName) {
        this.outputVdbName = outputVdbName;
    }

    /**
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the xml string representation of the dynamic vdb
     * @throws Exception if error occurs
     */
    public String getDynamicVdbXml() throws Exception {
        StringWriter writer = new StringWriter();

        //
        // Writer will be closed by write method
        //
        dynamicVdb.write(writer);
        return writer.toString();
    }

    /**
     * @return status
     */
    public IStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    protected void setStatus(IStatus status) {
        this.status = status;
    }

    /**
     * 
     * @param name
     * @param value
     */
    protected void addConversionOption(String name, String value) {
        conversionOptions.setProperty(name, value);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    protected String getConversionOption(String key) {
    	return conversionOptions.getProperty(key);
    }
    
    protected void setOverwriteExistingFiles(boolean overwrite) {
    	this.overwriteExistingFiles = overwrite;
    }
    
    protected boolean overwriteExistingFiles() {
    	return this.overwriteExistingFiles;
    }

    /**
     * Validate the values of the wizard
     */
    protected void validate() {
        setStatus(Status.OK_STATUS);

        // Check ouptut vdb name
        String proposedVdbName = getOutputVdbFileName();
        String validationMessage = nameValidator.checkValidName(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        // Check Version # is an integer
        try {
            Integer.parseInt(getVersion());
        } catch (NumberFormatException nfe) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID,
                                 NLS.bind(Messages.GenerateVdbWizard_validation_versionNotInteger, getVersion())));
            return;
        }

        // output location can't be null
        if (getOutputLocation() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetLocationUndefined));
            return;
        }

        if (!getOutputLocation().exists()) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetLocationNotExist));
            return;
        }

        if (getOutputVdbFileName() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_vdbFileNameUndefined));
            return;
        }

        IFile destination = getDestination();
        if (destination.exists()) {
        	int severity = overwriteExistingFiles ? IStatus.WARNING : IStatus.ERROR;
            setStatus(new Status(severity, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetFileAlreadyExists));
            return;
        }

    }

    protected void generateVdbJob(final GeneratorCallback callback) {
        GenerateRunnable runnable = new GenerateRunnable(callback);
        try {
            wizard.getContainer().run(true, true, runnable);
        } catch (Exception ex) {
            CoreException coreException = toCoreException(ex);
            callback.onCompletion(coreException.getStatus());
        } finally {
            wizard.getContainer().updateButtons();
        }
    }

    protected CoreException toCoreException(Throwable throwable) {
        if (throwable instanceof CoreException)
            return (CoreException)throwable;

        String message = VdbPlugin.UTIL.getStringOrKey(BasicVdb.class.getSimpleName() + DOT + "vdbExceptionThrown"); //$NON-NLS-1$ 

        MultiStatus status = new MultiStatus(VdbConstants.PLUGIN_ID, IStatus.ERROR, message, null);
        do {
            status.add(new Status(IStatus.ERROR, VdbConstants.PLUGIN_ID, " * " + throwable.getLocalizedMessage())); //$NON-NLS-1$
            throwable = throwable.getCause();
        } while (throwable != null);

        return new CoreException(status);
    }

    /**
     * @throws Exception
     */
    protected void write() throws Exception {
        //
        // Refresh the output location
        //
        refreshOutputLocation();

        //
        // Perform a clean build on the project
        //
        IProject project = getOutputLocation().getProject();
        project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
    }
}
