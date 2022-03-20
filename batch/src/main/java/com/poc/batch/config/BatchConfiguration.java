package com.poc.batch.config;

import com.poc.batch.domain.User;
import com.poc.batch.listener.JobCompletionNotificationListener;
import com.poc.batch.model.UserDetail;
import com.poc.batch.processor.UserProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    @Qualifier("userJob")
    public Job updateUserJob()
            throws Exception {

        return this.jobBuilderFactory.get("userJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobCompletionNotificationListener())
                .start(start(reader(), writer(mongoTemplate)))
                .build();
    }

    public Step start(FlatFileItemReader<UserDetail> itemReader, MongoItemWriter<User> itemWriter)
            throws Exception {

        return this.stepBuilderFactory.get("step1").<UserDetail, User>chunk(5).reader(itemReader)
                .processor(new UserProcessor()).writer(itemWriter).build();
    }


    public FlatFileItemReader<UserDetail> reader() {
        return new FlatFileItemReaderBuilder<UserDetail>().name("userItemReader")
                .resource(new ClassPathResource("user-sample-data.csv")).delimited()
                .names("email", "firstName", "lastName", "mobileNumber")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<UserDetail>() {
                    {
                        setTargetType(UserDetail.class);
                    }
                }).build();
    }

    public MongoItemWriter<User> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<User>().template(mongoTemplate).collection("user")
                .build();
    }
}
