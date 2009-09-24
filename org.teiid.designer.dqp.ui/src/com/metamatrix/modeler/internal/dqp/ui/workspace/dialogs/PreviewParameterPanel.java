/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * @since 5.5.3
 */
public class PreviewParameterPanel extends ScrolledComposite implements DqpUiConstants, IChangeNotifier {
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(PreviewParameterPanel.class);

    private static final IStatus ERROR_STATUS;

    private static final IStatus GOOD_STATUS;

    static {
        ERROR_STATUS = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, UTIL.getString(PREFIX + "errorStatus"), null); //$NON-NLS-1$
        GOOD_STATUS = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, UTIL.getString(PREFIX + "goodStatus"), null); //$NON-NLS-1$
    }

    private List<Label> images;

    private List<Label> names;

    private List<IChangeListener> listeners;

    private IStatus overallStatus = ERROR_STATUS;

    private Composite pnlMain;

    private IStatus[] statuses;

    private List<Text> textFields;

    private PreviewParameterPanel( Composite parent ) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);
        setLayoutData(new GridData(GridData.FILL_BOTH));

        // customize scroll bars to give better scrolling behavior
        ScrollBar bar = getHorizontalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }

        bar = getVerticalBar();

        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        }

        this.pnlMain = WidgetFactory.createPanel(this, SWT.NONE, GridData.FILL_BOTH);
        ((GridLayout)this.pnlMain.getLayout()).numColumns = 4;
        setContent(this.pnlMain);
    }

    public PreviewParameterPanel( Composite parent,
                                  List<EObject> parameters ) {
        this(parent);
        createParameterControls(this.pnlMain, parameters);
        postInit();
    }

    public PreviewParameterPanel( Composite parent,
                                  EObject accessPattern ) {
        this(parent);
        SqlColumnSetAspect aspect = (SqlColumnSetAspect)SqlAspectHelper.getSqlAspect(accessPattern);
        createParameterControls(this.pnlMain, aspect.getColumns(accessPattern));
        postInit();
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 5.5.3
     */
    public void addChangeListener( IChangeListener listener ) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<IChangeListener>(1);
        }

        this.listeners.add(listener);
    }

    private void createParameterControls( Composite parent,
                                          List<EObject> parameters ) {
        ILabelProvider labelProvider = ModelUtilities.getAdapterFactoryLabelProvider();
        int numCols = parameters.size();
        this.names = new ArrayList(numCols);
        this.images = new ArrayList(numCols);
        this.textFields = new ArrayList(numCols);
        this.statuses = new IStatus[numCols];

        for (EObject param : parameters) {
            Label name = new Label(parent, SWT.NONE);
            name.setText(labelProvider.getText(param));
            this.names.add(name);

            Label image = new Label(parent, SWT.NONE);
            image.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.BLANK_ICON));
            this.images.add(image);

            final Text textField = WidgetFactory.createTextField(parent);
            final ModifyListener modifyListener = createModifyListener(param);
            textField.addModifyListener(modifyListener);
            textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

            // set focus to the first textfield
            if (this.textFields.isEmpty()) {
                textField.setFocus();
            }

            this.textFields.add(textField);

            // set initial validation state
            Event e = new Event();
            e.widget = textField;
            modifyListener.modifyText(new ModifyEvent(e));
            
            // add "make null" checkbox if value can be null
            if (ParameterValueValidator.canBeNull(param)) {
                final Button btn = WidgetFactory.createCheckBox(parent, UTIL.getString(PREFIX + "chkNullValue")); //$NON-NLS-1$
                btn.setToolTipText(UTIL.getString(PREFIX + "chkNullValue.tip")); //$NON-NLS-1$
                btn.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected( SelectionEvent e ) {
                        // enable textfield only when checkbox is not checked
                        textField.setEnabled(!btn.getSelection());
                        
                        // put focus back in textfield when the checkbox was unselected
                        if (textField.isEnabled()) {
                            textField.setFocus();
                        }

                        // send event to update status
                        Event event = new Event();
                        event.widget = textField;
                        modifyListener.modifyText(new ModifyEvent(event));
                    }
                });
            } else {
                // add empty panel to fill area where the checkbox would've been
                WidgetFactory.createPanel(parent);
            }
        }
    }

    private ModifyListener createModifyListener( final EObject column ) {

        ModifyListener modifyListener = new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                Text textField = (Text)e.widget;
                IStatus status = ParameterValueValidator.isValidValue(column, getValue(textField));
                setStatus(status, textField);
            }
        };

        return modifyListener;
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     * @since 5.5.3
     */
    @Override
    public void dispose() {
        for (IChangeListener listener : this.listeners) {
            removeChangeListener(listener);
        }

        super.dispose();
    }

    private void fireStateChanged() {
        if (this.listeners != null) {
            for (IChangeListener listener : this.listeners) {
                listener.stateChanged(this);
            }
        }
    }

    /**
     * @return the ordered list of parameter values
     * @since 5.5.3
     */
    public List<String> getColumnValues() {
        List<String> values = new ArrayList<String>(this.textFields.size());

        for (Text textField : this.textFields) {
            // if textfield is enabled use its value else user wants to use a null value
            values.add(getValue(textField));
        }

        return values;
    }
    
    /**
     * If the checkbox is selected indicating the value should be null then the textField will be disabled.
     * 
     * @param textField the widget being checked
     * @return the text or <code>null</code> if the checkbox is selected
     * @since 6.0.0
     */
    String getValue( Text textField ) {
        return (textField.getEnabled() ? textField.getText() : null);
    }

    /**
     * @return the panel's validation status (never <code>null</code>)
     * @since 5.5.3
     */
    public IStatus getStatus() {
        return this.overallStatus;
    }

    private void postInit() {
        // need to size scroll panel
        Point pt = computeSize(SWT.DEFAULT, SWT.DEFAULT);
        setMinWidth(pt.x);
        setMinHeight(pt.y);
        setExpandHorizontal(true);
        setExpandVertical(true);
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 5.5.3
     */
    public void removeChangeListener( IChangeListener listener ) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    void setStatus( IStatus fieldStatus,
                    Text textField ) {
        int index = this.textFields.indexOf(textField);
        this.statuses[index] = fieldStatus;

        Label lblName = this.names.get(index);
        Label lblIcon = this.images.get(index);
        Image image = null;
        String toolTip = null;

        if ((fieldStatus == null) || (fieldStatus.getSeverity() != IStatus.ERROR)) {
            image = UiPlugin.getDefault().getImage(PluginConstants.Images.BLANK_ICON);
            toolTip = StringUtil.Constants.EMPTY_STRING;
        } else {
            image = UiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON);
            toolTip = fieldStatus.getMessage();
        }

        lblName.setToolTipText(toolTip);
        lblIcon.setImage(image);
        lblIcon.setToolTipText(toolTip);
        textField.setToolTipText(toolTip);

        // calculate overall status
        this.overallStatus = GOOD_STATUS;

        for (IStatus status : this.statuses) {
            if ((status != null) && status.getSeverity() == IStatus.ERROR) {
                this.overallStatus = ERROR_STATUS;
                break;
            }
        }

        fireStateChanged();
    }
}
