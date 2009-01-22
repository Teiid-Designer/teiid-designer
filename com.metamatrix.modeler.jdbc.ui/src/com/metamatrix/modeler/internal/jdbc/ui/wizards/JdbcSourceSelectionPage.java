/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 4.0
 */
public class JdbcSourceSelectionPage extends AbstractWizardPage implements
                                                               IChangeNotifier,
                                                               InternalModelerJdbcUiPluginConstants,
                                                               InternalModelerJdbcUiPluginConstants.Widgets,
                                                               InternalUiConstants.Widgets,
                                                               StringUtil.Constants {

    // ===========================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcSourceSelectionPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;
    private static final int EDIT_PANEL_COLUMN_COUNT = 2;

    private static final String SOURCE_LABEL = getString("sourceLabel"); //$NON-NLS-1$
    private static final String SOURCES_BUTTON = getString("sourcesButton"); //$NON-NLS-1$

    private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$
    

    // ===========================================================================================================================
    // Static Methods

    /**
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    // ===========================================================================================================================
    // Variables

    private JdbcManager mgr;
    private JdbcSource src;
    private Connection connection;
    private ListenerList notifier;
    private String password;

    private ILabelProvider srcLabelProvider;
    private Combo srcCombo;
    private Composite editPanel;
    private CLabel driverLabel, urlLabel, userNameLabel;
    private Text pwdText;
    private Map enableMap;

    // ===========================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public JdbcSourceSelectionPage() {
        this(null);
    }

    /**
     * @since 4.0
     */
    public JdbcSourceSelectionPage(final JdbcSource source) {
        super(JdbcSourceSelectionPage.class.getSimpleName(), TITLE);
        this.src = source;
        this.mgr = JdbcUiUtil.getJdbcManager();
        this.notifier = new ListenerList(ListenerList.IDENTITY);
        // Set page incomplete initially
        setPageComplete(false);
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @since 4.0
     */
    public void addChangeListener(final IChangeListener listener) {
        this.notifier.add(listener);
    }

    /**
     * Creates a connection to the JDBC source if one has not already been established.
     * 
     * @return True if a connection has been successfully established (possibly in a prior call to this method).
     * @since 5.0
     */
    public boolean connect() {
        if (this.connection == null) {
            this.connection = JdbcUiUtil.connect(getSource(), getPassword());
            if (this.connection == null) {
                return false;
            }
            fireStateChanged();
        }
        return true;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl(final Composite parent) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pg);
        // Add widgets to page
        WidgetFactory.createLabel(pg, SOURCE_LABEL);
        ArrayList sourceList = new ArrayList(this.mgr.getJdbcSources().size());
        for (Iterator iter = this.mgr.getJdbcSources().iterator(); iter.hasNext();) {
            Object source = iter.next();
            if (source != null && !sourceList.contains(source)) {
                sourceList.add(source);
            }
        }
        this.srcLabelProvider = new LabelProvider() {

            @Override
            public String getText(final Object source) {
                return ((JdbcSource)source).getName();
            }
        };
        this.srcCombo = WidgetFactory.createCombo(pg,
                                                  SWT.READ_ONLY,
                                                  GridData.FILL_HORIZONTAL,
                                                  sourceList,
                                                  this.src,
                                                  this.srcLabelProvider,
                                                  true);
        this.srcCombo.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                sourceModified();
            }
        });
        
        this.srcCombo.setVisibleItemCount(10);
        
        WidgetFactory.createButton(pg, SOURCES_BUTTON).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                launchSourceWizard();
            }
        });
        this.editPanel = WidgetFactory.createPanel(pg,
                                                   SWT.NO_TRIM,
                                                   GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL,
                                                   COLUMN_COUNT,
                                                   EDIT_PANEL_COLUMN_COUNT);
        WidgetFactory.createLabel(this.editPanel, DRIVER_LABEL);
        this.driverLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, URL_LABEL);
        this.urlLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, USER_NAME_LABEL);
        this.userNameLabel = WidgetFactory.createLabel(this.editPanel, GridData.FILL_HORIZONTAL);
        WidgetFactory.createLabel(this.editPanel, PASSWORD_LABEL);
        this.pwdText = WidgetFactory.createTextField(this.editPanel, GridData.FILL_HORIZONTAL);
        this.pwdText.setEchoChar('*');
        this.pwdText.addModifyListener(new ModifyListener() {

            public void modifyText(final ModifyEvent event) {
                passwordModified();
            }
        });
        sourceModified();

        if( validatePage() ) {
            setMessage(INITIAL_MESSAGE);
        }

    }

    /**
     * @since 4.0
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
     * @since 5.0
     */
    @Override
    public IWizardPage getNextPage() {
        if (!connect()) {
            return null;
        }
        return super.getNextPage();
    }

    /**
     * @since 4.0
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @since 4.0
     */
    public JdbcSource getSource() {
        return this.src;
    }

    /**
     * @since 4.0
     */
    void launchSourceWizard() {
        final JdbcSourceWizard wizard = new JdbcSourceWizard();
        final WizardDialog dlg = WidgetFactory.createOnePageWizardDialog(getShell(), wizard);
        wizard.setSelection(this.src);
        if (dlg.open() == Window.OK) {
            // Update src combo, preserving any previous selection
            WidgetUtil.setComboItems(this.srcCombo, this.mgr.getJdbcSources(), this.srcLabelProvider, true);
            final JdbcSource src = wizard.getSelection();
            if (src != null && this.src != src) {
                WidgetUtil.setComboText(this.srcCombo, src, this.srcLabelProvider);
            }
            // Update password field
            String pwd = wizard.getPassword();
            if (pwd != null) {
                this.pwdText.setText(pwd);
            }
        }
    }

    /**
     * @since 4.0
     */
    void passwordModified() {
        this.password = this.pwdText.getText();
        this.connection = null;
    }

    /**
     * @since 4.0
     */
    public void removeChangeListener(final IChangeListener listener) {
        this.notifier.remove(listener);
    }

    /**
     * @since 4.0
     */
    void sourceModified() {
        final String text = this.srcCombo.getText();
        if (text.length() > 0) {
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
            this.src = this.mgr.findSources(text)[0];
            this.driverLabel.setText(this.src.getDriverName());
            this.urlLabel.setText(this.src.getUrl());
            JdbcUiUtil.setText(this.userNameLabel, this.src.getUsername());
        } else {
            this.src = null;
            this.driverLabel.setText(EMPTY_STRING);
            this.urlLabel.setText(EMPTY_STRING);
            this.userNameLabel.setText(EMPTY_STRING);
            if (this.enableMap == null) {
                this.enableMap = WidgetUtil.disable(this.editPanel);
            }
        }
        this.connection = null;
        validatePage();
    }

    /**
     * @since 4.0
     */
    private boolean validatePage() {
        // Check for at least ONE open non-hidden Model Project
        boolean validProj = false;
        for( IProject proj: ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            try {
                boolean result =  proj.isOpen() && 
                                 !proj.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID) &&
                                 proj.hasNature(ModelerCore.NATURE_ID);
                if( result ) {
                    validProj = true;
                    break;
                }
            } catch (CoreException e) {
                UiConstants.Util.log(e);
            }
        }
        
        if( !validProj ) {
            WizardUtil.setPageComplete(this, getString("noOpenProjectsMessage"), ERROR); //$NON-NLS-1$
        } else if (this.srcCombo.getText().length() == 0) {
            WizardUtil.setPageComplete(this, INVALID_PAGE_MESSAGE, ERROR);
        } else {
            WizardUtil.setPageComplete(this);
        }
        fireStateChanged();
        
        return validProj;
    }
    
    private void fireStateChanged() {
        Object[] listeners = this.notifier.getListeners();
        
        for (Object listener : listeners) {
            ((IChangeListener)listener).stateChanged(this);
        }
    }
}
