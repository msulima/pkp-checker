package pl.msulima.pkpchecker

import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


class Fetcher(
        private val stationIds: Set<Int>,
        private val intervalInMinutes: Int,
        private val databaseDirectory: File
) {
    companion object {
        private val logger = LogManager.getLogger(Fetcher::class.java)
    }

    private val trains: MutableSet<Train> = ConcurrentHashMap.newKeySet()
    private val timer = Timer(false)

    fun start() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run(): Unit {
                logger.info("Start processing")

                getTrainsFromStations()
                processAll()
            }
        }, 0, TimeUnit.MINUTES.toMillis(intervalInMinutes.toLong()))
    }

    fun stop() {
        timer.cancel()
    }

    private fun getTrainsFromStations() {
        stationIds
                .flatMap { station ->
                    readTrains(fetchStation(station, databaseDirectory))
                }
                .map { trains.add(it) }
    }

    private fun processAll() {
        trains.forEach(this::processTrain)
    }

    private fun processTrain(train: Train) {
        val file = fetchTrain(train.id, train.url, databaseDirectory)
        val maybeStatistics = readStatisticsForTrain(file)

        if (maybeStatistics == null) {
            trains.remove(train)
            logger.warn("Could not find train $train")
        } else if (maybeStatistics.completed) {
            saveCompleted(train.id, file, databaseDirectory)
            trains.remove(train)
            logger.info("Finished train $train")
            printTrainStatistics(maybeStatistics)
        }
    }
}
