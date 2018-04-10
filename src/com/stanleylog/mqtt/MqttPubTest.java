/**
 * 
 */
package com.stanleylog.mqtt;

import com.ibm.micro.client.mqttv3.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Zhiguang Sun
 * 
 */
public class MqttPubTest {

	private static MqttClient _client;
	private static MqttTopic _topic;
	private static MqttConnectOptions _opt;


	public static void doTest() {
		// TODO Auto-generated method stub

		try {
			_client = new MqttClient("tcp://192.168.56.103:1883", "pub");
			_topic = _client.getTopic("HXB/test");
			_opt = new MqttConnectOptions();
			_opt.setCleanSession(true);
			_opt.setUserName("pub");
			_opt.setPassword("pub".toCharArray());
			_client.connect(_opt);

			for (int i = 0; i <= 100; i++) {
//				System.out.println(i + ": " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				MqttMessage msg = new MqttMessage((i + ": " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).getBytes());
				msg.setQos(1);
				_topic.publish(msg);

			}

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				_client.disconnect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


	}


	public static void main(String[] args) {
		MqttPubTest m = new MqttPubTest();
		m.doTest();
	}
}
