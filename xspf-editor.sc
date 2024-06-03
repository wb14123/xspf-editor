#!/usr/bin/env amm

import $ivy.`org.scala-lang.modules::scala-xml:2.2.0 compat`

import java.nio.file.Files
import scala.xml.XML

def getVideosFromDir(dir: os.Path): Set[String] = {
  os.walk(dir, includeTarget = true)
    .filter { f =>
      val typ = Files.probeContentType(f.toNIO)
      typ != null && typ.contains("video")}
    .map(_.toString).toSet
}

def getVideosFromDoc(playlist: os.Path): Set[String] = {
  if (os.exists(playlist)) {
    val doc = XML.loadFile(playlist.toString())
    val videos = (doc \\ "track" \ "location").map(node => playlist / os.RelPath.up / os.RelPath(node.text))
    val cleanedVideos = videos.filter(os.exists).map(_.toString()).toSet
    val diff = videos.size - cleanedVideos.size
    println(s"Cleaned up $diff videos.")
    cleanedVideos
  } else {
    println(s"File $playlist doesn't exists.")
    Set()
  }
}

def createPlaylist(videos: Set[String], playlist: os.Path): Unit = {
  val tracks = videos.map { video =>
    val path = os.Path(video).relativeTo(playlist / os.RelPath.up)
    <track>
      <title>{path.baseName}</title>
      <location>{path.toString()}</location>
    </track>
  }
  val doc = <playlist version="1" xmlns="http://xspf.org/ns/0/">
    <trackList>
      {tracks}
    </trackList>
  </playlist>
  XML.save(playlist.toString(), doc)
}

@main
def remove(playlist: os.Path, dir: os.Path): Unit = {
  val oldVideos = getVideosFromDoc(playlist)
  val removeVideos = getVideosFromDir(dir)
  val newVideos = oldVideos -- removeVideos
  val diff = oldVideos.size - newVideos.size
  createPlaylist(newVideos, playlist)
  println(s"Removed $diff videos.")
}

@main
def clean(playlist: os.Path): Unit = {
  val videos = getVideosFromDoc(playlist)
  createPlaylist(videos, playlist)
}

@main
def add(playlist: os.Path, dir: os.Path): Unit = {
  val oldVideos = getVideosFromDoc(playlist)
  val newVideos = getVideosFromDir(dir)
  createPlaylist(oldVideos ++ newVideos, playlist)
  println(s"Added ${newVideos.size} videos.")
}
