package com.dev.controller;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import com.dev.beans.Available;
import com.dev.beans.Bus;
import com.dev.beans.Ticket;
import com.dev.beans.User;
import com.dev.exception.BusNotFoundException;
import com.dev.exception.DeleteException;
import com.dev.exception.LoginException;
import com.dev.exception.RegisterException;
import com.dev.exception.TicketBookingException;
import com.dev.exception.UpdateException;
import com.dev.service.Service;
import com.dev.service.ServiceImpl;

public class UserApp {
	static int userid = 0; // global user id
	static Service service = new ServiceImpl();
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("1.Login" + "\n" + "2.Register");
		int option = Integer.parseInt(sc.next());
		if (option == 1) {
			Boolean login = false;
			try {
				login = loginUser(); // user login
			} catch (LoginException e) {
				System.out.println(e.getMessage());
			}
			if (login) {
				System.out.println("Login Successful");
				boolean bo = true;
				while (bo) {

					System.out.println("1.Update Profile" + "\n" + "2.Delete Profile" + "\n" + "3.Search Bus" + "\n"
							+ "4.Check Availability" + "\n" + "5.Book Ticket" + "\n" + "6.Get Ticket" + "\n"
							+ "7.Cancel Ticket" + "\n" + "8.Feedback" + "\n" + "9.Exit");
					int log = sc.nextInt();
					switch (log) {

					case 1:
						try {
							updateUser();
						} catch (UpdateException e) { // custom exception
							System.out.println(e.getMessage());
						}
						break;
					case 2:
						try {
							deleteUser();
						} catch (DeleteException e) { // custom exception
							System.out.println(e.getMessage());
						}
						break;

					case 3:
						try {
							searchBus();
						} catch (BusNotFoundException e) { // custom exception
							System.out.println(e.getMessage());
						}
						break;
					case 4:
						checkAvailability();
						break;
					case 5:
						try {
							bookTicket();
						} catch (TicketBookingException e) { // custom exception
							System.out.println(e.getMessage());
						}
						break;
					case 6:
						getTicket();
						break;

					case 7:
						cancelTicket();
						break;
					case 8:
						giveFeedback();
						break;
					case 9:
						bo = false;
						sc.close();
						System.out.println("*****************************");
						break;
					default:
						System.out.println("Incoorect Option");
						break;
					}

				}
			} else {
				System.out.println("Login unsucessful");
			}

		} else if (option == 2) {
			try {
				createUser();
			} catch (RegisterException e) { // custom exception
				System.out.println(e.getMessage());
			}

		}
	}

	// update user

	private static void updateUser() throws UpdateException {
		User user = new User();
		user.setUserId(userid);
		System.out.println("Enter Password");

		user.setUserPassword(sc.next());
		System.out.println("Enter New Username");
		user.setUserName(sc.next());
		boolean checkEmail = true;
		while (checkEmail) {
			System.out.println("Enter Email:");
			String temp = service.regexemail(sc.next()); // email validation
			if (temp != null) {
				user.setEmail(temp);
				checkEmail = false;
			} else {
				System.out.println("Wrong Email Format!! e.g(example@email.com)");
			}
		}

		boolean checkContact = true;
		while (checkContact) {
			System.out.println("Enter Contact No.:"); // contact validation
			Long temp = service.regexcontact(sc.next());
			if (temp != null) {
				user.setContact(temp);
				checkContact = false;
			} else {
				System.out.println("Contact should be of 10 digits!!");
			}
		}

		boolean b = service.updateUser(user);
		if (b) {
			System.out.println("SuccessFully Updated");
		} else {
			System.out.println("Failed to Update");
			throw new UpdateException("Updation Fail Exception"); // throws custom exception
		}

	}

	// login user

	private static boolean loginUser() throws LoginException {

		boolean checkLogin = true;
		while (checkLogin) {
			System.out.println("Enter userid:");
			Integer tempId = service.regex(sc.next()); // id validation

			if (tempId != null) {
				userid = tempId;
				checkLogin = false;
			} else {
				System.out.println("User id should be number !");

			}
		}
		System.out.println("Enter password:");
		String password = sc.next();
		if (service.loginUser(userid, password) != null) {
			return true;
		} else {
			throw new LoginException("Login Failed Exception"); // throws custom exception
		}

	}

	// delete user

	private static void deleteUser() throws DeleteException {
		System.out.println("Enter Password");
		String password = sc.next();
		if (service.deleteUser(userid, password)) {
			System.out.println("Profile sucessfully Deleted");
		} else {
			throw new DeleteException("User Profile deletion Failed"); // throws custom exception
		}
	}

	// search bus

	private static void searchBus() throws BusNotFoundException {
		boolean busCheck = true;
		Integer busId = 0;
		while (busCheck) {
			System.out.println("Enter BusId"); // id validation

			Integer tempId = service.regex(sc.next());
			if (tempId != null) {
				busId = tempId;
				busCheck = false;
			} else {
				System.out.println("User id should be number !");

			}
		}

		Bus bus = service.searchBus(busId);
		if (bus != null) {
			System.out.println(bus);
		} else {
			throw new BusNotFoundException("Bus Not Found Exception"); // throws custom exception
		}

	}

	// check availability of bus

	private static void checkAvailability() {
		Available available = new Available();
		System.out.println("Enter Source point");
		String source = sc.next();
		System.out.println("Enter Destination point");
		String destination = sc.next();
		System.out.println("Enter Date (YYYY-MM-DD)");
		String tempDate = sc.next();
		Date date = Date.valueOf(tempDate);
		available.setAvailableDate(date);
		List<Bus> list = service.checkAvailability(source, destination, date);

		for (Bus bs : list) {
			System.out.println(bs);
			int avail = service.checkAvailability(bs.getBusId(), date);
			System.out.println("Available Seats:" + avail);
		}

	}

	// book ticket

	private static void bookTicket() throws TicketBookingException {
		Ticket ticket = new Ticket();

		System.out.println("Enter source point");
		String checksource = sc.next();
		System.out.println("Enter Destination point");
		String checkdestination = sc.next();
		System.out.println("Enter date of journey(yyyy-mm-dd)");
		String tempDate = sc.next();
		Date date = Date.valueOf(tempDate);
		ticket.setJourneyDate(date);
		List<Bus> list = service.checkAvailability(checksource, checkdestination, date);

		for (Bus bs : list) {

			System.out.println(bs);
			int avail = service.checkAvailability(bs.getBusId(), date);
			System.out.println("Available Seats:" + avail);
		}

		System.out.println("Enter the bus_id");
		int bus_id = sc.nextInt();
		ticket.setBusId(bus_id);

		ticket.setUserId(userid);

		System.out.println("Enter number of seats to book");
		int noOfSeats = sc.nextInt();
		ticket.setNoofSeats(noOfSeats);
		Bus bus = service.searchBus(bus_id);
		ticket.setTotalPrice(bus.getPrice() * noOfSeats);

		Ticket bookTicket = service.bookTicket(ticket);
		if (bookTicket != null) {
			System.out.println("Ticket sucessfully Booked");
			System.out.println(bookTicket);
		} else {
			throw new TicketBookingException("Ticket Booking Fail Exception"); // throws custom exception
		}
	}

	// get ticket

	private static void getTicket() {
		System.out.println("Enter BookingId");
		Ticket ticket = service.getTicket(sc.nextInt());
		if (ticket != null) {
			System.out.println(ticket);
		} else {
			System.out.println("No Tickets Found");
		}
	}

	// cancel ticket
	private static void cancelTicket() {
		System.out.println("Enter BookingId");
		Boolean cancelTicket = service.cancelTicket(sc.nextInt());
		if (cancelTicket) {
			System.out.println("Ticket Successfully Cancelled");
		} else {
			System.out.println("No Tickets Found");
		}
	}

	// give feedback

	private static void giveFeedback() {
		System.out.println("Enter Your Feedback");
		String feedback = sc.next();
		Boolean sugg = service.giveFeedback(userid, feedback);
		if (sugg) {
			System.out.println("Feedback Successfully Given");
		} else {
			System.out.println("Fail to give Feedback");
		}
	}

	// register user

	private static void createUser() throws RegisterException {
		User user = new User();
		boolean checkLogin = true;
		while (checkLogin) {
			System.out.println("Enter userid:"); // id validation
			Integer tempId = service.regex(sc.next());
			if (tempId != null) {

				userid = tempId;
				user.setUserId(userid);
				checkLogin = false;

			} else {
				throw new RegisterException("RegisterException:Input should be Number");

			}
		}
		System.out.println("Enter Username:");
		user.setUserName(sc.next());
		boolean checkEmail = true; // email validation
		while (checkEmail) {
			System.out.println("Enter Email:");
			String temp = service.regexemail(sc.next());
			if (temp != null) {

				user.setEmail(temp);
				checkEmail = false;
			} else {
				System.out.println("Wrong Email Format!! e.g(example@email.com)");
			}
		}

		boolean checkContact = true; // contact validation
		while (checkContact) {
			System.out.println("Enter Contact No.:");
			Long temp = service.regexcontact(sc.next());
			if (temp != null) {
				user.setContact(temp);
				checkContact = false;
			} else {
				System.out.println("Contact should be of 10 digits!!");
			}
		}
		System.out.println("Enter Password:");
		user.setUserPassword(sc.next());
		sc.close();
		boolean reg = service.createUser(user);
		if (reg) {
			System.out.println("Registration Successful");
		} else {
			throw new RegisterException("Registration Fail Exception"); // throws custom exception
		}

	}
}