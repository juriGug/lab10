package it.unibo.oop.lab.streams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream().map(i -> i.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet().stream().filter(i -> i.getValue() == year).map(i -> i.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int)songs.stream().filter(i -> i.getAlbumName().isPresent()).filter(i-> i.getAlbumName().get().equals(albumName)).count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return songs.size() - (int)songs.stream().filter(i -> i.getAlbumName().isPresent()).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream().filter(i-> i.getAlbumName().isPresent())
            .filter(i-> i.getAlbumName().get().equals(albumName))
            .mapToDouble( i -> i.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return songs.stream().max(Comparator.comparingDouble(Song::getDuration)).map(i -> i.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return Optional.of(
            albums.keySet().stream()
            .max((x,y) -> Double.compare(getAlbumLength(x), getAlbumLength(y)))
            .get()
        );
    }

    public Double getAlbumLength(final String s) {
        return (Double)songs.stream().filter(i-> i.getAlbumName().isPresent())
        .filter(i-> i.getAlbumName().get().equals(s))
        .mapToDouble(x -> x.getDuration())
        .reduce((a,b) -> a + b).orElse(0);
    }
    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
