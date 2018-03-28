package com.leonardo.hackernewsclient;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HackerNewsApi {

    @GET("topstories.json")
    Call<String> getTopStories();

    @GET("item/{id}.json")
    Call<Story> getStory(@Path("id") String storyId);
}
