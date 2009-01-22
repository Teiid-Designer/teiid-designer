/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * XsdElement ChooserPanel
 */
public class XsdElementChooserPanel extends Composite implements IInternalUiConstants {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(XsdElementChooserPanel.class);

    private static final String NO_SELECTION = getString("noSelection.text"); //$NON-NLS-1$

    private static final String SELECT_ELEMENT_TITLE = getString("selectElementDialog.title"); //$NON-NLS-1$
    private static final String SELECT_ELEMENT_MSG = getString("selectElementDialog.msg"); //$NON-NLS-1$
    static final String INTERFACE_SELECTION_INVALID_MESSAGE = getString("selectElementDialog.selectionInvalid.msg"); //$NON-NLS-1$

    private XSDElementDeclaration currentElem;
    private XSDElementDeclaration originalElem;
    private CLabel elementLabel;
    private Button showElementDialogButton;

    /** List of listeners registered for this panels events */
    private List eventListeners;

    /**
     * Constructor
     * 
     * @param parent the parent composite
     */
    public XsdElementChooserPanel( Composite parent ) {
        super(parent, SWT.NONE);
        initialize();
    }

    /**
     * Set the Selected Datatype
     * 
     * @param type the selected datatype
     */
    public void setSelectedElem( XSDElementDeclaration elem ) {
        this.originalElem = elem;
        this.currentElem = elem;
        this.elementLabel.setText(getElementText(elem));
        this.elementLabel.setImage(getElementImage(elem));
        this.layout();
        notifyEventListeners();
    }

    /**
     * Get the Selected Datatype
     * 
     * @return the selected datatype
     */
    public XSDElementDeclaration getSelectedElement() {
        return currentElem;
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    // -------------------------------------------------------------------------
    // Methods to Register, UnRegister, Notify Listeners to this Panels Events
    // -------------------------------------------------------------------------
    /**
     * This method will register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be registered
     */
    public void addEventListener( EventObjectListener listener ) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        eventListeners.add(listener);
    }

    /**
     * This method will un-register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeEventListener( EventObjectListener listener ) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a CheckboxSelectionEvents
     */
    private void notifyEventListeners() {
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(new EventObject(this));
                }
            }
        }
    }

    /**
     * Initialization
     */
    private void initialize() {
        // Set the layout
        this.setLayout(new GridLayout(2, false));
        this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Element Label
        String text = getElementText(currentElem);
        Image image = getElementImage(currentElem);
        this.elementLabel = WidgetFactory.createLabel(this, text, image, GridData.HORIZONTAL_ALIGN_BEGINNING
                                                                         | GridData.GRAB_HORIZONTAL);

        // Create the showDatatypeDialog Button
        this.showElementDialogButton = WidgetFactory.createButton(this,
                                                                  "...", GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL); //$NON-NLS-1$
        this.showElementDialogButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                showElementDialogPressed();
            }
        });
        this.layout();
    }

    /**
     * get text for the provided object
     */
    private String getElementText( Object object ) {
        String result = NO_SELECTION;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getText(object);
            }
        }
        return result;
    }

    /**
     * get image for the provided object
     */
    private Image getElementImage( Object object ) {
        Image result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getImage(object);
            }
        }
        return result;
    }

    /**
     * handler for interface browse button clicked
     */
    void showElementDialogPressed() {

        // Show the Workspaceobject selection dialog
        final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(SELECT_ELEMENT_TITLE,
                                                                                  SELECT_ELEMENT_MSG,
                                                                                  true,
                                                                                  null,
                                                                                  interfaceFilter,
                                                                                  interfaceValidator,
                                                                                  new ModelExplorerLabelProvider(),
                                                                                  new ModelExplorerContentProvider());

        // Update the ui with the new interface selection
        if (selections.length == 1) {
            Object selection = selections[0];
            XSDElementDeclaration newElem = null;
            if (selection instanceof XSDElementDeclaration) {
                newElem = (XSDElementDeclaration)selection;
            } else if (selection instanceof XSDParticle) {
                Object content = ((XSDParticle)selection).getContent();
                if (content instanceof XSDElementDeclaration) {
                    newElem = (XSDElementDeclaration)content;
                }
            }
            if (newElem != null && !newElem.equals(originalElem)) {
                setSelectedElem(newElem);
            }
        }
    }

    /**
     * filter to show interfaces in open projects
     */
    final ViewerFilter interfaceFilter = new ModelWorkspaceViewerFilter(true) {

        @Override
        public boolean select( final Viewer viewer,
                               final Object parent,
                               final Object element ) {
            boolean doSelect = false;
            if (element instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)element).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (element instanceof IProject) {
                        doSelect = true;
                    } else if (element instanceof IContainer) {
                        doSelect = true;
                        // Show webservice model files, and not .xsd files
                    } else if (element instanceof IFile && ModelUtil.isXsdFile((IFile)element)) {
                        doSelect = true;
                    }
                }
            } else if (element instanceof IContainer) {
                doSelect = true;
            } else if (element instanceof XSDComponent) {
                if (!(element instanceof XSDAnnotation) && !(element instanceof XSDSimpleTypeDefinition)) {
                    doSelect = true;
                }
            }

            return doSelect;
        }
    };

    /**
     * validator for interface selection dialog
     */
    final ISelectionStatusValidator interfaceValidator = new ISelectionStatusValidator() {
        public IStatus validate( final Object[] selection ) {
            if (selection.length == 1) {
                if (selection[0] instanceof XSDElementDeclaration) {
                    return new Status(IStatus.OK, PLUGIN_ID, 0, "", null); //$NON-NLS-1$
                }
                if (selection[0] instanceof XSDParticle) {
                    XSDParticle particle = (XSDParticle)selection[0];
                    Object content = particle.getContent();
                    if (content instanceof XSDElementDeclaration) {
                        return new Status(IStatus.OK, PLUGIN_ID, 0, "", null); //$NON-NLS-1$
                    }
                }
            }

            return new Status(IStatus.ERROR, PLUGIN_ID, 0, INTERFACE_SELECTION_INVALID_MESSAGE, null);
        }
    };
}
