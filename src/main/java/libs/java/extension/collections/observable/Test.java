package libs.java.extension.collections.observable;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		Map<String, String> a = new NotificationMap<>(new HashMap<>(), null,
				new NotificationListener<KeyValue<String, String>>() {

					@Override
					public void onEvent(NotificationEvent<KeyValue<String, String>> event) {
						System.err.println(event.getElement());
					}

				});
		a.put("String2", "New");
		a.put("String", "NEWONE");
		NotificationCollection<String> values = (NotificationCollection<String>)a.values();
		System.out.println(values);
		values.addListener(new NotificationListener<String>() {
			
			@Override
			public void onEvent(NotificationEvent<String> event) {
				System.out.println(event.getType());
			}
		});
		values.remove("New");
		System.out.println(values);
		/*
		 * 
		 * List<String> list = new ArrayList<>(); list.add("B"); list.add("C");
		 * a.add("A"); a.get(0); a.addAll(list); Iterator<String> iterator =
		 * a.iterator(); while (iterator.hasNext()) {
		 * System.out.println(iterator.next()); } a.clear();
		 */
	}

}
