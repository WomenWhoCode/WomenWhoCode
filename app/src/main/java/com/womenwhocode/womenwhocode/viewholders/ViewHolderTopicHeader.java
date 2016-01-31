package com.womenwhocode.womenwhocode.viewholders;

import com.womenwhocode.womenwhocode.R;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zassmin on 11/12/15.
 */
public class ViewHolderTopicHeader extends RecyclerView.ViewHolder {
    @Bind(R.id.tvTopicHeader)
    public TextView tvTopicHeader;

    public ViewHolderTopicHeader(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

