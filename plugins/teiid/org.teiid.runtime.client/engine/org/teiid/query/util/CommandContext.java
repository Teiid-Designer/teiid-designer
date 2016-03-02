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

package org.teiid.query.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.security.auth.Subject;
import org.teiid.core.types.ClobImpl;
import org.teiid.core.types.InputStreamFactory;
import org.teiid.core.util.LRUCache;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.metadata.FunctionMethod.Determinism;

/** 
 * Defines the context that a command is processing in.  For example, this defines
 * who is processing the command and why.  Also, this class (or subclasses) provide
 * a means to pass context-specific information between users of the query processor
 * framework.
 */
public class CommandContext implements Cloneable, org.teiid.CommandContext {
	
	private static class GlobalState implements Cloneable {
	    private Random random = null;

	    private LRUCache<String, DecimalFormat> decimalFormatCache;
		private LRUCache<String, SimpleDateFormat> dateFormatCache;
		private LRUCache<Entry<String,Integer>, Pattern> patternCache;
	}
	
	private GlobalState globalState = new GlobalState();

	private Determinism[] determinismLevel = new Determinism[] {Determinism.DETERMINISTIC};

	private VariableContext variableContext = new VariableContext();

    private final ITeiidServerVersion teiidVersion;

    public CommandContext(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
    }

    /**
     * @return teiid version
     */
    public ITeiidServerVersion getTeiidVersion() {
        return teiidVersion;
    }

    public VariableContext getVariableContext() {
        return variableContext;
    }

    public Determinism getDeterminismLevel() {
        return determinismLevel[0];
    }

    public void setDeterminismLevel(Determinism level) {
        if (determinismLevel[0] == null || level.compareTo(determinismLevel[0]) < 0) {
            determinismLevel[0] = level;
        }
    }

    @Override
    public double getNextRand() {
        if (globalState.random == null) {
        	globalState.random = new Random();
        }
        return globalState.random.nextDouble();
    }
    
    @Override
    public double getNextRand(long seed) {
        if (globalState.random == null) {
        	globalState.random = new Random();
        }
        globalState.random.setSeed(seed);
        return globalState.random.nextDouble();
    }
	
	public static DecimalFormat getDecimalFormat(CommandContext context, String format) {
		DecimalFormat result = null;
		if (context != null) {
			if (context.globalState.decimalFormatCache == null) {
				context.globalState.decimalFormatCache = new LRUCache<String, DecimalFormat>(32);
			} else {
				result = context.globalState.decimalFormatCache.get(format);
			}
		}
		if (result == null) {
			result = new DecimalFormat(format); //TODO: could be locale sensitive
			result.setParseBigDecimal(true);
			if (context != null) {
				context.globalState.decimalFormatCache.put(format, result);
			}
		}
		return result;
	}
	
	public static SimpleDateFormat getDateFormat(CommandContext context, String format) {
		SimpleDateFormat result = null;
		if (context != null) {
			if (context.globalState.dateFormatCache == null) {
				context.globalState.dateFormatCache = new LRUCache<String, SimpleDateFormat>(32);
			} else {
				result = context.globalState.dateFormatCache.get(format);
			}
		}
		if (result == null) {
			result = new SimpleDateFormat(format); //TODO: could be locale sensitive
			if (context != null) {
				context.globalState.dateFormatCache.put(format, result);
			}
		}
		return result;
	}

	/**
     * Compile a regular expression into a {@link java.util.regex.Pattern} and cache it in
     * the {@link CommandContext} for future use.
     *
     * @param context
     * @param regex Regular expression.
     * @param flags Bitmask flags like {@link java.util.regex.Pattern#CASE_INSENSITIVE}.
     * @return Compiled regex.
     */
    public static Pattern getPattern(CommandContext context, String regex, int flags) {
        Pattern result = null;
        if (context != null) {
            if (context.globalState.patternCache == null) {
                context.globalState.patternCache = new LRUCache<Entry<String,Integer>,Pattern>(32);
            } else {
                result = context.globalState.patternCache.get(new SimpleEntry(result, flags));
            }
        }
        if (result == null) {
            result = Pattern.compile(regex, flags);
            if (context != null) {
                context.globalState.patternCache.put(new SimpleEntry(result, flags), result);
            }
        }
        return result;
    }

	/**
     * Used by the system table logic
     * @return a clob
     */
    public Clob getSpatialSysRef() {
        return new ClobImpl(new InputStreamFactory() {
            
            @Override
            public InputStream getInputStream() {
                return getClass().getClassLoader().getResourceAsStream("org/teiid/metadata/spatial_ref_sys.csv"); //$NON-NLS-1$
            }
        }, -1);
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVdbName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getVdbVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConnectionId() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public String getConnectionID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getProcessorBatchSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimeZone getServerTimeZone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subject getSubject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable getCommandPayload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getReuseCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getVDBClassLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addWarning(Exception ex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isContinuous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReturnAutoGeneratedKeys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSessionVariableCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object setSessionVariable(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getSessionVariable(String key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Used by the system table logic
     * @return
     */
    public Clob getSpatialRefSys() {
        return new ClobImpl(new InputStreamFactory() {
            
            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("org/teiid/metadata/spatial_ref_sys.csv"); //$NON-NLS-1$
            }
        }, -1);
    }

    public void addCreatedLob(InputStreamFactory isf) {
        // Do Nothing
    }

    public void disableAutoCleanLobs() {
        // Do Nothing
    }
}
