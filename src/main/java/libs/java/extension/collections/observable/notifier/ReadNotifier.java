package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

public class ReadNotifier< E> implements Notifier< E> {

	//private Collection<E> collection;
	private E element;

	public ReadNotifier(/*Collection<E> collection,*/ E element) {
//		this.collection = collection;
		this.element = element;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>(element, NotificationEventType.READ);
		listener.onEvent(event);
	}

}
