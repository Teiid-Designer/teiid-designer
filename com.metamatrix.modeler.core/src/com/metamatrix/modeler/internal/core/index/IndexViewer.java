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

package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.internal.core.index.BlocksIndexInput;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.IndexInput;

/**
 * IndexViewer
 */
public class IndexViewer {

    private static final char[] FORMAT_CHARS = {'\n', '\r', '\t'};
    
    /**
     * Returns the contents of the specified index file as a byte[].
     * Line separator characters are added after every WordEntry found
     * in the file.
     */
    public static byte[] getByteContent(final File file) throws IOException {
        Assertion.isNotNull(file);
        Assertion.assertTrue(IndexUtil.isIndexFile(file));
        Assertion.assertTrue(IndexUtil.indexFileExists(file.getAbsolutePath()));

        String content = getStringContent(file);
        return (content != null ? content.getBytes() : new byte[0]);
    }
    
    /**
     * Returns the contents of the specified index file as a string.
     * Line separator characters are added after every WordEntry found
     * in the file.
     */
    public static String getStringContent(final File file) throws IOException {
        Assertion.isNotNull(file);
        Assertion.assertTrue(IndexUtil.isIndexFile(file));
        Assertion.assertTrue(IndexUtil.indexFileExists(file.getAbsolutePath()));

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(file);
            input.open();
            StringBuffer buffer = new StringBuffer(); 
            while(input.hasMoreWords()) {
                final String word = String.valueOf(input.getCurrentWordEntry().getWord());                
                buffer.append(StringUtilities.removeChars(word, IndexViewer.FORMAT_CHARS));
                buffer.append(StringUtil.LINE_SEPARATOR);
                input.moveToNextWordEntry();
            }
            return buffer.toString();
        } catch (Throwable t) {
            return StringUtil.getStackTrace(t);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        //return ObjectConverterUtil.convertFileToString(file);
    }

    /**
     * Returns the collection of {@link char[]} instances representing
     * the contents of the specified index file.
     */
    public static Collection getWords(final String filePath) throws IOException {
        Assertion.isNotNull(filePath);
        Assertion.assertTrue(IndexUtil.isIndexFile(filePath));
        Assertion.assertTrue(IndexUtil.indexFileExists(filePath));

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(new File(filePath));
            input.open();
            Collection words = new ArrayList(); 
            while(input.hasMoreWords()) {
                words.add(input.getCurrentWordEntry().getWord());
                input.moveToNextWordEntry();
            }
            return words;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Returns the collection of {@link char[]} instances representing
     * the contents of the specified index file.
     */
    public static Collection getWords(final File file) throws IOException {
        Assertion.isNotNull(file);
        Assertion.assertTrue(IndexUtil.isIndexFile(file));
        Assertion.assertTrue(IndexUtil.indexFileExists(file.getAbsolutePath()));

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(file);
            input.open();
            Collection words = new ArrayList(); 
            while(input.hasMoreWords()) {
                words.add(input.getCurrentWordEntry().getWord());
                input.moveToNextWordEntry();
            }
            return words;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * 
     */
    public static void clearIndexFileContents(final String filePath) throws IOException {
        Index index =  new Index(filePath, null, true);
        index.empty();
    }

    /**
     * 
     */
    public static String getStringContent(final Index index) throws IOException {
        Assertion.isNotNull(index);
        Assertion.isNotNull(index.getIndexFile());
        Assertion.assertTrue(IndexUtil.isIndexFile(index.getIndexFile()));
        Assertion.assertTrue(IndexUtil.indexFileExists(index.getIndexFile().getAbsolutePath()));

        return getStringContent(index.getIndexFile());
    }

//    public static void main(String[] args) {
//        String filePath = "E:/Plugins/current/plugins/com.metamatrix.modeler.core/testdata/builtInDataTypes.INDEX"; //$NON-NLS-1$
//        File file = new File(filePath);
//        try {
//            System.out.println(getStringContent(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    
//    }

}
