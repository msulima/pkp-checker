package pl.msulima.pkpchecker

import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId

data class Train(val id: Int, val name: String, val relation: String, val vendor: String, val url: URL)
data class Stop(val station: String, val arrivalDelay: Int, val departureDelay: Int)
data class TrainStatistics(val train: Train, val completed: Boolean, val date: LocalDate, val stops: List<Stop>)

fun readTrains(file: File): List<Train> {
    val doc = Jsoup.parse(file, "UTF-8", "https://infopasazer.intercity.pl/?p=station&id=73312")
    val table = doc.select("table.table-delay")[0]
    val elements = table.select("tbody tr")

    val trains = elements.map { element ->
        val url = element.select("td:nth-child(1) a").attr("abs:href")
        val id = element.select("td:nth-child(1) a").attr("href").substring("?p=train&id=".length).toInt()
        val name = element.select("td:nth-child(1) a").text()
        val vendor = element.select("td:nth-child(2) a").text()
        val relation = element.select("td:nth-child(4) span").text()

        Train(id, name, relation, vendor, URL(url))
    }

    return trains
}

fun readStatisticsForTrain(train: Train, file: File): TrainStatistics {
    val rows = Jsoup
            .parse(file, "UTF-8", train.url.toString())
            .select("table.table-delay tbody tr")

    val stops = rows
            .takeWhile { !it.hasClass("current") }
            .map { stop ->
                val station = stop.select("td:nth-child(4) a").text()
                val arrivalDelay = stop.select("td:nth-child(6) span").text()
                val departureDelay = stop.select("td:nth-child(8) span").text()

                Stop(station, timeStringToInt(arrivalDelay), timeStringToInt(departureDelay))
            }

    val completed = rows.size == stops.size

    return TrainStatistics(train, completed, LocalDate.now(ZoneId.of("UTC")), stops)
}

private fun timeStringToInt(time: String): Int {
    return if (time == "---") {
        0
    } else {
        time.takeWhile { it != ' ' }.toInt()
    }
}
