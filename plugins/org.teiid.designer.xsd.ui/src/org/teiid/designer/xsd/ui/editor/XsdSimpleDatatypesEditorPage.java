/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.TransactionRunnable;
import org.teiid.designer.core.notification.util.SourcedNotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.custom.impl.XsdModelAnnotationImpl;
import org.teiid.designer.ui.common.eventsupport.SelectionProvider;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.common.widget.DefaultContentProvider;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.editors.ModelEditorPageOutline;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.filter.StructuredViewerTextFilterer;
import org.teiid.designer.ui.forms.FormTextObjectEditor;
import org.teiid.designer.ui.forms.FormUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;
import org.teiid.designer.xsd.ui.ModelerXsdUiPlugin;


/**
 * XsdSemanticsEditorPage
 *
 * @since 8.0
 */
public class XsdSimpleDatatypesEditorPage extends EditorPart implements ModelEditorPage// , ISelectionChangedListener
{
    private static final String PAGE_NAME = GUIFacetHelper.getString("SimpleDatatypeEditor.name"); //$NON-NLS-1$
    private static final String PAGE_TOOLTIP = GUIFacetHelper.getString("SimpleDatatypeEditor.tooltip"); //$NON-NLS-1$
    private static final String BUTTON_DELETE = GUIFacetHelper.getString("SimpleDatatypeEditor.delete"); //$NON-NLS-1$
    private static final String BUTTON_NEW = GUIFacetHelper.getString("SimpleDatatypeEditor.new"); //$NON-NLS-1$
    private static final String BUTTON_CLEAR = GUIFacetHelper.getString("SimpleDatatypeEditor.clear"); //$NON-NLS-1$
    private static final String DEFAULT_FILTER_TEXT = GUIFacetHelper.getString("SimpleDatatypeEditor.defaultFilter"); //$NON-NLS-1$
    private static final String LABEL_LOCAL_ID = "SimpleDatatypeEditor.labelLocal"; //$NON-NLS-1$
    private static final String LABEL_BUILTIN = GUIFacetHelper.getString("SimpleDatatypeEditor.labelBuiltin"); //$NON-NLS-1$
    static final String LINK_SHOW_BUILT_IN_TYPES = GUIFacetHelper.getString("SimpleDatatypeEditor.show"); //$NON-NLS-1$
    static final String LINK_HIDE_BUILT_IN_TYPES = GUIFacetHelper.getString("SimpleDatatypeEditor.hide"); //$NON-NLS-1$
    static final String TRANSACTION_NEW = GUIFacetHelper.getString("SimpleDatatypeEditor.transaction_new"); //$NON-NLS-1$

    /** This keeps track of the root object of the model. */
    protected XSDSchema xsdSchema;
    /** This is the model resource for the current xsd file being displayed in the editor */
    Resource xsdResource;

    SimpleDatatypeEditorPanel editorPanel;
    private Composite mainControl;
    ListViewer localTypes;
    ListViewer builtInTypes;
    Object lastSel;
    private MyNotifyChangedListener myChgList = new MyNotifyChangedListener();
    private MyModelSelectionProvider mySelProv = new MyModelSelectionProvider();
    Button newBtn;
    Button rmvBtn;
    Object selectAtNextRefresh;

    boolean xsdResourceIsStale = false;

    @Override
    public void doSave( IProgressMonitor monitor ) {
        // do nothing
    }

    @Override
    public void doSaveAs() {
        // do nothing
    }

    @Override
    public boolean isSaveAsAllowed() {
        // default
        return false;
    }

    // defect 18127 -- this method needs to be overridden to keep the correct title.
    @Override
    public String getTitle() {
        return PAGE_NAME;
    }

    // defect 18127 -- this method needs to be overridden to keep the correct title.
    @Override
    public String getTitleToolTip() {
        return PAGE_TOOLTIP;
    }

    @Override
	public boolean canDisplay( IEditorInput input ) {
        if (input instanceof IFileEditorInput) {
            return ModelUtil.isXsdFile(((IFileEditorInput)input).getFile());
        }
        return false;
    }

    @Override
	public boolean canOpenContext( Object input ) {
        if (input instanceof XsdModelAnnotationImpl) {
            return ((XsdModelAnnotationImpl)input).getResource().equals(this.xsdResource);
        } else if (input instanceof XSDConcreteComponent) {
            return ((XSDConcreteComponent)input).getSchema().equals(getXsdSchema());
        }

        return false;
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPage#initializeEditorPage(java.lang.Object, boolean)
     * @since 5.0.2
     */
    @Override
	public void initializeEditorPage() {
        // openContext(input);
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    @Override
	public void openContext( Object input,
                             boolean forceRefresh ) {
        // we have to ignore forceRefresh; in the most frequent case,
        // the user clicked a link to jump to a base type, which asks
        // for the editor for that type be open; forceRefresh is always
        // equal to forceOpen (in this case, true)
        // if (forceRefresh) {
        // refreshListAndEditor();
        // } // endif
        openContext(input);
    }

    @Override
	public void openContext( final Object input ) {
        final Object resource = this.xsdResource;

        if (editorPanel != null) {
            // run async to allow time for a new type to get inserted, if need be:
            Display.getDefault().asyncExec(new Runnable() {
                @Override
				public void run() {
                    XSDSimpleTypeDefinition std = null;
                    if (input instanceof XSDSimpleTypeDefinition) {
                        std = (XSDSimpleTypeDefinition)ModelObjectUtilities.getRealEObject((XSDSimpleTypeDefinition)input);
                    } else if (input instanceof XSDConstrainingFacet) {
                        XSDConstrainingFacet facet = (XSDConstrainingFacet)ModelObjectUtilities.getRealEObject((XSDConstrainingFacet)input);
                        std = facet.getSimpleTypeDefinition();
                    } // endif

                    ISelection newSel;

                    if (ModelerCore.getDatatypeManager(getXsdSchema(), true).isBuiltInDatatype(std)) {
                        // type is built-in; select it:
                        builtInTypes.setSelection((std == null) ? StructuredSelection.EMPTY : new StructuredSelection(std));
                        newSel = builtInTypes.getSelection();
                    } else {
                        // type should be local, select it:
                        localTypes.setSelection((std == null) ? StructuredSelection.EMPTY : new StructuredSelection(std));
                        newSel = localTypes.getSelection();
                    } // endif

                    editorPanel.setFocus(); // is this needed?

                    // make sure the selection has really been made:
                    if (newSel.isEmpty()) {
                        // nothing got selected; probably a filter is in place;
                        // select the type ourselves in the editor
                        editorPanel.setInput(std);
                    } // endif

                    // send out selection to the workspace
                    ISelection selection = newSel;

                    if (selection.isEmpty()) {
                        selection = new StructuredSelection(resource);
                    }

                    getModelObjectSelectionProvider().setSelection(selection);
                }
            });
        } // endif
    }

    @Override
	public Control getControl() {
        return mainControl;
    }

    @Override
	public ISelectionProvider getModelObjectSelectionProvider() {
        return mySelProv;
    }

    @Override
	public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        // ignore, for now
        return null;
    }

    @Override
	public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        return null;
    }

    @Override
	public void setLabelProvider( ILabelProvider provider ) {
        // do nothing
    }

    @Override
	public INotifyChangedListener getNotifyChangedListener() {
        return myChgList;
    }

    @Override
	public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    @Override
	public void updateReadOnlyState( boolean isReadOnly ) {
        if (editorPanel != null && !editorPanel.isDisposed()) {
            // assume if editor panel is good, so are buttons:
            editorPanel.setReadOnly(isReadOnly);
            rmvBtn.setEnabled(!isReadOnly);
            newBtn.setEnabled(!isReadOnly);
        } // endif
    }

    @Override
	public void setTitleText( String title ) {
        // do nothing
    }

    @Override
	public void preDispose() {
        // do nothing
    }

    @Override
	public void openComplete() {
        // do nothing
    }

    @Override
	public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
				public void run() {
                    refreshListAndEditor();
                }
            });
        }
    }

    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        setSite(site);
        setInput(input);
        if (input instanceof IFileEditorInput) {
            // get the XSD resource.
            try {
                // Get the IFile associated with the editor
                IFile xsdFile = ((IFileEditorInput)input).getFile();
                if (xsdFile == null) {
                    final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }

                // Get the EMF resource for the IFile in the workspace
                final String xsdLocation = xsdFile.getLocation().toString();
                final URI xsdUri = URI.createFileURI(xsdLocation);
                xsdResource = ModelerCore.getModelContainer().getResource(xsdUri, true);

                // The resource must exist in the container
                if (xsdResource == null) {
                    final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_1", input.getName()); //$NON-NLS-1$
                    throw new PartInitException(msg);
                }
                // modelProject =
                xsdFile.getProject();
            } catch (ModelWorkspaceException e) {
                final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            } catch (CoreException e) {
                final String msg = ModelerXsdUiConstants.Util.getString("XsdEditor.Input_Error__Model_Editor_cannot_open_{0}_2", input.getName()); //$NON-NLS-1$
                throw new PartInitException(msg, e);
            }
        } else {
            throw new PartInitException(
                                        ModelerXsdUiConstants.Util.getString("XsdEditor.Invalid_Input__Must_be_IFileEditorInput._33")); //$NON-NLS-1$
        }
    }

    @Override
    public boolean isDirty() {
        // defect 18041 - during deletion, we may not be able to resolve a proxy to get a real schema:
        XSDSchema xsdSchema = getXsdSchema();
        if (xsdSchema != null) {
            Resource resource = xsdSchema.eResource();
            if (resource != null) return resource.isModified();
        } // endif

        return false;
    }

    @Override
    public void createPartControl( Composite parent ) {
        mainControl = parent;

        parent.setLayout(new FillLayout());
        SashForm mainSash = new SashForm(parent, SWT.HORIZONTAL);

        FormToolkit ftk = ModelerXsdUiPlugin.getDefault().getFormToolkit(parent.getDisplay());

        Form leftPanel = ftk.createForm(mainSash);
        GridLayout gl = new GridLayout(2, false);
        leftPanel.getBody().setLayout(gl);

        newBtn = ftk.createButton(leftPanel.getBody(), BUTTON_NEW, SWT.NONE);
        newBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

        rmvBtn = ftk.createButton(leftPanel.getBody(), BUTTON_DELETE, SWT.NONE);
        rmvBtn.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true, false));

        AdapterFactoryLabelProvider labelProv = new AdapterFactoryLabelProvider(new XSDSemanticItemProviderAdapterFactory());
        StructuredViewerTextFilterer svf = new StructuredViewerTextFilterer(DEFAULT_FILTER_TEXT, BUTTON_CLEAR, null, labelProv);
        Control svfComp = svf.addControl(leftPanel.getBody(), ftk);
        svfComp.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 2, 1));

        // Lists:
        final SashForm typeSash = new SashForm(leftPanel.getBody(), SWT.VERTICAL);
        typeSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        // upper list:
        final Section upperSection = ftk.createSection(typeSash, ExpandableComposite.COMPACT | ExpandableComposite.EXPANDED
                                                                 | ExpandableComposite.TITLE_BAR);
        upperSection.marginHeight = 0;
        upperSection.marginWidth = 0;
        final Composite upperSectionContents = ftk.createComposite(upperSection);
        gl = new GridLayout();
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        upperSectionContents.setLayout(gl);
        upperSection.setClient(upperSectionContents);

        IResource xsdFile = ((IFileEditorInput)getEditorInput()).getFile();
        upperSection.setText(ModelerXsdUiConstants.Util.getString(LABEL_LOCAL_ID, xsdFile.getName()));
        // Label localLabel = ftk.createLabel(leftPanel.getBody(), ModelerXsdUiPlugin.Util.getString(LABEL_LOCAL_ID,
        // xsdFile.getName()));
        // localLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, true, false, 2, 1));

        localTypes = new ListViewer(upperSectionContents, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        localTypes.setContentProvider(new AtomicSimpleTypesContentProvider());
        localTypes.setLabelProvider(labelProv);
        localTypes.setInput(getXsdSchema());
        localTypes.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        FormTextObjectEditor ftoe = new FormTextObjectEditor(null, null, true) {
            @Override
            protected void valueClicked( Object value ) {
                if (typeSash.getMaximizedControl() != null) {
                    // need to unmaximize:
                    typeSash.setMaximizedControl(null);
                    setValue(LINK_HIDE_BUILT_IN_TYPES);
                } else {
                    // need to maximize:
                    typeSash.setMaximizedControl(upperSection);
                    setValue(LINK_SHOW_BUILT_IN_TYPES);
                } // endif
            }
        };
        ftoe.addControl(FormUtil.getScrolledForm(upperSectionContents), upperSectionContents, ftk);
        ftoe.getFormText().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ftoe.setValue(LINK_HIDE_BUILT_IN_TYPES);

        // lower list:
        final Section lowerSection = ftk.createSection(typeSash, ExpandableComposite.COMPACT | ExpandableComposite.EXPANDED
                                                                 | ExpandableComposite.TITLE_BAR);
        lowerSection.marginHeight = 0;
        lowerSection.marginWidth = 0;
        lowerSection.setText(LABEL_BUILTIN);
        // Label builtin = ftk.createLabel(leftPanel.getBody(), LABEL_BUILTIN);
        // builtin.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, true, false, 2, 1));
        builtInTypes = new ListViewer(lowerSection, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        builtInTypes.setContentProvider(new AtomicSimpleTypeArrayContentProvider());
        builtInTypes.setLabelProvider(labelProv);
        try {
            builtInTypes.setInput(ModelerCore.getDatatypeManager(getXsdSchema(), true).getBuiltInDatatypes());
        } catch (ModelerCoreException err) {
            ModelerXsdUiConstants.Util.log(err);
        } // endtry
        builtInTypes.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        lowerSection.setClient(builtInTypes.getControl());

        typeSash.setWeights(new int[] {1, 1});

        // Main panel, main sash:
        editorPanel = new SimpleDatatypeEditorPanel(mainSash);
        editorPanel.setSchema(getXsdSchema());
        mainSash.setWeights(new int[] {2, 7});

        // Add listeners:
        svf.attachToViewer(localTypes, false);
        svf.attachToViewer(builtInTypes, false);
        localTypes.addSelectionChangedListener(mySelProv); // to expose selection to world
        // defect 18562 - expose built-in selections, too:
        // Note this was orinally in there, but had to be removed. See changes to
        // MyModelSelectionProvider for how I got around the issue.
        builtInTypes.addSelectionChangedListener(mySelProv); // to expose selection to world
        localTypes.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged( SelectionChangedEvent event ) {
                ISelection selection = event.getSelection();
                if (!selection.isEmpty()) {

                    boolean canRemove = false;

                    EObject selected = SelectionUtilities.getSelectedEObject(selection);
                    final EObject resolvedSelected = ModelObjectUtilities.getRealEObject(selected);

                    // update list if needed:
                    if (resolvedSelected != selected) {
                        localTypes.refresh();
                    } // endif

                    // update editor if needed:
                    if (!FormUtil.safeEquals(resolvedSelected, lastSel)) {
                        builtInTypes.setSelection(null);
                        lastSel = resolvedSelected;
                        UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
							public void run() {
                                editorPanel.setInput((XSDSimpleTypeDefinition)resolvedSelected);
                            }
                        }); // endanon
                    } // endif
                    if (resolvedSelected != null) {
                        canRemove = !ModelObjectUtilities.isReadOnly(resolvedSelected);
                    }
                    rmvBtn.setEnabled(canRemove);
                } else {
                    // selection cleared.
                    lastSel = null;
                    rmvBtn.setEnabled(false);
                } // endif
            }
        });
        builtInTypes.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged( SelectionChangedEvent event ) {
                ISelection selection = event.getSelection();
                if (!selection.isEmpty()) {
                    final EObject selected = ModelObjectUtilities.getRealEObject(SelectionUtilities.getSelectedEObject(selection));
                    if (!FormUtil.safeEquals(selected, lastSel)) {
                        localTypes.setSelection(null);
                        lastSel = selected;
                        UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
							public void run() {
                                editorPanel.setInput((XSDSimpleTypeDefinition)selected);
                            }
                        }); // endanon
                    } // endif
                } else {
                    // selection cleared.
                    lastSel = null;
                } // endif
            }
        });
        newBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                // defect 18444 - make sure things are wrapped in transactions
                // run the new operation in a transaction to allow undo:
                final TransactionRunnable runnable = new TransactionRunnable() {
                    @Override
					public Object run( final UnitOfWork uow ) {
                        final XSDSimpleTypeDefinition newType = GUIFacetHelper.createType(newBtn.getShell(), getXsdSchema(), null);
                        if (newType != null) {
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
								public void run() {
                                    // a refresh is performed by model notification system
                                    // before this call
                                    localTypes.setSelection(new StructuredSelection(newType));

                                    // in case of proxy/refresh issues, try to select at next refresh:
                                    if (localTypes.getSelection().isEmpty()) {
                                        selectAtNextRefresh = newType;
                                    } // endif
                                }
                            });
                        } // endif
                        return null;
                    }
                }; // endanon transaction

                try {
                    ModelerCore.getModelEditor().executeAsTransaction(runnable, TRANSACTION_NEW, true, true, this);
                } catch (ModelerCoreException mce) {
                    ModelerXsdUiConstants.Util.log(mce);
                } // endtry
            }
        });
        rmvBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                EObject sel = SelectionUtilities.getSelectedEObject(localTypes.getSelection());
                if (sel != null) {
                    // note that a refresh is performed by model notification system
                    ModelObjectUtilities.delete(sel, true, true, XsdSimpleDatatypesEditorPage.this);
                    editorPanel.setInput(null);
                } // endif
            }
        });

        // org.teiid.designer.ui.common.util.LayoutDebugger.debugLayout(parent);
    }

    @Override
    public void setFocus() {
        // do nothing
    }

    //
    // Data methods:
    //
    public XSDSchema getXsdSchema() {
        if (xsdSchema == null) {
            // need to get in the first place:
            if (xsdResource != null && xsdResource instanceof XSDResourceImpl) {
                XSDResourceImpl rsrc = (XSDResourceImpl)xsdResource;
                xsdSchema = rsrc.getSchema();
            } // endif -- have resource, it is an xsd resource

        } else if (xsdSchema.eIsProxy()) {
            // need to resolve proxy:
            xsdSchema = (XSDSchema)ModelObjectUtilities.getRealEObject(xsdSchema);
            if (xsdSchema != null && GUIFacetHelper.isReady(editorPanel)) {
                editorPanel.setSchema(xsdSchema);
            } // endif
        } // endif

        return xsdSchema;
    }

    void refreshListAndEditor() {
        if (localTypes != null && !localTypes.getList().isDisposed()) {
            Object newSelection = null;
            if (selectAtNextRefresh == null) {
                // try to maintain the selection through the refresh:
                EObject oldSelection = SelectionUtilities.getSelectedEObject(localTypes.getSelection());
                if (oldSelection != null && oldSelection.eIsProxy()) {
                    // try to resolve:
                    newSelection = ModelObjectUtilities.getRealEObject(oldSelection);
                } // endif

            } else {
                // try to select a newly-created object:
                newSelection = selectAtNextRefresh;
                // clear pending selection:
                selectAtNextRefresh = null;
            } // endif

            localTypes.refresh();

            // see if we still have a selection:
            ISelection selection = localTypes.getSelection();
            if (selection.isEmpty()) {
                // no, selection empty, either a deletion or a save/reload:
                if (newSelection != null) {
                    // save or reload:
                    localTypes.setSelection(new StructuredSelection(newSelection));
                } else {
                    // a deletion:
                    editorPanel.setInput(null);
                } // endif
            } else {
                // selection not empty, use it:
                editorPanel.setInput((XSDSimpleTypeDefinition)SelectionUtilities.getSelectedObject(selection));
            } // endif
        } // endif
    }

    /*
     * Defect 23150 required checking the state of the resource behind this editor page.
     * If the resource is ever NULL or if the getSchema() == null or the schema object loses it's eResource, we can assume it's stale.
     */
    void validateResource() {
        if (!xsdResourceIsStale) {
            if (xsdResource == null) {
                xsdResourceIsStale = true;
            } else {
                XSDSchema schema = getXsdSchema();
                if (schema == null || schema.eResource() == null) {
                    xsdResourceIsStale = true;
                }
            }
        }
    }

    class MyNotifyChangedListener implements INotifyChangedListener {
        @Override
		public void notifyChanged( Notification notification ) {
            // Defect 23150 ( NPE's when processing notifications of a deleted model )
            // Check Resource in case it is deleted.
            validateResource();
            // If xsd resource is stale
            if (xsdResourceIsStale) {
                return;
            }

            // see if it applies to us:
            // check source and filter out if it is "us":
            SourcedNotification sn;
            if (notification instanceof SourcedNotification) {
                sn = (SourcedNotification)notification;
                Object src = sn.getSource();

                // only ignore if from this editor...
                if (src == editorPanel.getModel()) {

                    // if a rename, update the local list:
                    Set changedFeatures = org.teiid.designer.core.notification.util.SourcedNotificationUtilities.getAffectedFeatureIDs(sn,
                                                                                                                                           XSDSimpleTypeDefinition.class);
                    if (changedFeatures.contains(new Integer(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME)) ||
                    		changedFeatures.contains(new Integer(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__SIMPLE_TYPE)) ) {
                        // was a rename; update the list:
                        safeRefreshLocal();
                    } // endif

                    // this is a notification caused by a user change to the editor;
                    // do nothing else, since the editor already reflects the update:
                    return;
                } // endif

            } else {
                // not a SourceNotification:
                sn = null;
            } // endif

            Set notifiers;
            if (sn != null) {
                // obtain all notifiers for this notification:
                notifiers = SourcedNotificationUtilities.getAllNotifiers(sn);

            } else {
                // just one notifier:
                notifiers = Collections.singleton(notification.getNotifier());
            } // endif

            Iterator itor = notifiers.iterator();
            while (itor.hasNext()) {
                Object notifier = itor.next();

                XSDSchema xsdc = null;
                if (notifier == xsdResource) {
                    // our resource fired a notification; likely a save/reload or an unload:
                    xsdc = xsdSchema;

                } else if (notifier instanceof XSDSchema) {
                    // a schema has been updated, such as by add/remove children
                    xsdc = (XSDSchema)notifier;

                } else if (notifier instanceof XSDSimpleTypeDefinition) {
                    XSDSimpleTypeDefinition xstd = (XSDSimpleTypeDefinition)notifier;
                    xsdc = xstd.getSchema();

                } else if (notifier instanceof XSDFacet) {
                    XSDFacet xf = (XSDFacet)notifier;
                    XSDSimpleTypeDefinition simpleTypeDefinition = xf.getSimpleTypeDefinition();

                    // especially with deletions, we may not be able to find the simple
                    // type via the above method. Search for more detail if needed:
                    if (simpleTypeDefinition == null && notification instanceof SourcedNotification) {
                        // scan all notifications to see if we can find a simple type:
                        simpleTypeDefinition = findSimpleTypeDefinition(((SourcedNotification)notification).getNotifications());
                    } // endif

                    // only worry if the change is to the currently-selected STD
                    if (lastSel != null && lastSel.equals(simpleTypeDefinition)) {
                        xsdc = simpleTypeDefinition.getSchema();
                    } // endif

                } else if (notifier instanceof XSDAnnotation) {
                    XSDAnnotation xa = (XSDAnnotation)notifier;

                    // only worry if the change is to the currently-selected STD
                    if (lastSel != null && lastSel.equals(findSimpleType(xa))) {
                        xsdc = xa.getSchema();
                    } // endif

                } // endif

                // found a schema, is it ours?
                if (getXsdSchema().equals(xsdc)) {
                    // yes:
                    refreshListAndEditor();
                    break;
                } // endif
            } // endwhile
        }

        private void safeRefreshLocal() {
            if (localTypes != null && !localTypes.getControl().isDisposed()) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
					public void run() {
                        localTypes.refresh();
                    }
                });
            } // endif
        }

        private XSDSimpleTypeDefinition findSimpleTypeDefinition( Collection notifications ) {
            Iterator itor = notifications.iterator();
            while (itor.hasNext()) {
                Notification not = (Notification)itor.next();
                Object notifier = not.getNotifier();
                if (notifier instanceof XSDSimpleTypeDefinition) {
                    return (XSDSimpleTypeDefinition)notifier;
                } // endif
            } // endwhile

            // not found:
            return null;
        }

        private XSDSimpleTypeDefinition findSimpleType( XSDConcreteComponent xc ) {
            XSDSimpleTypeDefinition rv = null;
            XSDConcreteComponent parent = xc.getContainer();
            while (parent != null) {
                if (parent instanceof XSDSimpleTypeDefinition) {
                    rv = (XSDSimpleTypeDefinition)parent;
                    break;
                } // endif

                // not a SDT, go up hierarchy:
                parent = parent.getContainer();
            } // endwhile

            return rv;
        }
    } // endclass MyNotifyChangedListener

    class MyModelSelectionProvider extends SelectionProvider implements ISelectionChangedListener {
        // Listener:
        @Override
		public void selectionChanged( SelectionChangedEvent event ) {
            ISelection selection = event.getSelection();
            if (selection.isEmpty()) {
                if (localTypes.getSelection().isEmpty() && builtInTypes.getSelection().isEmpty()) {
                    // defect 18562 - only set an empty selection when neither of the two lists
                    // have a selection. This keeps us from getting selectionChanged events
                    // out of order (resulting in a blank properties view).
                    setSelection(selection);
                } // endif
            } else {
                setSelection(selection);
            } // endif
        }
    } // endclass MyModelSelectionProvider

    static class AtomicSimpleTypeArrayContentProvider extends DefaultContentProvider {
        @Override
        public Object[] getElements( Object inputElement ) {
            // defect 18560 - filter out non-atomic built-in types
            Object[] objects = (Object[])inputElement;
            List rv = new ArrayList(objects.length);

            for (int i = 0; i < objects.length; i++) {
                XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)objects[i];
                if (std.getVariety() == XSDVariety.ATOMIC_LITERAL) {
                    rv.add(std);
                } // endif
            } // endfor

            return rv.toArray();
        }
    } // endclass ArrayContentProvider

    static class AtomicSimpleTypesContentProvider extends DefaultContentProvider {
        @Override
        public Object[] getElements( Object inputElement ) {
            XSDSchema x = (XSDSchema)ModelObjectUtilities.getRealEObject((EObject)inputElement);
            List allTypes = x.getTypeDefinitions();
            List rv = new ArrayList(allTypes.size());

            Iterator itor = allTypes.iterator();
            while (itor.hasNext()) {
                XSDTypeDefinition type = (XSDTypeDefinition)itor.next();
                // make sure simple type:
                if (type.getSchema() == x && type instanceof XSDSimpleTypeDefinition) {
                    // force resolution of proxies:
                    XSDSimpleTypeDefinition simptype = (XSDSimpleTypeDefinition)ModelObjectUtilities.getRealEObject(type);
                    // make sure atomic simple type:
                    if (simptype.getVariety() == XSDVariety.ATOMIC_LITERAL) {
                        rv.add(simptype);
                    } // endif
                } // endif
            } // enwhile

            return rv.toArray();
        }
    }

    /**
     * @return False.
     * @see org.teiid.designer.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    @Override
	public boolean isSelectedFirst( IEditorInput input ) {
        return false;
    }
}
