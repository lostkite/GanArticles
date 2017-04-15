package com.cambrian.android.ganarticles.views;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cambrian.android.ganarticles.BuildConfig;
import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.enties.Article;
import com.cambrian.android.ganarticles.enties.TypeEnum;
import com.cambrian.android.ganarticles.utils.ListUtil;

import java.util.List;

/**
 * recycler view 的adapter
 * Created by S.J.Xiong on 2017/3/6.
 */

public class ArticleListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ArticleListAdapter";

    // item type 基本样式
    private static final int NORMAL_ITEM = 0x00;
    // 带 title item
    private static final int ITEM_WITH_TITLE = 0x01;
    // 图片型 item
    private static final int IMAGE_ITEM = 0x02;

    private static final int NORMAL_ITEM_WITH_IMAGE = 0x03;

    private static final int ITEM_WITH_TITLE_AND_IMAGE = 0x04;

    private List<Article> mArticleList;
    private ArticlesBindCallback mBindCallback;

    private boolean mShowTitle = false;

    /**
     * 构造函数
     *
     * @param articleList  list, 数据源
     * @param bindCallback 回调接口
     */
    public ArticleListAdapter(List<Article> articleList, ArticlesBindCallback bindCallback) {
        mArticleList = articleList;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "adapter " + mArticleList.size());
        }
        mBindCallback = bindCallback;
    }

    /**
     * 是否显示带title的 item
     *
     * @param showTitle show?
     */
    public void setShowTitle(boolean showTitle) {
        mShowTitle = showTitle;
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticleList.get(position);
        TypeEnum currentType = article.getType();
        if (currentType == TypeEnum.福利) {
            return IMAGE_ITEM;
        } else if (ListUtil.isListEmpty(article.getImageUrls())) {
            if (position == 0) {
                return mShowTitle ? ITEM_WITH_TITLE : NORMAL_ITEM;
            } else {
                int preIndex = position - 1;
                if (currentType != (mArticleList.get(preIndex).getType())) {
                    return mShowTitle ? ITEM_WITH_TITLE : NORMAL_ITEM;
                } else {
                    return NORMAL_ITEM;
                }
            }
        } else {
            if (position == 0) {
                return mShowTitle ? ITEM_WITH_TITLE_AND_IMAGE : NORMAL_ITEM_WITH_IMAGE;
            } else {
                int preIndex = position - 1;
                if (currentType != (mArticleList.get(preIndex).getType())) {
                    return mShowTitle ? ITEM_WITH_TITLE_AND_IMAGE : NORMAL_ITEM_WITH_IMAGE;
                } else {
                    return NORMAL_ITEM_WITH_IMAGE;
                }
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case IMAGE_ITEM:
                view = inflater.inflate(R.layout.image_item, parent, false);
                return new ImageItemHolder(view);
            case ITEM_WITH_TITLE:
                view = inflater.inflate(R.layout.item_with_title, parent, false);
                return new TitleItemHolder(view);
            case NORMAL_ITEM:
                view = inflater.inflate(R.layout.item_normal, parent, false);
                return new NormalItemHolder(view);
            case NORMAL_ITEM_WITH_IMAGE:
                view = inflater.inflate(R.layout.item_normal_with_image, parent, false);
                return new NormalItemWithImageHolder(view);
            case ITEM_WITH_TITLE_AND_IMAGE:
                view = inflater.inflate(R.layout.item_with_title_and_image, parent, false);
                return new ItemWithTitleAndImageHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        if (holder == null) {
            return;
        }
        if (holder instanceof ImageItemHolder) {
            ((ImageItemHolder) holder).bindArticle(article);
        } else if (holder instanceof ItemWithTitleAndImageHolder) {
            ((ItemWithTitleAndImageHolder) holder).bindArticle(article);
        } else if (holder instanceof TitleItemHolder) {
            ((TitleItemHolder) holder).bindArticle(article);
        } else if (holder instanceof NormalItemWithImageHolder) {
            ((NormalItemWithImageHolder) holder).bindArticle(article);
        } else {
            ((NormalItemHolder) holder).bindArticle(article);
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    /**
     * 普通的 item
     */
    private class NormalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Article mArticle;

        private TextView mItemDesc;
        private CheckBox mStarCheckBox;

        NormalItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemDesc = (TextView) itemView.findViewById(R.id.text_view_item_desc);
            mStarCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_item_star);
        }

        void bindArticle(Article article) {
            mArticle = article;

            mItemDesc.setText(mArticle.getDesc());
            mStarCheckBox.setChecked(article.isStarred());
            mStarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mArticle.setStarred(isChecked);
                    mBindCallback.onStarStateChanged(isChecked, mArticle);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mBindCallback.showDetailArticle(mArticle);
        }
    }

    /**
     * 带有 title 的 item
     */
    private class TitleItemHolder extends NormalItemHolder {
        TextView mTitle;

        TitleItemHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.text_view_item_type);
        }

        void bindArticle(Article article) {
            super.bindArticle(article);
            mTitle.setText(article.getType().toString());
        }
    }

    /**
     * 带图片的 item
     */
    private class NormalItemWithImageHolder extends NormalItemHolder {
        ImageView mImageView;

        NormalItemWithImageHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_article_image);
        }

        void bindArticle(final Article article) {
            super.bindArticle(article);
            if (!ListUtil.isListEmpty(article.getImageUrls())) {
                String imageUrl = article.getImageUrls().get(0);
                Glide.with(mImageView.getContext())
                        .load(imageUrl)
                        .asBitmap()
                        .placeholder(R.drawable.placeholder)
                        .into(mImageView);
            }
        }
    }

    /**
     * 带有图片和 title 的 item
     */
    private class ItemWithTitleAndImageHolder extends NormalItemWithImageHolder {
        TextView mTitleView;

        ItemWithTitleAndImageHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.text_view_item_type);
        }

        void bindArticle(Article article) {
            super.bindArticle(article);
            mTitleView.setText(article.getType().toString());
        }
    }


    /**
     * 图片型 item
     */
    private class ImageItemHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;

        ImageItemHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.welfare_image_view);
        }

        void bindArticle(final Article article) {
            if (article != null) {
                Glide.with(mImageView.getContext())
                        .load(article.getUrl())
                        .centerCrop()
                        .into(mImageView);
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBindCallback.showDetailArticle(article);
                    }
                });
            }
        }
    }

    /**
     * 由持有这个 adapter 的类实现该接口
     */
    public interface ArticlesBindCallback {
        /**
         * 显示给定 article 的详细信息
         *
         * @param article article
         */
        void showDetailArticle(Article article);

        /**
         * 当收藏状态改变时调用此方法
         *
         * @param starred star state
         * @param article article
         */
        void onStarStateChanged(boolean starred, Article article);
    }
}