package libs.java.extension.collections.observable.notifier;
import java.util.Collection;

import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

public class DeleteMultiNotifier<E> implements Notifier<E> {

	//private Collection<E> collection;
	private Collection<? extends E> elements;

	public DeleteMultiNotifier(/*Collection<E> collection, */Collection<? extends E> elements) {
	//	this.collection = collection;
		this.elements = elements;
	}

	@Override
	public void notify(NotificationListener<E> listener) {
		NotificationEvent<E> event = new NotificationEvent<>(elements, NotificationEventType.DELETE);
		listener.onEvent(event);
	}

}
