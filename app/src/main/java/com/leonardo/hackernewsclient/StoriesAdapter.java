package com.leonardo.hackernewsclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {
    private List<Story> stories;


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoryTitle;
        TextView tvStoryAuthor;
        TextView tvStoryComments;

        ViewHolder(View itemView) {
            super(itemView);
            tvStoryTitle = itemView.findViewById(R.id.tv_story_title);
            tvStoryAuthor = itemView.findViewById(R.id.tv_story_author);
            tvStoryComments = itemView.findViewById(R.id.tv_story_comments);
        }
    }

    StoriesAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.story_list_row, parent, false);

        return new StoriesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.tvStoryTitle.setText(story.getTitle());
        holder.tvStoryAuthor.setText(story.getBy());
        holder.tvStoryComments.setText("(" + String.valueOf(story.getDescendants()) + " comments)");
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }
}
