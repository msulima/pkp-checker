package pl.msulima.pkpchecker

import java.io.File


fun printSummary(databaseDirectory: File): Unit {
    val completed = readAllCompleted(databaseDirectory)
            .mapNotNull { readStatisticsForTrain(it) }

    completed
            .sortedBy { -it.stops.last().arrivalDelay }
            .take(30)
            .forEach { printTrainStatistics(it) }

    println("---")
    val onTrack = findOnTrack(completed, "Katowice", "Warszawa Centralna")
            .sortedBy { -it.stops.last().arrivalDelay }

    println("Average: ${onTrack.sumBy { it.stops.last().arrivalDelay }.toDouble() / onTrack.size}")
    println("75th p: ${onTrack[(onTrack.size * 0.25).toInt()].stops.last().arrivalDelay}")
    println("90th p: ${onTrack[(onTrack.size * 0.10).toInt()].stops.last().arrivalDelay}")
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

