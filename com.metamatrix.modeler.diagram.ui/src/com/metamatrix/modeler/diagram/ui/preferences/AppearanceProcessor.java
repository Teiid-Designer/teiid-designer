/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.diagram.ui.preferences;

import java.util.Iterator;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.diagram.ui.DiagramTypeManager;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * This class represents the preference page for setting the Diagram Appearance Preferences.
 */
public class AppearanceProcessor implements DiagramUiConstants, PluginConstants {

    private ColorObjectAndSelector[] colorInfo;
    private Button changeFontButton;
    private Label fontLabel;
    private IPreferenceStore preferenceStore;
    private Shell shell;
    private FontData currentFontData;

    public AppearanceProcessor( IPreferenceStore preferenceStore,
                                Shell shell ) {
        super();
        this.preferenceStore = preferenceStore;
        this.shell = shell;
    }

    public Control createContents( Composite parent ) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        comp.setLayout(layout);

        String fontGroupHdr = Util.getString("DiagramAppearancePrefPage.fontBorderText"); //$NON-NLS-1$  
        Group fontGroup = WidgetFactory.createGroup(comp, fontGroupHdr, GridData.FILL_HORIZONTAL, 1, 3);

        Label fontTypeLabel = new Label(fontGroup, SWT.NONE);
        String fontTypeLabelStr = Util.getString("DiagramAppearancePrefPage.fontType"); //$NON-NLS-1$
        fontTypeLabel.setText(fontTypeLabelStr);
        fontLabel = new Label(fontGroup, SWT.NONE);
        GridData fontLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
        fontLabelGridData.horizontalIndent = 6;
        fontLabel.setLayoutData(fontLabelGridData);
        changeFontButton = new Button(fontGroup, SWT.PUSH);
        String changeFontStr = Util.getString("DiagramAppearancePrefPage.change"); //$NON-NLS-1$
        changeFontButton.setText(changeFontStr);
        changeFontButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                changeFontButtonPressed();
            }
        });

        String backgroundColorGroupHdr = Util.getString("DiagramAppearancePrefPage.backgrdColorBorderText"); //$NON-NLS-1$
        Group backgroundGroup = WidgetFactory.createGroup(comp, backgroundColorGroupHdr, GridData.FILL_HORIZONTAL, 1, 4);

        DiagramTypeManager mgr = DiagramUiPlugin.getDiagramTypeManager();
        java.util.List /*<DiagramColorObject>*/colorInfoList = mgr.getDiagramColorInfo();
        colorInfo = new ColorObjectAndSelector[colorInfoList.size()];
        Iterator it = colorInfoList.iterator();
        for (int i = 0; it.hasNext(); i++) {
            GridData gdForLabels = new GridData();
            gdForLabels.horizontalAlignment = GridData.END;

            GridData gdForColorButtons = new GridData();
            gdForColorButtons.horizontalAlignment = GridData.BEGINNING;

            DiagramColorObject dco = (DiagramColorObject)it.next();
            Label diagramColorLabel = new Label(backgroundGroup, SWT.NONE);
            diagramColorLabel.setLayoutData(gdForLabels);

            String diagramLabelStr = dco.getDisplayName() + ':';
            diagramColorLabel.setText(diagramLabelStr);
            ColorSelector sel = new ColorSelector(backgroundGroup);
            RGB color = dco.getPreferenceValue();
            sel.setColorValue(color);
            colorInfo[i] = new ColorObjectAndSelector(dco, sel);
            sel.getButton().setLayoutData(gdForColorButtons);
        }
        FontData fontData = PreferenceConverter.getFontData(preferenceStore, PluginConstants.Prefs.Appearance.FONT);
        setFontData(fontData);
        return comp;
    }

    private void setFontData( FontData fontData ) {
        String name = fontData.getName();
        int height = fontData.getHeight();
        int style = fontData.getStyle();
        boolean isBold = ((style & SWT.BOLD) != 0);
        boolean isItalic = ((style & SWT.ITALIC) != 0);
        String styleStr = ""; //$NON-NLS-1$
        if (isBold) {
            styleStr += Util.getString("DiagramAppearancePrefPage.bold"); //$NON-NLS-1$
        }
        if (isItalic) {
            if (styleStr.length() > 0) {
                styleStr += " "; //$NON-NLS-1$
            }
            styleStr += Util.getString("DiagramAppearancePrefPage.italic"); //$NON-NLS-1$
        }
        if (styleStr.length() == 0) {
            styleStr = Util.getString("DiagramAppearancePrefPage.regular"); //$NON-NLS-1$
        }
        String fontStr = name + " - " + styleStr + " - " + height; //$NON-NLS-1$ //$NON-NLS-2$ 
        fontLabel.setText(fontStr);
        currentFontData = fontData;
    }

    void changeFontButtonPressed() {
        FontDialog dialog = new FontDialog(shell);
        FontData[] fontDataArray = new FontData[] {currentFontData};
        dialog.setFontList(fontDataArray);
        FontData newFontData = dialog.open();
        if (newFontData != null) {
            if (!currentFontData.equals(newFontData)) {
                setFontData(newFontData);
            }
        }
    }

    public boolean performOk() {
        PreferenceConverter.setValue(preferenceStore, PluginConstants.Prefs.Appearance.FONT, currentFontData);
        for (int i = 0; i < colorInfo.length; i++) {
            PreferenceConverter.setValue(preferenceStore,
                                         colorInfo[i].getColorObject().getRGBPreferenceKey(),
                                         colorInfo[i].getSelector().getColorValue());
        }
        DiagramUiPlugin.getDefault().savePluginPreferences();
        return true;
    }

    public void performDefaults() {
        currentFontData = PreferenceConverter.getDefaultFontData(preferenceStore, PluginConstants.Prefs.Appearance.FONT);
        setFontData(currentFontData);
        for (int i = 0; i < colorInfo.length; i++) {
            RGB color = PreferenceConverter.getDefaultColor(preferenceStore, colorInfo[i].getColorObject().getRGBPreferenceKey());
            if (color != null) {
                colorInfo[i].getSelector().setColorValue(color);
            }
        }
    }
}// end AppearancePreferencePage

class ColorObjectAndSelector {
    private DiagramColorObject colorObject;
    private ColorSelector selector;

    public ColorObjectAndSelector( DiagramColorObject colorObject,
                                   ColorSelector selector ) {
        super();
        this.colorObject = colorObject;
        this.selector = selector;
    }

    public DiagramColorObject getColorObject() {
        return colorObject;
    }

    public ColorSelector getSelector() {
        return selector;
    }
}// end ColorObjectAndSelector
