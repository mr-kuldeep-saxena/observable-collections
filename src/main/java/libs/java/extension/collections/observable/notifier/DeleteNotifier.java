package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

public class DeleteNotifier<E> implements Notifier<E> {

//	private Collection<E> collection;
	private E element;

	public DeleteNotifier(/*Collection<E> collection,*/ E element) {
		this.element = element;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>( element, NotificationEventType.DELETE);
		listener.onEvent(event);
	}

}
