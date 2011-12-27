/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.teiid.core.util.FileUtils;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;

/**
 * @since 4.0
 */
public final class UiUtil implements UiConstants {

    static IWorkbenchWindow currentWorkbenchWindow;

    /**
     * Programatically close a IEditorPart for the specified file, if one exists.
     * 
     * @param file
     * @param save true will request that the user to save or discard their changes. Should be set to true unless the modelFile is
     *        being deleted.
     * @return true if the editor closed successfully or there was no editor open for the specified modelFile. Will return false
     *         if the user aborted the close.
     */
    public static boolean close( final IFile modelFile,
                                 final boolean save ) {
        CloseEditorRunnable runnable = new CloseEditorRunnable(modelFile, save);
        Display.getDefault().syncExec(runnable);
        return runnable.didClose;
    }

    /**
     * Creates an <code>Image</code> from the specified source <code>Image</code> by changing every occurrence of the old color to
     * the new color.
     * 
     * @param theSourceImage the image used to create the new image
     * @param theOldColor the color being changed
     * @param theNewColor the new color
     * @return the new image
     */
    public static Image createImage( final Image theSourceImage,
                                     final Color theOldColor,
                                     final Color theNewColor ) {
        CoreArgCheck.isNotNull(theSourceImage);
        CoreArgCheck.isNotNull(theOldColor);
        CoreArgCheck.isNotNull(theNewColor);

        ImageData imageData = (ImageData)theSourceImage.getImageData().clone();
        PaletteData palette = imageData.palette;

        if (palette.isDirect) {
            // direct palette. colors are mapped into the pixel value
            for (int y = 0; y < imageData.height; y++) {
                for (int x = 0; x < imageData.width; x++) {
                    int value = imageData.getPixel(x, y);

                    if (palette.getRGB(value).equals(theOldColor.getRGB())) {
                        imageData.setPixel(x, y, palette.getPixel(theNewColor.getRGB()));
                    }
                }
            }
        } else {
            // indexed palette. colors have an index so swap out old for new.
            for (int i = 0; i < palette.colors.length; i++) {
                if (palette.colors[i].equals(theOldColor.getRGB())) {
                    palette.colors[i] = theNewColor.getRGB();
                    break;
                }
            }
        }

        return new Image(null, imageData);
    }

    /**
     * Converts the severity of the specified <code>IStatus</code> to the message type used by
     * {@link org.eclipse.jface.dialogs.TitleAreaDialog}s.
     * 
     * @param theStatus the status whose severity is being converted
     * @return the message type
     * @since 4.3
     * @see IMessageProvider
     */
    public static int getDialogMessageType( IStatus theStatus ) {
        int result = IMessageProvider.NONE;

        switch (theStatus.getSeverity()) {
            case IStatus.ERROR: {
                result = IMessageProvider.ERROR;
                break;
            }
            case IStatus.OK: {
                result = IMessageProvider.NONE;
                break;
            }
            case IStatus.INFO: {
                result = IMessageProvider.INFORMATION;
                break;
            }
            case IStatus.WARNING: {
                result = IMessageProvider.WARNING;
                break;
            }
            default: {
                result = IMessageProvider.ERROR;
                break;
            }
        }

        return result;
    }

    public static IEditorPart getEditorForFile( IFile file,
                                                boolean forceOpen ) {
        IEditorPart result = null;
        if (file != null) {
            IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
            if (page != null) {
                // look through the open editors and see if there is one available for this model file.
                IEditorReference[] editors = page.getEditorReferences();
                for (int i = 0; i < editors.length; ++i) {
                    IEditorPart editor = editors[i].getEditor(false);
                    if (editor != null) {
                        IEditorInput input = editor.getEditorInput();
                        if (input instanceof IFileEditorInput) {
                            if (file.equals(((IFileEditorInput)input).getFile())) {
                                // found it;
                                result = editor;
                                break;
                            }
                        }
                    }
                }

                if (result == null && forceOpen) {
                    // there is no editor open for this object. Open one and hand it the double-click target.
                    try {
                        result = IDE.openEditor(page, file);
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Obtains the identifier of the current perspective.
     * 
     * @return the ID of the current perspective or <code>null</code> if no perspective open or called outside the UI thread
     */
    public static String getPerspectiveId() {
        IWorkbenchPage page = getWorkbenchPage();

        // could happen if not in UI thread
        if (page == null) return null;

        IPerspectiveDescriptor descriptor = page.getPerspective();

        // no perspectives open
        if (descriptor == null) return null;

        // return the perspective ID
        return descriptor.getId();
    }

    /**
     * Obtains the {@link ResourcePatternFilter} from the specified Resource Navigator view. If that view is not a
     * {@link ResourceNavigator} or if the view is not showing a default filter is constructed.
     * 
     * @return the filter (never <code>null</code>)
     * @throws IllegalArgumentException if the input parameter is <code>null</code> or empty
     * @since 5.0.2
     */
    public static ViewerFilter getResourceFilter( String theResourceNavigatorViewId ) {
        CoreArgCheck.isNotEmpty(theResourceNavigatorViewId);

        ViewerFilter result = null;
        IViewPart view = UiUtil.getViewPart(theResourceNavigatorViewId);

        if ((view == null) || (!(view instanceof ResourceNavigator))) {
            // just use the default resource filter
            result = new ResourcePatternFilter();
        } else {
            // get the filter from the view
            result = ((ResourceNavigator)view).getPatternFilter();
        }

        return result;
    }

    /**
     * Obtains the appropriate <code>Image</code> for the specified <code>IStatus</code>.
     * 
     * @param theStatus the status whose image is being requested
     * @return the image
     * @since 4.3
     */
    public static Image getStatusImage( IStatus theStatus ) {
        Image result = null;
        int severity = theStatus.getSeverity();

        switch (severity) {
            case IStatus.ERROR: {
                result = UiPlugin.getDefault().getImage(Images.TASK_ERROR);
                break;
            }
            case IStatus.WARNING: {
                result = UiPlugin.getDefault().getImage(Images.TASK_WARNING);
                break;
            }
            case IStatus.INFO: {
                result = UiPlugin.getDefault().getImage(Images.TASK_INFO);
                break;
            }
            default: {
                break;
            }
        }

        return result;
    }

    /**
     * @return The current selection within the active part; never null.
     * @since 4.0
     */
    public static IStructuredSelection getStructuredSelection() {
        final ISelection selection = getWorkbenchWindowOnlyIfUiThread().getSelectionService().getSelection();
        return (selection instanceof IStructuredSelection ? (IStructuredSelection)selection : StructuredSelection.EMPTY);
    }

    /**
     * Returns the operating system color associated with the specified system color ID defined in the {@link SWT}class.
     * 
     * @param id The system color ID defined in the {@link SWT}class.
     * @return The operating system color.
     * @since 4.0
     */
    public static Color getSystemColor( final int id ) {
        return Display.getDefault().getSystemColor(id);
    }

    /**
     * @return The workbench.
     * @since 4.0
     */
    public static IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    /**
     * @return The active workbench page.
     * @since 4.0
     */
    public static IWorkbenchPage getWorkbenchPage() {
        IWorkbenchWindow window = getWorkbenchWindowOnlyIfUiThread();
        return (window == null) ? null : window.getActivePage();
    }

    /**
     * Obtains the {@link IViewPart} with the specified identifier.
     * 
     * @param theViewId the identifier of the view part being requested
     * @return the view part or <code>null</code> if not open, not found, or if there is no active workbench page
     * @throws com.metamatrix.core.util.AssertionError if <code>theViewId</code> is <code>null</code>
     * @since 4.2
     */
    public static IViewPart getViewPart( String theViewId ) {
        CoreArgCheck.isNotNull(theViewId);

        IViewPart result = null;
        IWorkbenchPage page = getWorkbenchPage();

        if (page != null) {
            result = page.findView(theViewId);
        }

        return result;
    }

    /**
     * Obtains the <code>Shell</code> of the active workbench window.
     * 
     * @return the <code>Shell</code>
     */
    public static Shell getWorkbenchShellOnlyIfUiThread() {
        return getWorkbenchWindowOnlyIfUiThread().getShell();
    }

    /**
     * @return The active workbench window.
     * @since 4.0
     */
    public static IWorkbenchWindow getWorkbenchWindow() {
        UiUtil.runInSwtThread(new Runnable() {

            public void run() {
                currentWorkbenchWindow = getWorkbench().getActiveWorkbenchWindow();
            }
        }, true);
        IWorkbenchWindow window = currentWorkbenchWindow;
        currentWorkbenchWindow = null;
        return window;
    }

    /**
     * @return The active workbench window.
     * @since 4.0
     */
    public static IWorkbenchWindow getWorkbenchWindowOnlyIfUiThread() {
        return getWorkbench().getActiveWorkbenchWindow();
    }
    
    /**
     * Sets focus to the perspective with the specified ID. The perspective will be opened if not already open.
     * 
     * @param perspectiveId the ID of the perspective to open
     */
    public static void openPerspective( String perspectiveId ) {
        IWorkbench workbench = getWorkbench();
        IWorkbenchWindow window = getWorkbenchWindow();

        try {
            workbench.showPerspective(perspectiveId, window);
        } catch (Exception theException) {
            Util.log(theException);
        }
    }

    /**
     * Opens a system editor with the specified file system resource.
     * 
     * @param theFile the file being opened in a system editor
     * @return <code>true</code>if editor successfully opened; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean openSystemEditor( final File theFile ) {
        return openSystemEditor(theFile.getAbsolutePath());
    }

    /**
     * Opens a system editor with the specified workspace file.
     * 
     * @param theFile the file being opened in a system editor
     * @return <code>true</code>if editor successfully opened; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean openSystemEditor( final IFile theFile ) {
        return openSystemEditor(theFile.getLocation().toOSString());
    }

    /**
     * Opens a system editor with the specified file name (full OS absolute path included).
     * 
     * @param theFileName the file being opened in a system editor
     * @return <code>true</code>if editor successfully opened; <code>false</code> otherwise.
     * @since 4.2
     */
    private static boolean openSystemEditor( final String theFileName ) {
        final boolean result[] = {false};

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

            public void run() {
                if (Program.findProgram(FileUtils.getExtension(theFileName)) != null) {
                    try {
                        // Program.launch(String) the first time it is called with a file having an extension
                        // that does not have an OS association returns false. It returns true from then on.
                        // this seems like a bug. the workaround was to first call Program.findProgram(String) which
                        // always returns null if no association exists.
                        result[0] = Program.launch(theFileName);
                    } catch (Throwable theException) {
                        theException.printStackTrace();
                        result[0] = false;
                    }
                }
            }
        });

        return result[0];
    }

    /**
     * @param operation The operation to be executed in the SWT thread.
     * @param asynchronous True if the operation should be run asynchronously, meaning the calling thread will not be blocked.
     * @since 4.1
     */
    public static void runInSwtThread( final Runnable operation,
                                       final boolean asynchronous ) {
        Display display = (Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent());
        if (Thread.currentThread() != display.getThread()) {
            if (asynchronous) {
                display.asyncExec(operation);
            } else {
                display.syncExec(operation);
            }
        } else {
            operation.run();
        }
    }

    /**
     * Saves the list values to the specified <code>IDialogSettings</code>. If the number of values exceeds the limit, values at
     * the front of the list are deleted.
     * 
     * @param theSettings the settings being saved
     * @param theId the settings identifier being saved
     * @param theValues the values being saved
     * @param theLimit the maximum number of values saved
     * @since 4.2
     */
    public static void save( IDialogSettings theSettings,
                             String theId,
                             List theValues,
                             int theLimit ) {
        String[] values = new String[0];

        if ((theValues != null) && !theValues.isEmpty()) {
            int size = theValues.size();
            int j = 0;

            if ((theLimit > 0) && (theValues.size() > theLimit)) {
                j = (size - theLimit);
                values = new String[theLimit];
            } else {
                values = new String[theValues.size()];
            }

            for (int i = 0; i < values.length; i++) {
                values[i] = theValues.get(j++).toString();
            }
        }

        theSettings.put(theId, values);
    }

    public static void savePreferences( IPreferenceStore store ) {
        if (store.needsSaving() && store instanceof IPersistentPreferenceStore) {
            try {
                ((IPersistentPreferenceStore)store).save();
            } catch (IOException err) {
                WidgetUtil.showError(err);
            }
        }
    }

    /**
     * Updates the specified integer-based preference with the specified value if and only if:
     * <ul>
     * <li>It equals the specified default value and the current preference value is not zero (i.e., does not represent the
     * default value). In this case, the value will be removed from the specified preference store.</li>
     * <li>It does not equal the specified default value and does not equal the current preference value.</li>
     * </ul>>
     * 
     * @param preference
     * @param value
     * @param defaultValue
     * @param store
     * @since 5.0.1
     */
    public static void updateIntegerPreference( String preference,
                                                int value,
                                                int defaultValue,
                                                IPreferenceStore store ) {
        int val = store.getInt(preference);
        if (value == defaultValue) {
            if (val != 0) {
                store.setToDefault(preference);
            }
        } else if (value != val) {
            store.setValue(preference, value);
        }
    }

    /**
     * Prevents instantiation.
     * 
     * @since 4.0
     */
    private UiUtil() {
    }
}

/**
 * CloseEditorRunnable is a Runnable for closing a IEditorPart that can return a boolean for whether or not the editor actually
 * closed.
 */
class CloseEditorRunnable implements Runnable {

    private IFile modelFile;
    private boolean save;
    public boolean didClose = true;

    public CloseEditorRunnable( IFile modelFile,
                                boolean save ) {
        this.modelFile = modelFile;
        this.save = save;
    }

    public void run() {
        final IEditorPart editor = UiUtil.getEditorForFile(modelFile, false);
        if (editor != null) {
            didClose = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().closeEditor(editor, save);
        }
    }
}
