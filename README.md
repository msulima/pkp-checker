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
pkp-checker/bin/pkp-checker $HOME/pkp-database
```
