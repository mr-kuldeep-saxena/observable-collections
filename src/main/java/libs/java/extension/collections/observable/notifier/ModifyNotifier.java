package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Modify event notifier
 * @author Kuldeep
 *
 * @param <E>
 */
public class ModifyNotifier<E> implements Notifier<E> {

	private E oldElement, newElement;

	public ModifyNotifier( E oldElement, E newElement) {
		this.oldElement = oldElement;
		this.newElement = newElement;

	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> eventData = new NotificationEvent<>(oldElement, newElement,
				NotificationEventType.MODIFY);
		listener.onEvent(eventData);

	}

}
