package org.belichenko.a.onehundredmeterchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;

import java.util.LinkedList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListFragment extends Fragment implements Constant, View.OnClickListener {

    private static final String TAG = "List fragment";
    protected static final String name = "Chat";
    private static final ListFragment ourInstance = new ListFragment();
    protected LinkedList<Message> messagesList;
    private OnListFragmentInteractionListener mListener;
    private RecyclerViewAdapter mAdapter;
    private ImageView imageList;
    private EditText textList;
    private AdLayout mAdView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }


    public static ListFragment getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        imageList = (ImageView) view.findViewById(R.id.list_send_btn);
        textList = (EditText) view.findViewById(R.id.list_msg_text);
        textList.setBackground(null);

        imageList.setOnClickListener(this);
        messagesList = new LinkedList<>();
        mAdapter = new RecyclerViewAdapter(messagesList, mListener);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(mAdapter);

        AdRegistration.enableLogging(false);
        AdRegistration.enableTesting(true);
        AdRegistration.setAppKey("ba43cb42ea6c4650a5be17803d99d5ce");
        mAdView = (AdLayout) view.findViewById(R.id.adListView);
        AdTargetingOptions adOptions = new AdTargetingOptions();
        mAdView.loadAd();
        mAdView.setListener(new AdListener() {
            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                Log.d(TAG, "onAdLoaded() called with: " + "ad = [" + ad + "], adProperties = [" + adProperties + "]");
            }

            @Override
            public void onAdFailedToLoad(Ad ad, AdError adError) {
                Log.d(TAG, "onAdFailedToLoad() called with: " + "ad = [" + ad + "], adError = [" + adError + "]");
            }

            @Override
            public void onAdExpanded(Ad ad) {
                Log.d(TAG, "onAdExpanded() called with: " + "ad = [" + ad + "]");
            }

            @Override
            public void onAdCollapsed(Ad ad) {
                Log.d(TAG, "onAdCollapsed() called with: " + "ad = [" + ad + "]");
            }

            @Override
            public void onAdDismissed(Ad ad) {
                Log.d(TAG, "onAdDismissed() called with: " + "ad = [" + ad + "]");
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateMessages(LinkedList<Message> msgList) {
        if (msgList == null) {
            Log.d(TAG, "updateMessages() called with: " + "msgList = [" + null + "]");
            return;
        }
        messagesList.clear();
        messagesList.addAll(msgList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMessages(((MainActivity) getActivity()).getMsgList());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdView.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_send_btn:
                ((MainActivity) getActivity()).sendNewMessage(textList.getText().toString());
                textList.setText("");
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Message message);
    }
}
