package com.action.reactive;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.kafka.model.CustomObject;

@EnableEurekaClient
@SpringBootApplication
public class LabClientApplication {
	static Map<String, String> topicMap = Stream.of(new Object[][] { { "Singapore", "SINGAPORE" },
			{ "Cyprus", "CYPRUS" }, { "Hong Kong", "HONG_KONG" }, { "Portugal", "PORTUGAL" }, { "Iceland", "ICELAND" },
			{ "Malta", "MALTA" }, { "Greece", "GREECE" }, { "Saudi Arabia", "SAUDI_ARABIA" },
			{ "Netherlands", "NETHERLANDS" }, { "Sweden", "SWEDEN" }, { "Austria", "AUSTRIA" }, { "Poland", "POLAND" },
			{ "Brazil", "BRAZIL" }, { "France", "FRANCE" }, { "Lithuania", "LITHUANIA" }, { "RSA", "RSA" },
			{ "USA", "USA" }, { "Japan", "JAPAN" }, { "Channel Islands", "CHANNEL_ISLANDS" },
			{ "European Community", "EUROPEAN_COMMUNITY" }, { "United Kingdom", "UNITED_KINGDOM" },
			{ "United Arab Emirates", "UNITED_ARAB_EMIRATES" }, { "Unspecified", "UNSPECIFIED" },
			{ "Switzerland", "SWITZERLAND" }, { "Bahrain", "BAHRAIN" }, { "Spain", "SPAIN" }, { "Lebanon", "LEBANON" },
			{ "Canada", "CANADA" }, { "Czech Republic", "CZECH_REPUBLIC" }, { "Belgium", "BELGIUM" },
			{ "Norway", "NORWAY" }, { "EIRE", "EIRE" }, { "Finland", "FINLAND" }, { "Denmark", "DENMARK" },
			{ "Italy", "ITALY" }, { "Israel", "ISRAEL" }, { "Australia", "AUSTRALIA" }, { "Germany", "GERMANY" },

	}).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
	static int port = 8080;

	public static void main1(String[] args) {
		System.out.println(topicMap);
		final String[] args1 = new String[2];
		topicMap.forEach((k, v) -> {
			Thread thread = new Thread(() -> {
				args1[0] = "--server.port=" + getNextPort();
				args1[1] = "--spring.application.name= microservices-child-" + v + " " + args1[0];
				SpringApplication.run(LabClientApplication.class, args);
			});
			thread.setDaemon(false);
			thread.start();
		});

	}

	private static int getNextPort() {
		// TODO Auto-generated method stub
		return port++;
	}

	public static void main(String[] args) {
		SpringApplication.run(LabClientApplication.class, args);

	}

}

@Service
class Producer {
	private static final Logger logger = LoggerFactory.getLogger(Producer.class);

	@Autowired
	private KafkaTemplate<Long, CustomObject> kafkaTemplate;

	public void sendMessage(String TOPIC,CustomObject customObject) {
		this.kafkaTemplate.send(TOPIC, customObject);
	}
}

@RestController
@RequestMapping(value = "/kafka")
class KafkaController {
	private final Producer producer;

	@Autowired
	public KafkaController(Producer producer) {
		this.producer = producer;
	}

	@PostMapping(value = "/publish",consumes = "application/json")
	public void sendMessageToKafkaTopic(@RequestBody CustomObject customObject, @RequestParam("topic") String topic) {
		this.producer.sendMessage(topic, customObject);
	}

}

@Service
class Consumer {
	private final Logger logger = LoggerFactory.getLogger(Consumer.class);

	@KafkaListener(id = "0",topics = "UNITED_KINGDOM", groupId = "group_id")
	public void consume(CustomObject customObject) {

		logger.info(String.format("$$ -> Consumed Message -> %s", customObject));

	}

}
