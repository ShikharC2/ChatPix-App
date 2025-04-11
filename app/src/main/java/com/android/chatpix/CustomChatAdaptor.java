package com.android.chatpix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomChatAdaptor extends BaseAdapter{

    List<ChatConstructor> message;
    LayoutInflater inflater;

    CustomChatAdaptor(Context context,List<ChatConstructor> msg){
        inflater = LayoutInflater.from(context);
        message = msg;
    }

    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Object getItem(int i) {
        return message.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String sender1 = message.get(i).isSender;


        if(sender1.equals("true")){
            view = inflater.inflate(R.layout.sender_chat_list,viewGroup,false);
        }
        else{
            view = inflater.inflate(R.layout.receiver_chat_list,viewGroup,false);
        }
        TextView sender = view.findViewById(R.id.textView17);
        TextView viewTime = view.findViewById(R.id.textView18);
        sender.setText(message.get(i).message);
        viewTime.setText(message.get(i).time);
        return view;
    }


}
