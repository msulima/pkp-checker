# pkp-checker

Because I had a feeling that 3/4 of my trains are late, I decided to verify it basing on real data.
Unfortunately PKP does not have any API, especially with historical data.
The only way to get data on current schedule of trains is [https://infopasazer.intercity.pl/](https://infopasazer.intercity.pl/).
This project extracts data of trains going via some popular stations like Warsaw or Katowice.
Data is can be later analyzed and average delay, 75th, 90th percentile are calculated.

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
