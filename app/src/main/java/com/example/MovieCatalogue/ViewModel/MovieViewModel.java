package com.example.MovieCatalogue.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.example.MovieCatalogue.BuildConfig;
import com.example.MovieCatalogue.PlainOldJavaObject.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MovieViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_movie_DB;
    private ArrayList<Movie> listItems = new ArrayList<>();
    private MutableLiveData<ArrayList<Movie>> listMovies = new MutableLiveData<>();

    private ArrayList<Movie> listItemsFavourite = new ArrayList<>();
    private MutableLiveData<ArrayList<Movie>> listMoviesFavourite = new MutableLiveData<>();


    public void setMovie(final Context context) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=" + currentLocale;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject listObject = new JSONObject(response);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItems.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        Movie movie = new Movie(movieObject);
                        listItems.add(movie);
                    }
                    listMovies.postValue(listItems);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "error data not found : " + statusCode, Toast.LENGTH_SHORT).show();
                client.cancelAllRequests(true);
            }
        });
    }

    public void setMovie(String query, final Context context){
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + query +"&language=" + currentLocale;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject listObject = new JSONObject(response);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItems.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        Movie movie = new Movie(movieObject);
                        listItems.add(movie);
                    }
                    listMovies.postValue(listItems);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "error data not found : " + statusCode, Toast.LENGTH_SHORT).show();
                client.cancelAllRequests(true);
            }
        });
    }

    public void setFavouriteMovie(final Context context, ArrayList<Long> idJSON) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        listItemsFavourite.clear();
        for(long id:idJSON){
            final String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + API_KEY + "&language=" + currentLocale;
            client.setResponseTimeout(5000);
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject movieObject = new JSONObject(response);
                        Movie movie = new Movie(movieObject);
                        listItemsFavourite.add(movie);
                        listMoviesFavourite.postValue(listItemsFavourite);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "error data not found : " + statusCode, Toast.LENGTH_SHORT).show();
                    client.cancelAllRequests(true);
                }
            });
        }
    }


    public LiveData<ArrayList<Movie>> getMoviesFavourites() {
        return listMoviesFavourite;
    }

    public LiveData<ArrayList<Movie>> getMovies() {
        return listMovies;
    }
}
