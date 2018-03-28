package com.leonardo.hackernewsclient;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);

        Intent intent = getIntent();
        Uri uriStory = intent.getParcelableExtra(MainActivity.EXTRA_STORY_URI);
        Uri uriComments = intent.getParcelableExtra(MainActivity.EXTRA_STORY_COMMENTS);

        Uri[] uris = new Uri[] {uriStory, uriComments};

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), StoryDetailsActivity.this, uris));

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
