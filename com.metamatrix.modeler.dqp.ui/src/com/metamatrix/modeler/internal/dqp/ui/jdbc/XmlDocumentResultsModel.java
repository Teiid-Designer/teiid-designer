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

package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/**
 * @since 4.3
 */
public class XmlDocumentResultsModel implements DqpUiConstants,
                                                IResults {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final IStatus GOOD_STATUS = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$

    private static final String[] NO_RESULTS = new String[0];

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private String comment;

    private String[] results = NO_RESULTS;

    private String sql;

    private Statement statement;

    private IStatus status = GOOD_STATUS;

    private int totalDocs = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public XmlDocumentResultsModel(String theSql,
                                   ResultSet theResults) {
        this.sql = theSql;

        try {
            this.statement = theResults.getStatement();
            createResults(theResults);
        } catch (SQLException theException) {
            this.results = NO_RESULTS;
            this.status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
            UTIL.log(theException);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void createResults(ResultSet theResults) {
        IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
        final int MAX_ROWS = store.getInt(IConstants.MAX_SQL_ROWS);
        List<String> temp = new ArrayList<String>(1);
        this.totalDocs = 0;
        try {
            while (theResults.next() && (this.totalDocs < MAX_ROWS)) {
            	SQLXML xml = theResults.getSQLXML(1);
                temp.add(xml.getString());
                ++this.totalDocs;
            }

            while (theResults.next()) {
                ++this.totalDocs;
            }
        } catch (SQLException theException) {
            this.results = NO_RESULTS;
            this.status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
            UTIL.log(theException);
        }
        this.results = temp.toArray(new String[temp.size()]);
    }

    /**
     * Collection  of string representations of result XML documents.
     * @return the collection (never <code>null</code>)
     * @since 4.3
     */
    public String[] getResults() {
        return this.results;
    }

    /**
     * @return the text representing all XML Documents found in the results
     * @since 5.5.3
     */
    public String getResultsAsText() {
        StringBuffer sb = new StringBuffer();
        String[] xmlDocs = getResults();

        for (int i = 0; i < xmlDocs.length; ++i) {
            // inserts the header comment at the beginning of the first result document (JBEDSP-343)
            if ((i == 0) && (this.comment != null)) {
                StringBuffer doc = new StringBuffer(xmlDocs[0]);
                int index = doc.indexOf("?>\n"); //$NON-NLS-1$
                assert (index != -1) : "XML is malformed"; //$NON-NLS-1$
                doc.insert(index + 3, this.comment + '\n');
                sb.append(doc.toString());
            } else {
                if (i != 0) {
                    sb.append('\n');
                }

                sb.append(xmlDocs[i]);
            }
        }

        return sb.toString();
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getSql()
     * @since 4.3
     */
    public String getSql() {
        return this.sql;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatement()
     * @since 4.3
     */
    public Statement getStatement() {
        return this.statement;
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatus()
     * @since 4.3
     */
    public IStatus getStatus() {
        return this.status;
    }

    /**
     * The total number of documents in the {@link ResultSet}. Maybe different than the number of documents processed
     * if the user preference for max row count is less than the total.
     * @return the total number of dccuments in the results
     * @since 4.3
     */
    public int getTotalDocumentCount() {
        return this.totalDocs;
    }

    /**
     * @param comment the comment to insert it the top of the first result document
     * @since 5.5.3
     */
    public void setHeaderComment( String comment ) {
        this.comment = comment;
    }
}
