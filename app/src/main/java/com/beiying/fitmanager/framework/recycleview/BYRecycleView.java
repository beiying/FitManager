package com.beiying.fitmanager.framework.recycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Created by yangjz on 17/5/24.
 */

public abstract class BYRecycleView<M> extends RecyclerView {

    public static final int TYPE_HEADER = 100100;
    public static final int TYPE_FOOTER = 100200;

    private LinearLayoutManager mLayoutManager;
    private OnItemClickListener mItemClickListener;
    private View mFooterView;
    private View mHeaderView;

    protected BYRecycleAdapter<M, MHolder> mAdapter;

    public BYRecycleView(Context context) {
        super(context);
        mLayoutManager = new LinearLayoutManager(context);
        setLayoutManager(mLayoutManager);
        mAdapter = new BYRecycleAdapter<M, MHolder>(getModelList()) {
            @Override
            public MHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == TYPE_FOOTER && mFooterView != null) {
                    return new MHolder(mFooterView);
                }
                if (viewType == TYPE_HEADER && mHeaderView != null) {
                    return new MHolder(mHeaderView);
                }
                return new MHolder(getItemView(parent, viewType));
            }

            @Override
            public void onBindViewHolder(MHolder holder, int position) {
                if ((position == getTotalSize() - 1 && mFooterView != null)) {
                    return;
                }

                if (mHeaderView != null) {
                    position--;
                }

                if (position < 0 || position >= mAdapter.size()) {
                    return;
                }

                M model = mAdapter.get(position);
                onBeforeBindModel(model, holder);
                holder.setModel(model);
                if (mItemClickListener != null) {
                    holder.setOnItemClickListener(mItemClickListener);
                }
            }

            @Override
            public int getItemCount() {
                return getTotalSize();
            }

            @Override
            public int getItemViewType(int position) {
                if (mHeaderView != null && position == 0) {
                    return TYPE_HEADER;
                }
                if (mFooterView != null && position == getTotalSize() - 1) {
                    return TYPE_FOOTER;
                }
                return getViewType(position - mAdapter.getHeaderSize());
            }
        };

        setAdapter(mAdapter);
    }

    @Override
    public BYRecycleAdapter getAdapter() {
        return mAdapter;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        mAdapter.setHasFooter(true);
    }


    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        mAdapter.setHasHeader(true);
    }

    public void removeFooterView() {
        removeViewInLayout(mFooterView);
        mFooterView = null;
        mAdapter.setHasHeader(false);
        mAdapter.notifyDataSetChanged();
    }

    public void removeHeaderView() {
        removeViewInLayout(mHeaderView);
        mHeaderView = null;
        mAdapter.setHasHeader(false);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    private int getTotalSize() {
        int size = mAdapter.size();
        if (mHeaderView != null) {
            size++;
        }
        if (mFooterView != null) {
            size++;
        }
        return size;
    }

    public void setOrientation(int orientation) {
        mLayoutManager.setOrientation(orientation);
    }

    protected abstract BYBaseRecycleItem<M> getItemView(ViewGroup parent, int viewType);

    protected List<M> getModelList() {
        return null;
    }

    protected int getViewType(int position) {
        return 0;
    }

    protected void onBeforeBindModel(M model, MHolder holder) {

    }

    public class MHolder extends ViewHolder {
        private BYBaseRecycleItem<M> mItem;
        private M mModel;

        public MHolder(View itemView) {
            super(itemView);
        }

        public MHolder(BYBaseRecycleItem<M> itemView) {
            super(itemView);
            mItem = itemView;
        }

        public void setModel(M model) {
            if (mItem == null) {
                return;
            }
            mModel = model;
            mItem.setModel(model);
        }

        public void setOnItemClickListener(OnItemClickListener itemClickListener) {
            if (mItem == null) {
                return;
            }
            mItem.setOnItemClickListener(itemClickListener);
        }

        public BYBaseRecycleItem<M> getItem() {
            return mItem;
        }
    }

    public interface OnItemClickListener<M> {
        void onItemClick(M model, View view);
    }
}
