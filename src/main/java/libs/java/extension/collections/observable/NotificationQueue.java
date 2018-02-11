package libs.java.extension.collections.observable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;
import libs.java.extension.collections.observable.notifier.AddMultiNotifier;
import libs.java.extension.collections.observable.notifier.AddNotifier;
import libs.java.extension.collections.observable.notifier.ClearNotifier;
import libs.java.extension.collections.observable.notifier.DeleteMultiNotifier;
import libs.java.extension.collections.observable.notifier.DeleteNotifier;
import libs.java.extension.collections.observable.notifier.NotifierAgent;
import libs.java.extension.collections.observable.notifier.ReadNotifier;

public class NotificationQueue<E> extends NotificationCollection<E> implements Queue<E> {

	/**
	 * Underlying queue
	 */
	private Queue<E> queue;
	

	/**
	 * Constructor with underlying queue, no executor, so all notification will
	 * be part of queue operation thread
	 * 
	 * @param queue
	 *            underlying queue
	 */
	public NotificationQueue(Queue<E> queue) {
		this(queue, null);
	}

	/**
	 * Constructor with queue and executor, all notification to listeners will
	 * be sent using this executor
	 * 
	 * @param queue
	 *            underlying list
	 * @param ex
	 *            executor
	 */
	public NotificationQueue(Queue<E> queue, Executor ex) {
		this(queue, ex, null);
	}

	/**
	 * Constructor with queue, executor and listener. This is to simplify
	 * registration of listener in separate call (See
	 * {@link NotificationList#addListener(NotificationListener)}. However more
	 * listener can be added, removed
	 * 
	 * @param queue
	 *            underlying queue
	 * @param ex
	 *            executor
	 * @param listener
	 *            listener for event
	 * @throws IllegalArgumentException
	 *             if queue is null
	 */
	public NotificationQueue(Queue<E> queue, Executor ex, NotificationListener<E> listener) {

		if (queue == null) {
			throw new IllegalArgumentException("Passed queue can't be null");
		}
		this.queue = queue;
		this.ex = ex;
		if (listener != null) {
			super.listeners.add(listener);
		}
	}

	/**
	 * returns underlying queue
	 * 
	 * @return queue
	 */
	public Queue<E> getQueue() {
		return queue;
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new NotificationIterator<>(queue.iterator(), listeners, ex);
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	/**
	 * Removes and Return element and notifies listener for Delete event
	 * NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link Queue#remove(Object))}
	 * 
	 * @param element
	 *            element to be removed.
	 * @return status
	 * 
	 */
	@Override
	public boolean remove(Object o) {
		boolean removed = queue.remove(o);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>((E) o), listeners, ex);
			} catch (Exception e) {

			}
		}
		return removed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	/**
	 * Add elements to the queue , and notifies listener for Add event
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link Queue#addAll(Collection))}
	 * 
	 * @param elements
	 *            elements to be added.
	 * @return status
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean added = queue.addAll(c);
		if (added) {
			NotifierAgent.instance().notify(new AddMultiNotifier<E>(c), listeners, ex);
		}
		return added;
	}

	/**
	 * Removes given elements from queue, and notifies listener for Delete Multi
	 * event NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link List#removeAll(Collection))} Note - Extra instructions to send
	 * notification, be careful in performance
	 * 
	 * @param elements
	 *            elements to remove.
	 * @return sttaus
	 * 
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		Collection<E> removedElements = new LinkedList<>();
		for (E e : queue) {
			if (c.contains(e)) {
				removedElements.add(e);
			}
		}
		boolean removed = queue.removeAll(c);
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
	 * {@link NotificationEventType#DELETE} {@link Queue#retainAll(Collection))}
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
			for (E e : queue) {
				if (elements.contains(e)) {
					containedElements.add(e);
				}
			}
			for (E e : queue) {
				if (!containedElements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = queue.retainAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	/**
	 * Clears queue , and notifies listener event NotifictionEventType =
	 * {@link NotificationEventType#CLEAR} See {@link List#clear()}
	 * 
	 * Note - It iterates over collection to capture elements, so extra
	 * instruction, careful in performance apps
	 */
	@Override
	public void clear() {

		List<E> elements = new ArrayList<>();
		if (CLEAR_METHOD_COPY_ENABLED) {
			queue.stream().collect(Collectors.toList());
		}
		queue.clear();
		NotifierAgent.instance().notify(new ClearNotifier<E>(elements), listeners, ex);
		return;
	}

	/**
	 * Add element to the queue, and notifies listener for Add event.
	 * NotifictionEventType = {@link NotificationEventType#ADD}
	 * {@link Queue#add(Object))}
	 * 
	 * @param e
	 *            element to be added.
	 * @return status
	 * 
	 */
	public boolean add(E e) {
		boolean added = queue.add(e);
		if (added) {
			NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);
		}
		return added;
	}

	@Override
	public boolean offer(E e) {
		boolean added = queue.offer(e);
		if (added) {
			NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);
		}
		return added;
	}

	/**
	 * Removes and Return head of queue and notifies listener for Delete event
	 * NotifictionEventType = {@link NotificationEventType#DELETE}
	 * {@link Queue#remove(Object))}
	 * 
	 * @return element
	 * 
	 */
	@Override
	public E remove() {
		E element = queue.remove();
		if (element != null) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>(element), listeners, ex);
			} catch (Exception e) {
			}
		}
		return element;
	}

	@Override
	public E poll() {
		E element = queue.poll();
		if (element != null) {
			NotifierAgent.instance().notify(new DeleteNotifier<E>(element), listeners, ex);
		}
		return element;
	}

	@Override
	public E element() {
		E element = queue.element();
		if (element != null) {
			NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
		}
		return element;
	}

	@Override
	public E peek() {
		E element = queue.peek();
		if (element != null) {
			NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
		}
		return element;
	}
}
