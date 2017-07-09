package pl.msulima.pkpchecker

val stations = setOf(
        73312, // Katowice
        80416, // Kraków
        33605, // Warszawa Centralna
        30601 // Poznań Główny
)

fun main(args: Array<String>) {
    stations
            .flatMap { station ->
                readTrains(fetchStation(station))
            }
            .toSet()
            .map { processTrain(it) }
            .filterNotNull()
            .filter { it.completed }
            .forEach { println(Pair(it.stops.last().arrivalDelay, it)) }
}

private fun processTrain(train: Train): TrainStatistics? {
    val file = fetchTrain(train.id, train.url)
    val maybeStatistics = readStatisticsForTrain(train, file)

    if (maybeStatistics.completed || maybeStatistics.stops.isEmpty()) {
        saveCompleted(train.id, file)
    }

    return maybeStatistics
}
