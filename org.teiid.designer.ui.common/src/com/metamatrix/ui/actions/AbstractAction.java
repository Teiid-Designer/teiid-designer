/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.actions;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.graphics.ImageImageDescriptor;
import com.metamatrix.ui.internal.InternalUiConstants.Actions;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * AbstractAction
 */
public abstract class AbstractAction extends Action
implements INullSelectionListener, ISelectionChangedListener {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** The localization identifier for the accelerator. */
    private String acceleratorId;

    /** The localization identifier for the description. */
    private String descriptionId;

    /** The path/file identifier for the disabled image. */
    private String disabledImageId;

    /** The identifier for the help context. */
    private String helpId;

    /** The path/file identifier for the hover image. */
    private String hoverImageId;

    /** The path/file identifier for the image. */
    private String imageId;

    /** The plugin associated with this action. Used for logging and localization. */
    private AbstractUiPlugin plugin;

    /** The current selection or <code>null</code>. */
    private ISelection selection;

    /** The current event or <code>null</code> if last event was a workbench selection. */
    private SelectionChangedEvent selectionEvent;

    /** The localization identifier for the label text. */
    private String textId;

    /** The localization identifier for the tool tip text. */
    private String tipId;

    /** Plugin's utility used for logging and localization. */
    private PluginUtil pluginUtils;

    /** Allow subclass to veto use of a wait-cursor */
    private boolean useWaitCursor = true;

    /**
     * The workbench part containing the selection or <code>null</code> if last selection was not a
     * workbench selection.
     */
    private IWorkbenchPart part;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an <code>AbstractAction</code> setting properties found in the plugin's
     * resource bundle.
     * @param thePlugin the plugin associated with this action
     */
    public AbstractAction(AbstractUiPlugin thePlugin) {
        plugin = thePlugin;
        init();
    }

    /**
     * Constructs an <code>AbstractAction</code> setting properties found in the plugin's
     * resource bundle.
     * @param thePlugin the plugin associated with this action
     * @param theStyle the style as defined by constants in {@link org.eclipse.jface.action.IAction}
     */
    public AbstractAction(AbstractUiPlugin thePlugin,
                          int theStyle) {
        // just pass empty string to super as init() will set text via properties
        super("", theStyle); //$NON-NLS-1$
        plugin = thePlugin;
        init();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Notifies the action that it is no longer needed. The action should dispose of any system resources.
     * After calling this method, the action is considered unusable.
     */
    public void dispose() {
    }

    /**
     * Gets the localization identifier for the accelerator.
     * @return the localization identifier
     */
    public String getAcceleratorId() {
        return acceleratorId;
    }

    /**
     * Gets the <code>ResourceBundle</code> key for the given property identifier.
     * The identifier should be a constant found in {@link UiConstants.Actions}.
     * @param thePropertyId the action property whose bundle key is being requested
     * @return the bundle key
     */
    private String getActionPropertyKey(String thePropertyId) {
        return new StringBuffer().append(getId())
                                 .append(Actions.DELIMITER)
                                 .append(thePropertyId)
                                 .toString();
    }

    /**
     * Gets the localization identifier for the description.
     * @return the localization identifier
     */
    public String getDescriptionId() {
        return descriptionId;
    }

    /**
     * Gets the disabled image path/file identifier used to create the {@link ImageDescriptor}.
     * @return the image path/file identifier
     */
    public String getDisabledImageId() {
        return disabledImageId;
    }

    /**
     * Gets the help context identifier used in workbench help.
     * @return the localization identifier
     */
    public String getHelpContextId() {
        return helpId;
    }

    /**
     * Gets the hover image path/file identifier used to create the {@link ImageDescriptor}.
     * @return the image path/file identifier
     */
    public String getHoverImageId() {
        return hoverImageId;
    }

    /**
     * Gets the image path/file identifier used to create the {@link ImageDescriptor}.
     * @return the image path/file identifier
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Gets the plugin associated with this action.
     * @return the plugin
     */
    public AbstractUiPlugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the utilities class used for logging and localization.
     * @return the plugin utilities
     */
    protected PluginUtil getPluginUtils() {
        return pluginUtils;
    }

    /**
     * Gets the selected object in the current selection. If more than one object is selected, the first
     * is returned.
     * @return the selected object or <code>null</code> if none selected
     */
    public Object getSelectedObject() {
        return SelectionUtilities.getSelectedObject(selection);
    }

    /**
     * Gets all objects in the current selection.
     * @return the list of all selected objects or an empty list
     */
    public List getSelectedObjects() {
        return SelectionUtilities.getSelectedObjects(selection);
    }

    /**
     * Gets the current workbench selection.
     * @return the current selection or <code>null</code>
     */
    public ISelection getSelection() {
        return selection;
    }

    /**
     * Gets the current selection event.
     * @return the selection event or <code>null</code>
     */
    public SelectionChangedEvent getSelectionEvent() {
        return selectionEvent;
    }

    /**
     * Gets the localization identifier for the label text.
     * @return the localization identifier
     */
    public String getTextId() {
        return textId;
    }

    /**
     * Gets the localization identifier for the tool tip text.
     * @return the localization identifier
     */
    public String getToolTipId() {
        return tipId;
    }

//    /* (non-Javadoc)
//     * @see org.eclipse.jface.action.Action#getToolTipText()
//     */
//    public String getToolTipText() {
//        // use for debugging purposes to see the identity hash of toolbar buttons
//        return super.getToolTipText() + " (" + System.identityHashCode(this) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
//    }

    /**
     * Gets the flag indicating whether or not using wait-cursor.
     *
     * @return  use-wait-cursor flag
     */
    protected boolean getUseWaitCursor() {
    	return useWaitCursor;
    }

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	protected IWorkbenchPart getPart() {
		return part;
	}

    /**
     * Initialization only done at construction. Sets action properties based on information in the plugin
     * <code>ResourceBundle</code>.
     */
    protected void init() {
        pluginUtils = plugin.getPluginUtil();

        // initialize id which is used in getting the properties below
        setId(getClass().getName());

        // initialize accelerator (must do before setting text)
        String key = getActionPropertyKey(Actions.ACCELERATOR);
        if (pluginUtils.keyExists(key)) {
            setAcceleratorId(key);
        }

        // initialize description
        key = getActionPropertyKey(Actions.DESCRIPTION);
        if (pluginUtils.keyExists(key)) {
            setDescriptionId(key);
        }

        // initialize disabledImage
        key = getActionPropertyKey(Actions.DISABLED_IMAGE);
        if (pluginUtils.keyExists(key)) {
            setDisabledImageId(key);
        }

        // initialize help context id
        key = getActionPropertyKey(Actions.HELP);
        if (pluginUtils.keyExists(key)) {
            setHelpContextId(key);
        }

        // initialize hover image
        key = getActionPropertyKey(Actions.HOVER_IMAGE);
        if (pluginUtils.keyExists(key)) {
            setHoverImageId(key);
        }

        // initialize image
        key = getActionPropertyKey(Actions.IMAGE);
        if (pluginUtils.keyExists(key)) {
            setImageId(key);
        }

        // initialize text
        key = getActionPropertyKey(Actions.TEXT);
        if (pluginUtils.keyExists(key)) {
            setTextId(key);
        }

        // initialize tooltip
        key = getActionPropertyKey(Actions.TOOLTIP);
        if (pluginUtils.keyExists(key)) {
            setToolTipTextId(key);
        }
    }

    /**
     * Indicates if the current selection is empty.
     * @return <code>true</code> if there is no object selected; <code>false</code> otherwise.
     */
    public boolean isEmptySelection() {
        return ((selection == null) || selection.isEmpty());
    }

    /**
     * Indicates if the current selection has multiple objects selected.
     * @return <code>true</code> if multiple objects are selected; <code>false</code> otherwise.
     */
    public boolean isMultiSelection() {
        return SelectionUtilities.isMultiSelection(selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent theEvent) {
        selection = theEvent.getSelection();
        this.part = null;
        selectionEvent = theEvent;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {

        this.part = thePart;
        selection = theSelection;
        selectionEvent = null;
    }

    /**
     * Sets the localization identifier for the accelerator. Causes the accelerator to be changed.
     * <strong>The accelerator is actually set when the text identifier is set.</strong>
     * <p>
     * Here are some example properties file entries for setting accelerator:
     * <ul>
     * <li>com.metamatrix.modeler.ui.action.MyAction.accelerator=Ctrl+M
     * <li>com.metamatrix.modeler.ui.action.YourAction.accelerator=Alt+Y
     * <li>com.metamatrix.modeler.ui.action.TheirAction.accelerator=Shift+T
     * <li>com.metamatrix.modeler.ui.action.OurAction.accelerator=Command+O
     * <li>com.metamatrix.modeler.ui.action.FindAction.accelerator=F3
     * </ul>
     * @param theId the localization identifier
     * @see #setTextId(String)
     */
    public void setAcceleratorId(String theId) {
        acceleratorId = theId;
    }

    /**
     * Sets the localization identifier for the description. Causes the description to be changed.
     * If <code>null</code> is passed in the description is changed to an empty string.
     * @param theId the localization identifier
     */
    public void setDescriptionId(String theId) {
        descriptionId = theId;
        setDescription((theId != null) ? pluginUtils.getString(theId)
                                       : ""); //$NON-NLS-1$
    }

    /**
     * Sets the disabled {@link ImageDescriptor} by using the given <code>Image</code>.
     * @param theImage the image being used to create the disabled <code>ImageDescriptor</code>
     */
    public void setDisabledImage(Image theImage) {
        setDisabledImageDescriptor(new ImageImageDescriptor(theImage));
    }

    /**
     * Sets the path/file identifier for the disabled image. Causes the {@link ImageDescriptor} to change.
     * @param theId the image identifier
     */
    public void setDisabledImageId(String theId) {
        disabledImageId = theId;
        ImageDescriptor descriptor = null;

        if (theId != null) {
            descriptor = plugin.getImageDescriptor(pluginUtils.getString(theId));
        }

        super.setDisabledImageDescriptor(descriptor);
    }

    /**
     * Sets the identifier used for context sensitive help.
     * @param theId the context help identifier
     */
    public void setHelpContextId(String theId) {
        helpId = theId;
        part.getSite().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp(this, theId);
    }

    /**
     * Sets the hover {@link ImageDescriptor} by using the given <code>Image</code>.
     * @param theImage the image being used to create the hover <code>ImageDescriptor</code>
     */
    public void setHoverImage(Image theImage) {
        setHoverImageDescriptor(new ImageImageDescriptor(theImage));
    }

    /**
     * Sets the path/file identifier for the hover image. Causes the {@link ImageDescriptor} to change.
     * @param theId the image identifier
     */
    public void setHoverImageId(String theId) {
        hoverImageId = theId;
        ImageDescriptor descriptor = null;

        if (theId != null) {
            descriptor = plugin.getImageDescriptor(pluginUtils.getString(theId));
        }

        super.setHoverImageDescriptor(descriptor);
    }

    /**
     * Sets the {@link ImageDescriptor} by using the given <code>Image</code>.
     * @param theImage the image being used to create the <code>ImageDescriptor</code>
     */
    public void setImage(Image theImage) {
        setImageDescriptor(new ImageImageDescriptor(theImage));
    }

    /**
     * Sets the path/file identifier for the image. Causes the {@link ImageDescriptor} to change.
     * @param theId the image identifier
     */
    public void setImageId(String theId) {
        imageId = theId;
        ImageDescriptor descriptor = null;

        if (theId != null) {
            descriptor = plugin.getImageDescriptor(pluginUtils.getString(theId));
        }

        super.setImageDescriptor(descriptor);
    }

    /**
     * Sets the utility object used for resource bundle lookups and logging. Defaults to use
     * <code>com.metamatrix.modeler.ui.UiPlugin</code>.
     * @param theUtil the utility instance
     */
    public void setPluginUtil(PluginUtil theUtil) {
        pluginUtils = theUtil;
    }

    /**
     * Sets the localization identifier for the label text. Causes the label text to be changed.
     * If <code>null</code> is passed in the text is changed to an empty string. Also, if an accelerator
     * identifier has been set, the accelerator is set.
     * @param theId the localization identifier
     */
    public void setTextId(String theId) {
        textId = theId;

        // get the label text
        String label = (theId != null) ? pluginUtils.getString(theId)
                                       : ""; //$NON-NLS-1$

        // if accelerator exists add it to the text of the label in the format required by
        // org.eclipse.jface.action.Action.
        if (acceleratorId != null) {
            String acceleratorText = pluginUtils.getString(acceleratorId);

            if ((acceleratorText != null) && acceleratorText.length() > 0) {
                label += "@" + acceleratorText; //$NON-NLS-1$
            }
        }

        setText(label);
    }

    /**
     * Sets the localization identifier for the tool tip text. Causes the tool tip text to be changed.
     * If <code>null</code> is passed in the tooltip text is changed to an empty string.
     * @param theId the localization identifier
     */
    public void setToolTipTextId(String theId) {
        tipId = theId;
        setToolTipText((theId != null) ? pluginUtils.getString(theId)
                                       : ""); //$NON-NLS-1$
    }

	/**
	 * Allow subclass to veto or reinstate using a wait-cursor.
	 *
	 * @param flag   use wait-cursor if true, not if false
	 */
	protected void setUseWaitCursor(boolean flag) {
		this.useWaitCursor = flag;
	}

	/**
	 * Force subclass to implement a doRun() method which is a substitute for the run() method
	 * which is declared as final in this class to force usage of a wait-cursor.
	 */
	protected abstract void doRun();


    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     */
    protected boolean preRun() {
        return true;
    }

	/**
	 * Subclasses implement a doRun() method which substitutes for run().  run() is here and
	 * runs doRun() as a Runnable, in order to pass the Runnable to BusyIndicator which will
	 * display a wait-cursor.
	 */
	@Override
    public final void run() {
        if( preRun() ) {
            if (useWaitCursor) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        doRun();
                    }
                };
                BusyIndicator.showWhile(null, runnable);
            } else {
                doRun();
            }
        }

        postRun();
	}

    /**
     * This method is called in the run() method of AbstractAction to give the actions a way to
     * implement post processing behavior, for example cleaning up state that is created during
     * the run.
     *
     */
    protected void postRun() {
        // no action
    }

}
