package org.belichenko.a.onehundredmeterchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.belichenko.a.onehundredmeterchat.ListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements Constant{

    private final List<Message> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final String storedName;

    public RecyclerViewAdapter(List<Message> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        SharedPreferences sharedPref = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        storedName = sharedPref.getString(STORED_NAME, "");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_left, parent, false);

            return new ViewHolderLeft(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_right, parent, false);
            return new ViewHolderRight(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position).user_id.equals(storedName)){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder.getItemViewType() == 1) {
            final ViewHolderLeft holder = (ViewHolderLeft) viewHolder;
            holder.mItem = mValues.get(position);
            holder.mUser.setText(mValues.get(position).user_id);
            holder.mMessage.setText(mValues.get(position).text);
            holder.mCoordinate.setText(mValues.get(position).time);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });

        }else{
            final ViewHolderRight holder = (ViewHolderRight) viewHolder;
            holder.mItem = mValues.get(position);
            holder.mUser.setText(mValues.get(position).user_id);
            holder.mMessage.setText(mValues.get(position).text);
            holder.mCoordinate.setText(mValues.get(position).time);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        public final FrameLayout mView;
        public final TextView mUser;
        public final TextView mMessage;
        public final TextView mCoordinate;
        public Message mItem;

        public ViewHolderLeft(View view) {
            super(view);
            mView = (FrameLayout) view;
            mUser = (TextView) view.findViewById(R.id.user_list);
            mMessage = (TextView) view.findViewById(R.id.message_list);
            mCoordinate = (TextView) view.findViewById(R.id.time_list);
        }
    }

    public class ViewHolderRight extends RecyclerView.ViewHolder {
        public final FrameLayout mView;
        public final TextView mUser;
        public final TextView mMessage;
        public final TextView mCoordinate;
        public Message mItem;

        public ViewHolderRight(View view) {
            super(view);
            mView = (FrameLayout) view;
            mUser = (TextView) view.findViewById(R.id.user_list);
            mMessage = (TextView) view.findViewById(R.id.message_list);
            mCoordinate = (TextView) view.findViewById(R.id.time_list);
        }
    }
}