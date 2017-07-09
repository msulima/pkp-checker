package pl.msulima.pkpchecker

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.ZoneId

fun fetchStation(id: Int): File {
    val now = LocalDateTime.now(ZoneId.of("UTC"))
    val output = File("database/station/date=${now.toLocalDate()}/hour=${now.hour}/$id.html")
    val url = URL("https://infopasazer.intercity.pl/?p=station&id=$id")

    return getOrDownload(output, url)
}

fun fetchTrain(id: Int, url: URL): File {
    val completed = File("database/train/completed/$id.html")

    if (completed.exists()) {
        return completed
    }

    val now = LocalDateTime.now(ZoneId.of("UTC"))
    val quarter = now.minute / 15
    val pending = File("database/train/pending/date=${now.toLocalDate()}/hour=${now.hour}/quarter=$quarter/$id.html")
    return getOrDownload(pending, url)
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

fun saveCompleted(id: Int, pending: File) {
    val completed = File("database/train/completed/$id.html")
    completed.parentFile.mkdirs()

    Files.move(pending.toPath(), completed.toPath(), StandardCopyOption.REPLACE_EXISTING)
}
