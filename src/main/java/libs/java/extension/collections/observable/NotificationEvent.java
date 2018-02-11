package libs.java.extension.collections.observable;
import java.util.Collection;

import libs.java.extension.collections.observable.NotificationListener.NotificationEventType;

/**
 * Notification event, different values available at different type of notification
 * 
 * @author Kuldeep
 *
 * @param <E>
 */
public class NotificationEvent<E> {

	/**
	 * Only available in single element event.
	 * Current element in add/delete notification, new element in case of modify.
	 */
	private E element;
	/**
	 * Only available when event is of multi type
	 */
	private Collection<? extends E> elements;
	/**
	 * type of event
	 */
	private NotificationEventType type;
	/**
	 * Available in modify event. old value of element
	 */
	private E oldElement;
	// private Collection<E> underlyingCollection;

	/**
	 * if event change multiple elements
	 */
	private boolean multi = false;

	public boolean isAdd() {

		return type == NotificationEventType.ADD;
	}

	public boolean isMulti() {
		return multi;
	}

	public boolean isModify() {
		return type == NotificationEventType.MODIFY;
	}

	public boolean isDelete() {
		return type == NotificationEventType.DELETE;

	}

	public boolean isClear() {
		return type == NotificationEventType.CLEAR;
	}

	public E getOldElement() {
		return oldElement;
	}

	/*
	 * public Collection<E> getUnderlyingCollection() { return
	 * underlyingCollection; }
	 */

	public NotificationEvent(/*Collection<E> underlyingCollection,*/ E element, NotificationEventType type) {
		this.element = element;
		// this.underlyingCollection = underlyingCollection;
		this.type = type;
	}

	public NotificationEvent(/*Collection<E> underlyingCollection,*/ Collection<? extends E> elements,
			NotificationEventType type) {
		this.elements = elements;
		this.type = type;
		// this.underlyingCollection = underlyingCollection;
		this.multi = true;
	}

	public NotificationEvent(/*Collection<E> underlyingCollection,*/ E element, E oldElement, NotificationEventType type) {
		this.element = element;
		this.type = type;
//		this.underlyingCollection = underlyingCollection;
	}

	public NotificationEvent(/*Collection<E> underlyingCollection,*/ Collection<? extends E> elements, E oldElement,
			NotificationEventType type) {
		this.elements = elements;
		this.type = type;
//		this.underlyingCollection = underlyingCollection;
	}

	public E getElement() {
		return element;
	}

	public Collection<? extends E> getElements() {
		return elements;
	}

	public NotificationEventType getType() {
		return type;
	}

}
