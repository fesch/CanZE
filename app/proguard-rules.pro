# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/robertfisch/Programs/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class lu.fisch.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

##------------------Begin: added rules to see if we can nip ClassNotFound in the butt ----------
## A bit of a shot in the dark taken from https://stackoverflow.com/questions/15686593/java-lang-classnotfoundexception-in-dalvik-system-basedexclassloader-findclass
-keep class com.google.gson.stream.** { *; }
-keep class android.support.** { *; } 
-keep interface android.support.** { *; }
# Keep line numbers to alleviate debugging stack traces 
-renamesourcefileattribute SourceFile 
-keepattributes SourceFile,LineNumberTable

