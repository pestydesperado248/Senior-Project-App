package com.example.timelychefs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity implements BrowseViewInterface{

    public ArrayList<BrowseItem> ItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getJSON("http://se4500seniorproject.atwebpages.com/getRecipes.php");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        //items.add(new BrowseItem("Spaghetti", "Mario", R.drawable.roast));
        //items.add(new BrowseItem("Pizza", "Luigi", R.drawable.roast));
        //items.add(new BrowseItem("Tortellini", "Wario", R.drawable.roast));


    }

    private void loadIntoListView(String json) throws JSONException{
        RecyclerView recyclerView = findViewById(R.id.recipeList);
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<BrowseItem> items = new ArrayList<BrowseItem>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int id = obj.getInt("recipeID");
            String name = obj.getString("title");
            String author = "Created by "+ obj.getString("displayName");
            String desc = obj.getString("description");
            items.add(new BrowseItem(id,name,author, R.drawable.roast));
            Log.d("d","Adding "+name);
        }
        ItemList = items;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BrowseAdapter(getApplicationContext(), items, this));


    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                Log.d("d",s);
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d("d", "Wow Look Noting");

                    return "[{\"title\":\"Sorry\",\"description\":\"Mamma mia! I eat-a a meatball!\",\"recipeID\":\"1\",\"displayName\":\"Something went wrong\"}" +
                            ",{\"title\":\""+ e.toString() + "\",\"description\":\"Mamma mia! I eat-a a meatball!\",\"recipeID\":\"1\",\"displayName\":\"Something went wrong\"}]";
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(BrowseActivity.this, RecipeActivity.class);
        intent.putExtra("recipeID", ItemList.get(position).getID());
        System.out.println("Recipe ID: "+ItemList.get(position).getID());
        System.out.println("Recipe Name: "+ItemList.get(position).getRecipe());
        startActivity(intent);
    }

    @Override
    public void OnLongItemClick(int position) {

    }
}
