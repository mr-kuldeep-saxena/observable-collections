package libs.java.extension.collections.observable;

/**
 * 
 * Listener to be implemented by classes to listen for various events,
 * {@link NotificationEventType}.
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public interface NotificationListener<E> {

	public void onEvent(NotificationEvent<E> event);

	public enum NotificationEventType {
		READ, DELETE, CLEAR, ADD, MODIFY;
	}

}
