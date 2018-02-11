package libs.java.extension.collections.observable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import libs.java.extension.collections.observable.notifier.NotifierAgent;
import libs.java.extension.collections.observable.notifier.ReadNotifier;

/**
 * A wrapped iterator, overrides iterator methods and generates event
 * 
 * @author Kuldeep
 *
 */
public class NotificationIterator<E> implements Iterator<E> {
	private Iterator<E> root;
	// private Collection<E> underlyingCollection;
	private List<NotificationListener<E>> listeners;
	private Executor ex;

	public NotificationIterator(Iterator<E> root, List<NotificationListener<E>> listeners, Executor ex) {
		// this.underlyingCollection = collection;
		this.listeners = listeners;
		this.ex = ex;
		this.root = root;
	}

	public boolean hasNext() {

		return root.hasNext();
	}

	public E next() {
		E element = root.next();
		NotifierAgent.instance().notify(new ReadNotifier<E>(element), listeners, ex);
		return element;
	}

}
