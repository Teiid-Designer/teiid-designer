/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author blafond
 *
 */
public class AllowedLanguages extends VdbUnit implements Iterable<String> {

    private Set<String> languages = new LinkedHashSet<String>();

	/**
	 *
	 */
	public AllowedLanguages() {
		super();
	}
	
	/**
	 * @param inputString
	 */
	public AllowedLanguages(String inputString) {
		this();
		addAllowedLanguage(inputString);
	}
	
	/**
	 * @param values
	 */
	public AllowedLanguages(String[] values) {
		this();
		addAllowedLanguages(values);
	}

	/**
	 * @param vdb
	 */
	public AllowedLanguages(Vdb vdb) {
	    this();
	    setVdb(vdb);
	}

	/**
	 * @return output string
	 */
	public String getOutputString() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		int max = languages.size();
		for( String lang : languages ) {
			sb.append(lang);
			count++;
			if( count < max ) sb.append(COMMA + SPACE);
		}
		return sb.toString();
	}

	/**
	 * @return array of allowed languages
	 */
	public String[] getAllowedLanguageValues() {
		return languages.toArray(new String[languages.size()]);
	}
	
	/**
	 * @param value containing new language
	 */
	public void addAllowedLanguage(String value) {
		if( value.contains(COMMA)) {
			loadFromString(value);
		} else {
			setChanged(languages.add(value));
		}
	}
	
	/**
	 * @param value containing new language
	 */
	public void removeAllowedLanguage(String value) {
		setChanged(languages.remove(value));
	}
	
	/**
	 * @param values
	 */
	public void addAllowedLanguages(String[] values) {
		boolean changed = false;
		for( String value : values ) {
			boolean result = this.languages.add(value);
			if( result ) changed = true;
		}
		setChanged(changed);
	}
	
	private boolean loadFromString(String commaSeparatedValues) {
		String[] values = commaSeparatedValues.split(COMMA);
		boolean changed = false;
		for( String value : values ) {
			boolean result = this.languages.add(value.trim());
			if( result ) changed = true;
		}
		return changed;
	}

	/**
     * @return is empty
     */
    public boolean isEmpty() {
        return languages.isEmpty();
    }

    /**
     * @return size
     */
	public int size() {
	    return languages.size();
	}

    @Override
    public Iterator<String> iterator() {
        return languages.iterator();
    }

    @Override
    public AllowedLanguages clone() {
        AllowedLanguages clone = new AllowedLanguages();
        for (String language : languages)
            clone.addAllowedLanguage(language);

        cloneVdbObject(clone);

        return clone;
    }
}
