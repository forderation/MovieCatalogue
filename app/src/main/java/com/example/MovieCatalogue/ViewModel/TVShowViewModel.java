package com.example.MovieCatalogue.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.example.MovieCatalogue.BuildConfig;
import com.example.MovieCatalogue.PlainOldJavaObject.TVShow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TVShowViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_movie_DB;
    private ArrayList<TVShow> listItems = new ArrayList<>();
    private MutableLiveData<ArrayList<TVShow>> listTVShows = new MutableLiveData<>();

    private ArrayList<TVShow> listItemsFavourite = new ArrayList<>();
    private MutableLiveData<ArrayList<TVShow>> listTVShowsFavourite = new MutableLiveData<>();

    public LiveData<ArrayList<TVShow>> getTVShows() {
        return listTVShows;
    }
    public LiveData<ArrayList<TVShow>> getTVShowsFavourite() {
        return listTVShowsFavourite;
    }
    public void setTVShows(final Context context) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/tv/popular?api_key=" + API_KEY + "&language=" + currentLocale;
        client.setResponseTimeout(10000);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject listObject = new JSONObject(result);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItems.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject tvObject = resultList.getJSONObject(i);
                        TVShow tvShow = new TVShow(tvObject);
                        listItems.add(tvShow);
                    }
                    listTVShows.postValue(listItems);
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

    public void setTVShows(String query, final Context context) {
        final AsyncHttpClient client = new AsyncHttpClient();
        String currentLocale = Locale.getDefault().getLanguage();
        if (currentLocale.compareToIgnoreCase("in") == 0) {
            currentLocale = "id";
        }
        final String url = "https://api.themoviedb.org/3/search/tv?api_key=" + API_KEY + "&query=" + query +"&language=" + currentLocale;
        client.setResponseTimeout(10000);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject listObject = new JSONObject(result);
                    JSONArray resultList = listObject.getJSONArray("results");
                    listItems.clear();
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject tvObject = resultList.getJSONObject(i);
                        TVShow tvShow = new TVShow(tvObject);
                        listItems.add(tvShow);
                    }
                    listTVShows.postValue(listItems);
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
            final String url = "https://api.themoviedb.org/3/tv/" + id + "?api_key=" + API_KEY + "&language=" + currentLocale;
            client.setResponseTimeout(5000);
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject tvObject = new JSONObject(response);
                        TVShow tvShow = new TVShow(tvObject);
                        listItemsFavourite.add(tvShow);
                        listTVShowsFavourite.postValue(listItemsFavourite);
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
}
