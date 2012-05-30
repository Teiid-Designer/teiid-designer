/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views.guides;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;

/**
 * 
 */
public class CheatSheetSection {
    private FormToolkit toolkit;

    private Section section;

    private IConfigurationElement[] cheatsheets;
    // ------------ CHEAT CHEET SCHEMA IDs -----------------------

    private static final String EXT_PT = "cheatSheetContent"; //$NON-NLS-1$
    private static final String ID_ATTR = "id"; //$NON-NLS-1$
    private static final String NAME_ATTR = "name"; //$NON-NLS-1$
    private static final String CHEATSHEET_ELEMENT = "cheatsheet"; //$NON-NLS-1$
    private static final String CHEAT_SHEET_PLUGIN_ID = "org.eclipse.ui.cheatsheets"; //$NON-NLS-1$

    private final DSPPluginImageHelper imageHelper = AdvisorUiPlugin.getImageHelper();

    /**
     * @param parent
     * @param style
     */
    public CheatSheetSection( FormToolkit toolkit,
                                 Composite parent ) {
        super();
        this.toolkit = toolkit;

        int nColumns = 2;
        GridLayout gl2 = new GridLayout(nColumns, false);
        parent.setLayout(gl2);
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.section = this.toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED
                                                          | Section.DESCRIPTION | ExpandableComposite.TWISTIE);
        
        
        loadCheatSheetExtensions();

        initSection();
    }

    private void initSection() {

        this.section.setText(Messages.CheatSheetSection_title);
        this.section.setDescription(Messages.CheatSheetSection_description);
        this.section.getDescriptionControl().setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
        section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridData gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        this.section.setLayoutData(gd);

        Composite sectionBody = this.toolkit.createComposite(section);
        TableWrapLayout tsbLayout = new TableWrapLayout();
        tsbLayout.numColumns = 2;
        tsbLayout.verticalSpacing = 3;
        tsbLayout.horizontalSpacing = 6;
        sectionBody.setLayout(tsbLayout);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL);
        twd.valign = TableWrapData.CENTER;
        sectionBody.setLayoutData(twd);

        Image image = imageHelper.HELP_IMAGE;
        final IConfigurationElement[] cheatSheetExtensions = this.cheatsheets;
        Label lblImage = null;
        Hyperlink link = null;

        for (int i = 0; i < cheatSheetExtensions.length; ++i) {
            String id = cheatSheetExtensions[i].getAttribute(ID_ATTR);
            // Only includes metamatrix cheat sheets for now
            if (id.indexOf("teiid") > -1) { //$NON-NLS-1$
                lblImage = this.toolkit.createLabel(sectionBody, null);
                lblImage.setImage(image);

                link = this.toolkit.createHyperlink(sectionBody, cheatSheetExtensions[i].getAttribute(NAME_ATTR), SWT.WRAP);
                this.toolkit.adapt(link, true, true);

                // create link action
                final IAction action = new CheatSheetLinkAction(id);
                link.addHyperlinkListener(new HyperlinkAdapter() {
                    @Override
                    public void linkActivated( HyperlinkEvent theEvent ) {
                        action.run();
                    }
                });
            }
        }

        sectionBody.pack(true);
        section.setClient(sectionBody);
        section.setExpanded(false);
    }

    /**
     * @return section
     */
    public Section getSection() {
        return section;
    }

    private void loadCheatSheetExtensions() {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CHEAT_SHEET_PLUGIN_ID, EXT_PT);

        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length != 0) {
                List temp = new ArrayList();

                for (int i = 0; i < extensions.length; ++i) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    // only care about cheatsheet configuration elements. don't care about category elements.
                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(CHEATSHEET_ELEMENT)) {
                            temp.add(elements[j]);
                        }
                    }
                }

                if (!temp.isEmpty()) {
                    temp.toArray(this.cheatsheets = new IConfigurationElement[temp.size()]);
                } else {
                    this.cheatsheets = new IConfigurationElement[0];
                }
            }
        }

        if (this.cheatsheets == null) {
            this.cheatsheets = new IConfigurationElement[0];
        }
    }

    private class CheatSheetLinkAction extends Action {
        String linkId;

        public CheatSheetLinkAction( String theCheatSheetId ) {
            this.linkId = theCheatSheetId;
        }

        @Override
        public void run() {

            OpenCheatSheetAction action = new OpenCheatSheetAction(linkId);
            action.run();
        }
    }
}
