package libs.java.extension.collections.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;
import libs.java.extension.collections.observable.notifier.AddMultiNotifier;
import libs.java.extension.collections.observable.notifier.AddNotifier;
import libs.java.extension.collections.observable.notifier.ClearNotifier;
import libs.java.extension.collections.observable.notifier.DeleteMultiNotifier;
import libs.java.extension.collections.observable.notifier.DeleteNotifier;
import libs.java.extension.collections.observable.notifier.ModifyNotifier;
import libs.java.extension.collections.observable.notifier.NotifierAgent;

public class NotificationSet<E> extends NotificationCollection<E> implements Set<E> {

	/**
	 * Underlying set
	 */
	private Set<E> set;

	/**
	 * Constructor with underlying Set, no executor, so all notification will be
	 * part of set operation thread
	 * 
	 * @param set
	 *            underlying set
	 */
	public NotificationSet(Set<E> set) {
		this(set, null);
	}

	/**
	 * Constructor with set and executor, all notification to listeners will be
	 * sent using this executor
	 * 
	 * @param set
	 *            underlying set
	 * @param ex
	 *            executor
	 */
	public NotificationSet(Set<E> set, Executor ex) {
		this(set, ex, null);
	}

	/**
	 * Constructor with set, executor and listener. This is to simplify
	 * registration of listener in separate call (See
	 * {@link NotificationSet#addListener(NotificationListener)}. However more
	 * listener can be added, removed
	 * 
	 * @param set
	 *            underlying set
	 * @param ex
	 *            executor
	 * @param listener
	 *            listener for event
	 * @throws IllegalArgumentException
	 *             if set is null
	 */
	public NotificationSet(Set<E> set, Executor ex, NotificationListener<E> listener) {

		if (set == null) {
			throw new IllegalArgumentException("Passed queue can't be null");
		}
		this.set = set;
		super.ex = ex;
		if (listener != null) {
			super.listeners.add(listener);
		}
	}

	/**
	 * returns underlying set
	 * 
	 * @return set
	 */
	public Set<E> getSet() {
		return set;
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {

		return set.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new NotificationIterator<>(set.iterator(), listeners, ex);
	}

	@Override
	public Object[] toArray() {

		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	/**
	 * Add listener to the set. Not allow to add same listener object twice
	 * 
	 * @param listener
	 *            listener object
	 */
	public void addListener(NotificationListener<E> listener) {
		if (!listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Removes listener to the set.
	 * 
	 * @param listener
	 *            listener object
	 * @return status
	 */
	public boolean removeListener(NotificationListener<E> listener) {
		return listeners.remove(listener);
	}

	/**
	 * Add element to the Set, and notifies listener for Add event.
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link Set#add(Object))}
	 * 
	 * @param e
	 *            element to be added.
	 * @return status
	 * 
	 */
	@Override
	public boolean add(E e) {
		boolean added = set.add(e);
		if (added) {
			NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);
		} else {
			NotifierAgent.instance().notify(new ModifyNotifier<E>(e, e), listeners, ex);
		}
		return added;
	}

	/**
	 * Removes and Return element and notifies listener for Delete event
	 * NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link Set#remove(Object))}
	 * 
	 * @param element
	 *            element to be removed.
	 * @return status
	 * 
	 */
	public boolean remove(Object element) {
		boolean removed = set.remove(element);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>((E) element), listeners, ex);
			} catch (Exception e) {
			}
		}
		return removed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	/**
	 * Add elements to the set , and notifies listener for Add event
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link List#addAll(Collection))} Note - extra instructions
	 * 
	 * @param elements
	 *            elements to be added.
	 * @return status
	 * 
	 */

	public boolean addAll(Collection<? extends E> elements) {
		if (elements == null) {
			throw new IllegalArgumentException("Can't add null elements to set");
		}
		Collection<E> modified = new HashSet<>();
		Collection<E> added = new HashSet<>();
		boolean changed = false;
		for (E element : elements) {
			boolean add = set.add(element);
			if (add) {
				added.add(element);
			} else {
				modified.add(element);
			}
			// always added or modified
			changed = true;
		}
		if (added.size() > 0) {
			NotifierAgent.instance().notify(new AddMultiNotifier<E>(added), listeners, ex);
		} else {
			// generate multiple modify event, otherwise need to map which is
			// old and which is new for each element
			for (E modify : modified) {
				NotifierAgent.instance().notify(new ModifyNotifier<E>(modify, modify), listeners, ex);
			}
		}
		return changed;
	}

	/**
	 * Removes given elements from set, and notifies listener for Delete Multi
	 * event NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link Set#removeAll(Collection))} Note - Extra instructions to send
	 * notification, be careful in performance
	 * 
	 * @param elements
	 *            elements to remove.
	 * @return status
	 * 
	 */
	public boolean removeAll(Collection<?> elements) {
		Collection<E> removedElements = new ArrayList<>();
		if (REMOVEALL_METHOD_COPY_ENABLED) {
			for (E e : set) {
				if (elements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = set.removeAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	/**
	 * Removes all elements except in given elements, and notifies listener for
	 * Delete multi event NotifictionEventType =
	 * {@link NotificationEventType#DELETE} {@link Set#retainAll(Collection))}
	 * 
	 * Note - Extra instructions to send notification, be careful in performance
	 * 
	 * @param elements
	 *            elements to retain.
	 * @return status
	 * 
	 */
	public boolean retainAll(Collection<?> elements) {
		Collection<E> removedElements = new ArrayList<>();
		if (RETAIN_METHOD_COPY_ENABLED) {

			Collection<E> containedElements = new ArrayList<>();

			for (E e : set) {
				if (elements.contains(e)) {
					containedElements.add(e);
				}
			}
			for (E e : set) {
				if (!containedElements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = set.retainAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	/**
	 * Clears set , and notifies listener event NotifictionEventType =
	 * {@link NotificationEventType#CLEAR} See {@link List#clear()}
	 * 
	 * Note - It iterates over collection to capture elements, so extra
	 * instruction, careful in performance apps
	 */
	public void clear() {
		List<E> elements = new ArrayList<>();
		if (CLEAR_METHOD_COPY_ENABLED) {
			elements = set.stream().collect(Collectors.toList());
		}
		set.clear();
		NotifierAgent.instance().notify(new ClearNotifier<E>(elements), listeners, ex);
		return;
	}

}
