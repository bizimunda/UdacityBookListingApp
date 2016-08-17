package com.udacity.udacitybooklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity {



    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String GOOGLE_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleAsyncTask task = new GoogleAsyncTask();
        task.execute();
    }


    private void updateUi(Model googleBooks) {
/*
        TextView title = (TextView) findViewById(R.id.activity_main_title);
        title.setText("Title: " + googleBooks.getTitle());

        TextView authorName = (TextView) findViewById(R.id.activity_main_autor);
        authorName.setText("Author Name: " + googleBooks.getAuthors());
*/
    }

    private class GoogleAsyncTask extends AsyncTask<URL, Void, Model> {

        @Override
        protected Model doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(GOOGLE_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Model model = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return model;
        }

        @Override
        protected void onPostExecute(Model model) {
            if (model == null) {
                return;
            }

            updateUi(model);
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error with creating URL", e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {

            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("MainActivity", "Error response code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                // TODO: Handle the exception
                Log.e("MainActivity", "Problem receiving the earthQuake JSON results: ", e);

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private Model extractFeatureFromJson(String googleJSON) {



            if (TextUtils.isEmpty(googleJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(googleJSON);

                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject firstObject = itemsArray.getJSONObject(i);

                    JSONObject volumeInfoObject = firstObject.getJSONObject("volumeInfo");
                    String authors = volumeInfoObject.getString("authors");
                    authors = authors.replace("[", "");
                    authors = authors.replace("]", "");
                    authors = authors.replace("\"", "");

                    String title = volumeInfoObject.getString("title");


                    return new Model(authors, title);

                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }


}

