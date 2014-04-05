package edu.sjsu.cmpe.library.Stomp;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class StompClient {

	private String apolloUser;
	private String apolloPassword;
	private String apolloHost;
	private String apolloPort;
	private String libraryName;
	private String queueName;
	private String topicName;
	private BookRepositoryInterface bookRepository;



	public StompClient(String apolloUser, String apolloPassword,
			String apolloHost, String apolloPort, String libraryName, String queueName, String topicName, BookRepositoryInterface bookRepository) {
		this.apolloUser = apolloUser;
		this.apolloPassword = apolloPassword;
		this.apolloHost = apolloHost;
		this.apolloPort = apolloPort;
		this.libraryName = libraryName;
		this.queueName = queueName;
		this.topicName = topicName;
		this.bookRepository= bookRepository;
	}

	public Connection createConnection() throws JMSException{
		StompJmsConnectionFactory factory= new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);
		Connection connection = factory.createConnection(apolloUser, apolloPassword);
		return connection;

	}


	public void sendMessageToQueue(Connection connection , long isbn) throws JMSException{
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = new StompJmsDestination(queueName);
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		String msgToQueue = libraryName + ":" + isbn;
		TextMessage message = session.createTextMessage(msgToQueue);
		message.setLongProperty("id", System.currentTimeMillis());

		producer.send(message);
		producer.send(session.createTextMessage("SHUTDOWN"));


	}

	public void  closeConnection(Connection connection) throws JMSException {
		connection.close();

	}

	public void reveiveQueueMessage(Connection connection) throws JMSException {
		connection.start();
		// bookReuqest = new BookRequest();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(queueName);
		MessageConsumer consumer = session.createConsumer(dest);

		while(true) {

			/**Wait for message for 5 sec*/
			Message msg = consumer.receive(5000);
			if( msg instanceof  TextMessage ) {
				String body = ((TextMessage) msg).getText();
				System.out.println("Received message = " + body);


			}
		}
	}
}