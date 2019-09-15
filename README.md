

[![Build Status](https://secure.travis-ci.org/brabenetz/secured-properties.png?branch=master)](http://travis-ci.org/brabenetz/secured-properties)
[![Coverage Status](https://coveralls.io/repos/brabenetz/secured-properties/badge.svg?branch=code-quality)](https://coveralls.io/github/brabenetz/secured-properties?branch=code-quality)
[![Coverity](https://scan.coverity.com/projects/10666/badge.svg)](https://scan.coverity.com/projects/brabenetz-secured-properties)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f5c89c8143444b6ae39dddb1f329b8f)](https://www.codacy.com/app/brabenetz/secured-properties?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=brabenetz/secured-properties&amp;utm_campaign=Badge_Grade)
[![Maven site](https://img.shields.io/badge/Maven-site-blue.svg)](http://secured-properties.brabenetz.net/archiv/latest/)
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache_2.0-brightgreen.svg)](https://github.com/brabenetz/secured-properties/blob/master/LICENSE.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.brabenetz.lib/secured-properties/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.brabenetz.lib/secured-properties)
[![Javadocs](http://www.javadoc.io/badge/net.brabenetz.lib/secured-properties.svg)](http://www.javadoc.io/doc/net.brabenetz.lib/secured-properties)
<!--
# Costs extra for more than one project:
[![Dependency Status](https://www.versioneye.com/user/projects/1234/badge.svg?style=flat)](https://www.versioneye.com/user/projects/1234)
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
 // prepare custom config
 final SecuredPropertiesConfig config = new SecuredPropertiesConfig()
        .withSecretFile(new File("G:/mysecret.key"))
        .initDefault();

 // auto-encrypt values in the property-file:
 SecuredProperties.encryptNonEncryptedValues(config,
         new File("myConfiguration.properties"), // The Property File
         "mySecretPassword"); // the property-key from "myConfiguration.properties"

 // read encrypted values from the property-file
 String secretValue = SecuredProperties.getSecretValue(config,
         new File("myConfiguration.properties"), // The Property File
         "mySecretPassword"); // the property-key from "myConfiguration.properties"
```

will return "test" as secretValue and automatically encrypt the value in the property file.

After the first run the Property file will looks similar to the following: 

```INI
mySecretPassword = {wVtvW8lQrwCf8MA9sadwww==}
```

This encrypted password can now be read only in combination with the secret file "G:/mysecret.key" 

## Get multiple values at ones

It is also possible to encrypt multiple values at ones:

```Java
  // custom configurations
  final SecuredPropertiesConfig config = new SecuredPropertiesConfig()
        .withSecretFile(new File("G:/mysecret.key"))
        .initDefault();

  Map secretValues = SecuredProperties.getSecretValues(config
      new File("myConfiguration.properties"), // The Property File
      "mySecretPassword", "anotherSecretPassword"); // the property-keys in "myConfiguration.properties" 
```

The returned Map contains the decrypted passwords for the two keys "mySecretPassword", "anotherSecretPassword".

## Manual Encryption/Decryption

In some cases you don't want encrypt/decrypt values from Properties Files.

This example shows how values from System Properties are encrypted/decrypted:

```Java
    String systemPropPassword = System.getProperty(key);
    if (SecuredProperties.isEncryptedPassword(systemPropPassword)) {
        return SecuredProperties.decrypt(config, systemPropPassword);
    } else if (StringUtils.isNotEmpty(systemPropPassword)) {
        System.out.println(String.format("you could now use the following encrypted password: -D%s=%s", key,
            SecuredProperties.encrypt(config, systemPropPassword)));
        return systemPropPassword;
    } else {
        return null;
    }
```

## Default Configurations

*new SecuredPropertiesConfig()* is a valid Configuration with following default behaviors:

  * **secretFile** default location: "%user_home%/.secret/securedProperties.key"
  * **autoCreateSecretKey** If the secret key doesn't exists, it will be created automatically
  * **allowedAlgorithm** AES-256,  AES-192,  AES-128, DESede-168, DESede-128: The first algorithm supported by the java-VM will be used to create the initial secret key.

All this configurations can be customized by the *SecuredPropertiesConfig.java*.

See: http://secured-properties.brabenetz.net/archiv/latest/configuration.html

## More Details

  * Maven Site: http://secured-properties.brabenetz.net/archiv/latest/index.html
  * Dependency-Information: http://secured-properties.brabenetz.net/archiv/latest/download.html
  * Custom Configuration: http://secured-properties.brabenetz.net/archiv/latest/configuration.html
  * Example with Spring Boot: http://secured-properties.brabenetz.net/archiv/latest/exampleSpringBoot.html
  * Example with CommonsConfiguration: http://secured-properties.brabenetz.net/archiv/latest/exampleCommonsConfiguration.html
  * Example with Settings4j: http://secured-properties.brabenetz.net/archiv/latest/exampleSettings4j.html


