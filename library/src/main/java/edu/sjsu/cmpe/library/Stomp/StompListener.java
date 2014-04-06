package edu.sjsu.cmpe.library.Stomp;

import java.net.MalformedURLException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.fusesource.stomp.jms.StompJmsDestination;

public class StompListener {
	
	public void listen() throws JMSException, MalformedURLException{
		
		StompClient stomp = new StompClient();
		Connection connection = stomp.createConnection();
		stomp.subscribeToTopic(connection);
		
		
		
	}
	

}
