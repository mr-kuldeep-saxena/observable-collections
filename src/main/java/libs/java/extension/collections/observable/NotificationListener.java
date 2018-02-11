package libs.java.extension.collections.observable;

/**
 * 
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
