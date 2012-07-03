/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.ui.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.xerces.util.EncodingMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.ViewerPane;
import org.eclipse.emf.common.ui.celleditor.ExtendedComboBoxCellEditor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMLHandler;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.action.CreateChildAction;
import org.eclipse.emf.edit.ui.action.CreateSiblingAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;
import org.eclipse.xsd.provider.XSDItemProviderAdapterFactory;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDParser;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.eclipse.xsd.util.XSDSwitch;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.helpers.DefaultHandler;
import com.metamatrix.metamodels.xsd.XsdResourceFactory;
import com.metamatrix.metamodels.xsd.ui.XsdUiPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * This is a an example of a xsd model editor.
 */
public class XsdEditor extends MultiPageEditorPart
    implements IEditingDomainProvider, ISelectionProvider, IMenuListener, IGotoMarker {

    /**
     * ResourceSet to be used when constructing the editor.
     */
    protected static ResourceSet resourceSet;

    /**
     * This keeps track of the root object of the model.
     */
    protected XSDSchema xsdSchema;

    /**
     * This keeps track of the editing domain that is used to track all changes to the model.
     */
    protected AdapterFactoryEditingDomain editingDomain;

    /**
     * This is the adapter factory used for providing the syntactive views of the model.
     */
    protected XSDItemProviderAdapterFactory syntacticAdapterFactory;

    /**
     * This is the adapter factory used for providing the semantic views of the model.
     */
    protected XSDItemProviderAdapterFactory semanticAdapterFactory;

    /**
     * This is the content outline page.
     */
    protected IContentOutlinePage contentOutlinePage;

    /**
     * This is a kludge...
     */
    protected IStatusLineManager contentOutlineStatusLineManager;

    /**
     * This is the content outline page's viewer.
     */
    protected TreeViewer contentOutlineViewer;

    /**
     * This is the property sheet page.
     */
    protected PropertySheetPage propertySheetPage;

    /**
     * This source part of the editor.
     */
    protected TextEditor textEditor;
    protected ISourceViewer sourceViewer;

    /**
     * This is the syntactic viewer that shadows the selection in the content outline. The parent relation must be correctly
     * defined for this to work.
     */
    protected TreeViewer syntacticSelectionViewer;

    /**
     * This is the semantic viewer that shadows the selection in the content outline.
     */
    protected TreeViewer semanticSelectionViewer;

    /**
     * This keeps track of the active viewer pane, in the book.
     */
    protected ViewerPane currentViewerPane;

    /**
     * This keeps track of the active content viewer, which may be either one of the viewers in the pages or the content outline
     * viewer.
     */
    protected Viewer currentViewer;

    /**
     * This listens to which ever viewer is active.
     */
    protected ISelectionChangedListener selectionChangedListener;

    /**
     * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this editor.
     */
    protected Collection selectionChangedListeners = new ArrayList();

    /**
     * This keeps track of the selection of the editor as a whole.
     */
    protected ISelection editorSelection;

    /**
     * This is the outline action to select the next unresolved component.
     */
    protected SelectDiagnosticAction selectNextDiagnosticsAction;

    /**
     * This is the outline action to select the previous unresolved component.
     */
    protected SelectDiagnosticAction selectPreviousDiagnosticsAction;

    /**
     * This is the outline action to select the next use of a component.
     */
    protected SelectUseAction selectNextUseAction;

    /**
     * This is the outline action to select the previous use of a component.
     */
    protected SelectUseAction selectPreviousUseAction;

    /**
     * This is the model resource for the current xsd file being displayed in the editor
     */
    private Resource xsdResource;

    /**
     * This listens for when things becomes active.
     */
    protected IPartListener partListener = new IPartListener() {
        public void partActivated( IWorkbenchPart p ) {
            handlePartActivated(p);
        }

        public void partBroughtToTop( IWorkbenchPart p ) {
        }

        public void partClosed( IWorkbenchPart p ) {
        }

        public void partDeactivated( IWorkbenchPart p ) {
        }

        public void partOpened( IWorkbenchPart p ) {
        }
    };

    /**
     * This creates a model editor.
     */
    public XsdEditor() {
        super();

        // Create an adapter factory that yields item providers.
        //
        syntacticAdapterFactory = new XSDItemProviderAdapterFactory();
        semanticAdapterFactory = new XSDSemanticItemProviderAdapterFactory();

        // Create the command stack that will notify this editor as commands are executed.
        //
        BasicCommandStack commandStack = new BasicCommandStack();

        // Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
        //
        commandStack.addCommandStackListener(new CommandStackListener() {
            public void commandStackChanged( final EventObject event ) {
                getParentComposite().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        firePropertyChange(IEditorPart.PROP_DIRTY);

                        // Try to select the affected objects.
                        //
                        Command mostRecentCommand = ((CommandStack)event.getSource()).getMostRecentCommand();
                        if (mostRecentCommand != null) {
                            setSelectionToViewer(mostRecentCommand.getAffectedObjects());
                        }

                        handleStructuredModelChange();

                        updateActions();

                        if (propertySheetPage != null) {
                            propertySheetPage.refresh();
                        }

                    }
                });

            }
        });

        if (resourceSet != null) {
            editingDomain = new AdapterFactoryEditingDomain(syntacticAdapterFactory, commandStack, resourceSet);
        } else {
            editingDomain = new AdapterFactoryEditingDomain(syntacticAdapterFactory, commandStack);
        }

        // Register our xsd resource factory for this context.
        //
        Map map = editingDomain.getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
        if (!map.containsKey(ModelUtil.EXTENSION_XSD)) {
            map.put(ModelUtil.EXTENSION_XSD, new XsdResourceFactory());
        }
    }

    Composite getParentComposite() {
        return getContainer();
    }

    protected void updateActions() {
        if (selectNextDiagnosticsAction != null) {
            selectNextDiagnosticsAction.updateAction();
            selectPreviousDiagnosticsAction.updateAction();

            selectNextUseAction.updateAction();
            selectPreviousUseAction.updateAction();
        }
    }

    protected String determineEncoding() {
        String encoding = (String)((XSDResourceImpl)getXsdSchema().eResource()).getDefaultSaveOptions().get(XSDResourceImpl.XSD_ENCODING);
        if (encoding != null && EncodingMap.getIANA2JavaMapping(encoding) != null) {
            encoding = EncodingMap.getIANA2JavaMapping(encoding);
        }
        return encoding;
    }

    protected boolean handledStructuredModelChange = false;

    protected void handleStructuredModelChange() {
        IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
        if (getXsdSchema().getElement() == null) {
            getXsdSchema().updateElement();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            getXsdSchema().eResource().save(out, null);
            String encoding = determineEncoding();
            String newContent = encoding == null ? out.toString() : out.toString(encoding);
            String oldContent = document.get();

            int startIndex = 0;
            while (startIndex < newContent.length() && startIndex < oldContent.length()
                   && newContent.charAt(startIndex) == oldContent.charAt(startIndex)) {
                ++startIndex;
            }
            int newEndIndex = newContent.length() - 1;
            int oldEndIndex = oldContent.length() - 1;
            while (newEndIndex >= startIndex && oldEndIndex >= startIndex
                   && newContent.charAt(newEndIndex) == oldContent.charAt(oldEndIndex)) {
                --newEndIndex;
                --oldEndIndex;
            }

            String replacement = newContent.substring(startIndex, newEndIndex + 1);
            int length = oldEndIndex - startIndex + 1;
            handledStructuredModelChange = true;
            document.replace(startIndex, length, replacement);

            getXsdSchema().eResource().setModified(true);
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    /**
     * This handles part activation.
     */
    protected void handlePartActivated( IWorkbenchPart workbenchPart ) {
        if (workbenchPart == this) {
            if (getActivePage() == 0) {
                setCurrentViewer((Viewer)sourceViewer);
            }
        } else if (workbenchPart instanceof ContentOutline) {
            if (((ContentOutline)workbenchPart).getCurrentPage() == contentOutlinePage) {
                getEditorSite().getActionBarContributor().setActiveEditor(XsdEditor.this);

                setCurrentViewer(contentOutlineViewer);
            }
        } else if (workbenchPart instanceof PropertySheet) {
            if (((PropertySheet)workbenchPart).getCurrentPage() == propertySheetPage) {
                getActionBarContributor().setActiveEditor(XsdEditor.this);
            }
        }
    }

    /**
     * This is here for the listener to be able to call it.
     */
    @Override
    protected void firePropertyChange( int action ) {
        super.firePropertyChange(action);
    }

    /**
     * This sets the selection into whichever viewer is active.
     */
    public void setSelectionToViewer( final Collection collection ) {
        // Make sure it's okay.
        //
        if (collection != null && !collection.isEmpty()) {
            // I don't know if this should be run this deferred
            // because we might have to give the editor a chance to process the viewer update events
            // and hence to update the views first.
            //
            //
            Runnable runnable = new Runnable() {
                public void run() {
                    // Try to select the items in the current content viewer of the editor.
                    //
                    if (currentViewer != null) {
                        currentViewer.setSelection(new StructuredSelection(collection.toArray()), true);
                    }
                }
            };
            runnable.run();
        }
    }

    /**
     * This returns the editing domain as required by the {@link IEditingDomainProvider} interface. This is important for
     * implementing the static methods of {@link AdapterFactoryEditingDomain} and for supporting {@link CommandAction}.
     */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }

    public void setCurrentViewerPane( ViewerPane viewerPane ) {
        if (currentViewerPane != viewerPane) {
            if (currentViewerPane != null) {
                currentViewerPane.showFocus(false);
            }
            currentViewerPane = viewerPane;
        }

        if (currentViewerPane != null) {
            setCurrentViewer(currentViewerPane.getViewer());
        }
    }

    /**
     * This makes sure that one content viewer, either for the current page or the outline view, if it has focus, is the current
     * one.
     */
    public void setCurrentViewer( Viewer viewer ) {
        // If it is changing...
        //
        if (currentViewer != viewer) {
            if (selectionChangedListener == null) {
                // Create the listener on demand.
                //
                selectionChangedListener = new ISelectionChangedListener() {
                    // This just notifies those things that are affected by the section.
                    //
                    public void selectionChanged( SelectionChangedEvent selectionChangedEvent ) {
                        setSelection(selectionChangedEvent.getSelection());
                    }
                };
            }

            // Stop listening to the old one.
            //
            if (currentViewer != null) {
                currentViewer.removeSelectionChangedListener(selectionChangedListener);
            }

            // Start listening to the new one.
            //
            if (viewer != null) {
                viewer.addSelectionChangedListener(selectionChangedListener);
            }

            // Remember it.
            //
            currentViewer = viewer;

            // Set the editors selection based on the current viewer's selection.
            //
            setSelection(currentViewer == null ? StructuredSelection.EMPTY : currentViewer.getSelection());
        }
    }

    /**
     * This is the contributor for the XSD model editor.
     */
    static public class ActionBarContributor extends EditingDomainActionBarContributor implements ISelectionChangedListener {
        protected IEditorPart activeEditorPart;
        protected ISelectionProvider selectionProvider;

        /**
         * This will contain one CreateChildAction corresponding to each descriptor generated for the current selection.
         */
        protected Collection createChildActions = Collections.EMPTY_LIST;

        /**
         * This is the menu manager into which menu contribution items should be added for the child creation actions.
         */
        protected IMenuManager createChildMenuManager;

        /**
         * This will contain one CreateSiblingAction corresponding to each descriptor generated for the current selection's
         * parent.
         */
        protected Collection createSiblingActions = Collections.EMPTY_LIST;

        /**
         * This is the menu manager into which menu contribution items should be added for sibling creation actions.
         */
        protected IMenuManager createSiblingMenuManager;

        /**
         * This creates an instance of the contributor.
         */
        public ActionBarContributor() {
        }

        /**
         * This adds to the menu bar a menu for editor actions, duplicating the menu contribution made in the plugin.xml, so that
         * the new menu is accessible for modification in code. Also, sub-menus are created for the addition and removal of child
         * and sibling creation items.
         */
        @Override
        public void contributeToMenu( IMenuManager menuManager ) {
            super.contributeToMenu(menuManager);

            // duplicate the menu contribution in the plugin.xml
            IMenuManager submenuManager = new MenuManager(
                                                          XsdUiPlugin.Util.getString("_UI_XSDEditor_menu"), "org.eclipse.xsdMenuID"); //$NON-NLS-1$ //$NON-NLS-2$
            menuManager.insertAfter(XsdUiPlugin.Util.getString("XsdEditor.additions_4"), submenuManager); //$NON-NLS-1$
            submenuManager.add(new Separator(XsdUiPlugin.Util.getString("XsdEditor.settings_5"))); //$NON-NLS-1$
            submenuManager.add(new Separator(XsdUiPlugin.Util.getString("XsdEditor.additions_6"))); //$NON-NLS-1$

            // prepare for child and sibling creation item addition/removal
            createChildMenuManager = new MenuManager(XsdUiPlugin.Util.getString("_UI_CreateChild_menu_item")); //$NON-NLS-1$
            createSiblingMenuManager = new MenuManager(XsdUiPlugin.Util.getString("_UI_CreateSibling_menu_item")); //$NON-NLS-1$
            submenuManager.insertBefore(XsdUiPlugin.Util.getString("XsdEditor.additions_9"), new Separator(XsdUiPlugin.Util.getString("XsdEditor.actions_10"))); //$NON-NLS-1$ //$NON-NLS-2$
            submenuManager.insertBefore(XsdUiPlugin.Util.getString("XsdEditor.additions_11"), createChildMenuManager); //$NON-NLS-1$
            submenuManager.insertBefore(XsdUiPlugin.Util.getString("XsdEditor.additions_12"), createSiblingMenuManager); //$NON-NLS-1$
        }

        /**
         * This adds Separators to the tool bar.
         */
        @Override
        public void contributeToToolBar( IToolBarManager toolBarManager ) {
            toolBarManager.add(new Separator(XsdUiPlugin.Util.getString("XsdEditor.xsd-settings_13"))); //$NON-NLS-1$
            toolBarManager.add(new Separator(XsdUiPlugin.Util.getString("XsdEditor.xsd-additions_14"))); //$NON-NLS-1$
        }

        /**
         * When the active editor changes, this remembers the change, and registers with it as a selection provider.
         */
        @Override
        public void setActiveEditor( IEditorPart part ) {
            super.setActiveEditor(part);
            activeEditorPart = part;

            // switch to the new selection provider
            if (selectionProvider != null) {
                selectionProvider.removeSelectionChangedListener(this);
            }
            selectionProvider = part.getSite().getSelectionProvider();
            selectionProvider.addSelectionChangedListener(this);

            // fake a selection changed event to update the menus
            if (selectionProvider.getSelection() != null) selectionChanged(new SelectionChangedEvent(
                                                                                                     selectionProvider,
                                                                                                     selectionProvider.getSelection()));
        }

        /**
         * This implements {@link ISelectionChangedListener}, handling SelectionChangedEvents by querying for the children and
         * siblings that can be added to the selected object and updating the menus accordingly.
         */
        public void selectionChanged( SelectionChangedEvent event ) {
            // remove any menu items for old selection
            if (createChildMenuManager != null) {
                depopulateManager(createChildMenuManager, createChildActions);
            }
            if (createSiblingMenuManager != null) {
                depopulateManager(createSiblingMenuManager, createSiblingActions);
            }

            // query new selection for appropriate new child/sibling descriptors...
            Collection newChildDescriptors = Collections.EMPTY_LIST;
            Collection newSiblingDescriptors = Collections.EMPTY_LIST;
            ISelection sel = event.getSelection();

            if (sel instanceof IStructuredSelection && ((IStructuredSelection)sel).size() == 1) {
                Object object = ((IStructuredSelection)sel).getFirstElement();
                EditingDomain domain = ((IEditingDomainProvider)activeEditorPart).getEditingDomain();

                newChildDescriptors = domain.getNewChildDescriptors(object, null);
                newSiblingDescriptors = domain.getNewChildDescriptors(domain.getParent(object), object);
            }

            // generate actions for selection, populate and redraw menu
            createChildActions = generateCreateChildActions(newChildDescriptors, sel);
            createSiblingActions = generateCreateSiblingActions(newSiblingDescriptors, sel);

            if (createChildMenuManager != null) {
                populateManager(createChildMenuManager, createChildActions, null);
                createChildMenuManager.update(true);
            }
            if (createSiblingMenuManager != null) {
                populateManager(createSiblingMenuManager, createSiblingActions, null);
                createSiblingMenuManager.update(true);
            }
        }

        /**
         * This generates a {@link CreateChildAction} for each object in <code>descriptors</code>, and returns the collection of
         * these actions.
         */
        protected Collection generateCreateChildActions( Collection descriptors,
                                                         ISelection sel ) {
            Collection actions = new LinkedList();
            for (Iterator i = descriptors.iterator(); i.hasNext();) {
                actions.add(new CreateChildAction(activeEditorPart, sel, i.next()));
            }
            return actions;
        }

        /**
         * This generates a {@link CreateSiblingAction} for each object in <code>descriptors</code>, and returns the collection of
         * these actions.
         */
        protected Collection generateCreateSiblingActions( Collection descriptors,
                                                           ISelection sel ) {
            Collection actions = new LinkedList();
            for (Iterator i = descriptors.iterator(); i.hasNext();) {
                actions.add(new CreateSiblingAction(activeEditorPart, sel, i.next()));
            }
            return actions;
        }

        /**
         * This populates the specified IContributionManager with ActionContributionItems based on the IActions contained in the
         * actions collection, by inserting them before the specified contribution item ID. If ID is null, they are simply added.
         */
        protected void populateManager( IContributionManager manager,
                                        Collection actions,
                                        String ID ) {
            for (Iterator i = actions.iterator(); i.hasNext();) {
                IAction action = (IAction)i.next();
                if (ID != null) {
                    manager.insertBefore(ID, action);
                } else {
                    manager.add(action);
                }
            }
        }

        /**
         * This removes from the specified IContributionManager all ActionContributionItems based on the IActions contained in the
         * actions collection.
         */
        protected void depopulateManager( IContributionManager manager,
                                          Collection actions ) {
            IContributionItem[] item = manager.getItems();
            for (int i = 0; i < item.length; i++) {
                // look into SubContributionItems
                IContributionItem curItem = item[i];
                while (curItem instanceof SubContributionItem) {
                    curItem = ((SubContributionItem)curItem).getInnerItem();
                }

                // delete ActionContributionItems with matching action
                if (curItem instanceof ActionContributionItem) {
                    IAction action = ((ActionContributionItem)curItem).getAction();
                    if (actions.contains(action)) {
                        manager.remove(curItem);
                    }
                }
            }
        }

        /**
         * This populates the pop-up menu before it appears.
         */
        @Override
        public void menuAboutToShow( IMenuManager menuManager ) {
            super.menuAboutToShow(menuManager);

            menuManager.insertAfter(XsdUiPlugin.Util.getString("XsdEditor.additions_15"), new Separator()); //$NON-NLS-1$

            MenuManager submenuManager = new MenuManager(XsdUiPlugin.Util.getString("_UI_CreateSibling_menu_item")); //$NON-NLS-1$
            populateManager(submenuManager, createSiblingActions, null);
            menuManager.insertAfter(XsdUiPlugin.Util.getString("XsdEditor.additions_17"), submenuManager); //$NON-NLS-1$

            submenuManager = new MenuManager(XsdUiPlugin.Util.getString("_UI_CreateChild_menu_item")); //$NON-NLS-1$
            populateManager(submenuManager, createChildActions, null);
            menuManager.insertAfter(XsdUiPlugin.Util.getString("XsdEditor.additions_19"), submenuManager); //$NON-NLS-1$
        }
    }

    /**
     * This creates a context menu for the viewer and adds a listener as well registering the menu for extension.
     */
    protected void createContextMenuFor( StructuredViewer viewer ) {
        MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
        contextMenu.add(new Separator(XsdUiPlugin.Util.getString("XsdEditor.additions_21"))); //$NON-NLS-1$
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(this);
        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(contextMenu, viewer);

        int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
        Transfer[] transfers = new Transfer[] {LocalTransfer.getInstance()};
        viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
        viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer));
    }

    /**
     * This is the method used by the framework to install your own controls.
     */
    @Override
    public void createPages() {
        createSourcePage();
        createSemanticsPage();
        createSyntaxPage();

        setActivePage(0);
        setCurrentViewer((Viewer)sourceViewer);
    }

    protected void createResource( String uri ) {
        extendedCreateResource(uri);
    }

    protected void standardCreateResource( String uri ) {

        // Load the resource through the editing domain.
        // This will creat a context and associate it with the resource set.
        //
        XSDResourceImpl xsdResource = (XSDResourceImpl)editingDomain.loadResource(uri);
        xsdSchema = xsdResource.getSchema();
    }

    protected void extendedCreateResource( String uri ) {
        editingDomain.getResourceSet().getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION, Boolean.TRUE);
        try {
            XSDResourceImpl xsdResource = (XSDResourceImpl)editingDomain.getResourceSet().getResource(URI.createFileURI(uri),
                                                                                                      true);
            xsdSchema = xsdResource.getSchema();

            // code to close the editor when the resource is deleted
            textEditor.getDocumentProvider().addElementStateListener(new IElementStateListener() {
                public void elementDirtyStateChanged( Object element,
                                                      boolean isDirty ) {
                }

                public void elementContentAboutToBeReplaced( Object element ) {
                }

                public void elementContentReplaced( Object element ) {
                }

                public void elementDeleted( Object element ) {
                    Display display = getSite().getShell().getDisplay();
                    display.asyncExec(new Runnable() {
                        public void run() {
                            if (sourceViewer != null) {
                                getSite().getPage().closeEditor(XsdEditor.this, false);
                            }
                        }
                    });
                }

                public void elementMoved( Object originalElement,
                                          Object movedElement ) {
                }
            });
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    protected void createModel() {
        // Do the work within an operation because this is a long running activity that modifies the workbench.
        //
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            // This is the method that gets invoked when the operation runs.
            //
            @Override
            protected void execute( IProgressMonitor progressMonitor ) {
                try {
                    progressMonitor.beginTask(XsdUiPlugin.Util.getString("XsdEditor._22"), 10); //$NON-NLS-1$

                    IFileEditorInput modelFile = (IFileEditorInput)getEditorInput();
                    IFile file = modelFile.getFile();

                    editingDomain.getResourceSet().getLoadOptions().put(XSDResourceImpl.XSD_PROGRESS_MONITOR, progressMonitor);
                    createResource(file.getLocation().toString());
                    editingDomain.getResourceSet().getLoadOptions().remove(XSDResourceImpl.XSD_PROGRESS_MONITOR);

                    progressMonitor.worked(1);
                    final String msg = XsdUiPlugin.Util.getString("_UI_Validating_message");//$NON-NLS-1$
                    progressMonitor.subTask(msg);
                    if (getXsdSchema().getDiagnostics().isEmpty()) {
                        getXsdSchema().validate();
                        getXsdSchema().eResource().setModified(false);
                    }

                    progressMonitor.worked(1);
                    progressMonitor.subTask(XsdUiPlugin.Util.getString("_UI_ReportingErrors_message")); //$NON-NLS-1$

                    handleDiagnostics(progressMonitor);
                } catch (Throwable t) {
                    XsdUiPlugin.Util.log(t);
                } finally {
                    progressMonitor.done();
                }
            }
        };

        try {
            // This runs the operation, and shows progress.
            // (It appears to be a bad thing to fork this onto another thread.)
            //
            new ProgressMonitorDialog(getSite().getShell()).run(false, false, operation);
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    protected void handleSourceCaretPosition() {
        int offset = sourceViewer.getTextWidget().getCaretOffset();
        Element element = getXsdSchema().getElement();
        if (element != null) {
            IDocument document = sourceViewer.getDocument();
            int line = 0;
            int lineOffset = 0;
            try {
                line = document.getLineOfOffset(offset);
                lineOffset = document.getLineOffset(line);
            } catch (BadLocationException exception) {
            }
            int column = offset - lineOffset;
            // System.out.println("[" + line + "," + column + "]");

            Element bestElement = findBestElement(element, line + 1, column + 1);
            if (bestElement != null) {
                handleSelectedNodes(Collections.singleton(bestElement));
            }
        }
    }

    public Element findBestElement( Element element,
                                    int line,
                                    int column ) {
        int startLine = XSDParser.getStartLine(element);
        int startColumn = XSDParser.getStartColumn(element);
        int endLine = XSDParser.getEndLine(element);
        int endColumn = XSDParser.getEndColumn(element);

        Element candidate = null;
        if ((line == startLine ? column >= startColumn : line > startLine)
            && (line == endLine ? column <= endColumn : line < endLine)) {
            candidate = element;
            for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element)child;
                    Element betterCandidate = findBestElement(childElement, line, column);
                    if (betterCandidate != null) {
                        candidate = betterCandidate;
                        break;
                    }
                }
            }
        }
        return candidate;
    }

    public void handleSelectedNodes( Collection nodes ) {
        Collection selection = new ArrayList();
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            XSDConcreteComponent bestXSDConcreteComponent = getXsdSchema().getCorrespondingComponent(node);
            if (bestXSDConcreteComponent != null) {
                boolean add = true;
                for (XSDConcreteComponent parent = bestXSDConcreteComponent; parent != null; parent = parent.getContainer()) {
                    if (selection.contains(parent)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    XSDConcreteComponent container = bestXSDConcreteComponent.getContainer();
                    if (container instanceof XSDParticle || container instanceof XSDAttributeUse) {
                        bestXSDConcreteComponent = container;
                    }
                    selection.add(bestXSDConcreteComponent);
                }
            }
        }
        if (!selection.isEmpty()) {
            ISelection newSelection = new StructuredSelection(selection.toArray());
            if (contentOutlineViewer != null) {
                contentOutlineViewer.setSelection(newSelection, true);
            }
            setSelection(newSelection);
            handleContentOutlineSelectionForTextEditor(newSelection, false);
        }
    }

    protected void handleDocumentChange() {
        try {
            XSDParser xsdParser = new XSDParser(null);
            String documentContent = sourceViewer.getDocument().get();
            byte[] bytes =
            // domDocument.getEncoding() == null ? documentContent.getBytes() :
            // documentContent.getBytes(domDocument.getEncoding());
            documentContent.getBytes();
            xsdParser.parse(new ByteArrayInputStream(bytes));
            xsdParser.setSchema(getXsdSchema());
            getXsdSchema().validate();
            getXsdSchema().eResource().setModified(true);

            handleDiagnostics(null);
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    protected void createSourcePage() {
        try {
            // Create the SED Editor.
            //
            textEditor = new TextEditor() {
                @Override
                public ISourceViewer createSourceViewer( Composite parent,
                                                         IVerticalRuler ruler,
                                                         int styles ) {
                    final ISourceViewer result = super.createSourceViewer(parent, ruler, styles);
                    result.getTextWidget().addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseDown( MouseEvent event ) {
                            handleSourceCaretPosition();
                        }
                    });
                    result.getTextWidget().addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed( KeyEvent event ) {
                            switch (event.keyCode) {
                                case SWT.ARROW_UP:
                                case SWT.ARROW_DOWN:
                                case SWT.ARROW_LEFT:
                                case SWT.ARROW_RIGHT:
                                case SWT.PAGE_UP:
                                case SWT.PAGE_DOWN: {
                                    handleSourceCaretPosition();
                                    break;
                                }
                            }
                        }
                    });
                    sourceViewer = result;
                    return result;
                }
            };

            IFileEditorInput modelFile = (IFileEditorInput)getEditorInput();
            int pageIndex = addPage(textEditor, modelFile);

            createModel();

            String encoding = determineEncoding();
            IEncodingSupport encodingSupport = (IEncodingSupport)textEditor.getAdapter(IEncodingSupport.class);
            if (encodingSupport != null && encoding != null) {
                encodingSupport.setEncoding(encoding);
            }

            setPageText(pageIndex, XsdUiPlugin.Util.getString("XsdEditor.Source_26")); //$NON-NLS-1$

            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            document.addDocumentListener(new IDocumentListener() {
                protected Timer timer = new Timer();
                protected TimerTask timerTask;

                public void documentAboutToBeChanged( DocumentEvent documentEvent ) {
                }

                public void documentChanged( final DocumentEvent documentEvent ) {
                    try {
                        // This is need for the Properties view.
                        //
                        // setSelection(StructuredSelection.EMPTY);

                        if (timerTask != null) {
                            timerTask.cancel();
                        }

                        if (handledStructuredModelChange) {
                            handledStructuredModelChange = false;
                            handleDocumentChange();
                        } else {
                            timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    getSite().getShell().getDisplay().asyncExec(new Runnable() {
                                        public void run() {
                                            handleDocumentChange();
                                        }
                                    });
                                }
                            };

                            timer.schedule(timerTask, 1000);
                        }
                    } catch (Exception exception) {
                        XsdUiPlugin.Util.log(exception);
                    }
                }
            });
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    protected void createSemanticsPage() {
        // Create a page for the selection tree view.
        //
        {
            ViewerPane viewerPane = new ViewerPane(getSite().getPage(), XsdEditor.this) {
                @Override
                public Viewer createViewer( Composite composite ) {
                    Tree tree = new Tree(composite, SWT.MULTI);
                    TreeViewer newTreeViewer = new TreeViewer(tree);
                    return newTreeViewer;
                }

                @Override
                public void requestActivation() {
                    super.requestActivation();
                    setCurrentViewerPane(this);
                }
            };
            viewerPane.createControl(getContainer());

            semanticSelectionViewer = (TreeViewer)viewerPane.getViewer();
            semanticSelectionViewer.setContentProvider(new AdapterFactoryContentProvider(semanticAdapterFactory));
            semanticSelectionViewer.setLabelProvider(new AdapterFactoryLabelProvider(semanticAdapterFactory));
            semanticSelectionViewer.setAutoExpandLevel(2);

            semanticSelectionViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                // This just notifies those things that are affected by the section.
                //
                public void selectionChanged( SelectionChangedEvent selectionChangedEvent ) {
                    if (currentViewer == semanticSelectionViewer && contentOutlineViewer != null) {
                        contentOutlineViewer.setSelection(selectionChangedEvent.getSelection(), true);
                    }
                }
            });

            semanticSelectionViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));
            viewerPane.setTitle(getXsdSchema());

            new AdapterFactoryTreeEditor(semanticSelectionViewer.getTree(), semanticAdapterFactory);

            createContextMenuFor(semanticSelectionViewer);
            int pageIndex = addPage(viewerPane.getControl());
            setPageText(pageIndex, XsdUiPlugin.Util.getString("_UI_Semantics_title")); //$NON-NLS-1$
        }
    }

    protected void createSyntaxPage() {
        // Create a page for the selection tree view.
        //
        {
            ViewerPane viewerPane = new ViewerPane(getSite().getPage(), XsdEditor.this) {
                @Override
                public Viewer createViewer( Composite composite ) {
                    Tree tree = new Tree(composite, SWT.MULTI);
                    TreeViewer newTreeViewer = new TreeViewer(tree);
                    return newTreeViewer;
                }

                @Override
                public void requestActivation() {
                    super.requestActivation();
                    setCurrentViewerPane(this);
                }
            };
            viewerPane.createControl(getContainer());

            syntacticSelectionViewer = (TreeViewer)viewerPane.getViewer();
            syntacticSelectionViewer.setContentProvider(new AdapterFactoryContentProvider(syntacticAdapterFactory));
            syntacticSelectionViewer.setLabelProvider(new AdapterFactoryLabelProvider(syntacticAdapterFactory));
            syntacticSelectionViewer.setAutoExpandLevel(2);

            syntacticSelectionViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));
            viewerPane.setTitle(getXsdSchema());

            new AdapterFactoryTreeEditor(syntacticSelectionViewer.getTree(), syntacticAdapterFactory);

            createContextMenuFor(syntacticSelectionViewer);
            int pageIndex = addPage(viewerPane.getControl());
            setPageText(pageIndex, XsdUiPlugin.Util.getString("_UI_Syntax_title")); //$NON-NLS-1$
        }
    }

    protected void initializeMarkerPosition( IMarker marker,
                                             XSDDiagnostic xsdDiagnostic ) throws CoreException {
        Node node = xsdDiagnostic.getNode();
        if (node != null && node.getNodeType() == Node.ATTRIBUTE_NODE) {
            node = ((Attr)node).getOwnerElement();
        }
        if (node != null && XSDParser.getUserData(node) != null) {
            int startLine = XSDParser.getStartLine(node) - 1;
            int startColumn = XSDParser.getStartColumn(node);
            int endLine = XSDParser.getEndLine(node) - 1;
            int endColumn = XSDParser.getEndColumn(node);

            marker.setAttribute(IMarker.LINE_NUMBER, startLine);

            try {
                IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                marker.setAttribute(IMarker.CHAR_START, document.getLineOffset(startLine) + startColumn - 1);
                marker.setAttribute(IMarker.CHAR_END, document.getLineOffset(endLine) + endColumn - 1);
            } catch (BadLocationException exception) {
            }
        } else {
            marker.setAttribute(IMarker.LINE_NUMBER, xsdDiagnostic.getLine());
        }
    }

    protected void handleDiagnostics( IProgressMonitor progressMonitor ) {
        if (progressMonitor == null) {
            // Do the work within an operation because this is a long running activity that modifies the workbench.
            //
            IWorkspaceRunnable operation = new IWorkspaceRunnable() {
                // This is the method that gets invoked when the operation runs.
                //
                public void run( IProgressMonitor localProgressMonitor ) {
                    handleDiagnostics(localProgressMonitor);
                }
            };

            try {
                ResourcesPlugin.getWorkspace().run(operation, new NullProgressMonitor());
                // getSite().getWorkbenchWindow().run(false, false, operation);
            } catch (Exception exception) {
                XsdUiPlugin.Util.log(exception);
            }
        } else {
            XSDConcreteComponent newSelection = null;
            try {
                // I assume that the input is a file object.
                //
                IFileEditorInput modelFile = (IFileEditorInput)getEditorInput();
                IFile file = modelFile.getFile();

                IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
                Collection deletableMarkers = new ArrayList(Arrays.asList(markers));

                for (Iterator xsdDiagnostics = getXsdSchema().getAllDiagnostics().iterator(); xsdDiagnostics.hasNext();) {
                    XSDDiagnostic xsdDiagnostic = (XSDDiagnostic)xsdDiagnostics.next();
                    String uriReferencePath = getXsdSchema().eResource().getURIFragment(xsdDiagnostic);

                    IMarker marker = null;
                    for (int i = 0; i < markers.length; ++i) {
                        if (markers[i].getAttribute(XSDDiagnostic.URI_FRAGMENT_ATTRIBUTE,
                                                    XsdUiPlugin.Util.getString("XsdEditor._29")).equals(uriReferencePath)) //$NON-NLS-1$
                        {
                            marker = markers[i];
                            deletableMarkers.remove(marker);
                            break;
                        }
                    }

                    if (marker == null) {
                        marker = file.createMarker(XSDDiagnostic.MARKER);
                        marker.setAttribute(XSDDiagnostic.URI_FRAGMENT_ATTRIBUTE, uriReferencePath);
                    }

                    initializeMarkerPosition(marker, xsdDiagnostic);

                    marker.setAttribute(IMarker.MESSAGE, xsdDiagnostic.getMessage());

                    switch (xsdDiagnostic.getSeverity().getValue()) {
                        case XSDDiagnosticSeverity.FATAL:
                        case XSDDiagnosticSeverity.ERROR: {
                            if (newSelection == null) {
                                newSelection = xsdDiagnostic.getPrimaryComponent();
                            }
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                            break;
                        }
                        case XSDDiagnosticSeverity.WARNING: {
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                            break;
                        }
                        case XSDDiagnosticSeverity.INFORMATION: {
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                            break;
                        }
                    }
                }

                for (Iterator i = deletableMarkers.iterator(); i.hasNext();) {
                    IMarker marker = (IMarker)i.next();
                    marker.delete();
                }
            } catch (Exception exception) {
                XsdUiPlugin.Util.log(exception);
            }

            // This will refresh the status.
            //
            if (editorSelection != null) {
                setSelection(editorSelection);
            }
            // This is the startup case.
            //
            else if (newSelection != null) {
                final IStructuredSelection errorSelection = new StructuredSelection(newSelection);
                getSite().getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        if (contentOutlineViewer != null) {
                            contentOutlineViewer.setSelection(errorSelection, true);
                        }
                        if (sourceViewer != null) {
                            handleContentOutlineSelectionForTextEditor(errorSelection, true);
                        }
                        setSelection(errorSelection);
                        handleSourceCaretPosition();
                    }
                });
            }
        }
    }

    /**
     * This is used to track the active viewer.
     */
    @Override
    protected void pageChange( int pageIndex ) {
        super.pageChange(pageIndex);

        if (pageIndex == 0) {
            setCurrentViewerPane(null);
            setCurrentViewer((Viewer)sourceViewer);
        }

        // This is a temporary workaround... EATM
        //
        Control control = getControl(pageIndex);
        if (control != null) {
            control.setVisible(true);
            control.setFocus();
        }

        handleContentOutlineSelection(getContentOutlinePage().getSelection());
    }

    /**
     * This is how the framework determines which interfaces we implement.
     */
    @Override
    public Object getAdapter( Class key ) {
        if (key.equals(IContentOutlinePage.class)) {
            return getContentOutlinePage();
        } else if (key.equals(IPropertySheetPage.class)) {
            return getPropertySheetPage();
        } else {
            return textEditor.getAdapter(key);
        }
    }

    /**
     * This is a utility function to resolve a component.
     */
    public static XSDConcreteComponent getResolvedObject( XSDConcreteComponent xsdConcreteComponent ) {
        XSDConcreteComponent result = (XSDConcreteComponent)new XSDSwitch() {
            @Override
            public Object caseXSDAttributeUse( XSDAttributeUse xsdAttributeUse ) {
                return xsdAttributeUse.getAttributeDeclaration().getResolvedAttributeDeclaration();
            }

            @Override
            public Object caseXSDAttributeDeclaration( XSDAttributeDeclaration xsdAttributeDeclaration ) {
                return xsdAttributeDeclaration.getResolvedAttributeDeclaration();
            }

            @Override
            public Object caseXSDAttributeGroupDefinition( XSDAttributeGroupDefinition xsdAttributeGroupDefinition ) {
                return xsdAttributeGroupDefinition.getResolvedAttributeGroupDefinition();
            }

            @Override
            public Object caseXSDElementDeclaration( XSDElementDeclaration xsdElementDeclaration ) {
                return xsdElementDeclaration.getResolvedElementDeclaration();
            }

            @Override
            public Object caseXSDModelGroupDefinition( XSDModelGroupDefinition xsdModelGroupDefinition ) {
                return xsdModelGroupDefinition.getResolvedModelGroupDefinition();
            }

            @Override
            public Object caseXSDParticle( XSDParticle xsdParticle ) {
                Object resolvedObject = getResolvedObject(xsdParticle.getContent());
                if (resolvedObject instanceof XSDModelGroup) {
                    return xsdParticle;
                }
                return resolvedObject;
            }
        }.doSwitch(xsdConcreteComponent);

        return result == null ? xsdConcreteComponent : result;
    }

    /**
     * @param set
     */
    public static void setResourceSet( ResourceSet rs ) {
        resourceSet = rs;
    }

    /**
     * This accesses a cached version of the content outliner.
     */
    public IContentOutlinePage getContentOutlinePage() {
        if (contentOutlinePage == null) {
            // The content outline is just a tree.
            //
            class MyContentOutlinePage extends ContentOutlinePage {
                @Override
                public void createControl( Composite parent ) {
                    super.createControl(parent);
                    contentOutlineViewer = getTreeViewer();
                    contentOutlineViewer.addSelectionChangedListener(this);
                    contentOutlineViewer.setAutoExpandLevel(2);

                    selectNextDiagnosticsAction = new SelectDiagnosticAction(true, contentOutlineViewer);
                    selectPreviousDiagnosticsAction = new SelectDiagnosticAction(false, contentOutlineViewer);

                    selectNextUseAction = new SelectUseAction(true, contentOutlineViewer);
                    selectPreviousUseAction = new SelectUseAction(false, contentOutlineViewer);

                    contentOutlineViewer.getTree().addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseDoubleClick( MouseEvent event ) {
                            // Do fancy navigation selections when double clicking.
                            //
                            IStructuredSelection selection = (IStructuredSelection)contentOutlineViewer.getSelection();
                            for (Iterator objects = selection.toList().iterator(); objects.hasNext();) {
                                XSDConcreteComponent object = (XSDConcreteComponent)objects.next();
                                Object resolvedObject = getResolvedObject(object);
                                if (object != resolvedObject) {
                                    contentOutlineViewer.setSelection(new StructuredSelection(new Object[] {resolvedObject}),
                                                                      true);
                                    break;
                                } else if (object instanceof XSDAttributeDeclaration) {
                                    XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration)object;
                                    XSDSimpleTypeDefinition typeDefinition = xsdAttributeDeclaration.getTypeDefinition();
                                    if (typeDefinition != null
                                        && typeDefinition.getSchema() == xsdAttributeDeclaration.getSchema()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(new Object[] {typeDefinition}),
                                                                          true);
                                        break;
                                    }
                                } else if (object instanceof XSDElementDeclaration) {
                                    XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)object;
                                    XSDTypeDefinition typeDefinition = xsdElementDeclaration.getTypeDefinition();
                                    if (typeDefinition != null && typeDefinition.getSchema() == xsdElementDeclaration.getSchema()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(new Object[] {typeDefinition}),
                                                                          true);
                                        break;
                                    }
                                } else if (object instanceof XSDSimpleTypeDefinition) {
                                    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition)object;
                                    XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition.getBaseTypeDefinition();
                                    if (baseTypeDefinition != null
                                        && baseTypeDefinition.getSchema() == xsdSimpleTypeDefinition.getSchema()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(
                                                                                                  new Object[] {baseTypeDefinition}),
                                                                          true);
                                        break;
                                    }
                                    XSDSimpleTypeDefinition itemTypeDefinition = xsdSimpleTypeDefinition.getItemTypeDefinition();
                                    if (itemTypeDefinition != null
                                        && itemTypeDefinition.getSchema() == xsdSimpleTypeDefinition.getSchema()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(
                                                                                                  new Object[] {itemTypeDefinition}),
                                                                          true);
                                        break;
                                    }
                                    List memberTypeDefinitions = xsdSimpleTypeDefinition.getMemberTypeDefinitions();
                                    if (!memberTypeDefinitions.isEmpty()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(memberTypeDefinitions.toArray()),
                                                                          true);
                                        break;
                                    }
                                } else if (object instanceof XSDComplexTypeDefinition) {
                                    XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition)object;
                                    XSDTypeDefinition baseTypeDefinition = xsdComplexTypeDefinition.getBaseTypeDefinition();
                                    if (baseTypeDefinition != null
                                        && baseTypeDefinition.getSchema() == xsdComplexTypeDefinition.getSchema()) {
                                        contentOutlineViewer.setSelection(new StructuredSelection(
                                                                                                  new Object[] {baseTypeDefinition}),
                                                                          true);
                                        break;
                                    }
                                }
                            }
                        }
                    });

                    // Set up the tree viewer.
                    //
                    contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(syntacticAdapterFactory));
                    contentOutlineViewer.setLabelProvider(new AdapterFactoryLabelProvider(syntacticAdapterFactory));
                    contentOutlineViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));

                    // Make sure our popups work.
                    //
                    createContextMenuFor(contentOutlineViewer);

                    // Select the root object in the view.
                    //
                    ArrayList selection = new ArrayList();
                    selection.add(getXsdSchema());
                    contentOutlineViewer.setSelection(new StructuredSelection(selection), true);

                    // Listen to selection so that we can handle it is a special way.
                    //
                    this.addSelectionChangedListener(new ISelectionChangedListener() {
                        // This ensures that we handle selections correctly.
                        //
                        public void selectionChanged( SelectionChangedEvent event ) {
                            ISelection s = event.getSelection();
                            if (contentOutlineViewer == currentViewer) {
                                handleContentOutlineSelection(s);
                            }
                            selectNextDiagnosticsAction.setCurrentObjects(((IStructuredSelection)s).toList());
                            selectPreviousDiagnosticsAction.setCurrentObjects(((IStructuredSelection)s).toList());

                            selectNextUseAction.setCurrentObjects(((IStructuredSelection)s).toList());
                            selectPreviousUseAction.setCurrentObjects(((IStructuredSelection)s).toList());
                        }
                    });
                }

                @Override
                public void setActionBars( IActionBars actionBars ) {
                    super.setActionBars(actionBars);

                    contentOutlineStatusLineManager = actionBars.getStatusLineManager();

                    actionBars.getToolBarManager().add(selectNextUseAction);
                    actionBars.getToolBarManager().add(selectPreviousUseAction);

                    actionBars.getToolBarManager().add(selectNextDiagnosticsAction);
                    actionBars.getToolBarManager().add(selectPreviousDiagnosticsAction);

                    actionBars.getMenuManager().add(selectNextDiagnosticsAction);
                    actionBars.getMenuManager().add(selectPreviousDiagnosticsAction);

                    actionBars.getMenuManager().add(selectNextUseAction);
                    actionBars.getMenuManager().add(selectPreviousUseAction);

                    getActionBarContributor().shareGlobalActions(this, actionBars);
                }
            }

            contentOutlinePage = new MyContentOutlinePage();

            // Listen to selection so that we can handle it is a special way.
            //
            contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
                // This ensures that we handle selections correctly.
                //
                public void selectionChanged( SelectionChangedEvent event ) {
                    if (contentOutlineViewer == currentViewer) {
                        handleContentOutlineSelection(event.getSelection());
                    }
                }
            });
        }

        return contentOutlinePage;
    }

    /**
     * This accesses a cached version of the property sheet.
     */
    public IPropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            propertySheetPage = new PropertySheetPage() {
                @Override
                public void makeContributions( IMenuManager menuManager,
                                               IToolBarManager toolBarManager,
                                               IStatusLineManager statusLineManager ) {
                    super.makeContributions(menuManager, toolBarManager, statusLineManager);
                }
            };
            propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(syntacticAdapterFactory) {
                @Override
                public void notifyChanged( final Notification notification ) {
                    getParentComposite().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            propertySheetPage.refresh();
                        }
                    });
                }

                @Override
                protected IPropertySource createPropertySource( Object object,
                                                                IItemPropertySource itemPropertySource ) {
                    return new PropertySource(object, itemPropertySource) {
                        @Override
                        protected IPropertyDescriptor createPropertyDescriptor( IItemPropertyDescriptor itemPropertyDescriptor ) {
                            return new PropertyDescriptor(this.object, itemPropertyDescriptor) {
                                @Override
                                public CellEditor createPropertyEditor( Composite composite ) {
                                    if (!this.itemPropertyDescriptor.canSetProperty(this.object)) {
                                        return null;
                                    }

                                    CellEditor result = null;

                                    Object genericFeature = this.itemPropertyDescriptor.getFeature(this.object);
                                    if (genericFeature instanceof EStructuralFeature) {
                                        EStructuralFeature feature = (EStructuralFeature)genericFeature;
                                        EObject getEType = feature.getEType();
                                        if (getEType == ecorePackage.getEBoolean()) {
                                            Collection choiceOfValues = this.itemPropertyDescriptor.getChoiceOfValues(this.object);
                                            if (choiceOfValues != null) {
                                                result = new ExtendedComboBoxCellEditor(composite, new ArrayList(choiceOfValues),
                                                                                        getLabelProvider(), true);
                                            }
                                        }
                                    }
                                    if (result == null) {
                                        result = super.createPropertyEditor(composite);
                                    }
                                    return result;
                                }
                            };
                        }
                    };
                }
            });
        }

        return propertySheetPage;
    }

    /**
     * This deals with how we want selection in the outliner to affect the other views.
     */
    public void handleContentOutlineSelection( ISelection selection ) {
        if ((currentViewerPane != null || getActivePage() == 0) && !selection.isEmpty()
            && selection instanceof IStructuredSelection) {
            if (getActivePage() == 0) {
                handleContentOutlineSelectionForTextEditor(selection, true);
            } else if (currentViewerPane.getViewer() == syntacticSelectionViewer) {
                // Set the selection to the widget.
                //
                syntacticSelectionViewer.setSelection(selection);
            } else if (currentViewerPane.getViewer() == semanticSelectionViewer) {
                ArrayList selectionList = new ArrayList();
                for (Iterator elements = ((IStructuredSelection)selection).iterator(); elements.hasNext();) {
                    selectionList.add(getResolvedObject((XSDConcreteComponent)elements.next()));
                }

                // Set the selection to the widget.
                //
                semanticSelectionViewer.setSelection(new StructuredSelection(selectionList));
            }
        }
    }

    /**
     * This deals with how we want selection in the outliner to affect the text editor.
     */
    public void handleContentOutlineSelectionForTextEditor( ISelection selection,
                                                            boolean reveal ) {
        XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)((IStructuredSelection)selection).iterator().next();
        if (xsdConcreteComponent instanceof XSDParticle) {
            XSDParticle xsdParticle = (XSDParticle)xsdConcreteComponent;
            XSDConcreteComponent content = xsdParticle.getContent();
            if (content != null) {
                xsdConcreteComponent = content;
            }
        }

        Element element = xsdConcreteComponent.getElement();
        if (element != null) {
            try {
                IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                int startLine = XSDParser.getStartLine(element);
                int startColumn = XSDParser.getStartColumn(element);
                int endLine = XSDParser.getEndLine(element);
                int endColumn = XSDParser.getEndColumn(element);

                int startOffset = document.getLineOffset(startLine - 1);
                startOffset += startColumn - 1;
                int endOffset = document.getLineOffset(endLine - 1);
                endOffset += endColumn - 1;
                if (startLine == endLine) {
                    textEditor.setHighlightRange(startOffset, endOffset - startOffset, false);
                    if (reveal) {
                        textEditor.selectAndReveal(startOffset, endOffset - startOffset);
                    }
                } else {
                    textEditor.setHighlightRange(startOffset, endOffset - startOffset, reveal);
                }
            } catch (Exception exception) {
                XsdUiPlugin.Util.log(exception);
            }
        }
    }

    /**
     * This is for implementing {@link IEditorPart} and simply tests the command stack.
     */
    @Override
    public boolean isDirty() {
        return ((BasicCommandStack)editingDomain.getCommandStack()).isSaveNeeded() || textEditor != null && textEditor.isDirty();
    }

    /**
     * @return
     */
    public XSDSchema getXsdSchema() {
        if (xsdSchema != null && xsdSchema.eResource() != null) {
            return xsdSchema;
        }

        if (xsdResource != null && xsdResource instanceof XSDResourceImpl) {
            XSDResourceImpl rsrc = (XSDResourceImpl)xsdResource;
            if (rsrc != null) {
                xsdSchema = rsrc.getSchema();
            }
        }

        return xsdSchema;
    }

    /**
     * This is for implementing {@link IEditorPart} and simply saves the model file.
     */
    @Override
    public void doSave( IProgressMonitor progressMonitor ) {
        final IEditorInput input = getEditorInput();
        final IDocumentProvider documentProvider = textEditor.getDocumentProvider();

        // Do the work within an operation because this is a long running activity that modifies the workbench.
        //
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            // This is the method that gets invoked when the operation runs.
            @Override
            protected void execute( IProgressMonitor monitor ) {
                try {
                    // Save the resource to the file system.
                    documentProvider.saveDocument(monitor, input, documentProvider.getDocument(input), true);
                } catch (Exception exception) {
                    XsdUiPlugin.Util.log(exception);
                }
            }
        };

        try {
            documentProvider.aboutToChange(input);
            operation.run(progressMonitor); // performs the operation
            documentProvider.changed(input);
            ((BasicCommandStack)editingDomain.getCommandStack()).saveIsDone();
            firePropertyChange(IEditorPart.PROP_DIRTY);
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    /**
     * This always returns false because it is not current supported.
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * This also changes the editor's input.
     */
    @Override
    public void doSaveAs() {
        SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
        saveAsDialog.open();
        IPath path = saveAsDialog.getResult();
        if (path != null) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            if (file != null) {
                if (!file.exists()
                    || MessageDialog.openQuestion(getSite().getShell(), XsdUiPlugin.Util.getString("_UI_FileExists_title"), //$NON-NLS-1$
                                                  XsdUiPlugin.Util.getString("_UI_FileExists_description", new Object[] {file.getFullPath()}))) //$NON-NLS-1$
                {
                    getXsdSchema().eResource().setURI(URI.createURI(XsdUiPlugin.Util.getString("XsdEditor.platform_/resource_32") + file.getFullPath())); //$NON-NLS-1$
                    IFileEditorInput modelFile = new FileEditorInput(file);
                    setInput(modelFile);
                    setPartName(file.getName());
                    doSave(getActionBars().getStatusLineManager().getProgressMonitor());
                }
            }
        }
    }

    public void gotoMarker( IMarker marker ) {
        try {
            /*
                  if (marker.getType().equals(XSDDiagnostic.MARKER) && xsdSchema != null)
                  {
                    XSDDiagnostic xsdDiagnostic =
                      xsdSchema.getDiagnosticForURIReferencePath(marker.getAttribute(XSDDiagnostic.URI_FRAGMENT_ATTRIBUTE, "/0/"));
                  }
            */
            setActivePage(0);
            ((IGotoMarker)textEditor.getAdapter(IGotoMarker.class)).gotoMarker(marker);
        } catch (Exception exception) {
            XsdUiPlugin.Util.log(exception);
        }
    }

    /**
     * This is called during startup.
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput editorInput ) throws PartInitException {
        if (editorInput instanceof IFileEditorInput) {
            // get the XSD resource.
            try {
                // Get the IFile associated with the editor
                final IResource xsdFile = ((IFileEditorInput)editorInput).getFile();
                if (xsdFile == null) {
                    final String msg = XsdUiPlugin.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", editorInput.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }

                // Get the EMF resource for the IFile in the workspace
                final String xsdLocation = xsdFile.getLocation().toString();
                final URI xsdUri = URI.createFileURI(xsdLocation);
                xsdResource = ModelerCore.getModelContainer().getResource(xsdUri, true);

                // The resource must exist in the container
                if (xsdResource == null) {
                    final String msg = XsdUiPlugin.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_1", editorInput.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }
                // modelProject =
                xsdFile.getProject();
            } catch (ModelWorkspaceException e) {
                final String msg = XsdUiPlugin.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", editorInput.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            } catch (CoreException e) {
                final String msg = XsdUiPlugin.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", editorInput.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            }

            setSite(site);
            setInput(editorInput);
            setPartName(((IFileEditorInput)editorInput).getFile().getName());
            site.setSelectionProvider(new MultiPageSelectionProvider(this)); // EATM
            site.getPage().addPartListener(partListener);
        } else {
            throw new PartInitException(
                                        XsdUiPlugin.Util.getString("XsdEditor.Invalid_Input__Must_be_IFileEditorInput._33")); //$NON-NLS-1$
        }
    }

    @Override
    public void setFocus() {
        getControl(getActivePage()).setFocus();
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.add(listener);
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.remove(listener);
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
     */
    public ISelection getSelection() {
        return editorSelection;
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection. Calling this
     * result will notify the listeners.
     */
    public void setSelection( ISelection selection ) {
        editorSelection = selection;

        SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, selection);
        ((MultiPageSelectionProvider)getSite().getSelectionProvider()).fireSelectionChanged(selectionChangedEvent);
        for (Iterator listeners = selectionChangedListeners.iterator(); listeners.hasNext();) {
            ISelectionChangedListener listener = (ISelectionChangedListener)listeners.next();
            listener.selectionChanged(selectionChangedEvent);
        }

        setStatusLineManager(selection);
    }

    /**
     * This shows the selection on the status line.
     */
    public void setStatusLineManager( ISelection selection ) {
        IStatusLineManager statusLineManager = getActionBars().getStatusLineManager();
        if (currentViewer == contentOutlineViewer) {
            statusLineManager = contentOutlineStatusLineManager;
        }

        if (statusLineManager != null) {
            if (selection instanceof IStructuredSelection) {
                Collection collection = ((IStructuredSelection)selection).toList();
                switch (collection.size()) {
                    case 0: {
                        statusLineManager.setMessage(XsdUiPlugin.Util.getString("_UI_NoObjectSelected")); //$NON-NLS-1$
                        break;
                    }
                    case 1: {
                        Object object = collection.iterator().next();
                        String text = new AdapterFactoryItemDelegator(syntacticAdapterFactory).getText(object);
                        text = XsdUiPlugin.Util.getString("_UI_SingleObjectSelected", new Object[] {text}); //$NON-NLS-1$
                        if (object instanceof XSDConcreteComponent) {
                            XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)object;
                            if (!xsdConcreteComponent.getDiagnostics().isEmpty()) {
                                text = (xsdConcreteComponent.getDiagnostics().get(0)).getMessage();
                            }
                        }

                        statusLineManager.setMessage(text);
                        break;
                    }
                    default: {
                        statusLineManager.setMessage(XsdUiPlugin.Util.getString("_UI_MultiObjectSelected", new Object[] {Integer.toString(collection.size())})); //$NON-NLS-1$
                        break;
                    }
                }
            } else {
                statusLineManager.setMessage(""); //$NON-NLS-1$
            }
        }
    }

    /**
     * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions from the
     * Edit menu.
     */
    public void menuAboutToShow( IMenuManager menuManager ) {
        ((IMenuListener)getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
    }

    /**
     * This convenience method provides typed access to the contributor.
     */
    public EditingDomainActionBarContributor getActionBarContributor() {
        return (EditingDomainActionBarContributor)getEditorSite().getActionBarContributor();
    }

    /**
     * This convenience method provides access to the actionbars.
     */
    public IActionBars getActionBars() {
        return getActionBarContributor().getActionBars();
    }

    /**
     * This is called when the editor is disposed.
     */
    @Override
    public void dispose() {
        super.dispose();
        getSite().getPage().removePartListener(partListener);
        ((IDisposable)semanticAdapterFactory).dispose();
        ((IDisposable)syntacticAdapterFactory).dispose();
    }

    /**
     * This is the base action for the outline actions.
     */
    class SelectObjectAction extends Action {
        protected Collection objectsToSelect;
        protected StructuredViewer structuredViewer;

        public SelectObjectAction( StructuredViewer structuredViewer,
                                   String text,
                                   ImageDescriptor imageDescriptor ) {
            super(text, imageDescriptor);
            this.structuredViewer = structuredViewer;
            setEnabled(false);
        }

        public void setObjectToSelect( Object objectToSelect ) {
            setObjectsToSelect(objectToSelect != null ? Collections.singleton(objectToSelect) : (Collection)Collections.EMPTY_LIST);
        }

        public void setObjectsToSelect( Collection objectsToSelect ) {
            this.objectsToSelect = new ArrayList(objectsToSelect);
            setEnabled(!objectsToSelect.isEmpty());
        }

        @Override
        public void run() {
            ISelection selection = null;

            if ((objectsToSelect == null) || objectsToSelect.isEmpty()) {
                selection = StructuredSelection.EMPTY;
            } else {
                selection = new StructuredSelection(objectsToSelect.toArray());
            }
            structuredViewer.setSelection(selection, true);
        }
    }

    /**
     * This is used to implement the select next/previous unresolved component action.
     */
    class SelectDiagnosticAction extends SelectObjectAction {
        boolean isForward;

        public SelectDiagnosticAction( boolean isForward,
                                       StructuredViewer structuredViewer ) {
            super(
                  structuredViewer,
                  isForward ? XsdUiPlugin.Util.getString("XsdEditor.Select_&Next_Diagnosed_Object_38") : XsdUiPlugin.Util.getString("XsdEditor.Select_&Previous_Diagnosed_Object_39"), //$NON-NLS-1$ //$NON-NLS-2$
                  ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/elcl16/SelectNextDiagnosticObject.gif" : "icons/full/elcl16/SelectPreviousDiagnosticObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$
            this.isForward = isForward;

            setHoverImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/clcl16/SelectNextDiagnosticObject.gif" : "icons/full/clcl16/SelectPreviousDiagnosticObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$

            setDisabledImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/dlcl16/SelectNextDiagnosticObject.gif" : "icons/full/dlcl16/SelectPreviousDiagnosticObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$

            setToolTipText(isForward ? XsdUiPlugin.Util.getString("XsdEditor.Select_&Next_Diagnosed_Object_38") //$NON-NLS-1$
            : XsdUiPlugin.Util.getString("XsdEditor.Select_&Previous_Diagnosed_Object_39")); //$NON-NLS-1$
        }

        public void updateAction() {
            setCurrentObjects(((IStructuredSelection)structuredViewer.getSelection()).toList());
        }

        public void setCurrentObjects( List objects ) {
            XSDConcreteComponent result = null;

            boolean isStarted = false;
            for (TreeIterator tree = editingDomain.treeIterator(getXsdSchema()); tree.hasNext();) {
                XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)tree.next();
                if (!isForward && objects.contains(xsdConcreteComponent)) {
                    break;
                } else if (isStarted || !isForward) {
                    if (!xsdConcreteComponent.getDiagnostics().isEmpty() || xsdConcreteComponent instanceof XSDParticle
                        && !((XSDParticle)xsdConcreteComponent).getContent().getDiagnostics().isEmpty()
                        || xsdConcreteComponent instanceof XSDAttributeUse
                        && !((XSDAttributeUse)xsdConcreteComponent).getContent().getDiagnostics().isEmpty()) {
                        if (isStarted) {
                            result = xsdConcreteComponent;
                            break;
                        }
                        result = xsdConcreteComponent;
                    }
                } else if (objects.contains(xsdConcreteComponent)) {
                    isStarted = true;
                }
            }

            setObjectToSelect(result);
        }
    }

    /**
     * This is used to implement the select next/previous component use action.
     */
    class SelectUseAction extends SelectObjectAction {
        boolean isForward;

        public SelectUseAction( boolean isForward,
                                StructuredViewer structuredViewer ) {
            super(
                  structuredViewer,
                  isForward ? XsdUiPlugin.Util.getString("XsdEditor.Select_&Next_Use_46") : XsdUiPlugin.Util.getString("XsdEditor.Select_&Previous_Use_47"), //$NON-NLS-1$ //$NON-NLS-2$
                  ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/elcl16/SelectNextUseObject.gif" : "icons/full/elcl16/SelectPreviousUseObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$
            this.isForward = isForward;

            setHoverImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/clcl16/SelectNextUseObject.gif" : "icons/full/clcl16/SelectPreviousUseObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$
            setDisabledImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(XsdUiPlugin.INSTANCE.getImage(isForward ? "icons/full/dlcl16/SelectNextUseObject.gif" : "icons/full/dlcl16/SelectPreviousUseObject.gif"))); //$NON-NLS-1$ //$NON-NLS-2$

            setToolTipText(isForward ? XsdUiPlugin.Util.getString("XsdEditor.Select_&Next_Use_46") //$NON-NLS-1$
            : XsdUiPlugin.Util.getString("XsdEditor.Select_&Previous_Use_47")); //$NON-NLS-1$
        }

        public void updateAction() {
            setCurrentObjects(((IStructuredSelection)structuredViewer.getSelection()).toList());
        }

        public void setCurrentObjects( List objects ) {
            XSDConcreteComponent result = null;

            final List resolvedObjects = new ArrayList();
            for (Iterator i = objects.iterator(); i.hasNext();) {
                XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)i.next();
                XSDConcreteComponent resolvedObject = getResolvedObject(xsdConcreteComponent);
                if (! this.equals(resolvedObject)) {
                    resolvedObjects.add(resolvedObject);
                }
            }

            boolean isStarted = false;
            for (TreeIterator tree = editingDomain.treeIterator(getXsdSchema()); tree.hasNext();) {
                XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)tree.next();
                if (!isForward && objects.contains(xsdConcreteComponent)) {
                    break;
                } else if (isStarted || !isForward) {
                    XSDConcreteComponent resolvedObject = getResolvedObject(xsdConcreteComponent);
                    if (resolvedObjects.contains(resolvedObject)) {
                        if (isStarted) {
                            result = xsdConcreteComponent;
                            break;
                        }
                        result = xsdConcreteComponent;
                    }
                } else if (objects.contains(xsdConcreteComponent)) {
                    isStarted = true;
                }
            }

            setObjectToSelect(result);
        }
    }

    public static class GenericXMLResourceFactoryImpl extends XMLResourceFactoryImpl {
        protected XMLResource.XMLMap xmlMap = new XMLMapImpl();

        public GenericXMLResourceFactoryImpl() {
            super();
        }

        @Override
        public Resource createResource( URI uri ) {
            XMLResource result = new GenericXMLResourceImpl(uri);
            result.getDefaultSaveOptions().put(XMLResource.OPTION_XML_MAP, xmlMap);
            result.getDefaultLoadOptions().put(XMLResource.OPTION_XML_MAP, xmlMap);
            return result;
        }
    }

    public static class GenericXMLResourceImpl extends XMLResourceImpl {
        protected XSDEcoreBuilder xsdEcoreBuilder;
        protected XMLHelper xmlHelper;

        public GenericXMLResourceImpl( URI uri ) {
            super(uri);
        }

        @Override
        protected XMLHelper createXMLHelper() {
            if (xmlHelper == null) {
                xmlHelper = new XMLHelperImpl(this) {
                    @Override
                    protected String getQName( String uri,
                                               String name ) {
                        if (uri == null) {
                            return name;
                        }

                        EPackage pkg = xsdEcoreBuilder.getTargetNamespaceToEPackageMap().get(uri);
                        if (pkg == null || pkg.getNsPrefix().equals("")) //$NON-NLS-1$
                        {
                            return name;
                        }
                        packages.put(pkg, null);
                        return pkg.getNsPrefix() + ":" + name; //$NON-NLS-1$
                    }
                };
            }
            return xmlHelper;
        }

        @Override
        public void doLoad( InputStream inputStream,
                            final Map op ) throws IOException {
            XMLLoad xmlStart = new XMLLoadImpl(createXMLHelper()) {
                @Override
                protected DefaultHandler makeDefaultHandler() {
                    final XMLResource resrc = resource;
                    SAXXMLHandler saxXMLHandler = new SAXXMLHandler(resrc, helper, op) {
                        protected MyStack elementDeclarations = new MyStack();
                        protected MyStack dfaStates = new MyStack();

                        @Override
                        protected void createTopObject( String prefix,
                                                        String name ) {
                            String namespaceURI = helper.getURI(prefix);
                            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                                String attributeName = attribs.getQName(i);
                                int index = attributeName.indexOf(":"); //$NON-NLS-1$
                                String attributeNamespaceURI = null;
                                String attributeLocalName = attributeName;
                                if (index != -1) {
                                    attributeNamespaceURI = helper.getURI(attributeName.substring(0, index));
                                    attributeLocalName = attributeName.substring(index + 1);
                                }

                                if (XSDConstants.SCHEMA_INSTANCE_URI_2001.equals(attributeNamespaceURI)
                                    && (namespaceURI == null ? XsdUiPlugin.Util.getString("XsdEditor.noSchemaLocation_57") : XsdUiPlugin.Util.getString("XsdEditor.schemaLocation_58")).equals(attributeLocalName)) //$NON-NLS-1$ //$NON-NLS-2$
                                {
                                    String schemaLocationHint = null;

                                    if (namespaceURI == null) {
                                        schemaLocationHint = attribs.getValue(i);
                                    } else {
                                        for (StringTokenizer stringTokenizer = new StringTokenizer(attribs.getValue(i)); stringTokenizer.hasMoreTokens();) {
                                            String namespaceURIHint = stringTokenizer.nextToken();
                                            if (stringTokenizer.hasMoreTokens()) {
                                                if (namespaceURIHint.equals(namespaceURI)) {
                                                    schemaLocationHint = stringTokenizer.nextToken();
                                                    break;
                                                }
                                                stringTokenizer.nextToken();
                                            } else {
                                                break;
                                            }
                                        }
                                    }

                                    if (schemaLocationHint != null) {
                                        URI uri = URI.createURI(schemaLocationHint);
                                        if (resolve && uri.isRelative() && uri.hasRelativePath()) {
                                            uri = uri.resolve(resourceURI);
                                        }

                                        xsdEcoreBuilder = new XSDEcoreBuilder();
                                        Collection resources = xsdEcoreBuilder.generateResources(uri);
                                        resrc.getResourceSet().getResources().addAll(resources);
                                    }
                                }
                            }

                            if (xsdEcoreBuilder == null) {
                                error(new XMIException(
                                                       XsdUiPlugin.Util.getString("XsdEditor.Cannot_resolve_schema_location_59"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                            } else {
                                XSDElementDeclaration xsdElementDeclaration = xsdEcoreBuilder.getSchema().resolveElementDeclaration(namespaceURI,
                                                                                                                                    name);
                                EClass eClass = (EClass)xsdEcoreBuilder.getXSDComponentToEModelElementMap().get(xsdElementDeclaration);
                                if (eClass != null) {
                                    processTopObject(eClass.getEPackage().getEFactoryInstance().create(eClass));
                                    elementDeclarations.push(xsdElementDeclaration);
                                    XSDParticle xsdParticle = xsdElementDeclaration.getTypeDefinition().getComplexType();
                                    if (xsdParticle != null) {
                                        dfaStates.push(xsdParticle.getDFA().getInitialState());
                                    } else {
                                        dfaStates.push(null);
                                    }
                                } else {
                                    error(new XMIException(
                                                           XsdUiPlugin.Util.getString("XsdEditor.Cannot_resolve_EClass__60"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                                }
                            }
                        }

                        @Override
                        protected void processElement( String name,
                                                       String prefix,
                                                       String localName ) {
                            if (isError()) {
                                types.push(ERROR_TYPE);
                            } else {
                                if (objects.isEmpty()) {
                                    createTopObject(prefix, localName);

                                } else {
                                    EObject peekObject = objects.peek();
                                    XSDParticle.DFA.State state = (XSDParticle.DFA.State)dfaStates.peek();
                                    if (state == null) {
                                        error(new XMIException(
                                                               XsdUiPlugin.Util.getString("XsdEditor.Cannot_contain_content__61"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                                    } else {
                                        XSDParticle.DFA.Transition transition = state.accept(helper.getURI(prefix), localName);
                                        if (transition == null) {
                                            error(new XMIException(
                                                                   XsdUiPlugin.Util.getString("XsdEditor.Not_expecting_this_element__62"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                                        } else {
                                            dfaStates.set(dfaStates.size() - 1, transition.getState());

                                            XSDParticle transitionXSDParticle = transition.getParticle();
                                            XSDTerm xsdTerm = transitionXSDParticle.getTerm();
                                            XSDElementDeclaration xsdElementDeclaration = null;
                                            if (xsdTerm instanceof XSDElementDeclaration) {
                                                xsdElementDeclaration = (XSDElementDeclaration)xsdTerm;
                                            } else {
                                                xsdElementDeclaration = xsdEcoreBuilder.getSchema().resolveElementDeclaration(helper.getURI(prefix),
                                                                                                                              name);
                                            }

                                            EClass eClass = (EClass)xsdEcoreBuilder.getXSDComponentToEModelElementMap().get(xsdElementDeclaration);
                                            if (eClass != null) {
                                                EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
                                                ((EList)peekObject.eGet(peekObject.eClass().getEStructuralFeature(XsdUiPlugin.Util.getString("XsdEditor.contents_63")))).add(eObject); //$NON-NLS-1$

                                                processObject(eObject);
                                                elementDeclarations.push(xsdElementDeclaration);
                                                XSDParticle xsdParticle = xsdElementDeclaration.getTypeDefinition().getComplexType();
                                                if (xsdParticle != null) {
                                                    dfaStates.push(xsdParticle.getDFA().getInitialState());
                                                } else {
                                                    dfaStates.push(null);
                                                    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = xsdElementDeclaration.getTypeDefinition().getSimpleType();
                                                    if (xsdSimpleTypeDefinition != null) {
                                                        EStructuralFeature valueFeature = eClass.getEStructuralFeature(XsdUiPlugin.Util.getString("XsdEditor.value_64")); //$NON-NLS-1$
                                                        if (valueFeature != null) {
                                                            text = new StringBuffer();
                                                            types.set(types.size() - 1, valueFeature);
                                                        }
                                                    }
                                                }
                                            } else {
                                                error(new XMIException(
                                                                       XsdUiPlugin.Util.getString("XsdEditor.Cannot_resolve_EClass__65"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void endElement( String uri,
                                                String localName,
                                                String name ) {
                            EObject topObject = objects.pop();
                            elements.pop();
                            Object type = types.pop();

                            if (text != null) {
                                EAttribute eAttribute = (EAttribute)type;
                                EDataType eDataType = eAttribute.getEAttributeType();
                                Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
                                                                                                              text.toString());
                                topObject.eSet(eAttribute, value);
                                text = null;
                            }

                            XSDParticle.DFA.State state = (XSDParticle.DFA.State)dfaStates.pop();
                            if (state != null && !state.isAccepting()) {
                                error(new XMIException(
                                                       XsdUiPlugin.Util.getString("XsdEditor.Need_more_content__66"), getLocation(), getLineNumber(), getColumnNumber())); //$NON-NLS-1$
                            }
                            elementDeclarations.pop();
                        }
                    };

                    return saxXMLHandler;
                }
            };
            xmlStart.load(this, inputStream, op);
        }
    }

    public static class GenericXMLLoadAction extends org.eclipse.ui.actions.ActionDelegate {
        protected IFile file;

        public GenericXMLLoadAction() {
        }

        @Override
        public void run( IAction action ) {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new GenericXMLResourceFactoryImpl()); //$NON-NLS-1$

            Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true);
            resource.setURI(URI.createPlatformResourceURI(file.getFullPath().toString() + ".save.xml", true)); //$NON-NLS-1$
            try {
                resource.save(Collections.EMPTY_MAP);
            } catch (IOException exception) {
                XsdUiPlugin.Util.log(exception);
            }
        }

        @Override
        public void selectionChanged( IAction action,
                                      ISelection selection ) {
            if (selection instanceof IStructuredSelection) {
                Object object = ((IStructuredSelection)selection).getFirstElement();
                if (object instanceof IFile) {
                    file = (IFile)object;
                    action.setEnabled(true);
                    return;
                }
            }
            file = null;
            action.setEnabled(false);
        }
    }

}
