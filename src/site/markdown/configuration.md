# Configuration

<!-- MACRO{toc} -->

## Default Configuration


*new SecuredPropertiesConfig()* is a valid Configuration with following default behaviors:

  * **secretFile** default location: "%user_home%/.secret/securedProperties.key"
  * **autoCreateSecretKey** If the secret key doesn't exists, it will be created automatically
  * **allowedAlgorithm** AES-256,  AES-192,  AES-128, DESede-168, DESede-128:<br/>
      The first algorithm supported by the java-VM will be used to create the initial secret key.<br/>
      **Attention:** the default JDK doesn't support AES-256 and AES-192 without extension.<br/>
      After generating the secretKey file, the algorithm cannot be changed anymore. 

## Custom Configuration

The following customizations are available:

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/ConfigurationSnippet.java} -->

### [1] initDefault():

Similar to Spring-Boot, Externalize your configuration so that you can work with the same application code in different environments.

1. Application property Files './application.properties'. If the File doesn't exist, it will be ignored.
2. Application property Files './config/application.properties'. If the File doesn't exist, it will be ignored.
3. OS environment variables.
4. Java System properties (System.getProperties()).

The last one has the highest priority and will overwrite properties before.<br/>
The Properties which can be configured can be found in  [ConfigKey.java](./xref/net/brabenetz/lib/securedproperties/config/ConfigKey.html).<br/>
The default prefix is "SECURED_PROPERTIES" and the keys must be configured formatted as:

* **UPPER_CASE:** Like "SECURED_PROPERTIES_SECRET_FILE" is used for **OS environment variables**.
* **kebab-case:** Like "secured-properties.secret-file" is used for **System-Properties** and **Property-Files**

### [2] init(ConfigInitializer...):

The generic variant of initDefault().<br/>
Just put in the [ConfigInitializers.java](./xref/net/brabenetz/lib/securedproperties/config/ConfigInitializers.html) you want
into it in the order you want.<br/>
Or implement yout own [ConfigInitializer.java](./xref/net/brabenetz/lib/securedproperties/config/ConfigInitializer.html).<br/>
The last ConfigInitializer has the highest priority and will overwrite properties before.

### [3] withSecretFile(File):

If no secret file path is configured in th eproperty file with key **secretFilePropertyKey**, then the **defaultSecretFile** will be used.

Without **defaultSecretFile** the default  "%user_home%/.secret/securedProperties.key" will be used as secret file.
        
### [4] withSaltLength(int)

The salt length defines the length of the randomly generated salt which will be added to the value before encryption.
Default is 11, and a length of 0 will deactivate the salt.
The salt makes sure that two properties with the same value doesn't have the same encrypted value.

### [5] withAllowedAlgorithm(Algorithm...)

will replace the default **allowedAlgorithm** with the given algorithm.
 
### [6] addAllowedAlgorithm(Algorithm...)

will add additional **allowedAlgorithm** to the given algorithm.
 
### [7] withAutoCreateSecretKey(boolean)

With "false" it will deactivate the auto creation of the secretKey file. If the secretKey file doesn't exist an Exception will be thrown.<br/>
Default is "true".
