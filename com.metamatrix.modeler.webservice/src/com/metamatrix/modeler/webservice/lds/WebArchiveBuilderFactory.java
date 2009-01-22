/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.lds;

/**
 * This class is able to create an instance of a WebArchiveBuilder class.
 */
public class WebArchiveBuilderFactory {

    /**
     * Creates an instance of a WebArchiveBuilder.
     */
    public static WebArchiveBuilder create() {

        return new DefaultWebArchiveBuilderImpl();
    }
}
