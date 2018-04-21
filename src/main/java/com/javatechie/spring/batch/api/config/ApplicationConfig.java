package com.javatechie.spring.batch.api.config;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.javatechie.spring.batch.api.data.Person;
import com.javatechie.spring.batch.api.util.MailUtil;

@Configuration

public class ApplicationConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private MongoTemplate template;

	@Autowired
	private MailUtil util;

	@SuppressWarnings("serial")
	@Bean
	public MongoItemReader<Person> reader() {
		MongoItemReader<Person> reader = new MongoItemReader<Person>();
		reader.setTemplate(template);
		reader.setQuery("{}");
		reader.setTargetType(Person.class);
		reader.setSort(new HashMap<String, Sort.Direction>() {
			{
				put("_custId", Direction.DESC);
			}
		});
		return reader;
	}

	@Bean
	public StaxEventItemWriter<Person> writter() {
		StaxEventItemWriter<Person> writter = new StaxEventItemWriter<Person>();
		writter.setRootTagName("Persons");
		writter.setResource(new FileSystemResource("xml/bank.xml"));
		writter.setMarshaller(marshaller());
		return writter;
	}

	private XStreamMarshaller marshaller() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> map = new HashMap<>();
		map.put("Person", Person.class);
		marshaller.setAliases(map);
		return marshaller;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Person, Person>chunk(10).reader(reader()).processor(process())
				.writer(writter()).build();
	}

	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("report generation").flow(step1()).end().build();
	}

	public ItemProcessor<Person, Person> process() {

		ItemProcessor<Person, Person> process = new ItemProcessor<Person, Person>() {

			@Override
			public Person process(Person person) throws Exception {
				if (person.getStatus().equalsIgnoreCase("Pending")) {
					String msg = util.sendEmail(person.getEmail(), buildMessage(person));
					System.out.println(msg);
					return person;
				}
				return null;
			}

			private String buildMessage(Person person) {
				String mailBody = "Dear " + person.getName() + "," + "\n" + "statement of your credit card ending with "
						+ person.hashCode() + "**" + " has been generated" + "\n" + "dues amount :"
						+ person.getOutstandingAmount() + "\n" + "last payment date : "
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a").format(person.getLastDueDate()) + "\n" + "\n"
						+ "If you already paid please ignore this email" + "\n" + "Thanks for using our credit card ";
				return mailBody;
			}

		};
		return process;

	}

}
