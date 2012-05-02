/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.teiid.core.util.FileUtils;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ui.wizard.IDifferencingWizard;
import com.metamatrix.modeler.compare.ui.wizard.ShowDifferencesPage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.relational.JdbcImporter;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcModelProcessorManager;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;
import com.metamatrix.modeler.jdbc.ui.ModelerJdbcUiConstants;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider;
import com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportPostProcessor;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.0
 */
public class JdbcImportWizard extends AbstractWizard
    implements IPropertiesContext, IDifferencingWizard, IJdbcImportInfoProvider, ModelerJdbcUiConstants, ModelerJdbcUiConstants.Images {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final ImageDescriptor IMAGE = UiPlugin.getDefault().getImageDescriptor(IMPORT_DATABASE_ICON);

    private static final String COPY_ERROR_MESSAGE = getString("copyErrorMessage"); //$NON-NLS-1$
    private static final String IMPORT_ERROR_MESSAGE = getString("importErrorMessage"); //$NON-NLS-1$

    private static final String UNDERSCORE_ONE = "_"; //$NON-NLS-1$
    private static final String UNDERSCORE_TWO = "__"; //$NON-NLS-1$
    
    private boolean doNotFinish = false;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private JdbcImporter importer;
    private IContainer folder;
    private String modelName;
    private IStatus status;
    JdbcSourceSelectionPage srcPg;
    private JdbcImportOptionsPage optionsPg;
    private ShowDifferencesPage diffsPg;
    DifferenceReport drDifferenceReport;
    ProcessorPack ppProcessorPack;
    
    private Properties designerProperties;

    boolean controlsHaveBeenCreated = false;
    private boolean isVirtual;

    private IJdbcImportPostProcessor[] postProcessors;

    // Need to cash the profile when connection is selected so we can use it in Finish method to
    // inject the connection info into model.
    private IConnectionProfile connectionProfile;

    /**
     * @since 4.0
     */
    public JdbcImportWizard() {
        super(UiPlugin.getDefault(), TITLE, IMAGE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean bCanFinish = false;

        boolean bUpdateSelected = false;

        // was update selected?
        if (this.optionsPg != null) {
            bUpdateSelected = this.optionsPg.updateSelected();
        }

        // if so, has user seen the diffs page?
        boolean bDiffsDisplayed = false;
        if (this.diffsPg != null) {
            bDiffsDisplayed = this.diffsPg.isVisible();
        }

        boolean bUpdateStepsPending = false;
        if (bUpdateSelected) {
            if (!bDiffsDisplayed) {
                bUpdateStepsPending = true;
            }
        }

        bCanFinish = super.canFinish() && getFolder() != null && !bUpdateStepsPending;

        return bCanFinish;
    }

    /**
     * Method used to notify any pages that need to know if the connection changed so they can clear info, settings, or do any
     * clean-up work. REQUIRED for Defect 19426 This is intended to be called in sqlConnectionChanged() if a connection is
     * successfuly established.
     * 
     * @since 4.3
     */
    protected void connectionEstablished() {
        // Default does nothing?
    }

    /**
     * Creates the wizard page responsible for selection of types of metadata to import.
     * 
     * @since 4.3
     */
    protected ShowDifferencesPage createDifferencesPage() {
        return new ShowDifferencesPage(this);
    }

    /**
     * Creates the wizard page responsible for selection of types of metadata to import.
     * 
     * @since 4.3
     */
    protected IWizardPage createMetadataPage() {
        return new JdbcImportMetadataPage();
    }

    /**
     * Creates the wizard page responsible for selection of specific source objects from which to import.
     * 
     * @since 4.3
     */
    protected IWizardPage createObjectsPage() {
        return new JdbcImportObjectsPage();
    }

    /**
     * Creates the wizard page responsible for general import options.
     * 
     * @since 4.3
     */
    protected JdbcImportOptionsPage createOptionsPage() {
        return new JdbcImportOptionsPage();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer);
        controlsHaveBeenCreated = true;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        try {
            this.importer.disconnect();
        } catch (final SQLException err) {
            JdbcUiUtil.showAccessError(err);
        }
        super.dispose();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        if( this.doNotFinish ) {
        	return false;
        }
        
        if (!this.srcPg.connect()) {
            return false;
        }

        boolean result = false;

        /*
         * 'finish' will use the previously created 'processor' instead of creating a fresh one, if one was previously created.
         */

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Changing Sql Connections", //$NON-NLS-1$
                                                       new DefaultIgnorableNotificationSource(JdbcImportWizard.this, modelName));
                boolean succeeded = false;
                try {
                    runFinish(monitor);
                    if (getDesignerProperties() != null) {
                        DesignerPropertiesUtil.setSourceModelName(getDesignerProperties(), modelName);
                    }
                    succeeded = true;
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(true, true, op);
            switch (this.status.getSeverity()) {
                case IStatus.WARNING:
                case IStatus.INFO:
                case IStatus.OK:
                    break;
                case IStatus.ERROR:
                default:
                    Util.log(this.status);
                    WidgetUtil.showError(IMPORT_ERROR_MESSAGE);
                    break;
            }
            result = true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            JdbcUiUtil.showError(err, IMPORT_ERROR_MESSAGE);
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getDatabase()
     * @since 5.0
     */
    public JdbcDatabase getDatabase() {
        return this.importer.getDatabase();
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getDifferenceReport()
     * @since 5.0
     */
    public List getDifferenceReports() {
        final List reports = new ArrayList();
        if (drDifferenceReport == null || diffTargetChanged(drDifferenceReport)) {

            // Save object selections from previous page
            final IRunnableWithProgress op = new IRunnableWithProgress() {

                public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        final JdbcSource src = getSource();
                        final RelationalModelProcessor processor = JdbcModelProcessorManager.createRelationalModelProcessor(srcPg.getMetadataProcessor());
                        processor.setMoveRatherThanCopyAdds(!isUpdatedModel());

                        // set property on processor for Inclusion of incomplete FKs.
                        final boolean includeIncompleteFKs = getDatabase().getIncludes().includeIncompleteFKs();
                        processor.setIncludeIncompleteFKs(includeIncompleteFKs);

                        final IFile modelFile = getFolder().getFile(new Path(getModelName()));
                        final ModelResource resrc = ModelerCore.create(modelFile);

                        final ModelAnnotation modelAnnotation = resrc.getModelAnnotation();
                        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                        
                        ModelType type = ModelType.PHYSICAL_LITERAL;
                        
                        isVirtual = optionsPg.isVirtual();
                       
                        if( isVirtual ) {
                        	type = ModelType.VIRTUAL_LITERAL;
                        }
                        modelAnnotation.setModelType(type);

                        if (resrc instanceof ModelResourceImpl) {
                            ((ModelResourceImpl)resrc).setModelType(type);
                        }
                        if (processor instanceof RelationalModelProcessorImpl) {
                            JdbcImportWizard.this.drDifferenceReport = ((RelationalModelProcessorImpl)processor).generateDifferenceReport(resrc,
                                                                                                                                          getDatabase(),
                                                                                                                                          src.getImportSettings(),
                                                                                                                                          monitor);
                        }

                        // any post create tasks?

                        // capture objects in the processorpack
                        ppProcessorPack = new ProcessorPack(processor, src, modelFile, resrc);

                    } catch (final OperationCanceledException err) {
                        // do nothing in particular
                    } catch (final Exception err) {
                        throw new InvocationTargetException(err);
                    } finally {
                        monitor.done();
                    }

                }
            };

            // now run it
            try {
                new ProgressMonitorDialog(getShell()).run(true, true, op);

            } catch (Throwable err) {
                if (err instanceof InvocationTargetException) {
                    err = ((InvocationTargetException)err).getTargetException();
                }
                JdbcUiUtil.showError(err, IMPORT_ERROR_MESSAGE);
            }
        }

        if (drDifferenceReport != null) {
            reports.add(drDifferenceReport);
        }
        return reports;
    }

    /**
     * Determines if current modelName selection is different than the DifferenceReport target
     * 
     * @diffReport the difference report
     * @return 'true' if the selected modelName is different than the diff report target
     */
    private boolean diffTargetChanged( DifferenceReport diffReport ) {
        boolean result = false;
        // Get the target modelName from the difference report
        String diffReportTargetModel = getDifferenceTargetModelName(diffReport);
        // Determine if difference report target is different than selected modelName
        if (diffReportTargetModel != null && !diffReportTargetModel.equalsIgnoreCase(getModelName())) {
            result = true;
        }
        return result;
    }

    /**
     * helper method to get the target model name from the difference report
     * 
     * @param diffReport the difference report
     * @return the diff report target model name
     */
    private String getDifferenceTargetModelName( DifferenceReport diffReport ) {
        String targetModelName = null;
        String uriStr = diffReport.getResultUri();
        URI uri = URI.createURI(uriStr);
        if (uri.isFile()) {
            File theFile = new File(uri.toFileString());
            targetModelName = theFile.getName();
        }
        return targetModelName;
    }

    /**
     * @since 4.0
     */
    IContainer getFolder() {
        return this.folder;
    }

    private IJdbcImportInfoProvider getImportInfoProvider() {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getModelName()
     * @since 5.0
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getModelResource()
     * @since 5.5.3
     */
    public ModelResource getModelResource() {
        return this.ppProcessorPack.getModelResource();
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getPassword()
     * @since 5.0
     */
    public String getPassword() {
        String result = null;
        IWizardPage[] pages = getPages();

        if (pages != null) {
            for (int i = 0; i < pages.length; ++i) {
                if (pages[i] instanceof JdbcSourceSelectionPage) {
                    result = ((JdbcSourceSelectionPage)pages[i]).getPassword();
                    break;
                }
            }
        }

        return result;
    }

    private IJdbcImportPostProcessor[] getPostProcessors() {
        if (this.postProcessors == null) {
            final String EXT_PT = ExtensionPoints.JdbcImportPostProcessor.ID;
            final String CLASS_ATTR = ExtensionPoints.JdbcImportPostProcessor.CLASS_NAME;
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, EXT_PT);

            // shouldn't happen but check to be sure
            if (extensionPoint == null) {
                Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "noExtensionPointFound", EXT_PT)); //$NON-NLS-1$
                this.postProcessors = new IJdbcImportPostProcessor[0];
                return this.postProcessors;
            }

            try {
                // get all extensions
                IExtension[] extensions = extensionPoint.getExtensions();

                if (extensions.length != 0) {
                    List temp = new ArrayList(extensions.length);

                    for (int i = 0; i < extensions.length; ++i) {
                        IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                        for (int j = 0; j < elements.length; ++j) {
                            try {
                                Object obj = elements[j].createExecutableExtension(CLASS_ATTR);

                                if (obj instanceof IJdbcImportPostProcessor) {
                                    temp.add(obj);
                                } else {
                                    Object[] params = new Object[] {obj.getClass(),
                                        elements[j].getDeclaringExtension().getUniqueIdentifier()};
                                    Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "incorrectClass", params)); //$NON-NLS-1$
                                }
                            } catch (Exception theException) {
                                Object[] params = new Object[] {elements[j].getAttribute(CLASS_ATTR),
                                    elements[j].getDeclaringExtension().getUniqueIdentifier()};
                                Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "initError", params)); //$NON-NLS-1$
                            }
                        }
                    }

                    temp.toArray(this.postProcessors = new IJdbcImportPostProcessor[temp.size()]);
                } else {
                    // no post processors found
                    this.postProcessors = new IJdbcImportPostProcessor[0];
                }
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, Util.getString(I18N_PREFIX + "unexpectedErrorProcessingExtensions", EXT_PT)); //$NON-NLS-1$
                this.postProcessors = new IJdbcImportPostProcessor[0];
            }
        }

        return this.postProcessors;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.ui.wizards.IJdbcImportInfoProvider#getSource()
     * @since 5.0
     */
    public JdbcSource getSource() {
        return this.importer.getSource();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection inputSelection ) {
        IStructuredSelection selection = inputSelection;

        Object seletedObj = selection.getFirstElement();
        boolean isSourceModel = false;
        try {
            if (seletedObj instanceof IFile) {
                ModelResource modelResource = ModelUtil.getModelResource((IFile)seletedObj, false);
                isSourceModel = ModelUtilities.isPhysical(modelResource);
            }
        } catch (Exception e) {
            Util.log(e);
        }

        // We need to determine if the product characteristics dictate a hidden project or not
        if (!isSourceModel && ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            selection = new StructuredSelection(ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject());
        }

        this.importer = new JdbcImporter();

        // If not null, set folder to current selection if a folder or to containing folder if a model object
        if (!selection.isEmpty() && 
        	(selection.getFirstElement() instanceof IProject && ((IProject)selection.getFirstElement()).isOpen() ) ) {
            final Object obj = selection.getFirstElement();
            final IContainer folder = ModelUtil.getContainer(obj);
            try {
                if (folder != null && folder.getProject().getNature(ModelerCore.NATURE_ID) != null) {
                    this.folder = folder;
                }
            } catch (final CoreException err) {
                Util.log(err);
                WidgetUtil.showError(err);
            }
            ModelResource model = null;
            try {
                model = JdbcRelationalUtil.getPhysicalModifiableRelationalModel(obj);
                if (model != null) {
                    for (final Iterator iter = model.getAllRootEObjects().iterator(); iter.hasNext();) {
                        if (iter.next() instanceof JdbcSource) {
                            // If we want to support multiple source settings for a given model, then the importer needs
                            // to be updated to figure out which one to use initially during an update
                            this.importer.setUpdatedModel(model);
                            String name = FileUtils.getFilenameWithoutExtension(model.getItemName());
                            String validName = CoreValidationRulesUtil.getValidString(name, null, -1);
                            if (validName != null) {
                                name = validName;
                                while (name.indexOf(UNDERSCORE_TWO) > -1) {
                                    name = name.replaceAll(UNDERSCORE_TWO, UNDERSCORE_ONE);
                                }
                            }
                            this.modelName = name + ModelerCore.MODEL_FILE_EXTENSION;
                            break;
                        }
                    }
                }
            } catch (final ModelWorkspaceException err) {
                if (model != null && !model.hasErrors()) {
                    // Unexpected ...
                    Util.log(err);
                }
                WidgetUtil.showError(err);
            }
        } else {
            if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
            	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
            	
            	if( newProject != null ) {
            		selection = new StructuredSelection(newProject);
            	}
            }
        }

        // Create listener for changes to SQL connection
        this.srcPg = new JdbcSourceSelectionPage(this.importer.getSource());
        this.srcPg.addChangeListener(new IChangeListener() {

            public void stateChanged( final IChangeNotifier notifier ) {
                if (controlsHaveBeenCreated) {
                    // Wrap in transaction so it doesn't result in Significant Undoable
                    boolean started = ModelerCore.startTxn(false, false, "Changing Sql Connections", this); //$NON-NLS-1$
                    boolean succeeded = false;
                    try {
                        sqlConnectionChanged(JdbcImportWizard.this.srcPg);
                        succeeded = true;
                    } finally {
                        if (started) {
                            if (succeeded) {
                                ModelerCore.commitTxn();
                            } else {
                                ModelerCore.rollbackTxn();
                            }
                        }
                    }
                }
            }
        });

        this.optionsPg = createOptionsPage();
        // If updating, mark options page incomplete to force user to select update checkbox on page
        if (this.importer.getUpdatedModel() != null) {
            this.optionsPg.setPageComplete(false);
        }
        this.diffsPg = createDifferencesPage();

        addPage(this.srcPg);
        addPage(createMetadataPage());
        addPage(createObjectsPage());
        addPage(this.optionsPg);
        addPage(this.diffsPg);
    }

    /**
     * @since 4.0
     */
    boolean isUpdatedModel() {
        return (this.importer.getUpdatedModel() != null);
    }

    void runFinish( IProgressMonitor monitor ) throws InvocationTargetException {
        Stopwatch totalWatch = new Stopwatch();
        totalWatch.start();
        Stopwatch sWatch = new Stopwatch();

        try {
            sWatch.start();
            if (ppProcessorPack == null) {
                final JdbcSource src = getSource();
                final RelationalModelProcessor processor = JdbcModelProcessorManager.createRelationalModelProcessor(srcPg.getMetadataProcessor());

                // set property on processor for Inclusion of incomplete FKs.
                final boolean includeIncompleteFKs = getDatabase().getIncludes().includeIncompleteFKs();
                processor.setIncludeIncompleteFKs(includeIncompleteFKs);

                // Added for debug performance logging purposes
                processor.setMoveRatherThanCopyAdds(!isUpdatedModel());
                final IFile modelFile = getFolder().getFile(new Path(getModelName()));
                final ModelResource resrc = ModelerCore.create(modelFile);

                final ModelAnnotation modelAnnotation = resrc.getModelAnnotation();
                modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                ModelType type = ModelType.PHYSICAL_LITERAL;

                this.isVirtual = this.optionsPg.isVirtual();

                if( this.isVirtual ) {
                	type = ModelType.VIRTUAL_LITERAL;
                }
                modelAnnotation.setModelType(type);

                if (resrc instanceof ModelResourceImpl) {
                    ((ModelResourceImpl)resrc).setModelType(type);
                }
                // Moved this call to AFTER the MODEL TYPE has been set.
                ModelUtilities.initializeModelContainers(resrc, "Jdbc Import", this); //$NON-NLS-1$

                // If connection profile is cached, which it should, go ahead and inject the data into the
                // model resource.
                if (this.connectionProfile != null) {
                    IConnectionInfoProvider provider = new JDBCConnectionInfoProvider();
                    provider.setConnectionInfo(resrc, this.connectionProfile);
                }
                JdbcImportWizard.this.status = processor.execute(resrc, getDatabase(), src.getImportSettings(), monitor);

                // capture objects in the processorpack
                ppProcessorPack = new ProcessorPack(processor, src, modelFile, resrc);
            } else {
                /*
                 * handle the case where we have already created a processor, allowed the user to modify the Diff Report, etc.
                 * ppProcessorPack The processor has special code inside its 'performMerge' method that uses the existing
                 * DifferenceProcessor if one exists (meaning it had been created by the 'generateProcessor(?)' method...So all we
                 * need to do here is to call 'execute' using the preexisting RelationalModelProcessor.
                 */
                JdbcImportWizard.this.status = ppProcessorPack.getProcessor().execute(ppProcessorPack.getModelResource(),
                                                                                      getDatabase(),
                                                                                      ppProcessorPack.getJdbcSource().getImportSettings(),
                                                                                      monitor);
            }
            sWatch.stop();

            //
            // post processors
            //

            if (!monitor.isCanceled() && JdbcImportWizard.this.status.getSeverity() != IStatus.ERROR) {
                IJdbcImportPostProcessor[] processors = getPostProcessors();

                for (int i = 0; i < processors.length; ++i) {
                    try {
                        processors[i].postProcess(getImportInfoProvider());
                    } catch (Exception theException) {
                        Util.log(IStatus.ERROR,
                                 theException,
                                 Util.getString(I18N_PREFIX + "postProcessingError", processors[i].getClass())); //$NON-NLS-1$
                    }
                }
            }

            // cleanup
            if (JdbcImportWizard.this.status.getSeverity() != IStatus.ERROR) {
                sWatch.start(true);
                // Remove the old source setting in the model, in case this is an update
                // The loop below assumes that only one source setting can exist for a given model
                final List objs = ppProcessorPack.getModelResource().getAllRootEObjects();
                for (final Iterator iter = objs.iterator(); iter.hasNext();) {
                    if (iter.next() instanceof JdbcSource) {
                        iter.remove();
                        break;
                    }
                }
                // Add the source setting to the root of the model
                ppProcessorPack.getJdbcSource().setJdbcDriver(null);
                objs.add(ppProcessorPack.getJdbcSource());

                sWatch.stop();
                sWatch.start(true);

                // Check if Virtual, then re-set ModelType
                if( isVirtual ) {
                	ppProcessorPack.getModelResource().getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
                	((ModelResourceImpl)ppProcessorPack.getModelResource()).setModelType(ModelType.VIRTUAL_LITERAL);
                }
                
                // Auto save the model & refresh
                ppProcessorPack.getModelResource().save(monitor, true);
                sWatch.stop();

                sWatch.start(true);
                final IResource resrc = this.folder.findMember(getModelName());
                if (resrc != null) {
                    resrc.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                }
                sWatch.stop();

                sWatch.start(true);
                ModelEditorManager.activate(ppProcessorPack.getModelFile(), true);
                sWatch.stop();

            }
        } catch (final OperationCanceledException err) {
            // do nothing in particular
        } catch (final Exception err) {
            throw new InvocationTargetException(err);
        } finally {
            monitor.done();
            ppProcessorPack = null;
            totalWatch.stop();
        }
    }

    /**
     * @since 4.0
     */
    void setFolder( final IContainer folder ) {
        CoreArgCheck.isNotNull(folder);
        this.folder = folder;
    }

    /**
     * @since 4.0
     */
    void setModelName( final String name ) {
        CoreArgCheck.isNotEmpty(name);
        this.modelName = name;
    }

    ModelResource getUpdatedModel() {
        return this.importer.getUpdatedModel();
    }

    /**
     * @throws ModelWorkspaceException
     * @since 4.0
     */
    void setUpdatedModel( ModelResource model ) throws ModelWorkspaceException {
        this.importer.setUpdatedModel(model);
    }

    /**
     * @since 4.0
     */
    void sqlConnectionChanged( final JdbcSourceSelectionPage page ) {
        final Connection sqlConnection = page.getConnection();
        if (sqlConnection == null) {
            this.importer.setDatabase(null);
            this.connectionProfile = null;
        } else {
            try {
                final JdbcSource src = (JdbcSource)ModelerCore.getModelEditor().copy(page.getSource());
                this.importer.setSource(src);
                final JdbcDatabase db = JdbcPlugin.getJdbcDatabase(src, sqlConnection);
                JdbcPlugin.ensureNonNullImportSettings(src);
                this.importer.setDatabase(db);
                if (getModelName() == null) {
                    String tempModelName = CoreValidationRulesUtil.getValidString(db.getName(), null, -1);
                    if (tempModelName == null) {
                        tempModelName = db.getName();
                    }
                    tempModelName = FileUtils.toFileNameWithExtension(tempModelName, ModelerCore.MODEL_FILE_EXTENSION);

                    String validName = tempModelName;
                    while (validName.indexOf(UNDERSCORE_TWO) > -1) {
                        validName = validName.replaceAll(UNDERSCORE_TWO, UNDERSCORE_ONE);
                    }
                    tempModelName = validName;

                    if (!CoreStringUtil.isEmpty(tempModelName)
                        && ResourcesPlugin.getWorkspace().validateName(tempModelName, IResource.FILE).isOK()) {
                        this.modelName = tempModelName;
                    }
                }
                if (this.folder != null) {
                    final IResource resrc = this.folder.findMember(this.modelName);
                    if (resrc != null) {
                        final ModelResource model = JdbcRelationalUtil.getPhysicalModifiableRelationalModel(resrc);
                        if (model != null) {
                            this.importer.setUpdatedModel(model);
                            // Mark options page incomplete to force user to select update checkbox on page
                            this.optionsPg.setPageComplete(false);
                        }
                    }
                }

                // Cache the CP so it can be used during Finish method
                this.connectionProfile = page.getConnectionProfile();
                connectionEstablished();
            } catch (final Exception err) {
                JdbcUiUtil.showError(err, COPY_ERROR_MESSAGE);
            }
        }
        getContainer().updateButtons();
    }

    JdbcNode findNode( final IPath path,
                       final JdbcNode parent ) throws JdbcException {
        return this.importer.findNode(path, parent);
    }
    
    @Override
    public void setProperties(Properties props) {
        this.designerProperties = props;
        if (this.folder == null) {
        	IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcFolder = DesignerPropertiesUtil.getSourcesFolder(this.designerProperties);
            if (srcFolder != null) {
                setFolder(srcFolder);
            } else if( project != null ) {
            	setFolder(project);
            }
        }
    	
    	if( this.connectionProfile == null ) {
    		// check for project property and if sources folder property exists
            String profileName = DesignerPropertiesUtil.getConnectionProfileName(this.designerProperties);
    		if( profileName != null && !profileName.isEmpty() ) {
    			// Select profile
    			srcPg.selectConnectionProfile(profileName);
    		}
    	}
    }

    public Properties getDesignerProperties() {
        return this.designerProperties;
    }

    // ===========================================================================================================================
    // Inner Class

    class ProcessorPack {

        private RelationalModelProcessor rmpProcessor;
        private JdbcSource src;
        private IFile modelFile;
        private ModelResource resrc;

        public ProcessorPack() {
        }

        public ProcessorPack( RelationalModelProcessor rmpProcessor,
                              JdbcSource src,
                              IFile modelFile,
                              ModelResource resrc ) {

            this.rmpProcessor = rmpProcessor;
            this.src = src;
            this.modelFile = modelFile;
            this.resrc = resrc;
        }

        public void setProcessor( RelationalModelProcessor rmpProcessor ) {
            this.rmpProcessor = rmpProcessor;
        }

        public RelationalModelProcessor getProcessor() {
            return rmpProcessor;
        }

        public void setJdbcSource( JdbcSource src ) {
            this.src = src;
        }

        public JdbcSource getJdbcSource() {
            return src;
        }

        public void setModelFile( IFile modelFile ) {
            this.modelFile = modelFile;
        }

        public IFile getModelFile() {
            return modelFile;
        }

        public void setModelResource( ModelResource resrc ) {
            this.resrc = resrc;
        }

        public ModelResource getModelResource() {
            return resrc;
        }

    }
}
