package libs.java.extension.collections.observable.notifier;
import java.util.Collection;

import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Clear event notifier
 * @author Kuldeep
 *
 * @param <E>
 */
public class ClearNotifier<E> implements Notifier<E> {

	private Collection<? extends E> elementsDeleted;

	public ClearNotifier(Collection<? extends E> elementsDeleted) {
		this.elementsDeleted = elementsDeleted;

	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>( elementsDeleted, NotificationEventType.CLEAR);
		listener.onEvent(event);
	}

}
