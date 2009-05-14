/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.sqlexplorer.preferences;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GeneralPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    //public static final String BOLD= "_bold"; //$NON-NLS-1$
    public final OverlayPreferenceStore.OverlayKey[] fKeys = new OverlayPreferenceStore.OverlayKey[] {

    new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, IConstants.PRE_ROW_COUNT),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, IConstants.MAX_SQL_ROWS),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, IConstants.AUTO_COMMIT),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, IConstants.COMMIT_ON_CLOSE),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, IConstants.SQL_ASSIST),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, IConstants.SHOW_QUERY_PLAN),
        new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.LONG, IConstants.XML_CHAR_LIMIT),};

    private IPropertyChangeListener prefListener;
    private IntegerFieldEditor fPreviewRowCountEditor;
    private IntegerFieldEditor fMaxSqlRowEditor;
    private IntegerFieldEditor fMaxXmlCharactersEditor;
    // private IPreferenceStore store;
    OverlayPreferenceStore fOverlayStore;
    Button fAutoCommitBox;
    Button fCommitOnCloseBox;
    Button fAssistance;
    Button fShowQueryPlan;

    public GeneralPreferencePage( OverlayPreferenceStore fOverlayStore ) {
        this.setTitle(Messages.getString("General_Preferences_1")); //$NON-NLS-1$
        this.fOverlayStore = fOverlayStore;
    }

    public void init( IWorkbench workbench ) {
    }

    public GeneralPreferencePage() {

        fOverlayStore = new OverlayPreferenceStore(SQLExplorerPlugin.getDefault().getPreferenceStore(), fKeys);

        fOverlayStore.load();
        fOverlayStore.start();

        this.prefListener = new IPropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent theEvent ) {
                handlePreferenceChange(theEvent);
            }
        };

        this.fOverlayStore.addPropertyChangeListener(this.prefListener);
    }

    @Override
    protected Control createContents( Composite parent ) {
        Composite colorComposite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        colorComposite.setLayout(layout);
        fPreviewRowCountEditor = new EnforceRangeIntegerFieldEditor(
                                                                    IConstants.PRE_ROW_COUNT,
                                                                    Messages.getString("Preview_Max_Rows_3"), colorComposite, 1, 100, 80); //$NON-NLS-1$ 
        fPreviewRowCountEditor.setErrorMessage(Messages.getString("Accepted_Range_is__1_-_100_1")); //$NON-NLS-1$

        fMaxSqlRowEditor = new EnforceRangeIntegerFieldEditor(
                                                              IConstants.MAX_SQL_ROWS,
                                                              Messages.getString("SQL_Limit_Rows_2"), colorComposite, 100, 5000, 2000); //$NON-NLS-1$  
        fMaxSqlRowEditor.setErrorMessage(Messages.getString("Accepted_Range_is__100_-_5000_3")); //$NON-NLS-1$

        fAutoCommitBox = new Button(colorComposite, SWT.CHECK);
        fAutoCommitBox.setText(Messages.getString("GeneralPreferencePage.AutoCommit_1")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 2;
        fAutoCommitBox.setLayoutData(gd);

        fCommitOnCloseBox = new Button(colorComposite, SWT.CHECK);
        fCommitOnCloseBox.setText(Messages.getString("GeneralPreferencePage.Commit_On_Close_2")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 2;
        fCommitOnCloseBox.setLayoutData(gd);

        fAssistance = new Button(colorComposite, SWT.CHECK);
        fAssistance.setText(Messages.getString("GeneralPreferencePage.Tables_and_columns_auto-completing_assistance._Use_only_with_fast_database_connections_1")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 2;
        fAssistance.setLayoutData(gd);

        fAutoCommitBox.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                fOverlayStore.setValue(IConstants.AUTO_COMMIT, fAutoCommitBox.getSelection());
                if (fAutoCommitBox.getSelection()) {
                    fCommitOnCloseBox.setEnabled(false);
                } else fCommitOnCloseBox.setEnabled(true);
            }
        });

        fCommitOnCloseBox.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                fOverlayStore.setValue(IConstants.COMMIT_ON_CLOSE, fCommitOnCloseBox.getSelection());
            }
        });

        fAssistance.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                fOverlayStore.setValue(IConstants.SQL_ASSIST, fAssistance.getSelection());
            }
        });

        fShowQueryPlan = new Button(colorComposite, SWT.CHECK);
        fShowQueryPlan.setText(Messages.getString("GeneralPreferencePage.showQueryPlan")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.horizontalSpan = 2;
        fShowQueryPlan.setLayoutData(gd);

        fShowQueryPlan.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
            }

            public void widgetSelected( SelectionEvent e ) {
                fOverlayStore.setValue(IConstants.SHOW_QUERY_PLAN, fShowQueryPlan.getSelection());
            }
        });

        fMaxXmlCharactersEditor = new EnforceRangeIntegerFieldEditor(
                                                                     IConstants.XML_CHAR_LIMIT,
                                                                     Messages.getString("GeneralPreferencePage.ResultsMaxXmlChars"),
                                                                     colorComposite, 10000, 9999999, 100000); 
        fMaxXmlCharactersEditor.setErrorMessage(Messages.getString("Accepted_Range_is__10000_to_9999999")); //$NON-NLS-1$

        initialize();

        return colorComposite;
    }

    void handlePreferenceChange( PropertyChangeEvent theEvent ) {
        String pref = theEvent.getProperty();

        if (pref.equals(IConstants.MAX_SQL_ROWS)) {
            this.fMaxSqlRowEditor.load();
        } else if (pref.equals(IConstants.PRE_ROW_COUNT)) {
            this.fPreviewRowCountEditor.load();
        } else if (pref.equals(IConstants.XML_CHAR_LIMIT)) {
            this.fMaxXmlCharactersEditor.load();
        } else if (pref.equals(IConstants.COMMIT_ON_CLOSE)) {
            this.fCommitOnCloseBox.setSelection(this.fOverlayStore.getBoolean(IConstants.COMMIT_ON_CLOSE));
        } else if (pref.equals(IConstants.AUTO_COMMIT)) {
            this.fAutoCommitBox.setSelection(this.fOverlayStore.getBoolean(IConstants.AUTO_COMMIT));
            this.fCommitOnCloseBox.setEnabled(!this.fAutoCommitBox.getSelection());
        } else if (pref.equals(IConstants.SQL_ASSIST)) {
            this.fAssistance.setSelection(this.fOverlayStore.getBoolean(IConstants.SQL_ASSIST));
        } else if (pref.equals(IConstants.SHOW_QUERY_PLAN)) {
            this.fShowQueryPlan.setSelection(this.fOverlayStore.getBoolean(IConstants.SHOW_QUERY_PLAN));
        }
    }

    private void initialize() {
        fMaxSqlRowEditor.setPreferenceStore(fOverlayStore);
        fMaxSqlRowEditor.setPreferenceName(IConstants.MAX_SQL_ROWS); 
        fMaxSqlRowEditor.setPage(this);
        fMaxSqlRowEditor.load();

        fPreviewRowCountEditor.setPreferenceStore(fOverlayStore);
        fPreviewRowCountEditor.setPreferenceName(IConstants.PRE_ROW_COUNT); 
        fPreviewRowCountEditor.setPage(this);
        fPreviewRowCountEditor.load();

        fMaxXmlCharactersEditor.setPreferenceStore(fOverlayStore);
        fMaxXmlCharactersEditor.setPreferenceName(IConstants.XML_CHAR_LIMIT); 
        fMaxXmlCharactersEditor.setPage(this);
        fMaxXmlCharactersEditor.load();

        fAutoCommitBox.getDisplay().asyncExec(new Runnable() {
            public void run() {
                fCommitOnCloseBox.setSelection(fOverlayStore.getBoolean(IConstants.COMMIT_ON_CLOSE));
                fAutoCommitBox.setSelection(fOverlayStore.getBoolean(IConstants.AUTO_COMMIT));
                if (fAutoCommitBox.getSelection()) {
                    fCommitOnCloseBox.setEnabled(false);
                } else fCommitOnCloseBox.setEnabled(true);
            }
        });
        fAssistance.getDisplay().asyncExec(new Runnable() {
            public void run() {
                fAssistance.setSelection(fOverlayStore.getBoolean(IConstants.SQL_ASSIST));
            }
        });
        fShowQueryPlan.getDisplay().asyncExec(new Runnable() {
            public void run() {
                fShowQueryPlan.setSelection(fOverlayStore.getBoolean(IConstants.SHOW_QUERY_PLAN));
            }
        });
    }

    @Override
    public void dispose() {
        this.setPreferenceStore(null);
        /*if (fOverlayStore != null) {
        	fOverlayStore.stop();
        	fOverlayStore= null;
        }*/
        if (fPreviewRowCountEditor != null) {
            fPreviewRowCountEditor.setPreferenceStore(null);
            fPreviewRowCountEditor.setPage(null);
        }
        if (fMaxSqlRowEditor != null) {
            fMaxSqlRowEditor.setPreferenceStore(null);
            fMaxSqlRowEditor.setPage(null);
        }
        if (fMaxXmlCharactersEditor != null) {
            fMaxXmlCharactersEditor.setPreferenceStore(null);
            fMaxXmlCharactersEditor.setPage(null);
        }
        // remove preference change listener
        if ((this.fOverlayStore != null) && (this.prefListener != null)) {
            this.fOverlayStore.removePropertyChangeListener(this.prefListener);
        }

        super.dispose();
    }

    @Override
    public boolean performOk() {
        if (fPreviewRowCountEditor != null) {
            fPreviewRowCountEditor.store();

        }
        if (fMaxSqlRowEditor != null) {
            fMaxSqlRowEditor.store();
        }
        if (fMaxXmlCharactersEditor != null) {
            fMaxXmlCharactersEditor.store();
        }
        (fOverlayStore).propagate();
        return true;
    }

    @Override
    protected void performDefaults() {
        (fOverlayStore).loadDefaults();
        if (fPreviewRowCountEditor != null) {
            fPreviewRowCountEditor.loadDefault();
            // System.out.println("Previouw defaul value:="+fPreviewRowCountEditor.getIntValue());
        }
        if (fMaxSqlRowEditor != null) {
            fMaxSqlRowEditor.loadDefault();
        }
        if (fMaxXmlCharactersEditor != null) {
            fMaxXmlCharactersEditor.loadDefault();
        }

        super.performDefaults();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // EnforceRangeIntegerFieldEditor INNER CLASS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private class EnforceRangeIntegerFieldEditor extends IntegerFieldEditor {
        private int minValue;
        private int maxValue;
        private int defaultValue;

        public EnforceRangeIntegerFieldEditor( String theName,
                                               String theText,
                                               Composite theParent,
                                               int theMinValue,
                                               int theMaxValue,
                                               int theDefaultValue ) {
            super(theName, theText, theParent);

            this.minValue = theMinValue;
            this.maxValue = theMaxValue;
            this.defaultValue = theDefaultValue;

            setValidRange(this.minValue, this.maxValue);
        }

        /**
         * @see org.eclipse.jface.preference.IntegerFieldEditor#doLoad()
         * @since 4.3
         */
        @Override
        protected void doLoad() {
            Text text = getTextControl();

            if (text != null) {
                int value = getPreferenceStore().getInt(getPreferenceName());
                value = getValue(new Integer(value));
                text.setText("" + value);//$NON-NLS-1$
            }
        }

        /**
         * @see org.eclipse.jface.preference.IntegerFieldEditor#doStore()
         * @since 4.3
         */
        @Override
        protected void doStore() {
            Text text = getTextControl();

            if (text != null) {
                int value = this.maxValue;

                try {
                    Integer proposedValue = new Integer(text.getText());
                    value = getValue(proposedValue);
                } catch (NumberFormatException theException) {
                    value = this.defaultValue;
                }

                getPreferenceStore().setValue(getPreferenceName(), value);
            }
        }

        private int getValue( Integer theProposedValue ) {
            int result = this.defaultValue;
            int intValue = theProposedValue.intValue();

            if (intValue < this.minValue) {
                result = this.minValue;
            } else if (intValue > this.maxValue) {
                result = this.maxValue;
            } else {
                result = intValue;
            }

            return result;
        }
    }

}
