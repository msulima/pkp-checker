# pkp-checker

## Run

```bash
./gradlew run
```

## Build

```bash
./gradlew distZip
```

## Google Compute Engine host preparation

```bash
sudo apt-get update
sudo apt-get install -y unzip default-jre-headless

rm -rf pkp-checker && unzip pkp-checker.zip 
pkp-checker/bin/pkp-checker ./pkp-database
echo "0,20,40,59 * * * * $(pwd)/pkp-checker/bin/pkp-checker $(pwd)/pkp-database &>> $(pwd)/pkp-checker.log" | crontab -

rsync -avrzu $USER@$HOST:/home/$USER/pkp-database/ database/
```
