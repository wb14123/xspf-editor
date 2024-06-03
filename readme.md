A Scala script to edit [XSPF](https://en.wikipedia.org/wiki/XML_Shareable_Playlist_Format) playlist.

## Requirement

Install [Ammonite](https://ammonite.io/#Ammonite)

## Usage

Note: no order is guaranteed when adding videos to playlist.

Add videos in `<dir>` to playlist:

```
./xspf-editor.sc add --playlist <playlist-file> --dir <dir>
```

Remove videos in `<dir>` to playlist:

```
./xspf-editor.sc remove --playlist <playlist-file> --dir <dir>
```

Clean videos that no longer exists in playlist:

```
./xspf-editor.sc clean --playlist <playlist-file>
```


