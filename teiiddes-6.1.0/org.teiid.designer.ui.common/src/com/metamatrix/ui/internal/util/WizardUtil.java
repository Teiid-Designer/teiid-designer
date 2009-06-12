/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

/**<p>
 * </p>
 * @since 4.0
 */
public final class WizardUtil implements InternalUiConstants.Widgets,
                                         StringUtil.Constants,
                                         UiConstants {
    //============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(WizardUtil.class);

    private static final String SENTENCE_SEPARATOR = "  "; //$NON-NLS-1$

    private static final String CLOSED_PROJECT_MESSAGE = getString("closedProjectMessage"); //$NON-NLS-1$
    private static final String MISSING_FILE_MESSAGE   = getString("missingFileMessage"); //$NON-NLS-1$
    private static final String MISSING_FOLDER_MESSAGE = getString("missingFolderMessage"); //$NON-NLS-1$
    private static final String INVALID_FOLDER_MESSAGE = getString("invalidFolderMessage"); //$NON-NLS-1$

    //============================================================================================================================
    // Static Methods

    /**<p>
     * Initializes the specified wizard by initializing its dialog settings, title, and default page image.
     * </p>
     * @since 4.0
     */
    public static void initialize(final Wizard wizard,
                                  final AbstractUIPlugin plugin,
                                  final String title,
                                  final ImageDescriptor image) {
        wizard.setDialogSettings(WidgetUtil.initializeSettings(wizard, plugin));
        wizard.setWindowTitle(title);
        // don't overwrite default if we haven't got anything useful:
        if (image != null) {
            wizard.setDefaultPageImageDescriptor(image);
        } // endif
    }

    /**<p>
     * Saves the settings of all {@link IPersistentWizardPages} within the specified wizard.
     * </p>
     * @since 4.0
     */
    public static void saveSettings(final IWizard wizard) {
        final IWizardPage[] pgs = wizard.getPages();
        for (int ndx = 0;  ndx < pgs.length;  ++ndx) {
            final IWizardPage pg = pgs[ndx];
            if (pg instanceof IPersistentWizardPage) {
                ((IPersistentWizardPage)pgs[ndx]).saveSettings();
            }
        }
    }

    /**<p>
     * Sets the specified page complete.
     * </p>
     * @since 4.0
     */
    public static void setPageComplete(final WizardPage page) {
        setPageComplete(page, null);
    }

    /**<p>
     * Sets the specified page complete and sets the page's message to the specified message.
     * </p>
     * @since 4.0
     */
    public static void setPageComplete(final WizardPage page,
                                       final String message) {
        setPageComplete(page, message, IMessageProvider.NONE, null);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static void setPageComplete(final WizardPage page,
                                       final String message,
                                       final int status) {
        setPageComplete(page, message, status, null);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static void setPageComplete(final WizardPage page,
                                       String message,
                                       final int status,
                                       final Control control) {
        ArgCheck.isNotNull(page);
        // Set page complete/incomplete appropriately
        page.setPageComplete(status != IMessageProvider.ERROR);
        // Set page's message according to whether its complete and its position within the wizard
        if (status == IMessageProvider.NONE) {
            if (message == null) {
                message = EMPTY_STRING;
            } else {
                message += SENTENCE_SEPARATOR;
            }
            IWizard wizard = page.getWizard();
            final boolean nextPg = (wizard.getNextPage(page) != null);
            final boolean canFinish = wizard.canFinish();
            if (nextPg  &&  canFinish) {
                message += VALID_LAST_OR_MIDDLE_PAGE_MESSAGE;
            } else if (nextPg) {
                message += VALID_PAGE_MESSAGE;
            } else {
                message += VALID_LAST_PAGE_MESSAGE;
            }
            page.setMessage(message);
        } else {
            ArgCheck.isNotNull(message);
            page.setMessage(message, status);
        }
        // Set focus on control if not null
        if (control != null) {
            control.setFocus();
        }
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static IContainer validateFileAndFolder(final Text fileText,
                                                   final Text folderText,
                                                   final WizardPage page,
                                                   final String fileExtension) throws CoreException {
        return validateFileAndFolder(fileText, folderText, page, fileExtension, IMessageProvider.ERROR);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static IContainer validateFileAndFolder(final Text fileText,
                                                   final Text folderText,
                                                   final WizardPage page,
                                                   final String fileExtension,
                                                   final int existsLevel) throws CoreException {
        return validateFileAndFolder(fileText, folderText, page, fileExtension, existsLevel, false);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static IContainer validateFileAndFolder(final Text fileText,
                                                   final Text folderText,
                                                   final WizardPage page,
                                                   final String fileExtension,
                                                   final boolean forceFileExtension) throws CoreException {
        return validateFileAndFolder(fileText, folderText, page, fileExtension, IMessageProvider.ERROR, forceFileExtension);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    public static IContainer validateFileAndFolder(final Text fileText,
                                                   final Text folderText,
                                                   final WizardPage page,
                                                   final String fileExtension,
                                                   final int existsLevel,
                                                   final boolean forceFileExtension) throws CoreException {
        ArgCheck.isNotNull(fileText);
        ArgCheck.isNotNull(folderText);
        ArgCheck.isNotNull(page);
        ArgCheck.isNotNull(fileExtension);
        String fileName = fileText.getText();
        if (StringUtil.isEmpty(fileName)) {
            setPageComplete(page, MISSING_FILE_MESSAGE, IMessageProvider.ERROR);
        } else {
            // Append passed-in file extension if file name doesn't already end with it.
            if (forceFileExtension  &&  !fileName.endsWith(fileExtension)) {
                fileName = FileUtils.toFileNameWithExtension(fileName, fileExtension, true);
                final int ndx = fileText.getCaretPosition();
                fileText.setText(fileName);
                fileText.setSelection(ndx);
                // Return, assuming this method will be re-entered due to the file text being modified.
                page.setPageComplete(false);
                return null;
            }
            if (!ResourcesPlugin.getWorkspace().validateName(fileName, IResource.FILE).isOK()) {
                setPageComplete(page, INVALID_FILE_MESSAGE, IMessageProvider.ERROR);
            } else {
                final String folderName = folderText.getText();
                if (StringUtil.isEmpty(folderName)) {
                    setPageComplete(page, MISSING_FOLDER_MESSAGE, IMessageProvider.ERROR);
                } else {
                    final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
                    if (resrc == null  ||  !(resrc instanceof IContainer)  ||  resrc.getProject() == null) {
                        setPageComplete(page, INVALID_FOLDER_MESSAGE, IMessageProvider.ERROR);
                    } else if (!resrc.getProject().isOpen()) {
                        setPageComplete(page, CLOSED_PROJECT_MESSAGE, IMessageProvider.ERROR);
                    } else {
                        String errorMsg = null;
                        final IContainer folder = (IContainer)resrc;
                        boolean exists = false;
                        final IResource[] resrcs;
                        resrcs = folder.members();
                        // Append file extension if necessary
                        fileName = FileUtils.toFileNameWithExtension(fileName, fileExtension);
                        for (int ndx = resrcs.length;  --ndx >= 0;) {
                            if (resrcs[ndx].getName().equalsIgnoreCase(fileName)) {
                                exists = true;
                                errorMsg = WidgetUtil.getFileExistsMessage(folder.getFile(new Path(fileName)));
                                break;
                            }
                        }

                        // check to see if it exists just on file system and not in workspace
                        if (!exists) {
                            exists = folder.getLocation().append(fileName).toFile().exists();
                            errorMsg = WidgetUtil.getFileExistsButNotInWorkspaceMessage(folder.getFile(new Path(fileName)));
                        }

                        if (existsLevel != IMessageProvider.NONE  &&  exists) {
                            setPageComplete(page, errorMsg, existsLevel);
                        } else {
                            setPageComplete(page);
                        }
                        return folder;
                    }
                }
            }
        }
        return null;
    }

    //============================================================================================================================
    // Static Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    //============================================================================================================================
    // Constructors

    /**<p>
     * Prevents instantiation.
     * @since 4.0
     */
    private WizardUtil() {
    }
}
