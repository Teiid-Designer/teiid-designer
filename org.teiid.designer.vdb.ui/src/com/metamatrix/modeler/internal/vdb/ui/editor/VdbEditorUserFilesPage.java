/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * UserFiles page
 * 
 * @since 5.3.3
 */
public final class VdbEditorUserFilesPage extends EditorPart
    implements CoreStringUtil.Constants, VdbUiConstants, IRevertable, IGotoMarker {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditorUserFilesPage.class);

    private static final int COLUMNS = 1;
    private static final String TITLE = getString("title"); //$NON-NLS-1$

    /**
     * @since 5.3.3
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;

    private FontMetrics fontMetrics;
    VdbEditorUserFilesComposite userFilesPanel;
    private IResourceChangeListener resourceChangeListener;
    boolean isEnabled = false;

    /**
     * @since 5.3.3
     */
    VdbEditorUserFilesPage( final VdbEditor editor ) {
        this.editor = editor;
        // Access Eclipse Dialog class to get static initializer to run
        Dialog.class.getName();
    }

    protected int convertHeightInCharsToPixels( final int chars ) {
        // test for failure to initialize for backward compatibility
        if (fontMetrics == null) return 0;
        return Dialog.convertHeightInCharsToPixels(fontMetrics, chars);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.3.3
     */
    @Override
    public void createPartControl( final Composite parent ) {
        // Compute and store a font metric
        final GC gc = new GC(parent);
        gc.setFont(parent.getFont());
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

        // insert a ScrolledComposite so controls don't disappear if the panel shrinks
        final ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setLayout(new GridLayout());

        // tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = scroller.getHorizontalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif
        bar = scroller.getVerticalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif

        final Composite pg = WidgetFactory.createPanel(scroller, SWT.NONE, GridData.FILL_BOTH, 1, COLUMNS);
        scroller.setContent(pg);

        // Model table: ===========================
        this.userFilesPanel = new VdbEditorUserFilesComposite(this.editor);
        final Control panel = this.userFilesPanel.createPartControl(pg);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, COLUMNS, 1);
        gd.heightHint = 180;
        gd.minimumHeight = 180;
        panel.setLayoutData(gd);

        // ========= GUI finish-up:

        // Size with a fixed width and a bit more than the kids' height:
        final Point pt = pg.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final int miny = pt.y + 45; // add a little extra to keep all label text visible.
        scroller.setMinWidth(400);
        scroller.setMinHeight(miny);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);

        // pack and resize:
        panel.pack(true);
        pg.pack(true);
        userFilesPanel.resetColumnWidths();

        resourceChangeListener = new IResourceChangeListener() {
            public void resourceChanged( final IResourceChangeEvent event ) {
                userFilesPanel.refresh();
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     * @since 5.3.3
     */
    @Override
    public void dispose() {
        if (resourceChangeListener != null) ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);

        super.dispose();
    }

    public void doRevertToSaved() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                // defect 18303 - make sure open and visible:
                if (editor.getVdb() != null) setFocus();
            }
        });
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.3.3
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 5.3.3
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ide.IGotoMarker#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 5.3.3
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) {
        setSite(site);
        setInput(input);
        setPartName(TITLE);
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 5.3.3
     */
    @Override
    public boolean isDirty() {
        return this.editor.getVdb().isModified();
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 5.3.3
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    void setEnabledState() {
        // syncExec so that the enabled state can be determined immediately after this method is called.
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                final IFile file = ((IFileEditorInput)getEditorInput()).getFile();
                isEnabled = !file.isReadOnly();

                // change model panel:
                userFilesPanel.setEnabledState(isEnabled);
            }
        });
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 5.3.3
     */
    @Override
    public void setFocus() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (editor.getVdb() != null) setEnabledState();
            }
        });
    }
}
