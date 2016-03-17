package org.belichenko.a.onehundredmeterchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListFragment extends Fragment implements Constant, View.OnClickListener {

    private static final String TAG = "List fragment";
    protected static String name = "Chat";
    private static ListFragment ourInstance = new ListFragment();
    protected ArrayList<Message> messagesList;
    private OnListFragmentInteractionListener mListener;
    private RecyclerViewAdapter mAdapter;
    private ImageView imageList;
    private EditText textList;

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

        imageList.setOnClickListener(this);
        messagesList = new ArrayList<>();
        mAdapter = new RecyclerViewAdapter(messagesList, mListener);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

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

    public void updateMessage(ArrayList<Message> msgList) {
        if (msgList == null) {
            Log.d(TAG, "updateMessage() called with: " + "msgList = [" + msgList + "]");
            return;
        }
        messagesList.clear();
        messagesList.addAll(msgList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMessage(((MainActivity) getActivity()).getMsgList());
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
