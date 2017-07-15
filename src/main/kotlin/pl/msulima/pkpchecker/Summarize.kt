package pl.msulima.pkpchecker

import java.io.File


fun summarize(databaseDirectory: File): List<TrainStatistics> {
    return readAllCompleted(databaseDirectory)
            .map { readStatisticsForTrain(it) }
            .filterNotNull()
            .sortedBy { -it.stops.last().arrivalDelay }
}
