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
package net.sourceforge.sqlexplorer.plugin.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.DBView;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModel;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModelChangedListener;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import net.sourceforge.sqlexplorer.sqlpanel.SQLTextViewer;
import net.sourceforge.sqlexplorer.sqlpanel.actions.ClearTextAction;
import net.sourceforge.sqlexplorer.sqlpanel.actions.ExecSQLAction;
import net.sourceforge.sqlexplorer.sqlpanel.actions.OpenFileAction;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class SQLEditor extends TextEditor implements IResourceChangeListener {
    private MouseClickListener mcl = new MouseClickListener();

    @Override
    protected void editorContextMenuAboutToShow( IMenuManager menu ) {
        super.editorContextMenuAboutToShow(menu);

        IContributionItem[] iContributionItems = SQLExplorerPlugin.getDefault().pluginManager.getEditorContextMenuActions(this);
        if (iContributionItems != null && iContributionItems.length > 0) {
            menu.add(new Separator());

            for (int i = 0; i < iContributionItems.length; i++) {
                menu.add(iContributionItems[i]);
            }
        }

        // DEFECT 21637 - the preference action wasn't initializeing the Preference page properly. Let's just remove it.
        // DEFECT 21636 - the Shift Left and Shift Right actions don't work either.

        IContributionItem[] allItems = menu.getItems();
        IContributionItem[] itemsToRemove = new IContributionItem[3];

        for (int i = 0; i < allItems.length; i++) {
            if (allItems[i] instanceof ActionContributionItem) {
                IAction theAction = ((ActionContributionItem)allItems[i]).getAction();
                if (theAction.getText().indexOf("Preference") > -1) {
                    itemsToRemove[0] = allItems[i];
                } else if (ITextEditorActionDefinitionIds.SHIFT_LEFT.equals(theAction.getActionDefinitionId())) {
                    itemsToRemove[1] = allItems[i];
                } else if (ITextEditorActionDefinitionIds.SHIFT_RIGHT.equals(theAction.getActionDefinitionId())) {
                    itemsToRemove[2] = allItems[i];
                }
            }
        }

        for (int i = 0; i < itemsToRemove.length; ++i) {
            if (itemsToRemove[i] != null) {
                menu.remove(itemsToRemove[i]);
            }
        }
    }

    private IPartListener partListener;
    IPreferenceStore store;
    public SQLTextViewer sqlTextViewer;
    ExecSQLAction execSQLAction;
    StatusLineManager statusMgr;
    SQLEditorSessionListener listener;
    private OpenFileAction openFileAction;
    private ClearTextAction clearTextAction;

    String closedConnectionName;

    SessionTreeNode sessionTreeNode;
    Combo catalogCombo;
    SessionTreeModel stm = SQLExplorerPlugin.getDefault().stm;

    public SQLEditor() {

        store = SQLExplorerPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
    }

    ISourceViewer getViewer() {
        return getSourceViewer();
    }

    @Override
    protected ISourceViewer createSourceViewer( Composite parent,
                                                IVerticalRuler ruler,
                                                int style ) {
        style |= SWT.WRAP;
        parent.setLayout(new FillLayout());
        Composite myParent = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
        myParent.setLayout(layout);

        ToolBarManager toolBarMgr = new ToolBarManager(SWT.FLAT);
        ToolBar toolBar = toolBarMgr.createControl(myParent);

        GridData gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        gid.verticalAlignment = GridData.BEGINNING;
        gid.heightHint = 25;
        toolBar.setLayoutData(gid);
        execSQLAction = new ExecSQLAction(this, store.getInt(IConstants.MAX_SQL_ROWS));
        openFileAction = new OpenFileAction(this);
        // saveFileAction=new SaveFileAction(this);
        clearTextAction = new ClearTextAction(this);

        toolBarMgr.add(execSQLAction);
        // toolBarMgr.add(saveFileAction);
        toolBarMgr.add(openFileAction);
        toolBarMgr.add(clearTextAction);
        IAction[] toolActions = SQLExplorerPlugin.getDefault().pluginManager.getEditorToolbarActions(this);
        if (toolActions != null) {
            for (int i = 0; i < toolActions.length; i++)
                toolBarMgr.add(toolActions[i]);
        }
        toolBarMgr.update(true);
        // ToolItem sep1 = new ToolItem (toolBar, SWT.SEPARATOR);
        ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);
        catalogCombo = new Combo(toolBar, SWT.READ_ONLY);
        listener = new SQLEditorSessionListener(this);
        stm.addListener(listener);
        updateSessionStatus(stm);
        catalogCombo.setToolTipText("Choose Catalog");
        catalogCombo.setSize(200, catalogCombo.getSize().y);
        catalogCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent arg0 ) {
                int selIndex = catalogCombo.getSelectionIndex();
                String newCat = catalogCombo.getItem(selIndex);
                if (sessionTreeNode != null) {
                    try {
                        sessionTreeNode.setCatalog(newCat);
                    } catch (Exception e1) {
                        SQLExplorerPlugin.error("Error changing catalog", e1);
                    }
                }
            }

            public void widgetDefaultSelected( SelectionEvent arg0 ) {
            }
        });
        sep2.setWidth(catalogCombo.getSize().x);
        sep2.setControl(catalogCombo);

        toolBar.pack();

        toolBar.update();

        gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;

        sqlTextViewer = new SQLTextViewer(myParent, style, store, null, ruler);
        sqlTextViewer.getControl().setLayoutData(gid);
        sqlTextViewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey( VerifyEvent event ) {
                if (event.stateMask == SWT.CTRL && event.keyCode == 13) {
                    event.doit = false;
                    execSQLAction.run();
                }
            }
        });
        statusMgr = new StatusLineManager();
        statusMgr.createControl(myParent);
        gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        gid.verticalAlignment = GridData.BEGINNING;
        statusMgr.getControl().setLayoutData(gid);
        // sourceViewer= new SourceViewer(myParent, ruler, fOverviewRuler, isOverviewRulerVisible(), style);
        // sourceViewer.getControl().setLayoutData(gid);
        // new VerticalRuler(0)
        myParent.layout();
        IDocument dc = new Document();
        sqlTextViewer.setDocument(dc);
        if (sessionTreeNode != null) setNewDictionary(sessionTreeNode.getDictionary());
        partListener = new IPartListener() {
            public void partActivated( IWorkbenchPart part ) {
                if (part == SQLEditor.this) {
                    if (sessionTreeNode != null) {
                        if (sessionTreeNode.supportsCatalogs()) {
                            String catalog = sessionTreeNode.getCatalog();
                            String catalogs[] = catalogCombo.getItems();
                            for (int i = 0; i < catalogs.length; i++) {
                                if (catalog.equals(catalogs[i])) {
                                    catalogCombo.select(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            public void partBroughtToTop( IWorkbenchPart part ) {
            }

            public void partClosed( IWorkbenchPart part ) {
            }

            public void partDeactivated( IWorkbenchPart part ) {
            }

            public void partOpened( IWorkbenchPart part ) {
            }
        };
        getEditorSite().getPage().addPartListener(partListener);
        mcl.install(sqlTextViewer);

        return sqlTextViewer;

    }

    public void setNewDictionary( Dictionary dictionary ) {
        if (sqlTextViewer != null) {
            sqlTextViewer.setNewDictionary(dictionary);
            sqlTextViewer.refresh();
        }
        // setSourceViewerConfiguration(new SQLSourceViewerConfiguration(new SQLTextTools(store,dictionary)));
        // sourceViewer.refresh();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        super.init(site, input);
        if (input instanceof SQLEditorInput) {
            SQLEditorInput sqlInput = (SQLEditorInput)input;
            sessionTreeNode = sqlInput.getSessionNode();
            if (sessionTreeNode != null) {
                setNewDictionary(sessionTreeNode.getDictionary());
                updateSessionStatus(stm);
            }
            
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        }
    }

    public String getSQLToBeExecuted() {
        String sql = sqlTextViewer.getTextWidget().getSelectionText();
        if (sql == null || sql.trim().length() == 0) {
            sql = sqlTextViewer.getTextWidget().getText();
            /*String sep = System.getProperty("line.separator"); //$NON-NLS-1$
            int iStartIndex = 0;
            int iEndIndex = sql.length();

            int iCaretPos = sqlTextViewer.getTextWidget().getCaretOffset();

            int iIndex = sql.lastIndexOf(sep+sep,iCaretPos);
            if(iIndex >0) 
            	iStartIndex = iIndex;
            iIndex = sql.indexOf(sep+sep,iCaretPos);
            if(iIndex >0) 
            	iEndIndex = iIndex;

            sql = sql.substring(iStartIndex, iEndIndex).trim();*/
        }
        System.out.println("Exec " + sql);
        return sql != null ? sql : ""; //$NON-NLS-1$
    }

    /**
     * @return
     */
    public SessionTreeNode getSessionTreeNode() {
        return sessionTreeNode;
    }

    public void updateSessionStatus( final SessionTreeModel stm ) {
        // Defect 21949 requires null checks and isDiposed() checks to prevent errors during exit/shutdown
        if (catalogCombo != null && catalogCombo.isDisposed()) {
            return;
        }
        this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                SessionTreeNode[] sessionNodes = stm.getRoot().getSessionTreeNodes();
                boolean found = false;

                for (int i = 0; i < sessionNodes.length; i++) {
                    if (sessionTreeNode == sessionNodes[i]) {
                        // found exact session
                        found = true;
                    } else if (sessionTreeNode != null) {
                        // another session with same alias
                        if (sessionTreeNode.getAlias().getName().equals(sessionNodes[i].getAlias().getName())) {
                            sessionTreeNode = sessionNodes[i];
                            found = true;
                        }
                    } else if (closedConnectionName != null) {
                        // closed session name matches a new session name
                        if (closedConnectionName.equals(sessionNodes[i].getAlias().getName())) {
                            sessionTreeNode = sessionNodes[i];
                            found = true;
                        }
                    }

                    if (found) {
                        closedConnectionName = null;
                        break;
                    }

                }
                if (!catalogCombo.isDisposed()) {
                    if (found) {
                        if (sessionTreeNode.supportsCatalogs()) {
                            catalogCombo.setVisible(true);
                            String catalogs[] = sessionTreeNode.getCatalogs();
                            String currentCatalog = sessionTreeNode.getCatalog();

                            for (int i = 0; i < catalogs.length; i++) {
                                catalogCombo.add(catalogs[i]);

                                if (currentCatalog.equals(catalogs[i])) {
                                    catalogCombo.select(catalogCombo.getItemCount() - 1);
                                }
                            }
                        } else {
                            catalogCombo.setVisible(false);
                        }
                    } else {
                        // save old session/connection name in case a new one is opened
                        if (sessionTreeNode != null) {
                            closedConnectionName = sessionTreeNode.getAlias().getName();
                        }

                        sessionTreeNode = null;
                        setNewDictionary(null);
                        catalogCombo.setVisible(false);
                    }

                    execSQLAction.setEnabled(found);
                }
            }
        });
    }

    /**
     * @param txt
     */
    public void setText( String txt ) {
        // reuse existing document so that undo/redo stack is not lost
        IDocument doc = sqlTextViewer.getDocument();

        if (doc == null) {
            doc = new Document();
            sqlTextViewer.setDocument(doc);
        }

        doc.set(txt);

        if (sessionTreeNode != null) setNewDictionary(sessionTreeNode.getDictionary());

    }

    /**
     * @param str
     */
    public void loadFile( String file ) {
        FileReader fr = null;
        BufferedReader in = null;
        try {
            fr = new FileReader(file);
            in = new BufferedReader(fr);
            String str;
            StringBuffer all = new StringBuffer();
            String delimiter = sqlTextViewer.getTextWidget().getLineDelimiter();
            while ((str = in.readLine()) != null) {
                all.append(str);
                all.append(delimiter);
            }
            sqlTextViewer.setDocument(new Document(all.toString()));

        } catch (Throwable e) {
            SQLExplorerPlugin.error("Error loading document", e); //$NON-NLS-1$
        } finally {
            try {
                fr.close();
            } catch (java.io.IOException e) {
            }
            try {
                in.close();
            } catch (java.io.IOException e) {
            }

        }

    }

    /**
	 * 
	 */
    public void clearText() {
        sqlTextViewer.clearText();

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        if (partListener != null) getEditorSite().getPage().removePartListener(partListener);
        stm.removeListener(listener);
        mcl.uninstall();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    public void setMessage( String s ) {
        statusMgr.setMessage(s);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    @Override
    protected void createActions() {

        super.createActions();
        Action action = new Action("Auto-Completion") {
            @Override
            public void run() {
                sqlTextViewer.showAssistance();
            }
        };

        // This action definition is associated with the accelerator Ctrl+Space
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action); //$NON-NLS-1$
        execSQLAction.setActionDefinitionId("net.sourceforge.sqlexplorer.sqlrun");
        setAction("SQL Run", execSQLAction);

    }

    /**
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
     * @since 4.3
     */
    @Override
    public boolean isDirty() {
        // only dirty if file associated with editor
        boolean result = false;
        IEditorInput input = getEditorInput();

        if ((!(input instanceof SQLEditorInput)) || (((SQLEditorInput)input).getFile() != null)) {
            result = super.isDirty();
        }

        return result;
    }

    class MouseClickListener
        implements KeyListener, MouseListener, MouseMoveListener, FocusListener, PaintListener, IPropertyChangeListener,
        IDocumentListener, ITextInputListener {
        private boolean fActive;
        IDbModel activeTableNode;
        private ISourceViewer sourceViewer;
        /** The currently active style range. */
        private IRegion fActiveRegion;
        /** The currently active style range as position. */
        private Position fRememberedPosition;
        /** The hand cursor. */
        private Cursor fCursor;

        /** The link color. */
        private Color fColor;
        /** The key modifier mask. */
        private int fKeyModifierMask = SWT.CTRL;

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
         */
        public void keyPressed( KeyEvent event ) {
            if (fActive) {
                deactivate();
                return;
            }

            if (event.keyCode != fKeyModifierMask) {
                deactivate();
                return;
            }

            fActive = true;

        }

        public void deactivate() {
            deactivate(false);
        }

        public void deactivate( boolean redrawAll ) {
            if (!fActive) return;

            repairRepresentation(redrawAll);
            fActive = false;
        }

        private void repairRepresentation( boolean redrawAll ) {

            if (fActiveRegion == null) return;

            if (sourceViewer != null) {
                resetCursor(sourceViewer);

                int offset = fActiveRegion.getOffset();
                int length = fActiveRegion.getLength();

                // remove style
                if (!redrawAll && sourceViewer instanceof ITextViewerExtension2) ((ITextViewerExtension2)sourceViewer).invalidateTextPresentation(offset,
                                                                                                                                                  length);
                else sourceViewer.invalidateTextPresentation();

                // remove underline
                if (sourceViewer instanceof ITextViewerExtension5) {
                    ITextViewerExtension5 extension = (ITextViewerExtension5)sourceViewer;
                    offset = extension.modelOffset2WidgetOffset(offset);
                } else {
                    offset -= sourceViewer.getVisibleRegion().getOffset();
                }

                StyledText text = sourceViewer.getTextWidget();
                try {
                    text.redrawRange(offset, length, true);
                } catch (IllegalArgumentException x) {
                    x.printStackTrace();
                    // JavaPlugin.log(x);
                }
            }

            fActiveRegion = null;
        }

        private void resetCursor( ISourceViewer viewer ) {
            StyledText text = viewer.getTextWidget();
            if (text != null && !text.isDisposed()) text.setCursor(null);

            if (fCursor != null) {
                fCursor.dispose();
                fCursor = null;
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
         */
        public void keyReleased( KeyEvent e ) {
            if (!fActive) return;

            deactivate();

        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseDoubleClick( MouseEvent e ) {
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseDown( MouseEvent event ) {
            if (!fActive) return;

            if (event.stateMask != fKeyModifierMask) {
                deactivate();
                return;
            }

            if (event.button != 1) {
                deactivate();
                return;
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseUp( MouseEvent e ) {
            if (!fActive) return;

            if (e.button != 1) {
                deactivate();
                return;
            }

            boolean wasActive = fCursor != null;

            deactivate();

            if (wasActive) {
                // activeTableNode.get
                //IAction action= getAction("OpenEditor");  //$NON-NLS-1$
                // if (action != null)
                // action.run();
                BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        try {
                            DBView dbView = (DBView)SQLEditor.this.getEditorSite().getWorkbenchWindow().getActivePage().findView("net.sourceforge.sqlexplorer.plugin.views.DBView");
                            if (dbView != null) {
                                SQLEditor.this.getEditorSite().getWorkbenchWindow().getActivePage().bringToTop(dbView);
                                dbView.setInput(sessionTreeNode);
                                dbView.tryToSelect(sessionTreeNode, activeTableNode);
                            }

                        } catch (Exception e1) {
                            SQLExplorerPlugin.error("Error selecting table", e1);
                        }
                    }
                });

            }

        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
         */
        public void mouseMove( MouseEvent event ) {
            if (event.widget instanceof Control && !((Control)event.widget).isFocusControl()) {
                deactivate();
                return;
            }

            if (!fActive) {
                if (event.stateMask != fKeyModifierMask) return;
                // modifier was already pressed
                fActive = true;
            }

            if (sourceViewer == null) {
                deactivate();
                return;
            }

            StyledText text = sourceViewer.getTextWidget();
            if (text == null || text.isDisposed()) {
                deactivate();
                return;
            }

            if ((event.stateMask & SWT.BUTTON1) != 0 && text.getSelectionCount() != 0) {
                deactivate();
                return;
            }

            IRegion region = getCurrentTextRegion(sourceViewer);
            if (region == null || region.getLength() == 0) {
                repairRepresentation();
                return;
            }

            highlightRegion(sourceViewer, region);
            activateCursor(sourceViewer);
        }

        private void activateCursor( ISourceViewer viewer ) {
            StyledText text = viewer.getTextWidget();
            if (text == null || text.isDisposed()) return;
            Display display = text.getDisplay();
            if (fCursor == null) fCursor = new Cursor(display, SWT.CURSOR_HAND);
            text.setCursor(fCursor);
        }

        private void repairRepresentation() {
            repairRepresentation(false);
        }

        private IRegion selectWord( IDocument document,
                                    int anchor ) {

            try {
                int offset = anchor;
                char c;

                while (offset >= 0) {
                    c = document.getChar(offset);
                    if (!Character.isJavaIdentifierPart(c)) break;
                    --offset;
                }

                int start = offset;

                offset = anchor;
                int length = document.getLength();

                while (offset < length) {
                    c = document.getChar(offset);
                    if (!Character.isJavaIdentifierPart(c)) break;
                    ++offset;
                }

                int end = offset;

                if (start == end) return new Region(start, 0);
                return new Region(start + 1, end - start - 1);

            } catch (BadLocationException x) {
                return null;
            }
        }

        private IRegion getCurrentTextRegion( ISourceViewer viewer ) {
            if (viewer == null) return null;
            Dictionary dictionary = ((SQLTextViewer)viewer).dictionary;
            if (dictionary == null) return null;
            int offset = getCurrentTextOffset(viewer);
            if (offset == -1) return null;
            // IJavaElement input= SelectionConverter.getInput(JavaEditor.this);
            // if (input == null)
            // return null;

            try {

                // IJavaElement[] elements= null;
                // synchronized (input) {
                // elements= ((ICodeAssist) input).codeSelect(offset, 0);
                // }

                // if (elements == null || elements.length == 0)
                // return null;

                IRegion reg = selectWord(viewer.getDocument(), offset);
                if (reg == null) return null;
                String selection = viewer.getDocument().get(reg.getOffset(), reg.getLength());
                if (selection == null) return null;
                Object obj = dictionary.getByTableName(selection.toLowerCase());

                if (obj == null) return null;
                if (!(obj instanceof ArrayList)) return null;
                ArrayList ls = (ArrayList)obj;
                if (ls.isEmpty()) return null;
                Object node = ((ArrayList)obj).get(0);
                if (node instanceof TableNode) activeTableNode = (TableNode)node;
                else return null;
                return reg;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private int getCurrentTextOffset( ISourceViewer viewer ) {

            try {
                StyledText text = viewer.getTextWidget();
                if (text == null || text.isDisposed()) return -1;

                Display display = text.getDisplay();
                Point absolutePosition = display.getCursorLocation();
                Point relativePosition = text.toControl(absolutePosition);

                int widgetOffset = text.getOffsetAtLocation(relativePosition);
                if (viewer instanceof ITextViewerExtension5) {
                    ITextViewerExtension5 extension = (ITextViewerExtension5)viewer;
                    return extension.widgetOffset2ModelOffset(widgetOffset);
                }
                return widgetOffset + viewer.getVisibleRegion().getOffset();

            } catch (IllegalArgumentException e) {
                return -1;
            }
        }

        private void highlightRegion( ISourceViewer viewer,
                                      IRegion region ) {

            if (region.equals(fActiveRegion)) return;

            repairRepresentation();

            StyledText text = viewer.getTextWidget();
            if (text == null || text.isDisposed()) return;

            // highlight region
            int offset = 0;
            int length = 0;

            if (viewer instanceof ITextViewerExtension5) {
                ITextViewerExtension5 extension = (ITextViewerExtension5)viewer;
                IRegion widgetRange = extension.modelRange2WidgetRange(region);
                if (widgetRange == null) return;

                offset = widgetRange.getOffset();
                length = widgetRange.getLength();

            } else {
                offset = region.getOffset() - viewer.getVisibleRegion().getOffset();
                length = region.getLength();
            }

            StyleRange oldStyleRange = text.getStyleRangeAtOffset(offset);
            Color foregroundColor = fColor;
            Color backgroundColor = oldStyleRange == null ? text.getBackground() : oldStyleRange.background;
            StyleRange styleRange = new StyleRange(offset, length, foregroundColor, backgroundColor);
            text.setStyleRange(styleRange);

            // underline
            text.redrawRange(offset, length, true);

            fActiveRegion = region;
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
         */
        public void focusGained( FocusEvent e ) {
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
         */
        public void focusLost( FocusEvent e ) {
            deactivate();

        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
         */
        public void paintControl( PaintEvent event ) {
            if (fActiveRegion == null) return;

            if (sourceViewer == null) return;

            StyledText text = sourceViewer.getTextWidget();
            if (text == null || text.isDisposed()) return;

            int offset = 0;
            int length = 0;

            if (sourceViewer instanceof ITextViewerExtension5) {

                ITextViewerExtension5 extension = (ITextViewerExtension5)sourceViewer;
                IRegion widgetRange = extension.modelRange2WidgetRange(new Region(offset, length));
                if (widgetRange == null) return;

                offset = widgetRange.getOffset();
                length = widgetRange.getLength();

            } else {

                IRegion region = sourceViewer.getVisibleRegion();
                if (!includes(region, fActiveRegion)) return;

                offset = fActiveRegion.getOffset() - region.getOffset();
                length = fActiveRegion.getLength();
            }

            // support for bidi
            Point minLocation = getMinimumLocation(text, offset, length);
            Point maxLocation = getMaximumLocation(text, offset, length);

            int x1 = minLocation.x;
            int x2 = minLocation.x + maxLocation.x - minLocation.x - 1;
            int y = minLocation.y + text.getLineHeight() - 1;

            GC gc = event.gc;
            if (fColor != null && !fColor.isDisposed()) gc.setForeground(fColor);
            gc.drawLine(x1, y, x2, y);

        }

        private boolean includes( IRegion region,
                                  IRegion position ) {
            return position.getOffset() >= region.getOffset()
                   && position.getOffset() + position.getLength() <= region.getOffset() + region.getLength();
        }

        private Point getMinimumLocation( StyledText text,
                                          int offset,
                                          int length ) {
            Point minLocation = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

            for (int i = 0; i <= length; i++) {
                Point location = text.getLocationAtOffset(offset + i);

                if (location.x < minLocation.x) minLocation.x = location.x;
                if (location.y < minLocation.y) minLocation.y = location.y;
            }

            return minLocation;
        }

        private Point getMaximumLocation( StyledText text,
                                          int offset,
                                          int length ) {
            Point maxLocation = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

            for (int i = 0; i <= length; i++) {
                Point location = text.getLocationAtOffset(offset + i);

                if (location.x > maxLocation.x) maxLocation.x = location.x;
                if (location.y > maxLocation.y) maxLocation.y = location.y;
            }

            return maxLocation;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange( PropertyChangeEvent event ) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentAboutToBeChanged( DocumentEvent event ) {
            if (fActive && fActiveRegion != null) {
                fRememberedPosition = new Position(fActiveRegion.getOffset(), fActiveRegion.getLength());
                try {
                    event.getDocument().addPosition(fRememberedPosition);
                } catch (BadLocationException x) {
                    fRememberedPosition = null;
                }
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentChanged( DocumentEvent event ) {
            if (fRememberedPosition != null && !fRememberedPosition.isDeleted()) {
                event.getDocument().removePosition(fRememberedPosition);
                fActiveRegion = new Region(fRememberedPosition.getOffset(), fRememberedPosition.getLength());
            }
            fRememberedPosition = null;

            if (sourceViewer != null) {
                StyledText widget = sourceViewer.getTextWidget();
                if (widget != null && !widget.isDisposed()) {
                    widget.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            deactivate();
                        }
                    });
                }
            }

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged( IDocument oldInput,
                                                   IDocument newInput ) {
            if (oldInput == null) return;
            deactivate();
            oldInput.removeDocumentListener(this);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged( IDocument oldInput,
                                          IDocument newInput ) {
            if (newInput == null) return;
            newInput.addDocumentListener(this);
        }

        public void install( ISourceViewer sourceViewer ) {

            this.sourceViewer = sourceViewer;
            if (sourceViewer == null) return;

            StyledText text = sourceViewer.getTextWidget();
            if (text == null || text.isDisposed()) return;

            updateColor(sourceViewer);

            sourceViewer.addTextInputListener(this);

            IDocument document = sourceViewer.getDocument();
            if (document != null) document.addDocumentListener(this);

            text.addKeyListener(this);
            text.addMouseListener(this);
            text.addMouseMoveListener(this);
            text.addFocusListener(this);
            text.addPaintListener(this);
        }

        public void uninstall() {

            if (fColor != null) {
                fColor.dispose();
                fColor = null;
            }

            if (fCursor != null) {
                fCursor.dispose();
                fCursor = null;
            }

            if (sourceViewer == null) return;

            sourceViewer.removeTextInputListener(this);

            IDocument document = sourceViewer.getDocument();
            if (document != null) document.removeDocumentListener(this);

            StyledText text = sourceViewer.getTextWidget();
            if (text == null || text.isDisposed()) return;

            text.removeKeyListener(this);
            text.removeMouseListener(this);
            text.removeMouseMoveListener(this);
            text.removeFocusListener(this);
            text.removePaintListener(this);
        }

        private void updateColor( ISourceViewer viewer ) {
            if (fColor != null) fColor.dispose();

            StyledText text = viewer.getTextWidget();
            if (text == null || text.isDisposed()) return;

            Display display = text.getDisplay();
            fColor = new Color(display, new RGB(0, 0, 255));
        }

    }

    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            if (getSite().getShell().isDisposed()) {
                return;
            }

            final Display display = getSite().getShell().getDisplay();
            IResourceDelta delta = event.getDelta();

            if (delta != null) {
                try {
                    delta.accept(new IResourceDeltaVisitor() {

                        @Override
                        public boolean visit( IResourceDelta delta ) {
                            String name = null;
                            
                            if (closedConnectionName != null) {
                                name = new File(closedConnectionName).getName();
                            } else {
                                return false;
                            }
                            
                            if (name.equals(delta.getResource().getName()) && (getSessionTreeNode() == null)
                                && (delta.getKind() & IResourceDelta.REMOVED) != 0) {
                                
                                // close the editor
                                display.asyncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (display.isDisposed()) {
                                            return;
                                        }

                                        getEditorSite().getWorkbenchWindow().getActivePage().closeEditor(SQLEditor.this, false);
                                    }
                                });

                                return false;
                            }

                            return true;
                        }
                    });
                } catch (CoreException e) {
                    SQLExplorerPlugin.error("Error processing ResourceChangeEvent", e);
                }
            }
        }
    }

}

class SQLEditorSessionListener implements SessionTreeModelChangedListener {
    SQLEditor editor;

    public SQLEditorSessionListener( SQLEditor editor ) {
        this.editor = editor;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModelChangedListener#modelChanged()
     */
    public void modelChanged( SessionTreeNode nd ) {
        editor.updateSessionStatus(SQLExplorerPlugin.getDefault().stm);

    }
}
