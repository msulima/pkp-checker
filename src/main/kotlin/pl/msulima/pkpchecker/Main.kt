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

    findCompletedAndSave(stations, databaseDirectory)
            .filter { it.completed }
            .forEach { println(Pair(it.stops.last().arrivalDelay, it)) }
}

private fun summarize(databaseDirectory: File) {
    readAllCompleted(databaseDirectory)
}

private fun findCompletedAndSave(stationIds: Set<Int>, databaseDirectory: File): List<TrainStatistics> {
    return stationIds
            .flatMap { station ->
                readTrains(fetchStation(station, databaseDirectory))
            }
            .distinctBy { it.id }
            .map { processTrain(it, databaseDirectory) }
}

private fun processTrain(train: Train, databaseDirectory: File): TrainStatistics {
    val file = fetchTrain(train.id, train.url, databaseDirectory)
    val maybeStatistics = readStatisticsForTrain(train, file)

    if (maybeStatistics.completed || maybeStatistics.stops.isEmpty()) {
        saveCompleted(train.id, file, databaseDirectory)
    }

    return maybeStatistics
}
