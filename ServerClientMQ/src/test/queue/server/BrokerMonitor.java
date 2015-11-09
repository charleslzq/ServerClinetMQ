package test.queue.server;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.ietools.connect.messageProcessor.FileToParse;
import org.ietools.connect.messageProcessor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerMonitor {
	private static final transient Logger LOG = LoggerFactory.getLogger(BrokerMonitor.class);
	
	private static BrokerMonitor instance;
	
	private BrokerService brokerService;
    private Connection connection;
    private Session session;
    private String bindAddress;
    private MessageProcessor processor;
    private String topicName;
    private String queueName;
    private MessageConsumer consumer;
    private long TTL;
    
    private BrokerMonitor(){
    	connection = null;
    	brokerService = null;
    	session = null;
    	consumer = null;
    }
    
    public static BrokerMonitor getInstance(){
    	if(instance == null)
    		instance = new BrokerMonitor();
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
        processor.init(true);
        processor.setSession(session);
        
        Destination des = session.createQueue(queueName);
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
    
    public void publishTopicMessage(String topicName, Message message, int deliveryMode) throws JMSException{
    	Destination destination = session.createTopic(topicName);
    	MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(deliveryMode);
		producer.setTimeToLive(TTL);
		producer.send(message);
    }
    
    public void sendQueueMessage(String queueName, Message message, int deliveryMode) throws JMSException{
    	Destination destination = session.createQueue(queueName);
    	MessageProducer producer = session.createProducer(destination);
    	producer.setDeliveryMode(deliveryMode);
    	producer.setTimeToLive(TTL);
    	producer.send(message);
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
    
    public void sendFileToParse(ExtractionTask task) throws JMSException{
		ObjectMessage message = session.createObjectMessage();
		
		String taskID = task.getTaskID();
		URI fileURI = task.getFileURI();
		FileToParse file = new FileToParse(taskID, fileURI);
		file.setConfigURIs(task.getConfigURIs());
		
		message.setObject(file);
		
		message.setJMSType("FileToParse");
		message.setBooleanProperty("RequireResponse", false);
		message.setStringProperty("Receiver", task.getExtracterIP());
		
		publishTopicMessage(topicName, message, DeliveryMode.NON_PERSISTENT);
		task.setState(TaskState.TASK_DISPATCHED);
	}
    
    public void askForParse(ExtractionTask task) throws JMSException{
    	MapMessage message = session.createMapMessage();
    	
    	String taskID = task.getTaskID();
    	message.setString("Task ID", taskID);
    	
    	message.setJMSType("AskForParse");
		
		publishTopicMessage(topicName, message, DeliveryMode.NON_PERSISTENT);
    }
    
    public static void main(String argv[]){
    	BrokerMonitor monitor = BrokerMonitor.getInstance();
    	ExtractionTaskMonitor taskMonitor = ExtractionTaskMonitor.getInstance();
    	
		try {
			LOG.info("Local host ip:\t"+InetAddress.getLocalHost().getHostAddress());
			monitor.connectToBroker(ActiveMQConnection.DEFAULT_USER, 
					ActiveMQConnection.DEFAULT_PASSWORD);
			/*TextMessage text = monitor.session.createTextMessage();
			text.setText("Hello!");
			text.setJMSType("test");
			text.setBooleanProperty("RequireResponse", true);
			text.setStringProperty("NameToSend", "FeedBack");
			text.setBooleanProperty("Topic", false);
			monitor.publishTopicMessage("FileToParse", text, DeliveryMode.NON_PERSISTENT);
			*/
			FileChooser chooser = new FileChooser(new PDFFileFilter());
			List<File> files = chooser.chooserFileAndDir();
			for(File file:files){
				taskMonitor.addNewFile(URI.create(file.getName()), new HashSet<URI>());
			}
			taskMonitor.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("Fail to initialize broker...");
		}
    	
    }
    
   
}
