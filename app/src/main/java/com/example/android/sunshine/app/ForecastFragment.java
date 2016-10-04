package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jezeh on 7/6/16.
 */
public class ForecastFragment extends Fragment {



    public ForecastFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        //Populating with some dummy data for now
        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 88 / 46",
                "Weds - Black - 100 / 95",
                "Thurs - Rainy - 64 / 51",
                "Fri -  WHERE IS THE SUN? - 10 / -55",
                "Sat - APOCALYPTIC - 127 / -30"

        };

        List<String> weekForecastsArray = new ArrayList<>(Arrays.asList(forecastArray));

        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(
                //Current Context
                getActivity(),
                //The Layout file that defines the list
                R.layout.list_item_forecast,
                //The text field within the list that needs to be populated
                R.id.listItemForecastTextView,
                //The Array List of strings to populate with
                weekForecastsArray);

        //Get ListView and set an adapter to it
        ListView forecastListView = (ListView) rootView.findViewById(R.id.listViewForecast);
        forecastListView.setAdapter(forecastAdapter);

        return rootView;
    }


    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == (R.id.action_refresh)){
            FetchWeatherTask task = new FetchWeatherTask();
            task.execute();
        }
        else if (id == R.id.action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

     class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=21230&mode=json&units=metric&cnt=7&";
                String apiKey = getApiKey();
                String userZip = getZip();
                //URL url = new URL(baseURL.concat(apiKey));

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("api.openweathermap.org");
                builder.appendPath("data");
                builder.appendPath("2.5");
                builder.appendPath("forecast");
                builder.appendPath("daily");
                builder.appendQueryParameter("q",userZip);
                builder.appendQueryParameter("mode","json");
                builder.appendQueryParameter("units","metric");
                builder.appendQueryParameter("cnt","7");
                builder.appendQueryParameter("APPID", apiKey);
                String myURL = builder.build().toString();
                URL url = new URL(myURL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: "+forecastJsonStr);
            }

            catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

    public String getZip(){
          String zip = PreferenceManager.getDefaultSharedPreferences(getActivity())
                  .getString("ZIP_CODE_PREF", null);
        return zip;
    }

    public String getApiKey(){
        try{
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String apiKey = bundle.getString("OPEN_WEATHER_API_KEY");
            return apiKey;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

