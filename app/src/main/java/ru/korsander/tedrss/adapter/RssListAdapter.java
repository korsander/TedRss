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

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.utils.Const;

public class RssListAdapter extends RecyclerView.Adapter<RssListAdapter.ViewHolder> {
    private ArrayList<Article> items;
    private int lastPosition = -1;

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
        final Article a = items.get(position);
        final int pos = position;
        StringBuilder builder = new StringBuilder();
        builder.append("<b>").append(a.getTitle()).append("</b>")
                .append(" - ").append(a.getDescription());
        if(a.isViewed()) {
            holder.tvTime.setTextAppearance(TedRss.getContext(), R.style.TimeTextView_Light);
        } else {
            holder.tvTime.setTextAppearance(TedRss.getContext(), R.style.TimeTextView_Dark);
        }
        holder.tvTime.setText(a.getFormattedDate(Const.RFC1123_SHORT_DATE_PATTERN));
        holder.tvDesc.setText(Html.fromHtml(builder.toString()));
        holder.tvDuration.setText(a.getDurationString());
        holder.chbViewed.setChecked(a.isViewed());
        Picasso.with(TedRss.getContext()).load(a.getThumb()).into(holder.ivThumb);
        holder.chbViewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.get(pos).setViewed(((CheckBox) view).isChecked());
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(pos);
                    }
                });
            }
        });

        Animation animation = AnimationUtils.loadAnimation(TedRss.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
        holder.chbViewed.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumb;
        private TextView tvTime;
        private TextView tvDesc;
        private TextView tvDuration;
        private CheckBox chbViewed;

        public ViewHolder(View view) {
            super(view);
            ivThumb = (ImageView) view.findViewById(R.id.thumbImageView);
            tvTime = (TextView) view.findViewById(R.id.timeTextView);
            tvDesc = (TextView) view.findViewById(R.id.descTextView);
            tvDuration = (TextView) view.findViewById(R.id.durationTextView);
            chbViewed = (CheckBox) view.findViewById(R.id.viewedCheckBox);

        }
    }
}
