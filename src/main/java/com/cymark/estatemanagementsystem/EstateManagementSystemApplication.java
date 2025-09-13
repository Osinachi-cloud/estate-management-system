package com.cymark.estatemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "realEntityManager",
//        basePackages = {"com.cymark.estatemanagementsystem.repository"},
//        transactionManagerRef = "transactionManager"
//)
//@EntityScan(basePackages = {
//        "com.cymark.estatemanagementsystem.model.entity"
//})
public class EstateManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstateManagementSystemApplication.class, args);
    }

}
