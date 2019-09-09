# Example Settings4j

An example how secured-properties can be used with [Spring-Boot](https://spring.io/).

<!-- MACRO{toc} -->

## Description

Spring-Boot has a nice clear definition [how an application should be configured](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

typically configurations are place in `file:./application.properties` or `file:./config/application.properties`

Limitation: Property-files in classpath cannot be modified, and YAML variants are not supported to auto-encrypt values by secured-properties for now. 
  
## Implementation Pattern

It is recommended to create a simple Helper class to initial encrypt values in the propoerty files and decrypt the values on demand where you need it.

### Typically spring-boot config

In Spring-Boot you can simpley [annotate a simple POJO](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/configuration-metadata.html#configuration-metadata-annotation-processor) with `@ConfigurationProperties` for application-configurations

Like the following Example:

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/SpringBootTestProperties.java} -->

### Secured Properties Helper

The Helper contains your secured-properties configuration and can be used to initial encrypt unencrypted values in the propoerty files and decrypt the values on demand where you need it.

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/SpringBootSecuredPropertiesHelper.java} -->

### The Spring-Boot Starter Application

A spring-Boot application has a starter-class where you can directly decrypt values in the property-files if needed before it is used by spring.

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/SpringBootStarterApplication.java} -->

### Some Service-Class

Now you can decrypt the values of your Properties class whereever you need it.

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/SpringBootTestService.java} -->
