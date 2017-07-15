package pl.msulima.pkpchecker

import java.io.File

val stations = setOf(
        73312, // Katowice
        80416, // Kraków
        33605, // Warszawa Centralna
        30601 // Poznań Główny
)

fun main(args: Array<String>) {
    val databaseDirectory = if (args.isEmpty()) {
        File("./database")
    } else {
        File(args[0])
    }

    stations
            .flatMap { station ->
                readTrains(fetchStation(station, databaseDirectory))
            }
            .distinctBy { it.id }
            .map { processTrain(it, databaseDirectory) }
            .filterNotNull()
            .filter { it.completed }
            .forEach { println(Pair(it.stops.last().arrivalDelay, it)) }
}

private fun processTrain(train: Train, databaseDirectory: File): TrainStatistics? {
    val file = fetchTrain(train.id, train.url, databaseDirectory)
    val maybeStatistics = readStatisticsForTrain(train, file)

    if (maybeStatistics.completed || maybeStatistics.stops.isEmpty()) {
        saveCompleted(train.id, file, databaseDirectory)
    }

    return maybeStatistics
}
