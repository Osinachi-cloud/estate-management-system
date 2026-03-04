package com.cymark.estatemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.nio.ByteBuffer;
import java.util.Base64;

@SpringBootApplication
//@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = "com.cymark.estatemanagementsystem.repository")
@EnableJpaRepositories(
        entityManagerFactoryRef = "realEntityManager",
        basePackages = {"com.cymark.estatemanagementsystem.repository"},
        transactionManagerRef = "transactionManager",
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class
)
//@EntityScan(basePackages = {
//        "com.cymark.estatemanagementsystem.model.entity"
//})
public class EstateManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstateManagementSystemApplication.class, args);
    }
}
