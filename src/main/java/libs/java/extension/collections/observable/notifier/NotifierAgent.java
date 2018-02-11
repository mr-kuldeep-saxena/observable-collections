package libs.java.extension.collections.observable.notifier;

import java.util.List;
import java.util.concurrent.Executor;

import libs.java.extension.collections.observable.NotificationListener;

public class NotifierAgent {

	private static final NotifierAgent agent = new NotifierAgent();

	protected NotifierAgent() {

	}

	public static NotifierAgent instance() {
		return agent;
	}

	public <E> void notify(Notifier<E> notifier, List<NotificationListener<E>> listeners, Executor ex) {
		if (listeners != null) {
			for (NotificationListener<E> listener : listeners) {
				if (ex != null) {
					ex.execute(new Runnable() {
						public void run() {
							notifier.notify(listener);
						}
					});
				} else {
					notifier.notify(listener);
				}
			}
		}

	}

}
