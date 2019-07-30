package com.example.moviecatalogue.viewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecatalogue.BuildConfig;
import com.example.moviecatalogue.model.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MovieViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_movie_DB;
    private ArrayList<Movie> listItemsPopular = new ArrayList<>();
    private ArrayList<Movie> listItemsReleasedNow = new ArrayList<>();
    private MutableLiveData<ArrayList<Movie>> listMovies = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Movie>> listReleasedNow = new MutableLiveData<>();

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
                    listItemsPopular.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        Movie movie = new Movie(movieObject);
                        listItemsPopular.add(movie);
                    }
                    listMovies.postValue(listItemsPopular);
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

    public void setMovie(String query, final Context context) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + query + "&language=" + currentLocale;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject listObject = new JSONObject(response);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItemsPopular.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        Movie movie = new Movie(movieObject);
                        listItemsPopular.add(movie);
                    }
                    listMovies.postValue(listItemsPopular);
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

    public void setMovieReleasedNow(final Context context, final String dateNow) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/discover/movie?primary_release_date.gte=" + dateNow + "&api_key=" + BuildConfig.API_movie_DB + "&language=" + currentLocale;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject listObject = new JSONObject(response);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItemsReleasedNow.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject movieObject = resultList.getJSONObject(i);
                        if(movieObject.getString("release_date").compareTo(dateNow) == 0){
                            Movie movie = new Movie(movieObject);
                            listItemsReleasedNow.add(movie);
                        }
                    }
                    listReleasedNow.postValue(listItemsReleasedNow);
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

    public LiveData<ArrayList<Movie>> getMovies() {
        return listMovies;
    }
    public LiveData<ArrayList<Movie>> getNowReleasedMovies() {
        return listReleasedNow;
    }
}
