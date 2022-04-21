/*
 * Copyright 2016-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.djimgou.core.cooldto.testing.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Dany Djimgou
 */
/*
@ComponentScan(basePackages = {"com.act","com.djimgou.com.sepla.security","com.djimgou.com.sepla.security.repo","com.djimgou.com.sepla"})*/
/*"com.djimgou.security","com.djimgou.filestorage","com.djimgou.reporting","com.djimgou.com.sepla"*/
@EntityScan(basePackages = {"com.djimgou.core.util","com.djimgou.core.test", "com.djimgou.core.cooldto"})
@EnableJpaRepositories(basePackages = {"com.djimgou.core.util","com.djimgou.core.test", "com.djimgou.core.cooldto"})
@SpringBootApplication(scanBasePackages = {"com.djimgou.core.util","com.djimgou.core.test", "com.djimgou.core.cooldto"}
/*exclude = { SecurityAutoConfiguration.class }*/)
// https://stackoverflow.com/questions/51751772/spring-session-table-name-property-does-not-change-the-table-name
// https://stackoverflow.com/questions/39211369/caused-by-com-mysql-jdbc-exceptions-jdbc4-mysqlsyntaxerrorexception-table-tes
// @EnableJdbcHttpSession  !! Danger ne pas mettre cette anotation, sinon les tables de session ne vont pas se créer
public class CoreCoolDtoTestApplication {


}
