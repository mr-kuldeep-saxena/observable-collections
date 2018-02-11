package libs.java.extension.collections.observable.notifier;

import libs.java.extension.collections.observable.NotificationListener;

/**
 * Default notifier interface, implemented by different notifiers and used by
 * {@link NotifierAgent}
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public interface Notifier<E> {

	public void notify(NotificationListener<E> listeners);
}
