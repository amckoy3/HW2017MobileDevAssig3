package com.mckoy.howardchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Get references to text views to display database data.
        //final TextView rootTextView = v.findViewById(R.id.root_text_view);
        //final TextView subTreeTextView = v.findViewById(R.id.subtree_text_view);
        final ListView mListView = v.findViewById(R.id.list_view);
        final EditText mEditText = v.findViewById(R.id.editText);
        Button mbutton = v.findViewById(R.id.button);

        MessageSource.get(getContext()).getMessages(new MessageSource.MessageListener(){
            @Override
                    public void onMessageRecieved(List<Message> messageList){
                        MessageArrayAdapter adapter = new MessageArrayAdapter(getContext(), messageList);
                        mListView.setAdapter(adapter);
                        mListView.setSelection(adapter.getCount()-1);
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user ==  null){
                    Toast toast = Toast.makeText(getContext(), "Must log in to send message", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String edit_text = mEditText.getText().toString().trim();
                if(edit_text.isEmpty() || edit_text.length() ==0 || edit_text.equals("") || edit_text==null){
                    Toast toast = Toast.makeText(getContext(), "Can't send blank message", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Message message = new Message(user.getDisplayName(), user.getUid(),mEditText.getText().toString());
                MessageSource.get(getContext()).sendMessage(message);
                mEditText.setText("");
            }

        });
        // Add database code here.

        return v;
    }

    private class MessageArrayAdapter extends BaseAdapter{
        protected Context mContext;
        protected List<Message> mMessageList;
        protected LayoutInflater mLayoutInflater;
        public MessageArrayAdapter(Context context, List<Message> mesageList){
            mContext = context;
            mMessageList = mesageList;
            mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount(){
            return mMessageList.size();
        }

        @Override
        public Object getItem(int position){
            return position;
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final Message message = mMessageList.get(position);
            View rowView = mLayoutInflater.inflate(R.layout.list_view_item, parent, false);

            TextView username = rowView.findViewById(R.id.user_text_view);
            username.setText(message.getmUserName());

            TextView content = rowView.findViewById(R.id.content);
            content.setText(message.getmContent());

            return rowView;

        }


    }

}
