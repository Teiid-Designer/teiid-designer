/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant task that creates an Ant property by applying a supplied regular expression pattern to a supplied input string.
 */
public class PropertyFromPatternTask extends AbstractTask {

    private String name;
    private String input;
    private String pattern;

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#loadProperties(java.lang.String, java.util.Map)
     */
    @Override
    protected void loadProperties( String folder,
                                   Map<String, String> properties ) throws Exception {
        // Determine property value from input and pattern
        Matcher matcher = Pattern.compile(pattern).matcher(input);
        properties.put(name, matcher.find() ? matcher.group() : ""); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#run()
     */
    @Override
    protected void run() throws Exception {
        createProperty(name);
    }

    /**
     * @param input the text from which a property value will be created after being matched against the supplied
     *        {@link #setPattern(String) regular expression pattern}.
     */
    public void setInput( String input ) {
        this.input = input;
    }

    /**
     * @param name the name of the property to be created.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @param pattern the regular expression pattern to be applied to the {@link #setInput(String) input}.
     */
    public void setPattern( String pattern ) {
        this.pattern = pattern;
    }
}
