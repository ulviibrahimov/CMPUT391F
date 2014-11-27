#!/bin/bash

for ((i=1;; i++)); do
    read "d$i" || break;
done < $1

echo "username $d9"
echo "username $d10"

sqlplus $d9/$d10<<THEEND

-- Change "1" to the desired fatal return code

--whenever sqlerror exit 1;

@setupscript.sql

quit;


THEEND

echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/* WEB-INF/classes/services/UtilHelper.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/* WEB-INF/classes/RestController.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/GetBigPic.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/GetOnePic.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/groupPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/myPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/PictureBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/publicPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:Sources/jar/*  WEB-INF/classes/popularPicBrowse.java
echo "Installation complete!"
