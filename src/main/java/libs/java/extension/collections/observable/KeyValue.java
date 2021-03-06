package libs.java.extension.collections.observable;

/**
 * Used to forward notification in case of map (without creating too many
 * interface, it fixes notification data contained in this bean with key-value
 * 
 * @author Kuldeep
 *
 * @param <K>
 * @param <V>
 */
public class KeyValue<K, V> {
	private K key;
	private V value;

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "KeyValue [key=" + key + ", value=" + value + "]";
	}
}