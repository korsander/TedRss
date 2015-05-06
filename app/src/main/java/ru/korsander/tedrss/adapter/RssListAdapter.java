/**
 * Created by korsander on 05.05.2015.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.korsander.tedrss.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.model.Article;

public class RssListAdapter extends RecyclerView.Adapter<RssListAdapter.ViewHolder> {
    private ArrayList<Article> items;

    public RssListAdapter(ArrayList<Article> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.rss_list_item, group, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article a = items.get(position);
        holder.tvTitle.setText(a.getTitle());
        holder.tvDesc.setText(a.getDescription());
        holder.tvDuration.setText(a.getDuration());
        Picasso.with(TedRss.getContext()).load(a.getThumb()).into(holder.ivThumb);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumb;
        private TextView tvTitle;
        private TextView tvDesc;
        private TextView tvDuration;

        public ViewHolder(View view) {
            super(view);
            ivThumb = (ImageView) view.findViewById(R.id.thumbImageView);
            tvTitle = (TextView) view.findViewById(R.id.titleTextView);
            tvDesc = (TextView) view.findViewById(R.id.descTextView);
            tvDuration = (TextView) view.findViewById(R.id.descTextView);
        }
    }
}
