package com.example.MovieCatalogue.PlainOldJavaObject;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Movie implements Parcelable, Comparable {
    public static final String PATH_IMG = "https://image.tmdb.org/t/p/w500";
    public static final String SMALL_IMG = "https://image.tmdb.org/t/p/w92";
    private long id;
    private String originalTitle, overview, posterPath, voteAverage, releaseDate, backdropPath, originalLanguage;
    private boolean adult;

    public Movie(JSONObject object) {
        try {
            setId(object.getLong("id"));
            setAdult(object.getBoolean("adult"));
            setBackdropPath(object.getString("backdrop_path"));
            setOriginalLanguage(object.getString("original_language"));
            setOriginalTitle(object.getString("original_title"));
            setOverview(object.getString("overview"));
            setPosterPath(object.getString("poster_path"));
            setVoteAverage(object.getString("vote_average"));
            setReleaseDate(object.getString("release_date"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Movie(Parcel in) {
        id = in.readLong();
        originalTitle = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
        backdropPath = in.readString();
        originalLanguage = in.readString();
        adult = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(backdropPath);
        dest.writeString(originalLanguage);
        dest.writeByte((byte) (adult ? 1 : 0));
    }

    @Override
    public int compareTo(Object o) {
        String name = ((Movie)o).getOriginalTitle();
        return getOriginalTitle().compareTo(name);
    }

    @Override
    public String toString() {
        return getOriginalTitle();
    }
}
