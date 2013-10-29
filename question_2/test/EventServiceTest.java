import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventServiceTest {

	public static class LoginSystem implements EventListener {

		public List<Event> events = new ArrayList<Event>();

		@Override
		public void notify(Event event) {
			events.add(event);
		}
	}

	public static class DatabaseSystem implements EventListener {

		public List<Event> events = new ArrayList<Event>();

		@Override
		public void notify(Event event) {
			events.add(event);
		}
	}

	public static class TestableEventService extends EventService {

		public int getNumberOfSubscribers() {
			int numSubscribers = 0;

			for (Set<EventListener> listener : this.listeners.values()) {
				numSubscribers += listener.size();
			}

			return numSubscribers;
		}

	}

	TestableEventService eventService;
	DatabaseSystem databaseEventListener;
	LoginSystem loginSystemEventListener;

	FaultEvent faultEvent;
	SecurityEvent securityEvent;
	FinancialEvent financialEvent;

	@Before
	public void setup() {
		eventService = new TestableEventService();
		databaseEventListener = new DatabaseSystem();
		loginSystemEventListener = new LoginSystem();
		faultEvent = new FaultEvent(FaultEvent.EventLevel.MODERATE);
		securityEvent = new SecurityEvent(SecurityEvent.SecurityLevel.AUTHORIZATION_FAILED);
		financialEvent = new FinancialEvent(FinancialEvent.FinancialLevel.ABOVE_500K);
	}

	@After
	public void tearDown() {
		eventService = null;
		databaseEventListener = null;
		loginSystemEventListener = null;
		faultEvent = null;
		securityEvent = null;
		financialEvent = null;
	}

	@Test
	public void testEventServiceSubscribe() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, FaultEvent.class);
		eventService.subscribe(databaseEventListener, SecurityEvent.class);

		assertEquals(2, eventService.getNumberOfSubscribers());
	}

	@Test
	public void testEventServiceUnsubscribe() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, FaultEvent.class);
		eventService.subscribe(databaseEventListener, SecurityEvent.class);

		assertEquals(2, eventService.getNumberOfSubscribers());

		eventService.unsubscribe(loginSystemEventListener, FaultEvent.class);
		eventService.unsubscribe(databaseEventListener, SecurityEvent.class);

		assertEquals(0, eventService.getNumberOfSubscribers());
	}

	@Test
	public void testSimpleSubscribeAndPublishWorks() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, FaultEvent.class);

		eventService.publish(faultEvent);

		assertEquals(1, loginSystemEventListener.events.size());
	}

	@Test
	public void testPublishNonSubscribeEventWorks() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, FaultEvent.class);

		eventService.publish(securityEvent);

		assertEquals(0, loginSystemEventListener.events.size());
	}

	@Test
	public void testSubscribeAndPublishMultipleEvents() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, FaultEvent.class);
		eventService.subscribe(loginSystemEventListener, SecurityEvent.class);
		eventService.subscribe(databaseEventListener, SecurityEvent.class);

		eventService.publish(faultEvent);

		assertEquals(1, loginSystemEventListener.events.size());
		assertEquals(0, databaseEventListener.events.size());

		eventService.publish(securityEvent);

		assertEquals(2, loginSystemEventListener.events.size());
		assertEquals(1, databaseEventListener.events.size());
	}

	// Add Additional tests below here.

	@Test
	public void testSubscribeAndPublishWithNonConcreteClass() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(databaseEventListener, ManagementEvent.class);
		eventService.subscribe(loginSystemEventListener, Event.class);

		assertEquals(0, loginSystemEventListener.events.size());
		assertEquals(0, databaseEventListener.events.size());

		eventService.publish(faultEvent);

		assertEquals(1, loginSystemEventListener.events.size());
		assertEquals(1, databaseEventListener.events.size());

		eventService.publish(securityEvent);

		assertEquals(2, loginSystemEventListener.events.size());
		assertEquals(2, databaseEventListener.events.size());

	}

	@Test
	public void testGetSubTypesOf() {

		Set<Class<? extends Event>> classes = eventService.getSubTypesOf(ManagementEvent.class);

		assertEquals(true, classes.contains(FaultEvent.class));
		assertEquals(true, classes.contains(SecurityEvent.class));
		assertEquals(true, classes.contains(FinancialEvent.class));
	}

	@Test
	public void testSubscribeUnsubscribeNonConcreteEvent() {
		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(loginSystemEventListener, Event.class);
		eventService.subscribe(databaseEventListener, ManagementEvent.class);

		assertEquals(6, eventService.getNumberOfSubscribers());

		eventService.unsubscribe(loginSystemEventListener, FaultEvent.class);
		eventService.unsubscribe(databaseEventListener, SecurityEvent.class);
		eventService.unsubscribe(databaseEventListener, FinancialEvent.class);

		assertEquals(3, eventService.getNumberOfSubscribers());

		eventService.unsubscribe(databaseEventListener, FaultEvent.class);

		assertEquals(2, eventService.getNumberOfSubscribers());

		eventService.unsubscribe(loginSystemEventListener, Event.class);

		assertEquals(0, eventService.getNumberOfSubscribers());
	}

	@Test
	public void testPublishNonConcreteEvent() {

		assertEquals(0, eventService.getNumberOfSubscribers());

		eventService.subscribe(databaseEventListener, ManagementEvent.class);
		eventService.subscribe(loginSystemEventListener, Event.class);

		assertEquals(0, loginSystemEventListener.events.size());
		assertEquals(0, databaseEventListener.events.size());

		eventService.publish(faultEvent);
		eventService.publish(financialEvent);

		assertEquals(2, loginSystemEventListener.events.size());
		assertEquals(2, databaseEventListener.events.size());

		eventService.unsubscribe(databaseEventListener, Event.class);

		assertEquals(2, loginSystemEventListener.events.size());
		assertEquals(2, databaseEventListener.events.size());

	}

}