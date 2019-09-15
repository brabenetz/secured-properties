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

If **secretFilePropertyKey** is set, the path to the secret file will be read from the given property of the properties file.

### [2] init(ConfigInitializer...):

If **secretFilePropertyKey** is set, the path to the secret file will be read from the given property of the properties file.

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

will deactivate the auto creation of the secretKey file. If the secretKey file doesn't exist an Exception will be thrown.
