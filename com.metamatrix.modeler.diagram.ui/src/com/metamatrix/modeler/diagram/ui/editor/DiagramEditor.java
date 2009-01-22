/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IGotoMarker;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.IDiagramType;
import com.metamatrix.modeler.diagram.ui.NotationChangeListener;
import com.metamatrix.modeler.diagram.ui.actions.AutoLayout;
import com.metamatrix.modeler.diagram.ui.actions.DiagramFontManager;
import com.metamatrix.modeler.diagram.ui.actions.NotationChoiceRadioActionGroup;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.dummy.DummyDiagramNode;
import com.metamatrix.modeler.diagram.ui.dummy.DummyDiagramPartFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.outline.DiagramOverview;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.diagram.ui.editor.DiagramActionContributor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.IInitializationCompleteListener;
import com.metamatrix.modeler.ui.editors.IInitializationCompleteNotifier;
import com.metamatrix.modeler.ui.editors.IInlineRenameable;
import com.metamatrix.modeler.ui.editors.INavigationSupported;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;
import com.metamatrix.modeler.ui.editors.NavigationMarker;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.print.IPrintable;
import com.metamatrix.ui.print.Printable;

/**
 * MM implementation of a GEF editor.
 */
public class DiagramEditor extends GraphicalEditor
    implements ModelEditorPage, AutoLayout, ZoomableEditor, INotifyChangedListener, ISelectionChangedListener,
    NotationChangeListener, ILabelProviderListener, IPartListener, IInitializationCompleteNotifier, INavigationLocationProvider,
    INavigationSupported, IGotoMarker, IInlineRenameable, DiagramUiConstants, UiConstants {

    private static final String THIS_CLASS = "DiagramEditor"; //$NON-NLS-1$
    boolean initializeModelPackage = true;

    private DiagramModelNode currentModel = null;
    private DiagramModelFactory diagramModelFactory = null;
    protected DiagramViewer viewer = null;
    private KeyHandler sharedKeyHandler;
    private IDiagramSelectionHandler selectionHandler;
    private String sNotationId;
    private Image titleImage;
    private String title;
    private String tooltip;
    private DiagramEditorInput diagramInput;

    private DiagramActionContributor dacDiagramActionContributor;
    private IDiagramActionAdapter currentActionAdapter;
    private MenuManager mmNotationActionGroup;

    private ScaledFont scaledFontManager;

    private DiagramViewForm diagramViewForm;

    private ToolBar toolBar;
    private DiagramToolBarManager toolBarManager;
    private DiagramController diagramController;
    private DiagramEditorSelectionProvider selectionProvider;
    private DiagramDecoratorHandler decoratorHandler;
    private DiagramOverview overview;

    private IMarker mMostRecentlyCreatedMarker;

    boolean bOkToCreateMarkers = true;
    private IResourceChangeListener markerListener;
    private ModelEditor meParentEditor;
    private ModelResource currentModelResource;
    private IPath currentModelPath;
    private EObject revealableEObject;

    private double zoomFactor = 1.0;
    private static HashMap hmapModelTreeStates;

    private Collection completionListeners;

    // cache adapters so that they can be reused
    private Map adapterMap = new HashMap();

    public DiagramEditor() {
        DiagramUiUtilities.setLoggingLevel(DiagramUiUtilities.LOG_NONE);

        setEditDomain(new DefaultEditDomain(this));
        sNotationId = DiagramUiPlugin.getDiagramNotationManager().getCurrentExtensionId();
        viewer = new DiagramViewer(this);
        titleImage = DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.EDITOR_ICON);
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(IEditorSite, IEditorInput)
     */
    @Override
    public void init( IEditorSite iSite,
                      IEditorInput iInput ) {
        setSite(iSite);
        setInput(iInput);

        markerListener = new IResourceChangeListener() {
            public void resourceChanged( IResourceChangeEvent event ) {

                if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                    // Let's see if all dependencies are open in workspace.
                    if (getDiagram() != null && getDiagram().getTarget() != null
                        && !ModelObjectUtilities.isStale(getDiagram().getTarget())) {
                        ModelResource currentMR = ModelUtilities.getModelResourceForModelObject(getDiagram());
                        boolean allDepExist = ModelUtilities.allDependenciesOpenInWorkspace(currentMR);
                        if (allDepExist) {
                            final IMarkerDelta[] deltas = event.findMarkerDeltas(null, true);

                            if (deltas != null && deltas.length > 0) {

                                List visitedResources = new ArrayList();
                                // boolean which will break this method out of it's loop
                                // We only need to refresh the diagram once here

                                boolean foundRefreshableResource = false;

                                for (int i = 0; i < deltas.length; i++) {
                                    IResource eventResource = deltas[i].getResource();
                                    // Need to only look at a delta's resource if we haven't looked at it before.
                                    if (!visitedResources.contains(eventResource)) {
                                        if (ModelUtilities.isModelFile(eventResource)) {
                                            ModelResource mr = null;
                                            try {
                                                mr = ModelUtilities.getModelResource((IFile)eventResource, false);
                                            } catch (ModelWorkspaceException e) {
                                                DiagramUiConstants.Util.log(e);
                                                WidgetUtil.showError(e);
                                            }
                                            if (mr != null && getDiagram() != null) {
                                                if (currentMR != null && mr.equals(currentMR)) {
                                                    boolean refreshDiagram = getDecoratorHandler().handleResouceChanged();
                                                    if (refreshDiagram) {
                                                        foundRefreshableResource = true;
                                                        refreshDiagramSafe();
                                                    }
                                                }
                                            }
                                        }
                                        if (!foundRefreshableResource) visitedResources.add(eventResource);
                                    }
                                    if (foundRefreshableResource) break;
                                }
                                updateReadOnlyState();
                            }
                        } else {
                            // We know that something happened that removed a dependent model
                            Display.getDefault().syncExec(new Runnable() {
                                public void run() {
                                    openContext(getDiagram());
                                    updateReadOnlyState();
                                }
                            });
                        }
                    }
                }
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(markerListener);
        ((AbstractActionService)DiagramUiPlugin.getDefault().getActionService(iSite.getPage())).addPartListener(this);

    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
     **/
    @Override
    protected void initializeGraphicalViewer() {
        initializeDiagram();
    }

    protected void initializeDiagram() {
        // Here's where we go ahead and if the input is an IFile and we can find the model resource
        // If we can find a model resource, we can open it's package diagram
        if (getEditorInput() instanceof IFileEditorInput) {
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        String message = this.getClass().getName() + ":  initializeDiagram() error sleeping on Thread"; //$NON-NLS-1$
                        DiagramUiConstants.Util.log(IStatus.ERROR, e1, message);
                    }

                    if (initializeModelPackage) {
                        IFileEditorInput ifei = (IFileEditorInput)getEditorInput();
                        IFile modelFile = ifei.getFile();

                        ModelResource mr = null;
                        try {
                            mr = ModelUtilities.getModelResource(modelFile, true);
                        } catch (ModelWorkspaceException e) {
                            String message = this.getClass().getName()
                                             + ":  initializeDiagram() error finding model resource for file = " + modelFile; //$NON-NLS-1$
                            DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                            WidgetUtil.showError(e);
                        }

                        if (mr != null) {
                            bOkToCreateMarkers = false;

                            openContext(mr);

                            bOkToCreateMarkers = true;

                        }
                    }
                }
            });

        } else {
            setCurrentModel(new DummyDiagramNode());
            getGraphicalViewer().setContents(getCurrentModel());
        }

        // capture it here so we are assured of having at least the resource
        // oLatestInput = getEditorInput();

    }

    // jhTODO: for rename/delete keybindings
    // //// protected void initializeKeyBindings() {
    // //// getGraphicalViewer().setKeyHandler( getCommonKeyHandler() );
    // //// }
    // ////

    public DiagramModelNode getCurrentModel() {
        return currentModel;
    }

    protected void setCurrentModel( DiagramModelNode newDiagramModelNode ) {
        this.currentModel = newDiagramModelNode;
    }

    public Diagram getDiagram() {
        if (diagramInput != null) {
            return diagramInput.getDiagram();
        }

        return null;
    }

    public HashMap getTreeStatesMap() {
        /*
         * key: document eobject
         * val: Object[]    // tree expanded state
         */
        if (hmapModelTreeStates == null) {
            hmapModelTreeStates = new HashMap();
        }

        return hmapModelTreeStates;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
     **/
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
     **/
    @Override
    public void doSave( IProgressMonitor iMonitor ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#doSaveAs()
     **/
    @Override
    public void doSaveAs() {
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.INavigationLocationProvider#createEmptyNavigationLocation()
     * @since 4.0
     */
    public INavigationLocation createEmptyNavigationLocation() {
        //        System.out.println("[DiagramEditor.createEmptyNavigationLocation] TOP"); //$NON-NLS-1$
        return null;
        // return neNavigableEditor.createEmptyNavigationLocation();
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.INavigationLocationProvider#createNavigationLocation()
     * @since 4.0
     */
    public INavigationLocation createNavigationLocation() {
        //        System.out.println("[DiagramEditor.createNavigationLocation] TOP"); //$NON-NLS-1$
        INavigationLocation newLocation = null;

        // newLocation = neNavigableEditor.createNavigationLocation();
        // if( newLocation instanceof DefaultModelEditorNavigationLocation && getDiagram() != null ) {
        // EObject target = getDiagram().getTarget();
        // if( target != null && !(target instanceof ModelAnnotation) ) {
        // String pathFromModelToTarget = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(target).toString();
        // if( pathFromModelToTarget != null ) {
        // ((DefaultModelEditorNavigationLocation)newLocation).setText(pathFromModelToTarget);
        // }
        // }
        // }
        return newLocation;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.INavigationSupported
     **/
    public IMarker createMarker() {

        NavigationMarker nmMarker = new NavigationMarker();
        nmMarker.setAttribute(Navigation.MARKER_TYPE, Navigation.NAVIGATION);
        nmMarker.setAttribute(Navigation.CURRENT_INPUT, getDiagram());

        // if (UiConstants.Util.isDebugEnabled(DebugConstants.NAVIGATION) ) {
        //                UiConstants.Util.print(DebugConstants.NAVIGATION, THIS_CLASS + ".createMarker();  curr diagram saved: " + getDiagram() ); //$NON-NLS-1$
        //                UiConstants.Util.print(DebugConstants.NAVIGATION, THIS_CLASS + ".createMarker();  Nav History Count: " + neNavigableEditor.getNavHistoryCount() ); //$NON-NLS-1$
        // }

        // also save Selections
        updateSelectionsInMarker(nmMarker);
        mMostRecentlyCreatedMarker = nmMarker;
        return nmMarker;
    }

    private void updateSelectionsInMarker( IMarker mMostRecentlyCreatedMarker ) {

        if (mMostRecentlyCreatedMarker != null) {

            try {

                // update Selection
                if (getSelectionHandler() != null) {

                    List lstEObjects = getSelectionHandler().getSelectedEObjects();
                    mMostRecentlyCreatedMarker.setAttribute(Navigation.CURRENT_SELECTION, lstEObjects);
                }

            } catch (CoreException ce) {
                String message = this.getClass().getName() + ":  updateSelectionsInMarker() error  "; //$NON-NLS-1$
                DiagramUiConstants.Util.log(IStatus.ERROR, ce, message);
            }
        }

    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(IMarker)
     **/
    public void gotoMarker( IMarker iMarker ) {
        /*
         *
         * 1.  the marker should also have SELECTED_OBJECT
         *   retrieve it and then:
         *          getSelectionHandler().select( oSelectedObject );
         */
        //        System.out.println("\n[DiagramEditor.gotoMarker] TOP, iMarker: " + iMarker ); //$NON-NLS-1$
        if (UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NAVIGATION)) {
            UiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NAVIGATION,
                                   THIS_CLASS + ".gotoMarker();  iMarker: " + iMarker); //$NON-NLS-1$
        }

        String sMarkerType = iMarker.getAttribute(Navigation.MARKER_TYPE, Navigation.UNKNOWN);

        try {

            if (sMarkerType.equals(Navigation.NAVIGATION)) {

                // close the createMarker feature during this operation:
                bOkToCreateMarkers = false;

                Object oInput = MarkerUtilities.getMarkerAttribute(iMarker, Navigation.CURRENT_INPUT); // iMarker.getAttribute(
                // Navigation.CURRENT_INPUT
                // );
                //                System.out.println("[DiagramEditor.gotoMarker] Input: " + oInput ); //$NON-NLS-1$

                if (UiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NAVIGATION)) {
                    UiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NAVIGATION,
                                           THIS_CLASS
                                           + ".gotoMarker();  iMarker.getAttributes(): " + iMarker.getAttributes().keySet().toString()); //$NON-NLS-1$
                    UiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NAVIGATION,
                                           THIS_CLASS + ".gotoMarker();  Input: " + oInput); //$NON-NLS-1$
                }

                if (oInput != null) {
                    if (canOpenContext(oInput)) {
                        openContext(oInput);

                        // reset selection
                        List lstSelection = (List)MarkerUtilities.getMarkerAttribute(iMarker, Navigation.CURRENT_SELECTION); // iMarker.getAttribute(
                        // Navigation.CURRENT_SELECTION
                        // );

                        if (lstSelection != null) {

                            Iterator it = lstSelection.iterator();

                            while (it.hasNext()) {
                                EObject eoTemp = (EObject)it.next();
                                getSelectionHandler().select(eoTemp);
                            }
                        }

                        // force the parent container to display us
                        getParent().displayModelEditorPage(this);
                    }
                }
            }
        } catch (CoreException ce) {
            bOkToCreateMarkers = true;
            String message = this.getClass().getName() + ":  gotoMarker() error  "; //$NON-NLS-1$
            DiagramUiConstants.Util.log(IStatus.ERROR, ce, message);

        } finally {
            // open up createMarker again:
            bOkToCreateMarkers = true;

            // quit now (skip the remainder of this method)
            if (sMarkerType.equals(Navigation.NAVIGATION)) {
                return;
            }
        }

        // if NOT a navigation marker, handle this way:
        EObject targetEObject = ModelObjectUtilities.getMarkedEObject(iMarker);

        if (targetEObject != null) {
            // Here's where we should open up the package diagram for the object so it can be viewed for
            // edit and selected so it's properties are in the pop.
            // PackageDiagramProvider pdp = new PackageDiagramProvider();
            // Diagram packageDiagram = pdp.getPackageDiagram(targetEObject);

            Diagram someDiagram = DiagramUiPlugin.getDiagramTypeManager().getDiagramForGoToMarkerEObject(targetEObject, true);

            if (someDiagram != null) {
                if (getDiagram() == null || !someDiagram.equals(getDiagram())) {
                    if (canOpenContext(someDiagram)) {
                        openContext(someDiagram);
                        // defect 18922 - notify diagram ctrlr if changing selection:
                        if (getDiagramController() != null) {
                            getDiagramController().selectionChanged(getParent(), new StructuredSelection(targetEObject));
                        } else if (getSelectionHandler() != null) {
                            getSelectionHandler().select(targetEObject);
                        } // endif
                    }
                } else if (getDiagram() != null && someDiagram.equals(getDiagram())) {
                    // defect 18922 - notify diagram ctrlr if changing selection:
                    if (getDiagramController() != null) {
                        getDiagramController().selectionChanged(getParent(), new StructuredSelection(targetEObject));
                    } else if (getSelectionHandler() != null) {
                        getSelectionHandler().select(targetEObject);
                    } // endif

                }
            }

        }
    }

    /**
     * @see org.eclipse.ui.IEditorPart#isDirty()
     **/
    @Override
    public boolean isDirty() {
        return false;
    }

    protected void setInitialPartFactory() {
        viewer.setEditPartFactory(new DummyDiagramPartFactory());
    }

    public DiagramDecoratorHandler getDecoratorHandler() {
        if (decoratorHandler == null) decoratorHandler = new DiagramDecoratorHandler(this);

        return decoratorHandler;
    }

    /**
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
     **/
    @Override
    protected void configureGraphicalViewer() {

        super.configureGraphicalViewer();

        viewer = (DiagramViewer)getGraphicalViewer();

        resetRootEditPart();

        setInitialPartFactory();

        // If you don't put this line, then moving figures by drag & drop
        // above the left or top limit of the editor window will lead to
        // an infinite loop!
        ((FigureCanvas)viewer.getControl()).setScrollBarVisibility(FigureCanvas.ALWAYS);

        selectionProvider = new DiagramEditorSelectionProvider(viewer);
        viewer.addDropTargetListener(new DiagramDropTargetAdapter(viewer));

        this.getControl().addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseExit( MouseEvent e ) {
                if (e.x < 0 && getSelectionHandler() != null) getSelectionHandler().fireMouseExit();
            }
        });
    }

    protected ScaledFont getFontManager() {
        if (scaledFontManager == null) scaledFontManager = new DiagramFontManager(viewer);

        return scaledFontManager;
    }

    @Override
    public Object getAdapter( Class type ) {

        if (type == ZoomManager.class && getDiagramViewer().isValidViewer() && getDiagramViewer().getRootEditPart() != null) {
            ZoomManager zm = ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
            zm.setZoomLevels(DiagramUiConstants.Zoom.zoomValues);
            zm.setZoom(zoomFactor);
            return zm;
        }

        if (type == ScaledFont.class) {
            return getFontManager();
        }

        if (type == AutoLayout.class) {
            return this;
        }

        if (type == IDiagramActionAdapter.class) {
            if (getDiagramActionAdapter() != null) {
                return getDiagramActionAdapter();
            }
        }

        if (type == IPrintable.class) {
            if (this.getGraphicalViewer() != null) {
                return new Printable(getGraphicalViewer());
            }
        }

        return super.getAdapter(type);
    }

    private void saveZoom() {
        ZoomManager zm = ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
        zoomFactor = zm.getZoom();
    }

    public void resetZoom() {
        ZoomManager zm = ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
        zm.setZoom(zoomFactor);
    }

    public IDiagramActionAdapter getDiagramActionAdapter() {
        return currentActionAdapter;
    }

    private void setDiagramActionAdapter( Diagram newDiagram ) {
        if (newDiagram != null) {
            if (currentActionAdapter == null || isNewDiagram(newDiagram)) {
                // deactivate current adapter
                if (this.currentActionAdapter != null) {
                    this.currentActionAdapter.pageDeactivated();
                }

                // if an adapter for this type has already been used, use it.
                // otherwise construct a new one
                String type = newDiagram.getType();
                IDiagramActionAdapter newAdapter = (IDiagramActionAdapter)adapterMap.get(type);

                if (newAdapter == null) {
                    newAdapter = DiagramUiPlugin.getDiagramTypeManager().getDiagram(type).getActionAdapter(this);

                    if (newAdapter != null) {
                        adapterMap.put(type, newAdapter);
                    }
                }

                if (newAdapter != null) {
                    newAdapter.setDiagramEditor(this);
                }

                this.currentActionAdapter = newAdapter;
            }
        }
        // jhTODO: for rename/delete keybindings

        //////        System.out.println("[DiagramEditor.setDiagramActionAdapter] About to call initializeKeyBindings();"); //$NON-NLS-1$
        // //// initializeKeyBindings();

    }

    // This private method will only work when called before the setDiagram() is called in openContext();
    private boolean isNewDiagram( Diagram newDiagram ) {
        if (newDiagram == null && getDiagram() == null) return false;

        if (getDiagram() != null && newDiagram != null && newDiagram.getType().equals(getDiagram().getType())) return false;

        return true;
    }

    /**
     * Creates the GraphicalViewer on the specified <code>Composite</code>.
     */
    @Override
    protected void createGraphicalViewer( Composite parent ) {
        // System.out.println("  -->>  DE.createGraphicalViewer(START) calling viewer.createControl() DVF.isDisposed() = " +
        // diagramViewForm.isDisposed());
        viewer.createControl((Composite)diagramViewForm.getSashForm());
        setGraphicalViewer(viewer);
        configureGraphicalViewer();
        // Decided to not call hookGraphicalViewer() here, so we don't wire the ModelEditorSelectionProvider twice
        // It's is done now via the DiagramEditorSelectionProvider.
        // We do, just to be save, continue to wire the viewer to the selection Synchronizer.
        getSelectionSynchronizer().addViewer(viewer);
        // hookGraphicalViewer();
        initializeGraphicalViewer();
        // remove the extra keyhandler to prevent duplicate invocations:
        // viewer.setKeyHandler(new DiagramKeyHandler(viewer));
        // System.out.println("  -->>  DE.createGraphicalViewer(END) DVF.isDisposed() = " + diagramViewForm.isDisposed());
    }

    /**
     * Returns the KeyHandler with common bindings for both the Outline and Graphical Views. For example, delete is a common
     * action.
     */
    protected KeyHandler getCommonKeyHandler() {
        //////        System.out.println("[DiagramEditor.getCommonKeyHandler] TOP"); //$NON-NLS-1$
        if (sharedKeyHandler == null) {
            sharedKeyHandler = new KeyHandler();
            sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),

            getActionRegistry().getAction(ActionFactory.DELETE.getId()));
            // jhTODO: for rename/delete keybindings (alternatives)
            // //// getActionRegistry().getAction(IWorkbenchActionConstants.DELETE));
            // // getActionRegistry().getAction( EclipseGlobalActions.DELETE ));

            sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
        }
        return sharedKeyHandler;
    }

    protected void createSelectionHandler( Diagram diagram,
                                           DiagramViewer theViewer ) {
        selectionHandler = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagram.getType()).getSelectionHandler(theViewer);
    }

    protected IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    private void resetRootEditPart() {
        RootEditPart root = viewer.getRootEditPart();
        if (root != null && dacDiagramActionContributor != null) dacDiagramActionContributor.tellZoomWrappersToClose();
        if (root != null) {
            if (!(root instanceof ScalableFreeformRootEditPart)) {
                // ORIG root = new ScalableFreeformRootEditPart();
                root = new CustomScalableFreeformRootEditPart();
                viewer.setRootEditPart(root);
            } else {
                root.setContents(null);
            }
            // defect 16983 - remove any extra connections that are left over:
            ConnectionLayer cLayer = (ConnectionLayer)((ScalableFreeformRootEditPart)root).getLayer(LayerConstants.CONNECTION_LAYER);
            if (!cLayer.getChildren().isEmpty()) {
                cLayer.removeAll();
            } // endif
            // } else {
            // root.setContents(null);
        }
    }

    private void clearCurrentDiagram() {
        // Cleanup work??
        // Start with clearing all associations.
        if (getCurrentModel() != null) {
            setCurrentModel(null);
            diagramInput = null;
        }
        if (!getDiagramViewer().getSelectedEditParts().isEmpty()) getDiagramViewer().deselectAll();
        resetRootEditPart();
    }

    protected void createModel( Diagram diagram,
                                IProgressMonitor monitor ) {
        if (getModelFactory() != null) {

            diagramInput = new DiagramEditorInput(diagram);

            setCurrentModel(getModelFactory().createModel(diagram, getNotationId(), monitor));

            DiagramEditPartFactory newEditPartFactory = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagram.getType()).getEditPartFactory();
            newEditPartFactory.setNotationId(getNotationId());
            createSelectionHandler(diagram, viewer);
            newEditPartFactory.setSelectionHandler(getSelectionHandler());
            viewer.setEditPartFactory(newEditPartFactory);
        }
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    protected void setDiagram( Diagram diagram,
                               IProgressMonitor monitor ) {
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDITOR) && diagram != null && diagram.getType() != null) {
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_EDITOR, THIS_CLASS
                                                                         + ".setDiagram() diagramType = " + diagram.getType()); //$NON-NLS-1$
        }

        boolean requiredStart = false;
        boolean succeeded = false;
        try {
            // Let's wrap this in a transaction!!! that way all constructed objects and layout properties
            // will result in only one transaction?

            requiredStart = ModelerCore.startTxn(false, false, "Display Diagram", this); //$NON-NLS-1$

            saveZoom();

            clearCurrentDiagram();

            if (diagram != null) {
                // New: 5/6/04 to cache diagram entities.
                DiagramEntityManager.addDiagram(diagram);

                currentModelResource = ModelUtilities.getModelResourceForModelObject(diagram.getTarget());
                currentModelPath = ((IFileEditorInput)getEditorInput()).getFile().getFullPath();

                setNotationId(DiagramUiUtilities.getDiagramNotation(diagram));

                DiagramModelFactory modelFactory = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagram.getType()).getModelFactory();

                setModelFactory(modelFactory);

                createModel(diagram, monitor);

                if (getCurrentModel() != null) {
                    if (monitor != null) {
                        monitor.subTask("Setting Viewer Contents"); //$NON-NLS-1$
                        monitor.worked(20);
                    }
                    getGraphicalViewer().setContents(getCurrentModel());

                    if (monitor != null) {
                        monitor.subTask("Performing Layout"); //$NON-NLS-1$
                        monitor.worked(10);
                    }
                    if (getGraphicalViewer().getContents() instanceof DiagramEditPart) {
                        DiagramEditPart diagramEP = (DiagramEditPart)getGraphicalViewer().getContents();

                        /* componentLayout() is called to give the components that have "children" a chance to layout
                         * those children. Classifiers, for instance have model children which result in Attribute
                         * Edit parts being created.  The Classifier has already been constructed but still needs to
                         * layout the attributes.
                         */
                        diagramEP.layout(DiagramEditPart.LAYOUT_CHILDREN);
                        resetZoom();
                        diagramEP.constructionCompleted(true);
                    }
                    if (monitor != null) monitor.worked(10);
                    viewer.printContents();
                }
            } else {
                if ((currentModelResource != null) && currentModelResource.exists() && currentModelResource.isOpen()) {
                    openContext(currentModelResource);
                } else {
                    setModelFactory(null);
                    selectionHandler = null;
                    diagramInput = null;
                    if (currentActionAdapter != null) currentActionAdapter.disposeOfActions();
                    currentActionAdapter = null;

                    removeDiagramController();
                    clearDiagramToolbar();
                    setInitialPartFactory();
                    setCurrentModel(new DummyDiagramNode());
                    getGraphicalViewer().setContents(getCurrentModel());
                }
            }
            succeeded = true;
        } catch (Exception ex) {
            DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" + THIS_CLASS + ".setDiagram()"); //$NON-NLS-1$ //$NON-NLS-2$
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
                if (overview != null) overview.resetContents();
            }
        }

    }

    private void clearDiagramToolbar() {
        getToolBarManager().removeAll();
        getToolBarManager().update(true);
        diagramViewForm.redraw();

    }

    public DiagramViewForm getDiagramViewForm() {
        return diagramViewForm;
    }

    private void setDiagramToolBar() {
        if (toolBarManager != null && toolBar != null) {
            if (getDiagramActionAdapter() != null) {
                getDiagramActionAdapter().contributeToDiagramToolBar();
                getDiagramActionAdapter().enableDiagramToolbarActions();
            }
            getToolBarManager().update(true);
        } else {
            // System.out.println("  -->>  DE.setDiagramToolBar() TBM or ToolBar == NULL!!! ");
        }
        // System.out.println("  -->>  DE.setDiagramToolBar() ViewForm.isDisposed == " + diagramViewForm.isDisposed());
        if (!diagramViewForm.isDisposed() && diagramViewForm.isVisible()) diagramViewForm.redraw();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext( Object input ) {
        return DiagramUiPlugin.getDiagramTypeManager().canOpenContext(input);
    }

    public void initializeEditorPage() {
    }

    /*
     * non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input ) {
        openContext(input, false);
    }

    public void openContext( Object input,
                             boolean forceRefresh ) {
        // System.out.println("  -->>  DE.openContext() already openin/gContext = " + openingContext + " NOpens = " + nOpens +
        // " Input = " + input);
        Diagram previousDiagram = getDiagram();
        //System.out.println("DiagramEditor.openContext(" + input + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input instanceof Diagram) {
            autoSelect();
        }

        if (DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TIMER)) DiagramUiConstants.Util.start("DiagramEditor.openContext()", com.metamatrix.modeler.internal.ui.DebugConstants.TIMER); //$NON-NLS-1$

        // oLatestInput = input;

        initializeModelPackage = false;

        if (input == null) {
            //            System.out.println("[DiagramEditor.openContext] Input is NULL "  ); //$NON-NLS-1$

            setDiagram(null, null);
            titleImage = null;
            title = null;
            tooltip = null;
            this.firePropertyChange(PROP_TITLE);
            getDecoratorHandler().clear();
        } else {
            Diagram inputDiagram = null;
            boolean contextIsDiagram = false;

            if (input instanceof Diagram && !(input instanceof DiagramProxy)) {
                contextIsDiagram = true;
                inputDiagram = (Diagram)input;
            } else inputDiagram = DiagramUiPlugin.getDiagramTypeManager().getDiagramForContext(input);

            if (inputDiagram != null
                && !DiagramUiPlugin.getDiagramTypeManager().getDiagram(inputDiagram.getType()).isDiagramTooLarge(inputDiagram)) {
                final Diagram newDiagram = inputDiagram;

                // [fix for defect 16563]:
                // if new diagram and old diagram are the same, take no further action
                if (this.meParentEditor != null && !forceRefresh && previousDiagram != null
                    && inputDiagram.equals(previousDiagram)) {
                    // ----------------------------
                    // Defect 22844 - setting ignoreInternalFocus
                    // This cleans up simple selection causing focus to OperationEditorPage way too often
                    // ----------------------------
                    meParentEditor.setIgnoreInternalFocus(true);
                    return;
                }

                setDiagramActionAdapter(newDiagram);

                boolean requiresProgress = false;
                requiresProgress = DiagramUiPlugin.getDiagramTypeManager().getDiagram(newDiagram.getType()).isDiagramLarge(newDiagram);

                if (requiresProgress) {
                    setDiagramWithProgress(newDiagram);
                } else {
                    UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                        public void run() {
                            setDiagram(newDiagram, null);
                        }
                    });
                }

                updateDiagramController();

                setDiagramToolBar();

                // This check is required to fix Defect 15800 when CLOSE ALL EDITORS is selected during the
                // middle of this this openContext() method.
                if (!diagramViewForm.isDisposed()) {
                    // Tell the viewform to layout
                    diagramViewForm.getSashForm().update();
                    // This line doesn't appear to be necessary
                    // For large diagrams it was causing a "handle" error in the windowing/widge world.
                    // I commmented it out on 12/17/03 BML
                    // diagramViewForm.getSashForm().pack(true);
                    diagramViewForm.update();
                    diagramViewForm.layout(false);
                    diagramViewForm.setInitialSashFormWeights();
                }

                updateEditorTab(newDiagram);

                // Lastly, we give the diagram type the chance to fire an initial selection
                EObject initialSelection = DiagramUiPlugin.getDiagramTypeManager().getDiagram(newDiagram.getType()).getInitialSelection(input);
                if (initialSelection != null) getDiagramViewer().getSelectionHandler().select(initialSelection);
                else {
                    if (!contextIsDiagram && input instanceof EObject) {
                        getDiagramViewer().getSelectionHandler().select((EObject)input);
                    }
                }
                revealableEObject = DiagramUiPlugin.getDiagramTypeManager().getDiagram(newDiagram.getType()).getRevealedEObject(newDiagram,
                                                                                                                                input);
                if (bOkToCreateMarkers) {
                    //                  System.out.println("\n[DiagramEditor.openContext] About to markLocation( this ) (DiagramEditor) " ); //$NON-NLS-1$
                    UiUtil.getWorkbenchPage().getNavigationHistory().markLocation(this);
                }

            }
            getDecoratorHandler().reset();

        }
        if (DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.TIMER)) DiagramUiConstants.Util.stop("DiagramEditor.openContext()", com.metamatrix.modeler.internal.ui.DebugConstants.TIMER); //$NON-NLS-1$

        // System.out.println("  ------------ End openContext() ------------------" );
        // System.out.println(" " );
        updateReadOnlyState();

        notifyInitializationComplete();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openComplete()
     * @since 4.2
     */
    public void openComplete() {
        if (revealableEObject != null) {
            final EObject revealedObject = revealableEObject;
            getDiagramViewer().reveal(revealedObject);
            revealableEObject = null;
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     * @since 4.2
     */
    public void setTitleText( String newTitle ) {
        this.title = newTitle;
    }

    private boolean setDiagramWithProgress( Diagram newDiagram ) {
        boolean success = false;
        final Diagram theDiagram = newDiagram;
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                monitor.beginTask("Constructing Diagram", 100); //$NON-NLS-1$
                setDiagram(theDiagram, monitor);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(getControl().getShell());
            dlg.run(false, true, op);
            if (dlg.getProgressMonitor().isCanceled()) {
                return true;
            }

            success = true;
        } catch (final InterruptedException ignored) {
            success = true;
        } catch (final Exception err) {
            success = false;
        }

        return success;
    }

    private void updateDiagramController() {

        boolean useExistingController = false;

        // get the new diagram's controller class (if exists)
        Class controllerClass = DiagramUiPlugin.getDiagramTypeManager().getDiagram((getDiagram()).getType()).getDiagramControllerClass();

        // Clean up and remove current controller if exists.
        // Check for non-null controller and if non-null, then is it the same class as the new diagram type.
        if (diagramController != null) {

            // If new diagram's controller is the same type, ask the
            // controller if it want's to maintain control
            if (controllerClass != null) {
                if (diagramController.getClass().equals(controllerClass)) {
                    useExistingController = diagramController.maintainControl(getDiagram());
                }
            }

            // We need to throw away the old controller here.
            if (!useExistingController) {
                // Remove the control listener that was added at end of this method
                if (diagramController instanceof ControlListener) {
                    getControl().removeControlListener((ControlListener)diagramController);
                }

                diagramController.deactivate();

                selectionProvider.removeDiagramController(diagramController);

                Control diagramControl = diagramViewForm.getControllerControl();
                if (diagramControl != null) {
                    diagramControl.dispose();
                }
                // Finish cleaning up the diagram controller
                diagramController.dispose();
                diagramController = null;

                // resize the diagram view form
                diagramViewForm.getSashForm().update();
                diagramViewForm.getSashForm().pack(true);
                diagramViewForm.update();
                diagramViewForm.layout(false);
            }

        }

        if (useExistingController) {
            diagramController.rewireDiagram(getDiagram());
        } else if (controllerClass != null) {
            DiagramController newDiagramController = getDiagramController(getDiagram());

            // If new controller, wire it up.
            if (newDiagramController != null) {
                diagramController = newDiagramController;
                diagramController.wireDiagram(getDiagram());
                selectionProvider.setDiagramController(diagramController);
                if (diagramController instanceof ControlListener) {
                    getControl().addControlListener((ControlListener)diagramController);
                }
            }
        }
    }

    /*
     * Private utility method to get the DiagramController for a given diagram.
     * This method checks for NPE reported in Defect 13860.
     */
    private DiagramController getDiagramController( Diagram diagram ) {
        if (diagram != null && diagram.getType() != null) {
            IDiagramType dt = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagram.getType());
            if (dt != null) return dt.getDiagramController(this);
        }
        return null;
    }

    private void removeDiagramController() {

        // Clean up and remove current controller if exists.
        // Check for non-null controller and if non-null, then is it the same class as the new diagram type.
        if (diagramController != null) {
            if (diagramController instanceof ControlListener && getControl() != null) getControl().removeControlListener((ControlListener)diagramController);

            diagramController.deactivate();
            selectionProvider.removeDiagramController(diagramController);

            Control diagramControl = diagramViewForm.getControllerControl();
            if (diagramControl != null) {
                diagramControl.dispose();
            }
            diagramViewForm.getSashForm().update();
            diagramViewForm.getSashForm().pack(true);
            diagramViewForm.update();
            diagramViewForm.layout(false);
            diagramController = null;
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return selectionProvider;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener(java.lang.Object)
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return this;
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( SelectionChangedEvent sce ) {
        ISelection selection = sce.getSelection();
        if (selection instanceof IStructuredSelection) {

            if (SelectionUtilities.isSingleSelection(selection)) {
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                if (obj instanceof EObject) {
                    if (getSelectionHandler() != null) {
                        getSelectionHandler().select((EObject)obj);
                    }

                    if (getDiagramController() != null) getDiagramController().selectionChanged(getParent(), selection);

                    if (meParentEditor != null && obj instanceof Diagram && meParentEditor.getActiveEditor() == this) {
                        // ----------------------------
                        // Defect 22844 - setting ignoreInternalFocus
                        // This cleans up simple selection causing focus to OperationEditorPage way too often
                        // ----------------------------
                        meParentEditor.setIgnoreInternalFocus(true);
                    }
                }
            } else {
                if (getSelectionHandler() != null) {
                    getSelectionHandler().select(selection);
                }
            }

            // update the selections in the most recent marker
            updateSelectionsInMarker(mMostRecentlyCreatedMarker);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getEditorActionBarContributor()
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        // jh experiment: commenting out the 'if null' logic.
        // this way, we'll always provide a new one, but that one will persist
        // until a new one is required...Whoa..that led to an NPE???
        if (dacDiagramActionContributor == null) {
            dacDiagramActionContributor = new DiagramActionContributor(this);
        }
        return dacDiagramActionContributor;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getEditorActionBarContributor()
     */
    public void setNotationId( String sNotationId ) {
        //        Util.log( IStatus.INFO, "[DiagramEditor.setNotationId] TOP "  ); //$NON-NLS-1$
        if (!this.sNotationId.equals(sNotationId)) {
            this.sNotationId = sNotationId;
            DiagramUiUtilities.setDiagramNotation(sNotationId, getDiagram());
            // viewer.setEditPartFactory(DiagramUiPlugin.getDiagramNotationManager().getEditPartFactory(sNotationId));
            //            Util.log( IStatus.INFO, "[DiagramEditor.setNotationId] About to call setInput, notation is: " + this.sNotationId ); //$NON-NLS-1$
            // setInput( input );
            // UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            // public void run() {
            // setDiagram(diagramInput.getDiagram());
            // }
            // });
            setDiagramWithProgress(diagramInput.getDiagram());

        }
    }

    public String getNotationId() {
        return this.sNotationId;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    public void setLabelProvider( ILabelProvider provider ) {
        DiagramUiPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator().addListener(this);
        // provider.addListener(this);
        DiagramUiPlugin.getDiagramNotationManager().setLabelProvider(provider);
    }

    public ILabelProvider getLabelProvider() {
        return DiagramUiPlugin.getDiagramNotationManager().getLabelProvider();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    public INotifyChangedListener getNotifyChangedListener() {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public Control getControl() {
        return viewer.getControl();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public DiagramViewForm getPrimaryControl() {
        return diagramViewForm;
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        // System.out.println("[DIagramEditor.notifyChanged] TOP");
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDITOR)) DiagramUiConstants.Util.start("DiagramEditor.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$

        boolean diagramStillValid = true;

        if (DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS)) {
            DiagramUiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS,
                                          " DiagramEditor.notifyChanged(): START PROCESSING NOTIFICATION ------------------------------"); //$NON-NLS-1$
            if (notification instanceof SourcedNotification) {
                Object source = ((SourcedNotification)notification).getSource();
                DiagramUiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS,
                                              " DiagramEditor.notifyChanged(): got a notifyChanged( ) Source = " + source); //$NON-NLS-1$
            } else DiagramUiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS,
                                                 " DiagramEditor.notifyChanged(): got a notifyChanged( ) " + notification); //$NON-NLS-1$
        }
        // DiagramModelFactory inherently wraps
        if (getModelFactory() != null && getCurrentModel() != null) {
            diagramStillValid = getModelFactory().notifyModel(notification,
                                                              getCurrentModel(),
                                                              diagramInput.getDiagram().getType());
        }

        if (diagramStillValid) {
            try {

                if (getDiagramActionAdapter() != null) getDiagramActionAdapter().handleNotification(notification);

                if (diagramController != null) diagramController.handleNotification(notification);

                if (DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS)) {
                    DiagramUiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.NOTIFICATIONS,
                                                  " DiagramEditor.notifyChanged(): END PROCESSING NOTIFICATION --------------------------------\n\n"); //$NON-NLS-1$
                }
            } catch (Exception ex) {
                DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName());
            }
            if (getDiagram() != null) {
                updateEditorTab(getDiagram());
                // defect 16803 - be smarter about model changes
                if (getModelFactory().shouldRefreshDiagram(notification, getCurrentModel(), diagramInput.getDiagram().getType())) {
                    // trigger the refresh:
                    refreshDiagramSafe();
                } // endif
            }
        } else {
            if (meParentEditor != null && meParentEditor.getModelFile() != null
                && meParentEditor.getModelFile().getRawLocation().toFile().exists()) {
                // here's where we replace current diagram with bogus empty diagram by calling openContext(null)
                openContext(null);
            }
        }
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDITOR)) DiagramUiConstants.Util.stop("DiagramEditor.notifyChanged()", InternalUiConstants.Debug.Metrics.NOTIFICATIONS); //$NON-NLS-1$
    }

    private void updateEditorTab( Diagram someDiagram ) {
        ILabelProvider labelProvider = DiagramUiPlugin.getDiagramNotationManager().getLabelProvider();
        if (labelProvider != null) {
            titleImage = labelProvider.getImage(someDiagram);
            setTitleText(labelProvider.getText(someDiagram));
            tooltip = labelProvider.getText(someDiagram);
        }
        if (this.meParentEditor != null) {
            this.meParentEditor.refreshEditorTabs();
        }
    }

    public MenuManager getNotationActionGroup() {
        if (mmNotationActionGroup == null) {
            mmNotationActionGroup = DiagramUiPlugin.getDiagramNotationManager().getNotationActionGroup(this, getNotationId());
        } else {
            ((NotationChoiceRadioActionGroup)mmNotationActionGroup).updateNotationActions(getNotationId());
        }
        return mmNotationActionGroup;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    @Override
    public String getTitle() {
        if (title == null) {
            return super.getTitle();
        }
        return title;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    @Override
    public Image getTitleImage() {
        return titleImage;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        if (tooltip == null) {
            return super.getTitleToolTip();
        }
        return tooltip;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    public ModelEditorPageOutline getOutlineContribution() {
        if (overview == null) overview = new DiagramOverview(getGraphicalViewer(), getSelectionSynchronizer(), getCurrentModel());
        return overview;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setParent()
     */
    public void setParent( ModelEditor meParentEditor ) {
        this.meParentEditor = meParentEditor;
    }

    /*
     */
    public ModelEditor getParent() {
        return meParentEditor;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     */
    public boolean canDisplay( IEditorInput input ) {
        boolean result = false;
        ModelResource mr = null;

        if (input instanceof EObject) {
            mr = ModelUtilities.getModelResourceForModelObject((EObject)input);
        } else if (input instanceof ModelResource) {
            mr = (ModelResource)input;
        } else if (input instanceof IFileEditorInput) {
            IFileEditorInput ifei = (IFileEditorInput)input;
            IFile modelFile = ifei.getFile();

            if (ModelUtil.isXsdFile(modelFile)) {
                result = false;
            } else {
                try {
                    mr = ModelUtilities.getModelResource(modelFile, true);
                } catch (ModelWorkspaceException e) {
                    String message = this.getClass().getName()
                                     + ":  canDisplay() error finding model resource for file = " + modelFile; //$NON-NLS-1$
                    DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }

        }
        if (mr != null) {
            result = ModelUtilities.supportsDiagrams(mr);
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.actions.AutoLayout#autoLayout()
     */
    public void autoLayout() {
        if (getCurrentModel() != null && getGraphicalViewer().getContents() instanceof DiagramEditPart) {
            DiagramEditPart diagram = (DiagramEditPart)getGraphicalViewer().getContents();

            /* componentLayout() is called to give the components that have "children" a chance to layout
             * those children. Classifiers, for instance have model children which result in Attribute
             * Edit parts being created.  The Classifier has already been constructed but still needs to
             * layout the attributes.
             */
            diagram.setUnderConstruction(true);
            diagram.layout();
            diagram.constructionCompleted(true);
            if (getDiagramController() != null) {
                getDiagramController().updateForAutoLayout();
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.actions.AutoLayout#canAutoLayout()
     */
    public boolean canAutoLayout() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.actions.AutoLayout#canAutoLayout()
     */
    public void doRefreshDiagram() {
        refreshDiagramSafe();
        autoLayout();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.ZoomableEditor#getDiagramEditor()
     */
    public DiagramEditor getDiagramEditor() {
        return this;
    }

    public DiagramViewer getDiagramViewer() {
        return viewer;
    }

    /**
     * @return
     */
    public DiagramController getDiagramController() {
        return diagramController;
    }

    /**
     * @return
     */
    public DiagramModelFactory getModelFactory() {
        return diagramModelFactory;
    }

    /**
     * @param factory
     */
    public void setModelFactory( DiagramModelFactory factory ) {
        diagramModelFactory = factory;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        // System.out.println("  -->>  DE.createPartControl() Creating ViewForm and ToolBar() ");
        diagramViewForm = new DiagramViewForm(parent, SWT.BORDER);
        toolBar = new ToolBar(diagramViewForm, SWT.FLAT | SWT.WRAP | SWT.VERTICAL);
        // toolBar.setLayout(new GridLayout());
        // GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END, SWT.FILL, false, true); //GridData.HORIZONTAL_ALIGN_END |
        // GridData.VERTICAL_ALIGN_END);
        // toolBar.setLayoutData(data);SWT.VERTICAL

        diagramViewForm.setToolBar(toolBar);

        toolBarManager = new DiagramToolBarManager(toolBar);

        super.createPartControl(diagramViewForm);

        toolBarManager.update(true);
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                DiagramController dController = getDiagramController();
                if (dController != null) {
                    dController.dispose();
                }
            }
        });
        // System.out.println("  -->>  DE.createPartControl() ViewForm.isDisposed() = " + diagramViewForm.isDisposed());
    }

    public ToolBarManager getToolBarManager() {
        return toolBarManager;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        // dispose the action adapters
        if (!this.adapterMap.isEmpty()) {
            Iterator itr = this.adapterMap.values().iterator();

            while (itr.hasNext()) {
                ((IDiagramActionAdapter)itr.next()).disposeOfActions();
            }
        }

        DiagramUiPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator().removeListener(this);
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(markerListener);
        ((AbstractActionService)DiagramUiPlugin.getDefault().getActionService(getEditorSite().getPage())).removePartListener(this);
        if (toolBarManager != null) {
            // System.out.println("  -->>  DE.dispose() calling toolBarManager.dispose() TBM = NULL");
            toolBarManager.dispose();
            toolBarManager = null;
        }

        if (dacDiagramActionContributor != null) {
            dacDiagramActionContributor.dispose();
            dacDiagramActionContributor = null;
        }

        if (currentActionAdapter != null) {
            currentActionAdapter = null;
        }

        // jh TODO Defect 18038
        // serialize and save the mapTreeStatesMap
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#preDispose()
     * @since 4.2
     */
    public void preDispose() {
        // Default Implementation
    }

    public void updateDiagramPreferences() {
        // Refresh Font
        ((DiagramFontManager)getFontManager()).setFontFromPreferences();

        // Let's get the primary Edit part, get it's figure and update it's BKGD color
        if (getDiagramViewer().getContents() != null && getDiagramViewer().getContents() instanceof DiagramEditPart) {
            DiagramEditPart rootEditPart = (DiagramEditPart)getDiagramViewer().getContents();
            if (rootEditPart != null) rootEditPart.updateForPreferences();
        }

        // update the print grid if print prefs have changed
        getDiagramViewer().updateForPrintPreferences();

    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProviderListener#labelProviderChanged(org.eclipse.jface.viewers.LabelProviderChangedEvent)
     */
    public void labelProviderChanged( LabelProviderChangedEvent event ) {

        boolean modelChanged = false;
        Object[] elements = event.getElements();

        if (elements != null && elements.length > 1) {
            for (int i = 0; i < elements.length; i++) {
                Object nextElement = elements[i];
                if (nextElement instanceof EObject) {
                    if (ModelUtilities.areModelResourcesSame((EObject)nextElement, getDiagram())) modelChanged = true;
                } else if (nextElement instanceof IResource) {
                    if (ModelUtilities.isModelFile((IResource)nextElement)) {
                        ModelResource modelResource = null;
                        try {
                            modelResource = ModelUtilities.getModelResource((IFile)nextElement, false);
                        } catch (ModelWorkspaceException e) {
                            DiagramUiConstants.Util.log(IStatus.ERROR,
                                                        e,
                                                        "DiagramEditor.labelProviderChanged()  ERROR finding ModelResource"); //$NON-NLS-1$
                        }
                        if (modelResource != null && modelResource.equals(modelResource)) {
                            modelChanged = true;
                        }
                    }
                }
                if (modelChanged) break;
            }
        }

        if (getModelFactory() != null && getCurrentModel() != null && modelChanged) {
            getDecoratorHandler().handleLabelProviderChanged();
        }

    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        refreshFont();
        super.setFocus();
        // ----------------------------
        // Defect 22844 - setting ignoreInternalFocus
        // This cleans up simple selection causing focus to OperationEditorPage way too often
        // ----------------------------
        if (meParentEditor != null) meParentEditor.setIgnoreInternalFocus(true);
    }

    /**
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated( IWorkbenchPart part ) {
        // No action
    }

    /**
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop( IWorkbenchPart part ) {
        // No action
    }

    /**
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed( IWorkbenchPart part ) {
        // No action
    }

    /* This call is required because we are managing fonts globally. So if an editor isn't displayed, but has
     * diagram figures containing "disposed" fonts, they need to be updated to the new font, so the GC methods
     * don't barf.... BML 5/18/04
     * On close all, each tab/editor is deactivated and somehow told to "refresh" display before going away...
     */
    private void refreshFont() {
        EditPart editPart = getDiagramViewer().getContents();

        if ((editPart != null) && (editPart instanceof DiagramEditPart)) {
            DiagramEditPart diagramEP = (DiagramEditPart)editPart;

            if ((diagramEP.getCurrentDiagramFont() != null) && diagramEP.getCurrentDiagramFont().isDisposed()) {
                diagramEP.refreshFont(true);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated( IWorkbenchPart part ) {
        refreshFont();
    }

    /**
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened( IWorkbenchPart part ) {
        // No action
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     */
    public void updateReadOnlyState( boolean isReadOnly ) {
        if (getCurrentModel() != null && getCurrentModel().isReadOnly() != isReadOnly) {
            getCurrentModel().setReadOnly(isReadOnly);
            Iterator iter = getCurrentModel().getChildren().iterator();
            Object nextObj = null;
            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramModelNode) {
                    ((DiagramModelNode)nextObj).setReadOnly(isReadOnly);
                }
            }
        }
    }

    void updateReadOnlyState() {
        // Check Readonly status
        if (getEditorInput() instanceof IFileEditorInput) {
            boolean readOnly = ((IFileEditorInput)getEditorInput()).getFile().isReadOnly();
            updateReadOnlyState(readOnly);
        }
    }

    /**
     * @return Returns the zoomFactor.
     * @since 4.2
     */
    public double getCurrentZoomFactor() {
        return this.zoomFactor;
    }

    public void handleZoomChanged() {
        saveZoom();
        // Let's get the primary Edit part, get it's figure and update it's BKGD color
        if (getDiagramViewer().getContents() != null && getDiagramViewer().getContents() instanceof DiagramEditPart) {
            DiagramEditPart rootEditPart = (DiagramEditPart)getDiagramViewer().getContents();
            if (rootEditPart != null) rootEditPart.handleZoomChanged();

            if (diagramController != null) {
                diagramController.handleZoomChanged();
            }

            // update the print grid if zoom has changed diagram size
            getDiagramViewer().updateForPrintPreferences();
        }
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {

        ModelResourceEvent event = (ModelResourceEvent)obj;

        // Return if event concerns closed resource, otherwise subsequent call to checkValidity will cause resource to be
        // re-opened
        final int eventType = event.getType();
        if (eventType == ModelResourceEvent.CLOSING || eventType == ModelResourceEvent.CLOSED) {
            return;
        }

        boolean stillValid = checkValidity(this.getClass().getName() + ".processEvent()"); //$NON-NLS-1$
        final Diagram currentDiagram = getDiagram();

        if (eventType == ModelResourceEvent.RELOADED) {
            final ModelResource evResource = event.getModelResource();
            boolean isSameResource = isCurrentResource(evResource);
            final IResource res = event.getResource();
            if (isSameResource) {
                // we are the editor for the reloaded file:
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        EObject realDiagram = ModelObjectUtilities.getRealEObject(currentDiagram);
                        ModelEditorManager.open(realDiagram, true);
                        if (res instanceof IFile) {
                            IFile file = (IFile)res;
                            ModelEditorManager.activate(file, true);
                        } // endif
                    }
                });
            } else {
                // We also need to check if the current diagram's resource, though different may "import"
                // the event resource, in this case we call open also.

                // defect 16805 - ask the IDiagramType for whether we depend on
                // the event resource.
                if (currentDiagram != null) {
                    IDiagramType idt = DiagramUiPlugin.getDiagramTypeManager().getDiagram(currentDiagram.getType());

                    if (idt.dependsOnResource(currentModel, evResource.getResource())) {
                        // redisplay contents:
                        refreshDiagramSafe();
                    } // endif -- depends
                } // endif -- diagram not null
            }
        } else if (stillValid && eventType == ModelResourceEvent.CHANGED) {
            // Check Readonly status
            updateReadOnlyState();
        } else if (stillValid && eventType == ModelResourceEvent.ADDED) {
            // file should not be represented in the diagram, since it is new.
            // TODO: how do we find if we need to refresh?
        } else if (stillValid && (eventType == ModelResourceEvent.REMOVED || eventType == ModelResourceEvent.MOVED)) {
            // if moved or removed:
            if (currentDiagram != null) {
                IDiagramType idt = DiagramUiPlugin.getDiagramTypeManager().getDiagram(currentDiagram.getType());
                if (idt.dependsOnResource(currentModel, event.getResource())) {
                    // redisplay contents:
                    refreshDiagramSafe();
                } // endif -- depends
            } // endif -- diagram not null
        }
    }

    /**
     * Safely rebuilds the diagram in the display thread, without adjusting the scrolling viewport.
     */
    void refreshDiagramSafe() {
        // redisplay contents:
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                // refresh the diagram:
                // Since this is in an async, we need to really check if the model/workspace isn't closing???
                if (!DiagramUiPlugin.getDefault().getWorkbench().isClosing() && getDiagramViewer().isValidViewer())
                // -----------------------------
                // Defect 23360
                // NPE resulting from deleting a resource and NOT checking for stale diagrams.
                if (getCurrentModel() != null && getCurrentModel().getModelObject() != null
                    && DiagramUiUtilities.isValidDiagram(getDiagram())) {
                    openContext(getCurrentModel().getModelObject(), true);
                }
            }
        });
    }

    public boolean checkValidity( String callPrefix ) {
        // Check if diagram == null, diagram resource == null
        if (getDiagram() == null) {
            //            String message = callPrefix + ":  checkValidity() ERROR - Diagram == NULL"; //$NON-NLS-1$
            // DiagramUiPlugin.Util.log(IStatus.ERROR, message);
            return false;
        }

        ModelResource mr = ModelUtilities.getModelResourceForModelObject(getDiagram());
        if (mr == null) {
            //            String message = callPrefix + ":  checkValidity() ERROR - Model Resource == NULL for current diagram."; //$NON-NLS-1$
            // DiagramUiPlugin.Util.log(IStatus.ERROR, message);
            return false;
        }

        if (DiagramUiUtilities.isValidDiagram(getDiagram())) {
            return false;
        }

        return true;
    }

    private boolean isCurrentResource( ModelResource modelResource ) {
        if (modelResource == null) {
            Assertion.isNotNull(modelResource, DiagramUiConstants.Util.getString("DiagramEditor.isCurrentResourceNullCheck")); //$NON-NLS-1$
        }
        // Check cached model resource
        if (currentModelResource != null && modelResource == currentModelResource) {
            return true;
        }

        // get resource from diagram.
        ModelResource editorMR = ModelUtilities.getModelResourceForModelObject(getDiagram());
        if (editorMR != null) {
            if (modelResource == editorMR) return true;
        } else {
            // Get file for resource
            if (currentModelPath != null) {
                // get file path for resource
                IPath newPath = modelResource.getPath();
                if (currentModelPath.equals(newPath)) return true;
            }
        }

        return false;
    }

    public ModelResource getCurrentModelResource() {
        return this.currentModelResource;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInitializationCompleteNotifier#addListener(com.metamatrix.modeler.ui.editors.IInitializationCompleteListener)
     * @since 4.3
     */
    public void addListener( IInitializationCompleteListener theListener ) {
        if (completionListeners == null) {
            completionListeners = new ArrayList();
        }

        completionListeners.add(theListener);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInitializationCompleteNotifier#notifyInitializationComplete()
     * @since 4.3
     */
    public void notifyInitializationComplete() {
        if (completionListeners != null && !completionListeners.isEmpty()) {
            for (Iterator iter = completionListeners.iterator(); iter.hasNext();) {
                ((IInitializationCompleteListener)iter.next()).processInitializationComplete();
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInitializationCompleteNotifier#removeListener(com.metamatrix.modeler.ui.editors.IInitializationCompleteListener)
     * @since 4.3
     */
    public void removeListener( IInitializationCompleteListener theListener ) {
        if (completionListeners != null && !completionListeners.isEmpty()) {
            completionListeners.remove(theListener);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#canRenameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public IInlineRenameable getInlineRenameable( EObject theObj ) {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#renameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public void renameInline( final EObject theObj,
                              IInlineRenameable renameable ) {
        if (renameable == this) {
            // Set Selection
            // Let's asynch this off
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    // Defect 19537 - replaced call to handleDoubleClick() to use a new renameInline() method
                    // since this is what we really want to do!!!!!
                    getSelectionHandler().renameInline(theObj);
                }
            });

        }
    }

    /**
     * @return False.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst( IEditorInput input ) {
        return false;
    }

    private void autoSelect() {
        if (meParentEditor != null) ModelEditorManager.autoSelectEditor(meParentEditor, this);
    }
}
