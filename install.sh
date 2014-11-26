#!/bin/bash

for ((i=1;; i++)); do
    read "d$i" || break;
done < $1


sqlplus $d9/$d10<<THEEND

-- Change "1" to the desired fatal return code

whenever sqlerror exit 1;

@setupscrip.sql

quit;


THEEND

echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:. WEB-INF/classes/services/UtilHelper.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:. WEB-INF/classes/RestController.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/GetBigPic.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/GetOnePic.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/groupPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/myPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/PictureBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/publicPicBrowse.java
echo "Compiling UtilHelper.java"
javac -cp WEB-INF/classes:.:WEB-INF/lib/*:.  WEB-INF/classes/popularPicBrowse.java
echo "Installation complete!"
