/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

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
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.xsd.ui.PluginConstants;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiPlugin;

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
    public void addPropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     */
    public boolean canDisplay( IEditorInput input ) {
        if (input instanceof IFileEditorInput) {
            return ModelUtil.isXsdFile(((IFileEditorInput)input).getFile());
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext( Object input ) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
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
    public void dispose() {
        if (this.semanticAdapterFactory != null) {
            this.semanticAdapterFactory.dispose();
        }
    }

    /* (non-Javadoc) 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#preDispose()
     */
    public void preDispose() {
        // Default Implementation
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getActionBarContributor()
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        if (actionContributor == null) {
            actionContributor = new XsdSemanticsEditorActionContributor(this);
        }
        return actionContributor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public Control getControl() {
        return treeViewer.getControl();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorInput()
     */
    public IEditorInput getEditorInput() {
        return theInput;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorSite()
     */
    public IEditorSite getEditorSite() {
        return theSite;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return treeViewer;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    public INotifyChangedListener getNotifyChangedListener() {
        if (notificationListener == null) {
            notificationListener = new XsdSemanticsNotificationHandler(treeViewer, this.xsdResource,
                                                                       getSite().getPage().getActiveEditor());
        }
        return notificationListener;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getSite()
     */
    public IWorkbenchPartSite getSite() {
        return theSite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    public String getTitle() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    public Image getTitleImage() {
        return ModelerXsdUiPlugin.getDefault().getImage(PluginConstants.Images.SEMANTICS_ICON);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    public String getTitleToolTip() {
        return TOOLTIP;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     */
    public void setTitleText( String title ) {
        // do nothing;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( IMarker marker ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
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
    public boolean isDirty() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
     */
    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#initializeEditorPage()
     * @since 5.0.2
     */
    public void initializeEditorPage() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input,
                             boolean forceRefresh ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    public void setLabelProvider( ILabelProvider provider ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     */
    public void updateReadOnlyState( boolean isReadOnly ) {
        // swjTODO: implement
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    treeViewer.setAutoExpandLevel(2);
                    treeViewer.setInput(new ItemProvider(Collections.singleton(getXsdSchema())));
                }
            });
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openComplete()
     * @since 4.2
     */
    public void openComplete() {
        // Default Implementation
    }

    /**
     * @return False.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst( IEditorInput input ) {
        return false;
    }
}
