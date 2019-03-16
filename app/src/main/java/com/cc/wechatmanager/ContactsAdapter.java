package com.cc.wechatmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cc.core.wechat.model.user.Friend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    List<Friend> contacts;
    List<Friend> selectContacts = new LinkedList<>();
    private boolean selectable, singleSelect;
    private OnSelectFriendListener selectFriendListener;
    private OnClickFriendListener clickFriendListener;
    public ContactsAdapter(boolean selectable) {
        this(selectable, false);
    }
    public ContactsAdapter(boolean selectable, boolean singleSelect) {
        this.contacts = new ArrayList<>();
        this.selectable = selectable;
        this.singleSelect = singleSelect;
    }

    public Friend getItem(int position) {
        if (contacts == null || position >= contacts.size()) {
            return null;
        } else {
            return contacts.get(position);
        }
    }

    public void refreshData(List<Friend> data) {
        contacts.clear();
        if (data != null) {
            contacts.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setSelectFriendListener(OnSelectFriendListener selectFriendListener) {
        this.selectFriendListener = selectFriendListener;
    }

    public void setClickFriendListener(OnClickFriendListener clickFriendListener) {
        this.clickFriendListener = clickFriendListener;
    }

    void onSelectContact(Friend friend, boolean isSelected) {
        if (friend == null) {
            return;
        }

        if (isSelected && !selectContacts.contains(friend)) {
            if (singleSelect) {
                clearSelectContacts();
            }
            selectContacts.add(friend);
        } else if (!isSelected) {
            selectContacts.remove(friend);
        }
        if (selectFriendListener != null) {
            selectFriendListener.onSelectFriend(friend, isSelected);
        }
    }

    public List<Friend> getSelectContacts() {
        return selectContacts;
    }

    public void clearSelectContacts() {
        selectContacts.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new ContactsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bindView(getItem(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView wechatView;
        CheckBox checkBox;
        CheckListener checkListener;
        ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            nameView = itemView.findViewById(R.id.name);
            wechatView = itemView.findViewById(R.id.wechatId);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkListener = new CheckListener();
            itemClickListener = new ItemClickListener();
            itemView.setOnClickListener(itemClickListener);
            checkBox.setOnCheckedChangeListener(checkListener);
        }

        void bindView(Friend friend) {
            checkBox.setVisibility(selectable ? View.VISIBLE : View.GONE);
            checkListener.friend = friend;
            itemClickListener.friend = friend;
            Glide.with(avatar.getContext()).load(friend.getAvatar()).into(avatar);
            nameView.setText(TextUtils.isEmpty(friend.getRemark()) ? friend.getNickname() : friend.getRemark());
            wechatView.setText(friend.getWechatId());
            checkBox.setChecked(selectContacts.contains(friend));
        }
    }

    void onClickItem(Friend friend) {
        if (clickFriendListener != null) {
            clickFriendListener.onClickFriend(friend);
        }
    }

    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        private Friend friend;
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onSelectContact(friend, isChecked);
        }
    }

    private class ItemClickListener implements View.OnClickListener {
        private Friend friend;

        @Override
        public void onClick(View v) {
            onClickItem(friend);
        }
    }

    public interface OnSelectFriendListener {
        void onSelectFriend(Friend friend, boolean isSelected);
    }

    public interface OnClickFriendListener {
        void onClickFriend(Friend friend);
    }
}
