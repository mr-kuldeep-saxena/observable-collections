package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Add event notifier
 * @author Kuldeep
 *
 * @param <E>
 */
public class AddNotifier<E> implements Notifier<E> {

	private E e;
	
	public AddNotifier(E element) {
		this.e = element;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>(e, NotificationEventType.ADD);
		listener.onEvent(event);
	}

}
