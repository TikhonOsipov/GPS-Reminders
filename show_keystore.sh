#!/bin/bash
keytool -list -v -keystore $1 -alias gpsreminders -storepass 123456 -keypass 123456 | grep "SHA1"
