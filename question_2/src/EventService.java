import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class EventService {

	protected final Map<Class<? extends Event>, Set<EventListener>> listeners = new HashMap<Class<? extends Event>, Set<EventListener>>();

	public EventService() {

	}

	/**
	 * Publishes an event to all of the listeners that have subscribed to that event. All listeners should only be notified once per event.
	 * 
	 * @param event
	 *            the event to notify listerners with
	 */
	public void publish(Event event) {

		Class<? extends Event> eventClass = event.getClass();

		if (listeners.containsKey(eventClass)) {
			for (EventListener listener : listeners.get(event.getClass())) {
				listener.notify(event);
			}
		}
	}

	public void subscribe(EventListener eventListener, Class<? extends Event> eventType) {

		Set<Class<? extends Event>> classes = getSubTypesOf(eventType);

		if (classes.size() > 0) {
			for (Class<? extends Event> clazz : classes) {
				addASubscription(eventListener, clazz);
			}
		} else {
			addASubscription(eventListener, eventType);
		}
	}

	private void addASubscription(EventListener eventListener, Class<? extends Event> eventType) {
		if (listeners.containsKey(eventType)) {
			listeners.get(eventType).add(eventListener);
		} else {
			Set<EventListener> events = new HashSet<EventListener>();
			events.add(eventListener);
			listeners.put(eventType, events);
		}
	}

	public void unsubscribe(EventListener eventListener, Class<? extends Event> eventType) {

		Set<Class<? extends Event>> classes = getSubTypesOf(eventType);

		if (classes.size() > 0) {
			for (Class<? extends Event> clazz : classes) {
				removeASubscription(eventListener, clazz);
			}
		} else {
			removeASubscription(eventListener, eventType);
		}
	}

	private void removeASubscription(EventListener eventListener, Class<? extends Event> eventType) {
		if (listeners.containsKey(eventType)) {
			listeners.get(eventType).remove(eventListener);
			if (listeners.get(eventType).size() == 0) {
				listeners.remove(eventType);
			}
		}
	}

	public Set<Class<? extends Event>> getSubTypesOf(Class<? extends Event> eventType) {
		Set<Class<? extends Event>> classes = new HashSet<Class<? extends Event>>();

		// at the runtime, lookup in the implementing classes from eventType class
		Reflections reflections = new Reflections("");
		Set<?> specificClassesSet = reflections.getSubTypesOf(eventType);
		if (specificClassesSet.size() > 0) {
			for (Object clazz : specificClassesSet) {
				@SuppressWarnings("unchecked")
				Class<? extends Event> subClass = (Class<? extends Event>) clazz;
				Set<Class<? extends Event>> subTypesOfSet = getSubTypesOf(subClass);
				if (subTypesOfSet.size() == 0) {
					classes.add(subClass);
				} else {
					classes.addAll(subTypesOfSet);
				}
			}
		}

		return classes;
	}

}