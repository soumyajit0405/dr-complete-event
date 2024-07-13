
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

class WorkerThread implements Runnable {
	public int eventId;
	public String startTime;
	public int eventTypeId;
	
	public WorkerThread(int eventId, String startTime, int eventTypeId) {
		this.eventId = eventId;
		this.startTime = startTime;
		this.eventTypeId = eventTypeId;
	}

	public void run() {
		try {
			ScheduleDAO sdc= new ScheduleDAO();
			System.out.println("Inside worker thread "+ eventTypeId + " "+ eventId);
			if (eventTypeId == 2) {
				System.out.println("Inside worker thread 1"+ eventTypeId + " "+ eventId);	
				ArrayList<HashMap<String,Object>> listOfCustomers=sdc.getEventCustomer(eventId);
				if (listOfCustomers.size() > 0) {

					System.out.println("Inside worker thread 2");
					ExecutorService executor = Executors.newFixedThreadPool(listOfCustomers.size());// creating a pool of 1000
																								// threads
					for (int i = 0; i < listOfCustomers.size(); i++) {
						Runnable worker = new EventCustomerThread((int) listOfCustomers.get(i).get("customerId"),(int) listOfCustomers.get(i).get("eventCustomerMapping"),startTime,eventId );
						System.out.println("Inside loop1");
						executor.execute(worker);// calling execute method of ExecutorService
					}
					sdc.updateEventStatus(eventId,startTime);
					executor.shutdown();
					while (!executor.isTerminated()) {
					}
					
				}
				sdc.updateEventStatus(eventId,startTime);				
			} else {
				sdc.updateEventStatus(eventId,startTime);
			}


			
	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		finally {
			if (ScheduleDAO.con != null) {
//				try {
//			//		ScheduleDAO.con.close();  Close later
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
				}
		System.out.println(Thread.currentThread().getName() + " (End)");// prints thread name
	}

	private void processmessage() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}