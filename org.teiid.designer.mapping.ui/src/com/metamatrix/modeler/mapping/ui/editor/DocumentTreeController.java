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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.modeler.ui.event.IRevealHideListener;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * DocumentTreeController
 */
public class DocumentTreeController implements ITreeViewerListener, ISelectionProvider, ISelectionChangedListener {

    MappingDiagramController diagramController;

    ScrollBar docTreeVertScrollBar;

    private static final int ROW_HEIGHT = 16;
    int verticalScrollValue = 0;

    private DocumentTreeViewer viewer;

    XmlDocumentModelObjectLabelProvider provider;
    private ExtendedDecoratingLabelProvider decLabelProvider;

    public DocumentTreeController( MappingDiagramController mdc ) {
        this.diagramController = mdc;
    }

    public Control getControl() {
        return viewer.getControl();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite theParent ) {
        viewer = new DocumentTreeViewer(theParent);
        viewer.setUseHashlookup(true);

        /*
         * jh Lyra enh: Changing this to use a custom label provider (based on the EMF label provider)
         */
        provider = new XmlDocumentModelObjectLabelProvider(this);

        ILabelDecorator decorator = DiagramUiPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator();
        decLabelProvider = new ExtendedDecoratingLabelProvider(provider, decorator);
        viewer.setLabelProvider(decLabelProvider);

        viewer.addTreeListener(this);
        docTreeVertScrollBar = viewer.getTree().getVerticalBar();
        docTreeVertScrollBar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                // don't process drag events since there are so many of them. another event
                // is received when the drag is done

                // bmlFIXME: Looks like the scroll widget doesn't broadcast anything on "Drag Scroll" other than SWT.DRAG.
                // Don't know what we are going to do here, but this is updating all the time now (i.e. several times per drag)
                if (verticalScrollValue != docTreeVertScrollBar.getSelection()) {
                    resetExtentsFromDocument();
                }
            }
        });

    }

    public void resetExtentsFromDocument() {
        int selectedRow = docTreeVertScrollBar.getSelection();
        int newY = 0 - ROW_HEIGHT * selectedRow;
        verticalScrollValue = selectedRow;
        diagramController.resetExtentLocationsFromDocument(newY);
    }

    public MappingAdapterFilter getMappingAdapterFilter() {
        return viewer.getMappingAdapterFilter();
    }

    public MappingAdapterFilter getMappingAdapterFilter( boolean bForceRecreate ) {
        return viewer.getMappingAdapterFilter(bForceRecreate);
    }

    public XmlDocumentModelObjectLabelProvider getXmlDocumentModelObjectLabelProvider() {
        return provider;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
     */
    public void treeCollapsed( final TreeExpansionEvent event ) {
        // System.out.println("[DocumentTreeController.treeCollapsed] event: " + event.getElement().toString() );

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {

                IRevealHideListener rhl = diagramController.getRevealHideListener();
                if (rhl != null && rhl.isRevealHideBehaviorEnabled()) {
                    List lstChildren = getChildrenOfExpandedNode((EObject)event.getElement(),
                                                                 diagramController.getMappingFilter());

                    rhl.notifyElementsHidden(DocumentTreeController.this, lstChildren);
                }

                /*
                 * Changed reconcileMC's to TRUE because if collapsed, we need to make sure any extra mapping classes are removed
                 * from the diagram.
                 */
                diagramController.refresh(true);
                resetExtentsFromDocument();
            }
        });
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeViewerListener#treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
     */
    public void treeExpanded( final TreeExpansionEvent event ) {
        // System.out.println("[DocumentTreeController.treeExpanded] event: " + event.getElement().toString() );

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {

                IRevealHideListener rhl = diagramController.getRevealHideListener();
                if (rhl != null && rhl.isRevealHideBehaviorEnabled()) {
                    List lstChildren = getChildrenOfExpandedNode((EObject)event.getElement(),
                                                                 diagramController.getMappingFilter());

                    rhl.notifyElementsRevealed(DocumentTreeController.this, lstChildren);
                }
                /*
                 * Changed reconcileMC's to TRUE because if collapsed, we need to make sure any new mapping classes are added
                 * to the diagram.
                 */
                diagramController.refresh(true);
                resetExtentsFromDocument();
            }
        });
    }

    List getChildrenOfExpandedNode( EObject eo,
                                    MappingAdapterFilter xmlFilter ) {
        List lstResult = new ArrayList();

        DocumentContentProvider dcpContentProvider = (DocumentContentProvider)xmlFilter.getTreeViewer().getContentProvider();
        Object[] children = dcpContentProvider.getChildren(eo);

        for (int i = 0; i < children.length; i++) {
            lstResult.add(children[i]);
        }

        return lstResult;
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        this.viewer.addSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return this.viewer.getSelection();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        this.viewer.removeSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection( ISelection selection ) {
        this.viewer.setSelection(selection, true);
    }

    public DocumentTreeViewer getViewer() {
        return this.viewer;
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {

        // System.out.println( "[DocumentTreeController.selectionChanged] TOP" );

        ISelection selection = event.getSelection();

        // walk through selected objects, and add all to list as long as they are either a MappingClass
        // or a MappingClassColumn
        List selections = SelectionUtilities.getSelectedEObjects(selection);
        List mappingSelections = new ArrayList(selections.size());
        List nodeSelections = new ArrayList(selections.size());
        Iterator iter = selections.iterator();
        boolean allSelectionsValid = true;
        boolean selectedMappings = false;
        boolean selectedTreeNodes = false;
        if (selections.size() < 1) allSelectionsValid = false;

        while (iter.hasNext() && allSelectionsValid) {
            Object obj = iter.next();
            if (obj instanceof MappingClass || obj instanceof MappingClassColumn) {
                mappingSelections.add(obj);
                selectedMappings = true;
            } else if (ModelMapperFactory.isXmlTreeNode((EObject)obj)) {
                nodeSelections.add(obj);
                selectedTreeNodes = true;
            } else {
                // Don't know how to hilite anything but mapping class objects
                // in document tree, so we don't send it anything.
                allSelectionsValid = false;
            }
        }

        if (allSelectionsValid) {
            if (selectedMappings) {
                viewer.setSelectedMapping(mappingSelections);
                resetExtentsFromDocument();
            } else if (selectedTreeNodes) {
                // jh Lyra enh
                viewer.setSelectedNodes(nodeSelections);
                resetExtentsFromDocument();

                // jh Defect 22096: Should only do the refresh IF in COARSE, and the
                // 'Populate Diagram From Tree Selection' feature is turned on.
                if (diagramController != null && diagramController.getMappingType() == PluginConstants.COARSE_MAPPING) {
                    boolean bPopulateDiagramFromTreeSelection = MappingDiagramUtil.getCurrentMappingDiagramBehavior().getPopulateDiagramFromTreeSelectionState();
                    if (bPopulateDiagramFromTreeSelection) {
                        refreshOnTreeSelection();
                    }
                }
            }
        } else {
            if (!event.getSource().equals(this.viewer)) viewer.showNoneSelected();
            else {
                viewer.clearAllHilites();
                diagramController.clearDiagramSelection();
            }
        }

    }

    /**
     * @see org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
     */
    public void refreshOnTreeSelection() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                /*
                 * jh fix: This isDisposed check protects us from the case of this method being 
                 *         called because of an tree select event that happens during a CLOSE.  
                 *         Because this is async it will then happen after the close, and the
                 *         tree will no longer be there, causing a 'Widget is disposed' error.
                 *         This check prevents that from happening.                
                 */
                if (!getViewer().getControl().isDisposed()) {
                    diagramController.refresh(false);
                }
            }
        });
    }

    public void reveal( Object eObject ) {

    }

    /**
     * @since 5.0
     */
    public void dispose() {
        // Defect 22290 reflects memory (leaks) issues within designer.
        // remove listeners with disposed.
        decLabelProvider.removeListeners();
    }

    class ExtendedDecoratingLabelProvider extends DecoratingLabelProvider {
        INotifyChangedListener notifyChangedListener;

        public ExtendedDecoratingLabelProvider( ILabelProvider provider,
                                                ILabelDecorator decorator ) {
            super(provider, decorator);
            addListener();

        }

        public void removeListeners() {
            // Defect 22290 reflects memory (leaks) issues within designer.
            // remove listeners with disposed.
            ModelUtilities.removeNotifyChangedListener(notifyChangedListener);
        }

        private void addListener() {
            notifyChangedListener = new INotifyChangedListener() {
                /**
                 * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
                 * @since 4.3
                 */
                public void notifyChanged( Notification notification ) {
                    final Display display = Display.getDefault();
                    if (display.isDisposed()) {
                        return;
                    }
                    EObject eo = NotificationUtilities.getEObject(notification);

                    if (eo != null) {
                        display.asyncExec(new Runnable() {
                            public void run() {
                                changeLabel();
                            }
                        });
                    }
                }
            };

            ModelUtilities.addNotifyChangedListener(notifyChangedListener);
        }

        void changeLabel() {
            LabelProviderChangedEvent event = new LabelProviderChangedEvent(provider.getLabelProviderChangedEventSource(), null);
            fireLabelProviderChanged(event);
        }
    }
}
