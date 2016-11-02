# Example Settings4j

An example how secured-properties can be used with [Settings4j](http://settings4j.org).

<!-- MACRO{toc} -->

## Description

The following steps are required to initialize settings4j:

  * First get the decrypted password.
  * Store the decrypted password into SystemProperties under the same key.
  * create a PropertyFileConnector
  * Add the PropertyFileConnector to the Settings4j-Repository after the SystemPropertyConnector.
  * FINSHED: use Settings4j as usual.

In this example, the SystemProperties is only a working example to provide the decrypted password.
Better would be to use a custom in-memory implementations of the Settings4j-Connector.  
  
## The Property File

The example property-file "TestProperties-Valid.properties":

<!-- MACRO{snippet|id=configExample|file=src/test/data/TestProperties-Valid.properties} -->

## The Java Code

The java code example:

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/Settings4jExampleTest.java} -->




