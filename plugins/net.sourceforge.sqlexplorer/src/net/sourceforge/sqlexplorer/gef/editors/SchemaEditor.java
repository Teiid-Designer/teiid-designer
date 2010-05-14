/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.gef.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.EventObject;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.model.Table;
import net.sourceforge.sqlexplorer.gef.wizards.TableAdapter;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.TableNodeTransfer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class SchemaEditor extends GraphicalEditor {

    Schema schema;

    private final ResourceTracker resourceListener = new ResourceTracker();

    private IPartListener partListener = new IPartListener() {
        // If an open, unsaved file was deleted, query the user to either do a "Save As"
        // or close the editor.
        public void partActivated( final IWorkbenchPart part ) {
            if (part != SchemaEditor.this) return;
            if (!((FileEditorInput)getEditorInput()).getFile().exists()) {
                final Shell shell = getSite().getShell();
                final String title = "Deleted";//$NON-NLS-1$
                final String message = "File Deleted without Saving";//$NON-NLS-1$
                final String[] buttons = {"Save", //$NON-NLS-1$
                    "Close"};//$NON-NLS-1$
                final MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.QUESTION, buttons, 0);
                if (dialog.open() == 0) {
                    if (!performSaveAs()) partActivated(part);
                } else closeEditor(false);
            }
        }

        public void partBroughtToTop( final IWorkbenchPart part ) {
        }

        public void partClosed( final IWorkbenchPart part ) {
        }

        public void partDeactivated( final IWorkbenchPart part ) {
        }

        public void partOpened( final IWorkbenchPart part ) {
        }
    };

    private boolean savePreviouslyNeeded = false;

    public static final String ID = "net.sf.gef.editors.SchemaEditor";//$NON-NLS-1$

    static ActionRegistry getActionRegistry( final SchemaEditor schemaEditor ) {
        return schemaEditor.getActionRegistry();
    }

    static DefaultEditDomain getEditDomain( final SchemaEditor schemaEditor ) {
        return schemaEditor.getEditDomain();
    }

    private KeyHandler keyHandler;

    public SchemaEditor() {
        this.setEditDomain(new DefaultEditDomain(this));
    }

    protected void closeEditor( final boolean flag ) {
        getSite().getPage().closeEditor(SchemaEditor.this, flag);
    }

    @Override
    public void commandStackChanged( final EventObject eventobject ) {
        if (isDirty()) {
            if (!savePreviouslyNeeded()) {
                setSavePreviouslyNeeded(true);
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        } else {
            setSavePreviouslyNeeded(false);
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }

        super.commandStackChanged(eventobject);

    }

    @Override
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();
        final ScrollingGraphicalViewer scrollinggraphicalviewer = (ScrollingGraphicalViewer)getGraphicalViewer();
        final ScalableFreeformRootEditPart scalablefreeformrooteditpart = new ScalableFreeformRootEditPart();
        final ZoomInAction zoominaction = new ZoomInAction(scalablefreeformrooteditpart.getZoomManager());
        final ZoomOutAction zoomoutaction = new ZoomOutAction(scalablefreeformrooteditpart.getZoomManager());
        getActionRegistry().registerAction(zoominaction);
        getActionRegistry().registerAction(zoomoutaction);

        try {
            final IEditorPart pt = this;
            final Action act = new PrintAction(pt) {
                @Override
                public String getText() {
                    return Messages.getString("SchemaEditor.Print..._1"); //$NON-NLS-1$
                }
            };
            getActionRegistry().registerAction(act);
        } catch (final Throwable e) {
        }
        final IHandlerService svc = (IHandlerService)getSite().getService(IHandlerService.class);
        svc.activateHandler(zoominaction.getActionDefinitionId(), new ActionHandler(zoominaction));
        svc.activateHandler(zoomoutaction.getActionDefinitionId(), new ActionHandler(zoomoutaction));
        scrollinggraphicalviewer.setRootEditPart(scalablefreeformrooteditpart);
        scrollinggraphicalviewer.setEditPartFactory(new SchemaPartFactory());
        final SchemaEditorContextMenuProvider schemaContextMenuProvider = new SchemaEditorContextMenuProvider(
                                                                                                              scrollinggraphicalviewer,
                                                                                                              getActionRegistry(),
                                                                                                              this);

        scrollinggraphicalviewer.setContextMenu(schemaContextMenuProvider);
        getSite().registerContextMenu(schemaContextMenuProvider, scrollinggraphicalviewer);
        scrollinggraphicalviewer.setKeyHandler((new GraphicalViewerKeyHandler(scrollinggraphicalviewer)).setParent(getCommonKeyHandler()));
        // Font font = JFaceResources.getDefaultFont();

        final Font font1 = JFaceResources.getDefaultFont();
        scrollinggraphicalviewer.getControl().setFont(font1);
        getGraphicalViewer().addDropTargetListener(new AbstractTransferDropTargetListener(getGraphicalViewer(),
                                                                                          TableNodeTransfer.getInstance()) {
            TableFactory factory = new TableFactory();

            @Override
            protected Request createTargetRequest() {
                // System.out.println("createTargetRequest " +(TableNode) TableNodeTransfer.getInstance().getSelection());
                final CreateRequest request = new CreateRequest();
                request.setFactory(factory);
                factory.setTableNode((TableNode)TableNodeTransfer.getInstance().getSelection());
                return request;
            }

            @Override
            protected void handleDragOver() {
                final DropTargetEvent ev = getCurrentEvent();
                ev.detail = DND.DROP_COPY;
                // System.out.println("handleDragOver " +ev.currentDataType+ " "+ev.data+" "+ev.dataTypes);
                ev.data = TableNodeTransfer.getInstance().getSelection();
                super.handleDragOver();

            }

            @Override
            protected void handleDrop() {
                // DropTargetEvent ev=getCurrentEvent();
                super.handleDrop();
            }

            @Override
            public boolean isEnabled( final DropTargetEvent dte ) {
                if (TableNodeTransfer.getInstance().getSelection() != null) return true;
                return super.isEnabled(dte);
            }

            @Override
            protected void updateTargetRequest() {
                // System.out.println("updateTargetRequest");
                ((CreateRequest)getTargetRequest()).setLocation(getDropLocation());
            }

        });

    }

    @Override
    protected void createActions() {
        super.createActions();
        final ActionRegistry actionregistry = getActionRegistry();
        final IAction iaction = actionregistry.getAction("print");//$NON-NLS-1$
        if (iaction != null) actionregistry.removeAction(iaction);
        Object obj = null;
        obj = new AlignmentAction((IWorkbenchPart)this, 1);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
        obj = new AlignmentAction((IWorkbenchPart)this, 4);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
        obj = new AlignmentAction((IWorkbenchPart)this, 8);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
        obj = new AlignmentAction((IWorkbenchPart)this, 32);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
        obj = new AlignmentAction((IWorkbenchPart)this, 2);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
        obj = new AlignmentAction((IWorkbenchPart)this, 16);
        actionregistry.registerAction(((IAction)(obj)));
        getSelectionActions().add(((IAction)(obj)).getId());
    }

    protected void createOutputStream( final OutputStream os ) throws IOException {
        final ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(this.getSchema());
        out.close();
    }

    @Override
    public void dispose() {

        getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
        partListener = null;
        ((FileEditorInput)getEditorInput()).getFile().getWorkspace().removeResourceChangeListener(resourceListener);
        super.dispose();
    }

    @Override
    public void doSave( final IProgressMonitor progressMonitor ) {
        // IPath ipath = null;
        try {
            final IFile ifile = ((IFileEditorInput)getEditorInput()).getFile();
            // ipath = ifile.getLocation();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            createOutputStream(out);
            ifile.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, progressMonitor);
            out.close();
            getCommandStack().markSaveLocation();
        } catch (final Throwable e) {
            SQLExplorerPlugin.error("Error saving", e);//$NON-NLS-1$
        }
    }

    @Override
    public void doSaveAs() {
        performSaveAs();
    }

    @Override
    public Object getAdapter( final Class type ) {

        if (type == IContentOutlinePage.class) return new OutlinePage(new TreeViewer());
        if (type == ZoomManager.class) return ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();

        return super.getAdapter(type);

    }

    protected KeyHandler getCommonKeyHandler() {
        if (keyHandler == null) {
            keyHandler = new KeyHandler();
            keyHandler.put(KeyStroke.getPressed('\177', 127, 0), getActionRegistry().getAction(ActionFactory.DELETE.getId()));
            keyHandler.put(KeyStroke.getPressed('c', SWT.CTRL), getActionRegistry().getAction(ActionFactory.COPY.getId()));
            keyHandler.put(KeyStroke.getPressed('v', SWT.CTRL), getActionRegistry().getAction(ActionFactory.PASTE.getId()));
            keyHandler.put(KeyStroke.getPressed('C', SWT.CTRL), getActionRegistry().getAction(ActionFactory.COPY.getId()));
            keyHandler.put(KeyStroke.getPressed('V', SWT.CTRL), getActionRegistry().getAction(ActionFactory.PASTE.getId()));
        }
        return keyHandler;
    }

    @Override
    protected GraphicalViewer getGraphicalViewer() {
        return super.getGraphicalViewer();
    }

    /**
     * @return
     */
    Schema getSchema() {
        if (schema == null) schema = new Schema();
        return schema;
    }

    SelectionSynchronizer getSynchronizer() {
        return getSelectionSynchronizer();
    }

    public void gotoMarker( final IMarker imarker ) {

    }

    @Override
    protected void initializeGraphicalViewer() {
        getGraphicalViewer().setContents(getSchema());
    }

    @Override
    public boolean isDirty() {
        return isSaveOnCloseNeeded();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return getCommandStack().isDirty();
    }

    protected boolean performSaveAs() {
        final SaveAsDialog dialog = new SaveAsDialog(getSite().getWorkbenchWindow().getShell());
        dialog.setOriginalFile(((IFileEditorInput)getEditorInput()).getFile());
        dialog.open();
        final IPath path = dialog.getResult();

        if (path == null) return false;

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IFile file = workspace.getRoot().getFile(path);

        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {
                try {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    createOutputStream(out);
                    file.create(new ByteArrayInputStream(out.toByteArray()), true, monitor);
                    out.close();
                } catch (final Exception e) {
                    SQLExplorerPlugin.error("Error performing save as", e);//$NON-NLS-1$
                }
            }
        };

        try {
            new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell()).run(false, true, op);
            setInput(new FileEditorInput(file));
            getCommandStack().markSaveLocation();
        } catch (final Exception e) {
            SQLExplorerPlugin.error("Error performing save as", e);//$NON-NLS-1$
        }
        return true;
    }

    private boolean savePreviouslyNeeded() {
        return savePreviouslyNeeded;
    }

    @Override
    public void setInput( final IEditorInput input ) {
        superSetInput(input);

        final IFile file = ((IFileEditorInput)input).getFile();
        try {
            final InputStream is = file.getContents(false);
            final ObjectInputStream ois = new ObjectInputStream(is);
            setSchema((Schema)ois.readObject());
            ois.close();
        } catch (final Exception e) {
            // e.printStackTrace();
        }
    }

    private void setSavePreviouslyNeeded( final boolean value ) {
        savePreviouslyNeeded = value;
    }

    public void setSchema( final Schema schema ) {
        this.schema = schema;
    }

    @Override
    protected void setSite( final IWorkbenchPartSite iworkbenchpartsite ) {
        super.setSite(iworkbenchpartsite);
        getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
    }

    protected void superSetInput( final IEditorInput input ) {
        if (getEditorInput() != null) {
            final IFile file = ((FileEditorInput)getEditorInput()).getFile();
            file.getWorkspace().removeResourceChangeListener(resourceListener);
        }

        super.setInput(input);

        if (getEditorInput() != null) {
            final IFile file = ((FileEditorInput)getEditorInput()).getFile();
            file.getWorkspace().addResourceChangeListener(resourceListener);
        } else setPartName(Messages.getString("SchemaEditor.Schema_Viewer__2")); //$NON-NLS-1$
    }

    protected void updateMarkers( final IResource iresource,
                                  final IProgressMonitor iprogressmonitor ) {

    }

    protected class OutlinePage extends ContentOutlinePage implements IAdaptable {

        private PageBook pageBook;

        private Control outline;

        private Canvas overview;

        private IAction showOutlineAction, showOverviewAction;

        static final int ID_OUTLINE = 0;

        static final int ID_OVERVIEW = 1;

        private boolean overviewInitialized;

        private Thumbnail thumbnail;

        private final SchemaTreePartFactory schemaTreePartFactory;

        public OutlinePage( final EditPartViewer editpartviewer ) {
            super(editpartviewer);
            schemaTreePartFactory = new SchemaTreePartFactory();
        }

        protected void configureOutlineViewer() {
            getViewer().setEditDomain(SchemaEditor.getEditDomain(SchemaEditor.this));
            getViewer().setEditPartFactory(schemaTreePartFactory);
            // SchemaEditorContextMenuProvider schemaCMP = new SchemaEditorContextMenuProvider(getViewer(),
            // SchemaEditor.getActionRegistry(SchemaEditor.this),SchemaEditor.this);
            // getViewer().setContextMenu(schemaCMP);
            // getSite().registerContextMenu("net.sf.ProvaGef.edit.outline.contextmenu", schemaCMP,
            // getSite().getSelectionProvider());
            getViewer().setKeyHandler(getCommonKeyHandler());
            final IToolBarManager itoolbarmanager = getSite().getActionBars().getToolBarManager();

            showOutlineAction = new Action() {
                @Override
                public void run() {
                    showPage(ID_OUTLINE);
                }
            };
            showOutlineAction.setImageDescriptor(ImageDescriptor.createFromURL(SqlexplorerImages.getOutline()));
            itoolbarmanager.add(showOutlineAction);
            showOverviewAction = new Action() {
                @Override
                public void run() {
                    showPage(ID_OVERVIEW);
                }
            };
            showOverviewAction.setImageDescriptor(ImageDescriptor.createFromURL(SqlexplorerImages.getOverview()));
            itoolbarmanager.add(showOverviewAction);
            showPage(ID_OUTLINE);
        }

        @Override
        public void createControl( final Composite composite ) {
            pageBook = new PageBook(composite, 0);
            outline = getViewer().createControl(pageBook);
            overview = new Canvas(pageBook, 0);
            pageBook.showPage(outline);
            configureOutlineViewer();
            hookOutlineViewer();
            initializeOutlineViewer();
        }

        @Override
        public void dispose() {
            unhookOutlineViewer();
            // schemaTreePartFactory.dispose();
            super.dispose();
        }

        public Object getAdapter( final Class key ) {
            if (key == org.eclipse.gef.editparts.ZoomManager.class) return ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
            return null;
        }

        @Override
        public Control getControl() {
            return pageBook;
        }

        protected void hookOutlineViewer() {
            getSynchronizer().addViewer(getViewer());
        }

        @Override
        public void init( final IPageSite ipagesite ) {
            super.init(ipagesite);
            final ActionRegistry actionregistry = SchemaEditor.getActionRegistry(SchemaEditor.this);
            final IActionBars iactionbars = ipagesite.getActionBars();
            String s = "undo"; //$NON-NLS-1$
            iactionbars.setGlobalActionHandler(s, actionregistry.getAction(s));
            s = "redo"; //$NON-NLS-1$
            iactionbars.setGlobalActionHandler(s, actionregistry.getAction(s));
            s = "delete"; //$NON-NLS-1$
            iactionbars.setGlobalActionHandler(s, actionregistry.getAction(s));
            iactionbars.updateActionBars();
        }

        protected void initializeOutlineViewer() {
            getViewer().setContents(getSchema());
        }

        protected void initializeOverview() {
            final LightweightSystem lws = new LightweightSystem(overview);
            final RootEditPart rep = getGraphicalViewer().getRootEditPart();
            if (rep instanceof ScalableFreeformRootEditPart) {
                final ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart)rep;
                thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());
                thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
                lws.setContents(thumbnail);
            }
        }

        protected void showPage( final int id ) {
            if (id == ID_OUTLINE) {
                showOutlineAction.setChecked(true);
                showOverviewAction.setChecked(false);
                pageBook.showPage(outline);
                if (thumbnail != null) thumbnail.setVisible(false);
            } else if (id == ID_OVERVIEW) {
                if (!overviewInitialized) initializeOverview();
                showOutlineAction.setChecked(false);
                showOverviewAction.setChecked(true);
                pageBook.showPage(overview);
                thumbnail.setVisible(true);
            }
        }

        protected void unhookOutlineViewer() {
            getSynchronizer().removeViewer(getViewer());
        }
    }

    class ResourceTracker implements IResourceChangeListener, IResourceDeltaVisitor {
        public void resourceChanged( final IResourceChangeEvent event ) {
            final IResourceDelta delta = event.getDelta();
            try {
                if (delta != null) delta.accept(this);
            } catch (final CoreException exception) {
                // What should be done here?
            }
        }

        public boolean visit( final IResourceDelta delta ) {
            if (delta == null || !delta.getResource().equals(((FileEditorInput)getEditorInput()).getFile())) return true;

            if (delta.getKind() == IResourceDelta.REMOVED) if ((IResourceDelta.MOVED_TO & delta.getFlags()) == 0) { // if the file
                                                                                                                    // was deleted
                // NOTE: The case where an open, unsaved file is deleted is being handled by the
                // PartListener added to the Workbench in the initialize() method.
                if (!isDirty()) closeEditor(false);
            } else { // else if it was moved or renamed
                final IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(delta.getMovedToPath());
                final Display display = getSite().getShell().getDisplay();
                display.asyncExec(new Runnable() {
                    public void run() {
                        superSetInput(new FileEditorInput(newFile));
                    }
                });
            }
            return false;
        }
    }
}

class TableFactory implements CreationFactory {

    private TableNode tn = null;

    public Object getNewObject() {
        try {
            final TableAdapter tad = new TableAdapter(tn);
            final Table tb = tad.adapt();
            return tb;
        } catch (final Throwable e) {
        }
        return null;

    }

    public Object getObjectType() {
        return Table.class;
    }

    public void setTableNode( final TableNode tn ) {
        this.tn = tn;
    }
}
