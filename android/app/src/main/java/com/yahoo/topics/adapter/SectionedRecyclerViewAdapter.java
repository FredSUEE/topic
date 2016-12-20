package com.yahoo.topics.adapter;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

/**
 * Created by jyuen on 2/3/16.
 */
public abstract class SectionedRecyclerViewAdapter extends RecyclerViewAdapter {

    protected final static int VIEW_TYPE_SECTION_HEADER = 1;
    protected final static int VIEW_TYPE_SECTION_ITEM = 2;

    private final ArrayMap<Integer, Integer> mHeaderLocationMap;

    public SectionedRecyclerViewAdapter() {
        mHeaderLocationMap = new ArrayMap<>();
    }

    public final boolean isSectionHeader(int position) {
        return mHeaderLocationMap.get(position) != null;
    }

    public abstract int getSectionCount();

    public abstract int getSectionItemCount(int section);

    public abstract void onBindSectionItemViewHolder(RecyclerViewAdapter.ViewHolder holder,
            int section, int relativePosition, int absolutePosition);

    public abstract void onBindSectionHeaderViewHolder(RecyclerView.ViewHolder holder, int section);


    @Override
    protected final int getContentItemCount() {
        int count = 0;
        mHeaderLocationMap.clear();
        for (int s = 0; s < getSectionCount(); s++) {
            mHeaderLocationMap.put(count, s);
            count += getSectionItemCount(s) + 1;
        }
        return count;
    }

    public int getSectionHeaderViewType(int section) {
        return VIEW_TYPE_SECTION_HEADER;
    }

    public int getSectionItemViewType(int section, int relativePosition, int absolutePosition) {
        return VIEW_TYPE_SECTION_ITEM;
    }

    @Override
    protected final int getContentItemViewType(int position) {
        if (isSectionHeader(position)) {
            return getSectionHeaderViewType(mHeaderLocationMap.get(position));
        } else {
            final Pair<Integer, Integer> sectionAndPos =
                    getSectionIndexAndRelativePosition(position);
            return getSectionItemViewType(
                    sectionAndPos.first,
                    sectionAndPos.second,
                    position - (sectionAndPos.first + 1));
        }
    }

    @Override
    protected final void onBindContentItemViewHolder(ViewHolder holder, int position) {
        if (isSectionHeader(position)) {
            onBindSectionHeaderViewHolder(holder, mHeaderLocationMap.get(position));
        } else {
            final Pair<Integer, Integer> sectionAndPos =
                    getSectionIndexAndRelativePosition(position);
            onBindSectionItemViewHolder(
                    holder,
                    sectionAndPos.first,
                    sectionAndPos.second,
                    position - (sectionAndPos.first + 1));
        }
    }

    protected final Pair<Integer, Integer> getSectionIndexAndRelativePosition(int itemPosition) {
        synchronized (mHeaderLocationMap) {
            Integer lastSectionIndex = -1;
            for (final Integer sectionIndex : mHeaderLocationMap.keySet()) {
                if (itemPosition > sectionIndex) {
                    lastSectionIndex = sectionIndex;
                } else {
                    break;
                }
            }
            return new Pair<>(mHeaderLocationMap.get(lastSectionIndex),
                    itemPosition - lastSectionIndex - 1);
        }
    }
}
