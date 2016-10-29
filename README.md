

[![Build Status](https://secure.travis-ci.org/brabenetz/secured-properties.png?branch=master)](http://travis-ci.org/brabenetz/secured-properties)
[![Coverage Status](https://coveralls.io/repos/brabenetz/secured-properties/badge.svg?branch=code-quality)](https://coveralls.io/github/brabenetz/secured-properties?branch=code-quality)
[![Coverity](https://scan.coverity.com/projects/10666/badge.svg)](https://scan.coverity.com/projects/brabenetz-secured-properties)
[![Maven site](https://img.shields.io/badge/Maven-site-blue.svg)](http://secured-properties.brabenetz.net/archiv/latest/)
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache_2.0-brightgreen.svg)](https://github.com/brabenetz/secured-properties/blob/master/LICENSE.txt)
<!--
# Costs extra for more than one project:
[![Dependency Status](https://www.versioneye.com/user/projects/1234/badge.svg?style=flat)](https://www.versioneye.com/user/projects/1234)
# Not now:
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.brabenetz.lib/secured-properties/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.brabenetz.lib/secured-properties)
[![Javadocs](http://www.javadoc.io/badge/net.brabenetz.lib/secured-properties.svg)](http://www.javadoc.io/doc/net.brabenetz.lib/secured-properties)
-->


# Secured Properties

Encrypt and Decrypt secret values (e.g. passwords) in properties files

<!-- MACRO{toc} -->

## Basic Idea

The password in a property file should be encrypted by a secret key, stored somewhere save.

This secret file could be stored in:

  * The user home folder (at least obfuscating is better then plain-text)
  * A virtual mounted encrypted Drive. e.g.: [Veracrypt](https://veracrypt.codeplex.com)
  * A hardware encrypted Drive. e.g.: [Corsair Padloc](https://amzn.com/B003SHMKHS)

**"Secured Properties" can only be as save as the location of the secret key.**

## Usage

The Property file "myConfiguration.properties":

```INI
mySecretPassword = test
```

The Java code:

```Java
  String secretValue = SecuredProperties.getSecretValue(
      new SecuredPropertiesConfig().withSecretFile(new File("G:/mysecret.key")), // custom configurations
      new File("myConfiguration.properties"), // The Property File
      "mySecretPassword"); // the property-key from "myConfiguration.properties" 
```

will return "test" as secretValue and automatically encrypt the value in the property file.

After the first run the Property file will looks similar to the following: 

```INI
mySecretPassword = {wVtvW8lQrwCf8MA9sadwww==}
```

This encrypted password can now be read only in combination with the secret file "G:/mysecret.key" 


## Default Configurations

*new SecuredPropertiesConfig()* is a valid Configuration with following default behaviors:

  * **secretFile** default location: "%system_home%/.secret/securedProperties.key"
  * **autoCreateSecretKey** If the secret key doesn't exists, it will be created automatically
  * **allowedAlgorithm** AES-256,  AES-192,  AES-128, DESede-168, DESede-128: The first algorithm supported by the java-VM will be used to create the initial secret key.
  * **autoEncryptNonEncryptedValues** If the value in the property file is not already encrypted. it will be replaced by the encrypted value.

All this configurations can be customized by the *SecuredPropertiesConfig.java*.

See: http://secured-properties.brabenetz.net/archiv/latest/configuration.html

## More Details

  * Maven Site: http://secured-properties.brabenetz.net/archiv/latest/index.html
  * Dependency-Information: http://secured-properties.brabenetz.net/archiv/latest/download.html
  * Custom Configuration: http://secured-properties.brabenetz.net/archiv/latest/configuration.html


