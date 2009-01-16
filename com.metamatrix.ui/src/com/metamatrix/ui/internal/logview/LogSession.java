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
