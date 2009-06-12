/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;


public class InsertRowsDialog extends TitleAreaDialog
                           implements UiConstants,
                                      PluginConstants {

    private Spinner rowSpinner = null;
    private int currentRowValue = 1;
    private CLabel lblMessage;

    private static final String INSERT_ROWS_MESSAGE = UiConstants.Util.getString("InsertRowsDialog.message"); //$NON-NLS-1$
    private static final String INSERT_ROWS_MIN = UiConstants.Util.getString("InsertRowsDialog.min"); //$NON-NLS-1$
    private static final String INSERT_ROWS_MAX = UiConstants.Util.getString("InsertRowsDialog.max"); //$NON-NLS-1$
    private static final String INSERT_ROWS_DYNAMIC_MESSAGE_SINGULAR = UiConstants.Util.getString("InsertRowsDialog.dynamicMessageSingular"); //$NON-NLS-1$
    private static final String INSERT_ROWS_DYNAMIC_MESSAGE_PLURAL = UiConstants.Util.getString("InsertRowsDialog.dynamicMessagePlural"); //$NON-NLS-1$
    private static final String DIALOG_TITLE = UiConstants.Util.getString("InsertRowsDialog.title"); //$NON-NLS-1$
    private static final String INSERT_ROWS_TITLE = UiConstants.Util.getString("InsertRowsDialog.componentTitle",INSERT_ROWS_MAX); //$NON-NLS-1$

    private static final String OUT_OF_RANGE_MESSAGE 
        = UiConstants.Util.getString( "InsertRowsDialog.outOfRange.message", //$NON-NLS-1$ 
                                   INSERT_ROWS_MIN, 
                                   INSERT_ROWS_MAX ); 
   
    public InsertRowsDialog( Shell parentShell, Image img ) {
        super( parentShell );
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DIALOG_TITLE);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite pnlOuter = (Composite)super.createDialogArea(parent);
        
        Composite composite = new Composite( pnlOuter, SWT.NONE );
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout( gridLayout );

        setTitle( INSERT_ROWS_TITLE );         

        // message
        lblMessage = WidgetFactory.createLabel( composite );
        lblMessage.setText( INSERT_ROWS_MESSAGE );

        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = GridData.CENTER;
        lblMessage.setLayoutData( gridData1 );
        
        // spinner
        rowSpinner = new Spinner(composite, SWT.BORDER);
        rowSpinner.setIncrement(1);
        rowSpinner.setMaximum(Integer.parseInt( INSERT_ROWS_MAX ));
        rowSpinner.setMinimum(Integer.parseInt( INSERT_ROWS_MIN ));

        rowSpinner.addModifyListener( modifyListener );
        
        rowSpinner.setEnabled( true );

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.CENTER;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.minimumWidth = 100;
        rowSpinner.setLayoutData( gridData2 );               

        // update the message
        updateDynamicMessage();
    
        return composite;
    }

     public void updateDynamicMessage() {
//"x rows will be inserted"

        if ( rowSpinner.getSelection() < 0 ) {
            setMessage( OUT_OF_RANGE_MESSAGE, IMessageProvider.ERROR );
            return;
        }
        
        int iSpinnerValue = ( rowSpinner.getSelection() > 0 ) ? rowSpinner.getSelection() : 1;
        currentRowValue = iSpinnerValue;
        if ( iSpinnerValue == 1 ) {            
            setMessage( String.valueOf( currentRowValue )
                       + " " //$NON-NLS-1$
                       + INSERT_ROWS_DYNAMIC_MESSAGE_SINGULAR, IMessageProvider.NONE );
        } else {        
             setMessage( String.valueOf( currentRowValue )
                        + " " //$NON-NLS-1$
                        + INSERT_ROWS_DYNAMIC_MESSAGE_PLURAL, IMessageProvider.NONE );
        }
     }

     private ModifyListener modifyListener = new ModifyListener() {
         public void modifyText( ModifyEvent theEvent ) {
             updateDynamicMessage();
         }
     };
        
    public int getCount() {
        return currentRowValue;
    }

}
