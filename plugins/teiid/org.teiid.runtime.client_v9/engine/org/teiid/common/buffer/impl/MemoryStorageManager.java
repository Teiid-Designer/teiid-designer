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

package org.teiid.common.buffer.impl;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.teiid.common.buffer.Cache;
import org.teiid.common.buffer.CacheEntry;
import org.teiid.common.buffer.FileStore;
import org.teiid.common.buffer.Serializer;


public class MemoryStorageManager implements Cache<Long> {
	
	public static final int MAX_FILE_SIZE = 1 << 17;
    
	private final class MemoryFileStore extends FileStore {
		private ByteBuffer buffer = ByteBuffer.allocate(MAX_FILE_SIZE);
		
		public MemoryFileStore() {
			buffer.limit(0);
		}

		@Override
		public synchronized void removeDirect() {
			removed.incrementAndGet();
			buffer = ByteBuffer.allocate(0);
		}
		
		@Override
		protected synchronized int readWrite(long fileOffset, byte[] b, int offSet,
				int length, boolean write) {
			if (!write) {
				if (fileOffset >= getLength()) {
					return -1;
				}
				int position = (int)fileOffset;
				buffer.position(position);
				length = Math.min(length, (int)getLength() - position);
				buffer.get(b, offSet, length);
				return length;	
			}
			int requiredLength = (int)(fileOffset + length);
			if (requiredLength > buffer.limit()) {
				buffer.limit(requiredLength);
			}
			buffer.position((int)fileOffset);
			buffer.put(b, offSet, length);
			return length;
		}

		@Override
		public synchronized void setLength(long length) {
			buffer.limit((int)length);
		}
		
		@Override
		public synchronized long getLength() {
			return buffer.limit();
		}

	}

	private Map<Long, Map<Long, CacheEntry>> groups = new ConcurrentHashMap<Long, Map<Long, CacheEntry>>();
	private AtomicInteger created = new AtomicInteger();
	private AtomicInteger removed = new AtomicInteger();
	
    public void initialize() {
    }

	@Override
	public FileStore createFileStore(String name) {
		created.incrementAndGet();
		return new MemoryFileStore();
	}
	
	public int getCreated() {
		return created.get();
	}
	
	public int getRemoved() {
		return removed.get();
	}
	
	@Override
	public boolean add(CacheEntry entry, Serializer<?> s) {
		Map<Long, CacheEntry> group = groups.get(s.getId());
		if (group != null) {
			group.put(entry.getId(), entry);
		}
		return true;
	}
	
	@Override
	public boolean addToCacheGroup(Long gid, Long oid) {
		Map<Long, CacheEntry> group = groups.get(gid);
		if (group != null) {
			group.put(oid, null);
			return true;
		}
		return false;
	}
	
	@Override
	public void createCacheGroup(Long gid) {
		groups.put(gid, Collections.synchronizedMap(new HashMap<Long, CacheEntry>()));
	}
	
	@Override
	public Long lockForLoad(Long oid, Serializer<?> serializer) {
		return oid;
	}
	
	@Override
	public void unlockForLoad(Long o) {
		//nothing to do no locking
	}
	
	@Override
	public CacheEntry get(Long lock, Long oid,
			WeakReference<? extends Serializer<?>> ref)
			throws Exception {
		Map<Long, CacheEntry> group = groups.get(ref.get().getId());
		if (group != null) {
			return group.get(oid);
		}
		return null;
	}
		
	@Override
	public boolean remove(Long gid, Long id) {
		Map<Long, CacheEntry> group = groups.get(gid);
		if (group != null) {
			synchronized (group) {
				int size = group.size();
				group.remove(id);
				return group.size() != size;
			}
		}
		return false;
	}
	
	@Override
	public Collection<Long> removeCacheGroup(Long gid) {
		Map<Long, CacheEntry> group = groups.remove(gid);
		if (group == null) {
			return Collections.emptySet();
		}
		synchronized (group) {
			return new ArrayList<Long>(group.keySet());
		}
	}

	@Override
	public void shutdown() {
		
	}
	
}