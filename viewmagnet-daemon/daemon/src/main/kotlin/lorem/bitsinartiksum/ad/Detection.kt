package lorem.bitsinartiksum.ad

import model.Ad
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

enum class Detection {
    DOG,
    CAT,
    RAIN,
    BICYCLE,
    TORNADO,
    COLD,
    NOISE,
    HOT,
    BABY;

    companion object {
        private val defaultPath = System.getProperty("user.dir") + File.separator + "detections"

        fun extractAdsFromFolder(
            path: String = System.getProperty(
                "detectionAds",
                defaultPath
            )
        ): Map<Detection, Set<Ad>> {
            val types = values().map { it.name.toLowerCase() }

            // Check if a folder exists for each detection type
            val allExists = types.all { name ->
                Files.exists(Paths.get(path, "$name.txt"))
            }

            if (!allExists) {
                val folderPath = Paths.get(path)
                if (Files.notExists(folderPath)) Files.createDirectory(folderPath)
                types
                    .map { folderPath.resolve("$it.txt") }
                    .filter { Files.notExists(it) }
                    .forEach { Files.createFile(it) }
                throw Exception("Create a folder with at least 1 img link for each type, given path: $path/detections")
            }

            val files = Files.list(Paths.get(path)).collect(Collectors.toList())
            val detectionAds = {
                files

                    .filter { it.fileName.toString().substringBefore(".") in types }
                    .map { fpath ->
                        val detection = valueOf(fpath.fileName.toString().substringBefore(".").toUpperCase())
                        val images = Files.readAllLines(fpath)
                        if (images.isEmpty()) throw Exception("$fpath doesnt have any image in it")
                        val ads =
                            images.mapIndexed { i, link -> Ad(id = "default-${fpath.fileName}-$i", content = link) }
                        detection to ads.toSet()
                    }.toMap()
            }()
            return detectionAds
        }

//        fun extractAdsFromFolder(
//            path: String = System.getProperty(
//                "detectionAds",
//                defaultPath
//            )
//        ): Map<Detection, Set<BufferedImage>> {
//
//            val folders = Files.list(Paths.get(path)).collect(Collectors.toList())
//            val types = values().map { it.name.toLowerCase() }
//
//            // Check if a folder exists for each detection type
//            val allExists = types.all { name ->
//                folders.any { Files.isDirectory(it) && it.fileName.toString() == name }
//            }
//
//            if (!allExists) {
//                Files.createDirectory(Paths.get(defaultPath, "detections"))
//                types.forEach {
//                    Files.createDirectory()
//                }
//                throw Exception("Create a folder with at least 1 img for each type, given path: $path/detections")
//            }
//
//            val detectionImages = {
//                folders
//                    .filter { it.fileName.toString() in types }
//                    .map { fpath ->
//                        val detection = valueOf(fpath.fileName.toString().toUpperCase())
//                        val images = Files.list(fpath).map { ImageIO.read(it.toFile()) } .collect(Collectors.toSet())
//                        if(images.size > 0) {
//                            throw Exception("At least 1 image required for each type couldn't find img for ${detection.name}")
//                        }
//                        val pair: Pair<Detection, Set<BufferedImage>> = detection to images
//                        pair
//                    }.toMap()
//            }()
//
//            return detectionImages
//        }
    }
}