/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ddl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import com.metamatrix.core.log.Logger;
import com.metamatrix.core.log.NullLogger;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.xslt.Style;
import com.metamatrix.core.xslt.XsltTransform;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.ddl.DdlOptions;
import com.metamatrix.modeler.ddl.DdlPlugin;
import com.metamatrix.modeler.ddl.DdlWriter;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * DdlWriterImpl
 */
public class DdlWriterImpl implements DdlWriter {

    public static final int WRITTEN_WITH_NO_PROBLEMS = 4001;
    public static final int UNABLE_TO_GET_EMF_RESOURCE = 4002;
    public static final int WRITE_WITH_WARNINGS = 4003;
    public static final int WRITE_WITH_ERRORS = 4004;
    public static final int WRITE_WITH_WARNINGS_AND_ERRORS = 4005;
    public static final int WRITE_WITH_NO_WARNINGS_AND_ERRORS = 4006;
    public static final int UNEXPECTED_EXCEPTION = 4007;
    public static final int XSLT_PROBLEMS = 4008;
    public static final int UNEXPECTED_IO_EXCEPTION = 4009;
    public static final int TRANSFORMER_CONFIGURATION_EXCEPTION = 4010;
    public static final int ERROR_COMPUTING_RESOURCES_TO_BE_EXPORTED = 4011;

    /** The logger; never null */
    private Logger logger;

    /** The options; never null */
    private final DdlOptions options;

    /**
     * Construct an instance of DdlWriterImpl.
     */
    public DdlWriterImpl() {
        super();
        this.logger = new NullLogger();
        this.options = new DdlOptionsImpl();
    }

    /**
     * Get the logger that this writer is using.
     * 
     * @return the logger; never null, but may be a {@link com.metamatrix.core.log.NullLogger NullLogger} if there is no logging.
     * @see DdlWriter#getLogger()
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Set the logger that this writer should use.
     * 
     * @param logger the new logger; may be null or a {@link com.metamatrix.core.log.NullLogger NullLogger} if there is to be no
     *        logging.
     * @see DdlWriter#setLogger(Logger)
     */
    public void setLogger( final Logger logger ) {
        this.logger = logger != null ? logger : new NullLogger();
    }

    /**
     * Get the options that this writer is currently using
     * 
     * @return the options; never null
     * @see DdlWriter#getOptions()
     */
    public DdlOptions getOptions() {
        return options;
    }

    // /**
    // * Set the options that this writer should use
    // * @param options the new options; may not be null
    // * @see DdlWriter#setOptions(DdlOptions)
    // */
    // public void setOptions(final DdlOptions options) {
    // ArgCheck.isNotNull(options);
    // this.options = options;
    // }

    /**
     * @see DdlWriter#write(Resource, OutputStream, IProgressMonitor)
     */
    public IStatus write( final Resource emfResource,
                          final String modelName,
                          final String modelFilename,
                          final OutputStream stream,
                          final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(emfResource);
        ArgCheck.isNotNull(stream);
        ArgCheck.isNotNull(modelName);
        final ModelContents contents = emfResource instanceof EmfResource ? ((EmfResource)emfResource).getModelContents() : new ModelContents(
                                                                                                                                              emfResource);
        final ModelWrapper wrapper = new ModelWrapper(emfResource, contents, null, modelName, modelFilename);
        final IntermediateFormat formatter = new IntermediateFormat(wrapper, this.options, monitor);
        return doWrite(formatter, stream, monitor != null ? monitor : new NullProgressMonitor());
    }

    /**
     * @see DdlWriter#write(ModelResource, OutputStream, IProgressMonitor)
     */
    public IStatus write( final ModelResource model,
                          final OutputStream stream,
                          final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(model);
        ArgCheck.isNotNull(stream);

        // Get the emf resource from the model ...
        try {
            final Resource emfResource = model.getEmfResource();
            final ModelEditor editor = ModelerCore.getModelEditor();
            final String modelName = editor.getModelName(model);
            final String modelFilename = model.getPath().toString();
            final ModelContents contents = ModelContents.getModelContents(model);
            final ModelWrapper wrapper = new ModelWrapper(emfResource, contents, null, modelName, modelFilename);
            final IntermediateFormat formatter = new IntermediateFormat(wrapper, this.options, monitor);
            return doWrite(formatter, stream, monitor != null ? monitor : new NullProgressMonitor());
        } catch (ModelWorkspaceException e) {
            final int code = UNABLE_TO_GET_EMF_RESOURCE;
            final Object[] params = new Object[] {model.getPath()};
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_getting_EMF_resource", params); //$NON-NLS-1$
            return new Status(IStatus.ERROR, DdlPlugin.PLUGIN_ID, code, msg, e);
        }
    }

    /**
     * @see DdlWriter#write(ModelWorkspaceSelections, OutputStream, IProgressMonitor)
     */
    public IStatus write( final ModelWorkspaceSelections selections,
                          final OutputStream stream,
                          final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(selections);
        ArgCheck.isNotNull(selections.getModelWorkspaceView());
        ArgCheck.isNotNull(stream);

        // Get the selected objects ...
        List modelResources = null;
        try {
            modelResources = selections.getSelectedOrPartiallySelectedModelResources();
        } catch (ModelWorkspaceException e) {
            final int code = ERROR_COMPUTING_RESOURCES_TO_BE_EXPORTED;
            return new Status(IStatus.ERROR, DdlPlugin.PLUGIN_ID, code, e.getMessage(), e);
        }

        // Create a ModelWrapper for each resource ...
        final List wrappers = new LinkedList();
        final Iterator iter = modelResources.iterator();
        while (iter.hasNext()) {
            final ModelResource modelResource = (ModelResource)iter.next();
            final IPath modelPath = modelResource.getPath();
            try {
                final Resource emfResource = modelResource.getEmfResource();
                final ModelEditor editor = ModelerCore.getModelEditor();
                final String modelName = editor.getModelName(modelResource);
                final String modelFilename = modelPath.toString();
                final ModelContents contents = ModelContents.getModelContents(modelResource);
                final ModelWrapper wrapper = new ModelWrapper(emfResource, contents, selections, modelName, modelFilename);
                wrappers.add(wrapper);
            } catch (ModelWorkspaceException e) {
                final int code = UNABLE_TO_GET_EMF_RESOURCE;
                final Object[] params = new Object[] {modelPath};
                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_getting_EMF_resource", params); //$NON-NLS-1$
                return new Status(IStatus.ERROR, DdlPlugin.PLUGIN_ID, code, msg, e);
            }
        }

        // Construct the formatter and write the document ...
        final IntermediateFormat formatter = new IntermediateFormat(wrappers, this.options, monitor);
        return doWrite(formatter, stream, monitor != null ? monitor : new NullProgressMonitor());
    }

    /**
     * Method that actually performs the DDL writing.
     * 
     * @param emfResource the {@link com.metamatrix.metamodels.relational.RelationalPackage relational} EMF resource that contains
     *        the model to be written out; may not be null
     * @param modelContents the ModelContents object to use; may not be null
     * @param stream the stream to which the DDL is to be written; may not be null
     * @param problems the list to which problems (e.g., {@link IStatus} instances) should be written
     * @param monitor the monitor the should be used; never null
     * @return a status of the process with any {@link IStatus#WARNING warnings}, {@link IStatus#ERROR errors} or
     *         {@link IStatus#INFO information messages}, or which will be {@link IStatus#isOK() marked as OK} if there were no
     *         warnings, errors or other messages.
     */
    protected IStatus doWrite( final IntermediateFormat formatter,
                               final OutputStream stream,
                               final IProgressMonitor monitor ) {
        ArgCheck.isNotNull(formatter);
        ArgCheck.isNotNull(stream);

        // Start the progress monitor tasks ...
        final String taskName = DdlPlugin.Util.getString("DdlWriterImpl.ProgressMonitor_main_task_name"); //$NON-NLS-1$
        final int unitsPerPhase = 100;
        final int numUnits = 6 * unitsPerPhase; // 6 phases, 100 units each
        monitor.beginTask(null, numUnits);
        monitor.setTaskName(taskName);

        final List problems = new ArrayList();
        final String PLUGINID = DdlPlugin.PLUGIN_ID;

        // Write the DDL ...
        try {

            // Phase 1: Create the intermediate XML document ...
            final Document intDoc = formatter.createDocument();

            // Phase 2: Transform the intermediate XML document using the stylesheet ...
            final Style style = this.options.getStyle();
            if (style != null) {
                final XsltTransform xform = new XsltTransform(style);
                xform.transform(intDoc, stream);
            } else {
                // Write the intermediate form out to the stream ...
                XMLOutputter outputter = new XMLOutputter(JdomHelper.getFormat("  ", true)); //$NON-NLS-1$
                outputter.output(intDoc, stream);
            }
        } catch (TransformerConfigurationException e) {
            final int code = TRANSFORMER_CONFIGURATION_EXCEPTION;
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_while_initializing_and_configuring_XSLT_transformer"); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
            problems.add(status);
        } catch (IOException e) {
            final int code = UNEXPECTED_IO_EXCEPTION;
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Unexpected_I/O_exception"); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
            problems.add(status);
        } catch (TransformerException e) {
            final int code = XSLT_PROBLEMS;
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_while_transforming_the_model_into_DDL"); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
            problems.add(status);
        } catch (Throwable e) {
            final int code = UNEXPECTED_EXCEPTION;
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Unexpected_exception"); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
            problems.add(status);
        } finally {
            monitor.done();
        }

        // Put all of the problems into a single IStatus ...
        IStatus resultStatus = null;
        if (problems.isEmpty()) {
            final int code = WRITTEN_WITH_NO_PROBLEMS;
            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.completed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, PLUGINID, code, msg, null);
            resultStatus = status;
        } else if (problems.size() == 1) {
            resultStatus = (IStatus)problems.get(0);
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            final Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                final IStatus aStatus = (IStatus)iter.next();
                if (aStatus.getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (aStatus.getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            final IStatus[] statusArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            if (numWarnings != 0 && numErrors == 0) {
                final int code = WRITE_WITH_WARNINGS;
                final Object[] params = new Object[] {new Integer(numWarnings)};
                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                final int code = WRITE_WITH_ERRORS;
                final Object[] params = new Object[] {new Integer(numErrors)};
                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                final int code = WRITE_WITH_WARNINGS_AND_ERRORS;
                final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.warnings_and_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
            } else {
                final int code = WRITE_WITH_NO_WARNINGS_AND_ERRORS;
                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.no_warnings_or_errors"); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
            }
        }

        return resultStatus;
    }

}
