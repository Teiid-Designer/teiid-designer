/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.editor;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent.TargetType;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * @since 8.0
 */
public class TeiidServerEditor extends EditorPart {

    /**
     * Identifier of this editor
     */
    public static final String EDITOR_ID = TeiidServerEditor.class.getCanonicalName();

    /**
     * Flag indicating editor's dirty status
     */
    private boolean dirty = false;
    
    private TeiidServerManager serverManager;
    
    private ITeiidServer teiidServer;
    
    private ScrolledForm form;
    private FormToolkit toolkit;

    private Text customNameText;

    private Hyperlink jbServerNameHyperlink;

    private Text hostNameText;
    
    private Text versionText;

    private FormText adminDescriptionText;
    
    private Button adminSSLCheckbox;

    private Hyperlink adminPingHyperlink;

    private Label adminPingResultLabel;

    private Text jdbcUserNameText;

    private Text jdbcPasswdText;

    private Label jdbcPort;
    
    private Button jdbcSSLCheckbox;

    private Hyperlink jdbcPingHyperlink;

    private Label jdbcPingResultLabel;

    private HyperlinkAdapter parentServerHyperlinkAdapter = new HyperlinkAdapter() {
        
        @Override
        public void linkActivated(HyperlinkEvent e) {
            ServerUIPlugin.editServer(teiidServer.getParent());
        }
    };
    
    private IExecutionConfigurationListener excutionConfigListener = new IExecutionConfigurationListener() {
        
        @Override
        public void configurationChanged(ExecutionConfigurationEvent event) {
            if (event.getTargetType() != TargetType.SERVER || teiidServer != event.getServer())
                return;
            
            refreshDisplayValues();
        }
    };

    private KeyAdapter dirtyKeyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            TeiidServerEditor.this.setDirty();
        }
    };
    
    private SelectionListener dirtySelectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            TeiidServerEditor.this.setDirty();
        }
    };
    
    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        if (input instanceof TeiidServerEditorInput) {
            TeiidServerEditorInput tsei = (TeiidServerEditorInput) input;
            teiidServer = tsei.getTeiidServer();
            serverManager = DqpPlugin.getInstance().getServerManager();
            
            serverManager.addListener(excutionConfigListener);
        }
    }
    
    @Override
    public void createPartControl(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        toolkit.decorateFormHeading(form.getForm());
        form.setText(UTIL.getString("TeiidServerEditor.title")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().applyTo(form.getBody());
        
        Composite columnComp = toolkit.createComposite(form.getBody());
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(10, 0).applyTo(columnComp);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(columnComp);
        
        // left column
        Composite leftColumnComp = toolkit.createComposite(columnComp);
        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 10).applyTo(leftColumnComp);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(leftColumnComp);
        
        // insert sections
        if (teiidServer != null) {
            createOverviewSection(leftColumnComp);
            createAdminSection(leftColumnComp);
        }
        
        // right column
        Composite rightColumnComp = toolkit.createComposite(columnComp);
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(10, 0).applyTo(rightColumnComp);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(rightColumnComp);
        
        if (teiidServer != null) {
            createJDBCSection(leftColumnComp);
        }
        
        // insert sections
        form.reflow(true);
    }
    
    private void blueForeground(Control control) {
        control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
    }

    /**
     * @param parent
     * @param toolkit
     */
    private void createOverviewSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerOverviewSection.title")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        Label customNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.customLabel")); //$NON-NLS-1$
        blueForeground(customNameLabel);
        
        customNameText = toolkit.createText(composite, teiidServer.getCustomLabel() != null ? teiidServer.getCustomLabel() : ""); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(customNameText);
        customNameText.addKeyListener(dirtyKeyListener);
        
        Label hostLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.hostLabel")); //$NON-NLS-1$
        blueForeground(hostLabel);
        
        hostNameText = toolkit.createText(composite, teiidServer.getHost());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(hostNameText);
        hostNameText.setEditable(false);
        hostNameText.setEnabled(false);
        
        Label versionLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.versionLabel")); //$NON-NLS-1$
        blueForeground(versionLabel);
        
        versionText = toolkit.createText(composite, teiidServer.getServerVersion().toString());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(versionText);
        versionText.setEditable(false);
        versionText.setEnabled(false);
        
        Label jbLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerOverviewSection.jbLabel")); //$NON-NLS-1$
        blueForeground(jbLabel);
        
        String jbServerName = teiidServer.getParent() != null ? teiidServer.getParent().getName() : ""; //$NON-NLS-1$
        jbServerNameHyperlink = toolkit.createHyperlink(composite, jbServerName, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jbServerNameHyperlink);
        jbServerNameHyperlink.addHyperlinkListener(parentServerHyperlinkAdapter);
        
        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }
    
    private void createAdminSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerAdminSection.title")); //$NON-NLS-1$ 
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
       
        adminDescriptionText = WidgetFactory.createFormText(composite, toolkit, UTIL.getString("TeiidServerAdminSection.description"), parentServerHyperlinkAdapter); //$NON-NLS-1$
        blueForeground(adminDescriptionText);
        GridDataFactory.fillDefaults().grab(false, false).span(2, 1).applyTo(adminDescriptionText);
        
        if (ITeiidServerVersion.SEVEN.equals(teiidServer.getServerVersion().getMajor())) {
            Label checkboxLabel = toolkit.createLabel(composite, UTIL.getString("serverPageSecureConnAdminLabel")); //$NON-NLS-1$
            GridDataFactory.fillDefaults().grab(true, false).applyTo(checkboxLabel);
            blueForeground(checkboxLabel);
            
            adminSSLCheckbox = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
            adminSSLCheckbox.setSelection(teiidServer.getTeiidAdminInfo().isSecure());
            adminSSLCheckbox.addSelectionListener(dirtySelectionListener);
            GridDataFactory.fillDefaults().grab(false, false).applyTo(adminSSLCheckbox);
        }
        
        adminPingHyperlink = toolkit.createHyperlink(composite, UTIL.getString("TeiidServerAdminSection.testPingButtonLabel"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(adminPingHyperlink);
        adminPingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            
            @Override
            public void linkActivated(HyperlinkEvent e) {
                doSave(null);

                IStatus status = teiidServer.testPing();
                adminPingResultLabel.setText(status.getMessage());
                form.layout(true, true);
            }
        });
        
        adminPingResultLabel = toolkit.createLabel(composite, "", SWT.WRAP); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).grab(true, true).hint(325, SWT.DEFAULT).applyTo(adminPingResultLabel);
        
        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }

    /**
     * @param parent
     */
    private void createJDBCSection(Composite parent) {
        Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
        section.setText(UTIL.getString("TeiidServerJDBCSection.title")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(section);
        
        Composite composite = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).margins(5, 10).spacing(5, 20).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
        
        Label userNameLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.userNameLabel")); //$NON-NLS-1$
        blueForeground(userNameLabel);
        
        jdbcUserNameText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getUsername());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcUserNameText);
        jdbcUserNameText.addKeyListener(dirtyKeyListener);
        
        Label passwdLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.passwordLabel")); //$NON-NLS-1$
        blueForeground(passwdLabel);
        
        jdbcPasswdText = toolkit.createText(composite, teiidServer.getTeiidJdbcInfo().getPassword(), SWT.PASSWORD);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPasswdText);
        jdbcPasswdText.addKeyListener(dirtyKeyListener);
        
        Label portLabel = toolkit.createLabel(composite, UTIL.getString("TeiidServerJDBCSection.portLabel")); //$NON-NLS-1$
        blueForeground(portLabel);
        
        jdbcPort = toolkit.createLabel(composite, teiidServer.getTeiidJdbcInfo().getPort());
        blueForeground(jdbcPort);
        
        Label checkboxLabel = toolkit.createLabel(composite, UTIL.getString("serverPageSecureConnJDBCLabel")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(checkboxLabel);
        blueForeground(checkboxLabel);
        
        jdbcSSLCheckbox = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
        jdbcSSLCheckbox.setSelection(teiidServer.getTeiidJdbcInfo().isSecure());
        blueForeground(jdbcSSLCheckbox);
        jdbcSSLCheckbox.addSelectionListener(dirtySelectionListener);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(jdbcSSLCheckbox);
        
        jdbcPingHyperlink = toolkit.createHyperlink(composite, UTIL.getString("TeiidServerJDBCSection.testPingButtonLabel"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(jdbcPingHyperlink);
        jdbcPingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {                
                doSave(null);

                IStatus status = teiidServer.testJDBCPing(teiidServer.getHost(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getPort(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getUsername(), 
                                                                                 teiidServer.getTeiidJdbcInfo().getPassword());
                jdbcPingResultLabel.setText(status.getMessage());
                form.layout(true, true);
            }
        });
        
        jdbcPingResultLabel = toolkit.createLabel(composite, "", SWT.WRAP); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.FILL).grab(true, true).hint(325, SWT.DEFAULT).applyTo(jdbcPingResultLabel);
        
        toolkit.paintBordersFor(composite);
        section.setClient(composite);
    }
    
    private void refreshDisplayValues() {
        this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                customNameText.setText(teiidServer.getCustomLabel() != null ? teiidServer.getCustomLabel() : ""); //$NON-NLS-1$
                hostNameText.setText(teiidServer.getHost());
                versionText.setText(teiidServer.getServerVersion().toString());
                jbServerNameHyperlink.setText(teiidServer.getParent() != null ? teiidServer.getParent().getName() : ""); //$NON-NLS-1$
                jdbcUserNameText.setText(teiidServer.getTeiidJdbcInfo().getUsername());
                jdbcPasswdText.setText(teiidServer.getTeiidJdbcInfo().getPassword());
                jdbcPort.setText(teiidServer.getTeiidJdbcInfo().getPort());
            }
        });
    }
    
    @Override
    public void dispose() {
        serverManager.removeListener(excutionConfigListener);
        super.dispose();
    }
    
    @Override
    public void setFocus() {
        if(customNameText != null) {
            customNameText.setFocus();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // Overwrite the properties of the teiid server
        teiidServer.setCustomLabel(customNameText.getText());
        
        if (adminSSLCheckbox != null) {
            ITeiidAdminInfo adminInfo = teiidServer.getTeiidAdminInfo();
            adminInfo.setSecure(adminSSLCheckbox.getSelection());
        }
        
        ITeiidJdbcInfo jdbcInfo = teiidServer.getTeiidJdbcInfo();
        jdbcInfo.setUsername(jdbcUserNameText.getText());
        jdbcInfo.setPassword(jdbcPasswdText.getText());
        jdbcInfo.setSecure(jdbcSSLCheckbox.getSelection());
        
        dirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
        
        serverManager.notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(teiidServer));
    }

    @Override
    public void doSaveAs() {
        // do nothing
    }
    
    private void setDirty() {
        dirty = true;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

}
