package libs.java.extension.collections.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import libs.java.extension.collections.observable.notifier.AddMultiNotifier;
import libs.java.extension.collections.observable.notifier.AddNotifier;
import libs.java.extension.collections.observable.notifier.ClearNotifier;
import libs.java.extension.collections.observable.notifier.DeleteMultiNotifier;
import libs.java.extension.collections.observable.notifier.DeleteNotifier;
import libs.java.extension.collections.observable.notifier.NotifierAgent;

/**
 * Default implementation with Collection methods, to provide notification.
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public class AbstractNotificationCollection<E> extends NotificationCollection<E> {

	private Collection<E> collection;

	public AbstractNotificationCollection(Collection<E> collection) {
		this.collection = collection;
	}

	@Override
	public boolean add(E e) {
		boolean added = collection.add(e);
		if (added) {
			NotifierAgent.instance().notify(new AddNotifier<E>(e), listeners, ex);
		}
		return added;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean added = collection.addAll(c);
		if (added) {
			NotifierAgent.instance().notify(new AddMultiNotifier<E>(c), listeners, ex);
		}
		return added;
	}

	@Override
	public void clear() {
		List<E> elements = new ArrayList<>();
		if (CLEAR_METHOD_COPY_ENABLED) {
			elements = collection.stream().collect(Collectors.toList());
		}
		collection.clear();
		NotifierAgent.instance().notify(new ClearNotifier<E>(elements), listeners, ex);
		return;
	}

	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new NotificationIterator<>(collection.iterator(), listeners, ex);
	}

	@Override
	public boolean remove(Object element) {
		boolean removed = collection.remove(element);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteNotifier<E>((E) element), listeners, ex);
			} catch (Exception e) {
			}
		}
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> elements) {
		Collection<E> removedElements = new ArrayList<>();
		if (REMOVEALL_METHOD_COPY_ENABLED) {
			for (E e : collection) {
				if (elements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = collection.removeAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	@Override
	public boolean retainAll(Collection<?> elements) {
		Collection<E> removedElements = new ArrayList<>();
		if (RETAIN_METHOD_COPY_ENABLED) {
			Collection<E> containedElements = new ArrayList<>();
			for (E e : collection) {
				if (elements.contains(e)) {
					containedElements.add(e);
				}
			}
			for (E e : collection) {
				if (!containedElements.contains(e)) {
					removedElements.add(e);
				}
			}
		}
		boolean removed = collection.retainAll(elements);
		if (removed) {
			try {
				NotifierAgent.instance().notify(new DeleteMultiNotifier<E>(removedElements), listeners, ex);
			} catch (Exception e) {
			}
		}

		return removed;
	}

	@Override
	public int size() {
		return collection.size();
	}

	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return collection.toArray(a);
	}

	@Override
	public String toString() {
		return "AbstractNotificationCollection [collection=" + collection + "]";
	}

}
