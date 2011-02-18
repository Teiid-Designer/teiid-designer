/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.actions;


/** 
 * @since 4.2
 */
public class WorkerProblem {
    private String title;
    private String message;
    /** 
     * 
     * @since 4.2
     */
    public WorkerProblem(String title, String message) {
        super();
        this.title = title;
        this.message = message;
    }
    
    

    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
