/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 Brabenetz Harald, Austria
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
package net.brabenetz.lib.securedproperties.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValueReplacementUtils {

    public static String replaceValue(final String line, final String key, final String newValue) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(key) + "(\\s*=\\s*).*$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String gr1 = matcher.group(1);
            return key + gr1 + newValue;
        }
        return line;
    }
}
