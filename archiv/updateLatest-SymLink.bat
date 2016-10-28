rmdir latest
mklink /D latest 1.0-SNAPSHOT
rmdir current
mklink /D current 1.0-SNAPSHOT
cd ..
rmdir currentrelease
mklink /D currentrelease archiv\1.0-SNAPSHOT
cd archiv
