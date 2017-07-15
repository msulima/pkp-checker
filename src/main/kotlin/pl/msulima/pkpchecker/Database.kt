package pl.msulima.pkpchecker

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.ZoneId

fun fetchStation(id: Int, databaseDirectory: File): File {
    val now = LocalDateTime.now(ZoneId.of("UTC"))
    val output = station(id, now, databaseDirectory)
    val url = URL("https://infopasazer.intercity.pl/?p=station&id=$id")

    return getOrDownload(output, url)
}

private fun station(id: Int, now: LocalDateTime, databaseDirectory: File): File {
    return File(databaseDirectory, "station/date=${now.toLocalDate()}/hour=${now.hour}/$id.html")
}

fun fetchTrain(id: Int, url: URL, databaseDirectory: File): File {
    val completed = completedTrain(id, databaseDirectory)

    if (completed.exists()) {
        return completed
    }

    val now = LocalDateTime.now(ZoneId.of("UTC"))
    val pending = pendingTrain(id, now, databaseDirectory)

    return getOrDownload(pending, url)
}

private fun pendingTrain(id: Int, now: LocalDateTime, databaseDirectory: File): File {
    val quarter = now.minute / 15
    return File(databaseDirectory, "train/pending/date=${now.toLocalDate()}/hour=${now.hour}/quarter=$quarter/$id.html")
}

private fun getOrDownload(output: File, url: URL): File {
    if (!output.exists()) {
        println("Downloading $url (missing file $output)")

        output.parentFile.mkdirs()

        val readableByteChannel = Channels.newChannel(url.openStream())
        val fileOutputStream = FileOutputStream(output)

        fileOutputStream.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
    } else {
        println("Reading file $output")
    }

    return output
}

fun saveCompleted(id: Int, pending: File, databaseDirectory: File) {
    val completed = completedTrain(id, databaseDirectory)
    completed.parentFile.mkdirs()

    Files.move(pending.toPath(), completed.toPath(), StandardCopyOption.REPLACE_EXISTING)
}

fun readAllCompleted(databaseDirectory: File): List<File> {
    return completedTrainsDir(databaseDirectory)
            .listFiles()
            .toList()
}

private fun completedTrain(id: Int, databaseDirectory: File): File {
    return File(completedTrainsDir(databaseDirectory), "$id.html")
}

private fun completedTrainsDir(databaseDirectory: File): File {
    return File(databaseDirectory, "train/completed")
}
