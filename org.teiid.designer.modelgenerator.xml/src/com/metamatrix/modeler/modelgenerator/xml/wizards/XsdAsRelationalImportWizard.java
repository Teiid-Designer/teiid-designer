/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.relational.JdbcImporter;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.impl.JdbcFactoryImpl;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.impl.JdbcDatabaseImpl;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;
import com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;
import com.metamatrix.modeler.modelgenerator.xml.IUiConstants;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.modelgenerator.xml.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.modelgenerator.xml.model.ConnectionImpl;
import com.metamatrix.modeler.modelgenerator.xml.model.DatabaseMetaDataImpl;
import com.metamatrix.modeler.modelgenerator.xml.model.UserSettings;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.BaseXMLRelationalExtensionManager;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.XMLFileExtensionManager;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.XMLHTTPExtensionManager;
import com.metamatrix.modeler.modelgenerator.xml.wizards.jdbc.XmlImporterJdbcDriver;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class XsdAsRelationalImportWizard extends AbstractWizard implements IImportWizard {
    protected static final String WIZARD_TITLE = "title"; //$NON-NLS-1$
    protected static final String XML_FILE_EXTENSION_NAME = "XMLFileExtension"; //$NON-NLS-1$
    protected static final String XML_HTTP_EXTENSION_NAME = "XMLHTTPExtension"; //$NON-NLS-1$
    protected static final String XML_ACS_EXTENSION_NAME = "XMLACSExtension"; //$NON-NLS-1$
    protected static final String TABLES_CLASS_NAME = "RelTables"; //$NON-NLS-1$
    protected static final String CATALOGS_CLASS_NAME = "RelCatalogs"; //$NON-NLS-1$
    protected static final String COLUMNS_CLASS_NAME = "RelColumns"; //$NON-NLS-1$
    protected static final String NAMESPACEPREFIXES_ATTRIBUTE_NAME = "NamespacePrefixes"; //$NON-NLS-1$
    protected static final String COLUMNROLE_ATTRIBUTE_NAME = "Role"; //$NON-NLS-1$
    protected static final String COLUMN_INPUT_PARAM_ATTRIBUTE_NAME = "IsInputParameter"; //$NON-NLS-1$
    protected static final String ACS_URI_SCHEME_RESPONSE = "ACSResponse"; //$NON-NLS-1$
    protected static final String ACS_URI_SCHEME_REQUEST = "ACSRequest"; //$NON-NLS-1$
    protected static final String XPATH_ROOT_FOR_INPUT_ATTRIBUTE_NAME = "XPathRootForInput"; //$NON-NLS-1$
    public static final String RESPONSE_ID_IN_COL_NAME = "ResponseIn"; //$NON-NLS-1$
    public static final String RESPONSE_ID_OUT_COL_NAME = "ResponseOut"; //$NON-NLS-1$
    public static final String COLUMN_ROLE_ENUM = "ColumnRole"; //$NON-NLS-1$
    public static final String RESPONSE_IN_ROLE_ENUM_VALUE = "Response In"; //$NON-NLS-1$
    public static final String RESPONSE_OUT_ROLE_ENUM_VALUE = "Response Out"; //$NON-NLS-1$

    public static final int NO_CATALOG_VAL = 1;
    public static final int NAMESPACE_CATALOG_VAL = 2;
    public static final int FILENAME_CATALOG_VAL = 3;
    public static final int CUSTOM_CATALOG_VAL = 4;

    StateManager manager;
    public static BaseXMLRelationalExtensionManager extManager;

    IntroductionPage introductionPage;
    ResponseSelectionPage responseSelectionPage;
    RequestSelectionPage requestSelectionPage;
    RootElementsPage rootElementsPage;
    JdbcShowDifferencesPage diffsPg;
    JdbcImportOptionsPage optionsPg;
    JdbcImportObjectsPage importObjectsPg;

    // SchemaRepresentation schemaRepresentation;
    UserSettings userSettings;
    RelationshipProcessor relationshipProcessor;
    JdbcFactory jdbcFactory;
    ConnectionImpl jdbcConnection;

    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        createState();

        // Check contents of incoming structured selection. By default it will
        // contain the workspace folder selection. If launched from the ACS/Librados
        // plugin, it will contain several initial state values.
        if (selection != null && !selection.isEmpty()) {
            final Object obj = selection.getFirstElement();
            if (obj instanceof IProject || obj instanceof IFile) {
                // If current selection is a folder or model object, set local folder
                // to specified folder or containing folder.
                final IContainer folder = ModelUtil.getContainer(obj);
                try {
                    if (folder != null && folder.getProject().getNature(ModelerCore.NATURE_ID) != null) {
                        this.folder = folder;
                    }
                } catch (final CoreException err) {
                    util.log(err);
                    WidgetUtil.showError(err);
                }
            } else if (obj instanceof String && ((String)obj).equals("acsMode")) { //$NON-NLS-1$
                // Launched from ACS plugin via the LaunchXSDImporterAction class.
                // Set ACSMode flag and initial state so appropriate values are
                // selected by default when the Introduction page displays.
                //
                // Contents of incoming ACS structured selection:
                // [0] = "acsMode" (String) provided as sanity check
                // [1] = Integration Component name (String)
                // [2] = Business Method name (String)
                // [3] = Request schema (String) - for future use
                // [4] = Response schema (String)
                final List acsPreLoadValues = selection.toList();
                if ((String)acsPreLoadValues.get(0) == "acsMode") { //$NON-NLS-1$
                    String opaquePart = ((String)acsPreLoadValues.get(1)) + ":" + ((String)acsPreLoadValues.get(2)); //$NON-NLS-1$
                    manager.setACSMode(true);
                    // Catalog name must be set to the Business Method name.
                    manager.setCatalogType(CUSTOM_CATALOG_VAL);
                    manager.setCustomCatalogName((String)acsPreLoadValues.get(2));
                    // Create a generic URI that contains the actual schema string.
                    // This is then fairly easy for the schema processor to extract and parse.
                    URI acsURI = URI.createGenericURI(ACS_URI_SCHEME_RESPONSE, opaquePart, (String)acsPreLoadValues.get(4));
                    // The standard form of addSchema() is (file key, uri). We don't have a file as a key.
                    // Use static string instead.
                    manager.addSchema(ACS_URI_SCHEME_RESPONSE, acsURI);
                    // Lastly, set the default model name to something better than the namespace
                    // extracted from the schema: IntegrationComponentName_BusinessMethodName
                    // TODO: Validate name conforms to MM standards.
                    this.setModelName(((String)acsPreLoadValues.get(1)) + "_" + ((String)acsPreLoadValues.get(2))); //$NON-NLS-1$
                }
            }
        }

        importer = new JdbcImporter();

        introductionPage = new IntroductionPage(this);
        if (!manager.getACSMode()) { // if not launched by ACS, load page
            addPage(introductionPage);
        } else { // otherwise set the source type here and don't load page
            userSettings.setSourceType(StateManager.SOURCE_ACS);
        }

        responseSelectionPage = new ResponseSelectionPage(this, util);
        if (!manager.getACSMode()) {
            addPage(responseSelectionPage);
        }

        requestSelectionPage = new RequestSelectionPage(this, util);
        // addPage(requestSelectionPage);
        //
        rootElementsPage = new RootElementsPage(this);
        addPage(rootElementsPage);

        importObjectsPg = new JdbcImportObjectsPage();
        addPage(importObjectsPg);

        optionsPg = new JdbcImportOptionsPage();
        addPage(optionsPg);

        diffsPg = new JdbcShowDifferencesPage(this);
        addPage(diffsPg);

        // If updating, mark options page incomplete to force user to select update checkbox on page
        if (importer.getUpdatedModel() != null) {
            optionsPg.setPageComplete(false);
        }

        // Create listener for changes to SQL connection

        String wizardTitle = getString(WIZARD_TITLE);
        setWindowTitle(wizardTitle);
    }

    private void createState() {

        userSettings = new UserSettings(this);
        // For some reason, even though the defaults are specified in the preferences page
        // they do not become set unless the property page is opened once.
        // So we'll hack it and set them here.
        new PreferencePage();
        IPreferenceStore prefs = XmlImporterUiPlugin.getDefault().getPreferenceStore();
        int cThreshold = prefs.getInt(XmlImporterUiPlugin.C_threshold);
        int pThreshold = prefs.getInt(XmlImporterUiPlugin.P_threshold);
        int fThreshold = prefs.getInt(XmlImporterUiPlugin.F_threshold);
        userSettings.set_C_threshold(cThreshold);
        userSettings.set_P_threshold(pThreshold);
        userSettings.set_F_threshold(fThreshold);

        manager = new StateManager(userSettings, util);

        String requestTableLocalName = prefs.getString(XmlImporterUiPlugin.requestTable);
        userSettings.setRequestTableLocalName(requestTableLocalName);

        String mergedChildSep = prefs.getString(XmlImporterUiPlugin.mergedChildSep);
        userSettings.setMergedChildSep(mergedChildSep);
        
        String xsdLibrary = prefs.getString(XmlImporterUiPlugin.xsdLibrary);
        userSettings.setXsdLibrary(mergedChildSep);

        jdbcFactory = new JdbcFactoryImpl();
        jdbcConnection = new ConnectionImpl(manager, userSettings, this);
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public RelationshipProcessor getRelationshipProcessor() {
        return relationshipProcessor;
    }

    void setUseSchemaTypes( boolean useSchemaTypes ) {
        userSettings.setUseSchemaTypes(useSchemaTypes);
    }

    void schemaChanged( SchemaModel model ) {
        jdbcConnection.changed(); // reinitialize the JDBCMetadata
        try {
            String dbName = jdbcConnection.getCatalog();
            if (dbName == null || dbName.equals("")) { //$NON-NLS-1$
                Iterator iter = manager.getSchemaURIs().iterator();
                Object oFirstURI = iter.hasNext() ? manager.getSchemaURIs().iterator().next() : null;
                if (oFirstURI instanceof URI) {
                    URI firstURI = (URI)oFirstURI;
                    String lastSegment = firstURI.lastSegment();
                    int dot = lastSegment.indexOf('.');
                    if (dot > 0) {
                        dbName = lastSegment.substring(0, dot);
                    } else {
                        dbName = lastSegment;
                    }
                }
            }

            // getValidString returns null if the string you pass is valid, principle of most surprise.
            String validatedName = CoreValidationRulesUtil.getValidString(dbName, null, -1);
            if (null != validatedName) {
                dbName = validatedName;
            }

            JdbcDatabase db = new JdbcDatabaseImpl(jdbcConnection, dbName);
            setDatabase(db);

            JdbcSource source = jdbcFactory.createJdbcSource();
            source.setDriverClass(XmlImporterJdbcDriver.class.getName());
            source.setDriverName(XmlImporterJdbcDriver.class.getName());
            Includes includes = db.getIncludes();
            includes.setApproximateIndexes(false);
            includes.setIncludeForeignKeys(true);
            includes.setIncludeIndexes(false);
            includes.setIncludeProcedures(false);
            includes.setUniqueIndexesOnly(false);
            String[] tableTypeNames = DatabaseMetaDataImpl.getTableTypeNames();
            includes.setIncludedTableTypes(tableTypeNames);
            JdbcImportSettings importSettings = jdbcFactory.createJdbcImportSettings();
            importSettings.setIncludeApproximateIndexes(false);
            importSettings.setIncludeForeignKeys(true);
            importSettings.setIncludeIndexes(true);
            importSettings.setIncludeProcedures(false);
            importSettings.setIncludeUniqueIndexes(false);
            List tableTypes = importSettings.getIncludedTableTypes();
            for (int i = 0; i < tableTypeNames.length; ++i) {
                final String tableType = tableTypeNames[i];
                tableTypes.add(tableType);
            }
            source.setImportSettings(importSettings);
            source.setJdbcDriver(null);
            source.setJdbcSourceContainer(null);
            String location = null;
            StringBuffer url = new StringBuffer();
            url.append("xsd:"); //$NON-NLS-1$
            boolean first = true;
            for (Iterator iter = manager.getSchemaURIs().iterator(); iter.hasNext();) {
                Object o = iter.next();
                URI uri = (URI)o;
                // In the case of ACS/Librados, we overload the schema URI to hold the actual
                // full schema as a a string so it can be passed from the Librados plugin to the
                // import wizard. This results in a long, unfriendly URL that is meaningless in
                // the JDBCSource object. Thus if ACS, trim off the xml string (saved in the
                // fragment portion of the URI.
                if (manager.getACSMode()) {
                    location = uri.trimFragment().toString();
                } else {
                    location = uri.toString();
                }
                if (!first) {
                    url.append("&"); //$NON-NLS-1$
                }
                first = false;
                url.append(location);
            }
            source.setUrl(url.toString());
            source.setUsername(""); //$NON-NLS-1$
            setSource(source);
            processModelName(db);
        } catch (Exception err) {
            JdbcUiUtil.showError(err, COPY_ERROR_MESSAGE);
        }
    }

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(XsdAsRelationalImportWizard.class);

    private static final ImageDescriptor IMAGE = XmlImporterUiPlugin.getDefault().getImageDescriptor(IUiConstants.Images.IMPORT_DATABASE_ICON);

    PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    private String TITLE = getString("title"); //$NON-NLS-1$

    protected String COPY_ERROR_MESSAGE = getString("copyErrorMessage"); //$NON-NLS-1$

    private String IMPORT_ERROR_MESSAGE = getString("importErrorMessage"); //$NON-NLS-1$

    private static boolean importLicensed = false;

    /**
     * @since 4.0
     */
    private String getString( String id ) {
        return util.getString(I18N_PREFIX + id);
    }

    private JdbcImporter importer;

    private IContainer folder;

    private String modelName;

    IStatus status;

    DifferenceReport drDifferenceReport;

    ProcessorPack ppProcessorPack;

    public XsdAsRelationalImportWizard() {
        super(XmlImporterUiPlugin.getDefault(), "", IMAGE); //$NON-NLS-1$
        super.setWindowTitle(TITLE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        if (importLicensed) {
            super.createPageControls(pageContainer);
        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = false;

        /*
         * 'finish' will use the previously created 'processor' instead of
         * creating a fresh one, if one was previously created.
         */

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {

                    if (ppProcessorPack == null) {
                        // Has to happen before the Processor is created.
                        XPackage xPackage = getExtensionPackage(getFolder());

                        final JdbcSource src = getSource();
                        final RelationalModelProcessor processor = JdbcRelationalPlugin.createRelationalModelProcessor(src);
                        processor.setMoveRatherThanCopyAdds(!isUpdatedModel());

                        final IFile modelFile = getFolder().getFile(new Path(getModelName()));
                        final ModelResource resrc = ModelerCore.create(modelFile);

                        final ModelAnnotation modelAnnotation = resrc.getModelAnnotation();
                        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                        modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);

                        if (xPackage == null) {
                            util.log(IStatus.WARNING, util.getString("XsdAsRelationalImportWizard.nullXPackage")); //$NON-NLS-1$
                        } else {
                            modelAnnotation.setExtensionPackage(xPackage);
                        }

                        if (resrc instanceof ModelResourceImpl) {
                            ((ModelResourceImpl)resrc).setModelType(ModelType.PHYSICAL_LITERAL);
                        }

                        XsdAsRelationalImportWizard.this.status = processor.execute(resrc,
                                                                                    getDatabase(),
                                                                                    src.getImportSettings(),
                                                                                    monitor);

                        // capture objects in the processor pack
                        ppProcessorPack = new ProcessorPack(processor, src, modelFile, resrc);
                    } else {
                        /*
                         * handle the case where we have already created a
                         * processor, allowed the user to modify the Diff
                         * Report, etc. ppProcessorPack The processor has
                         * special code inside its 'performMerge' method
                         * that uses the existing DifferenceProcessor if one
                         * exists (meaning it had been created by the
                         * 'generateProcessor(?)' method...So all we need to
                         * do here is to call 'execute' using the
                         * preexisting RelationalModelProcessor.
                         *  
                         */
                        XsdAsRelationalImportWizard.this.status = ppProcessorPack.getProcessor().execute(ppProcessorPack.getModelResource(),
                                                                                                         getDatabase(),
                                                                                                         ppProcessorPack.getJdbcSource().getImportSettings(),
                                                                                                         monitor);
                    }

                    // cleanup
                    if (XsdAsRelationalImportWizard.this.status.getSeverity() != IStatus.ERROR) {
                        // Remove the old source setting in the model, in
                        // case this is an update
                        // The loop below assumes that only one source
                        // setting can exist for a given model
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
                        // Auto save the model & refresh
                        ppProcessorPack.getModelResource().save(monitor, true);
                        getFolder().refreshLocal(IResource.DEPTH_INFINITE, monitor);

                        ModelEditorManager.activate(ppProcessorPack.getModelFile(), true);
                    }
                } catch (final OperationCanceledException err) {
                    // do nothing in particular
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                    ppProcessorPack = null;
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
                    util.log(this.status);
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

    protected XPackage getExtensionPackage( IContainer folder ) throws ModelerCoreException {
        extManager = getExtension();
        extManager.loadModelExtensions(folder, new NullProgressMonitor());
        return extManager.getPackage();
    }

    protected BaseXMLRelationalExtensionManager getExtension() {
        BaseXMLRelationalExtensionManager modelExtension;
        if (StateManager.SOURCE_DOCUMENT == userSettings.getSourceType()) {
            modelExtension = new XMLFileExtensionManager();
        } else {
            modelExtension = new XMLHTTPExtensionManager();
        }
        return modelExtension;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean bCanFinish = false;

        boolean bUpdateSelected = false;

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

        bCanFinish = super.canFinish() && getDatabase() != null && getSource() != null && getFolder() != null
                     && getModelName() != null && !bUpdateStepsPending;

        return bCanFinish;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        if (importLicensed) {
            try {
                this.importer.disconnect();
            } catch (final SQLException err) {
                JdbcUiUtil.showAccessError(err);
            }
        }
        super.dispose();
    }

    protected void setSource( JdbcSource source ) {
        this.importer.setSource(source);
    }

    protected void setDatabase( JdbcDatabase database ) {
        this.importer.setDatabase(database);
    }

    /**
     * @since 4.0
     */
    JdbcSource getSource() {
        return this.importer.getSource();
    }

    /**
     * @since 4.0
     */
    JdbcDatabase getDatabase() {
        return this.importer.getDatabase();
    }

    /**
     * @since 4.0
     */
    IContainer getFolder() {
        return this.folder;
    }

    /**
     * @since 4.0
     */
    String getModelName() {
        return this.modelName;
    }

    /**
     * @since 4.0
     */
    boolean isUpdatedModel() {
        return (this.importer.getUpdatedModel() != null);
    }

    /**
     * @since 4.0
     */
    void setFolder( final IContainer folder ) {
        ArgCheck.isNotNull(folder);
        this.folder = folder;
    }

    /**
     * @since 4.0
     */
    void setModelName( final String name ) {
        ArgCheck.isNotEmpty(name);
        this.modelName = name;
    }

    public DifferenceReport getDifferenceReport() {
        if (drDifferenceReport == null) {

            // Save object selections from previous page
            final IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        final JdbcSource src = getSource();
                        final RelationalModelProcessor processor = JdbcRelationalPlugin.createRelationalModelProcessor(src);
                        processor.setMoveRatherThanCopyAdds(!isUpdatedModel());

                        final IFile modelFile = getFolder().getFile(new Path(getModelName()));
                        final ModelResource resrc = ModelerCore.create(modelFile);

                        final ModelAnnotation modelAnnotation = resrc.getModelAnnotation();
                        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                        modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);

                        if (resrc instanceof ModelResourceImpl) {
                            ((ModelResourceImpl)resrc).setModelType(ModelType.PHYSICAL_LITERAL);
                        }
                        if (processor instanceof RelationalModelProcessorImpl) {
                            XsdAsRelationalImportWizard.this.drDifferenceReport = ((RelationalModelProcessorImpl)processor).generateDifferenceReport(resrc,
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

        return drDifferenceReport;
    }

    protected void processModelName( final JdbcDatabase db ) throws ModelWorkspaceException {
        if (getModelName() == null) {
            String modelName = CoreValidationRulesUtil.getValidString(db.getName(), null, -1);
            if (modelName == null) {
                modelName = db.getName();
            }
            modelName = FileUtils.toFileNameWithExtension(modelName, ModelerCore.MODEL_FILE_EXTENSION);
            if (!StringUtil.isEmpty(modelName) && ResourcesPlugin.getWorkspace().validateName(modelName, IResource.FILE).isOK()) {
                setModelName(modelName);
                if (this.folder != null) {
                    final IResource resrc = this.folder.findMember(modelName);
                    if (resrc != null) {
                        final ModelResource model = JdbcRelationalUtil.getPhysicalModifiableRelationalModel(resrc);
                        if (model != null) {
                            this.importer.setUpdatedModel(model);
                        }
                    }
                }
            }
        }
    }

    public StateManager getStateManager() {
        return manager;
    }

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
