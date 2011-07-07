/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.mapping.Mapping;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.aspects.validation.rules.RelationalStringNameRule;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceProcessor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.MergeProcessor;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.util.CompareUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.jdbc.relational.ContextImpl;
import com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.SourceNames;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.data.Results;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcProcedure;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.metadata.JdbcTableType;
import com.metamatrix.modeler.jdbc.metadata.impl.GetColumnsRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetExportedForeignKeysRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetImportedForeignKeysRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetIndexesRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetPrimaryKeyRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetProcedureParametersRequest;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;
import com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor;

/**
 * RelationalModelProcessorImpl
 */
public class RelationalModelProcessorImpl implements ModelerJdbcRelationalConstants, RelationalModelProcessor {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalModelProcessorImpl.class);

    private static final String IMPORT_DESCRIPTION = getString("importDescription"); //$NON-NLS-1$

    public static final int IMPORT_WITH_NO_PROBLEMS = 3001;
    public static final int IMPORT_WITH_WARNINGS = 3002;
    public static final int IMPORT_WITH_ERRORS = 3003;
    public static final int IMPORT_WITH_WARNINGS_AND_ERRORS = 3004;
    public static final int IMPORT_WITH_NO_WARNINGS_AND_ERRORS = 3005;
    public static final int ERROR_PROCESSING_NODE = 3006;

    private static final int UNITS_PHASE_0 = 10;
    private static final int UNITS_PHASE_1 = 1000;
    private static final int UNITS_PHASE_2 = 1000;
    private static final int UNITS_PHASE_3 = 10000;
    private static final int UNITS_PHASE_4 = 10;
    private static final int UNITS_PHASE_5 = 10000;
    private static final int UNITS_PHASE_6 = 1000;
    private static final int UNITS_PHASE_7 = 1000;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private final RelationalFactory factory;

    private boolean verbose = false;
    private RelationalTypeMapping typeMapping;
    private DatatypeManager datatypeManager;
    DifferenceProcessor diffProc;
    DifferenceReport drDifferenceReport;
    private boolean moveRatherThanCopyAdds;
    private boolean debugTimingEnabled = false;

    /**
     * Construct an instance of RelationalModelProcessorImpl.
     */
    public RelationalModelProcessorImpl() {
        this(RelationalFactory.eINSTANCE, null);
    }

    public RelationalModelProcessorImpl( final RelationalFactory factory ) {
        this(factory, null);
    }

    public RelationalModelProcessorImpl( final RelationalFactory factory,
                                         final RelationalTypeMapping mapping ) {
        super();
        CoreArgCheck.isNotNull(factory);
        this.factory = factory;
        this.typeMapping = mapping;
    }

    protected class WorkingArea {
        private final URI uri;
        private final EmfResource resource;
        private final Context context;

        protected WorkingArea( final URI importUri,
                               final JdbcDatabase jdbcDatabase,
                               final JdbcImportSettings settings,
                               final IProgressMonitor monitor ) {
            this.uri = importUri;
            this.resource = new MtkXmiResourceImpl(this.uri);
            ResourceSet container = new ResourceSetImpl();
            container.getResources().add(this.resource);
            this.context = new ContextImpl(this.resource, this.resource.getModelContents(), jdbcDatabase, settings, monitor);

            // BML Defect 19640: For now, we need to make sure that the ModelAnnotation exists and the primary metamodel uri
            // and ModelType are set to relational. Later on, we should probably pass this in on the constructor or extract it
            // from the ModelResource. createAnnotation() requires that the ModelType is one that supports annotations.
            // ModelType == UNKNOWN does not support annotations (see ModelResourceContainerFactory.supportsAnnotations())
            ModelAnnotation annotation = this.resource.getModelAnnotation();
            annotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            annotation.setModelType(ModelType.PHYSICAL_LITERAL);
        }

        public ModelContents getModelContents() {
            return this.resource.getModelContents();
        }

        public Resource getResource() {
            return resource;
        }

        public URI getUri() {
            return uri;
        }

        public Context getContext() {
            return context;
        }

    }

    void stopLogIncrementAndRestart( Stopwatch watch,
                                     String message ) {
        if (debugTimingEnabled) watch.stopLogIncrementAndRestart(message);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.RelationalModelProcessor#execute(org.eclipse.emf.ecore.resource.Resource,
     *      com.metamatrix.modeler.jdbc.metadata.JdbcDatabase, com.metamatrix.modeler.jdbc.metadata.JdbcNode[],
     *      com.metamatrix.modeler.jdbc.JdbcImportSettings, org.eclipse.core.runtime.IProgressMonitor)
     */
    public final IStatus execute( final Resource modelResource,
                                  final JdbcDatabase jdbcDatabase,
                                  final JdbcImportSettings settings,
                                  final IProgressMonitor monitor ) {
        return execute(modelResource, null, jdbcDatabase, settings, monitor);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.RelationalModelProcessor#execute(org.eclipse.emf.ecore.resource.Resource,
     *      com.metamatrix.modeler.jdbc.metadata.JdbcDatabase, com.metamatrix.modeler.jdbc.metadata.JdbcNode[],
     *      com.metamatrix.modeler.jdbc.JdbcImportSettings, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.4
     */
    public final IStatus execute( final Resource modelResource,
                                  final Container container,
                                  final JdbcDatabase jdbcDatabase,
                                  final JdbcImportSettings settings,
                                  final IProgressMonitor monitor ) {

        Stopwatch totalWatch = new Stopwatch();
        totalWatch.start();
        Stopwatch sWatch = new Stopwatch();
        sWatch.start();
        // Create the ModelContents wrapper/utility ...
        final ModelContents modelContents = modelResource instanceof EmfResource ? ((EmfResource)modelResource).getModelContents() : new ModelContents(
                                                                                                                                                       modelResource);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Got/Created Model Contents - Delta "); //$NON-NLS-1$

        // Set up the context ...
        final Context context = new ContextImpl(modelResource, modelContents, jdbcDatabase, settings, monitor);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Created ContextImpl - Delta "); //$NON-NLS-1$

        // Create the working area into which the imported objects will be placed prior to merging
        final URI uri = modelResource.getURI();
        final WorkingArea workingArea = new WorkingArea(uri, jdbcDatabase, settings, monitor);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Created Working Area - Delta "); //$NON-NLS-1$

        // And execute within the context of a transaction
        IStatus iStatus = executeWithinTransaction(context, workingArea, container);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Executing ContextImpl - Delta "); //$NON-NLS-1$

        stopLogIncrementAndRestart(totalWatch, " RelationalModelProcessor:  Execute() Total - Delta "); //$NON-NLS-1$
        return iStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.RelationalModelProcessor#execute(com.metamatrix.modeler.core.workspace.ModelResource, com.metamatrix.modeler.jdbc.metadata.JdbcDatabase, com.metamatrix.modeler.jdbc.metadata.JdbcNode[], com.metamatrix.modeler.jdbc.JdbcImportSettings, org.eclipse.core.runtime.IProgressMonitor)
     */
    public final IStatus execute( final ModelResource modelResource,
                                  final JdbcDatabase jdbcDatabase,
                                  final JdbcImportSettings settings,
                                  final IProgressMonitor monitor ) throws ModelWorkspaceException {
        Stopwatch totalWatch = new Stopwatch();
        totalWatch.start();
        Stopwatch sWatch = new Stopwatch();
        sWatch.start();
        // Get the ModelContents wrapper/utility from the resource ...
        final Resource emfResource = modelResource.getEmfResource();
        final ModelContents modelContents = ModelContents.getModelContents(modelResource);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Got/Created Model Contents - Delta "); //$NON-NLS-1$

        // Set up the context ...
        final Context context = new ContextImpl(emfResource, modelContents, jdbcDatabase, settings, monitor);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Created ContextImpl - Delta "); //$NON-NLS-1$

        // Create the working area into which the imported objects will be placed prior to merging
        final URI uri = modelResource.getEmfResource().getURI();
        final WorkingArea workingArea = new WorkingArea(uri, jdbcDatabase, settings, monitor);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Created Working Area - Delta "); //$NON-NLS-1$

        // And execute within the context of a transaction
        IStatus iStatus = executeWithinTransaction(context, workingArea);

        stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  Executing ContextImpl - Delta "); //$NON-NLS-1$

        stopLogIncrementAndRestart(totalWatch, " RelationalModelProcessor:  Execute() Total - Delta "); //$NON-NLS-1$
        return iStatus;
    }

    protected IStatus executeWithinTransaction( final Context context,
                                                final WorkingArea workingArea ) {
        return executeWithinTransaction(context, workingArea, null);
    }

    /**
     * @since 4.4
     */
    protected IStatus executeWithinTransaction( final Context context,
                                                final WorkingArea workingArea,
                                                final Container container ) {
        final List problems = new LinkedList();

        // Perform the execute within a transaction ...
        try {
            final TransactionRunnable op = new TransactionRunnable() {
                public Object run( UnitOfWork uow ) throws ModelerCoreException {
                    Stopwatch sWatch = new Stopwatch();
                    sWatch.start();
                    performExecute(workingArea.getContext(), problems);
                    stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  performExecute() - Delta "); //$NON-NLS-1$
                    performMerge(context, workingArea, problems);
                    stopLogIncrementAndRestart(sWatch, " RelationalModelProcessor:  performMerge() - Delta "); //$NON-NLS-1$
                    return null;
                }
            };
            ModelerCore.getModelEditor().executeAsTransaction(op, container, IMPORT_DESCRIPTION, true, this);
        } catch (final ModelerCoreRuntimeException e) {
            if (e.getChild() instanceof UserCancelledException) {
                // do nothing - the user cancelled
            } else {
                final int code = 0;
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Problem_while_importing"); //$NON-NLS-1$
                final IStatus problem = new Status(
                                                   IStatus.ERROR,
                                                   com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                   code, msg, e);
                problems.add(problem);
            }
        } catch (final ModelerCoreException err) {
            final int code = 0;
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Problem_while_importing"); //$NON-NLS-1$
            final IStatus problem = new Status(IStatus.ERROR,
                                               com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                               code, msg, err);
            problems.add(problem);
        }

        // Put all of the problems into a single IStatus ...
        final String PLUGINID = com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID;
        IStatus resultStatus = null;
        if (problems.isEmpty()) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Import_into_relational_model_completed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, PLUGINID, IMPORT_WITH_NO_PROBLEMS, msg, null);
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
                final Object[] params = new Object[] {new Integer(numWarnings)};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Completed_import_with_warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, IMPORT_WITH_WARNINGS, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                final Object[] params = new Object[] {new Integer(numErrors)};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Import_resulted_in_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, IMPORT_WITH_ERRORS, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Import_resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, IMPORT_WITH_WARNINGS_AND_ERRORS, statusArray, msg, null);
            } else {
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Completed_import_with_no_warnings_or_errors"); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, IMPORT_WITH_NO_WARNINGS_AND_ERRORS, statusArray, msg, null);
            }
        }

        return resultStatus;
    }

    /* (non-Javadoc)
    * @see com.metamatrix.modeler.jdbc.RelationalModelProcessor#execute(org.eclipse.emf.ecore.resource.Resource, com.metamatrix.modeler.jdbc.metadata.JdbcDatabase, com.metamatrix.modeler.jdbc.metadata.JdbcNode[], com.metamatrix.modeler.jdbc.JdbcImportSettings, org.eclipse.core.runtime.IProgressMonitor)
    */
    public DifferenceReport generateDifferenceReport( final ModelResource modelResource,
                                                      final JdbcDatabase jdbcDatabase,
                                                      final JdbcImportSettings settings,
                                                      final IProgressMonitor monitor ) throws ModelWorkspaceException {

        /*
         * jh: adapted from execute/'throws'
         */

        final List problems = new LinkedList();

        // Get the ModelContents wrapper/utility from the resource ...
        final Resource emfResource = modelResource.getEmfResource();
        final ModelContents modelContents = ModelContents.getModelContents(modelResource);

        // Set up the context ...
        final Context context = new ContextImpl(emfResource, modelContents, jdbcDatabase, settings, monitor);
        // Create the working area into which the imported objects will be placed prior to merging
        final URI uri = modelResource.getEmfResource().getURI();

        final WorkingArea workingArea = new WorkingArea(uri, jdbcDatabase, settings, monitor);

        // And execute within the context of a transaction
        createDifferenceReport(context, workingArea, problems);
        return getDifferenceReport();
    }

    private DifferenceReport createDifferenceReport( final Context context,
                                                     final WorkingArea workingArea,
                                                     final List problems ) {
        /*
         * jh: adapted from executeWithTransaction and performMerge
         */

        // Perform the execute within a transaction ...
        try {
            final TransactionRunnable op = new TransactionRunnable() {
                public Object run( UnitOfWork uow ) {
                    performExecute(workingArea.getContext(), problems);

                    /*
                     * jh: code adapted from performMerge:
                     */
                    final Resource startingResource = context.getResource();
                    final Resource endingResource = workingArea.getResource();
                    final IProgressMonitor monitor = context.getProgressMonitor();

                    // Execute the difference analysis ...
                    diffProc = ModelerComparePlugin.createDifferenceProcessor(startingResource, endingResource);
                    final IStatus diffStatus = diffProc.execute(monitor);
                    problems.add(diffStatus);

                    // If there was NOT an error in the difference processing ...
                    if (diffStatus.getSeverity() != IStatus.ERROR) {
                        // Prepare for the merge ...
                        drDifferenceReport = diffProc.getDifferenceReport();
                        preProcessDifferenceReport(drDifferenceReport, context, workingArea, problems);
                    }
                    return null;
                }
            };
            ModelerCore.getModelEditor().executeAsTransaction(op, IMPORT_DESCRIPTION, true, this);
        } catch (final ModelerCoreRuntimeException e) {
            if (e.getChild() instanceof UserCancelledException) {
                // do nothing - the user cancelled
            } else {
                final int code = 0;
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Problem_while_importing"); //$NON-NLS-1$
                final IStatus problem = new Status(
                                                   IStatus.ERROR,
                                                   com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                   code, msg, e);
                problems.add(problem);
            }
        } catch (final ModelerCoreException err) {
            final int code = 0;
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Problem_while_importing"); //$NON-NLS-1$
            final IStatus problem = new Status(IStatus.ERROR,
                                               com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                               code, msg, err);
            problems.add(problem);
        }

        return drDifferenceReport;
    }

    public DifferenceReport getDifferenceReport() {
        return drDifferenceReport;
    }

    private DifferenceProcessor getDifferenceProcessor() {
        return diffProc;
    }

    /**
     * Merge into the context all of the content that has been imported into the working area.
     * 
     * @param context the import context (i.e., actual model) into which the metadata should be imported; never null
     * @param workingArea the transient area into which the metadata has already been imported; never null
     * @param problem the list of {@link IStatus problems and statues} resulting from the execution
     */
    protected void performMerge( final Context context,
                                 final WorkingArea workingArea,
                                 final List problems ) throws ModelerCoreException {

        if (getDifferenceProcessor() == null) {

            final Resource startingResource = context.getResource();
            final Resource endingResource = workingArea.getResource();
            final IProgressMonitor monitor = context.getProgressMonitor();

            // Execute the difference analysis ...
            diffProc = ModelerComparePlugin.createDifferenceProcessor(startingResource, endingResource);
            final IStatus diffStatus = diffProc.execute(monitor);
            problems.add(diffStatus);

            // If there was NOT an error in the difference processing ...
            if (diffStatus.getSeverity() != IStatus.ERROR) {
                // Prepare for the merge ...
                final DifferenceReport differences = diffProc.getDifferenceReport();
                preProcessDifferenceReport(differences, context, workingArea, problems);
            }

        } else {
            // the difference processor was already created in createDifferenceReport():
            diffProc = getDifferenceProcessor();
        }

        // Execute the merge ...
        final IProgressMonitor monitor = context.getProgressMonitor();
        final EObject[] externalReferences = getDatatypeManager().getAllDatatypes();
        // Move objects from the source when "added" to the result ...
        EmfResource eResource = (EmfResource)context.getResource();
        ContainerImpl ctnr = (ContainerImpl)eResource.getContainer();
        ModelEditorImpl.setContainer(ctnr);
        final MergeProcessor mergeProc = ModelerComparePlugin.createMergeProcessor(diffProc,
                                                                                   externalReferences,
                                                                                   this.moveRatherThanCopyAdds);
        final IStatus mergeStatus = mergeProc.execute(monitor);
        problems.add(mergeStatus);

    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor#setMoveRatherThanCopyAdds(boolean)
     * @since 4.3
     */
    public void setMoveRatherThanCopyAdds( final boolean moveRatherThanCopyAdds ) {
        this.moveRatherThanCopyAdds = moveRatherThanCopyAdds;
    }

    /**
     * Perform any pre-processing of the difference report prior to {@link #performMerge(Context, WorkingArea, List) merging}.
     * 
     * @param differences the difference report; never null
     * @param context the import context (i.e., actual model) into which the metadata should be imported; never null
     * @param workingArea the transient area into which the metadata has already been imported; never null
     * @param problem the list of {@link IStatus problems and statues} resulting from the execution
     */
    protected void preProcessDifferenceReport( final DifferenceReport differences,
                                               final Context context,
                                               final WorkingArea workingArea,
                                               final List problems ) {
        CompareUtil.skipDeletesOfStandardContainers(differences);
        CompareUtil.skipDeletesOfModelImports(differences);
        skipDeletesOfJdbcSource(differences);
        skipChanges(differences);
    }

    /**
     * This method is responsible for walking through the difference report and marking to be skipped those changes to Column
     * property values that are not accurately determined via the JDBC importer. For example, JDBC DatabaseMetaData does not
     * provide any access to the 'minimum value' of a column, so the Column:minimumValue property should never be changed on a
     * model during refresh/update.
     * <p>
     * This method really only processes <i>changes</i> to existing columns. Any time columns are added, the user will not have
     * had a chance to modify anything. Any time a column is deleted, it doesn't matter what the user had modified.
     * </p>
     * 
     * @param differences the difference report
     */
    protected void skipChanges( DifferenceReport differences ) {
        // Walk the difference report's Mapping objects looking for changes ...
        final Mapping rootMapping = differences.getMapping();
        skipChanges(rootMapping);
    }

    protected void skipChanges( Mapping mapping ) {
        // Process the mapping ...
        final DifferenceDescriptor descriptor = CompareUtil.getDifferenceDescriptor(mapping);
        boolean recursive = true;
        if (descriptor != null) {
            if (descriptor.isChanged()) {
                // Get the input object (a change should have 1 input and 1 output) ...
                final List inputs = mapping.getInputs();
                if (inputs.size() == 1) {
                    // Look at the change ...
                    skipChanges(descriptor, (EObject)inputs.get(0));
                }
                recursive = false;
            } else if (descriptor.isAddition()) {
                // nothing to do on or below an addition ...
                recursive = false;
            } else if (descriptor.isDeletion()) {
                // nothing to do on or below an deletion ...
                recursive = false;
            }
        }

        if (recursive) {
            // Walk the difference report's Mapping objects looking for changes ...
            final List nestedMappings = mapping.getNested();
            final Iterator iter = nestedMappings.iterator();
            while (iter.hasNext()) {
                final Mapping nestedMapping = (Mapping)iter.next();
                // Call this method recursively ...
                skipChanges(nestedMapping);
            }
        }
    }

    protected void skipChanges( final DifferenceDescriptor changeDescriptor,
                                final EObject inputObject ) {
        if (inputObject instanceof Column) {
            final List propertyDiffs = changeDescriptor.getPropertyDifferences();
            final Iterator iter = propertyDiffs.iterator();
            while (iter.hasNext()) {
                final PropertyDifference propDiff = (PropertyDifference)iter.next();
                final EStructuralFeature columnFeature = propDiff.getAffectedFeature();
                final int columnFeatureId = columnFeature.getFeatureID();
                switch (columnFeatureId) {
                    // No values for these features can be determined from JDBC DatabaseMetaData,
                    // so we never should change any value on an existing column
                    case (RelationalPackage.COLUMN__AUTO_INCREMENTED):
                    case (RelationalPackage.COLUMN__CHARACTER_SET_NAME):
                    case (RelationalPackage.COLUMN__COLLATION_NAME):
                    case (RelationalPackage.COLUMN__CURRENCY):
                    case (RelationalPackage.COLUMN__FORMAT):
                    case (RelationalPackage.COLUMN__MAXIMUM_VALUE):
                    case (RelationalPackage.COLUMN__MINIMUM_VALUE):
                    case (RelationalPackage.COLUMN__SELECTABLE):
                    case (RelationalPackage.COLUMN__SIGNED):
                    case (RelationalPackage.COLUMN__UPDATEABLE):
                        propDiff.setSkip(true);
                        break;
                    // Searchability is determined through JDBC DatabaseMetaData, but we should
                    // not change an existing column's searchability (and therefore whether
                    // it's case sensitive, since that's determined 100% by the searchability)
                    // See defect 14403
                    case (RelationalPackage.COLUMN__CASE_SENSITIVE):
                    case (RelationalPackage.COLUMN__SEARCHABILITY):
                        propDiff.setSkip(true);
                        break;
                }
            }
        }
    }

    protected void skipDeletesOfJdbcSource( final DifferenceReport differences ) {
        // Iterate through the mappings of root-level objects
        final Mapping rootMapping = differences.getMapping();
        final List nestedMappings = rootMapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            if (CompareUtil.hasInstanceof(nestedMapping, JdbcSource.class)) {
                // Always skip anything to do with a JDBC source object (the wizard will fix it) ...
                final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                desc.setSkip(true);
            }
        }
    }

/**
     * The primary implementation of both the
     * {@link #execute(ModelResource, JdbcDatabase, JdbcNode[], JdbcImportSettings, IProgressMonitor)} and
     * {@link #execute(Resource, JdbcDatabase, JdbcNode[], JdbcImportSettings, IProgressMonitor) methods.
     * This method can be overridden by specializations to actually perform the work of the changing the
     * supplied model
     * @param context the import context into which the metadata should be imported; never null
     * @param problem the list of {@link IStatus problems and statues} resulting from the execution
     */
    protected void performExecute( final Context context,
                                   final List problems ) {

        // Start the progress monitor tasks ...
        final Object[] taskNameParams = new Object[] {context.getJdbcDatabase().getName()};
        final String taskName = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Progress_main_task_name", taskNameParams); //$NON-NLS-1$
        final int numUnits = UNITS_PHASE_0 + UNITS_PHASE_1 + UNITS_PHASE_2 + UNITS_PHASE_3 + UNITS_PHASE_4 + UNITS_PHASE_5
                             + UNITS_PHASE_6 + UNITS_PHASE_7;
        final IProgressMonitor monitor = context.getProgressMonitor();
        monitor.beginTask(null, numUnits);
        monitor.setTaskName(taskName);

        try {
            // First check if cancelled
            if (monitor.isCanceled()) {
                throw new UserCancelledException();
            }

            // -----------------------------------------------------------------------------
            // Phase 0:
            // -----------------------------------------------------------------------------
            monitor.worked(UNITS_PHASE_0);

            // ----------------------------------------------------------------------------------------
            // Phase 1: Build the structure of the model that identifies the JdbcNodes that will be
            // the root objects in the model and the JdbcNodes that are the children of
            // other JdbcNodes
            // ----------------------------------------------------------------------------------------
            if (this.isVerboseLogging()) {
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Analyzing_structure_of_database"); //$NON-NLS-1$
                ModelerJdbcRelationalConstants.Util.log(IStatus.INFO, msg);
            }
            final JdbcModelStructure modelStructure = JdbcModelStructure.build(context);
            if (this.isVerboseLogging()) {
                final StringBuffer sb = new StringBuffer();
                modelStructure.print(sb);
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Completed_analysis._The_following_structure_to_be_put_into_model"); //$NON-NLS-1$
                ModelerJdbcRelationalConstants.Util.log(IStatus.INFO, msg + sb.toString());
            }
            monitor.worked(UNITS_PHASE_1);

            // ----------------------------------------------------------------------------------------
            // Phase 2: Starting at the root of this structure (null), get the JdbcNodes that are
            // the children and find the RelationalEntity objects in the existing model
            // that best match those nodes. Continue this process recursively for each
            // JdbcNode child in the structure for which there is a match
            // ----------------------------------------------------------------------------------------
            final Map nodesToModelObjects = new HashMap();
            final ObjectMatcher matcher = new ObjectMatcher(nodesToModelObjects);
            final List rootModelObjects = context.getModelContents().getEObjects();
            // Get the root JdbcNodes ...
            List rootNodes = modelStructure.getChildren(null);
            if (rootNodes == null) {
                rootNodes = Collections.EMPTY_LIST;
            }
            matchChildren(null, rootNodes, rootModelObjects, modelStructure, matcher, monitor);
            monitor.worked(UNITS_PHASE_2);
            final int totalNumTablesAndProcedures = modelStructure.getTotalTablesAndProceduresCount();

            // ----------------------------------------------------------------------------------------
            // Phase 2a: Check for the existance of a RelationalTypeMapping object.
            // ----------------------------------------------------------------------------------------
            if (this.typeMapping == null) {
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.NoDatatypeManager"); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.WARNING,
                                                  com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                  0, msg, null);
                problems.add(status);
            }
            if (this.getDatatypeManager() == null) {
                try {
                    // Only care about built-in datatypes, so use the Workspace DT Mgr ...
                    final DatatypeManager mgr = ModelerCore.getWorkspaceDatatypeManager();
                    this.setDatatypeManager(mgr);
                } catch (ModelerCoreRuntimeException e) {
                    final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Unable_to_find_the_builtin_Datatype_Manager"); //$NON-NLS-1$
                    final IStatus status = new Status(
                                                      IStatus.WARNING,
                                                      com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                      0, msg, null);
                    problems.add(status);
                }
            }

            // ----------------------------------------------------------------------------------------
            // Phase 3: Create all the new RelationalEntity objects for which there were no matches.
            // Foreign keys are not created at this point, since they require references
            // between tables (which may have not yet been created when the foreign keys
            // are to be created).
            // ----------------------------------------------------------------------------------------

            final List newTableObjects = new ArrayList();
            final int unitsPerModelObject = totalNumTablesAndProcedures != 0 ? UNITS_PHASE_3 / totalNumTablesAndProcedures : 0;
            if (matcher.getUnmatchedJdbcNodes().size() == 0) {
                monitor.worked(UNITS_PHASE_3);
            }
            int counter = 0;
            final Iterator iter = matcher.getUnmatchedJdbcNodes().iterator();
            while (iter.hasNext()) {
                // First check if cancelled
                if (monitor.isCanceled()) {
                    throw new UserCancelledException();
                }

                final JdbcNode unmatchedNode = (JdbcNode)iter.next();
                boolean countWork = unmatchedNode instanceof JdbcTable || unmatchedNode instanceof JdbcProcedure;
                if (countWork) {
                    ++counter;
                }

                // Get the parent of the new model object ...
                final JdbcNode parentNode = modelStructure.getParent(unmatchedNode);
                RelationalEntity parent = null;
                if (parentNode != null) {
                    // Find the existing(!) model object that corresponds to the parent
                    parent = (RelationalEntity)nodesToModelObjects.get(parentNode);
                    CoreArgCheck.isNotNull(parent);
                }

                // Create the real object ...
                final RelationalEntity newModelObject = createNewObject(unmatchedNode,
                                                                        parent,
                                                                        modelStructure,
                                                                        nodesToModelObjects,
                                                                        context,
                                                                        totalNumTablesAndProcedures,
                                                                        unitsPerModelObject,
                                                                        counter,
                                                                        monitor,
                                                                        problems,
                                                                        newTableObjects);
                if (newModelObject == null) {
                    continue;
                }

                // If the parent node was null ...
                if (parentNode == null) {
                    // Then the new object is a root-level object ...
                    context.getResource().getContents().add(newModelObject);
                }
                // if ( countWork ) {
                // monitor.worked(unitsPerModelObject);
                // }
            }

            // ----------------------------------------------------------------------------------------
            // Phase 4: Change all of the existing RelationalEntity objects for which a matching
            // JdbcNode was found. This phase updates these model objects to synchronize
            // their state with what is in the database. This is a recursive operation.
            // ----------------------------------------------------------------------------------------
            // final int numNewModelObjects = matcher.get().size();
            // final int unitsPerModelObject = numNewModelObjects != 0 ? UNITS_PHASE_3/numNewModelObjects : 0;
            // if ( numNewModelObjects == 0 ) {
            // monitor.worked(UNITS_PHASE_3);
            // }
            // counter = 0;
            // final Iterator iter = matcher.getUnmatchedJdbcNodes().iterator();
            // while (iter.hasNext()) {
            // // First check if cancelled
            // if ( monitor.isCanceled() ) {
            // throw new UserCancelledException();
            // }
            //
            // ++counter;
            // // final JdbcNode unmatchedNode = (JdbcNode)iter.next();
            // //
            // // // Get the parent of the new model object ...
            // // final JdbcNode parentNode = modelStructure.getParent(unmatchedNode);
            // // RelationalEntity parent = null;
            // // if ( parentNode != null ) {
            // // // Find the existing(!) model object that corresponds to the parent
            // // parent = (RelationalEntity)nodesToModelObjects.get(parentNode);
            // // Assertion.isNotNull(parent);
            // // }
            // //
            // // // Create the real object ...
            // // final RelationalEntity newModelObject = createNewObject(unmatchedNode,parent,
            // // modelStructure,nodesToModelObjects,
            // // context,numNewModelObjects,counter,
            // // monitor,problems,newModelObjects);
            // // if ( newModelObject == null ) {
            // // continue;
            // // }
            // //
            // // // If the parent node was null ...
            // // if ( parentNode == null ) {
            // // // Then the new object is a root-level object ...
            // // context.getResource().getContents().add(newModelObject);
            // // }
            // // monitor.worked(unitsPerModelObject);
            // }

            // ----------------------------------------------------------------------------------------
            // Phase 5: Insert all of the foreign keys between the new model objects that were
            // either created in Phase 3 or updated in Phase 4.
            // ----------------------------------------------------------------------------------------
            if (context.getJdbcDatabase().getIncludes().includeForeignKeys()) {
                final int numTables = newTableObjects.size();
                if (numTables != 0) {
                    final int unitsPerForeignKey = UNITS_PHASE_5 / numTables;
                    int count = 1;
                    final Integer totalCount = new Integer(numTables);
                    final Iterator iterator = newTableObjects.iterator();
                    while (iterator.hasNext()) {
                        final JdbcTable tableNode = (JdbcTable)iterator.next();
                        // First check if cancelled
                        if (monitor.isCanceled()) {
                            throw new UserCancelledException();
                        }
                        final RelationalEntity entity = (RelationalEntity)nodesToModelObjects.get(tableNode);
                        final Object[] tabParams = new Object[] {tableNode.getName(), totalCount, new Integer(count)};
                        String subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Creating_foreign_keys", tabParams); //$NON-NLS-1$
                        monitor.subTask(subtaskMsg);
                        if (entity instanceof BaseTable) {
                            createForeignKey(tableNode, (BaseTable)entity, context, nodesToModelObjects, problems);
                        }
                        ++count;
                        monitor.worked(unitsPerForeignKey);
                    }
                }
            } else {
                monitor.worked(UNITS_PHASE_5);
            }

            // ----------------------------------------------------------------------------------------
            // Phase 6: Remove all of the existing RelationalEntity objects for which NO matching
            // JdbcNode was found. These are essentially extra model objects that no
            // longer exist in the database (or, perhaps never did). At the end of this
            // phase, these branches are traversed to ensure they are no longer referenced
            // by the model objects that are to remain.
            // ----------------------------------------------------------------------------------------
            // First check if cancelled
            if (monitor.isCanceled()) {
                throw new UserCancelledException();
            }
            monitor.worked(UNITS_PHASE_6);

            // ----------------------------------------------------------------------------------------
            // Phase 7: Update the information on the resource (or ModelAnnotation).
            // ----------------------------------------------------------------------------------------
            // First check if cancelled
            if (monitor.isCanceled()) {
                throw new UserCancelledException();
            }
            processModelInformation(context, problems);
            monitor.worked(UNITS_PHASE_7);

        } catch (JdbcException e) {
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              null, e);
            problems.add(status);
        } catch (UserCancelledException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.User_cancelled_operation"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.WARNING,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, null);
            problems.add(status);
            // The user cancelled. The current policy is to throw away the model since it
            // is likely very inconsistent; therefore, throw the exception so that the
            // transaction is rolled back. One the other hand, if the behavior is to keep
            // whatever (likely inconsistent, certainly incomplete) metadata imported up
            // until the cancel operation, then the exception should NOT be thrown
            throw e;
        } catch (Throwable e) {
            final Object[] params = new Object[] {context.getJdbcDatabase().getName()};
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Unexpected_exception", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        } finally {
            monitor.done();
        }
    }

    /**
     * @param context
     * @param problems
     */
    protected void processModelInformation( final Context context,
                                            final List problems ) {
        final ModelContents contents = context.getModelContents();
        final ModelAnnotation model = contents.getModelAnnotation();
        this.updateModelAnnotation(model);
    }

    /**
     * Create the new RelationalEntity object corresponding to the supplied node. This method is also supposed to create any
     * objects that exist below the new object (and thus assigning any references between those child objects and this new
     * object). However, this method is not responsible for putting the new object into it's parent.
     * 
     * @param node the JdbcNode that specifies the type of RelationalEntity that is to be created; never null
     * @param parent the RelationalEntity that is to be the parent of the new object; may be null if the new object is at the
     *        top-level of the model (in which case the calling method is responsible for adding the new object to the resource),
     *        otherwise this method is responsible for setting the reference between the parent and the new object
     * @param modelStructure the object that contains the structure of how the <code>jdbcNodes</code> are to be placed into the
     *        model; may not be null
     * @return the new object, or null if the object was not able to be created because it was invalid (e.g., if the JDBC database
     *         object has no name); in such cases, warnings or errors will be added to the <code>problems</code> list.
     * @throws JdbcException if there is a problem or error
     */
    protected RelationalEntity createNewObject( final JdbcNode node,
                                                final RelationalEntity parent,
                                                final JdbcModelStructure modelStructure,
                                                final Map nodesToModelObjects,
                                                final Context context,
                                                final int totalNum,
                                                final int unitsPerModelObject,
                                                final int index,
                                                final IProgressMonitor monitor,
                                                final List problems,
                                                final List newTableObjects ) throws JdbcException {
        CoreArgCheck.isNotNull(node);
        CoreArgCheck.isNotNull(modelStructure);
        CoreArgCheck.isNotNull(context);
        final ModelContents contents = context.getModelContents();
        final int nodeType = node.getType();
        final String nodeName = node.getName();

        if (monitor.isCanceled()) {
            throw new UserCancelledException();
        }

        // Check for an invalid name ...
        if (nodeName == null || nodeName.trim().length() == 0) {
            // There is no reason to model Relational objects that have no name, since they can't be used by a client application!
            final String typeName = node.getTypeName();
            final Object[] params = new Object[] {typeName};
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Skipping_{0}_with_no_name", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.WARNING,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, null);
            problems.add(status);
        }
        
        String subtaskMsg;

        RelationalEntity obj = null;
        switch (nodeType) {
            case JdbcNode.CATALOG:
//                final Catalog cat = factory.createCatalog();
//                setNameAndNameInSource(cat, nodeName, node, context);
//                final Object[] catParams = new Object[] {nodeName, new Integer(totalNum), new Integer(index)};
//                String subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Creating_catalog", catParams); //$NON-NLS-1$
//                monitor.subTask(subtaskMsg);
//                obj = cat;
//                monitor.worked(unitsPerModelObject);
                break;
            case JdbcNode.SCHEMA:
//                final Schema schema = factory.createSchema();
//                final Object[] schemaParams = new Object[] {nodeName, new Integer(totalNum), new Integer(index)};
//                subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Creating_schema", schemaParams); //$NON-NLS-1$
//                monitor.subTask(subtaskMsg);
//                // Set the owner ...
//                if (parent instanceof Catalog) {
//                    schema.setCatalog((Catalog)parent);
//                }
//                setNameAndNameInSource(schema, nodeName, node, context);
//                obj = schema;
//                monitor.worked(unitsPerModelObject);
                break;
            case JdbcNode.TABLE:
                // Create the correct type of object ...
                final JdbcTable tableNode = (JdbcTable)node;
                final String tableType = tableNode.getTypeName();
                final String eclassName = JdbcRelationalPlugin.getJdbcNodeToRelationalMapping().getRelationalClassForJdbcTableType(tableType);
                final RelationalPackage pkg = RelationalPackage.eINSTANCE;

                CoreArgCheck.isNotNull(pkg,
                                       ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.RelationalPackageNotFound")); //$NON-NLS-1$

                EClass eclass = (EClass)pkg.getEClassifier(eclassName);
                if (eclass == null) {
                    eclass = pkg.getBaseTable();
                }
                final Object[] subtaskParams = new Object[] {eclass.getName(), nodeName, new Integer(totalNum),
                    new Integer(index)};
                subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Creating_table_or_view", subtaskParams); //$NON-NLS-1$
                monitor.subTask(subtaskMsg);
                final Table table = (Table)factory.create(eclass);

                // Set the owner (do this before doing anything with columns, pks, or indexes) ...
                if (parent instanceof Catalog) {
                    table.setCatalog((Catalog)parent);
                } else if (parent instanceof Schema) {
                    table.setSchema((Schema)parent);
                } else {
                    // need to add table directly to resource as catalogs and schemas are not included in model
                    context.getResource().getContents().add(table);
                }

                // Set the name ...
                setNameAndNameInSource(table, nodeName, node, context, problems);

                // Set the description ...
                final String desc = tableNode.getRemarks();
                if (desc != null && desc.trim().length() != 0) {
                    final Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(table,
                                                                                                    contents.getAnnotationContainer(true));
                    annotation.setDescription(desc);
                }

                // Construct all of the detailed information ...
                createColumns(tableNode, table, context, problems);
                createPrimaryKey(tableNode, table, context, problems);
                if (context.getJdbcDatabase().getIncludes().includeIndexes()) {
                    createIndexes(tableNode, table, context, problems);
                }

                newTableObjects.add(node);
                obj = table;
                monitor.worked(unitsPerModelObject);
                break;
            // case JdbcNode.VIEW:
            // break;
            case JdbcNode.PROCEDURE:
                final JdbcProcedure procNode = (JdbcProcedure)node;
                final Procedure proc = factory.createProcedure();
                final Object[] procParams = new Object[] {nodeName, new Integer(totalNum), new Integer(index)};
                subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Creating_procedure", procParams); //$NON-NLS-1$
                monitor.subTask(subtaskMsg);
                // Set the owner ...
                if (parent instanceof Catalog) {
                    proc.setCatalog((Catalog)parent);
                } else if (parent instanceof Schema) {
                    proc.setSchema((Schema)parent);
                } else {
                    // need to add procedure directly to resource as catalogs and schemas are not included in model
                    context.getResource().getContents().add(proc);
                }
                setNameAndNameInSource(proc, nodeName, node, context, problems);
                obj = proc;

                // Create the parameters and return/results ...
                createParameters(procNode, proc, context, problems);
                monitor.worked(unitsPerModelObject);
                break;
        }

        // Register the new object as having a binding to a JdbcNode ...
        nodesToModelObjects.put(node, obj);

        // Process the children of the node ...
        final List children = modelStructure.getChildren(node);
        int counter = 0;
        if (children != null && children.size() != 0) {
            final Iterator iter = children.iterator();
            while (iter.hasNext()) {
                final JdbcNode child = (JdbcNode)iter.next();
                ++counter;
                createNewObject(child,
                                obj,
                                modelStructure,
                                nodesToModelObjects,
                                context,
                                totalNum,
                                unitsPerModelObject,
                                index + counter,
                                monitor,
                                problems,
                                newTableObjects); // should've added as child of 'obj'
            }
        }
        return obj;
    }

    protected void createColumns( final JdbcTable tableNode,
                                  final Table table,
                                  final Context context,
                                  final List problems ) {
        CoreArgCheck.isNotNull(context);
        final ModelContents contents = context.getModelContents();
        try {
            // Get the column information ...
            final Request request = tableNode.getRequest(GetColumnsRequest.NAME, false);
            final Results results = request.getResults();
            final Object[] rows = results.getRows();
            final int numRows = results.getRowCount();
            // Results are ordered by ORDINAL_POSITION
            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                final String name = results.getString(row, 3);
                final int type = results.getInt(row, 4);
                final String typeName = results.getString(row, 5);
                final int columnSize = results.getInt(row, 6);
                // buffer length; unused
                final int numDecDigits = results.getInt(row, 8);
                final int numPrecRadix = results.getInt(row, 9);
                final int nullable = results.getInt(row, 10);
                final String remarks = results.getString(row, 11);
                final String defaultValue = results.getString(row, 12);
                // sql_data_type; unused
                // sql_datetime_sub; unused
                final int charOctetLen = results.getInt(row, 15);
                // final int position = results.getInt(row,16);
                // final String isNullable = results.getString(row,17);

                final Column column = this.factory.createColumn();
                // Add the column to the table
                column.setOwner(table);

                setColumnInfo(column,
                              tableNode,
                              context,
                              problems,
                              name,
                              type,
                              typeName,
                              columnSize,
                              numDecDigits,
                              numPrecRadix,
                              nullable,
                              defaultValue,
                              charOctetLen);

                // Set the description (after adding column to table)...
                if (remarks != null && remarks.trim().length() != 0) {
                    final Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(column,
                                                                                                    contents.getAnnotationContainer(true));
                    annotation.setDescription(remarks);
                }
            }
        } catch (JdbcException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_obtaining_column_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        }
    }

    protected void setColumnInfo( final Column column,
                                  final JdbcTable tableNode,
                                  final Context context,
                                  final List problems,
                                  final String name,
                                  final int type,
                                  final String typeName,
                                  final int columnSize,
                                  final int numDecDigits,
                                  final int numPrecRadix,
                                  final int nullable,
                                  final String defaultValue,
                                  final int charOctetLen ) {

        setNameAndNameInSource(column, name, tableNode, context, true, problems);

        // Set the type information ...
        column.setFixedLength(isFixedLength(type, typeName));
        column.setNativeType(typeName);
        final EObject datatype = findType(type, typeName, columnSize, columnSize, numDecDigits, problems);
        if (datatype != null) {
            column.setType(datatype);
        }

        // Set the searchability ...
        final SearchabilityType searchability = this.typeMapping != null && datatype != null ? this.typeMapping.getSearchabilityType(datatype) : SearchabilityType.UNSEARCHABLE_LITERAL;
        column.setSearchability(searchability);
        // Set the length, precision and scale ...
        if (searchability.getValue() == SearchabilityType.ALL_EXCEPT_LIKE) {
            column.setCaseSensitive(false);
            column.setPrecision(columnSize);
            column.setLength(0);
        } else if (searchability.getValue() == SearchabilityType.SEARCHABLE) {
            column.setLength(columnSize);
            column.setPrecision(0);
            column.setCaseSensitive(true);
        } else {
            column.setCaseSensitive(false);
            column.setPrecision(0);
            column.setLength(columnSize);
        }
        // Set the length for character types
        if (type == Types.CHAR) {
            column.setLength(columnSize);
            column.setPrecision(0);
        }
        column.setScale(numDecDigits);

        // Set the nullability
        switch (nullable) {
            case DatabaseMetaData.columnNoNulls:
                column.setNullable(NullableType.NO_NULLS_LITERAL);
                break;
            case DatabaseMetaData.columnNullable:
                column.setNullable(NullableType.NULLABLE_LITERAL);
                break;
            default:
                column.setNullable(NullableType.NULLABLE_UNKNOWN_LITERAL);
                break;
        }

        if (defaultValue != null) {
            if(type == Types.BIT) {
                if (columnSize <= 1) {
                    // Set boolean true or false, depending on incoming bit value for MySQL case
                    if(defaultValue.length() == 1) {
                        int charIntVal = defaultValue.charAt(0);
                        // Set boolean FALse for incoming 0, TRUE for 1
                        if(charIntVal==0) {
                            column.setDefaultValue(Boolean.FALSE.toString());
                        } else if(charIntVal==1) {
                            column.setDefaultValue(Boolean.TRUE.toString());
                        }
                    } else {
                        String trimedDefault = defaultValue.trim();
                        if (defaultValue.startsWith("(") && defaultValue.endsWith(")")) { //$NON-NLS-1$ //$NON-NLS-2$
                            trimedDefault = defaultValue.substring(1, defaultValue.length() - 1);
                        }
                        column.setDefaultValue(trimedDefault);
                    }
                }
            } else if (type != Types.BINARY) {
                //TODO: writing a binary string creates an invalid .xmi
                column.setDefaultValue(defaultValue);
            }
        }
        column.setSelectable(true);
        column.setUpdateable(true);

        if (numPrecRadix != 0) {
            column.setRadix(numPrecRadix);
        }

        // Don't currently know how to get these ...
        // column.setFormat("");
        // column.setMaximumValue("");
        // column.setMinimumValue("");
        // column.setCharacterSetName("");
        // column.setCollationName("");
        // column.setCurrency(false);
        // column.setAutoIncremented(false);
    }

    /**
     * @return True if the specified type should be considered fixed-length.
     * @since 4.2
     */
    protected boolean isFixedLength( final int type,
                                     final String typeName ) {
        return !(type == Types.LONGVARBINARY || type == Types.LONGVARCHAR || type == Types.VARBINARY || type == Types.VARCHAR
                 || type == Types.ARRAY || type == Types.BLOB || type == Types.CLOB);
    }

    /**
     * Find the type given the supplied information. This method is called by the various <code>create*</code> methods, and is
     * currently implemented to use {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)} (by
     * name) for other types.
     * <p>
     * In general, this method should not be overridden, since it contains logic that calls other <code>findType</code> methods
     * that each perform less complex operations. Instead, it is usually easier and better to override one or more of the
     * <code>findType</code> methods.
     * </p>
     * 
     * @param type the {@link Types JDBC type} value
     * @param typeName the (potentially DBMS-specific) type name
     * @param precision the precision of the type
     * @param scale the scale of the type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype, or null if no such type could be found
     */
    protected EObject findType( int jdbcType,
                                final String typeName,
                                final int length,
                                final int precision,
                                final int scale,
                                final List problems ) {
        // If the type is NUMERIC and precision is non-zero, then look at the length of the column ...
        // (assume zero length means the length isn't known)
        EObject result = null;
        
        if (jdbcType == Types.BIT && precision > 1) {
            //BINARY is the closest type,
            //for mysql a long may also be a valid representation
            jdbcType = Types.BINARY;
        }

        // First look up by type code ...
        result = findType(jdbcType, problems);
        if (result != null) {
            return result;
        }

        // Still haven't found one, so look it up by name ...
        result = findType(typeName, problems);
        return result;
    }

    /**
     * Find the datatype by type code.
     * 
     * @param jdbcType the JDBC (or DBMS) type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype that is able to represent data with the supplied criteria, or null if no datatype could be found
     */
    protected EObject findType( final int jdbcType,
                                final List problems ) {
        if (this.typeMapping != null) {
            try {
                return this.typeMapping.getDatatype(jdbcType);
            } catch (ModelerCoreException e) {
                final Object[] params = new Object[] {new Integer(jdbcType)};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_computing_datatype", params); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.ERROR,
                                                  com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                  0, msg, e);
                problems.add(status);
            }
        }
        return null;
    }

    /**
     * Find the datatype by name.
     * 
     * @param jdbcTypeName the name of the JDBC (or DBMS) type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype that is able to represent data with the supplied criteria, or null if no datatype could be found
     */
    protected EObject findType( final String jdbcTypeName,
                                final List problems ) {
        if (jdbcTypeName != null && this.typeMapping != null) {
            try {
                return this.typeMapping.getDatatype(jdbcTypeName);
            } catch (ModelerCoreException e) {
                final Object[] params = new Object[] {jdbcTypeName};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_computing_datatype", params); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.ERROR,
                                                  com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                  0, msg, e);
                problems.add(status);
            }
        }
        return null;
    }

    /**
     * Find a builtin datatype by it's name.
     * 
     * @param builtinTypeName the identifier of the builtin datatype; see {@link DatatypeConstants.BuiltInNames}.
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype, or null if no such type could be found
     */
    protected EObject findBuiltinType( final String typeName,
                                       final List problems ) {
        if (typeName != null && this.getDatatypeManager() != null) {
            try {
                final EObject obj = this.getDatatypeManager().getBuiltInDatatype(typeName);
                if (obj != null) {
                    return obj;
                }
            } catch (ModelerCoreException e) {
                final Object[] params = new Object[] {typeName};
                final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_computing_datatype", params); //$NON-NLS-1$
                final IStatus status = new Status(
                                                  IStatus.ERROR,
                                                  com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
                                                  0, msg, e);
                problems.add(status);
            }
        }
        return null;
    }

    protected void createPrimaryKey( final JdbcTable tableNode,
                                     final Table table,
                                     final Context context,
                                     final List problems ) {
        // Can't add a primary key to a view ...
        if (!(table instanceof BaseTable)) {
            return; // don't create anything
        }

        // Skip if no primary keys are to be imported ...
        final boolean includeUniqueIndexes = context.getJdbcImportSettings().isIncludeUniqueIndexes();
        final boolean includeForeignKeys = context.getJdbcImportSettings().isIncludeForeignKeys();
        if (!includeUniqueIndexes && !includeForeignKeys) {
            return; // skip if no unique indexes AND no foreign keys are included
        }

        try {
            // Get the column information ...
            final Request request = tableNode.getRequest(GetPrimaryKeyRequest.NAME, false);
            final Results results = request.getResults();
            final Object[] rows = results.getRows();
            final int numRows = results.getRowCount();

            if (numRows == 0) {
                return;
            }

            // Build a map of the columns by their name so they can be found much more quickly ...
            final Map columnsByName = createColumnMapKeyedByNames(table);

            // Go through the results
            String pkName = null;
            final Column[] columns = new Column[numRows];
            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                String columnName = results.getString(row, 3); // COLUMN_NAME
                pkName = results.getString(row, 5);
                // Convert the column name to match the form stored in the 'name in source'.
                String quoteStr = getQuoteString(context, problems);
                if( quoteStr != null ) {
                	columnName =  tableNode.getUnqualifiedName(columnName).replaceAll(quoteStr, StringUtilities.EMPTY_STRING);
                } else {
                	columnName = tableNode.getUnqualifiedName(columnName);
                }
                columnName = convertName(columnName, context);
                final Column column = (Column)columnsByName.get(columnName);
                if (column != null) {
                    // Find the column in the table ...
                    final short seqIndex = results.getShort(row, 4); // KEY_SEQ
                    columns[seqIndex - 1] = column;
                }
            }

            // Create the primary key
            final PrimaryKey primaryKey = factory.createPrimaryKey();
            // Set the reference to the table ...
            primaryKey.setTable((BaseTable)table);

            if (pkName != null) {
                setNameAndNameInSource(primaryKey, pkName, tableNode, context, true, problems);
            }
            // Add the columns in the correct order
            final List keyColumns = primaryKey.getColumns();
            for (int i = 0; i < columns.length; ++i) {
                final Column column = columns[i];
                if (column != null) {
                    keyColumns.add(column);
                }
            }

        } catch (JdbcException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_primary_key_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        }
    }

    protected boolean checkExportedForeignKeysIfNoImportedForeignKeysFound() {
        return true;
    }

    protected Request getForeignKeyRequest( final JdbcTable tableNode,
                                            final String name,
                                            final boolean includeMetadata ) throws JdbcException {
        return tableNode.getRequest(GetImportedForeignKeysRequest.NAME, false);
    }

    protected void createForeignKey( final JdbcTable tableNode,
                                     final Table table,
                                     final Context context,
                                     final Map nodesToModelObjects,
                                     final List problems ) {
        // Can't add a foreign key to a view ...
        if (!(table instanceof BaseTable)) {
            return; // don't create anything
        }

        try {
            // Get the column information ...
            Request request = getForeignKeyRequest(tableNode, GetImportedForeignKeysRequest.NAME, false);
            Results results = request.getResults();
            Object[] rows = results.getRows();
            int numRows = results.getRowCount();

            if (numRows == 0 && checkExportedForeignKeysIfNoImportedForeignKeysFound()) {
                // No imported keys, so try again with the Exported Keys ...
                request = tableNode.getRequest(GetExportedForeignKeysRequest.NAME, false);
                results = request.getResults();
                rows = results.getRows();
                numRows = results.getRowCount();

                if (numRows == 0) {
                    // No imported and no exported foreign keys, so just return
                    return;
                }
                return;
            }

            // Build a map of the columns by their name so they can be found much more quickly ...
            final Map columnsByName = createColumnMapKeyedByNames(table);

            // Go through the results and assemble the rows that go with each foreign key
            // According to the JDBC spec, these rows are to be ordered by the KEY_SEQ
            List currentColumnsForKey = null;
            final Map columnsByFK = new HashMap();
            List currentColumnNamesForPk = null;
            final Map columnNamesForPks = new HashMap();
            final Map fkSpecs = new HashMap();

            // Defect 17820 - keep track of array initialization
            List previousColForKeyList = null;
            boolean initDone = false;

            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                final short seqIndex = results.getShort(row, 8); // KEY_SEQ
                String fkColumnName = results.getString(row, 7); // FKCOLUMN_NAME
                String pkColumnName = results.getString(row, 3); // PKCOLUMN_NAME
                // Convert the column name to match the form stored in the 'name in source'.
                String quoteStr = getQuoteString(context, problems);
                if( quoteStr != null ) {
                	fkColumnName =  tableNode.getUnqualifiedName(fkColumnName).replaceAll(quoteStr, StringUtilities.EMPTY_STRING);
                } else {
                	fkColumnName = tableNode.getUnqualifiedName(fkColumnName);
                }
                fkColumnName = convertName(fkColumnName, context);
                
                if( quoteStr != null ) {
                	pkColumnName =  tableNode.getUnqualifiedName(pkColumnName).replaceAll(quoteStr, StringUtilities.EMPTY_STRING);
                } else {
                	pkColumnName = tableNode.getUnqualifiedName(pkColumnName);
                }
                pkColumnName = convertName(pkColumnName, context);
                // Find the column ...
                final Column column = (Column)columnsByName.get(fkColumnName);
                final String fkName = results.getString(row, 11); // FK_NAME
                currentColumnsForKey = (List)columnsByFK.get(fkName);
                currentColumnNamesForPk = (List)columnNamesForPks.get(fkName);

                // Defect 17820 - if this is a new foreign key, set the initDone flag to false
                if (currentColumnsForKey == null || currentColumnsForKey != previousColForKeyList) {
                    initDone = false;
                    previousColForKeyList = currentColumnsForKey;
                }

                if (column != null) {
                    // Defect 17820 - reinit on KEY_SEQ of 0 or 1, but only do it once.
                    // Allow 0 or 1 since older MySQL driver apparently doesnt follow spec.
                    if ((seqIndex == 1 || seqIndex == 0) && !initDone) {
                        // New key, so create a new rowlist
                        currentColumnsForKey = new ArrayList();
                        currentColumnNamesForPk = new ArrayList();
                        final ForeignKeySpec fkSpec = new ForeignKeySpec();
                        fkSpec.fkName = fkName;
                        fkSpec.pkName = results.getString(row, 12); // PK_NAME
                        fkSpec.pkTable = results.getString(row, 2); // PK_TABLE_NAME
                        fkSpec.pkSchema = results.getString(row, 1); // PK_SCHEMA_NAME
                        fkSpec.pkCatalog = results.getString(row, 0); // PK_CATALOG_NAME
                        fkSpecs.put(fkName, fkSpec);
                        columnsByFK.put(fkName, currentColumnsForKey);
                        columnNamesForPks.put(fkName, currentColumnNamesForPk);
                        initDone = true;
                    }
                    currentColumnsForKey.add(column);
                    currentColumnNamesForPk.add(pkColumnName);
                }
            }

            // Go through each of the lists of rows (one for each key)
            final Iterator fkSpecIter = columnsByFK.keySet().iterator();
            final Iterator columnsForKeyIter = columnsByFK.values().iterator();
            final Iterator columnNamesForPkIter = columnNamesForPks.values().iterator();
            while (columnsForKeyIter.hasNext()) {
                final List columnsForKey = (List)columnsForKeyIter.next();
                final List columnNamesForPk = (List)columnNamesForPkIter.next();
                final ForeignKeySpec fkSpec = (ForeignKeySpec)fkSpecs.get(fkSpecIter.next());

                // Find or create the foreign key ...
                final ForeignKey fk = factory.createForeignKey();
                fk.setTable((BaseTable)table);
                setNameAndNameInSource(fk, fkSpec.fkName, tableNode, context, true, problems);
                // Put the columns into the foreign key ...
                fk.getColumns().addAll(columnsForKey);

                // Set the FK and PK multiplicities to unspecified (defect 13226)
                fk.setPrimaryKeyMultiplicity(MultiplicityKind.UNSPECIFIED_LITERAL);
                fk.setForeignKeyMultiplicity(MultiplicityKind.UNSPECIFIED_LITERAL);

                // Find or create the primary key ...
                final UniqueKey ukey = findUniqueKey(tableNode.getJdbcDatabase(),
                                                     nodesToModelObjects,
                                                     fkSpec.pkCatalog,
                                                     fkSpec.pkSchema,
                                                     fkSpec.pkTable,
                                                     columnNamesForPk);
                if (ukey != null) {
                    fk.setUniqueKey(ukey);
                }
            }

        } catch (JdbcException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_primary_key_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        }
    }

    protected UniqueKey findUniqueKey( final JdbcDatabase dbNode,
                                       final Map nodesToModelObjects,
                                       final String catalogName,
                                       final String schemaName,
                                       final String tableName,
                                       final List columnNames ) {
        // Build a path ...
        IPath path = new Path("/"); //$NON-NLS-1$
        if (catalogName != null && catalogName.trim().length() != 0) {
            // Per defect 13227, the getImportedKeys and getExportedKeys might return a non-null value in the
            // 'catalog' column, even though DatabaseMetaData returns 'false' for supportsCatalogs.
            // Therefore, as a workaround, ignore the catalog name when supportsCatalogs is false ...
            boolean supportsCatalogs = true;
            try {
                supportsCatalogs = dbNode.getCapabilities().supportsCatalogs();
            } catch (Exception e) {
                // ignore it ...
            }
            if (supportsCatalogs) {
                path = path.append(catalogName);
            }
        }
        if (schemaName != null && schemaName.trim().length() != 0) {
            // Per defect 13227, the getImportedKeys and getExportedKeys might return a non-null value in the
            // 'schema' column, even though DatabaseMetaData returns 'false' for supportsSchemas.
            // Therefore, as a workaround, ignore the schema name when supportsSchemas is false ...
            boolean supportsSchemas = true;
            try {
                supportsSchemas = dbNode.getCapabilities().supportsSchemas();
            } catch (Exception e) {
                // ignore it ...
            }
            if (supportsSchemas) {
                path = path.append(schemaName);
            }
        }
        JdbcNode tableTypeContainer = dbNode.findJdbcNode(path);
        if (tableTypeContainer == null) {
            tableTypeContainer = dbNode;
        }
        // Iterate over all JdbcTableType children of the schema ...
        JdbcNode[] children = null;
        try {
            children = tableTypeContainer.getChildren();
        } catch (JdbcException e) {
            ModelerJdbcRelationalConstants.Util.log(e);
            return null;
        }
        for (int i = 0; i < children.length; ++i) {
            final JdbcNode child = children[i];
            if (child instanceof JdbcTableType) {
                // Find the table with the same name ...
                final JdbcNode tableWithKey = child.findChild(tableName);
                if (tableWithKey != null && tableWithKey instanceof JdbcTable) {
                    // Found a table !
                    final JdbcTable tableNode = (JdbcTable)tableWithKey;
                    // Find the corresponding RelationalEntity ...
                    final RelationalEntity table = (RelationalEntity)nodesToModelObjects.get(tableWithKey);
                    if (table != null && table instanceof BaseTable) {
                        final BaseTable baseTable = (BaseTable)table;
                        // See if the primary key is a match ...
                        final PrimaryKey pk = baseTable.getPrimaryKey();
                        if (isUniqueyMatch(pk, columnNames, tableNode)) {
                            return pk;
                        }
                        // Iterate through the unique keys ...
                        final Iterator iter = ((BaseTable)table).getUniqueConstraints().iterator();
                        while (iter.hasNext()) {
                            final UniqueConstraint constraint = (UniqueConstraint)iter.next();
                            if (isUniqueyMatch(constraint, columnNames, tableNode)) {
                                return constraint;
                            }
                        }
                    }
                }
            }
        }
        return null; // no match found
    }

    protected Map createColumnMapKeyedByNames( final Table table ) {
        // Build a map of the columns by their name so they can be found much more quickly ...
        final Map columnsByName = new HashMap();
        final Iterator iter = table.getColumns().iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            String columnName = column.getNameInSource();
            if (columnName == null || columnName.trim().length() == 0) {
                columnName = column.getName();
            }
            columnsByName.put(columnName, column);
        }
        return columnsByName;
    }

    protected List createIndexes( final JdbcTable tableNode,
                                  final Table table,
                                  final Context context,
                                  final List problems ) {
        List indexes = null;

        try {
            // Get the column information ...
            final Request request = tableNode.getRequest(GetIndexesRequest.NAME, false);
            final Results results = request.getResults();
            final Object[] rows = results.getRows();
            final int numRows = results.getRowCount();

            if (numRows == 0) {
                return Collections.EMPTY_LIST;
            }

            indexes = new LinkedList();

            // Build a map of the columns by their name so they can be found much more quickly ...
            final Map columnsByName = createColumnMapKeyedByNames(table);

            // Go through the results and assemble the rows that go with each index
            // According to the JDBC spec, these rows are to be ordered by
            // NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION
            final List indexSpecs = new ArrayList();
            List currentColumnsForIndex = null;
            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                // First look at the type of the index ...
                final short type = results.getShort(row, 6);
                if (type == DatabaseMetaData.tableIndexStatistic) {
                    final int cardinality = results.getInt(row, 10);
                    updateCardinality(table, cardinality);
                    // Skip all statistic indexes
                    continue;
                }
                final short position = results.getShort(row, 7);
                String columnName = results.getString(row, 8);
                // Convert the column name to match the form stored in the 'name in source'.
                String quoteStr = getQuoteString(context, problems);
                if( quoteStr != null ) {
                	columnName =  tableNode.getUnqualifiedName(columnName).replaceAll(quoteStr, StringUtilities.EMPTY_STRING);
                } else {
                	columnName = tableNode.getUnqualifiedName(columnName);
                }
                columnName = convertName(columnName, context);
                // Find the column ...
                final Column column = (Column)columnsByName.get(columnName);
                if (position == 1) {
                    // New index, so create a new rowlist
                    currentColumnsForIndex = new ArrayList();
                    final IndexSpec spec = new IndexSpec();
                    spec.indexName = results.getString(row, 5);
                    spec.indexQualifier = results.getString(row, 4);
                    spec.nonUnique = results.getBoolean(row, 0);
                    spec.pages = results.getInt(row, 11);
                    spec.cardinality = results.getInt(row, 10);
                    spec.filterCondition = results.getString(row, 12);
                    spec.type = type;
                    spec.columns = currentColumnsForIndex;
                    indexSpecs.add(spec);
                }
                if (column != null) {
                    currentColumnsForIndex.add(column);
                }
            }

            // Go through each of the lists of rows (one for each key)
            final Iterator indexSpecIter = indexSpecs.iterator();
            while (indexSpecIter.hasNext()) {
                final IndexSpec indexSpec = (IndexSpec)indexSpecIter.next();

                modifyIndexName(indexSpec);

                // Find or create the index ...
                final Index index = findIndex(table, indexSpec, tableNode, context, true, problems);
                // Index may be null if we shouldn't create one!
                if (index != null) {
                    updateCardinality(table, indexSpec.cardinality);
                    indexes.add(index);
                }
            }

        } catch (JdbcException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_primary_key_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        }
        return indexes;
    }

    protected void modifyIndexName( IndexSpec indexSpec ) {
        // do nothing
    }

    protected void updateCardinality( final Table table,
                                      final int cardinality ) {
        // Check that the cardinality is set ...
        if (table.getCardinality() == 0) {
            // Get the cardinality and set on the table ...
            if (cardinality > 0) {
                table.setCardinality(cardinality);
            }
        }
    }

    /**
     * Find an existing index that is a match for the supplied information, and optionally create one if needed (and the index
     * should be created).
     * 
     * @param table
     * @param spec
     * @param tableNode
     * @param context
     * @param createIfRequired
     * @return
     */
    protected Index findIndex( final Table table,
                               final IndexSpec spec,
                               final JdbcTable tableNode,
                               final Context context,
                               final boolean createIfRequired,
                               List problems) {
        // Get all of the existing indexes ...
        final EObject tableOwner = table.eContainer();
        if (tableOwner != null) {
            List existingIndexes = null;
            // Owned by either a Catalog or Schema
            if (tableOwner instanceof Catalog) {
                existingIndexes = ((Catalog)tableOwner).getIndexes();
            } else if (tableOwner instanceof Schema) {
                existingIndexes = ((Schema)tableOwner).getIndexes();
            }
            // Find an index with the same name and table reference ...
            final Iterator iter = existingIndexes.iterator();
            while (iter.hasNext()) {
                final Index index = (Index)iter.next();
                final boolean isMatch = isMatchingIndex(index, spec);
                if (isMatch) {
                    // Ensure the name is correct ...
                    setNameAndNameInSource(index, spec.indexName, tableNode, context, problems);
                    return index;
                }
            }
        }
        // Otherwise, owned by a Resource (Table is a root object)
        else {
            final Resource resource = table.eResource();
            if (resource != null) {
                // Iterate the root level objects and accumulate the list of existing indexes ...
                final Iterator iter = resource.getContents().iterator();
                while (iter.hasNext()) {
                    final Object rootObj = iter.next();
                    if (rootObj instanceof Index) {
                        final Index index = (Index)rootObj;
                        if (isMatchingIndex(index, spec)) {
                            // Ensure the name is correct ...
                            setNameAndNameInSource(index, spec.indexName, tableNode, context, problems);
                            return index;
                        }
                    }
                }
            }
        }

        // Nothing was found, but first compare with the primary key
        // (some drivers return an index that corresponds to the primary key) ...
        if (table instanceof BaseTable) {
            final String objectName = convertName(spec.indexName, context);
            final PrimaryKey pk = ((BaseTable)table).getPrimaryKey();
            if (pk != null) {
                if (objectName.equals(pk.getName())) {
                    return null;
                }
            }
        }

        // Nothing was found, so create one ...
        final Index index = this.factory.createIndex();
        // Set the owner ...
        if (tableOwner != null) {
            if (tableOwner instanceof Catalog) {
                index.setCatalog((Catalog)tableOwner);
            } else if (tableOwner instanceof Schema) {
                index.setSchema((Schema)tableOwner);
            }
        } else {
            final Resource resource = table.eResource();
            CoreArgCheck.isNotNull(resource);
            resource.getContents().add(index);
        }
        setNameAndNameInSource(index, spec.indexName, tableNode, context, problems);
        index.setUnique(!spec.nonUnique);
        index.getColumns().addAll(spec.columns);
        index.setFilterCondition(spec.filterCondition);
        return index;
    }

    /**
     * @param index
     * @param spec
     * @return
     */
    protected boolean isMatchingIndex( final Index index,
                                       final IndexSpec spec ) {
        // Compare the uniqueness ...
        if (index.isUnique() && spec.nonUnique) {
            return false;
        }
        // Compare the number of columns referenced
        final List indexColumns = index.getColumns();
        final List specColumns = spec.columns;
        if (indexColumns.size() != specColumns.size()) {
            return false;
        }
        // Compare the columns reference
        if (!indexColumns.containsAll(specColumns) || !specColumns.containsAll(indexColumns)) {
            return false;
        }
        // Considered a match ...
        return true;
    }

    private boolean isUniqueyMatch( final UniqueKey ukey,
                                    final List columnNames,
                                    final JdbcTable tableNode ) {
        if (ukey == null) {
            return false;
        }
        final List ukeyColumns = ukey.getColumns();
        if (ukeyColumns.size() != columnNames.size()) {
            return false;
        }
        // Go through the columns and see if the names correlate ...
        final Iterator iter = ukeyColumns.iterator();
        final Iterator nameIter = columnNames.iterator();
        while (iter.hasNext()) {
            final Column ukColumn = (Column)iter.next();
            final String columnName = (String)nameIter.next();
            String ukColumnName = ukColumn.getNameInSource();
            if (ukColumnName == null || ukColumnName.trim().length() == 0) {
                ukColumnName = ukColumn.getName();
            }
            if (!ukColumnName.equals(columnName)) {
                return false;
            }
        }
        return true;
    }

    public class IndexSpec {
        public String indexName;
        public String indexQualifier;
        public String filterCondition;
        public int cardinality;
        public int pages;
        public int type;
        public boolean nonUnique;
        public List columns;
    }

    class ForeignKeySpec {
        public String fkName;
        public String pkName;
        public String pkCatalog;
        public String pkSchema;
        public String pkTable;
    }

    protected void createParameters( final JdbcProcedure procNode,
                                     final Procedure proc,
                                     final Context context,
                                     final List problems ) {
        CoreArgCheck.isNotNull(context);
        final ModelContents contents = context.getModelContents();
        try {
            // Get the parameter information ...
            final Request request = procNode.getRequest(GetProcedureParametersRequest.NAME, false);
            final Results results = request.getResults();
            final Object[] rows = results.getRows();
            final int numRows = results.getRowCount();

            if (numRows == 0) {
                return;
            }

            ProcedureResult procResult = null;
            int numParamsWithNullName = 0;

            // Go through the results
            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                String name = results.getString(row, 3); // COLUMN_NAME
                final short columnType = results.getShort(row, 4); // COLUMN_TYPE
                final short type = results.getShort(row, 5); // DATA_TYPE
                final String typeName = results.getString(row, 6); // TYPE_NAME
                final int precision = results.getInt(row, 7); // PRECISION
                final int length = results.getInt(row, 8); // LENGTH
                final int scale = results.getInt(row, 9); // SCALE
                final int radix = results.getInt(row, 10); // RADIX
                final short nullable = results.getShort(row, 11); // NULLABLE
                final String remarks = results.getString(row, 12); // REMARKS

                final boolean resultSetColumn = isProcedureResultColumn(columnType, type, typeName);
                if (!resultSetColumn) {
                    // It's just a normal procedure parameter ...
                    final ProcedureParameter param = this.factory.createProcedureParameter();
                    // Add the parameter to the procedure
                    param.setProcedure(proc);

                    if (name == null) {
                        ++numParamsWithNullName;
                        name = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.DefaultNameForProcParamWithNoName", numParamsWithNullName); //$NON-NLS-1$
                    }
                    setNameAndNameInSource(param, name, procNode, context, true, problems);

                    // Set the type information ...
                    param.setNativeType(typeName);
                    final EObject datatype = findType(type, typeName, length, precision, scale, problems);
                    if (datatype != null) {
                        param.setType(datatype);
                    }

                    // Set the length, precision and scale ...
                    param.setLength(length);
                    param.setPrecision(precision);
                    param.setScale(scale);
                    param.setRadix(radix);

                    // Set the nullability
                    switch (nullable) {
                        case DatabaseMetaData.procedureNoNulls:
                            param.setNullable(NullableType.NO_NULLS_LITERAL);
                            break;
                        case DatabaseMetaData.procedureNullable:
                            param.setNullable(NullableType.NULLABLE_LITERAL);
                            break;
                        default:
                            param.setNullable(NullableType.NULLABLE_UNKNOWN_LITERAL);
                            break;
                    }

                    // Set the column type
                    switch (columnType) {
                        case DatabaseMetaData.procedureColumnIn:
                            param.setDirection(DirectionKind.IN_LITERAL);
                            break;
                        case DatabaseMetaData.procedureColumnOut:
                            param.setDirection(DirectionKind.OUT_LITERAL);
                            break;
                        case DatabaseMetaData.procedureColumnInOut:
                            param.setDirection(DirectionKind.INOUT_LITERAL);
                            break;
                        case DatabaseMetaData.procedureColumnReturn:
                            param.setDirection(DirectionKind.RETURN_LITERAL);
                            break;
                        case DatabaseMetaData.procedureColumnResult:
                            // Create a
                            param.setDirection(DirectionKind.RETURN_LITERAL);
                            break;
                        default:
                            param.setDirection(DirectionKind.UNKNOWN_LITERAL);
                            break;
                    }

                    // Don't currently know how to get these ...
                    // column.setDefault("");

                    // Set the description (after adding column to table)...
                    if (remarks != null && remarks.trim().length() != 0) {
                        final Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(param,
                                                                                                        contents.getAnnotationContainer(true));
                        annotation.setDescription(remarks);
                    }
                } else {
                    // It is a procedure result set column
                    if (procResult == null) {
                        // Create the result set ...
                        procResult = this.factory.createProcedureResult();
                        procResult.setProcedure(proc); // adds result to procedure
                        final String resultSetName = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.ResultSetName"); //$NON-NLS-1$
                        setNameAndNameInSource(procResult, resultSetName, procNode, context, true, problems);
                    }
                    // Determine whether the column represents the ResultSet or a column in the ResultSet ...
                    final boolean includeColumn = includeColumnInProcedureResult(columnType, type, typeName);
                    if (!includeColumn) {
                        // this column represents the result set, so change the name ...
                        setNameAndNameInSource(procResult, name, procNode, context, true, problems);

                        // Set the description (after adding column to table)...
                        if (remarks != null && remarks.trim().length() != 0) {
                            final Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(procResult,
                                                                                                            contents.getAnnotationContainer(true));
                            annotation.setDescription(remarks);
                        }
                    } else {
                        final Column param = this.factory.createColumn();
                        param.setOwner(procResult);
                        setNameAndNameInSource(param, name, procNode, context, true, problems);

                        // Set the type information ...
                        param.setNativeType(typeName);
                        final EObject datatype = findType(type, typeName, length, precision, scale, problems);
                        if (datatype != null) {
                            param.setType(datatype);
                        }

                        // Set the length, precision and scale ...
                        param.setLength(length);
                        param.setPrecision(precision);
                        param.setScale(scale);
                        param.setRadix(radix);

                        // Set the nullability
                        switch (nullable) {
                            case DatabaseMetaData.procedureNoNulls:
                                param.setNullable(NullableType.NO_NULLS_LITERAL);
                                break;
                            case DatabaseMetaData.procedureNullable:
                                param.setNullable(NullableType.NULLABLE_LITERAL);
                                break;
                            default:
                                param.setNullable(NullableType.NULLABLE_UNKNOWN_LITERAL);
                                break;
                        }

                        // Set the description (after adding column to table)...
                        if (remarks != null && remarks.trim().length() != 0) {
                            final Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(param,
                                                                                                            contents.getAnnotationContainer(true));
                            annotation.setDescription(remarks);
                        }
                    }
                }
            }

        } catch (JdbcException e) {
            final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_primary_key_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, e);
            problems.add(status);
        }
    }

    /**
     * Determine whether the following information from the result set of a call to
     * {@link DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * represents a column in a result set for the procedure.
     * <p>
     * This implementation merely returns <code>columnType == DatabaseMetaData.procedureColumnResult</code>.
     * </p>
     * 
     * @param columnType the short indicating what the metadata describes; should be one of
     *        {@link DatabaseMetaData#procedureColumnUnknown}, {@link DatabaseMetaData#procedureColumnIn},
     *        {@link DatabaseMetaData#procedureColumnInOut}, {@link DatabaseMetaData#procedureColumnOut},
     *        {@link DatabaseMetaData#procedureColumnInReturn}, or {@link DatabaseMetaData#procedureColumnResult}.
     * @param type the {@link Types JDBC type} of the column
     * @param typeName the DBMS-specific name of the column type
     * @return true if the information designates a column in a result set for the procedure, or false otherwise.
     */
    protected boolean isProcedureResultColumn( final short columnType,
                                               final short type,
                                               final String typeName ) {
        return (columnType == DatabaseMetaData.procedureColumnResult);
    }

    /**
     * If a record from the call to
     * {@link DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     * DatabaseMetaData.getProcedureColumns(...)} was determined by {@link #isProcedureResultColumn(short, short, String)} to be a
     * column for a result set (i.e., the method returns true), then determine whether the column should be added to the
     * {@link ProcedureResult}.
     * <p>
     * Some JDBC drivers return a procedure column that represents the result set but no columns in the result set itself. In such
     * cases, this method should return false.
     * </p>
     * <p>
     * This method implementation returns true.
     * </p>
     * 
     * @param columnType the short indicating what the metadata describes; should be one of
     *        {@link DatabaseMetaData#procedureColumnUnknown}, {@link DatabaseMetaData#procedureColumnIn},
     *        {@link DatabaseMetaData#procedureColumnInOut}, {@link DatabaseMetaData#procedureColumnOut},
     *        {@link DatabaseMetaData#procedureColumnInReturn}, or {@link DatabaseMetaData#procedureColumnResult}.
     * @param type the {@link Types JDBC type} of the column
     * @param typeName the DBMS-specific name of the column type
     * @return true if the information designates a column <i>in</i> a result set for the procedure, or false if the column
     *         actually <i>represents</i> the result set and thus should not be considered a column in the {@link ProcedureResult}
     *         .
     */
    protected boolean includeColumnInProcedureResult( final short columnType,
                                                      final short type,
                                                      final String typeName ) {
        return true;
    }

    /**
     * Helper method to find the best matches between the supplied nodes and the supplied model objects. This method is recursive
     * so that it continues down the model structure where it finds matches to continue finding matches down such branches of the
     * structure. Nodes for which there are no matches are not traversed and are placed in the supplied {@link ObjectMatcher
     * matcher's} {@link ObjectMatcher#getUnmatchedJdbcNodes() unmatched nodes}. Model objects that don't match any nodes are
     * placed in the matcher's {@link ObjectMatcher#getUnmatchedModelObjects()}; the exceptions are all the model objects that are
     * below other unmatched model objects (as the unmatched model objects (and their branches) will either be removed from the
     * model or left unmodified.
     * 
     * @param jdbcNodes the JdbcNode instances that are all children of the same parent, and that are all to be put into the model
     *        per the {@link JdbcModelStructure model structure}; may not be null
     * @param modelObjs the {@link RelationalEntity RelationalEntity model objects} that currently exist at the same level in the
     *        model as the <code>jdbcNodes</code>; may not be null
     * @param structure the object that contains the structure of how the <code>jdbcNodes</code> are to be placed into the model;
     *        may not be null
     * @param matcher the object that tracks the matches between the JdbcNodes being put into the model and the
     *        {@link RelationalEntity relational model objects} that already exist in the model
     * @throws UserCancelledException if the monitor says to cancel the operation
     */
    protected void matchChildren( final JdbcNode parentOfNodes,
                                  final List jdbcNodes,
                                  final List modelObjs,
                                  final JdbcModelStructure structure,
                                  final ObjectMatcher matcher,
                                  final IProgressMonitor monitor ) {
        CoreArgCheck.isNotNull(jdbcNodes);
        CoreArgCheck.isNotNull(modelObjs);
        CoreArgCheck.isNotNull(structure);
        CoreArgCheck.isNotNull(matcher);

        // Find the best matches between the JdbcNodes and the RelationalEntity objects ...
        if (parentOfNodes == null) {
            final String subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Finding_existing_root_model_objects"); //$NON-NLS-1$
            monitor.subTask(subtaskMsg);
        } else {
            final Object[] params = new Object[] {parentOfNodes.getName()};
            final String subtaskMsg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Finding_existing_model_objects", params); //$NON-NLS-1$
            monitor.subTask(subtaskMsg);
        }
        // Remove any non-relational entity from the list of model objects ...
        final List relationalModelObjs = new LinkedList(modelObjs);
        final Iterator modelObjIter = relationalModelObjs.iterator();
        while (modelObjIter.hasNext()) {
            final Object modelObject = modelObjIter.next();
            if (!(modelObject instanceof RelationalEntity)) {
                modelObjIter.remove();
            }
        }
        matcher.findBestMatches(jdbcNodes, relationalModelObjs);

        // Iterate over each JdbcNode ...
        final Iterator iter = jdbcNodes.iterator();
        while (iter.hasNext()) {
            // First check if cancelled
            if (monitor.isCanceled()) {
                throw new UserCancelledException();
            }

            final JdbcNode child = (JdbcNode)iter.next();
            // Find the best match for this child ...
            final RelationalEntity match = (RelationalEntity)matcher.getDestination().get(child);
            if (match != null) {
                // If there is a best match, get the children of the child and the match
                final List childrenOfChild = structure.getChildren(child);
                if (childrenOfChild != null) {
                    // And call this method again to find the best matches between them.
                    // This is a recursive call ...
                    final List childrenOfMatch = match.eContents();
                    matchChildren(child, childrenOfChild, childrenOfMatch, structure, matcher, monitor);
                }
            }
            // Else, there is no match, so 'child' should appear in the matcher's unmatched nodes.
        }
        // Note that if there are no matches for any of the children (as in the case of JdbcNodes
        // being placed into a new model), this method simply returns
    }

    /**
     * @return
     */
    public boolean isVerboseLogging() {
        return verbose;
    }

    /**
     * @param b
     */
    public void setVerboseLogging( final boolean b ) {
        verbose = b;
    }

    /**
     * @return
     */
    public RelationalFactory getFactory() {
        return factory;
    }

    protected void setNameAndNameInSource( final RelationalEntity entity,
                                           final String name,
                                           final JdbcNode node,
                                           final Context context, 
                                           List problems) {

        // See if the name is valid within the context of the entity's parent ...
        RelationalStringNameRule rule = new RelationalStringNameRule(RelationalPackage.RELATIONAL_ENTITY__NAME);
        StringNameValidator siblingNameValidator = new StringNameValidator();

        for (Object sibling : rule.getSiblingsForUniquenessCheck(entity)) {
            if (sibling != entity) {
                String siblingName = ((RelationalEntity)sibling).getName();

                if (siblingName != null) {
                    siblingNameValidator.addExistingName(siblingName);
                }
            }
        }

        setNameAndNameInSource(entity, name, node, context, siblingNameValidator, false, problems);
    }
    
	protected void setNameAndNameInSource(final RelationalEntity entity,
			final String name, final JdbcNode node, final Context context, final boolean ignoreFullyQualifiedName, List problems) {

		// See if the name is valid within the context of the entity's parent
		// ...
		RelationalStringNameRule rule = new RelationalStringNameRule(
				RelationalPackage.RELATIONAL_ENTITY__NAME);
		StringNameValidator siblingNameValidator = new StringNameValidator();

		for (Object sibling : rule.getSiblingsForUniquenessCheck(entity)) {
			if (sibling != entity) {
				String siblingName = ((RelationalEntity) sibling).getName();

				if (siblingName != null) {
					siblingNameValidator.addExistingName(siblingName);
				}
			}
		}

		setNameAndNameInSource(entity, name, node, context,
				siblingNameValidator, ignoreFullyQualifiedName, problems);
	}

    private void setNameAndNameInSource( final RelationalEntity entity,
                                           final String name,
                                           final JdbcNode node,
                                           final Context context,
                                           final StringNameValidator validator,
                                           final boolean ignoreFullyQualifiedName,
                                           List problems) {
        // If the name is null, create a new replacement name ...
        String theName = name;
        if (theName == null || theName.trim().length() == 0) {
            theName = createReplacementName(entity, context);
        }

        // Convert the name based upon the preferences/options ...
        final String convertedName = convertName(theName, context);
        String uniqueName = validator.createValidUniqueName(convertedName);
        boolean forceSetNameInSource = false;

        // Set the entity's name in source ...
        String nameInSource = convertedName;
        if( !ignoreFullyQualifiedName ) {
        	nameInSource = computeNameInSource(entity, name, node, context, forceSetNameInSource, problems);
        }
        if (nameInSource != null) {
        	
            entity.setNameInSource(convertName(nameInSource, context));
        }
        
        final JdbcImportSettings settings = context.getJdbcImportSettings();
        final SourceNames sourceNames = settings.getGenerateSourceNamesInModel();
        final int value = sourceNames.getValue();
        
        if (! ignoreFullyQualifiedName && value == SourceNames.FULLY_QUALIFIED) {
        	uniqueName = nameInSource;
        	String quoteStr =  getQuoteString(context, problems);
        	if( quoteStr != null && quoteStr.length() > 0 ) {
        		uniqueName = nameInSource.replaceAll(quoteStr, StringUtilities.EMPTY_STRING);
        	}
        	
        }
        
        if (uniqueName == null) {
            // name was already unique ...
            entity.setName(convertName(convertedName, context));
        } else {
            // name had to be changed to be unique ...
            entity.setName(convertName(uniqueName, context));
            forceSetNameInSource = true;
        }
    }
    
    /*
     * Get the quoted string from the Database metadata. May be NULL
     */
    private String getQuoteString(final Context context, List problems) {
    	String quoteStr =  null;
    	
    	try {
			quoteStr = context.getJdbcDatabase().getDatabaseMetaData().getIdentifierQuoteString();
		} catch (JdbcException ex) {
			final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_quote_string",  //$NON-NLS-1$
					context.getJdbcDatabase().getName()) + ex.getLocalizedMessage();
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, ex);
            problems.add(status);
		} catch (SQLException ex) {
			final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_quote_string",  //$NON-NLS-1$
					context.getJdbcDatabase().getName()) + ex.getLocalizedMessage();
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, ex);
            problems.add(status);
		}
    	return quoteStr;
    }
    
    private ResultSet getCatalogs(final Context context, List problems) {
    	ResultSet catalogs =  null;
    	
    	try {
    		catalogs =  context.getJdbcDatabase().getDatabaseMetaData().getCatalogs();
		} catch (JdbcException ex) {
			final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_catalogs",  //$NON-NLS-1$
					context.getJdbcDatabase().getName()) + ex.getLocalizedMessage();
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, ex);
            problems.add(status);
		} catch (SQLException ex) {
			final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_catalogs",  //$NON-NLS-1$
					context.getJdbcDatabase().getName()) + ex.getLocalizedMessage();
            final IStatus status = new Status(IStatus.ERROR,
                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
                                              msg, ex);
            problems.add(status);
		}
    	return catalogs;
    }
    
    protected String convertName( final String originalName,
                                  final Context context ) {
        String name = originalName;
        if (originalName != null) {
            final JdbcImportSettings settings = context.getJdbcImportSettings();
            final CaseConversion caseConversion = settings.getConvertCaseInModel();
            switch (caseConversion.getValue()) {
                // case CaseConversion.NONE :
                // return name;
                case CaseConversion.TO_LOWERCASE:
                    return name.toLowerCase();
                case CaseConversion.TO_UPPERCASE:
                    return name.toUpperCase();
            }
        }
        return name;
    }

    /**
     * Method to determine a new replacement name for the entity because the entity name in the database is null or zero-length.
     * 
     * @param entity
     * @param context
     * @return
     */
    protected String createReplacementName( final RelationalEntity entity,
                                            final Context context ) {
        return "New" + entity.eClass().getName(); //$NON-NLS-1$
    }

    /**
     * Compute what the name in source should be for the supplied RelationalEntity given the name. Since the JDBC Connector is
     * forming a name by combining the name in source of the table (which should be fully qualified) and the name in source of the
     * column (connected by a dot), we do NOT need to prepend the fully qualified name in this method.
     * 
     * @param object the object on which the name in source is to be computed; never null
     * @param name the name for the entity, and which should be what was found from the JDBC source; note that this may be
     *        different than the {@link RelationalEntity#getName() object.getName()} value
     * @param node the JDBC node that corresponds to this object or that represents the parent of this object
     * @param context the context information
     * @param forced true if the name in source should be computed regardless of the import settings, or false if only the import
     *        settings should be used to determine the form of the name in source. This is set to true when the
     *        {@link RelationalEntity#getName()} for the entity is different from the <code>name</code>
     * @return the name in source, or null if no name in source should be computed
     */
    protected String computeNameInSource( final RelationalEntity object,
                                          final String name,
                                          final JdbcNode node,
                                          final Context context,
                                          final boolean forced,
                                          List problems) {
        if (name == null) {
            return null;
        }

        // It is not forced, so look at the import settings ...
        final JdbcImportSettings settings = context.getJdbcImportSettings();
        
        boolean includeCatalogs = settings.isCreateCatalogsInModel();
        
        ResultSet cats = null;
        String fullyQualifiedName = node.getFullyQualifiedName();
        String finalName = fullyQualifiedName;
        
        if( !includeCatalogs ) {
	        try {
	        	String quoteStr = getQuoteString(context, problems);
	        	
				cats = getCatalogs(context, problems);
		        if( cats != null ) {
		        	int i = 0;
		        	while( cats.next() ) {
		                final String catalogName = cats.getString(i+1);
		                if( catalogName.length() > 0 ) {
		                	String matchStr = catalogName + '.';
		                	if( quoteStr != null ) {
		                		matchStr = quoteStr + catalogName + quoteStr + '.';
		                	}
		                    if( fullyQualifiedName.indexOf(matchStr) > -1) {
		                    	finalName = fullyQualifiedName.replaceFirst(matchStr, StringUtilities.EMPTY_STRING);
		                    	break;
		                    }
		                }
		            }
		        }
			} catch (SQLException ex) {
				final String msg = ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_catalogs",  //$NON-NLS-1$
						context.getJdbcDatabase().getName()) + ex.getLocalizedMessage();
	            final IStatus status = new Status(IStatus.ERROR,
	                                              com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID, 0,
	                                              msg, ex);
	            problems.add(status);
			}
	    }
        
        return finalName;
    }

    /**
     * @return
     */
    public RelationalTypeMapping getTypeMapping() {
        return this.typeMapping;
    }

    /**
     * @param mapping
     */
    public void setTypeMapping( final RelationalTypeMapping mapping ) {
        this.typeMapping = mapping;
    }

    public void setDatatypeManager( final DatatypeManager manager ) {
        this.datatypeManager = manager;
    }

    protected DatatypeManager getDatatypeManager() {
        return this.datatypeManager;
    }

    /**
     * Method called to finalize the ModelAnnotation object and set the various properties. The
     * {@link ModelAnnotation#setPrimaryMetamodelUri(String)} should not be set.
     * 
     * @param modelAnnotation the model annotation;
     */
    protected void updateModelAnnotation( final ModelAnnotation modelAnnotation ) {
        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
        modelAnnotation.setSupportsOuterJoin(true);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor#getDebugLogTiming()
     * @since 4.3
     */
    public boolean getDebugLogTiming() {
        return this.debugTimingEnabled;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor#setDebugLogTiming(boolean)
     * @since 4.3
     */
    public void setDebugLogTiming( boolean logTiming ) {
        this.debugTimingEnabled = logTiming;
    }
}
