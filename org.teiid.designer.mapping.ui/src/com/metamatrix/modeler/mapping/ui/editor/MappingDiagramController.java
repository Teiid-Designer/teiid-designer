/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.CoordinateListener;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramSelectionHandler;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.mapping.ui.model.MappingDiagramModelFactory;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramEditPart;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramEditPart.TopAndBottomClassifierInfo;
import com.metamatrix.modeler.ui.event.IRevealHideListener;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

public class MappingDiagramController
    implements DiagramController, ISelectionChangedListener, ControlListener, IRevealHideListener, UiConstants {

    DocumentTreeController documentController;
    DiagramEditor diagramEditor;
    int scrollBarYPosition = 0;
    int treeYOrigin = 0;
    private ScrollBar scrollBar = null;
    private int mappingType = PluginConstants.COARSE_MAPPING;
    private SelectionAdapter verticalScrollAdapter;
    private DocumentNotificationHandler notificationHandler;
    private CoordinateListener coordinateListener;
    private Diagram currentDiagram;
    boolean limitAutoScroll = false;
    private static final int SCROLL_OBJECT_LIMIT = 100;
    private static final int SCROLL_EXTENT_LIMIT = 40;

    static final String PREFIX = I18nUtil.getPropertyPrefix(MappingDiagramController.class);

    private List visibleMappingClasses = new ArrayList();
    private boolean bSynchronizeInProgress = false;

    private MappingDiagramBehavior mappingDiagramBehavior;
    private IDoubleClickListener dclDocumentTreeDoubleClickListener;

    public MappingDiagramController( DiagramEditor editor ) {
        super();
        this.diagramEditor = editor;

        this.documentController = new DocumentTreeController(this);
        documentController.createControl((Composite)diagramEditor.getPrimaryControl().getSashForm());

        documentController.getControl().moveAbove(diagramEditor.getDiagramViewer().getControl());
        diagramEditor.getPrimaryControl().setControllerControl(documentController.getControl());
        diagramEditor.getModelObjectSelectionProvider().addSelectionChangedListener(documentController);
    }

    public void setDiagramEditor( DiagramEditor editor ) {
        diagramEditor = editor;
    }

    public ModelResource getCurrentModelResource() {
        ModelResource mr = null;

        if (diagramEditor != null && diagramEditor.getDiagram() != null) {
            mr = ModelUtilities.getModelResourceForModelObject(diagramEditor.getDiagram());
        }

        return mr;
    }

    public EObject getDocumentEObject() {
        EObject targetEO = null;

        if (getMappingType() == PluginConstants.DETAILED_MAPPING) {
            MappingClass mappingClassEO = (MappingClass)currentDiagram.getTarget();
            targetEO = mappingClassEO.getMappingClassSet().getTarget();
        } else targetEO = currentDiagram.getTarget();
        return targetEO;
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        // System.out.println("[MappingDiagramController.selectionChanged(IWorkbenchPart part, ISelection selection)] TOP!!!");
        List selectedEObjects = SelectionUtilities.getSelectedEObjects(selection);
        if (!selectedEObjects.isEmpty()) {
            getDocumentTreeController().setSelection(selection);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        // System.out.println("[MappingDiagramController.selectionChanged(SelectionChangedEvent event)] TOP!!!");
        ISelection selection = event.getSelection();
        List selectedEObjects = SelectionUtilities.getSelectedEObjects(selection);
        if (!selectedEObjects.isEmpty()) {
            diagramEditor.getDiagramViewer().getSelectionHandler().select(selection);
            // Now we need to tell the extents to reposition themselves because the selection may have auto-scrolled the diagram.
            if (diagramEditor.getControl() != null && !diagramEditor.getControl().isDisposed()) {
                documentController.resetExtentsFromDocument();
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#wireDiagram(com.metamatrix.metamodels.diagram.Diagram)
     */
    public void wireDiagram( Diagram theInput ) {
        // System.out.println("[MappingDiagramController.wireDiagram] TOP!!!");
        try {
            currentDiagram = theInput;
            // Let's set mapping type;
            if (theInput.getType() != null && theInput.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) setMappingType(PluginConstants.DETAILED_MAPPING);
            else setMappingType(PluginConstants.COARSE_MAPPING);

            addVerticalScrollListener();
            addCoordinateListener();

            // Let's get the diagram's target and that should be the input to the viewer
            // This needs to be the document. If diagram is "Detailed" we need to find the document from this.

            EObject targetEO = null;

            if (getMappingType() == PluginConstants.DETAILED_MAPPING) {
                MappingClass mappingClassEO = (MappingClass)theInput.getTarget();
                targetEO = mappingClassEO.getMappingClassSet().getTarget();

                // jh Defect 18919: Set up double click listening on the document tree
                addDocumentTreeListener();

            } else targetEO = theInput.getTarget();

            if (targetEO != null) {
                documentController.getViewer().setXmlMappingTarget(targetEO);
                DocumentContentProvider provider = new DocumentContentProvider(this);
                documentController.getViewer().setContentProvider(provider);
                documentController.addSelectionChangedListener(this);
                documentController.getViewer().setInput(targetEO);

                // ExpandAll only if the MappingClass count is less than 20, otherwise only expand to first level.
                documentController.getViewer().getControl().setRedraw(false);
                // System.out.println("[MappingDiagramController.wireDiagram] About to call: diagramEditor.getDiagramViewForm().setRedraw( false ) ");

                // diagram
                diagramEditor.getDiagramViewForm().setRedraw(false);

                /*
                 * jh research for Defect 18038: applying a previous Expanded State to the Mapping tree
                 *      == This appears to be the point of expanding the tree when
                 *      == setting the tree up for the first time.
                 *      
                 *      == The enhancement would be to
                 *          0. if the type is 'coarse':
                 *          1. discover if we have previously saved an expanded state
                 *             for this XML model
                 *             diagramEditor
                 *             diagramEditor.getCurrentModel()
                 *          2. if so, apply it to the viewer like this:
                 *      viewer.setExpandedElements( oExpandedElements );
                 *      
                 *          3. if not, do expandAll()      
                 *          
                 */

                /*
                 * jh Lyra enh:
                 * Further Issue: Should this autoexpand be done in a separate thread to
                 * avoid some looping problem???????  Or should we turn some listening off
                 * before we do it??????????
                 */
                if (getMappingType() == PluginConstants.COARSE_MAPPING) {
                    Object[] oExpandedElements = (Object[])diagramEditor.getTreeStatesMap().get(getDocumentEObject());
                    if (oExpandedElements != null) {
                        documentController.getViewer().setExpandedElements(oExpandedElements);
                    } else {
                        doDefaultExpansion();
                    }
                } else {
                    doDefaultExpansion();
                }

                // tree
                // System.out.println("[MappingDiagramController.wireDiagram] About to call: diagramEditor.getDiagramViewForm().setRedraw( true ) ");
                documentController.getViewer().getControl().setRedraw(true);

                // diagram
                diagramEditor.getDiagramViewForm().setRedraw(true);

                ITreeToRelationalMapper mapper = ModelMapperFactory.createModelMapper(targetEO);
                notificationHandler = new DocumentNotificationHandler(this, mapper.getMappableTree());

                documentController.getViewer().setMappingType(getMappingType());

                // jh Lyra enh: REDUNDANT, DUPLICATES LINE 177!:
                // documentController.getViewer().setXmlMappingTarget(targetEO);
                documentController.getViewer().getTree().getVerticalBar().setSelection(0);
            }
        } catch (Exception ex) {
            Util.log(IStatus.ERROR, ex, ex.getClass().getName());
        }

        refresh(false);

        // if(getMappingType() == PluginConstants.DETAILED_MAPPING ) {
        // // Let's find the mappingClass eObject here and tell the diagram to select it!
        // // Should be diagram target
        //                    
        // EObject mappingClassEObject = ((Diagram)theInput).getTarget();
        // diagramEditor.getDiagramViewer().getSelectionHandler().select(mappingClassEObject);
        // }

        // System.out.println("[MappingDiagramController.wireDiagram] BOT!!!");

    }

    private void addDocumentTreeListener() {

        if (dclDocumentTreeDoubleClickListener == null) {
            dclDocumentTreeDoubleClickListener = new IDoubleClickListener() {

                public void doubleClick( DoubleClickEvent event ) {

                    IStructuredSelection sel = (IStructuredSelection)event.getSelection();
                    Object element = sel.getFirstElement();

                    if (element instanceof EObject) {
                        EObject selectedEObject = (EObject)element;

                        // get MappingClass for this treenode

                        // 1. See if this node is a Mapping Class Root
                        MappingClass mc = getMappingAdapter().getMappingClass(selectedEObject);
                        if (mc == null) {
                            mc = getMappingAdapter().getStagingTable(selectedEObject);
                        }

                        // 2. if not a root, see if it is a mappable column, and get the Mapping Class it is capable of mapping
                        // to.
                        // (This should work whether or not the column is currently mapped.)
                        if (mc == null) {
                            mc = getMappingAdapter().getMappingClass(selectedEObject);
                        }

                        // if we found a Mapping Class using one of the two methods, open the Detailed diagram on that mc
                        if (mc != null) {
                            MappingDiagramSelectionHandler mdsh = (MappingDiagramSelectionHandler)diagramEditor.getDiagramViewer().getSelectionHandler();

                            mdsh.handleDoubleClick(mc);
                        }
                    }
                }
            };
        }

        documentController.getViewer().addDoubleClickListener(dclDocumentTreeDoubleClickListener);
    }

    private void removeDocumentTreeListener() {
        if (dclDocumentTreeDoubleClickListener != null) {
            documentController.getViewer().removeDoubleClickListener(dclDocumentTreeDoubleClickListener);
        }
    }

    private void doDefaultExpansion() {
        // System.out.println("[MappingDiagramController.doDefaultExpansion] TOP");
        /*
         * jh Lyra enh: Why are we hardcoding true here?  Why aren't we listening to the events and keeping
         *   our internal state up to date, so we do not have to rebuild it from scratch everytime someone
         *   expands a tree node?????
         *   
         *   Note: Changing true to false here and one other place Seems To Be Working!
         */
        TreeMappingAdapter mappingAdapter = documentController.getViewer().getMappingAdapterFilter(false).getMappingAdapter();

        List mappingClasses = mappingAdapter.getAllMappingClasses();

        /*
         * jhTODO Lyra enh: We will at least formalize Mark's performance fix as a preference.  For now,
         *              I am adjusting it downward to assist my own testing (to avoid expandall).
         */

        if (mappingClasses.size() < getLargeMappingClassBreakpointPreference()) {
            documentController.getViewer().expandAll();
        } else {
            documentController.getViewer().expandToLevel(getExpandLevelPreference());
        }
    }

    private int getExpandLevelPreference() {
        // set last-ditch default, in case pref is not available
        int iResult = 1;

        String sExpandLevelVal = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_TARGET_LEVEL);
        try {

            Integer IVal = new Integer(sExpandLevelVal);
            if (IVal.intValue() > 0) {
                iResult = IVal.intValue();
            }

        } catch (Exception e) {
            // no action
        }

        return iResult;
    }

    private int getLargeMappingClassBreakpointPreference() {
        // set last-ditch default, in case pref is not available
        int iResult = 20;

        String sMaxMappingVal = UiPlugin.getDefault().getPreferenceStore().getString(PluginConstants.Prefs.AUTO_EXPAND_MAX_MAPPING_CLASSES);
        try {

            Integer IVal = new Integer(sMaxMappingVal);
            if (IVal.intValue() > 0) {
                iResult = IVal.intValue();
            }

        } catch (Exception e) {
            // no action
        }

        return iResult;
    }

    public void deactivate() {
        diagramEditor.getModelObjectSelectionProvider().removeSelectionChangedListener(documentController);

        removeVerticalScrollListener();
        removeCoordinateListener();

        /*
         * jh research for Defect 18038: saving the Expanded State from the Mapping tree
         *      == This ( deactivete() appears to be the point at which we should
         *      == save the expanded state of the tree.
         *
         *      Code will look like this:
         *          Object[] oExpandedElements = viewer.getExpandedElements();
         *          
         *      Still need to determine where we can persist this info and for what
         *      lifecycle period (Session; or between sessions).          
         *
         */
        if (getMappingType() == PluginConstants.COARSE_MAPPING) {
            Object[] oExpandedElements = documentController.getViewer().getExpandedElements();
            diagramEditor.getTreeStatesMap().put(getDocumentEObject(), oExpandedElements);
        }

        // jh Defect 18919: Remove double click listening on the document tree
        removeDocumentTreeListener();

    }

    public void dispose() {
        // Tell DocumentTreeController to clean up it's listeners....
        // Defect 22290 reflects memory (leaks) issues within designer.
        // Need to tell controller to clean up somehow. Added method dispose() to controller.
        documentController.dispose();
    }

    /**
     * @return
     */
    public int getMappingType() {
        return mappingType;
    }

    public DocumentTreeController getDocumentTreeController() {
        return documentController;
    }

    /**
     * @param i
     */
    public void setMappingType( int i ) {
        mappingType = i;
    }

    void refreshDiagram( IProgressMonitor theMonitor,
                         boolean forceMappingClassReconcile ) {
        IProgressMonitor monitor = theMonitor;
        try {
            boolean showProgress = (monitor != null);

            if (showProgress) {
                monitor.subTask(Util.getString(PREFIX + "monitorRefreshDiagram")); //$NON-NLS-1$
            }

            if (diagramEditor.getDiagram() != null && diagramEditor.getCurrentModel() != null) {

                /*
                 * jh Lyra enh: Why are we hard-coding this to true?  Try false.
                 */
                MappingAdapterFilter mappingFilter = documentController.getViewer().getMappingAdapterFilter(false);
                MappingDiagramModelFactory modelFactory = (MappingDiagramModelFactory)DiagramUiPlugin.getDiagramTypeManager().getDiagram(PluginConstants.MAPPING_DIAGRAM_TYPE_ID).getModelFactory();

                if (showProgress) {
                    showProgress = !mappingFilter.getMappingAdapter().getAllMappingClasses().isEmpty();
                }

                if (!showProgress) {
                    monitor = new NullProgressMonitor();
                }

                // -------------------------------------------------
                // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
                // will result in only one transaction?
                // -------------------------------------------------
                boolean requiredStart = ModelerCore.startTxn(false, false, "Refresh Diagram", this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    if (showProgress) {
                        monitor.subTask(Util.getString(PREFIX + "taskRefreshDiagram")); //$NON-NLS-1$
                        monitor.worked(20);
                    }
                    modelFactory.refresh(diagramEditor.getCurrentModel(),
                                         diagramEditor.getDiagram(),
                                         mappingFilter,
                                         forceMappingClassReconcile,
                                         monitor);
                    // Make a call to synch. up the extents and new scrollbar in diagram.
                    if (showProgress) {
                        monitor.subTask(Util.getString(PREFIX + "taskAutolayout")); //$NON-NLS-1$
                        monitor.worked(10);
                    }

                    updateForAutoLayout();

                    /*
                     * jh Lyra enh/defect 20457: Losing selection: The following code is explicitly selecting 
                     *                           the current MAPPING CLASS.  no wonder I cannot get an extent selection
                     *                           to persist.  Dropping this.
                     */

                    // if( getMappingType() == PluginConstants.DETAILED_MAPPING ) {
                    // EObject mappingClassEObject = diagramEditor.getDiagram().getTarget();
                    // diagramEditor.getDiagramViewer().getSelectionHandler().select(mappingClassEObject);
                    // }
                    MappingDiagramUtil.hiliteUnconnectedExtents(diagramEditor.getCurrentModel());
                    succeeded = true;
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

                // -------------------------------------------------
            }
        } catch (Exception ex) {
            UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName());
        } finally {
            limitAutoScroll = false;
            if (diagramEditor != null && diagramEditor.getCurrentModel() != null
                && diagramEditor.getCurrentModel().getChildren() != null) {
                int nExt = getNumberOfExtents(diagramEditor.getCurrentModel());
                limitAutoScroll = (diagramEditor.getCurrentModel().getChildren().size() > SCROLL_OBJECT_LIMIT)
                                  || nExt > SCROLL_EXTENT_LIMIT;
            }
        }

        // System.out.println("\n[MappingDiagramController.refreshDiagram] BOT" );
    }

    /**
     * Determine if mapping classes have changed since last refresh.
     * 
     * @return true if mappingClasses have changed, false if not
     */
    private boolean mappingClassesChanged() {
        // System.out.println("\n[MappingDiagramController.mappingClassesChanged] TOP ");
        boolean haveChanged = true;

        // Get Current MappingClass list
        /*
         * jh Lyra enh: Why are we hardcoding true here?  Why aren't we listening to the events and keeping
         *   our internal state up to date, so we do not have to rebuild it from scratch everytime someone
         *   expands a tree node?????
         *   
         *   Note: Changing true to false here and one other place Seems To Be Working!
         *   Wait...The 'getMappingAdapterFilter( true )' call may have been the only way for us to update
         *     the inventory of visible mapping classes.  Try setting it back to true.  
         *   Ok, 'true' gets us back into the situation where all components subordinate to the MappingAdapterFilter
         *   are recreated many times.  Instead let's fix MAFilter so we can get it to refresh Mapping Classes
         *   by calling a method.  Duh.  
         */
        // MappingAdapterFilter mappingFilter = documentController.getViewer().getMappingAdapterFilter(false);
        // Ok, this is a low-frequency call, so we can try using TRUE
        /*
         * jhTODO jh Lyra enh: This is the heart of our performance improvement.  If we can ensure that we only set this arg to true
         * when something has been added or removed, that would be best.  
         */
        // System.out.println("[MappingDiagramController.mappingClassesChanged] About to call 'getMappingAdapterFilter(true)' ");
        // jh Lyra enh: debugging 1 27 2006: we are creating TreeMappingAdapter multiple times;
        // try setting this to false:
        // MappingAdapterFilter mappingFilter = documentController.getViewer().getMappingAdapterFilter( true );
        MappingAdapterFilter mappingFilter = documentController.getViewer().getMappingAdapterFilter(false);

        List currentMappingClasses = mappingFilter.getMappedClassifiers();

        // if list sizes are the same, do further checking between the saved list and current list.
        if (!visibleMappingClasses.isEmpty() && visibleMappingClasses.size() == currentMappingClasses.size()) {
            boolean allSame = true;
            Iterator currIter = currentMappingClasses.iterator();
            while (currIter.hasNext()) {
                // If difference found, set false and break
                if (!visibleMappingClasses.contains(currIter.next())) {
                    allSame = false;
                    break;
                }
            }
            // if allSame, set overall status false - no changes detected
            if (allSame) {
                haveChanged = false;
            }
        }
        // Update stored MappingClass List
        visibleMappingClasses = currentMappingClasses;
        // System.out.println("\n[MappingDiagramController.mappingClassesChanged] About to return: " + haveChanged);
        return haveChanged;
    }

    private int getNumberOfExtents( Object diagramModelNode ) {
        MappingDiagramModelFactory modelFactory = (MappingDiagramModelFactory)DiagramUiPlugin.getDiagramTypeManager().getDiagram(PluginConstants.MAPPING_DIAGRAM_TYPE_ID).getModelFactory();
        if (modelFactory != null) return modelFactory.getNumberOfMappingExtents(diagramModelNode);

        return 0;
    }

    public void refresh( boolean forceMappingClassReconcile ) {
        // System.out.println("[MappingDiagramController.refresh] TOP; forceMappingClassReconcile is: " +
        // forceMappingClassReconcile );

        // Check if document object exists or not (i.e. there may have been a delete model)
        if (getDocumentEObject() == null || getDocumentEObject().eResource() == null) {
            return;
        }
        /*
         * jh Lyran enh: Important fix:  Adding a 'force' flag to this method and the ones
         *               it calls fixed the problem of a new staging table not being displayed
         *               in the diagram until you collapsed and re expanded its parent in the tree.
         */
        MappingAdapterFilter filter = getMappingFilter(forceMappingClassReconcile);

        final boolean reconcileMappingClasses = (forceMappingClassReconcile) ? true : mappingClassesChanged();

        if (filter != null) {
            // -----------------------------
            // Defect 23360
            // this little block of code was throwing NPE because it was outside the filter != null above.
            if (forceMappingClassReconcile) {
                filter.setTreeExpansionMonitorStale();
            }

            int nVisibleObjects = filter.getNumberVisibleNodes();

            if (nVisibleObjects < 60) {
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        refreshDiagram(null, reconcileMappingClasses);
                    }
                });
            } else {
                final IRunnableWithProgress op = new IRunnableWithProgress() {
                    public void run( final IProgressMonitor monitor ) {
                        monitor.beginTask(Util.getString(PREFIX + "taskMappingDiagram"), 100); //$NON-NLS-1$

                        diagramEditor.getDiagramViewForm().setRedraw(false);

                        refreshDiagram(monitor, reconcileMappingClasses);

                        diagramEditor.getDiagramViewForm().setRedraw(true);
                    }
                };

                try {
                    final ProgressMonitorDialog dlg = new ProgressMonitorDialog(documentController.getControl().getShell());
                    dlg.run(false, true, op);
                    if (dlg.getProgressMonitor().isCanceled()) {
                        return;
                    }
                } catch (final InterruptedException ignored) {
                } catch (final Exception err) {
                }
            }
        }
    }

    // Method used by DocumentTree
    public void resetExtentLocations( int newY ) {
        final int tempY = newY;
        if (diagramEditor.getDiagram() != null) {
            final Diagram thisDiagram = diagramEditor.getDiagram();
            final DiagramModelNode diagramNode = diagramEditor.getCurrentModel();
            if (thisDiagram != null && diagramNode != null) {
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        MappingAdapterFilter mappingFilter = documentController.getMappingAdapterFilter();
                        MappingDiagramModelFactory modelFactory = (MappingDiagramModelFactory)DiagramUiPlugin.getDiagramTypeManager().getDiagram(PluginConstants.MAPPING_DIAGRAM_TYPE_ID).getModelFactory();
                        modelFactory.resetExtentLocations(diagramNode, thisDiagram, mappingFilter, tempY);
                    }
                });
            }
        }
    }

    // Method used by DocumentTree
    public void resetExtentLocationsFromDocument( int newY ) {
        final int tempY = newY;
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {
                // System.out.println("[MappingtDiagramConotroller.resetExtentLocationsFromDocument$run]");
                treeYOrigin = tempY;

                ScrollingGraphicalViewer scrolledViewer = diagramEditor.getDiagramViewer();
                FigureCanvas scrolledCanvas = (FigureCanvas)scrolledViewer.getControl();
                int viewportY = scrolledCanvas.getViewport().getViewLocation().y;

                int yValue = tempY + viewportY;// scrollBarYPosition;

                resetExtentLocations(yValue);
            }
        });
    }

    public int getScrollOffset() {
        return treeYOrigin;
    }

    // Method used by DocumentTree
    public void resetExtentLocationsFromDiagram( int newY ) {
        final int tempY = newY;
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {
                // System.out.println("[MappingtDiagramConotroller.resetExtentLocationsFromDiagram$run]");
                scrollBarYPosition = tempY;

                int yValue = tempY + treeYOrigin;

                int minY = getDiagramYMin();
                int maxY = getDiagramYMax();
                int currentCanvasHeight = maxY - minY;

                if (minY < 0 && treeYOrigin < 0) {
                    // we are already in negative territory, so let's get the current.
                    // We want to be at treeYOrigin
                    // Current Y = minY
                    // Desired Y = Delta from total difference
                    int deltaYSB = scrollBarYPosition + treeYOrigin;
                    // Then we move to minY - delta
                    yValue = minY + deltaYSB;
                    // scrollBar will change
                    scrollBarYPosition = scrollBarYPosition * (currentCanvasHeight + yValue) / currentCanvasHeight;
                }

                resetExtentLocations(yValue);
            }
        });

    }

    private void addVerticalScrollListener() {
        final DiagramViewer scrolledViewer = diagramEditor.getDiagramViewer();
        FigureCanvas scrolledCanvas = (FigureCanvas)scrolledViewer.getControl();
        scrollBar = scrolledCanvas.getVerticalBar();
        verticalScrollAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                // don't process drag events since there are so many of them. another event
                // is received when the drag is done
                if (theEvent.detail != SWT.DRAG) {
                    autoScrollToReveal();
                    // System.out.println("[MappingDiagramController.addVerticalScrollListener$widgetSelected]");
                    documentController.resetExtentsFromDocument();
                }
                if (!limitAutoScroll || (limitAutoScroll && (theEvent.detail != SWT.DRAG))) {

                    // jh Defect 21263
                    documentController.resetExtentsFromDocument();
                }
            }
        };
        scrollBar.addSelectionListener(verticalScrollAdapter);
    }

    private void removeVerticalScrollListener() {
        if (verticalScrollAdapter != null && diagramEditor.getDiagramViewer().isValidViewer()) {
            ScrollingGraphicalViewer scrolledViewer = diagramEditor.getDiagramViewer();
            FigureCanvas scrolledCanvas = (FigureCanvas)scrolledViewer.getControl();
            scrollBar = scrolledCanvas.getVerticalBar();
            scrollBar.removeSelectionListener(verticalScrollAdapter);
        }
    }

    // ===========================================================
    // jh Defect 21263: added coordinate listener to reset extents when user drags mapping
    // class beyond viewport (effectively enlarging the viewport).
    private void addCoordinateListener() {

        if (coordinateListener == null) {
            coordinateListener = new CoordinateListener() {
                public void coordinateSystemChanged( IFigure f ) {
                    // System.out.println("[MappingDiagramController.addCoordinateControlListener$coordinateSystemChanged]");
                    documentController.resetExtentsFromDocument();
                }
            };
        }

        final DiagramViewer scrolledViewer = diagramEditor.getDiagramViewer();
        FigureCanvas scrolledCanvas = (FigureCanvas)scrolledViewer.getControl();
        scrolledCanvas.getViewport().addCoordinateListener(coordinateListener);
    }

    private void removeCoordinateListener() {
        if (coordinateListener != null && diagramEditor.getDiagramViewer().isValidViewer()) {
            ScrollingGraphicalViewer scrolledViewer = diagramEditor.getDiagramViewer();
            FigureCanvas scrolledCanvas = (FigureCanvas)scrolledViewer.getControl();
            scrolledCanvas.getViewport().removeCoordinateListener(coordinateListener);
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#getSelectionSource()
     */
    public ISelectionProvider getSelectionSource() {
        return this.documentController;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#handleNotification(org.eclipse.emf.common.notify.Notification)
     */
    public void handleNotification( Notification notification ) {
        if (notificationHandler != null) notificationHandler.handleNotification(notification);
    }

    void autoScrollToReveal() {
        EditPart diagramEditPart = diagramEditor.getDiagramViewer().getContents();
        // only autoscroll if a mapping diagram:
        if (diagramEditPart instanceof MappingDiagramEditPart) {
            // determine if classifiers are visible:
            Rectangle vpRect = ((FigureCanvas)diagramEditor.getDiagramViewer().getControl()).getViewport().getBounds();
            Point vpLoc = diagramEditor.getDiagramViewer().getViewportLocation();

            MappingDiagramEditPart mdep = (MappingDiagramEditPart)diagramEditPart;

            TopAndBottomClassifierInfo info = mdep.getTopAndBottomClassifierInfo();

            if (info.bottomY < vpLoc.y) {
                if (info.bottomPart != null) diagramEditor.getDiagramViewer().reveal(info.bottomPart);
            } else if (info.topY > vpLoc.y + vpRect.height) {
                if (info.topPart != null) diagramEditor.getDiagramViewer().reveal(info.topPart);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#isControllerOK(com.metamatrix.metamodels.diagram.Diagram)
     */
    public boolean maintainControl( Diagram newDiagram ) {
        boolean isSameDocument = false;
        try {
            // Let's set mapping type;
            int newMappingType = PluginConstants.COARSE_MAPPING;
            if (newDiagram.getType() != null
                && newDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) newMappingType = PluginConstants.DETAILED_MAPPING;

            // Let's get the diagram's target and that should be the input to the viewer
            // This needs to be the document. If diagram is "Detailed" we need to find the document from this.

            EObject targetEO = null;

            if (newMappingType == PluginConstants.DETAILED_MAPPING) {
                MappingClass mappingClassEO = (MappingClass)newDiagram.getTarget();
                targetEO = mappingClassEO.getMappingClassSet().getTarget();
            } else targetEO = newDiagram.getTarget();
            EObject currentTargetEO = (EObject)documentController.getViewer().getInput();

            if (targetEO != null && currentTargetEO != null && targetEO.equals(currentTargetEO)) isSameDocument = true;
        } catch (Exception ex) {
            UiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName());
        }

        return isSameDocument;
    }

    public void rewireDiagram( Diagram newDiagram ) {
        if (newDiagram.getType() != null && newDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) setMappingType(PluginConstants.DETAILED_MAPPING);
        else setMappingType(PluginConstants.COARSE_MAPPING);

        // This was not being set, so the currentDiagram was really never set to "Detailed"
        currentDiagram = newDiagram;

        documentController.getViewer().setMappingType(getMappingType());
        refresh(true);

        if (getMappingType() == PluginConstants.DETAILED_MAPPING) {

            // jh Defect 18919: Set up double click listenening on the document tree
            addDocumentTreeListener();
        }
    }

    public MappingAdapterFilter getMappingFilter( boolean bForceRecreate ) {
        MappingAdapterFilter filter = this.documentController.getMappingAdapterFilter(bForceRecreate);
        if (filter == null) {
            // Log an error here with any info you can find
            String message = "Current diagram = " + currentDiagram.getName() + //$NON-NLS-1$
                             " Document Input = " + documentController.getViewer().getInput(); //$NON-NLS-1$
            if (currentDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) message = message
                                                                                                                   + " Mapping Class = " + currentDiagram.getTarget();//$NON-NLS-1$
            Util.log(IStatus.ERROR, new Exception("Mapping Filter Not Found"), message); //$NON-NLS-1$
        }

        return filter;
    }

    public MappingAdapterFilter getMappingFilter() {
        MappingAdapterFilter filter = this.documentController.getMappingAdapterFilter();
        if (filter == null) {
            // Log an error here with any info you can find
            String message = "Current diagram = " + currentDiagram.getName() + //$NON-NLS-1$
                             " Document Input = " + documentController.getViewer().getInput(); //$NON-NLS-1$
            if (currentDiagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID)) message = message
                                                                                                                   + " Mapping Class = " + currentDiagram.getTarget();//$NON-NLS-1$
            Util.log(IStatus.ERROR, new Exception("Mapping Filter Not Found"), message); //$NON-NLS-1$
        }

        return filter;
    }

    public TreeMappingAdapter getMappingAdapter() {
        MappingAdapterFilter filter = getMappingFilter();
        if (filter != null) return filter.getMappingAdapter();

        return null;
    }

    public IMappableTree getMappableTree() {
        MappingAdapterFilter filter = getMappingFilter();
        if (filter != null) return filter.getMappableTree();

        return null;
    }

    int getDiagramYMin() {
        int minY = 0;

        EditPart diagramEditPart = diagramEditor.getDiagramViewer().getContents();
        if (diagramEditPart instanceof MappingDiagramEditPart) {
            minY = ((MappingDiagramEditPart)diagramEditPart).getLowestYValue();
        }

        return minY;
    }

    int getDiagramYMax() {
        int maxY = 0;

        EditPart diagramEditPart = diagramEditor.getDiagramViewer().getContents();
        if (diagramEditPart instanceof MappingDiagramEditPart) {
            maxY = ((MappingDiagramEditPart)diagramEditPart).getHighestYValue();
        }

        return maxY;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#clearDiagramSelection()
     */
    public void clearDiagramSelection() {
        diagramEditor.getDiagramViewer().clearAllSelections(false);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#updateForAutoLayout()
     */
    public void updateForAutoLayout() {
        autoScrollToReveal();

        // jh Defect 21263 - call resetExtents on tree
        documentController.resetExtentsFromDocument();
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     */
    public void controlMoved( ControlEvent e ) {
        // Don't care about anything here.

    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     */
    public void controlResized( ControlEvent e ) {
        // We need to tell the diagram to update from it's current scroll position
        if (diagramEditor.getControl() != null && !diagramEditor.getControl().isDisposed()) {
            documentController.resetExtentsFromDocument();
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.DiagramController#handleZoomChanged()
     * @since 4.2
     */
    public void handleZoomChanged() {
        if (diagramEditor.getControl() != null && !diagramEditor.getControl().isDisposed()) {
            documentController.resetExtentsFromDocument();
        }
    }

    public Diagram getCurrentDiagram() {
        return currentDiagram;
    }

    public void notifyElementsRevealed( Object oSource,
                                        List lstElements ) {
        if (bSynchronizeInProgress) {
            return;
        }

        if (oSource instanceof UmlClassifierEditPart) {
            revealElementsInTree(lstElements);
        } else if (oSource instanceof DocumentTreeController) {
            revealElementsInDiagram(lstElements);
        }
    }

    public boolean isRevealHideBehaviorEnabled() {
        boolean bFeatureIsOn = MappingDiagramUtil.getCurrentMappingDiagramBehavior().getSyncTreeAndDiagramState();
        return bFeatureIsOn;
    }

    public IRevealHideListener getRevealHideListener() {
        return this;
    }

    public void notifyElementsHidden( Object oSource,
                                      List lstElements ) {
        if (bSynchronizeInProgress) {
            return;
        }

        if (oSource instanceof UmlClassifierEditPart) {
            hideElementsInTree(lstElements);
        } else if (oSource instanceof DocumentTreeController) {
            hideElementsInDiagram(lstElements);
        }
    }

    public void revealElementsInTree( List lstElements ) {

        List lstResultNodes = getTreeNodesForMappingClassColumns(lstElements);

        Iterator itResultNodes = lstResultNodes.iterator();

        // Set visible to FALSE so we don't see the expanding tree
        documentController.getViewer().getTree().setVisible(false);

        while (itResultNodes.hasNext()) {
            Object oTemp = itResultNodes.next();
            documentController.getViewer().reveal(oTemp);
        }
        // let's set the first one to keep the window from scrolling to bottom
        if (!lstResultNodes.isEmpty()) documentController.getViewer().reveal(lstResultNodes.get(0));

        // Set visible to TRUE so we DO see the expanded tree
        documentController.getViewer().getTree().setVisible(true);

    }

    public List getTreeNodesForMappingClassColumns( List lstElements ) {

        TreeMappingAdapter mappingAdapter = documentController.getViewer().getMappingAdapterFilter(false).getMappingAdapter();

        List lstNodes = new ArrayList();
        Iterator it = lstElements.iterator();
        ArrayList arylResultNodes = new ArrayList();

        while (it.hasNext()) {
            MappingClassColumn mccTemp = (MappingClassColumn)it.next();

            lstNodes = mappingAdapter.getMappingClassColumnOutputLocations(mccTemp);
            Iterator itNodes = lstNodes.iterator();

            while (itNodes.hasNext()) {
                Object oTemp = itNodes.next();

                if (!arylResultNodes.contains(oTemp)) {
                    arylResultNodes.add(oTemp);
                }
            }
        }

        return arylResultNodes;
    }

    public void hideElementsInTree( List lstElements ) {
        /*
         * Strategy: - find the corresponding element in the tree
         *           - determine whether or not ALL of its siblings are in the 'hidden' list
         *           - if ALL are in the list, (recursively!?!!?), we can close this element's
         *             parent node.
         */
        ITreeContentProvider cp = (ITreeContentProvider)documentController.getViewer().getContentProvider();

        // 1. get the tree nodes that map to these Mapping Class Columns
        List lstResultNodes = getTreeNodesForMappingClassColumns(lstElements);

        Iterator it = lstResultNodes.iterator();
        while (it.hasNext()) {
            EObject eoTemp = (EObject)it.next();

            // default this flag to true
            boolean bOkToCollapseParent = true;
            // System.out.println("[MappingDiagramController.hideElementsInTree] element: " + eoTemp );

            // get this object's parent in the tree
            Object oParent = cp.getParent(eoTemp);

            if (oParent != null && oParent instanceof EObject) {

                // get all of this parent's children
                Object[] children = cp.getChildren(oParent);

                // determine whether or not ALL of this parent's children are in our list
                for (int i = 0; i < children.length; i++) {
                    if (!(children[i] instanceof InputSet) && !lstResultNodes.contains(children[i])) {
                        // if any of this parent's children are NOT in our collapse list,
                        // we cannot collapse this parent:
                        bOkToCollapseParent = false;
                        break;
                    }
                }

                /*
                 * jhTODO jh Lyra enh: Figure out if collapsing treenode(s) because
                 *                      the user collapsed a Mapping Class is even possible.
                 *                      
                 */
                if (bOkToCollapseParent) {
                    // get the Item for this EObject?
                    documentController.getViewer().setExpandedState(oParent, false);
                }
            }
        }
    }

    public void revealElementsInDiagram( List lstElements ) {
        ArrayList arylParentsToExpand = new ArrayList();

        List lstMCCols = getMappingClassColumnsForTreeNodes(lstElements);

        Iterator it = lstMCCols.iterator();

        while (it.hasNext()) {
            MappingClassColumn mccTemp = (MappingClassColumn)it.next();

            MappingClass mc = mccTemp.getMappingClass();

            if (!arylParentsToExpand.contains(mc)) {
                arylParentsToExpand.add(mc);
            }
        }

        // now expand the parent Mapping Classes
        if (arylParentsToExpand.size() > 0) {
            DiagramSelectionHandler dsh = new DiagramSelectionHandler(diagramEditor.getDiagramViewer());

            Iterator itMCs = arylParentsToExpand.iterator();

            while (itMCs.hasNext()) {
                MappingClass mcTemp = (MappingClass)itMCs.next();

                // expand this Mapping Class
                EditPart ep = dsh.findEditPart(mcTemp, false);

                if (ep instanceof UmlClassifierEditPart) {
                    bSynchronizeInProgress = true;
                    UmlClassifierNode node = (UmlClassifierNode)((UmlClassifierEditPart)ep).getModel();
                    node.expand();
                    bSynchronizeInProgress = false;
                }
            }

            // try doing this at the end of the process, instead of once for each MC
            diagramEditor.doRefreshDiagram();
        }
    }

    public List getMappingClassColumnsForTreeNodes( List lstElements ) {
        TreeMappingAdapter mappingAdapter = documentController.getViewer().getMappingAdapterFilter(false).getMappingAdapter();

        Iterator it = lstElements.iterator();
        ArrayList arylResultNodes = new ArrayList();

        while (it.hasNext()) {
            EObject eoTemp = (EObject)it.next();

            Object oMCCol = mappingAdapter.getMappingClassColumn(eoTemp);

            if (oMCCol != null) {
                arylResultNodes.add(oMCCol);
            }
        }

        return arylResultNodes;
    }

    public void hideElementsInDiagram( List lstElements ) {
        ArrayList arylParentsToCollapse = new ArrayList();

        List lstMCCols = getMappingClassColumnsForTreeNodes(lstElements);

        Iterator it = lstMCCols.iterator();

        while (it.hasNext()) {
            MappingClassColumn mccTemp = (MappingClassColumn)it.next();

            MappingClass mc = mccTemp.getMappingClass();

            if (!arylParentsToCollapse.contains(mc)) {
                arylParentsToCollapse.add(mc);
            }
        }

        // now expand the parent Mapping Classes
        if (arylParentsToCollapse.size() > 0) {
            DiagramSelectionHandler dsh = new DiagramSelectionHandler(diagramEditor.getDiagramViewer());

            Iterator itMCs = arylParentsToCollapse.iterator();

            while (itMCs.hasNext()) {
                MappingClass mcTemp = (MappingClass)itMCs.next();

                // expand this Mapping Class
                EditPart ep = dsh.findEditPart(mcTemp, false);

                if (ep instanceof UmlClassifierEditPart) {
                    bSynchronizeInProgress = true;
                    UmlClassifierNode node = (UmlClassifierNode)((UmlClassifierEditPart)ep).getModel();
                    node.collapse();
                    bSynchronizeInProgress = false;
                }
            }
        }
    }

    public MappingDiagramBehavior getMappingDiagramBehavior() {
        if (mappingDiagramBehavior == null) {
            mappingDiagramBehavior = new MappingDiagramBehavior();
        }
        return mappingDiagramBehavior;
    }
}
