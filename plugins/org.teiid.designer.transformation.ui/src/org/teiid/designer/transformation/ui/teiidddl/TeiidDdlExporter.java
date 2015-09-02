package org.teiid.designer.transformation.ui.teiidddl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ddl.TeiidModelToDdlGenerator;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;


public class TeiidDdlExporter {
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

	
    public static boolean DEFAULT_USE_NAME_IN_SOURCE          = true;
    public static boolean DEFAULT_USE_NATIVE_TYPE             = false;
    

	public static ExportChoice CLIPBOARD_TYPE = ExportChoice.CLIPBOARD;
	public static ExportChoice FILE_TYPE = ExportChoice.FILE;
    
	public static enum ExportChoice {
		CLIPBOARD("Clipboard"), //$NON-NLS-1$

		FILE("To File"); //$NON-NLS-1$

		private final String label;

		private ExportChoice(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

    private boolean nameInSourceUsed;
    private boolean nativeTypeUsed;
    
    private ModelResource modelResource;
    
	private File ddlFile;
    
    private ExportChoice exportType = ExportChoice.CLIPBOARD;
    
    private static Clipboard CLIPBOARD;
    
	private IStatus writeStatus;
    
    private IStatus OK_STATUS = new Status(IStatus.OK, UiPlugin.PLUGIN_ID, "Click Finish to export");

    /**
     * Construct an instance of DdlOptionsImpl.
     * 
     */
    public TeiidDdlExporter() {
        super();
        this.nameInSourceUsed = DEFAULT_USE_NAME_IN_SOURCE;
        this.nativeTypeUsed = DEFAULT_USE_NATIVE_TYPE;
		if (CLIPBOARD == null) {
			CLIPBOARD = new Clipboard(UiPlugin.getDefault().getWorkbench().getDisplay());
		}
    }
    
    /**
     * Determine whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @return true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
	public boolean isNameInSourceUsed() {
        return this.nameInSourceUsed;
    }

    /**
     * Set whether names in source should be used in the DDL, or whether only names should be used.
     * This option does not apply when there are no names in source.
     * @param useNameInSource true if entities' name in source rather than their name should be used for the object
     * names in the DDL, or false if only their names should be used.
     */
	public void setNameInSourceUsed( final boolean useNameInSource) {
        this.nameInSourceUsed = useNameInSource;
    }

    /**
     */
	public boolean isNativeTypeUsed() {
        return this.nativeTypeUsed;
    }

    /**
     */
	public void setNativeTypeUsed(boolean useNativeType) {
        this.nativeTypeUsed = useNativeType;
    }

	public ModelResource getModelResource() {
		return modelResource;
	}

	public void setModelResource(ModelResource modelResource) {
		this.modelResource = modelResource;
	}
	
	public File getDdlFile() {
		return ddlFile;
	}

	public void setDdlFile(File ddlFile) {
		this.ddlFile = ddlFile;
	}
	

	public ExportChoice getExportType() {
		return exportType;
	}

	public void setExportType(ExportChoice exportType) {
		this.exportType = exportType;
	}

	public IStatus validate() {
		if( modelResource == null ) {
			return new Status(IStatus.ERROR, UiPlugin.PLUGIN_ID, "No model selected for export");
		}
		
		return OK_STATUS;
	}
	
	public String generateDdl() {
		String ddl = null;
        try {

			// At this point we have either a relational source or view model
			// If Source model, we can expect to walk the table, procedure and function objects
			TeiidModelToDdlGenerator generator = new TeiidModelToDdlGenerator();
			ddl = generator.generate(getModelResource());
			
		} catch (ModelWorkspaceException e) {
			// TODO ADD ERROR STATUS
			e.printStackTrace();
		}
        
        return ddl;
	}
	
    /**
     * @see DdlWriter#write(ModelResource, OutputStream, IProgressMonitor)
     */
	public IStatus write( final ModelResource modelResource,
                          final OutputStream stream,
                          final IProgressMonitor monitor ) {
        CoreArgCheck.isNotNull(modelResource);
        CoreArgCheck.isNotNull(stream);

        // Get the emf resource from the model ...
        try {
            final Resource emfResource = modelResource.getEmfResource();
            final ModelEditor editor = ModelerCore.getModelEditor();
            final String modelName = editor.getModelName(modelResource);
            final String modelFilename = modelResource.getPath().toString();
            final ModelContents contents = ModelContents.getModelContents(modelResource);
            return doWrite(modelResource, stream, monitor != null ? monitor : new NullProgressMonitor());
        } catch (ModelWorkspaceException e) {
            final int code = UNABLE_TO_GET_EMF_RESOURCE;
//            final Object[] params = new Object[] {model.getPath()};
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_getting_EMF_resource", params); //$NON-NLS-1$
            return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, "ERROR WRITING DDL");
        }
    }


    /**
     * Method that actually performs the DDL writing.
     * 
     * @param emfResource the {@link org.teiid.designer.metamodels.relational.RelationalPackage relational} EMF resource that contains
     *        the model to be written out; may not be null
     * @param modelContents the ModelContents object to use; may not be null
     * @param stream the stream to which the DDL is to be written; may not be null
     * @param problems the list to which problems (e.g., {@link IStatus} instances) should be written
     * @param monitor the monitor the should be used; never null
     * @return a status of the process with any {@link IStatus#WARNING warnings}, {@link IStatus#ERROR errors} or
     *         {@link IStatus#INFO information messages}, or which will be {@link IStatus#isOK() marked as OK} if there were no
     *         warnings, errors or other messages.
     */
    protected IStatus doWrite( final ModelResource modelResource,
                               final OutputStream stream,
                               final IProgressMonitor monitor ) {
        CoreArgCheck.isNotNull(modelResource);
        CoreArgCheck.isNotNull(stream);

//        // Start the progress monitor tasks ...
//        final String taskName = DdlPlugin.Util.getString("DdlWriterImpl.ProgressMonitor_main_task_name"); //$NON-NLS-1$
//        final int unitsPerPhase = 100;
//        final int numUnits = 6 * unitsPerPhase; // 6 phases, 100 units each
//        monitor.beginTask(null, numUnits);
//        monitor.setTaskName(taskName);
//
//        final List problems = new ArrayList();
//        final String PLUGINID = DdlPlugin.PLUGIN_ID;
//
//        // Write the DDL ...
//        try {
//
//            // Phase 1: Create the intermediate XML document ...
//            final Document intDoc = formatter.createDocument();
//
//            // Phase 2: Transform the intermediate XML document using the stylesheet ...
//            final Style style = this.options.getStyle();
//            if (style != null) {
//                final XsltTransform xform = new XsltTransform(style);
//                xform.transform(intDoc, stream);
//            } else {
//                // Write the intermediate form out to the stream ...
//                XMLOutputter outputter = new XMLOutputter(JdomHelper.getFormat("  ", true)); //$NON-NLS-1$
//                outputter.output(intDoc, stream);
//            }
//        } catch (TransformerConfigurationException e) {
//            final int code = TRANSFORMER_CONFIGURATION_EXCEPTION;
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_while_initializing_and_configuring_XSLT_transformer"); //$NON-NLS-1$
//            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
//            problems.add(status);
//        } catch (IOException e) {
//            final int code = UNEXPECTED_IO_EXCEPTION;
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Unexpected_I/O_exception"); //$NON-NLS-1$
//            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
//            problems.add(status);
//        } catch (TransformerException e) {
//            final int code = XSLT_PROBLEMS;
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Error_while_transforming_the_model_into_DDL"); //$NON-NLS-1$
//            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
//            problems.add(status);
//        } catch (Throwable e) {
//            final int code = UNEXPECTED_EXCEPTION;
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.Unexpected_exception"); //$NON-NLS-1$
//            final Status status = new Status(IStatus.ERROR, PLUGINID, code, msg, e);
//            problems.add(status);
//        } finally {
//            monitor.done();
//        }
//
//        // Put all of the problems into a single IStatus ...
//        IStatus resultStatus = null;
//        if (problems.isEmpty()) {
//            final int code = WRITTEN_WITH_NO_PROBLEMS;
//            final String msg = DdlPlugin.Util.getString("DdlWriterImpl.completed"); //$NON-NLS-1$
//            final IStatus status = new Status(IStatus.OK, PLUGINID, code, msg, null);
//            resultStatus = status;
//        } else if (problems.size() == 1) {
//            resultStatus = (IStatus)problems.get(0);
//        } else {
//            // There were problems, so determine whether there were warnings and errors ...
//            int numErrors = 0;
//            int numWarnings = 0;
//            final Iterator iter = problems.iterator();
//            while (iter.hasNext()) {
//                final IStatus aStatus = (IStatus)iter.next();
//                if (aStatus.getSeverity() == IStatus.WARNING) {
//                    ++numWarnings;
//                } else if (aStatus.getSeverity() == IStatus.ERROR) {
//                    ++numErrors;
//                }
//            }
//
//            // Create the final status ...
//            final IStatus[] statusArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
//            if (numWarnings != 0 && numErrors == 0) {
//                final int code = WRITE_WITH_WARNINGS;
//                final Object[] params = new Object[] {new Integer(numWarnings)};
//                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.warnings", params); //$NON-NLS-1$
//                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
//            } else if (numWarnings == 0 && numErrors != 0) {
//                final int code = WRITE_WITH_ERRORS;
//                final Object[] params = new Object[] {new Integer(numErrors)};
//                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.errors", params); //$NON-NLS-1$
//                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
//            } else if (numWarnings != 0 && numErrors != 0) {
//                final int code = WRITE_WITH_WARNINGS_AND_ERRORS;
//                final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
//                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.warnings_and_errors", params); //$NON-NLS-1$
//                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
//            } else {
//                final int code = WRITE_WITH_NO_WARNINGS_AND_ERRORS;
//                final String msg = DdlPlugin.Util.getString("DdlWriterImpl.no_warnings_or_errors"); //$NON-NLS-1$
//                resultStatus = new MultiStatus(PLUGINID, code, statusArray, msg, null);
//            }
//        }

        return new Status(IStatus.OK, UiConstants.PLUGIN_ID, "NOT YET IMPLEMENTED"); //resultStatus;
    }
	/*
	 * Write the DDL to the given output stream
	 */
	private void writeToStream(final OutputStream stream) throws Exception {
		new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true,
				new IRunnableWithProgress() {
					@Override
					public void run(final IProgressMonitor monitor) throws InvocationTargetException {
						try {
							writeStatus = write( modelResource, stream,
									monitor);
						} catch (final Exception err) {
							throw new InvocationTargetException(err);
						} finally {
							try {
								stream.close();
							} catch (IOException e) {
								UiConstants.Util.log(e);
							}
							monitor.done();
						}
					}
				});

		if (!writeStatus.isOK()) {
			UiConstants.Util.log(writeStatus);
			WidgetUtil.showError("ERROR exporting model to file");
		}
	}

	public void exportToFile() throws Exception {
		if (ddlFile == null || (ddlFile.exists() && !WidgetUtil.confirmOverwrite(ddlFile))) {
			return;
		}

		writeToStream(new FileOutputStream(ddlFile));
	}

	public void exportToClipboard() throws Exception {
		if (CLIPBOARD == null || CLIPBOARD.isDisposed())
			WidgetUtil.showError("ERROR exporting to clipboard");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		writeToStream(stream);
		CLIPBOARD.setContents(new Object[] { stream.toString() },
				new Transfer[] { TextTransfer.getInstance() });
	}
	
}
