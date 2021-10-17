SET cur_dir=%~dp0
SET reg_path=HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run

setx CARRENT_SERVER_PATH "\"%cur_dir%"
SET FILE_PATH=%~f0

reg query "%reg_path%" /v carrent

echo N | REG ADD "%reg_path%" /v carrent /t REG_SZ /d "\"%FILE_PATH%\" /f0

cd\
%~d0
cd "%CARRENT_SERVER_PATH%"

"cmder_mini/Cmder.exe"