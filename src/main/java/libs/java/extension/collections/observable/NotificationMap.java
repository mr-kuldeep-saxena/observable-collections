package libs.java.extension.collections.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;
import libs.java.extension.collections.observable.notifier.AddMultiNotifier;
import libs.java.extension.collections.observable.notifier.AddNotifier;
import libs.java.extension.collections.observable.notifier.ClearNotifier;
import libs.java.extension.collections.observable.notifier.DeleteNotifier;
import libs.java.extension.collections.observable.notifier.ModifyNotifier;
import libs.java.extension.collections.observable.notifier.NotifierAgent;

/**
 * Observable map. Usage <br>
 * Map<String, String> a = new NotificationMap<>(new HashMap<>(), null, new
 * NotificationListener<KeyValue<String, String>>() {
 * 
 * @Override public void onEvent(NotificationEvent<KeyValue<String, String>>
 *           event) { System.err.println(event.getElement()); }
 * 
 *           }); 
 *           a.put ("ABC", "DEF");
 *           </br>
  * NOTE - As of now, no event generated on stream based operation.
            
 * @author Kuldeep
 *
 * @param <K>
 * @param <V>
 */
public class NotificationMap<K, V> implements Map<K, V> {

	protected boolean RETAIN_METHOD_COPY_ENABLED = true;
	protected boolean REMOVEALL_METHOD_COPY_ENABLED = true;
	protected boolean CLEAR_METHOD_COPY_ENABLED = true;
	/**
	 * Executor, if provided, used to send notification, such that caller is not
	 * blocked
	 */
	protected Executor ex;
	protected List<NotificationListener<KeyValue<K, V>>> listeners = new ArrayList<>();

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableRetainMethodCopy() {
		RETAIN_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableContainMethodCopy()} flag
	 */
	public void enableRetainMethodCopy() {
		RETAIN_METHOD_COPY_ENABLED = true;
	}

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableRemoveAllMethodCopy() {
		REMOVEALL_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableRemoveAllMethodCopy()} flag
	 */
	public void ennableRemoveAllMethodCopy() {
		REMOVEALL_METHOD_COPY_ENABLED = true;
	}

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableClearMethodCopy() {
		CLEAR_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableClearMethodCopy()} flag
	 */
	public void ennableClearMethodCopy() {
		CLEAR_METHOD_COPY_ENABLED = true;
	}

	public Executor getExecutor() {
		return ex;

	}

	public void setExecutor(Executor ex) {
		this.ex = ex;
	}

	public void addListener(NotificationListener<KeyValue<K, V>> listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes listener to the list.
	 * 
	 * @param listener
	 *            listener object
	 * @return status
	 */
	public boolean removeListener(NotificationListener<KeyValue<K, V>> listener) {
		if (listener == null) {
			return false;
		}
		return listeners.remove(listener);
	}

	/**
	 * Underlying map
	 */
	private Map<K, V> map;

	/**
	 * Constructor with underlying map, no executor, so all notification will be
	 * part of list operation thread
	 * 
	 * @param map
	 *            underlying map
	 */
	public NotificationMap(Map<K, V> map) {
		this(map, null);
	}

	/**
	 * Constructor with map and executor, all notification to listeners will be
	 * sent using this executor
	 * 
	 * @param map
	 *            underlying map
	 * @param ex
	 *            executor
	 */
	public NotificationMap(Map<K, V> map, Executor ex) {
		this(map, ex, null);
	}

	/**
	 * Constructor with map, executor and listener. This is to simplify
	 * registration of listener in separate call (See
	 * {@link NotificationList#addListener(NotificationListener)}. However more
	 * listener can be added, removed
	 * 
	 * @param map
	 *            underlying map
	 * @param ex
	 *            executor
	 * @param listener
	 *            listener for event
	 * @throws IllegalArgumentException
	 *             if list is null
	 */
	public NotificationMap(Map<K, V> map, Executor ex, NotificationListener<KeyValue<K, V>> listener) {
		if (map == null) {
			throw new IllegalArgumentException("Passed map can't be null");
		}
		this.map = map;
		this.ex = ex;
		if (listener != null) {
			listeners.add(listener);
		}
	}

	/**
	 * returns underlying map
	 * 
	 * @return map
	 */
	public Map<K, V> getMap() {
		return map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		V old = map.put(key, value);
		if (old == null) {
			NotifierAgent.instance().notify(new AddNotifier<KeyValue<K, V>>(new KeyValue<K, V>(key, value)), listeners,
					ex);
		} else {
			NotifierAgent.instance().notify(
					new ModifyNotifier<KeyValue<K, V>>(new KeyValue<K, V>(key, value), new KeyValue<K, V>(key, old)),
					listeners, ex);
		}
		return old;
	}

	@Override
	public V remove(Object key) {
		V old = map.remove(key);
		if (old != null) {// removed
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<KeyValue<K, V>>(new KeyValue<K, V>((K) key, old)),
						listeners, ex);
			} catch (Exception e) {

			}
		}
		return old;
	}

	class Values {
		V oldValue;
		V newValue;

		Values(V oldValue, V newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}

	/**
	 * Warning - slow operations, because of event generation
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null) {
			throw new IllegalArgumentException("Can't add null map to map");
		}
		Collection<KeyValue<K, Values>> modified = new HashSet<>();
		Collection<KeyValue<K, V>> added = new HashSet<>();

		Set<?> s = m.keySet();
		for (K key : map.keySet()) {
			if (s.contains(key)) {
				modified.add(new KeyValue<K, Values>(key, new Values(map.get(key), m.get(key))));
			} else {
				added.add(new KeyValue<K, V>(key, m.get(key)));
			}
		}

		if (added.size() > 0) {
			NotifierAgent.instance().notify(new AddMultiNotifier<KeyValue<K, V>>(added), listeners, ex);
		} else {
			// generate multiple modify event
			for (KeyValue<K, Values> modify : modified) {
				NotifierAgent.instance()
						.notify(new ModifyNotifier<KeyValue<K, V>>(
								new KeyValue<K, V>(modify.getKey(), modify.getValue().oldValue),
								new KeyValue<K, V>(modify.getKey(), modify.getValue().newValue)), listeners, ex);
			}
		}
	}

	/**
	 * Clears map , and notifies listener event NotifictionEventType =
	 * {@link NotificationEventType#CLEAR} See {@link List#clear()}
	 * 
	 * Note - It iterates over collection to capture elements, so extra
	 * instruction, careful in performance apps
	 */
	public void clear() {

		Map<K, V> elements = new HashMap<>();
		Collection<KeyValue<K, V>> deleted = new ArrayList<>();

		if (CLEAR_METHOD_COPY_ENABLED) {
			map.forEach(elements::putIfAbsent);
			for (K key : elements.keySet()) {
				deleted.add(new KeyValue<K, V>(key, elements.get(key)));
			}

		}
		map.clear();
		NotifierAgent.instance().notify(new ClearNotifier<KeyValue<K, V>>(deleted), listeners, ex);
		return;
	}

	/**
	 * Returns a {@link NotificationSet}, for events, add listener on that
	 */
	@Override
	public Set<K> keySet() {
		return new NotificationSet<>(map.keySet());
	}

	/**
	 * Returns {@link NotificationCollection}-
	 * {@link AbstractNotificationCollection}, for events, add listener on that
	 */
	@Override
	public Collection<V> values() {
		return new AbstractNotificationCollection<>(map.values());
	}

	/**
	 * Returns a {@link NotificationSet}, for events, add listener on that
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new NotificationSet<>(map.entrySet());
	}

}
