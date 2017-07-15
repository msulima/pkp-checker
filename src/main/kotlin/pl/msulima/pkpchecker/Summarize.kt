package pl.msulima.pkpchecker

import java.io.File


fun summarize(databaseDirectory: File): List<TrainStatistics> {
    val completed = readAllCompleted(databaseDirectory)
            .map { readStatisticsForTrain(it) }
            .filterNotNull()


    return completed
}

fun findOnTrack(completed: List<TrainStatistics>, firstStation: String, secondStation: String): List<TrainStatistics> {
    val trainsByStation = hashMapOf<String, MutableSet<TrainStatistics>>()

    completed.forEach { train ->
        train.stops.forEach { (station) ->
            val trains = trainsByStation.computeIfAbsent(station, { hashSetOf() })
            trains.add(train)
        }
    }

    val startTrains = trainsByStation.getOrDefault(firstStation, hashSetOf())
    val targetTrains = trainsByStation.getOrDefault(secondStation, hashSetOf())

    val commonTrains = startTrains.intersect(targetTrains)

    return commonTrains.map { train ->
        val firstIndex = train.stops.indexOfFirst { it.station == firstStation }
        val secondIndex = train.stops.indexOfFirst { it.station == secondStation }

        val stopsBetweenStations = if (secondIndex > firstIndex) {
            train.stops.subList(firstIndex, secondIndex + 1)
        } else {
            train.stops.subList(secondIndex, firstIndex + 1)
        }

        train.copy(stops = stopsBetweenStations)
    }
}

