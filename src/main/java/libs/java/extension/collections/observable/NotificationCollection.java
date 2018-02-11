package libs.java.extension.collections.observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Base class with methods to ignore creating copy for event. It is not
 * implementation of{@link Collection}
 * 
 * NOTE - As of now, no event generated on stream based operation.
 * 
 * @author Kuldeep
 *
 */
public abstract class NotificationCollection<E> implements Collection<E> {

	protected boolean RETAIN_METHOD_COPY_ENABLED = true;
	protected boolean REMOVEALL_METHOD_COPY_ENABLED = true;
	protected boolean CLEAR_METHOD_COPY_ENABLED = true;
	/**
	 * Executor, if provided, used to send notification, such that caller is not
	 * blocked
	 */
	protected Executor ex;
	protected List<NotificationListener<E>> listeners = new ArrayList<>();

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableRetainMethodCopy() {
		RETAIN_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableContainMethodCopy()} flag
	 */
	public void enableRetainMethodCopy() {
		RETAIN_METHOD_COPY_ENABLED = true;
	}

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableRemoveAllMethodCopy() {
		REMOVEALL_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableRemoveAllMethodCopy()} flag
	 */
	public void ennableRemoveAllMethodCopy() {
		REMOVEALL_METHOD_COPY_ENABLED = true;
	}

	/**
	 * Disables copy of contents before removing, event still generated but with
	 * empty collection. This is to avoid copying elements when performance is
	 * required.
	 */
	public void disableClearMethodCopy() {
		CLEAR_METHOD_COPY_ENABLED = false;
	}

	/**
	 * Reverses {@link NotificationCollection#disableClearMethodCopy()} flag
	 */
	public void ennableClearMethodCopy() {
		CLEAR_METHOD_COPY_ENABLED = true;
	}

	public Executor getExecutor() {
		return ex;

	}

	public void setExecutor(Executor ex) {
		this.ex = ex;
	}

	public void addListener(NotificationListener<E> listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes listener to the list.
	 * 
	 * @param listener
	 *            listener object
	 * @return status
	 */
	public boolean removeListener(NotificationListener<E> listener) {
		if (listener == null) {
			return false;
		}
		return listeners.remove(listener);
	}

}
