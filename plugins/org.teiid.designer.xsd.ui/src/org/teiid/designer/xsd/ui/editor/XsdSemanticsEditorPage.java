/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.editor;

import java.util.Collections;
import java.util.EventObject;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.provider.XSDSemanticItemProviderAdapterFactory;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.editors.ModelEditorPageOutline;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;
import org.teiid.designer.xsd.ui.ModelerXsdUiPlugin;
import org.teiid.designer.xsd.ui.PluginConstants;


/**
 * @since 8.0
 */
public class XsdSemanticsEditorPage implements ModelEditorPage, ISelectionChangedListener, IGotoMarker {

    private static final String NAME = ModelerXsdUiConstants.Util.getString("xsdSemanticsEditor.name"); //$NON-NLS-1$
    private static final String TOOLTIP = ModelerXsdUiConstants.Util.getString("xsdSemanticsEditor.tooltip"); //$NON-NLS-1$

    TreeViewer treeViewer;
    private XSDSemanticItemProviderAdapterFactory semanticAdapterFactory;
    private INotifyChangedListener notificationListener;

    /** This keeps track of the root object of the model. */
    protected XSDSchema xsdSchema;
    /** This is the model resource for the current xsd file being displayed in the editor */
    private Resource xsdResource;
    private XsdSemanticsEditorActionContributor actionContributor;
    private IEditorSite theSite;
    private IEditorInput theInput;

    /**
     * Construct an instance of XsdSemanticsEditorPage.
     */
    public XsdSemanticsEditorPage() {
        super();
    }

    // This just notifies those things that are affected by the section.
    //
    @Override
	public void selectionChanged( SelectionChangedEvent selectionChangedEvent ) {
        // swjTODO: hook this into the selection provider for ModelEditor

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

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
	public void addPropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     */
    @Override
	public boolean canDisplay( IEditorInput input ) {
        if (input instanceof IFileEditorInput) {
            return ModelUtil.isXsdFile(((IFileEditorInput)input).getFile());
        }
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    @Override
	public boolean canOpenContext( Object input ) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
	public void createPartControl( Composite parent ) {
        treeViewer = new TreeViewer(parent);
        semanticAdapterFactory = new XSDSemanticItemProviderAdapterFactory();
        treeViewer.setContentProvider(new AdapterFactoryContentProvider(semanticAdapterFactory));
        treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(semanticAdapterFactory));
        treeViewer.setAutoExpandLevel(2);
        treeViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                // filter built-ins if needed:
                if (!(parentElement instanceof EObject) && element instanceof XSDSimpleTypeDefinition) {
                    // parent is not an EObject, and kid is a STD; need to filter out built-ins.
                    XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)element;
                    return std.getSchema() == xsdSchema;
                } // endif

                return true;
            }
        });

        treeViewer.addSelectionChangedListener(this);

        treeViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));

        // new AdapterFactoryTreeEditor(treeViewer.getTree(), semanticAdapterFactory);

        createContextMenu();
    }

    protected void createContextMenu() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
	public void dispose() {
        if (this.semanticAdapterFactory != null) {
            this.semanticAdapterFactory.dispose();
        }
    }

    /* (non-Javadoc) 
     * @See org.teiid.designer.ui.editors.ModelEditorPage#preDispose()
     */
    @Override
	public void preDispose() {
        // Default Implementation
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void doSave( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
	public void doSaveAs() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getActionBarContributor()
     */
    @Override
	public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        if (actionContributor == null) {
            actionContributor = new XsdSemanticsEditorActionContributor(this);
        }
        return actionContributor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
	public Object getAdapter( Class adapter ) {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getControl()
     */
    @Override
	public Control getControl() {
        return treeViewer.getControl();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorInput()
     */
    @Override
	public IEditorInput getEditorInput() {
        return theInput;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorSite()
     */
    @Override
	public IEditorSite getEditorSite() {
        return theSite;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     */
    @Override
	public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    @Override
	public ISelectionProvider getModelObjectSelectionProvider() {
        return treeViewer;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    @Override
	public INotifyChangedListener getNotifyChangedListener() {
        if (notificationListener == null) {
            notificationListener = new XsdSemanticsNotificationHandler(treeViewer, this.xsdResource,
                                                                       getSite().getPage().getActiveEditor());
        }
        return notificationListener;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    @Override
	public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getSite()
     */
    @Override
	public IWorkbenchPartSite getSite() {
        return theSite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    @Override
	public String getTitle() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    @Override
	public Image getTitleImage() {
        return ModelerXsdUiPlugin.getDefault().getImage(PluginConstants.Images.SEMANTICS_ICON);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    @Override
	public String getTitleToolTip() {
        return TOOLTIP;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     */
    @Override
	public void setTitleText( String title ) {
        // do nothing;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    @Override
	public void gotoMarker( IMarker marker ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
	public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        theSite = site;
        theInput = input;
        if (input instanceof IFileEditorInput) {
            // get the XSD resource.
            try {
                // Get the IFile associated with the editor
                final IResource xsdFile = ((IFileEditorInput)input).getFile();
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

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
	public boolean isDirty() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
	public boolean isSaveAsAllowed() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
     */
    @Override
	public boolean isSaveOnCloseNeeded() {
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.teiid.designer.ui.editors.ModelEditorPage#initializeEditorPage()
     * @since 5.0.2
     */
    @Override
	public void initializeEditorPage() {
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    @Override
	public void openContext( Object input ) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    @Override
	public void openContext( Object input,
                             boolean forceRefresh ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
	public void removePropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
	public void setFocus() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    @Override
	public void setLabelProvider( ILabelProvider provider ) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     */
    @Override
	public void updateReadOnlyState( boolean isReadOnly ) {
        // swjTODO: implement
    }

    /**
     * @see org.teiid.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    @Override
	public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {

            Display.getDefault().asyncExec(new Runnable() {
                @Override
				public void run() {
                    treeViewer.setAutoExpandLevel(2);
                    treeViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));
                }
            });
        }
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPage#openComplete()
     * @since 4.2
     */
    @Override
	public void openComplete() {
        // Default Implementation
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
