package test.queue.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.ietools.connect.messageProcessor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.queue.server.BrokerMonitor;

public class ClientMonitor {
	private static final transient Logger LOG = LoggerFactory.getLogger(BrokerMonitor.class);
	
	private static ClientMonitor instance;
	
    private Connection connection;
    private Session session;
    private String bindAddress;
    private MessageProcessor processor;
    private String topicName;
    private String queueName;
    private MessageConsumer consumer;
    private long TTL;
    
    private ClientMonitor(){
    	connection = null;
    	session = null;
    	consumer = null;
    }
    
    public static ClientMonitor getInstance(){
    	if(instance == null)
    		instance = new ClientMonitor();
    	return instance;
    }
    
    public void connectToBroker(String user, String password) throws Exception{
    	Properties properties = new Properties();
		FileInputStream fis = new FileInputStream("resources/config.xml");
		properties.loadFromXML(fis);
		
		topicName = properties.getProperty("TopicName");
		queueName = properties.getProperty("QueueName");
		TTL = Long.parseLong(properties.getProperty("TTL"));
		String ip = properties.getProperty("BrokerAddress");
		String port = properties.getProperty("BrokerPort");
		bindAddress = "tcp://"+ip+":"+port;
       
		ConnectionFactory factory = 
				new ActiveMQConnectionFactory(user, 
						password, 
						bindAddress);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
       
        processor = MessageProcessor.getInstance();
        processor.init(false);
        processor.setSession(session);
        
        Destination des = session.createTopic(topicName);
        consumer = session.createConsumer(des);
        consumer.setMessageListener(new MessageListener(){

			public void onMessage(Message msg) {
				// TODO Auto-generated method stub
				try {
					MessageProcessor.getInstance().process(msg);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		
    	});
        
        LOG.info("Successfully Connect To Broker "+bindAddress);
    }
	
    public void exit(){
		try {
			if(consumer!=null){
				consumer.close();
				LOG.info("Consumer closed");
			}
			if(session!=null){
				session.close();
				LOG.info("Session closed");
			}
			if(connection!=null){
	    		connection.close();
	    		LOG.info("Connection closed");
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
	public static void main(String argv[]){
		ClientMonitor monitor = ClientMonitor.getInstance();
		try {
			LOG.info("Local host ip:\t"+InetAddress.getLocalHost().getHostAddress());
			monitor.connectToBroker(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD);
			
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			stdin.readLine();
			monitor.exit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("Fail to initialize broker...");
		}
	}
}
