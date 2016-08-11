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

package org.teiid.query.function;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Stack;
import org.teiid.common.buffer.FileStore;
import org.teiid.common.buffer.FileStoreInputStreamFactory;
import org.teiid.common.buffer.impl.MemoryStorageManager;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.ClobImpl;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.ClobType.Type;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.InputStreamFactory;
import org.teiid.core.types.InputStreamFactory.StorageMode;
import org.teiid.core.types.Streamable;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.json.simple.ContentHandler;
import org.teiid.json.simple.JSONParser;
import org.teiid.json.simple.ParseException;
import org.teiid.query.eval.Evaluator;
import org.teiid.query.function.metadata.FunctionCategoryConstants;
import org.teiid.query.function.source.XMLSystemFunctions;
import org.teiid.query.util.CommandContext;
import org.teiid.runtime.client.TeiidClientException;

public class JSONFunctionMethods {
	
	/**
	 * Does nothing, just allows the parser to validate
	 */
	private static final ContentHandler validatingContentHandler = new ContentHandler() {
		@Override
		public boolean startObjectEntry(String key) throws ParseException,
				IOException {
			return true;
		}

		@Override
		public boolean startObject() throws ParseException, IOException {
			return true;
		}

		@Override
		public void startJSON() throws ParseException, IOException {
			
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean primitive(Object value) throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			return true;
		}

		@Override
		public boolean endObject() throws ParseException, IOException {
			return true;
		}

		@Override
		public void endJSON() throws ParseException, IOException {
			
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			return true;
		}
	};
	
	public static class JSONBuilder {
	    private final ITeiidServerVersion teiidVersion;
		private Writer writer;
		private FileStoreInputStreamFactory fsisf;
		private FileStore fs;
		private Stack<Integer> position = new Stack<Integer>();

		public JSONBuilder(ITeiidServerVersion teiidVersion) {
		    this.teiidVersion = teiidVersion;
            MemoryStorageManager manager = new MemoryStorageManager();

            String fileStoreName = "json"; //$NON-NLS-1$

			fs = manager.createFileStore(fileStoreName);
			fsisf = new FileStoreInputStreamFactory(fs, Streamable.ENCODING);
		    writer = fsisf.getWriter();
		}

		public void start(boolean array) throws TeiidClientException {
			position.push(0);
			try {
		    	if (array) {
		    		writer.append('[');
		    	} else {
		    		writer.append('{');
		    	}
			} catch (IOException e) {
				remove();
				throw new TeiidClientException(e);
			}
		}

		public void addValue(Object object) throws TeiidClientException {
			addValue(null, object);
		}
		
		public void addValue(String key, Object object) throws TeiidClientException {
			try {
				startValue(key);
				if (object == null) {
					writer.append("null"); //$NON-NLS-1$
				} else if (object instanceof ClobType) {
					ClobType clob = (ClobType)object;
					if (clob.getType() == Type.JSON) {
						Reader r = clob.getCharacterStream();
						try {
							ObjectConverterUtil.write(writer, r, -1, false);
						} finally {
							r.close();
						}
					} else {
						writer.append('"');
						JSONParser.escape(clob.getCharSequence(), writer);
						writer.append('"');
					}
				} else if (object instanceof Boolean) {
					writer.append(object.toString());
				} else if (object instanceof Number) {
					//TODO: if allow NaN infinity is on, then we may output an invalid value here
					writer.write(object.toString());
				} else {
					writer.append('"');
					String text = (String) DataTypeManagerService.getInstance(teiidVersion).transformValue(object, 
					                                                                  DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
					JSONParser.escape(text, writer);
					writer.append('"');
				}
			} catch (Exception e) {
				remove();
				throw new TeiidClientException(e);
			}
		}

		public void startValue(String key) throws TeiidClientException {
			try {
				if (position.peek() != 0) {
					writer.append(',');
				}
				position.add(position.pop() + 1);
				if (key != null) {
					writer.append('"');
					JSONParser.escape(key, writer);
					writer.append('"');
					writer.append(":"); //$NON-NLS-1$
				}
			} catch (IOException e) {
				remove();
				throw new TeiidClientException(e);
			}
		}
		
		public Writer getWriter() {
			return writer;
		}
		
		public ClobType close(CommandContext cc) throws TeiidClientException {
			try {
				writer.close();
			} catch (IOException e) {
				remove();
				throw new TeiidClientException(e);
			}

			if (fsisf.getStorageMode() == StorageMode.MEMORY) {
                //detach if just in memory
                byte[] bytes = fsisf.getMemoryBytes();
                fsisf.free();
                ClobType result = new ClobType(new ClobImpl(new String(bytes, Streamable.CHARSET)));
                result.setType(Type.JSON);
                return result;
            }

            ClobType result = new ClobType(new ClobImpl(fsisf, -1));
            if (cc != null) {
                cc.addCreatedLob(fsisf);
            }

	        result.setType(Type.JSON);
	        return result;
		}

		public void remove() {
			fs.remove();
		}

		public void end(boolean array) throws TeiidClientException {
			position.pop();
			try {
				if (array) {
					writer.append(']');
				} else {
					writer.append('}');
				}
			} catch (IOException e) {
				remove();
				throw new TeiidClientException(e);
			}
		}
		
	}

	@TeiidFunction(category=FunctionCategoryConstants.JSON)
	public static ClobType jsonParse(ClobType val, boolean wellformed) throws SQLException, IOException, ParseException {
		Reader r = null;
		if (val.getType() == Type.JSON) {
			return val;
		}
		if (!wellformed) {
			r = val.getCharacterStream();
		}
		try {
			if (!wellformed) {
				JSONParser parser = new JSONParser();
				parser.parse(r, validatingContentHandler);
			}
			ClobType ct = new ClobType(val.getReference());
			ct.setType(Type.JSON);
			return ct;
		} finally {
			if (r != null) {
				r.close();
			}
		}
	}
	
	@TeiidFunction(category=FunctionCategoryConstants.JSON)
	public static ClobType jsonParse(BlobType val, boolean wellformed) throws SQLException, IOException, ParseException {
		InputStreamReader r = XMLSystemFunctions.getJsonReader(val);
		try {
			if (!wellformed) {
				JSONParser parser = new JSONParser();
				parser.parse(r, validatingContentHandler);
			}
			ClobImpl clobImpl = new ClobImpl();
			clobImpl.setStreamFactory(new InputStreamFactory.BlobInputStreamFactory(val.getReference()));
			clobImpl.setEncoding(r.getEncoding());
			ClobType ct = new ClobType(clobImpl);
			ct.setType(Type.JSON);
			return ct;
		} finally {
			r.close();
		}
	}

	@TeiidFunction(category=FunctionCategoryConstants.JSON)
	public static ClobType jsonArray(CommandContext context, Object... vals) throws Exception {
		if (vals == null) {
			return null;
		}
		return Evaluator.jsonArray(context, null, vals, null, null);
	}

}
