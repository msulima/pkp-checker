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
private val DefaultDatabaseDirectory = "./database"

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

    val databaseDirectory = File(line.getOptionValue(DatabaseDirectory, DefaultDatabaseDirectory))
    val summarize = line.hasOption(Summarize);

    if (summarize) {
        printSummary(databaseDirectory)
    } else {
        val fetcher = Fetcher(stationIds = stations, intervalInMinutes = 15, databaseDirectory = databaseDirectory)
        fetcher.start()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                fetcher.stop()
            }
        })
    }
}

fun printTrainStatistics(trainStatistics: TrainStatistics) {
    val first = trainStatistics.stops.first()
    val last = trainStatistics.stops.last()

    println("${last.arrivalDelay}\t${first.station} - ${last.station}\t${trainStatistics.train}\t${trainStatistics.date}")
}
