
@ECHO OFF
SETLOCAL

SET CARRENT_SERVER_PROD_PATH=\\CARRENTSERVER\Users\Administrateur\Documents\CARRENT\app


SET cur_dir=%~dp0

SET FILE_PATH=%~f0

del /s /q "%CARRENT_SERVER_PROD_PATH%\target\*"
del /s /q "%cur_dir%target\carrent\*"
unzip -q "%cur_dir%target\carrent-distribution.zip" -d "%cur_dir%target"
robocopy "%cur_dir%target\carrent" "%CARRENT_SERVER_PROD_PATH%\target" /s


echo "deploiement terminé"
pause 
