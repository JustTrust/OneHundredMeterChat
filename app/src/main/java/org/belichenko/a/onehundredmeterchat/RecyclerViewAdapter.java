package org.belichenko.a.onehundredmeterchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.belichenko.a.onehundredmeterchat.ListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
            implements Constant{

    private final List<Message> mValues;
    private final OnListFragmentInteractionListener mListener;

    public RecyclerViewAdapter(List<Message> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SharedPreferences sharedPref = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String storedName = sharedPref.getString(STORED_NAME, "");

        if (mValues.get(position).user_id.equals(storedName)) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
            holder.mView.setLayoutParams(params);
            holder.mViewContainer.setBackgroundColor(App.getAppContext().getResources()
                    .getColor(R.color.green_msg));
        }else{
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT);
            holder.mView.setLayoutParams(params);
            holder.mViewContainer.setBackgroundColor(App.getAppContext().getResources()
                    .getColor(R.color.blue_msg));
        }
        holder.mItem = mValues.get(position);
        holder.mUser.setText(mValues.get(position).user_id);
        holder.mMessage.setText(mValues.get(position).text);
        holder.mCoordinate.setText(mValues.get(position).time);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout mView;
        public final RelativeLayout mViewContainer;
        public final TextView mUser;
        public final TextView mMessage;
        public final TextView mCoordinate;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = (FrameLayout) view;
            mViewContainer = (RelativeLayout) view.findViewById(R.id.list_item_container);
            mUser = (TextView) view.findViewById(R.id.user_list);
            mMessage = (TextView) view.findViewById(R.id.message_list);
            mCoordinate = (TextView) view.findViewById(R.id.time_list);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessage.getText() + "'";
        }
    }
}