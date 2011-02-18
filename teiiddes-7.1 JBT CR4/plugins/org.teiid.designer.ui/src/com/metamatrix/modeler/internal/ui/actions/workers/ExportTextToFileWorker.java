/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.workers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;



/**
 * Simple Text file export worker.
 * This class provides actions, views and editors the ability to export a desired text object to a file.
 * @since 5.0
 */
public class ExportTextToFileWorker {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ExportTextToFileWorker.class);

    private static final String STAR_DOT_STAR = "*.*"; //$NON-NLS-1$
    private static final String EXPORT_PROBLEM = UiConstants.Util.getString("ExportTextToFileWorker.exportError"); //$NON-NLS-1$
    private static final char PERIOD = '.';


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private String fileDialogTitle;
    private String defaultFileName;
    private String defaultFileExt;
    private String text;
    private String header;
    private String fileName;
    private String folder = CoreStringUtil.Constants.EMPTY_STRING;

    /**
     *
     * @since 5.0
     */
    public ExportTextToFileWorker() {
        super();
    }

    /**
     *
     * @since 5.0
     */
    public ExportTextToFileWorker(String title, String defFileName, String defFileExt, String header, String text) {
        super();

        this.fileDialogTitle = title;
        this.defaultFileName = defFileName;
        this.defaultFileExt = defFileExt;
        this.text = text;
        this.header = header;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Export the current string content of the sql display to a user-selected file
     * @return <code>true</code> if export was canceled
     */
     public boolean export() {
         this.folder = CoreStringUtil.Constants.EMPTY_STRING;
         this.fileName = null;

         boolean cancelled = false;
         Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
         FileDialog dlg=new FileDialog(shell,SWT.SAVE);
         dlg.setFilterExtensions(new String[]{STAR_DOT_STAR});
         dlg.setText(fileDialogTitle);
         dlg.setFileName(defaultFileName);

         String fileNameStr = dlg.open();

         // If there is no file extension, add default
         if(fileNameStr!=null && fileNameStr.indexOf(PERIOD)==-1) {
             fileNameStr = fileNameStr+ PERIOD + defaultFileExt;
         }

         boolean overwrite = true;

         // if file already exists ask user if they want to overwrite
         if (fileNameStr != null && new File(fileNameStr).exists()) {
             overwrite = MessageDialog.openConfirm(shell,
                                                   UiConstants.Util.getStringOrKey(PREFIX + "confirmOverwrite.title"), //$NON-NLS-1$
                                                   UiConstants.Util.getStringOrKey(PREFIX + "confirmOverwrite.msg")); //$NON-NLS-1$
         }

         if(fileNameStr!=null && overwrite ) {
             fileName = fileNameStr;
             this.folder = dlg.getFilterPath();
             FileWriter fw=null;
             BufferedWriter out=null;
             PrintWriter pw=null;
             try{
                 fw=new FileWriter(fileNameStr);
                 out = new BufferedWriter(fw);
                 pw=new PrintWriter(out);

                 pw.write(getFullText());

             }catch(Exception e){
                 String msg = EXPORT_PROBLEM;
                 UiConstants.Util.log(IStatus.ERROR, e, msg);
                 cancelled = true;
             }
             finally{
                 pw.close();
                 try{
                     out.close();
                 }catch(java.io.IOException e){}
                 try{
                     fw.close();
                 }catch(java.io.IOException e){}
             }
         } else {
             cancelled = true;
         }

         return cancelled;
     }

     private String getFullText() {
         int headerLength = (this.header == null) ? 0 : this.header.length();
         StringBuffer sb = new StringBuffer(text.length() + headerLength + 10);
         if( headerLength > 0 ) {
             sb.append(header).append(CoreStringUtil.Constants.NEW_LINE_CHAR);
         }
         sb.append(text);

         return sb.toString();
     }


    public String getFileName() {
        return this.fileName;
    }

    /**
     * @return the output directory or an empty string if not set
     * @see 5.5.3
     */
    public String getFolder() {
        return this.folder;
    }
}
