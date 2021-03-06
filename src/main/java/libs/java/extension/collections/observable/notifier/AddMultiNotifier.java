package libs.java.extension.collections.observable.notifier;

import java.util.Collection;

import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Add Event notifier (Multiple elements).
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public class AddMultiNotifier<E> implements Notifier<E> {

	private Collection<? extends E> elements;

	public AddMultiNotifier(Collection<? extends E> elements) {
		this.elements = elements;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>(elements, NotificationEventType.ADD);
		listener.onEvent(event);
	}

}
