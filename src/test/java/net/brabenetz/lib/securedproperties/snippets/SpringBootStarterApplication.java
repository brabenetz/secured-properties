/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 - 2019 Brabenetz Harald, Austria
 * ===============================================================
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
 * #L%
 */
package net.brabenetz.lib.securedproperties.snippets;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

//START SNIPPET: configExample
@SpringBootApplication
public class SpringBootStarterApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootStarterApplication.class)
                .bannerMode(Mode.OFF) // banner already shown by the inner context -> no need to show it again.
                .parent(new SpringApplicationBuilder(AppPrepare.class) // the inner-Context runs first
                        // .banner(new YourAppBanner()) // replace SpringBoot Ascii-Art with your own (but don't tell Josh Long).
                        .run(args))
                .run(args);
    }

    /**
     * Run the initialize in its own Spring-Boot-Context is needed to use the Spring-Boot backlog Logging config.
     */
    public static class AppPrepare implements InitializingBean {
        @Override
        public void afterPropertiesSet() {
            // this will encrypt the given properties if there are not already encrypted:
            SpringBootSecuredPropertiesHelper.encryptProperties(
                    "your.app.props.my-secret-password",
                    "your.app.props.another-secret-password");
        }
    }
}
//END SNIPPET: configExample
