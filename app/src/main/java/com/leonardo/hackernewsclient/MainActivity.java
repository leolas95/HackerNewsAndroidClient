package com.leonardo.hackernewsclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<String> {
    private List<Story> stories;
    private RecyclerView recyclerView;
    private StoriesAdapter adapter;

    HackerNewsApi api;

    private final String HACKER_NEWS_BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    private final int MAX_STORIES_TO_FETCH = 70;

    private final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_STORY_URI = "EXTRA_STORY_URI";
    public static final String EXTRA_STORY_COMMENTS = "EXTRA_STORY_COMMENTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stories = new ArrayList<>();
        recyclerView = findViewById(R.id.rv);
        adapter = new StoriesAdapter(stories);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(),
                recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Story story = stories.get(position);
                Uri uriStory, uriComments;

                // If the item is something like Ask HN, etc.
                if (story.getType().equals("story") && story.getUrl() == null) {
                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.scheme("https")
                            .authority("news.ycombinator.com")
                            .appendPath("item")
                            .appendQueryParameter("id", String.valueOf(story.getId()));

                    uriStory = uriBuilder.build();
                } else {
                    uriStory = Uri.parse(stories.get(position).getUrl());
                }

                // Gets the comments page of the item
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("https")
                        .authority("news.ycombinator.com")
                        .appendPath("item")
                        .appendQueryParameter("id", String.valueOf(story.getId()));

                uriComments = uriBuilder.build();

                Intent intent = new Intent(MainActivity.this, StoryDetailsActivity.class);
                intent.putExtra(EXTRA_STORY_URI, uriStory);
                intent.putExtra(EXTRA_STORY_COMMENTS, uriComments);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                // empty
            }
        }));

        loadStories();
    }

    private void loadStories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HACKER_NEWS_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(HackerNewsApi.class);
        Call<String> topStories = api.getTopStories();

        topStories.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
        Log.w(TAG, "Response: " + response.body());
        StoryDownloaderHelper storyDownloaderHelper = new StoryDownloaderHelper();

        try {
            final JSONArray jsonArray = new JSONArray(response.body());

            for (int i = 0; i < MAX_STORIES_TO_FETCH; i++) {
                Call<Story> storyCall = api.getStory(jsonArray.getString(i));
                storyCall.enqueue(storyDownloaderHelper);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
        Log.e(TAG, "Error downloading Top Stories: " + t.getMessage());
    }

    /**
     * This is to download a story individually. Because of the Hacker News API, in order to
     * fetch the topstories, we first have to make a request to get the id's of them, then
     * process each id to fetch its respective story.
     *
     * TODO: Choose a better name
     */
    private final class StoryDownloaderHelper implements Callback<Story> {

        @Override
        public void onResponse(@NonNull Call<Story> call, @NonNull Response<Story> response) {
            Story story = response.body();
            Log.w(TAG, "Downloaded story `" + story.getTitle() + "` with id = " + story.getId());
            stories.add(story);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(@NonNull Call<Story> call, @NonNull Throwable t) {
            Log.e(TAG, "Error downloading story: " + t.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.w(TAG, "Refreshing stories");
                loadStories();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}