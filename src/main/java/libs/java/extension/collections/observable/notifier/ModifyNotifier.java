package libs.java.extension.collections.observable.notifier;
import libs.java.extension.collections.observable.NotificationEvent;
import libs.java.extension.collections.observable.NotificationListener;
import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

public class ModifyNotifier<E> implements Notifier<E> {

//	private Collection<E> collection;
	private E oldElement, newElement;

	public ModifyNotifier(/*Collection<E> collection,*/ E oldElement, E newElement) {
	//	this.collection = collection;
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
