package server;

import java.io.IOException;

import org.junit.After;

import calendar.Person;

public class TestRoomHandler {
	Person dummy;
	
//	@BeforeClass
//	public static void truncateRooms() throws IOException, SQLException{
//		String query = "TRUNCATE TABLE Room";
//		Execute.update(query);
////		new Room("R1", 480);
//		new Room("F1", 500);
////		new Room("R7", 300);
//		new Room("EL-204", 6);
//		new Room("Dassen", 1);
//		new Room("ForTwo", 2);
////		new Room("PourTrois", 3);
//	}
	
//	@Before
//	public void setUpDummyAndTruncateDb() throws IOException{
//		dummy = new Person("john", "lol", "emails", null, "passord");
//		String query = "TRUNCATE TABLE Appointment";
//		Execute.update(query);
//	}
	
	@After
	public void tearDownDummy() throws IOException{
		dummy.delete();
	}
	
//	@Test
//	public void testIsValid() throws IOException{
//		int hourInMs = 1000*60*60;
//		Date monday8 = new Date(getStartOfWeek(1).getTime() + 8*hourInMs);
//		Date tuesday8 = new Date(monday8.getTime() + 24*hourInMs);
//		Date tuesday10 = new Date(tuesday8.getTime() + 2*hourInMs);
//		Date wednesday8 = new Date(tuesday8.getTime() + 24*hourInMs);
//		assertEquals("Alle 4 rommene skal være tilgjengelige", 4, RoomHandlerImpl.availableRooms(monday8, wednesday8, 1).size());
//		assertEquals("Skal være 3 rom med plass til 2 personer", 3, RoomHandlerImpl.availableRooms(monday8, wednesday8, 2).size());
//		assertEquals("Ingen rom med plass til mer enn 500 deltagere!", 0, RoomHandlerImpl.availableRooms(monday8, wednesday8, 501).size());
//		assertEquals("Skal være ett rom med plass til 500!", 1, RoomHandlerImpl.availableRooms(monday8, wednesday8, 500).size());
//		Appointment mondayMeeting = new Appointment("Avtale", monday8, new Date(monday8.getTime() + (int) 1.5*hourInMs), false, null, dummy);
//		mondayMeeting.setRoomName("EL-204");
//		mondayMeeting.save();
//		Appointment tuesdayMeeting = new Appointment("Ledermote", tuesday8, new Date(tuesday8.getTime() + (int) 1.5*hourInMs), false, null, dummy);
//		tuesdayMeeting.setRoomName("ForTwo");
//		tuesdayMeeting.save();
//		Appointment tuesdayLateMeeting = new Appointment("Møte", tuesday10, new Date(tuesday10.getTime() + (int) 3*hourInMs), false, null, dummy);
//		tuesdayLateMeeting.setRoomName("F1");
//		tuesdayLateMeeting.save();
//		assertEquals("1 av rommene skal være tilgjengelige", 1, RoomHandlerImpl.availableRooms(monday8, wednesday8, 1).size());
//		Date tuesday959 = new Date(tuesday10.getTime()-1);
//		assertEquals("under ett møte skal alle rom bortsett fra det være ledige", 3, RoomHandlerImpl.availableRooms(tuesday8, tuesday959, 1).size());
//	}
	
//	@Test
//	public void testAvailableRooms() throws IOException{
//		Date start = new Date(System.currentTimeMillis()-20000000);
//		Date end = new Date(System.currentTimeMillis()-10000000);
//		List<Room> roomsAvaiable = RoomHandlerImpl.availableRooms(start, end, 1);
//		
//		for (Room room: roomsAvaiable) {
//			System.out.println(room.getName());
//		}
//	}
	
}
