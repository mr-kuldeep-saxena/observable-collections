package libs.java.extension.collections.observable.notifier;

import libs.java.extension.collections.observable.NotificationListener;

public interface Notifier<E> {

	public void notify(NotificationListener<E> listeners);
}
