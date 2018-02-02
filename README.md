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
pkp-checker/bin/pkp-checker -d ./pkp-database 2>&1 | tee -a pkp-checker.log
echo "0 3 * * * rm -rf $(pwd)/pkp-database/train/pending" | crontab -

rsync -avrzu $USER@$HOST:/home/$USER/pkp-database/ database/
```
