/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.datatools.connectivity.ui.plan;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.TransformerFactoryImpl;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.teiid.datatools.connectivity.ui.Activator;

/**
 *
 */
public class ExecutionPlanConverter {

    /**
     * Convenience for java's temp directory
     */
    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$

    /**
     * HTML content displayed if there is no execution plan
     */
    public static final String NO_EXECUTION_PLAN_HTML = "<html><body><div style=\"text-align:center; font-weight:bold; margin-top:4em;\">No Execution Plan Available</div></body></html>"; //$NON-NLS-1$    

    private static final String EXECUTION_PLAN_DATA_HOME = "ExecPlanData"; //$NON-NLS-1$
    private static final String EXECUTION_PLAN_XSLT = EXECUTION_PLAN_DATA_HOME + File.separator + "qp.xslt"; //$NON-NLS-1$
    private static final String EXECUTION_PLAN_DATA_ZIP = EXECUTION_PLAN_DATA_HOME + File.separator + "exec-plan-data.zip"; //$NON-NLS-1$

    private static final String HTML_PREFIX = "execution-plan"; //$NON-NLS-1$
    private static final String HTML_SUFFIX = ".html"; //$NON-NLS-1$

    private Bundle bundle = Activator.getDefault().getBundle();

    private File planDestination = null;

    /**
     * Unzip the data directory to the destination
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void unzipData(File destination) throws IOException, FileNotFoundException {
        byte[] buffer = new byte[1024];
        //get the zip file content
        ZipInputStream zis = new ZipInputStream(FileLocator.openStream(bundle, new Path(EXECUTION_PLAN_DATA_ZIP), true));
        // Get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();
 
        while(ze != null) {
           String fileName = ze.getName();
           File newFile = new File(destination, fileName);

            // Create all directories if they do not already exist
            new File(newFile.getParent()).mkdirs();
 
            if (ze.isDirectory()) {
                newFile.mkdirs();
            }
            else {
                // Read out the file
                FileOutputStream fos = new FileOutputStream(newFile);
 
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
 
                fos.close();
            }

            ze = zis.getNextEntry();
        }
 
        zis.closeEntry();
        zis.close();
    }

    private File getDestination() throws Exception {
        if (planDestination != null)
            return planDestination;

        planDestination  = new File(TEMP_DIRECTORY, "execution-plans");  //$NON-NLS-1$
        if (! planDestination.exists()) {
            // Plan never created before
            if (! planDestination.mkdir()) {
                throw new Exception();
            }
        }

        unzipData(planDestination);
        
        return planDestination;
    }

    private String createOutFileName() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  //$NON-NLS-1$
        String dateStr = sdf.format(cal.getTime());
        return HTML_PREFIX + dateStr + HTML_SUFFIX;
    }

    /**
     * Convert the given execution plan into an HTML document
     *
     * @param execPlan
     *
     * @return url of the resulting html file
     * @throws Exception
     */
    public String convert(String execPlan) throws Exception {
        InputStream xslStream = null;
 
        try {
            xslStream = FileLocator.openStream(bundle, new Path(EXECUTION_PLAN_XSLT), true);
            TransformerFactoryImpl factory = new TransformerFactoryImpl();
            StreamSource xslSource = new StreamSource(xslStream);

            InputStream execPlanStream = new ByteArrayInputStream(execPlan.getBytes("UTF-8")); //$NON-NLS-1$
            
            File destination = getDestination();
            File outHTMLPlan = new File(destination, createOutFileName());

            StreamSource in = new StreamSource(execPlanStream);
            StreamResult out = new StreamResult(outHTMLPlan);

            Transformer transformer = factory.newTransformer(xslSource);
            transformer.transform(in, out);

            return outHTMLPlan.getAbsolutePath();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (xslStream != null)
                xslStream.close();
        }
    }
}
