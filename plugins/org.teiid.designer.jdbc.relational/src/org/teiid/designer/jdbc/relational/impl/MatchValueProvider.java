/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.impl;

/**
 * MatchValueProvider
 *
 * @since 8.0
 */
public interface MatchValueProvider {
    public String getMatchValue( final Object obj );
}
