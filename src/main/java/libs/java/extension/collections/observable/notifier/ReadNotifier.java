package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Read event notifier (Single element)
 * @author Kuldeep
 *
 * @param <E>
 */
public class ReadNotifier< E> implements Notifier< E> {

	private E element;

	public ReadNotifier(E element) {
		this.element = element;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>(element, NotificationEventType.READ);
		listener.onEvent(event);
	}

}
