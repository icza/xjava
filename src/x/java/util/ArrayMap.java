/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * A {@link Map} implementation which stores the entries in a simple array, no key hashing (like in
 * {@link HashMap}) or key linking (like in {@link TreeMap} is used/performed.
 * 
 * <p>
 * Since entries are stored in a simple array and searches are sequential, this implementation is not suitable
 * for many entries, but is faster and uses less memory for smaller maps compared to {@link HashMap}.
 * Recommended if entry count is about 10.<br>
 * As a plus, the {@link #containsValue(Object)} method is just as fast as the {@link #containsKey(Object)},
 * and this class also provides a {@link #getKeyByValue(Object)} method to provide a bi-directional map
 * functionality.
 * </p>
 * 
 * <p>
 * Implementation prioritizes reference searches and lookups, so performance will be extremely good if the
 * same key reference is used to look up a previously stored value.
 * </p>
 * 
 * <p>
 * If entries are only added and not removed, the iteration order of keys, values, entries will be the same as
 * the adding order.
 * </p>
 * 
 * @param <K> type of the keys
 * @param <V> type of the values
 * 
 * @author Andras Belicza
 */
public class ArrayMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /**
     * {@link java.util.Map.Entry} implementation used to store the entries.
     * 
     * @param <K> type of the keys
     * @param <V> type of the values
     * 
     * @author Andras Belicza
     */
    private static class ArrEntry<K, V> implements Entry<K, V>, Cloneable, Serializable {
        
        /** */
        private static final long serialVersionUID = 1L;
        
        /** Key. */
        public final K key;
        
        /** Value. */
        public V value;
        
        /**
         * Creates a new {@link ArrayMap.ArrEntry}.
         * 
         * @param key key
         * @param value value
         */
        public ArrEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return key;
        }
        
        @Override
        public V getValue() {
            return value;
        }
        
        @Override
        public V setValue(final V value) {
            final V oldValue = value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
        
        @Override
        protected ArrEntry<K, V> clone() {
            try {
                @SuppressWarnings("unchecked")
                final ArrEntry<K, V> e = (ArrEntry<K, V>) super.clone();
                return e;
            } catch (final CloneNotSupportedException e) {
                // Never to happen as we implement Cloneable
                throw new InternalError(e);
            }
        }
        
    }
    
    /** Array of the table entries. */
    private ArrEntry<K, V>[] entries;
    
    /** Size of the map, the number of entries. */
    private int size;
    
    /**
     * Creates a new {@link ArrayMap} with an initial capacity of 10.
     */
    public ArrayMap() {
        this(10);
    }
    
    /**
     * Creates a new {@link ArrayMap} with the specified initial capacity.
     * 
     * @param initialCapacity initial capacity
     */
    public ArrayMap(final int initialCapacity) {
        @SuppressWarnings("unchecked")
        final ArrEntry<K, V>[] entries = new ArrEntry[initialCapacity];
        
        this.entries = entries;
    }
    
    /**
     * Creates a new {@link ArrayMap} with the same mappings as the specified map.
     * 
     * @param m {@link Map} whose mappings to be placed into this map
     */
    public ArrayMap(final Map<? extends K, ? extends V> m) {
        this(m.size());
        
        // Local copy
        final ArrEntry<K, V>[] entries = this.entries;
        
        int i = 0;
        for (final Entry<? extends K, ? extends V> entry : m.entrySet())
            entries[i++] = new ArrEntry<>(entry.getKey(), entry.getValue());
        
        size = i;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns the entry for the specified key.
     * 
     * @param key key whose entry to be returned
     * @return the entry for the specified key; or <code>null</code> if the key is not in this map
     */
    private ArrEntry<K, V> getEntry(final Object key) {
        // Local reference for performance
        final ArrEntry<K, V>[] entries = this.entries;
        
        // First try a key search by reference.
        // This also handles if the searched key is the null value.
        for (int i = size - 1; i >= 0; i--)
            if (entries[i].key == key)
                return entries[i];
        
        // If key is the null value and was not found by the reference search, it's not in this map
        if (key == null)
            return null;
        
        for (int i = size - 1; i >= 0; i--)
            if (key.equals(entries[i].key)) // Key is certainly not null at this point
                return entries[i];
        
        return null;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return getEntry(key) != null;
    }
    
    /**
     * Returns the first entry for the specified value.
     * 
     * <p>
     * First means the first in insertion order if elements were not removed from the map.
     * </p>
     * 
     * @param value value whose first entry to be returned
     * @return the first entry for the specified value; or <code>null</code> if the value is not in this map
     */
    private ArrEntry<K, V> getEntryByValue(final Object value) {
        // Local reference for performance
        final ArrEntry<K, V>[] entries = this.entries;
        final int size = this.size;
        
        // First try a value search by reference.
        // This also handles if the searched value is the null value.
        // Have to up upward to get the first by insertion order
        for (int i = 0; i < size; i++)
            if (entries[i].value == value)
                return entries[i];
        
        // If value is the null value and was not found by the reference search, it's not in this map
        if (value == null)
            return null;
        
        // Have to up upward to get the first by insertion order
        for (int i = 0; i < size; i++)
            if (value.equals(entries[i].value)) // Value is certainly not null at this point
                return entries[i];
        
        return null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return getEntryByValue(value) != null;
    }
    
    @Override
    public V get(final Object key) {
        final ArrEntry<K, V> entry = getEntry(key);
        return entry == null ? null : entry.value;
    }
    
    /**
     * Returns the first key for the specified value.
     * 
     * <p>
     * First means the first in insertion order if elements were not removed from the map.
     * </p>
     * 
     * @param value value whose first key to return
     * @return the first key for the specified value or <code>null</code> if value was not found in this map
     */
    public K getKeyByValue(final Object value) {
        final ArrEntry<K, V> entry = getEntryByValue(value);
        return entry == null ? null : entry.key;
    }
    
    /**
     * Ensures entry array size for the specified capacity.
     * 
     * @param capacity capacity to be ensured
     */
    private void ensureCapacity(final int capacity) {
        if (entries.length >= capacity)
            return;
        
        entries = Arrays.copyOf(entries, capacity);
    }
    
    @Override
    public V put(final K key, final V value) {
        final ArrEntry<K, V> entry = getEntry(key);
        
        if (entry == null) {
            if (entries.length == size)
                ensureCapacity(entries.length < 2 ? 2 : entries.length * 2); // Double the size
                
            entries[size++] = new ArrEntry<>(key, value);
            return null;
        } else
            return entry.setValue(value);
    }
    
    @Override
    public V remove(final Object key) {
        final ArrEntry<K, V> entry = getEntry(key);
        if (entry == null)
            return null;
        
        // Copy last entry to the entry's place, and clear it's reference, and decrease size
        // Note: entrySet().iterator() counts on the fact that entries having less index than the removed
        // entry are not changed.
        size--;
        for (int i = size; i >= 0; i--)
            if (entries[i] == entry) {
                entries[i] = entries[size];
                entries[size] = null;
                break;
            }
        
        return entry.value;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        // New capacity of size+m.size() is not necessarily required as keys might be in both maps
        // but this is the fastest estimation.
        ensureCapacity(size + m.size());
        
        for (final Entry<? extends K, ? extends V> entry : m.entrySet())
            put(entry.getKey(), entry.getValue());
    }
    
    @Override
    public void clear() {
        // Besides zeroing the size, clear entry references because we keep the array (and so would the
        // references be kept!)
        for (int i = size - 1; i >= 0; i--)
            entries[i] = null;
        
        size = 0;
    }
    
    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    private int idx = 0; // Going upward to preserve the adding order
                    
                    @Override
                    public boolean hasNext() {
                        return idx < size;
                    }
                    
                    @Override
                    public K next() {
                        return entries[idx++].key;
                    }
                    
                    @Override
                    public void remove() {
                        // Iterator goes upward, and ArrayMap.remove() puts the last entry to the removed
                        // entry
                        // so it does not cause trouble to remove the current (previous) entry (but it has to
                        // be visited again)
                        ArrayMap.this.remove(entries[--idx].key);
                    }
                };
            }
            
            @Override
            public int size() {
                return size;
            }
            
            @Override
            public boolean contains(final Object o) {
                return ArrayMap.this.containsKey(o);
            }
            
            @Override
            public void clear() {
                ArrayMap.this.clear();
            }
        };
    }
    
    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>() {
            
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    private int idx = 0; // Going upward to preserve the adding order
                    
                    @Override
                    public boolean hasNext() {
                        return idx < size;
                    }
                    
                    @Override
                    public V next() {
                        return entries[idx++].value;
                    }
                    
                    @Override
                    public void remove() {
                        // Iterator goes upward, and ArrayMap.remove() puts the last entry to the removed
                        // entry
                        // so it does not cause trouble to remove the current (previous) entry (but it has to
                        // be visited again)
                        ArrayMap.this.remove(entries[--idx].key);
                    }
                };
            }
            
            @Override
            public int size() {
                return size;
            }
            
            @Override
            public boolean contains(final Object o) {
                return ArrayMap.this.containsValue(o);
            }
            
            @Override
            public void clear() {
                ArrayMap.this.clear();
            }
        };
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() {
            
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private int idx = 0; // Going upward to preserve the adding order
                    
                    @Override
                    public boolean hasNext() {
                        return idx < size;
                    }
                    
                    @Override
                    public Entry<K, V> next() {
                        return entries[idx++];
                    }
                    
                    @Override
                    public void remove() {
                        // Iterator goes upward, and ArrayMap.remove() puts the last entry to the removed
                        // entry
                        // so it does not cause trouble to remove the current (previous) entry (but it has to
                        // be visited again)
                        ArrayMap.this.remove(entries[--idx].key);
                    }
                };
            }
            
            @Override
            public int size() {
                return size;
            }
            
            @Override
            public void clear() {
                ArrayMap.this.clear();
            }
        };
    }
    
    /**
     * Checks if the specified object is equal to this map.
     * 
     * <p>
     * Will return <code>true</code> if the specified object:
     * </p>
     * 
     * <ul>
     * <li>is a {@link Map}
     * <li>contains exactly the same mappings
     * </ul>
     *
     * @param obj object to be checked for equality
     * 
     * @return <code>true</code> if the specified object is equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        
        if (!(obj instanceof Map))
            return false;
        
        final Map<?, ?> map2 = (Map<?, ?>) obj;
        if (size != map2.size())
            return false;
        
        for (int i = size - 1; i >= 0; i--) {
            final K k = entries[i].key;
            final V v = entries[i].value;
            
            // Check if other map has this entry
            if (v == null) {
                if (!map2.containsKey(k) || map2.get(k) != null)
                    return false;
            } else {
                if (!v.equals(map2.get(k)))
                    return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        
        for (int i = size - 1; i >= 0; i--)
            hash += entries[i].hashCode();
        
        return hash;
    }
    
    @Override
    public ArrayMap<K, V> clone() {
        // I don't use native cloning here because it would have no (or little) gain.
        // The entries array have to be deep cloned, so I just do it manually:
        
        final ArrayMap<K, V> m = new ArrayMap<>(entries.length);
        
        m.size = size;
        
        final ArrEntry<K, V>[] entries = this.entries;
        final ArrEntry<K, V>[] mentries = m.entries;
        
        for (int i = size - 1; i >= 0; i--)
            mentries[i] = entries[i].clone();
        
        return m;
    }
    
}
