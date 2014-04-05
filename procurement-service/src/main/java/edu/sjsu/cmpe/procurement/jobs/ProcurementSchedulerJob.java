package edu.sjsu.cmpe.procurement.jobs;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.domain.BookOrder;
import edu.sjsu.cmpe.procurement.stomp.StompClient;

/**
 * This job will run at every 5 second.
 */
@Every("50s")
public class ProcurementSchedulerJob extends Job {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void doJob() {
		/*String strResponse = ProcurementService.jerseyClient.resource(
		"http://ip.jsontest.com/").get(String.class);
	log.debug("Response from jsontest.com: {}", strResponse);*/

		System.out.println("Starting Job 1");

		BookOrder bookRequest ;
		try {
			StompClient stompQueue = new StompClient();
			Connection connection = stompQueue.createConnection();

			bookRequest = stompQueue.receiveMessageFromQueue(connection);

			if(bookRequest.get_order_book_isbns().size()!=0){

				System.out.println("Post to Publisher");
				Client client = Client.create();

				String url = "http://54.215.133.131:9000/orders";

				WebResource webResource = client.resource(url);
				ClientResponse response = webResource.accept("application/json").type("application/json").entity(bookRequest).post(ClientResponse.class);
				System.out.println(response.getEntity(String.class));
			}


		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch ( Exception e) {
			e.printStackTrace();
		}  





	}
}
