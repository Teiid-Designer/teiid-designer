/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.runtime.Connector;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 5.0
 */
public class CloneConnectorBindingPanel extends Composite implements ControlListener, IChangeListener, IChangeNotifier {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(CloneConnectorBindingPanel.class);

    private static String getString( String theKey ) {
        return DqpUiConstants.UTIL.getStringOrKey(PREFIX + theKey);
    }

    private ListenerList changeListeners;

    private boolean saveOnChange;

    private Text bindingNameText;

    private String currentBindingName;

    private Connector newConnectorBinding;

    public CloneConnectorBindingPanel( Composite theParent,
                                       Connector connector ) {
        super(theParent, SWT.NONE);

        this.changeListeners = new ListenerList(ListenerList.IDENTITY);

        // Clone the binding

        newConnectorBinding = connector;

        createContents(this);

    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        this.changeListeners.add(theListener);
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlMoved( ControlEvent theEvent ) {
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlResized( ControlEvent theEvent ) {
    }

    private void createContents( Composite theParent ) {
        GridLayout gridLayout = new GridLayout();
        theParent.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        theParent.setLayoutData(gridData);

        createNameAndTypeGroup(theParent);

        updateState(false);

    }

    private void createNameAndTypeGroup( Composite theParent ) {
        Composite nameGroup = WidgetFactory.createGroup(theParent, getString("bindingName"), SWT.FILL, 1, 3); //$NON-NLS-1$

        GridLayout gridLayout = new GridLayout();
        nameGroup.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        nameGroup.setLayoutData(gridData);

//        Label schemaNameLabel = new Label(nameGroup, SWT.NONE);
//        schemaNameLabel.setText(getString("name")); //$NON-NLS-1$
//        setGridData(schemaNameLabel, GridData.BEGINNING, false, GridData.CENTER, false);

        bindingNameText = WidgetFactory.createTextField(nameGroup, GridData.HORIZONTAL_ALIGN_FILL);
        bindingNameText.setEditable(true);
        // Line Below will maintain White background, if desired.
        // fileNameText.setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));
        GridData fileNameTextGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        // fileNameTextGridData.widthHint = FILE_NAME_TEXT_WIDTH;
        bindingNameText.setLayoutData(fileNameTextGridData);

        bindingNameText.setText(newConnectorBinding.getName());

        this.bindingNameText.addKeyListener(new KeyListener() {

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                handleBindingNameChanged();
            }
        });

        handleBindingNameChanged();
    }

    void handleBindingNameChanged() {
        currentBindingName = getNewBindingName();
        // // alert listeners
        fireChangeEvent();
    }

    public Connector getNewConnector() {
        return this.newConnectorBinding;
    }

    private String getNewBindingName() {
        return bindingNameText.getText();
    }

    public String getNewConnectorBindingName() {
        return currentBindingName;
    }

    public IStatus getStatus() {
        IStatus result = ModelerDqpUtils.isValidBindingName(getNewBindingName());

        if (result.getSeverity() != IStatus.ERROR) {
            int severity = IStatus.ERROR;
            String msg = "Message has not been set"; //$NON-NLS-1$

            if (!ModelerDqpUtils.isUniqueBindingName(getNewBindingName())) {
                // binding with that name already exists //MyCode : need check in the future
                severity = IStatus.ERROR;
                msg = DqpUiConstants.UTIL.getString("duplicateNameMsg", getNewBindingName()); //$NON-NLS-1$
            } else {
                severity = IStatus.OK;
                msg = DqpUiConstants.UTIL.getString("nameIsValidMsg"); //$NON-NLS-1$
            }

            result = new Status(severity, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, null);
        }

        return result;
    }

    /**
     * Notifies all registered listeners of a state change.
     * 
     * @since 4.3
     */
    protected void fireChangeEvent() {
        Object[] listeners = this.changeListeners.getListeners();

        for (int i = 0; i < listeners.length; ++i) {
            ((IChangeListener)listeners[i]).stateChanged(this);
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.changeListeners.remove(theListener);
    }

    private void saveInternal() {
        try {

        } catch (Exception theException) {
            DqpUiConstants.UTIL.log(theException);
            theException.printStackTrace();
        }
    }

    public void save() {
        if (!this.saveOnChange) {
            saveInternal();
        }
    }

    /**
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     * @since 4.3
     */
    @Override
    public boolean setFocus() {
        boolean result = super.setFocus();

        updateState(true);

        return result;
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 5.5
     */
    public void stateChanged( IChangeNotifier theSource ) {
        if (!isDisposed()) {
            updateState(true);
        }
    }

    private void updateState( boolean theUpdateDefnFlag ) {

    }
}
