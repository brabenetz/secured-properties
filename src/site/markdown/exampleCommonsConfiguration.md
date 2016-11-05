# Example Commons Configuration

An example how secured-properties can be used with [Commons Configuration](http://commons.apache.org/proper/commons-configuration/).

<!-- MACRO{toc} -->

## Description

The following steps are required to initialize Commons Configuration:

  * First get the decrypted password.
  * Store the decrypted password into SystemProperties under the same key.
  * create a SystemConfiguration
  * create a PropertiesConfiguration
  * create a CompositeConfiguration with the SystemConfiguration and the PropertiesConfiguration
  * FINSHED: use the CompositeConfiguration as usual (e.g. per dependency injection).

In this example, the SystemProperties is only a working example to provide the decrypted password.
Better would be to use a custom in-memory implementations of the Configuration.  
  
## The Property File

The example property-file "TestProperties-Valid.properties":

<!-- MACRO{snippet|id=configExample|file=src/test/data/TestProperties-Valid.properties} -->

## The Java Code

The java code example:

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/CommonsConfigurationExampleTest.java} -->

The complete code is in [Settings4jExampleTest.java](./xref-test/net/brabenetz/lib/securedproperties/snippets/CommonsConfigurationExampleTest.html)

