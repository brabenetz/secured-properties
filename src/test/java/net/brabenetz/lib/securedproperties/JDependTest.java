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
package net.brabenetz.lib.securedproperties;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import net.brabenetz.lib.securedproperties.config.ConfigInitializer;
import net.brabenetz.lib.securedproperties.core.Encryption;
import net.brabenetz.lib.securedproperties.utils.SecuredPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

public class JDependTest {

    @Test
    public void testToPreventCycleDependencies() throws Exception {
        JDepend jdepend = new JDepend(PackageFilter.all().including(
            SecuredProperties.class.getPackage().getName())
            .excludingRest());
        jdepend.addDirectory("./target/classes");
        jdepend.analyze();
        Assert.assertFalse(jdepend.containsCycles());

        JavaPackage pkgRoot = jdepend.getPackage(SecuredProperties.class.getPackage().getName());
        JavaPackage pkgConfig = jdepend.getPackage(ConfigInitializer.class.getPackage().getName());
        JavaPackage pkgCore = jdepend.getPackage(Encryption.class.getPackage().getName());
        JavaPackage pkgUtil = jdepend.getPackage(SecuredPropertiesUtils.class.getPackage().getName());

        // pkg uses other Pkg
        assertThat(pkgRoot.getEfferents(), containsInAnyOrder(pkgConfig, pkgUtil, pkgCore));
        assertThat(pkgConfig.getEfferents(), containsInAnyOrder(pkgUtil, pkgCore));
        assertThat(pkgUtil.getEfferents(), is(empty()));
        assertThat(pkgCore.getEfferents(), is(empty()));
        // pkg is used in other Pkg
        assertThat(pkgRoot.getAfferents(), is(empty()));
        assertThat(pkgConfig.getAfferents(), containsInAnyOrder(pkgRoot));
        assertThat(pkgUtil.getAfferents(), containsInAnyOrder(pkgRoot, pkgConfig));
        assertThat(pkgCore.getAfferents(), containsInAnyOrder(pkgRoot, pkgConfig));
    }

    @Test
    public void testDependenciesOfPackages() throws Exception {

        String pkgBase = SecuredProperties.class.getPackage().getName();
        Map<String, JavaPackage> rootPkgs = analyseDirectChildes(pkgBase);
        assertNoCycleBetweenSiblings(rootPkgs);

        assertThat(rootPkgs.keySet(), containsInAnyOrder("config", "core", "utils"));
        assertThat(rootPkgs.get("config").getEfferents(), containsInAnyOrder(rootPkgs.get("core"), rootPkgs.get("utils")));
        assertThat(rootPkgs.get("core").getEfferents(), is(empty()));
        assertThat(rootPkgs.get("utils").getEfferents(), is(empty()));

    }

    private void assertNoCycleBetweenSiblings(final Map<String, JavaPackage> rootPkgs) {
        List<String> message = new ArrayList<>();
        for (JavaPackage javaPackage : rootPkgs.values()) {
            ArrayList<JavaPackage> cycles = new ArrayList<>();
            boolean containsCycle = javaPackage.collectCycle(cycles);
            if (containsCycle) {
                message.add(String.format("Package '%s' has cycles with siblings: %s", javaPackage.getName(), cycles));
            }
        }
        Assert.assertTrue(message.stream().collect(Collectors.joining("\n")), message.isEmpty());
    }

    @SuppressWarnings("unused")
    private JavaPackage[] toJavaPackage(final String parentPackage, final String... subPackages) {
        return Arrays.stream(subPackages)
            .map((subPackage) -> new JavaPackage(parentPackage + "." + subPackage))
            .collect(Collectors.toList()).toArray(new JavaPackage[subPackages.length]);
    }

    private Map<String, JavaPackage> analyseDirectChildes(final String pkgRoot) throws Exception {

        JDepend jdepend = new JDepend(PackageFilter.all().including(pkgRoot).excludingRest());
        jdepend.addDirectory("./target/classes");
        jdepend.analyze();

        Map<String, JavaPackage> rootPkgs = getDirectChildPackages(jdepend, pkgRoot);

        fillDirectChildDependencies(jdepend, rootPkgs);

        return rootPkgs;
    }

    private void fillDirectChildDependencies(final JDepend jdepend, final Map<String, JavaPackage> rootPkgs) {
        if (rootPkgs.isEmpty()) {
            // nothing to fill
            return;
        }

        String pkgRoot = StringUtils.substringBeforeLast(rootPkgs.values().iterator().next().getName(), ".");

        for (JavaPackage javaPackage : jdepend.getPackages()) {
            String subPackage = getSubPackageName(pkgRoot, javaPackage);
            if (StringUtils.isEmpty(subPackage)) {
                continue;
            }
            JavaPackage javaSubPackage = rootPkgs.get(subPackage);

            javaPackage.getEfferents()
            .stream()
            .map((pkg) -> getSubPackageName(pkgRoot, pkg))
            .filter(StringUtils::isNotEmpty)
            .map(rootPkgs::get)
            .forEach(javaSubPackage::addEfferent);

            javaPackage.getAfferents()
            .stream()
            .map((pkg) -> getSubPackageName(pkgRoot, pkg))
            .filter(StringUtils::isNotEmpty)
            .map(rootPkgs::get)
            .forEach(javaSubPackage::addAfferent);

            javaPackage.getClasses().forEach(javaSubPackage::addClass);

        }
    }

    private Map<String, JavaPackage> getDirectChildPackages(final JDepend jdepend, final String pkgRoot) {
        Map<String, JavaPackage> rootPkgs = new HashMap<>();
        for (JavaPackage javaPackage : jdepend.getPackages()) {
            String subPackage = getSubPackageName(pkgRoot, javaPackage);
            if (StringUtils.isEmpty(subPackage)) {
                continue;
            }
            JavaPackage javaSubPackage = rootPkgs.get(subPackage);
            if (javaSubPackage == null) {
                javaSubPackage = new JavaPackage(pkgRoot + "." + subPackage);
                rootPkgs.put(subPackage, javaSubPackage);
            }

        }
        return rootPkgs;
    }

    private String getSubPackageName(final String pkgRoot, final JavaPackage javaPackage) {
        String subPackage = StringUtils.substringAfter(javaPackage.getName(), pkgRoot + ".");
        subPackage = StringUtils.substringBefore(subPackage, ".");
        return subPackage;
    }

}
