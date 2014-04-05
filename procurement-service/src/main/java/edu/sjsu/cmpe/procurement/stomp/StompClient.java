package edu.sjsu.cmpe.procurement.stomp;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.BookOrder;

public class StompClient {

	ProcurementServiceConfiguration configuration = new ProcurementServiceConfiguration();
	String apolloUser = "admin";
	String apolloPassword = "password";
	String apolloHost = "54.215.133.131";
	String apolloPort = "61613";
	//String topicName = configuration.getStompTopicName();
	
	private String isbn;

	String queueName = "/queue/78201.book.orders";
	public static void main(String[] args) {



	}
	

	
	
	
	
	
	

	public Connection createConnection() throws JMSException{
		StompJmsConnectionFactory factory= new StompJmsConnectionFactory();
		//System.out.println("apollo host" + configuration.getApolloHost());
		System.out.println(" Creating connection");
		System.out.println("Host"+apolloHost);
		factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);
		Connection connection = factory.createConnection(apolloUser, apolloPassword);
		
		
		return connection;

	}



	public BookOrder  receiveMessageFromQueue(Connection connection) throws JMSException{

		connection.start();
		System.out.println("Will recieve msg from queue");
		BookOrder requestOrder = new BookOrder();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = new StompJmsDestination(queueName);
		MessageConsumer consumer = session.createConsumer(destination);

		while(true){
			Message message= consumer.receive();
			
			System.out.println("Message from quuee"+ message.toString());

			if( message instanceof  TextMessage ) {
				String body = ((TextMessage) message).getText();
				
				isbn = body.substring(10);
				requestOrder.get_order_book_isbns().add(Integer.parseInt(isbn));

				if( "SHUTDOWN".equals(body)) {
					break;
				}
				System.out.println("Received message = " + body);

			} else if (message instanceof StompJmsMessage) {
				StompJmsMessage smsg = ((StompJmsMessage) message);
				String body = smsg.getFrame().contentAsString();
				if ("SHUTDOWN".equals(body)) {
					break;
				}
				System.out.println("Received message = " + body);

			} else {
				System.out.println("Unexpected message type: "+message.getClass());
			}
		}
		
		
		return requestOrder;

	}

}


