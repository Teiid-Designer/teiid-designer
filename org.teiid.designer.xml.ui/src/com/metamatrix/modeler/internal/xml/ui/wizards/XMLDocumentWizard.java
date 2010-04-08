/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.xml.factory.IDocumentsAndFragmentsPopulator;
import com.metamatrix.modeler.internal.xml.factory.VirtualDocumentModelPopulator;
import com.metamatrix.modeler.ui.wizards.INewModelObjectWizard;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.ModelerXmlPlugin;
import com.metamatrix.modeler.xml.PluginConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;
import com.metamatrix.modeler.xml.ui.dialogs.ConvertSimpleTypesToEnteriseTypesDialog;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * XMLDocumentWizard
 */
public class XMLDocumentWizard extends AbstractWizard implements INewModelObjectWizard, ModelerXmlUiConstants {

    static final String DOC_ERROR_MSG = Util.getString("XMLDocumentWizard.documentErrorMessage"); //$NON-NLS-1$

    private ModelResource modelResource;
    private NewVirtualDocumentWizardPage docPage;
    private VirtualDocumentStatisticsWizardPage statsPage;
    private PreviewVirtualDocumentWizardPage previewPage;
    private IWizardPage priorPage;
    private boolean completedOperation = false;
    private NewDocumentWizardModel model;

    // Transaction related objects
    boolean txnStarted = false;
    boolean txnSucceeded = false;
    boolean txnCancelled = false;

    public XMLDocumentWizard() {
        super(ModelerXmlUiPlugin.getDefault(), Util.getString("XMLDocumentWizard.title"), null); //$NON-NLS-1$
    }

    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        setNeedsProgressMonitor(true);
        model = new NewDocumentWizardModel();
        startTransaction();
    }

    // Added the following transaction methods to help encapsulate the creation of XML Documents (via new child)
    // in a single transaction (so UNDO works)
    // Defect 18433 - BML 8/31/05
    private void startTransaction() {
        // Start UoW if neccessary
        txnStarted = ModelerCore.startTxn(true, true, "Build XML Document", this); //$NON-NLS-1$
        txnSucceeded = false;
        txnCancelled = false;
    }

    private void commitTransaction() {
        // Commit txn if we started it.
        if (txnStarted) {
            if (txnSucceeded) {
                ModelerCore.commitTxn();
            } else {
                ModelerCore.rollbackTxn();
            }
        }
    }

    private void cancelTransaction() {
        // Commit txn if we started it.
        if (txnStarted) {
            ModelerCore.rollbackTxn();
        }
    }

    public void setModel( ModelResource model ) {
        this.modelResource = model;
    }

    // public boolean canFinish() {
    // return getContainer().getCurrentPage() != docPage;
    // }

    @Override
    public void addPages() {
        docPage = new NewVirtualDocumentWizardPage(model, null);
        addPage(docPage);
        model.setSource(docPage);
        statsPage = new VirtualDocumentStatisticsWizardPage(model);
        addPage(statsPage);
        previewPage = new PreviewVirtualDocumentWizardPage(model);
        addPage(previewPage);
    }

    /**
     * Overrides super's getNextPage to prepopulate the preview page as needed.
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        // make sure this is for the right page, and is a consequence
        // of hitting 'next', not 'back'
        IWizardPage currP = getContainer().getCurrentPage();

        try {
            DocSrcUpdater dsi = null;

            if (priorPage == docPage && currP == statsPage) {
                dsi = new DocSrcUpdater(statsPage, true, model.getSelectedFragmentCount());
            } else if (currP == previewPage) {
                dsi = new DocSrcUpdater(previewPage, true, model.getEstimatedNodeCount());
            } // endif

            if (dsi != null) {
                // Defect 18433 - BML 8/31/05 - Changed the "fork" argument to FALSE. Forking loses the scope of a transaction
                // breaking the work of building this document into multiple UNDO's
                getContainer().run(false, true, dsi);
            } // endif
        } catch (Exception ex) {
            Util.log(ex);
        } // endtry

        priorPage = currP;

        return super.getNextPage(page);
    }

    @Override
    public boolean finish() {
        completedOperation = finishWizard(docPage, previewPage, modelResource, getContainer(), model);
        if (completedOperation) {
            txnSucceeded = true;
            commitTransaction();
        }
        return completedOperation;
    }

    /**
     * Perform the finishing work of a wizard.
     * 
     * @param docPage The page specifying schema information
     * @param previewPage The page that shows a preview
     * @param modelResource The modelResource to write to
     * @param container The IWizardContainer running the process
     * @return true if the operation completed successfully, false otherwise.
     */
    public static boolean finishWizard( final NewVirtualDocumentWizardPage docPage,
                                        final PreviewVirtualDocumentWizardPage previewPage,
                                        final ModelResource modelResource,
                                        final IWizardContainer container,
                                        final NewDocumentWizardModel wizModel ) {
        // save pref:
        final boolean useXsdTypes = wizModel.getUseSchemaTypes();
        ModelerXmlPlugin.getDefault().getPluginPreferences().setValue(PluginConstants.PreferenceKeys.MAPPING_TYPE_FROM_XSD,
                                                                      useXsdTypes);
        ModelerXmlPlugin.getDefault().savePluginPreferences();

        // Get handle to DatatypeManager
        final DatatypeManager dtMgr = ModelerCore.getWorkspaceDatatypeManager();

        // simple hack to let me change a variable in the runnable:
        final boolean[] completedOperation = new boolean[1];
        // build the documents:
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor monitor ) {
                try {
                    IDocumentsAndFragmentsPopulator populator = docPage.getPopulator();

                    // the pages will generate the documents we need, though we have to
                    // add the data to the modelResource and build mapping classes
                    // ourselves.
                    IWizardPage currentPage = container.getCurrentPage();
                    boolean buildMappingClasses = wizModel.getBuildMappingClasses();
                    Collection fragments = new ArrayList();
                    if (currentPage != previewPage) {
                        // We are skipping the preview, just add the roots:
                        // This is a new path for Large Model performance. Don't
                        // update the fragments via the previewPage logic as it forces
                        // creation of an entire UI Component.
                        fragments = previewPage.getRoots(modelResource, monitor);
                        final Resource res = modelResource.getEmfResource();
                        if (res instanceof MtkXmiResourceImpl && !buildMappingClasses) {
                            // Most optimized path, but will not create notifications and requires
                            // user to reopen the editor to see the changes.
                            ((MtkXmiResourceImpl)res).addMany(fragments);
                        } else {
                            final EList contents = modelResource.getEmfResource().getContents();
                            // Check if any/all fragments have been already added to this resource or not. Don't want to add
                            // them twice.
                            Set missingFragments = new HashSet();

                            for (Iterator iter = fragments.iterator(); iter.hasNext();) {
                                Object obj = iter.next();
                                if (!contents.contains(obj)) {
                                    missingFragments.add(obj);
                                }
                            }

                            if (!missingFragments.isEmpty()) {
                                ModelerCore.getModelEditor().addValue(res, missingFragments, contents);
                            }
                        }
                    } else {
                        // This forces preview page logic to ensure unchecked entites get removed.
                        final XmlFragment[] xfs = previewPage.getFragments(modelResource, monitor);
                        fragments = Arrays.asList(xfs);

                        final Resource res = modelResource.getEmfResource();
                        final EList contents = modelResource.getEmfResource().getContents();
                        ModelerCore.getModelEditor().addValue(res, fragments, contents);
                    }

                    if (buildMappingClasses && populator != null) {
                        final Iterator roots = fragments.iterator();
                        while (roots.hasNext()) {
                            final XmlFragment fragment = (XmlFragment)roots.next();
                            populator.buildMappingClasses(fragment, wizModel.getMappingClassBuilderStrategy());
                        } // end while
                    }

                    // force GC
                    System.gc();
                    Thread.yield();

                    // If the user selected "Use XML types from the document" we will check
                    // to see if theier are any Simple Type elements selected that are not
                    // Enterprise Types. If we find any, we will ask the user if they would
                    // like to convert the Simple Types to Enterprise Datatypes. If yes,
                    // we load the xsd resource and update the Simple Types that meet our criteria:
                    // 1.) They were selected
                    // 2.) They are Simple Types that are not Enterprise Datatypes.
                    if (useXsdTypes && populator != null) {
                        // Determine if any of the accumulated datatypes are SimpleTypes and not Enterprise Datatypes
                        HashMap simpleTypeMap = new HashMap();
                        final Iterator accumulatedDatatypes = ((VirtualDocumentModelPopulator)populator).getAccumulatedDatatypes().iterator();
                        while (accumulatedDatatypes.hasNext()) {
                            Object type = accumulatedDatatypes.next();
                            // Add to hash if SimpleType and not Enterprise Datatype
                            if (type instanceof XSDSimpleTypeDefinition && !dtMgr.isEnterpriseDatatype((EObject)type)) {
                                simpleTypeMap.put(((XSDSimpleTypeDefinition)type).getName(),
                                                  ((XSDSimpleTypeDefinition)type).getSchema());
                            }
                        }

                        // If we found selected SimpleTypes that are not Enterprise Datatypes, prompt the user
                        // to let us convert them.
                        if (simpleTypeMap.size() > 0) {
                            Set resources = new HashSet();
                            ModelResource modelResrc = ModelUtilities.getModelResource((IFile)populator.getItem(), true);
                            if (modelResrc != null) {
                                Resource xsdResource = modelResrc.getEmfResource();
                                resources.add(xsdResource);
                                loadReferencedXsdResources(xsdResource, resources, xsdResource.getResourceSet());
                                Set affectedSchemas = getAffectedSchemas(resources, simpleTypeMap);
                                // Case 5229 - if referenced schemas are not in workspace, skip them.
                                removeExternalSchemas(affectedSchemas);
                                if (!affectedSchemas.isEmpty() && displayDialog(affectedSchemas)) {
                                    // check whether all the affected schemas are writable
                                    Iterator iter = affectedSchemas.iterator();
                                    while (iter.hasNext()) {
                                        Resource resource = (Resource)iter.next();
                                        if (ModelUtilities.getModelResource(resource, false).isReadOnly()) {
                                            throw new IOException(Util.getString("XMLDocumentWizard.readOnly", resource.getURI())); //$NON-NLS-1$
                                            // readOnlySchemas.add(resource);
                                        }
                                    }
                                    convertToEnterpriseType(affectedSchemas, simpleTypeMap, monitor);
                                }
                            }
                        }
                    }

                    // force GC
                    System.gc();
                    Thread.yield();

                    // save:
                    modelResource.save(monitor, true);
                    modelResource.getEmfResource().setModified(false);

                    completedOperation[0] = true;
                } catch (ModelWorkspaceException ex) {
                    Util.log(IStatus.ERROR, ex, DOC_ERROR_MSG);
                } catch (ModelerCoreException ex) {
                    Util.log(IStatus.ERROR, ex, DOC_ERROR_MSG);
                } catch (Exception ex) {
                    Util.log(IStatus.ERROR, ex, DOC_ERROR_MSG);
                } finally {
                    monitor.done();
                }
            }

            private boolean displayDialog( Set resources ) {
                ConvertSimpleTypesToEnteriseTypesDialog dialog = null;

                dialog = new ConvertSimpleTypesToEnteriseTypesDialog(null, resources);
                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    return true;
                }

                return false;
            }

            /**
             * Helper method to remove any schemas from the supplied set which cannot be found in the workspace
             * 
             * @param schemas the supplied set of schemas
             */
            private void removeExternalSchemas( Set schemas ) {
                Iterator iter = schemas.iterator();
                while (iter.hasNext()) {
                    Resource resource = (Resource)iter.next();
                    ModelResource modelRsrc = null;
                    modelRsrc = ModelUtilities.getModelResource(resource, false);
                    if (modelRsrc == null) {
                        iter.remove();
                    }
                }
            }

            /**
             * Load the xsd resource, find the Simple Types that have been selected to use in the generated xml document and
             * convert them to Enterprise Datatypes.
             * 
             * @param affectedSchemas
             * @param simpleTypeSet
             * @param monitor
             * @throws ModelWorkspaceException
             */
            private void convertToEnterpriseType( final Set resources,
                                                  final HashMap simpleTypeMap,
                                                  final IProgressMonitor monitor ) throws Exception {

                WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
                    @Override
                    protected void execute( IProgressMonitor monitor ) {
                        XSDSchema schema = null;
                        final ModelEditor me = ModelerCore.getModelEditor();

                        final Iterator xsdIter = resources.iterator();
                        while (xsdIter.hasNext()) {
                            boolean updated = false;
                            final XSDResourceImpl xsdResource = (XSDResourceImpl)xsdIter.next();
                            final Iterator eObjects = xsdResource.getContents().iterator();
                            while (eObjects.hasNext()) {
                                Object next = eObjects.next();
                                if (next instanceof XSDSchema) {
                                    // turn off notifications for Schema until
                                    // we are done
                                    schema = (XSDSchema)next;
                                    schema.setIncrementalUpdate(false);

                                    final Iterator children = ((XSDSchema)next).eContents().iterator();

                                    while (children.hasNext()) {
                                        final Object child = children.next();
                                        // If this is a Simple Type and one of
                                        // the selected fragments for the document,
                                        // convert it to an Enterprise Datatype.
                                        if (child instanceof XSDSimpleTypeDefinition) {
                                            Object value = simpleTypeMap.get(((XSDSimpleTypeDefinition)child).getName());
                                            if (value != null
                                                && ((XSDSchema)value).getSchemaLocation().equals(((XSDSchema)next).getSchemaLocation())) {
                                                EnterpriseDatatypeInfo edi = getEDIForType((XSDSimpleTypeDefinition)child);
                                                me.setEnterpriseDatatypePropertyValue((XSDSimpleTypeDefinition)child, edi);
                                                updated = true;
                                            }
                                        }
                                    }
                                }
                            }
                            try {
                                if (updated) {
                                    xsdResource.save(new HashMap());
                                    IResource iResource = ModelUtilities.getModelResource(xsdResource, true).getResource();
                                    iResource.refreshLocal(IResource.DEPTH_ZERO, null);
                                }
                            } catch (Exception err) {
                                final String msg = Util.getString("XMLDocumentWizard.convertToEnterpriseTypesSaveError"); //$NON-NLS-1$
                                Util.log(IStatus.ERROR, err, msg);
                            }
                        }
                    }
                };
                try {
                    new ProgressMonitorDialog(null).run(false, false, operation);
                } catch (InterruptedException e) {
                    Util.log(IStatus.ERROR, e, DOC_ERROR_MSG);
                } catch (InvocationTargetException e) {
                    Util.log(IStatus.ERROR, e, DOC_ERROR_MSG);
                }
            }

            /**
             * Determine which schema files will be affected by changing selected Simple Types to Enterprise types and return the
             * files in a Set.
             * 
             * @param resources the root schema and all dependents
             * @param simpleTypeSet
             */
            private Set getAffectedSchemas( Set resources,
                                            final HashMap simpleTypeMap ) {

                Set affectedSchemas = new HashSet();

                final Iterator xsdIter = resources.iterator();
                while (xsdIter.hasNext()) {
                    final XSDResourceImpl xsdResource = (XSDResourceImpl)xsdIter.next();
                    final Iterator eObjects = xsdResource.getContents().iterator();
                    while (eObjects.hasNext()) {
                        Object next = eObjects.next();

                        final Iterator children = ((XSDSchema)next).eContents().iterator();

                        while (children.hasNext()) {
                            final Object child = children.next();
                            // If this is a Simple Type and one of the selected fragments for the document,
                            // convert it to an Enterprise Datatype.
                            if (child instanceof XSDSimpleTypeDefinition) {
                                Object value = simpleTypeMap.get(((XSDSimpleTypeDefinition)child).getName());
                                if (value != null
                                    && ((XSDSchema)value).getSchemaLocation().equals(((XSDSchema)next).getSchemaLocation())) {
                                    affectedSchemas.add(xsdResource);
                                }
                            }
                        }
                    }
                }
                return affectedSchemas;
            }

            /**
             * Recursively loads a set of dependent schema resources
             * 
             * @param xsdSource - the root schema
             * @param resources - the set of dependent resources
             * @param container - the resource set from the current schema
             */
            private void loadReferencedXsdResources( Resource xsdSource,
                                                     final Set resources,
                                                     final ResourceSet container ) throws Exception {

                // Get a ResourceFinder to use when resolving dependent resource references
                CoreArgCheck.isNotNull(ModelerCore.getContainer(xsdSource));
                ResourceFinder finder = ModelerCore.getContainer(xsdSource).getResourceFinder();

                if (!xsdSource.isLoaded()) {
                    ResourceSet ctnr = xsdSource.getResourceSet();
                    if (ctnr == null) {
                        ctnr = ModelerCore.getModelContainer();
                    }
                    final Map options = (ctnr == null ? null : ctnr.getLoadOptions());
                    xsdSource.load(options);
                }

                XSDSchema schema = ((XSDResourceImpl)xsdSource).getSchema();

                for (final Iterator contents = schema.getContents().iterator(); contents.hasNext();) {
                    final Object obj = contents.next();
                    if (obj instanceof XSDSchemaDirective) {
                        xsdSource = finder.findByImport((XSDSchemaDirective)obj, false);
                        if (xsdSource != null && xsdSource.getResourceSet() == container) {
                            // Load referenced XSD resources
                            if (resources.add(xsdSource)) {
                                // Recurse to get the referenced resource
                                loadReferencedXsdResources(xsdSource, resources, container);
                            }
                        }
                    }
                }
            }

            /**
             * Returns the <code>EnterpriseDatatypeInfo</code> object for a Simple Type
             * 
             * @param type
             * @return edi EnterpriseDatatypeInfo
             */
            EnterpriseDatatypeInfo getEDIForType( final XSDSimpleTypeDefinition type ) {
                final EnterpriseDatatypeInfo edi = new EnterpriseDatatypeInfo();

                XSDSimpleTypeDefinition superType = type;
                XSDSimpleTypeDefinition enterpriseParent = null;
                while (superType != null && enterpriseParent == null) {
                    if (dtMgr.isEnterpriseDatatype(superType)) {
                        enterpriseParent = superType;
                    } else {
                        XSDSimpleTypeDefinition tmp = superType.getBaseTypeDefinition();
                        if (tmp != superType) {
                            superType = superType.getBaseTypeDefinition();
                        } else {
                            superType = null;
                        }
                    }
                }

                if (enterpriseParent != null) {
                    edi.setRuntimeTypeFixed(dtMgr.getRuntimeTypeFixed(enterpriseParent));
                    edi.setRuntimeType(dtMgr.getRuntimeTypeName(enterpriseParent));
                }

                ModelEditorImpl.fillWithDefaultValues(edi, type);
                return edi;
            }
        };
        try {
            container.run(false, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            String message = realException.getMessage();
            if (message == null) {
                message = realException.getClass().getName();
            }
            MessageDialog.openError(container.getShell(), Util.getString("XMLDocumentWizard.error"), //$NON-NLS-1$
                                    message);
            ModelerXmlUiConstants.Util.log(realException);
            return false;
        }
        return completedOperation[0];
    }

    public boolean completedOperation() {
        return completedOperation;
    }

    /**
     * Overrode super method so we could coordinate a "cancel" functionality on the transaction.
     * 
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     * @since 4.3
     */
    @Override
    public boolean performCancel() {
        cancelTransaction();
        return super.performCancel();
    }

    /**
     * @see com.metamatrix.modeler.ui.wizards.INewModelObjectWizard#setCommand(org.eclipse.emf.common.command.Command)
     */
    public void setCommand( Command descriptor ) {
    }

    public static class DocSrcUpdater implements IRunnableWithProgress {
        // Instance variables:
        private int units;
        private IVirtualDocumentFragmentSource srcToUpdate;
        private boolean isVis;

        // Constructors:
        public DocSrcUpdater( IVirtualDocumentFragmentSource thingToUpdate,
                              boolean isVisible,
                              int nodes ) {
            srcToUpdate = thingToUpdate;
            isVis = isVisible;
            units = nodes * 2;
        }

        // Implementation of the IRunnableWithProgress interface:
        public void run( IProgressMonitor monitor ) {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            } // endif
            monitor.beginTask(Util.getString("XMLDocumentWizard.taskGathering"), units); //$NON-NLS-1$
            srcToUpdate.updateSourceFragments(isVis, monitor);
            monitor.done();
        }
    }
}
