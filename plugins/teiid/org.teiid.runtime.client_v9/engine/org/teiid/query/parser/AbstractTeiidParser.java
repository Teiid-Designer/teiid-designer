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

package org.teiid.query.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants;
import org.teiid.metadata.AbstractMetadataRecord;
import org.teiid.metadata.BaseColumn;
import org.teiid.metadata.Column;
import org.teiid.metadata.Column.SearchType;
import org.teiid.metadata.Datatype;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionMethod.PushDown;
import org.teiid.metadata.FunctionParameter;
import org.teiid.metadata.KeyRecord;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.Procedure;
import org.teiid.metadata.ProcedureParameter;
import org.teiid.metadata.ProcedureParameter.Type;
import org.teiid.metadata.Table;
import org.teiid.query.metadata.DDLConstants;
import org.teiid.query.metadata.SystemMetadata;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.v9.Token;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.CacheHint;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Comment;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SourceHint;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

public abstract class AbstractTeiidParser implements TeiidParserSPI {

    protected static final Pattern udtPattern = Pattern.compile("(\\w+)\\s*\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)"); //$NON-NLS-1$

    protected static final Pattern FROM_CLAUSE_HINT_PATTERN = Pattern.compile("\\s*(\\w+(?:\\(\\s*(max:\\d+)?\\s*((?:no)?\\s*join)\\s*\\))?)\\s*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    protected static final Pattern SOURCE_HINT = Pattern.compile("\\s*sh(\\s+KEEP ALIASES)?\\s*(?::((?:'[^']*')+))?\\s*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //$NON-NLS-1$
    
    protected static final Pattern SOURCE_HINT_ARG = Pattern.compile("\\s*([^: ]+)(\\s+KEEP ALIASES)?\\s*:((?:'[^']*')+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //$NON-NLS-1$
	
    private static final Pattern HINT = Pattern.compile("\\s*/\\*([^/]*)\\*/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern CACHE_HINT_PRE_811 = Pattern.compile("/\\*\\+?\\s*cache(\\(\\s*(pref_mem)?\\s*(ttl:\\d{1,19})?\\s*(updatable)?\\s*(scope:(session|vdb|user))?[^\\)]*\\))?[^\\*]*\\*\\/.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //$NON-NLS-1$
	private static final Pattern CACHE_HINT =Pattern.compile("\\+?\\s*cache(\\(\\s*(pref_mem)?\\s*(ttl:\\d{1,19})?\\s*(updatable)?\\s*(scope:(session|vdb|user))?\\s*(min:\\d{1,19})?[^\\)]*\\))?[^\\*]*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL); //$NON-NLS-1$

	private static String COMMENT_START = "/*"; //$NON-NLS-1$
	private static String COMMENT_END = "*/"; //$NON-NLS-1$

    protected ITeiidServerVersion version;

    private MetadataFactory metadataFactory;

    private LanguageObject currentNode;

    private Set<Comment> commentCache;

    /**
     * Access to the javacc-generated Reinit method
     *
     * @param stream
     */
    protected abstract void ReInit(Reader stream);

    /**
     * @param sql
     */
    @Override
    public void reset(Reader sql) {
        this.ReInit(sql);
        if (commentCache != null) {
            //
            // Do not clear as the language objects created during parsing
            // have references to the same instance. This decouples this
            // parser from that instance
            //
            commentCache = null;
        }
    }

    /**
     * @return teiid instance version
     */
    @Override
    public ITeiidServerVersion getVersion() {
        return version;
    }

    /**
     * @param teiidVersion
     */
    @Override
    public void setVersion(ITeiidServerVersion teiidVersion) {
        this.version = teiidVersion;
    }

    /**
     * The version of this parser must be greater than
     * or equal to the given version.
     *
     * @param requiredVersionEnum
     * @return true if version less than required
     */
    public boolean versionLessThan(Version requiredVersionEnum) {
        return getVersion().isLessThan(requiredVersionEnum.get());
    }

    /**
     * The version of this parser must be  than
     * or equal to the given version.
     *
     * @param requiredVersionEnum
     * @return true if version at least required
     */
    public boolean versionAtLeast(Version requiredVersionEnum) {
        return ! versionLessThan(requiredVersionEnum);
    }

    @Override
    public DataTypeManagerService getDataTypeService() {
        return DataTypeManagerService.getInstance(getVersion());
    }

    protected <T extends LanguageObject> T createASTNode(ASTNodes nodeType) {
        return TeiidNodeFactory.jjtCreate(this, nodeType);
    }

    /**
     * @param comment
     */
    public void addComment(Comment comment) {
        if (commentCache == null)
            commentCache = new LinkedHashSet<Comment>();

        commentCache.add(comment);

        if (currentNode != null) {
            // Have a current node so set the comments collection
            currentNode.setComments(commentCache);
        }
    }

    @Override
    public void setCurrentNode(LanguageObject currentNode) {
        if (currentNode == null)
            return;

        this.currentNode = currentNode;

        if (commentCache == null)
            commentCache = new LinkedHashSet<Comment>();

        this.currentNode.setComments(commentCache);
    }

	public String getComment(Token t) {
		Token optToken = t.specialToken;
        if (optToken == null) { 
            return ""; //$NON-NLS-1$
        }
        //handle nested comments
        String image = optToken.image;
        while (optToken.specialToken != null) {
        	optToken = optToken.specialToken;
        	image = optToken.image + image;
        }
        String hint = image.substring(2, image.length() - 2);
        if (hint.startsWith("+")) { //$NON-NLS-1$
        	hint = hint.substring(1);
        }
        return hint;
	}

	protected String prependSign(String sign, String literal) {
		if (sign != null && sign.charAt(0) == '-') {
			return sign + literal;
		}
		return literal;
	}

	protected void convertToParameters(List<Expression> values, StoredProcedure storedProcedure, int paramIndex) {
		for (Expression value : values) {
			SPParameter parameter = new SPParameter(getVersion(), paramIndex++, value);
			parameter.setParameterType(SPParameter.IN);
			storedProcedure.setParameter(parameter);
		}
	}

    private static int parseNumericValue(CharSequence string, StringBuilder sb, int i, int value,
                                                                         int possibleDigits, int radixExp) {
        for (int j = 0; j < possibleDigits; j++) {
            if (i + 1 == string.length()) {
                break;
            }
            char digit = string.charAt(i + 1);
            int val = Character.digit(digit, 1 << radixExp);
            if (val == -1) {
                break;
            }
            i++;
            value = (value << radixExp) + val;
        }
        sb.append((char)value);
        return i;
    }

	/**
     * Unescape the given string
     * @param string
     * @param quoteChar
     * @param useAsciiExcapes
     * @param sb a scratch buffer to use
     * @return
     */
    private static String unescape(CharSequence string, int quoteChar, boolean useAsciiEscapes, StringBuilder sb) {
        boolean escaped = false;
        
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (escaped) {
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    i = parseNumericValue(string, sb, i, 0, 4, 4);
                    //TODO: this should probably be strict about needing 4 digits
                    break;
                default:
                    if (c == quoteChar) {
                        sb.append(quoteChar);
                    } else if (useAsciiEscapes) {
                        int value = Character.digit(c, 8);
                        if (value == -1) {
                            sb.append(c);
                        } else {
                            int possibleDigits = value < 3 ? 2:1;
                            int radixExp = 3;
                            i = parseNumericValue(string, sb, i, value, possibleDigits, radixExp);
                        }
                    }
                }
                escaped = false;
            } else {
                if (c == '\\') {
                    escaped = true;
                } else if (c == quoteChar) {
                    break;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

	private static String unescape(String string) {
        return unescape(string, -1, true, new StringBuilder());
    }

	private static String removeEscapeChars(String str, String tickChar) {
        return StringUtil.replaceAll(str, tickChar + tickChar, tickChar);
    }

	/**
	 * @param s
	 * @return normalized version of s
	 */
	public static String normalizeStringLiteral(String s) {
		int start = 1;
		boolean unescape = false;
  		if (s.charAt(0) == 'N') {
  			start++;
  		} else if (s.charAt(0) == 'E') {
  			start++;
  			unescape = true;
  		}
  		char tickChar = s.charAt(start - 1);
  		s = s.substring(start, s.length() - 1);
  		String result = removeEscapeChars(s, String.valueOf(tickChar));
  		if (unescape) {
  			result = unescape(result);
  		}
  		return result;
	}
	
	/**
	 * @param s
	 * @return normalized string id
	 */
	protected String normalizeId(String s) {
		if (s.indexOf('"') == -1) {
			return s;
		}
		List<String> nameParts = new LinkedList<String>();
		while (s.length() > 0) {
			if (s.charAt(0) == '"') {
				boolean escape = false;
				for (int i = 1; i < s.length(); i++) {
					if (s.charAt(i) != '"') {
						continue;
					}
					escape = !escape;
					boolean end = i == s.length() - 1;
					if (end || (escape && s.charAt(i + 1) == '.')) {
				  		String part = s.substring(1, i);
				  		s = s.substring(i + (end?1:2));
				  		nameParts.add(removeEscapeChars(part, "\"")); //$NON-NLS-1$
				  		break;
					}
				}
			} else {
				int index = s.indexOf('.');
				if (index == -1) {
					nameParts.add(s);
					break;
				} 
				nameParts.add(s.substring(0, index));
				s = s.substring(index + 1);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> i = nameParts.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append('.');
			}
		}
		return sb.toString();
	}
	
    /**
     * Check if this is a valid string literal
     * @param id Possible string literal
     */
    protected boolean isStringLiteral(String str, ParseInfo info) {
    	if (info.useAnsiQuotedIdentifiers() || str.charAt(0) != '"' || str.charAt(str.length() - 1) != '"') {
    		return false;
    	}
    	int index = 1;
    	while (index < str.length() - 1) {
    		index = str.indexOf('"', index);
    		if (index == -1 || index + 1 == str.length()) {
    			return true;
    		}
    		if (str.charAt(index + 1) != '"') {
    			return false;
    		}
    		index += 2;
    	}
    	return true;
    }    

    protected String validateName(String id, boolean nonAlias) throws Exception {
        if(id.indexOf('.') != -1) { 
            Messages.TeiidParser key = Messages.TeiidParser.Invalid_alias;
            if (nonAlias) {
                key = Messages.TeiidParser.Invalid_short_name;
            }
            throw new TeiidClientException(Messages.getString(key, id)); 
        }
        return id;
    }

    @Override
    public SourceHint getSourceHint(String comment) {
        Matcher matcher = SOURCE_HINT.matcher(comment);
        if (!matcher.find()) {
            return null;
        }

        SourceHint sourceHint = new SourceHint(getVersion());
        if (matcher.group(1) != null) {
            sourceHint.setUseAliases(true);
        }

        String generalHint = matcher.group(2);
        if (generalHint != null) {
            sourceHint.setGeneralHint(normalizeStringLiteral(generalHint));
        }

        int end = matcher.end();
        matcher = SOURCE_HINT_ARG.matcher(comment);
        while (matcher.find(end)) {
            end = matcher.end();
            sourceHint.setSourceHint(matcher.group(1), normalizeStringLiteral(matcher.group(3)), matcher.group(2) != null);
        }

        return sourceHint;
    }

	@Override
    public CacheHint getQueryCacheOption(String query) {
	    Pattern cacheHint = CACHE_HINT;

    	Matcher hintMatch = HINT.matcher(query);
    	int start = 0;
    	while (hintMatch.find()) {
    		if (start != hintMatch.start()) {
    			break;
    		}
    		start = hintMatch.end();
    		Matcher match = CACHE_HINT.matcher(hintMatch.group(1));
    		if (!match.matches()) {
    			continue;
    		}
    	    CacheHint hint = new CacheHint(getVersion());
    		if (match.group(2) !=null) {
    			hint.setPrefersMemory(true);
    		}
    		String ttl = match.group(3);
    		if (ttl != null) {
    			hint.setTtl(Long.valueOf(ttl.substring(4)));
    		}
    		if (match.group(4) != null) {
    			hint.setUpdatable(true);
    		}
    		String scope =  match.group(5);
    		if (scope != null) {
    			scope = scope.substring(6);
    			hint.setScope(scope);
    		}
		    String min = match.group(7);
		    if (min != null) {
		        hint.setMinRows(Long.valueOf(min.substring(4)));
		    }
    		return hint;
    	}
    	return null;
    }

    @Override
    public Command procedureBodyCommand(ParseInfo parseInfo) throws Exception {
        throw new UnsupportedOperationException("Not supported in Teiid Version " + getVersion()); //$NON-NLS-1$
    }

    /**
     * @param factory
     * @throws Exception 
     */
    @Override
    public void parseMetadata(MetadataFactory factory) throws Exception {
        throw new UnsupportedOperationException("Not supported in Teiid Version " + getVersion()); //$NON-NLS-1$
    }

    protected void setColumnOptions(BaseColumn c) {
    	Map<String, String> props = c.getProperties();
		setCommonProperties(c, props);
		
    	String v = props.remove(DDLConstants.RADIX); 
    	if (v != null) {
    		c.setRadix(Integer.parseInt(v));
    	}
    	
    	if (c instanceof Column) {
    		setColumnOptions((Column)c, props);
    	}
    }

    protected void removeColumnOption(String key, BaseColumn c) {
    	if (c.getProperty(key, false) != null) {
    		c.setProperty(key, null);
    	}    	
		removeCommonProperty(key, c);
		
    	if (key.equals(DDLConstants.RADIX)) {
    		c.setRadix(0);
    	}
    	
    	if (c instanceof Column) {
    		removeColumnOption(key, (Column)c);
    	}
    }    
    
    protected void removeColumnOption(String key, Column c) {
        if (key.equals(DDLConstants.CASE_SENSITIVE)) {
        	c.setCaseSensitive(false);
        }
    	
    	if (key.equals(DDLConstants.SELECTABLE)) {
    		c.setSelectable(true);
    	}
    	
    	if (key.equals(DDLConstants.UPDATABLE)) {
    		c.setUpdatable(false);
    	}
    	
    	if (key.equals(DDLConstants.SIGNED)) {
    		c.setSigned(false);
    	}
    	
    	if (key.equals(DDLConstants.CURRENCY)) {
    		c.setSigned(false);
    	}

    	if (key.equals(DDLConstants.FIXED_LENGTH)) {
    		c.setFixedLength(false);
    	}
    	
    	if (key.equals(DDLConstants.SEARCHABLE)) {
    		c.setSearchType(null);
    	}
    	
    	if (key.equals(DDLConstants.MIN_VALUE)) {
    		c.setMinimumValue(null);
    	}
    	
    	if (key.equals(DDLConstants.MAX_VALUE)) {
    		c.setMaximumValue(null);
    	}
    	
    	if (key.equals(DDLConstants.CHAR_OCTET_LENGTH)) {
    		c.setCharOctetLength(0);
    	}
        
    	if (key.equals(DDLConstants.NATIVE_TYPE)) {
    		c.setNativeType(null);
    	}

    	if (key.equals(DDLConstants.NULL_VALUE_COUNT)) {
    		c.setNullValues(-1);
    	}
    	
    	if (key.equals(DDLConstants.DISTINCT_VALUES)) {
    		c.setDistinctValues(-1);
    	}

    	if (key.equals(DDLConstants.UDT)) {
			c.setDatatype(null);
			c.setLength(0);
			c.setPrecision(0);
			c.setScale(0);
    	}    	
    }

    private void setColumnOptions(Column c, Map<String, String> props) {
        String v = props.remove(DDLConstants.CASE_SENSITIVE); 
        if (v != null) {
            c.setCaseSensitive(isTrue(v));
        }
        
        v = props.remove(DDLConstants.SELECTABLE);
        if (v != null) {
            c.setSelectable(isTrue(v));
        }
        
        v = props.remove(DDLConstants.UPDATABLE); 
        if (v != null) {
            c.setUpdatable(isTrue(v));
        }
        
        v = props.remove(DDLConstants.SIGNED);
        if (v != null) {
            c.setSigned(isTrue(v));
        }
        
        v = props.remove(DDLConstants.CURRENCY);
        if (v != null) {
            c.setSigned(isTrue(v));
        }

        v = props.remove(DDLConstants.FIXED_LENGTH);
        if (v != null) {
            c.setFixedLength(isTrue(v));
        }
        
        v = props.remove(DDLConstants.SEARCHABLE);
        if (v != null) {
            c.setSearchType(StringUtil.caseInsensitiveValueOf(SearchType.class, v));
        }
        
        v = props.remove(DDLConstants.MIN_VALUE);
        if (v != null) {
            c.setMinimumValue(v);
        }
        
        v = props.remove(DDLConstants.MAX_VALUE);
        if (v != null) {
            c.setMaximumValue(v);
        }
        
        v = props.remove(DDLConstants.CHAR_OCTET_LENGTH);
        if (v != null) {
            c.setCharOctetLength(Integer.parseInt(v));
        }
        
        v = props.remove(DDLConstants.NATIVE_TYPE);
        if (v != null) {
            c.setNativeType(v);
        }

        v = props.remove(DDLConstants.NULL_VALUE_COUNT); 
        if (v != null) {
            c.setNullValues(Integer.parseInt(v));
        }
        
        v = props.remove(DDLConstants.DISTINCT_VALUES); 
        if (v != null) {
            c.setDistinctValues(Integer.parseInt(v));
        }

        v = props.remove(DDLConstants.UDT); 
        if (v != null) {
            Matcher matcher = udtPattern.matcher(v);
            Map<String, Datatype> datatypes = SystemMetadata.getInstance(getVersion()).getSystemStore().getDatatypes();
            if (matcher.matches() && datatypes.get(matcher.group(1)) != null) {
                c.setDatatype(datatypes.get(matcher.group(1)));
                c.setLength(Integer.parseInt(matcher.group(2)));
                c.setPrecision(Integer.parseInt(matcher.group(3)));
                c.setScale(Integer.parseInt(matcher.group(4)));
            }
            else {
                throw new RuntimeException(Messages.getString(Messages.TeiidParser.udt_format_wrong, c.getName()));
            }
        }
    }

    protected void setCommonProperties(AbstractMetadataRecord c, Map<String, String> props) {
        String v = props.remove(DDLConstants.UUID); 
        if (v != null) {
            c.setUUID(v);
        }
        
        v = props.remove(DDLConstants.ANNOTATION); 
        if (v != null) {
            c.setAnnotation(v);
        }
        
        v = props.remove(DDLConstants.NAMEINSOURCE); 
        if (v != null) {
            c.setNameInSource(v);
        }
    }

    protected void removeCommonProperty(String key, AbstractMetadataRecord c) {
		if (key.equals(DDLConstants.UUID)) {
			c.setUUID(null);
		}
		
    	if (key.equals(DDLConstants.ANNOTATION)) {
    		c.setAnnotation(null);
    	}
		
		if (key.equals(DDLConstants.NAMEINSOURCE)) {
			c.setNameInSource(null);
		}
	}

    protected void setTableOptions(Table table) {
        Map<String, String> props = table.getProperties();
        setCommonProperties(table, props);
        
        String value = props.remove(DDLConstants.MATERIALIZED); 
        if (value != null) {
            table.setMaterialized(isTrue(value));
        }
        
        value = props.remove(DDLConstants.MATERIALIZED_TABLE); 
        if (value != null) {
            Table mattable = new Table();
            mattable.setName(value);
            table.setMaterializedTable(mattable);
        }
        
        value = props.remove(DDLConstants.UPDATABLE); 
        if (value != null) {
            table.setSupportsUpdate(isTrue(value));
        }
        
        value = props.remove(DDLConstants.CARDINALITY); 
        if (value != null) {
            table.setCardinality(Integer.parseInt(value));
        }
    }

	protected void removeTableOption(String key, Table table) {
    	if (table.getProperty(key, false) != null) {
    		table.setProperty(key, null);
    	}
    	removeCommonProperty(key, table);
    	
    	if (key.equals(DDLConstants.MATERIALIZED)) {
    		table.setMaterialized(false);
    	}
    	
    	if (key.equals(DDLConstants.MATERIALIZED_TABLE)) {
    		table.setMaterializedTable(null);
    	}
    	
    	if (key.equals(DDLConstants.UPDATABLE)) {
    		table.setSupportsUpdate(false);
    	}
    	
    	if (key.equals(DDLConstants.CARDINALITY)) {
    		table.setCardinality(-1);
    	}    	
    }

	protected void replaceProcedureWithFunction(MetadataFactory factory, Procedure proc) {
	    if (proc.isFunction() && proc.getQueryPlan() != null) {
            return;
        }

	    FunctionMethod method = createFunctionMethod(getVersion(), proc);

	    //remove the old proc
	    factory.getSchema().getResolvingOrder().remove(factory.getSchema().getResolvingOrder().size() - 1);
	    factory.getSchema().getProcedures().remove(proc.getName());
	    factory.getSchema().addFunction(method);
	}

	/**
	 * @param teiidVersion
	 * @param proc
	 * @return a function method from given procedure
	 */
	public static FunctionMethod createFunctionMethod(ITeiidServerVersion teiidVersion, Procedure proc) {
		FunctionMethod method = new FunctionMethod();
		method.setName(proc.getName());
		method.setPushdown(proc.isVirtual()?FunctionMethod.PushDown.CAN_PUSHDOWN:FunctionMethod.PushDown.MUST_PUSHDOWN);
		
		ArrayList<FunctionParameter> ins = new ArrayList<FunctionParameter>();
		for (ProcedureParameter pp:proc.getParameters()) {
			if (pp.getType() == ProcedureParameter.Type.InOut || pp.getType() == ProcedureParameter.Type.Out) {
				throw  new RuntimeException(Messages.getString(Messages.TeiidParser.function_in, proc.getName()));
			}

			// New functionality for Teiid 8.9+
		    //copy the metadata
            FunctionParameter fp = new FunctionParameter(teiidVersion, pp.getName(), pp.getRuntimeType(), pp.getAnnotation());
            fp.setDatatype(pp.getDatatype(), true, pp.getArrayDimensions());
            fp.setLength(pp.getLength());
            fp.setNameInSource(pp.getNameInSource());
            fp.setNativeType(pp.getNativeType());
            fp.setNullType(pp.getNullType());
            fp.setProperties(pp.getProperties());
            fp.setRadix(pp.getRadix());
            fp.setScale(pp.getScale());
            fp.setUUID(pp.getUUID());
            if (pp.getType() == ProcedureParameter.Type.In) {
                fp.setVarArg(pp.isVarArg());
                ins.add(fp);
                fp.setPosition(ins.size());
            } else {
                method.setOutputParameter(fp);
                fp.setPosition(0);
            }
		}
		method.setInputParameters(ins);
		
		if (proc.getResultSet() != null || method.getOutputParameter() == null) {
			throw  new RuntimeException(Messages.getString(Messages.TeiidParser.function_return, proc.getName()));
		}
		
		method.setAnnotation(proc.getAnnotation());
		method.setNameInSource(proc.getNameInSource());
		method.setUUID(proc.getUUID());
		
		Map<String, String> props = proc.getProperties();

		String value = props.remove(DDLConstants.CATEGORY); 
		method.setCategory(value);
		
		value = props.remove(DDLConstants.DETERMINISM); 
		if (value != null) {
			method.setDeterminism(FunctionMethod.Determinism.valueOf(value.toUpperCase()));
		}
		
		value = props.remove(DDLConstants.JAVA_CLASS); 
		method.setInvocationClass(value);
		
		value = props.remove(DDLConstants.JAVA_METHOD); 
		method.setInvocationMethod(value);
		
		for (String key:props.keySet()) {
			value = props.get(key);
			method.setProperty(key, value);
		}
		
		FunctionMethod.convertExtensionMetadata(proc, method);
		if (method.getInvocationMethod() != null) {
    		method.setPushdown(PushDown.CAN_PUSHDOWN);
    	}

		return method;
	}

	protected void setProcedureOptions(Procedure proc) {
    	Map<String, String> props = proc.getProperties();
    	setCommonProperties(proc, props);
    	
    	String value = props.remove("UPDATECOUNT"); //$NON-NLS-1$
    	if (value != null) {
    		proc.setUpdateCount(Integer.parseInt(value));
    	}
    }

	protected void removeOption(String option, AbstractMetadataRecord record) {
    	if (record instanceof Table) {
    		removeTableOption(option, (Table)record);
    	}
    	if (record instanceof Procedure) {
    		removeProcedureOption(option, (Procedure)record);
    	}
    	if (record instanceof BaseColumn) {
    		removeColumnOption(option, (BaseColumn)record);
    	}
    }

	protected void setOptions(AbstractMetadataRecord record) {
    	if (record instanceof Table) {
    		setTableOptions((Table)record);
    	}
    	if (record instanceof Procedure) {
    		setProcedureOptions((Procedure)record);
    	}
    	if (record instanceof BaseColumn) {
    		setColumnOptions((BaseColumn)record);
    	}
    }

	protected void removeProcedureOption(String key, Procedure proc) {
    	if (proc.getProperty(key, false) != null) {
    		proc.setProperty(key, null);
    	}    	
    	removeCommonProperty(key, proc);
    	
    	if (key.equals("UPDATECOUNT")) { //$NON-NLS-1$
    		proc.setUpdateCount(1);
    	}
    }    

	protected boolean isTrue(final String text) {
        return Boolean.valueOf(text);
    }    

	protected AbstractMetadataRecord getChild(String name, AbstractMetadataRecord record, boolean parameter) {
    	if (record instanceof Table) {
    		if (parameter) {
    			throw  new RuntimeException(Messages.getString(Messages.TeiidParser.alter_table_param, name, record.getName())); 
    		}
    		return getColumn(name, (Table)record);
    	}
		return getColumn(name, (Procedure)record, parameter);
    	//TODO: function is not supported yet because we store by uid, which should instead be a more friendly "unique name"
    }

	protected Column getColumn(String columnName, Table table) {
		Column c = table.getColumnByName(columnName);
		if (c != null) {
			return c;
		}
		throw  new RuntimeException(Messages.getString(Messages.TeiidParser.no_column, columnName, table.getName())); 
	}

	protected AbstractMetadataRecord getColumn(String paramName, Procedure proc, boolean parameter) {
		if (proc.getResultSet() != null) {
			Column result = proc.getResultSet().getColumnByName(paramName);
			if (result != null) {
				return result;
			}
		}
		if (parameter) {
			List<ProcedureParameter> params = proc.getParameters();
			for (ProcedureParameter param:params) {
				if (param.getName().equalsIgnoreCase(paramName)) {
					return param;
				}
			}
		}
		throw  new RuntimeException(Messages.getString(Messages.TeiidParser.alter_procedure_param_doesnot_exist, paramName, proc.getName()));
	}
	
	protected FunctionParameter getParameter(String paramName, FunctionMethod func) {
		List<FunctionParameter> params = func.getInputParameters();
		for (FunctionParameter param:params) {
			if (param.getName().equalsIgnoreCase(paramName)) {
				return param;
			}
		}
		throw  new RuntimeException(Messages.getString(Messages.TeiidParser.alter_function_param_doesnot_exist, paramName, func.getName()));
	}	
	
	protected void createDDLTrigger(MetadataFactory schema, AlterTrigger trigger) {
		GroupSymbol group = trigger.getTarget();
		
		Table table = schema.getSchema().getTable(group.getName());
		if (trigger.getEvent().equals(Table.TriggerEvent.INSERT)) {
			table.setInsertPlan(trigger.getDefinition().toString());
		}
		else if (trigger.getEvent().equals(Table.TriggerEvent.UPDATE)) {
			table.setUpdatePlan(trigger.getDefinition().toString());
		}
		else if (trigger.getEvent().equals(Table.TriggerEvent.DELETE)) {
			table.setDeletePlan(trigger.getDefinition().toString());
		}
	}

    protected BaseColumn addProcColumn(MetadataFactory factory, Procedure proc, String name, ParsedDataType type, boolean rs) {
        BaseColumn column = null;
        if (rs) {
            column = factory.addProcedureResultSetColumn(name, type.getType(), proc);
        } else {
            boolean added = false;
            for (ProcedureParameter pp : proc.getParameters()) {
                if (pp.getType() == Type.ReturnValue) {
                    added = true;
                    if (pp.getDatatype() != factory.getDataTypes().get(type.getType())) {
                        throw new RuntimeException(Messages.getString(Messages.TeiidParser.proc_type_conflict, proc.getName(), pp.getDatatype(), type.getType()));
                    }
                }
            }
            if (!added) {
                column = factory.addProcedureParameter(name, type.getType(), ProcedureParameter.Type.ReturnValue, proc);
            }
        }
        setTypeInfo(type, column);
        return column;
    }

    protected void setTypeInfo(ParsedDataType type, BaseColumn column) {
        if (type.getLength() != null){
            column.setLength(type.getLength());
        }
        if (type.getScale() != null){
            column.setScale(type.getScale());
        }   
        if (type.getPrecision() != null){
            column.setPrecision(type.getPrecision());
        }
    }

    protected KeyRecord addFBI(MetadataFactory factory, List<Expression> expressions, Table table, String name) {
        List<String> columnNames = new ArrayList<String>(expressions.size());
        List<Boolean> nonColumnExpressions = new ArrayList<Boolean>(expressions.size());
        boolean fbi = false;
        for (int i = 0; i < expressions.size(); i++) {
            Expression ex = expressions.get(i);
            if (ex instanceof ElementSymbol) {
                columnNames.add(((ElementSymbol)ex).getName());
                nonColumnExpressions.add(Boolean.FALSE);
            } else {
                columnNames.add(ex.toString());
                nonColumnExpressions.add(Boolean.TRUE);
                fbi = true;
            }
        }
        return factory.addFunctionBasedIndex(name != null?name:(SQLConstants.NonReserved.INDEX+(fbi?table.getFunctionBasedIndexes().size():table.getIndexes().size())), columnNames, nonColumnExpressions, table);
    }

    protected MetadataFactory getTempMetadataFactory() {
        if (this.metadataFactory == null) {
            this.metadataFactory = new MetadataFactory(version, "temp", "1", "temp", //$NON-NLS-1$ //$NON-NLS-2$
                                                       SystemMetadata.getInstance(getVersion()).getRuntimeTypeMap(), null, null);
        }
        return this.metadataFactory;
    }

    protected void setSourceHint(SourceHint sourceHint, Command command) {
        if (sourceHint == null)
            return;

        if (command instanceof SetQuery) {
            ((SetQuery)command).getProjectedQuery().setSourceHint(sourceHint);
        } else {
            command.setSourceHint(sourceHint);
        }
    }

    protected List<Expression> arrayExpressions(List<Expression> expressions, Expression expr) {
        if (expressions == null) {
            expressions = new ArrayList<Expression>();
        }
        if (expr != null) {
            expressions.add(expr);
        }
        return expressions;
    }
    
	public static void setDefault(BaseColumn column, Expression value) {
		if ((value instanceof Constant) && value.getType() == DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()) {
			column.setDefaultValue(((Constant)value).getValue().toString());
		} else {
			//it's an expression
			column.setProperty(BaseColumn.DEFAULT_HANDLING, BaseColumn.EXPRESSION_DEFAULT);
			column.setDefaultValue(value.toString());
		}
	}
}
