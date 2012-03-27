package server;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.RmiStarter;
import calendar.Appointment;
import calendar.Message;
import calendar.Person;
import calendar.Room;

public class DBStarter extends RmiStarter {

	public DBStarter() {
		super(AppointmentHandlerImpl.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			LocateRegistry.createRegistry(1099);
			Registry registry = LocateRegistry.getRegistry();
			
			InetAddress addr = InetAddress.getLocalHost();
			System.out.println("RMI server running on: " + addr.getHostAddress());
            AppointmentHandler appEngine= new AppointmentHandlerImpl();
            AppointmentHandler appEngineStub = (AppointmentHandler) UnicastRemoteObject.exportObject(appEngine, 0);
            registry.rebind(AppointmentHandler.SERVICE_NAME, appEngineStub);
            AppointmentHandlerImpl.init();
            Appointment.bindToHandler();
            
            MessageHandler msgEngine = new MessageHandlerImpl();
            MessageHandler msgEngineStub = (MessageHandler) UnicastRemoteObject.exportObject(msgEngine, 0);
            registry.rebind(MessageHandler.SERVICE_NAME, msgEngineStub);
            MessageHandlerImpl.init();
            Message.bindToHandler();
            
            PersonHandler personEngine = new PersonHandlerImpl();
            PersonHandler personEngineStub = (PersonHandler) UnicastRemoteObject.exportObject(personEngine, 0);
            registry.rebind(PersonHandler.SERVICE_NAME, personEngineStub);
            PersonHandlerImpl.init();
            Person.bindToHandler();
            
            RoomHandler roomEngine = new RoomHandlerImpl();
            RoomHandler roomEngineStub = (RoomHandler) UnicastRemoteObject.exportObject(roomEngine, 0);
            registry.rebind(RoomHandler.SERVICE_NAME, roomEngineStub);
            Room.bindToHandler();
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		new DBStarter();
		System.out.println("Accepting incoming.");
	}

}
