package libs.java.extension.collections.observable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;
import libs.java.extension.collections.observable.notifier.AddMultiNotifier;
import libs.java.extension.collections.observable.notifier.AddNotifier;
import libs.java.extension.collections.observable.notifier.ClearNotifier;
import libs.java.extension.collections.observable.notifier.DeleteMultiNotifier;
import libs.java.extension.collections.observable.notifier.DeleteNotifier;
import libs.java.extension.collections.observable.notifier.ModifyNotifier;
import libs.java.extension.collections.observable.notifier.NotifierAgent;
import libs.java.extension.collections.observable.notifier.ReadMultiNotifier;
import libs.java.extension.collections.observable.notifier.ReadNotifier;

/**
 * A decorated List which allows notification on different list operation.
 * Simple usage is <br>
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public class NotificationList<E> extends NotificationCollection<E> implements List<E> {

	/**
	 * Underlying list
	 */
	private List<E> list;

	/**
	 * Constructor with underlying list, no executor, so all notification will
	 * be part of list operation thread
	 * 
	 * @param list
	 *            underlying list
	 */
	public NotificationList(List<E> list) {
		this(list, null);
	}

	/**
	 * Constructor with list and executor, all notification to listeners will be
	 * sent using this executor
	 * 
	 * @param list
	 *            underlying list
	 * @param ex
	 *            executor
	 */
	public NotificationList(List<E> list, Executor ex) {
		this(list, ex, null);
	}

	/**
	 * Constructor with list, executor and listener. This is to simplify
	 * registration of listener in separate call (See
	 * {@link NotificationList#addListener(NotificationListener)}. However more
	 * listener can be added, removed
	 * 
	 * @param list
	 *            underlying list
	 * @param ex
	 *            executor
	 * @param listener
	 *            listener for event
	 * @throws IllegalArgumentException
	 *             if list is null
	 */
	public NotificationList(List<E> list, Executor executor, NotificationListener<E> listener) {

		if (list == null) {
			throw new IllegalArgumentException("Passed list can't be null");
		}
		this.list = list;
		super.ex = executor;
		if (listener != null) {
			super.listeners.add(listener);
		}
	}

	/**
	 * returns underlying list
	 * 
	 * @return list
	 */
	public List<E> getList() {
		return list;
	}

	

	/**
	 * Add element to the list, and notifies listener for Add event.
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link List#add(Object))}
	 * 
	 * @param e
	 *            element to be added.
	 * @return status
	 * 
	 */
	public boolean add(E e) {
		boolean added = list.add(e);
		if (added) {
			NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);
		}
		return added;
	}

	/**
	 * Add element to the list at specified index, and notifies listener for Add
	 * event NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link List#add(int, Object)))}
	 * 
	 * @param index
	 *            index where to add element to
	 * @param e
	 *            element to be added.
	 * @return status
	 * 
	 */

	public void add(int index, E e) {
		list.add(index, e);
		NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);

	}

	/**
	 * Add elements to the list , and notifies listener for Add event
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link List#addAll(Collection))}
	 * 
	 * @param elements
	 *            elements to be added.
	 * @return status
	 * 
	 */

	public boolean addAll(Collection<? extends E> elements) {
		boolean added = list.addAll(elements);
		if (added) {
			NotifierAgent.instance().notify(new AddMultiNotifier<E>(elements), listeners, ex);
		}
		return added;
	}

	/**
	 * Add elements to the list at specified index, and notifies listener for
	 * Add event NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link List#addAll(int, Collection)))}
	 * 
	 * @param index
	 *            index where to add element to
	 * @param elements
	 * 
	 *            elements to be added.
	 * @return status
	 * 
	 */
	public boolean addAll(int index, Collection<? extends E> elements) {
		boolean added = list.addAll(index, elements);
		if (added) {
			NotifierAgent.instance().notify(new AddMultiNotifier<E>(elements), listeners, ex);
		}
		return added;
	}

	/**
	 * Clears list , and notifies listener event NotifictionEventType =
	 * {@link NotificationEventType#CLEAR} See {@link List#clear()}
	 * 
	 * Note - It iterates over collection to capture elements, so extra
	 * instruction, careful in performance apps
	 */
	public void clear() {
		List<E> elements = new ArrayList<>();
		if (CLEAR_METHOD_COPY_ENABLED) {
			elements = list.stream().collect(Collectors.toList());
		}
		list.clear();
		NotifierAgent.instance().notify(new ClearNotifier<E>(elements), listeners, ex);
		return;
	}

	/**
	 * No notification event {@link List#contains(Object))}
	 */
	public boolean contains(Object e) {
		return list.contains(e);
	}

	/**
	 * No notifiaction event {@link List#containsAll(Collection)))}
	 */
	public boolean containsAll(Collection<?> elements) {
		return list.containsAll(elements);
	}

	/**
	 * Return element from given index , and notifies listener for Read event
	 * NotifictionEventType = {@link NotificationEventType#READ}
	 * {@link List#get(int))}
	 * 
	 * @param index
	 *            index to fetch element from.
	 * @return E element
	 * 
	 */
	public E get(int index) {
		E element = list.get(index);

		NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
		return element;
	}

	/**
	 * No Notification event
	 */
	public int indexOf(Object element) {
		return list.indexOf(element);
	}

	/**
	 * No notification event
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * 
	 * Wrapped iterator, .next() generates read event NotifictionEventType =
	 * {@link NotificationEventType#READ} {@link List#iterator())}
	 * 
	 * @param elements
	 *            element to be added.
	 * @return status
	 * 
	 */
	public Iterator<E> iterator() {
		return new NotificationIterator<E>(list.iterator(), listeners, ex);
	}

	/**
	 * Wrapped list iterator, overrides methods and generates events
	 * 
	 * @author Kuldeep
	 *
	 */
	class NotificationListIterator implements ListIterator<E> {
		ListIterator<E> root;
		E lastReturned;

		public NotificationListIterator(ListIterator<E> root) {
			this.root = root;
		}

		/**
		 * Generates Add event
		 */
		@Override
		public void add(E element) {
			root.add(element);
			NotifierAgent.instance().notify(new AddNotifier<E>(element), listeners, ex);
		}

		@Override
		public boolean hasNext() {
			return root.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return root.hasPrevious();
		}

		/**
		 * Generates Read event
		 */
		@Override
		public E next() {
			E element = root.next();
			lastReturned = element;
			NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
			return element;
		}

		@Override
		public int nextIndex() {
			return root.nextIndex();
		}

		/**
		 * Generates read event
		 */
		@Override
		public E previous() {
			E element = root.previous();
			lastReturned = element;
			NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
			return element;
		}

		@Override
		public int previousIndex() {
			return root.previousIndex();
		}

		/**
		 * Generates delete event
		 */
		@Override
		public void remove() {
			root.remove();
			NotifierAgent.instance().notify(new DeleteNotifier<E>(lastReturned), listeners, ex);
		}

		/**
		 * Generates Modify Event
		 */
		@Override
		public void set(E element) {

			root.set(element);
			// to-do, fix
			NotifierAgent.instance().notify(new ModifyNotifier<E>(lastReturned, element), listeners, ex);
		}

	}

	/**
	 * No notification event
	 */
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	/**
	 * Returns wrapped list iterator {@link WrappedListIterator}. Overrides
	 * methods and generates events
	 */
	public ListIterator<E> listIterator() {
		return new NotificationListIterator(list.listIterator());
	}

	/**
	 * Returns wrapped list iterator {@link WrappedListIterator}. Overrides
	 * methods and generates events
	 */
	public ListIterator<E> listIterator(int index) {

		return new NotificationListIterator(list.listIterator(index));
	}

	/**
	 * Removes and Return element and notifies listener for Delete event
	 * NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link List#remove(Object))}
	 * 
	 * @param element
	 *            element to be removed.
	 * @return status
	 * 
	 */
	public boolean remove(Object element) {
		boolean removed = list.remove(element);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>((E) element), listeners, ex);
			} catch (Exception e) {
			}
		}
		return removed;
	}

	/**
	 * Removes and Return element from given index , and notifies listener for
	 * Delete event NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link List#remove(int))}
	 * 
	 * @param index
	 *            index to remove element from.
	 * @return E element
	 * 
	 */
	public E remove(int index) {
		E element = list.remove(index);
		if (element != null) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>(element), listeners, ex);
			} catch (Exception e) {
			}
		}
		return element;
	}

	/**
	 * Removes given elements from list, and notifies listener for Delete Multi
	 * event NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link List#removeAll(Collection))} Note - Extra instructions to send
	 * notification, be careful in performance
	 * 
	 * @param elements
	 *            elements to remove.
	 * @return sttaus
	 * 
	 */
	public boolean removeAll(Collection<?> elements) {

		Collection<E> removedElements = new ArrayList<>();
		if (REMOVEALL_METHOD_COPY_ENABLED) {
			for (E e : list) {
				if (elements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = list.removeAll(elements);
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
	 * {@link NotificationEventType#DELETE} {@link List#retainAll(Collection))}
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
			for (E e : list) {
				if (elements.contains(e)) {
					containedElements.add(e);
				}
			}
			for (E e : list) {
				if (!containedElements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = list.retainAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	/**
	 * Sets element at specified index and generates Modify event
	 * NotifictionEventType = {@link NotificationEventType#MODIFY}
	 * {@link List#set(int, Object))}
	 * 
	 * @param index
	 *            index to set element at.
	 * @param element
	 *            element to set
	 * @return E element
	 * 
	 */
	public E set(int index, E element) {
		E old = list.set(index, element);
		NotifierAgent.instance().notify(new ModifyNotifier<E>(old, element), listeners, ex);
		return old;
	}

	/**
	 * No notification event
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Return sublist , and notifies listener for Read multi event
	 * NotifictionEventType = {@link NotificationEventType#READ}
	 * {@link List#subList(int, int)}
	 * 
	 * @param fromIndex
	 *            from index
	 * 
	 * @param toIndex
	 *            to index to
	 * @return List<E> sub list
	 * 
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		List<E> sub = list.subList(fromIndex, toIndex);
		NotifierAgent.instance().notify(new ReadMultiNotifier<E>(sub), listeners, ex);
		return sub;
	}

	/**
	 * Not generates read event
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/**
	 * Not generates read event
	 */
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	/**
	 * As of now, no event generated on stream based operation.
	 */
	@Override
	public Stream<E> parallelStream() {
		return list.parallelStream();
	}

}
