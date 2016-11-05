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
  * **autoEncryptNonEncryptedValues** If the value in the property file is not already encrypted. it will be replaced by the encrypted value.

In addition, secret values can also be set over SystemProperties (command line e.g. -DmyPwdKey=myPwd).<br/>
This works only if the properties file doesn't have a value for the given key.<br/>
An info-message will be logged with the encrypted version of the SystemProperty value if the given value was not already encrypted.

## Custom Configuration

The following customizations are available:

<!-- MACRO{snippet|id=configExample|file=src/test/java/net/brabenetz/lib/securedproperties/snippets/ConfigurationSnippet.java} -->

### [1] withSecretFile() and withSecretFilePropertyKey():

Only one of the two properties can be set.

If **secretFile** is set, **secretFilePropertyKey** will be ignored, and the **secretFile** will always be used as secret file.

If only **secretFilePropertyKey** is set, the path to the secret file will be read from the given property of the properties file.

If the properties file doesn't have a value for the given key, <br/>
then the default "%user_home%/.secret/securedProperties.key" will be used as secret file.
        
### [2] withAllowedAlgorithm()

will replace the default **allowedAlgorithm** with the given algorithm.
 
### [3] addAllowedAlgorithm()

will add additional **allowedAlgorithm** to the given algorithm.
 
### [4] disableAutoCreateSecretKey()

will deactivate the auto creation of the secretKey file. If the secretKey file doesn't exist an Exception will be thrown.
 
### [5] disableAutoEncryptNonEncryptedValues()

will deactivate the auto encryption of passwords in the property file. The property file will never be changed.<br/>
A warning will be logged if the password is in plain-text.
