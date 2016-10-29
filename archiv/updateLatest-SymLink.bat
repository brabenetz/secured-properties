rmdir latest
rm latest
mklink /D latest 1.0-SNAPSHOT
rmdir current
rm current
mklink /D current 1.0-SNAPSHOT
cd ..
rmdir currentrelease
rm currentrelease
mklink /D currentrelease archiv\1.0-SNAPSHOT
cd archiv
