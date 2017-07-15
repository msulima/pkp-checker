package pl.msulima.pkpchecker

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import java.io.File

private val stations = setOf(
        73312, // Katowice
        80416, // Kraków
        33605, // Warszawa Centralna
        30601 // Poznań Główny
)

private val DatabaseDirectory = "database-dir"
private val Summarize = "summarize"

private val options: Options = Options()
        .addOption(Option.builder("d")
                .longOpt(DatabaseDirectory)
                .required(false)
                .hasArg()
                .desc("Database directory")
                .build()
        )
        .addOption(Option.builder("s")
                .longOpt(Summarize)
                .required(false)
                .desc("Summarize completed")
                .build()
        )


fun main(args: Array<String>) {
    val parser = DefaultParser()
    val line = parser.parse(options, args)

    val databaseDirectory = File(line.getOptionValue(DatabaseDirectory, "./database"))
    val summarize = line.hasOption(Summarize);

    if (summarize) {
        summarize(databaseDirectory)
                .take(30)
                .forEach { printTrainStatistics(it) }
    } else {
        findCompletedAndSave(stations, databaseDirectory)
                .forEach { printTrainStatistics(it) }
    }
}

private fun findCompletedAndSave(stationIds: Set<Int>, databaseDirectory: File): List<TrainStatistics> {
    return stationIds
            .flatMap { station ->
                readTrains(fetchStation(station, databaseDirectory))
            }
            .distinctBy { it.id }
            .map { processTrain(it, databaseDirectory) }
            .filterNotNull()
}

private fun processTrain(train: Train, databaseDirectory: File): TrainStatistics? {
    val file = fetchTrain(train.id, train.url, databaseDirectory)
    val maybeStatistics = readStatisticsForTrain(file)

    if (maybeStatistics != null && maybeStatistics.completed) {
        saveCompleted(train.id, file, databaseDirectory)
    }

    return maybeStatistics
}

private fun printTrainStatistics(trainStatistics: TrainStatistics) {
    val first = trainStatistics.stops.first()
    val last = trainStatistics.stops.last()

    println("${last.arrivalDelay}\t${first.station} - ${last.station}\t${trainStatistics.train}\t${trainStatistics.date}")
}
