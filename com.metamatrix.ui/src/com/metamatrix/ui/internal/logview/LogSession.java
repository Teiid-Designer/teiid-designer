/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.logview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


/** 
 * @since 4.3
 */
public class LogSession {
    private String sessionData;
    private Date date;

    /**
     * Constructor for LogSession.
     */
    public LogSession() {
    }

    public Date getDate() {
        return date;
    }
    
    public void setDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SS"); //$NON-NLS-1$
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
        }
    }
    
    public String getSessionData() {
        return sessionData;
    }

    void setSessionData(String data) {
        this.sessionData = data;
    }
    
    public void processLogLine(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() == 6) {
            tokenizer.nextToken();
            StringBuffer dateBuffer = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                dateBuffer.append(tokenizer.nextToken());
                dateBuffer.append(" "); //$NON-NLS-1$
            }
            setDate(dateBuffer.toString().trim());
        }
    }
}
