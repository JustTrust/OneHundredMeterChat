package org.belichenko.a.onehundredmeterchat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.belichenko.a.onehundredmeterchat.ListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUser.setText(mValues.get(position).user_id);
        holder.mMessage.setText(mValues.get(position).text);
        holder.mCoordinate.setText(mValues.get(position).getLatLng());

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
        public final View mView;
        public final TextView mUser;
        public final TextView mMessage;
        public final TextView mCoordinate;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUser = (TextView) view.findViewById(R.id.user_list);
            mMessage = (TextView) view.findViewById(R.id.message_list);
            mCoordinate = (TextView) view.findViewById(R.id.coordinates_list);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessage.getText() + "'";
        }
    }
}