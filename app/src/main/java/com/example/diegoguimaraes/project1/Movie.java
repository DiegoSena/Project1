package com.example.diegoguimaraes.project1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by diego.guimaraes on 12/10/16.
 */
public class Movie implements Parcelable {
    String posterPath;
    String title;
    int releaseYear;
    double voteAverage;
    String overview;

    @Override
    public String toString() {
        return posterPath + title + releaseYear + voteAverage + overview;
    }

    public Movie(String poster_path, String title, int releaseYear, double voteAverage, String overview){
        this.posterPath = poster_path;
        this.title = title;
        this.releaseYear = releaseYear;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(title);
        dest.writeInt(releaseYear);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public Movie(Parcel pc){
        posterPath = pc.readString();
        title =  pc.readString();
        releaseYear = pc.readInt();
        voteAverage = pc.readDouble();
        overview = pc.readString();
    }
}
