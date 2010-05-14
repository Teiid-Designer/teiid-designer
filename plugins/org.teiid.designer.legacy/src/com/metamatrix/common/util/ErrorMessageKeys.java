/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package com.metamatrix.common.util;

/**
 * Date Apr 2, 2003
 * <p>
 * The ErrorMessageKeys contains the message ID's for use with {@link I18NLogManager I18NLogManager} for internationalization of
 * error messages.
 * </p>
 * <b>Adding a Message ID</b> <br>
 * An error message placed here <b>MUST</b> have a related entry in the project resource bundle file. </br> <br>
 * The format of the message ID should conform to the following convention: </br> ERR.000.000.0000 <strong>Example:</strong>
 * <code>ERR.003.001.0002</code> where - node 003 is the common project number - node 001 is the component and must be unique for
 * the project - node 0002 is a unique number for the specified component
 * <p>
 * <strong>Common Component Codes</strong>
 * <li>000 - misc</li>
 * <li>001 - config</li>
 * <li>002 - pooling</li>
 * <li>003 - api</li>
 * <li>004 - actions</li>
 * <li>005 - beans</li>
 * <li>006 - buffering</li>
 * <li>007 - util</li>
 * <li>008 - cache</li>
 * <li>009 - callback</li>
 * <li>010 - connecteion</li>
 * <li>011 - event</li>
 * <li>012 - finder</li>
 * <li>013 - id</li>
 * <li>014 - log</li>
 * <li>015 - jdbc</li>
 * <li>016 - license</li>
 * <li>017 - messaging</li>
 * <li>018 - namedobject</li>
 * <li>019 - object</li>
 * <li>020 - plugin</li>
 * <li>021 - properties</li>
 * <li>022 - proxy</li>
 * <li>023 - queue</li>
 * <li>024 - remote</li>
 * <li>025 - thread</li>
 * <li>026 - transaction</li>
 * <li>027 - transform</li>
 * <li>028 - tree</li>
 * <li>029 - types</li>
 * <li>030 - util</li>
 * <li>031 - xa</li>
 * <li>032 - xml</li>
 * </p>
 */
public interface ErrorMessageKeys {

    /** namedobject (018) */
    public static final String NAMEDOBJECT_ERR_0001 = "ERR.003.018.0001"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0002 = "ERR.003.018.0002"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0003 = "ERR.003.018.0003"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0004 = "ERR.003.018.0004"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0005 = "ERR.003.018.0005"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0006 = "ERR.003.018.0006"; //$NON-NLS-1$
    public static final String NAMEDOBJECT_ERR_0007 = "ERR.003.018.0007"; //$NON-NLS-1$
}
