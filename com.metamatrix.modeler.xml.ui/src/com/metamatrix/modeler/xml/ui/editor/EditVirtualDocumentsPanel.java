/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xml.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.metamodels.internal.xml.XmlDocumentBuilderImpl;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.OverlayImageIcon;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;
import com.metamatrix.ui.tree.AbstractTreeContentProvider;

/**
 * @author PForhan
 */
public class EditVirtualDocumentsPanel extends Composite implements IVirtualDocumentFragmentSource {

    static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final XmlFragment[] EMPTY_FRAGMENT_ARRAY = new XmlFragment[0];

    CheckboxTreeViewer viewer;
    VDocContentProvider content;
    VDocTreeViewerController controller;
    XmlFragment[] roots;
    Set wasStopped = new HashSet();
    Set wasRecursive = new HashSet();
    int stopExpansion;

    public EditVirtualDocumentsPanel( Composite parent ) {
        super(parent, SWT.NULL);

        initializeGUI();
    }

    private void initializeGUI() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        Composite useParent = this;

        // set up tree:
        controller = new VDocTreeViewerController();
        content = new VDocContentProvider();
        viewer = (CheckboxTreeViewer)WidgetFactory.createTreeViewer(useParent, SWT.CHECK, controller);
        // Create and set content provider for tree:
        viewer.setContentProvider(content);
        // Create and set label provider for tree:
        VDocLabelProvider vdocLabelProvider = new VDocLabelProvider();
        viewer.setLabelProvider(vdocLabelProvider);
        viewer.getTree().addDisposeListener(vdocLabelProvider);
        viewer.setInput(roots);
        viewer.addSelectionChangedListener(new VDocSelectionListener());

        // a key or mouse event would come in later than the tree events:
        // These two listeners are a nasty hack to prevent rampant
        // expansion via the '*' (expand tree) key .
        Control tree = viewer.getControl();
        tree.addKeyListener(new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                stopExpansion = 0;
            }

            public void keyReleased( KeyEvent e ) {
                stopExpansion = 0;
            }
        });
        // see note above.
        tree.addMouseListener(new MouseListener() {
            public void mouseDoubleClick( MouseEvent e ) {
                stopExpansion = 0;
            }

            public void mouseDown( MouseEvent e ) {
                stopExpansion = 0;
            }

            public void mouseUp( MouseEvent e ) {
                stopExpansion = 0;
            }
        });
    }

    static void buildNextLevelFromSchema( XmlElement element ) {
        // open and activate editor first so that the editor is dirty after building:
        // Note: this is not so important for new documents, but for existing ones,
        // we need to make sure the editor is open.
        ModelResource mr = ModelUtilities.getModelResourceForModelObject(element);
        if (mr != null && !mr.isOpen()) {
            ModelEditorManager.activate(mr, true);
        }

        try {
            XmlDocumentBuilderImpl builder = new XmlDocumentBuilderImpl(1);
            builder.buildDocument(element, null);
        } catch (final Exception theException) {
            final String msg = ModelerXmlUiConstants.Util.getString("NumberOfLevelsWizard.buildErrorMessage"); //$NON-NLS-1$
            ModelerXmlUiConstants.Util.log(IStatus.ERROR, theException, msg);
        } // endtry

        XmlDocumentUtil.setAllExcluded(element, true);
    }

    /**
     * Note that calling this method sets in motion final processing; The fragments the editor is working with will be changed and
     * things not checked in the editor will be deleted.
     */
    public XmlFragment[] getFragments( ModelResource modelResource,
                                       IProgressMonitor monitor ) {
        // TODO this method should make use of a progress monitor!
        List rv = new ArrayList(roots.length);
        for (int i = 0; i < roots.length; i++) {
            XmlFragment root = roots[i];
            try {
                boolean panelHidden = !isVisible();
                if (viewer.getChecked(root) || panelHidden) {
                    // this root was checked, or the panel is not showing, so assume it is:
                    pruneChildren(root, wasStopped, wasRecursive);
                    rv.add(root);
                } else {
                    // we are not including this root; delete:
                    ModelerCore.getModelEditor().delete(root, false);
                } // endif
            } catch (ModelerCoreException ex) {
                ModelerXmlUiConstants.Util.log(ex);
            } // endtry
        } // endfor

        return (XmlFragment[])rv.toArray(EMPTY_FRAGMENT_ARRAY);
    }

    /**
     * Return the set of root XML Elements
     */
    public Collection getRoots( IProgressMonitor monitor ) {
        if (roots == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(roots);
    }

    public boolean haveFragmentsChanged() {
        return true;
    }

    /** Implemented to do nothing for now; the Preview WizardPage handles this */
    public void updateSourceFragments( boolean isVisible,
                                       IProgressMonitor monitor ) {
    }

    private static void pruneChildren( XmlDocumentEntity node,
                                       Set wasStopped,
                                       Set wasRecursive ) throws ModelerCoreException {
        List kids = new ArrayList(node.eContents()); // wrap in an AL to prevent outside interference
        for (int i = 0; i < kids.size(); i++) {
            XmlDocumentEntity kid = (XmlDocumentEntity)kids.get(i);

            if (XmlDocumentUtil.isExcluded(kid, false)) {
                // no need for any excluded objects (or their kids):
                // false below because we don't want to check the resource,
                // since there may be none (new documents).
                ModelerCore.getModelEditor().delete(kid, false);
            } else {
                // not excluded (that would be included, folks), so process further:
                if (!XmlDocumentUtil.isIncomplete(kid)) {
                    // build didn't stop on this child, so dig in further:
                    pruneChildren(kid, wasStopped, wasRecursive);
                } // endif

                // now that any unneeded kids have been removed, see if I still
                // belong or if I need to revert any of my flags:
                boolean hasElements = XmlDocumentUtil.hasElementChildren(kid);
                if (!hasElements) {
                    // no elements below, may need to do something more:
                    if (kid instanceof XmlContainerNode) {
                        // delete any empty XmlContainers:
                        ModelerCore.getModelEditor().delete(kid);
                    } else {
                        // restore attributes:
                        if (wasStopped.contains(kid)) {
                            XmlDocumentUtil.setIncomplete(kid, true);
                        } // endif
                        if (wasRecursive.contains(kid)) {
                            XmlDocumentUtil.setRecursive(kid, true);
                        } // endif -- was recursive
                    } // endif -- was container node
                } // endif -- no element children
            } // endif -- was excluded
        } // endfor -- children
    }

    public XmlFragment[] getStartingFragments() {
        return roots;
    }

    public void setFragments( final XmlFragment[] docRoots ) {
        roots = docRoots;
    }

    /**
     * Set the initial documents and fragments to edit.
     * 
     * @param docRoots the fragments and documents to edit. These should be safe for EditVirtualDocumentsPanel to change.
     */
    public void setStartingFragments( final XmlFragment[] docRoots,
                                      final boolean isVisible,
                                      final IProgressMonitor monitor ) {
        roots = docRoots;
        if (viewer != null) {
            // do some init in the display thread:
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if (isVisible) viewer.setInput(roots);
                }
            });

            // eliminate a little overhead by going through the array here:
            for (int i = 0; i < docRoots.length; i++) {
                walkTreeAndCheck(content, docRoots[i], isVisible ? viewer : null, monitor);
            } // endfor
            monitor.subTask(ModelerXmlUiConstants.Util.getString("EditVirtualDocumentsPanel.subtaskDisplaying")); //$NON-NLS-1$

            // do the clean up in the display thread:
            // Defect 18433 - Changed thread to SYNC because we want all work to be done on the SAME thread so the transaction
            // boundary is maintained and the creation of a new XML DOcument(s) is Undoable
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if (roots.length > 0) {
                        viewer.setSelection(new StructuredSelection(roots[0]));
                        // make sure all roots are checked, regardless of this panel's isVisible status:
                        for (int i = 0; i < roots.length; i++) {
                            XmlFragment fragment = roots[i];
                            viewer.setChecked(fragment, true);
                            // make sure required children are checked:
                            controller.checkRequiredChildren(fragment);
                            if (isVisible) monitor.worked(1); // indicate progress
                        } // endfor
                    } // endif
                }
            });
        } // endif
    }

    /**
     * Check and expand all leaf nodes that should be initially checked. this method is safe to run from a non-gui thread.
     * 
     * @param fragment
     */
    private static void walkTreeAndCheck( ITreeContentProvider tcp,
                                          final Object current,
                                          final CheckboxTreeViewer viewer,
                                          IProgressMonitor monitor ) {
        // abort if necessary:
        if (monitor.isCanceled()) return;

        boolean onlyAttributeKids = true;
        Object[] kids = null;

        if (!XmlDocumentUtil.isIncomplete(current)) {
            // mark included:
            if (viewer != null) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        viewer.setChecked(current, true); // this creates TreeItems...
                    }
                });
            } // endif

            // it's OK to call getChildren without hasChildren here because
            // we checked the stopped attribute above.
            kids = tcp.getChildren(current); // note that this does *not* create TreeItems
            for (int i = 0; i < kids.length; i++) {
                // abort if necessary:
                if (monitor.isCanceled()) return;

                // process each child:
                Object kid = kids[i];
                walkTreeAndCheck(tcp, kid, viewer, monitor);
                if (!(kid instanceof XmlAttribute)) {
                    onlyAttributeKids = false;
                } // endif
            } // endfor
        } else {
            // was stopped, and was not a root, mark as excluded (to get the model in sync with the tree):
            setExcluded(current, true);
        } // endif

        // abort if necessary:
        if (monitor.isCanceled()) return;

        if (viewer != null && kids != null && kids.length > 0 && !onlyAttributeKids) {
            // parent of a non-attribute leaf node; make sure I am expanded:
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    viewer.expandToLevel(current, 1); // this creates TreeItems...
                }
            });
        } // endif
        monitor.worked(1);
    }

    static void setExcluded( final Object element,
                             final boolean exclude ) {
        boolean startedTxn = ModelerCore.startTxn(false, false, "Set Excluded", null); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            XmlDocumentUtil.setExcluded(element, exclude);
            succeeded = true;
        } finally {
            if (startedTxn) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    class VDocContentProvider extends AbstractTreeContentProvider {
        @Override
        public boolean hasChildren( Object element ) {
            // prevent rampant expansion:
            if (stopExpansion > 1) return false;

            if (element == roots && roots.length > 0) {
                return true;
            } // endif

            if (XmlDocumentUtil.isIncomplete(element)) { // only process if we are not programattically unchecking
                // double-check the node to make sure it really is expandable:
                if (element instanceof XmlDocumentNode) {
                    XmlDocumentNode dnode = (XmlDocumentNode)element;
                    boolean isComplex = XmlDocumentUtil.hasComplexType(dnode);
                    if (!isComplex) {
                        // a simpleType is not really stopped... just a consequence of building
                        // only one level of a document.
                        XmlDocumentUtil.setIncomplete(element, false);
                    } // endif
                    return isComplex;
                } // endif

                // not a documentNode, just return true to allow expansion:
                return true;
            } // endif

            return !((XmlDocumentEntity)element).eContents().isEmpty();
        }

        @Override
        public Object[] getChildren( final Object element ) {
            // prevent rampant expansion:
            if (stopExpansion > 1) return EMPTY_OBJECT_ARRAY;

            if (element == roots) {
                return roots;
            } // endif

            if (XmlDocumentUtil.isIncomplete(element)) {
                // track states for later, in case user changes their mind:
                wasStopped.add(element);

                if (XmlDocumentUtil.isRecursive(element)) {
                    wasRecursive.add(element);
                } // endif

                // do the work:
                buildNextLevelFromSchema((XmlElement)element);

                // make sure the check state is synchronized:
                if (viewer.getChecked(element)) {
                    setExcluded(element, false);
                } // endif -- item checked
            } // endif -- was stopped

            return ((XmlDocumentEntity)element).eContents().toArray();
        }

        public Object getParent( final Object element ) {
            if (element == roots) {
                return null;
            } // endif

            if (element instanceof XmlFragment) {
                return roots;
            } // endif

            return ((XmlDocumentEntity)element).eContainer();
        }
    } // endclass VDocContentProvider

    class VDocTreeViewerController extends DefaultTreeViewerController {
        @Override
        public void checkedStateToggled( final TreeItem item ) {
            boolean shouldExclude = !item.getChecked();

            // modify the value of shouldExclude:
            Object nodeData = item.getData();
            setExcluded(nodeData, shouldExclude);

            if (shouldExclude) {
                // uncheck kids if unchecked:
                excludeNodeAndChildren(content, nodeData);
            } else {
                // make sure ancestors are checked:
                checkRequiredAncestors(nodeData);
                checkDefaultChildren(nodeData);
            } // endif
        }

        @Override
        public boolean isItemCheckable( final TreeItem item ) {
            Object d = item.getData();
            return isUserCheckable(d);
        }

        @Override
        public void update( TreeItem item,
                            boolean selected ) {
            // force non-elements and non-fragments to keep their checked state:
            Object d = item.getData();
            if (!isUserCheckable(d) && item.getChecked()) {
                // force ancestors to be checked, too:
                checkRequiredAncestors(d);
                viewer.setChecked(d, true);
                setExcluded(d, false);
            } else if (viewer.getChecked(d)) {
                // we are an element or fragment, check kids and ancestors:
                checkRequiredAncestors(d);
                checkRequiredChildren(d);
            } // endif
        }

        @Override
        public void itemDoubleClicked( DoubleClickEvent event ) {
            treeExpanded(SelectionUtilities.getSelectedObject(event.getSelection()));
        }

        @Override
        public void itemExpanded( TreeExpansionEvent event ) {
            // try to prevent rampant expansion:
            stopExpansion++;
            treeExpanded(event.getElement());
        }

        private void treeExpanded( final Object nodeData ) {
            if (viewer.getChecked(nodeData)) {
                checkRequiredChildren(nodeData);
            } // endif
            if (wasStopped.contains(nodeData)) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        viewer.refresh(nodeData);
                    }
                });
            } // endif
        }

        private boolean isUserCheckable( Object d ) {
            return !(d instanceof XmlRoot) // defect 17530 users cannot uncheck the root element.
                   && (d instanceof XmlElement || d instanceof XmlFragment || d instanceof XmlContainerNode || d instanceof XmlAttribute);
        }

        private void excludeNodeAndChildren( ITreeContentProvider tcp,
                                             Object nodeData ) {
            Object[] kids;
            // only scan children of an object if that object is complete, and has children.
            if (!XmlDocumentUtil.isIncomplete(nodeData) && tcp.hasChildren(nodeData)) {
                kids = tcp.getChildren(nodeData);
            } else {
                // no kids of interest:
                kids = EMPTY_OBJECT_ARRAY;
            } // endif
            for (int i = 0; i < kids.length; i++) {
                Object kid = kids[i];
                // only scan an object if it is checked:
                if (viewer.getChecked(kid)) excludeNodeAndChildren(tcp, kid);
            } // endfor
            setExcluded(nodeData, true);
            viewer.setChecked(nodeData, false);
        }

        public void checkRequiredChildren( Object nodeData ) {
            // I am checked, make sure my non-clickable children are checked:
            Object[] kids;
            if (!XmlDocumentUtil.isIncomplete(nodeData) && content.hasChildren(nodeData)) {
                kids = content.getChildren(nodeData);
            } else {
                kids = EMPTY_OBJECT_ARRAY;
            } // endif

            for (int i = 0; i < kids.length; i++) {
                Object kid = kids[i];
                // need to check any included children that are not checkable:
                if (!isUserCheckable(kid)) {
                    // this is safe, because these entries don't have an excluded property:
                    viewer.setChecked(kid, true);
                    setExcluded(kid, false);
                } // endif
            } // endfor
        }

        private void checkDefaultChildren( Object nodeData ) {
            // I am checked, make sure my some of my children are checked:
            Object[] kids;
            if (!XmlDocumentUtil.isIncomplete(nodeData) && content.hasChildren(nodeData)) {
                kids = content.getChildren(nodeData);
            } else {
                kids = EMPTY_OBJECT_ARRAY;
            } // endif

            for (int i = 0; i < kids.length; i++) {
                Object kid = kids[i];
                // need to check any included children that are "simple":
                if (kid instanceof XmlAttribute || (kid instanceof XmlElement && !XmlDocumentUtil.hasComplexType(kid))) {
                    // this is safe, because these entries don't have an excluded property:
                    viewer.setChecked(kid, true);
                    setExcluded(kid, false);
                } // endif
            } // endfor
        }

        private void checkRequiredAncestors( Object nodeData ) {
            // I am checked, make sure all of my ancestors are checked:
            // (this for things like XmlContainerNodes)
            Object parent = content.getParent(nodeData);

            // walk ancestors to the top or until I hit a checked
            // one (since its ancestors are already checked):
            while (parent != null && !viewer.getChecked(parent)) {
                // need to check any ancestor that is not checked:
                viewer.setChecked(parent, true);
                setExcluded(parent, false);
                checkRequiredChildren(parent);
                parent = content.getParent(parent);
            } // endwhile
        }
    } // endclass VDocTreeViewerController

    static class VDocLabelProvider extends ModelExplorerLabelProvider implements DisposeListener {

        // Instance variables:
        Map classToLabelProvider = new HashMap();
        private HashMap decoratedImageRegistry = new HashMap();
        private List temporaryImages = new ArrayList();
        private List newDecoratedImages = new ArrayList();

        // Utility methods:
        private String getDatatypeText( XSDComponent xsdc ) {
            if (xsdc == null) {
                return ""; //$NON-NLS-1$
            } // endif
            Class xclass = xsdc.getClass();
            IItemLabelProvider provider = (IItemLabelProvider)classToLabelProvider.get(xclass);
            if (provider == null) {
                AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
                provider = (IItemLabelProvider)adapterFactory.adapt(xsdc, IItemLabelProvider.class);
                classToLabelProvider.put(xclass, provider);
            } // endif

            return provider.getText(xsdc);
        }

        // Overrides:
        @Override
        public String getText( Object element ) {
            if (element instanceof XmlDocumentNode) {
                XmlDocumentNode xdn = (XmlDocumentNode)element;
                String rv = getDatatypeText(xdn.getXsdComponent());
                if (rv == null || rv.length() == 0) {
                    return super.getText(element);
                } // endif

                return rv;
            } // endif

            return super.getText(element);
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            Image originalImage = super.getImage(element);

            boolean recursive = XmlDocumentUtil.isRecursive(element);
            boolean incomplete = XmlDocumentUtil.hasComplexType(element) && XmlDocumentUtil.isIncomplete(element);
            int cardinality = XmlDocumentUtil.getCardinality(element);

            ImageKey imageKey = new ImageKey(originalImage, recursive, incomplete, cardinality);
            return getDecoratedImage(imageKey);
        }

        private Image getDecoratedImage( ImageKey key ) {
            Image decoratedImage = (Image)decoratedImageRegistry.get(key);
            if (decoratedImage == null) {
                Image originalImage = key.getImage();
                decoratedImage = originalImage;

                if (key.isRecursive()) {
                    decoratedImage = decorateWithTopLeftImage(decoratedImage,
                                                              ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.RECURSIVE));
                }

                if (key.isIncomplete()) {
                    if (decoratedImage != originalImage) {
                        temporaryImages.add(decoratedImage);
                    }
                    decoratedImage = decorateWithTopRightImage(decoratedImage,
                                                               ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.INCOMPLETE));
                }

                Image cardinalityImage = getCardinalityImage(key.getCardinality());
                if (cardinalityImage != null) {
                    if (decoratedImage != originalImage) {
                        temporaryImages.add(decoratedImage);
                    }
                    decoratedImage = decorateWithBottomLeftImage(decoratedImage, cardinalityImage);
                }

                disposeTemporaryImages();

                if (decoratedImage != originalImage) {
                    newDecoratedImages.add(decoratedImage);
                }
                decoratedImageRegistry.put(key, decoratedImage);
            }
            return decoratedImage;
        }

        private Image getCardinalityImage( int cardinality ) {
            Image cardinalityImage = null;
            switch (cardinality) {
                case XmlDocumentUtil.XSD_OCCURRENCE_N:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_N);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_NToM:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_NToM);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_NToUnbounded:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_NToUnbounded);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_Zero:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_Zero);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_ZeroToOne:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_ZeroToOne);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_ZeroToN:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_ZeroToN);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_ZeroToUnbounded:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_ZeroToUnbounded);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_One:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_One);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_OneToN:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_OneToN);
                    break;
                case XmlDocumentUtil.XSD_OCCURRENCE_OneToUnbounded:
                    cardinalityImage = ModelerXmlUiPlugin.getDefault().getImage(ModelerXmlUiConstants.Images.XSD_OCCURRENCE_OneToUnbounded);
                    break;
                default:
            }
            return cardinalityImage;
        }

        private Image decorateWithTopLeftImage( Image originalImage,
                                                Image overlayImage ) {
            OverlayImageIcon overlayIcon = new OverlayImageIcon(originalImage, overlayImage, OverlayImageIcon.TOP_LEFT);
            return overlayIcon.getImage();
        }

        private Image decorateWithTopRightImage( Image originalImage,
                                                 Image overlayImage ) {
            OverlayImageIcon overlayIcon = new OverlayImageIcon(originalImage, overlayImage, OverlayImageIcon.TOP_RIGHT);
            return overlayIcon.getImage();
        }

        private Image decorateWithBottomLeftImage( Image originalImage,
                                                   Image overlayImage ) {
            OverlayImageIcon overlayIcon = new OverlayImageIcon(originalImage, overlayImage, OverlayImageIcon.BOTTOM_LEFT);
            return overlayIcon.getImage();
        }

        private void disposeTemporaryImages() {
            while (temporaryImages.size() > 0) {
                Image tempImage = (Image)temporaryImages.remove(temporaryImages.size() - 1);
                tempImage.dispose();
            }
        }

        private void disposeNewDecoratedImages() {
            while (newDecoratedImages.size() > 0) {
                Image tempImage = (Image)newDecoratedImages.remove(newDecoratedImages.size() - 1);
                tempImage.dispose();
            }
        }

        /**
         * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
         */
        public void widgetDisposed( DisposeEvent e ) {
            disposeNewDecoratedImages();
        }
    }

    private static class ImageKey {

        private Image image;
        private boolean recursive;
        private boolean incomplete;
        private int cardinality;

        /**
         * @param image
         * @param recursive
         * @param incomplete
         * @param cardinality
         */
        public ImageKey( Image image,
                         boolean recursive,
                         boolean incomplete,
                         int cardinality ) {
            super();
            this.image = image;
            this.recursive = recursive;
            this.incomplete = incomplete;
            this.cardinality = cardinality;
        }

        /**
         * @return Returns the cardinality.
         */
        public int getCardinality() {
            return cardinality;
        }

        /**
         * @return Returns the complete.
         */
        public boolean isIncomplete() {
            return incomplete;
        }

        /**
         * @return Returns the image.
         */
        public Image getImage() {
            return image;
        }

        /**
         * @return Returns the recursive.
         */
        public boolean isRecursive() {
            return recursive;
        }

        /**
         * Returns <code>true</code> if this <code>ImageKey</code> is the same as the o argument.
         * 
         * @return <code>true</code> if this <code>ImageKey</code> is the same as the o argument.
         */
        @Override
        public boolean equals( Object o ) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            ImageKey castedObj = (ImageKey)o;
            return ((this.image == null ? castedObj.image == null : this.image.equals(castedObj.image))
                    && (this.recursive == castedObj.recursive) && (this.incomplete == castedObj.incomplete) && (this.cardinality == castedObj.cardinality));
        }

        /**
         * Override hashCode.
         * 
         * @return the Objects hashcode.
         */
        @Override
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + (image == null ? 0 : image.hashCode());
            hashCode = 31 * hashCode + (recursive ? 1231 : 1237);
            hashCode = 31 * hashCode + cardinality;
            return hashCode;
        }
    }

    class VDocSelectionListener implements ISelectionChangedListener {
        private TreeItem lastColored;

        public void selectionChanged( SelectionChangedEvent event ) {
            // unhighlight previous:
            if (lastColored != null && !lastColored.isDisposed()) {
                lastColored.setBackground(null);
            } // endif
            // highlight recursive root if needed:
            Object nodeData = SelectionUtilities.getSelectedObject(event.getSelection());

            if (XmlDocumentUtil.isRecursive(nodeData)) {
                // save the type of the selected thing. Note that we can safely
                // cast because only XmlElements can be recursive:
                XSDTypeDefinition sType = XmlDocumentUtil.findXSDType(nodeData);
                // should be only one selected:
                TreeItem parent = viewer.getTree().getSelection()[0].getParentItem();
                while (parent != null) {
                    // need to color any ancestor that is of the same type:
                    Object parentData = parent.getData();

                    if (parentData instanceof XmlElement) {
                        // only check elements... nothing else could have a recursive nature:
                        XmlElement parentElement = (XmlElement)parentData;
                        XSDTypeDefinition pType = XmlDocumentUtil.findXSDType(parentElement);
                        if (pType.equals(sType)) {
                            lastColored = parent;
                            parent.setBackground(DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND);
                            break; // remove to highlight all roots sharing this type.
                        } // endif
                    } // endif
                    parent = parent.getParentItem();
                } // endwhile
            } // endif
        }
    } // endclass VDocSelectionListener

}
