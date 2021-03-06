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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//START SNIPPET: configExample
@Component
@ConfigurationProperties(prefix = "your.app.props")
public class SpringBootTestProperties {
    private String anotherSecretPassword;
    private String mySecretPassword;

    public String getAnotherSecretPassword() {
        return anotherSecretPassword;
        // END SNIPPET: configExample
    }

    public void setAnotherSecretPassword(String anotherSecretPassword) {
        this.anotherSecretPassword = anotherSecretPassword;
    }

    public String getMySecretPassword() {
        return mySecretPassword;
    }

    public void setMySecretPassword(String mySecretPassword) {
        this.mySecretPassword = mySecretPassword;
    }

}
