package com.cc.wechatmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cc.core.wechat.model.group.GroupMember;
import com.cc.core.wechat.model.user.Friend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
    List<GroupMember> members;
    List<GroupMember> selectGroupMembers = new LinkedList<>();
    public GroupMemberAdapter() {
        this.members = new ArrayList<>();
    }

    public GroupMember getItem(int position) {
        if (position >= members.size()) {
            return null;
        } else {
            return members.get(position);
        }
    }

    public void refreshData(List<GroupMember> data) {
        members.clear();
        if (data != null) {
            members.addAll(data);
        }
        notifyDataSetChanged();
    }

    void onSelectContact(GroupMember friend, boolean isSelected) {
        if (friend == null) {
            return;
        }

        if (isSelected && !selectGroupMembers.contains(friend)) {
            selectGroupMembers.add(friend);
        } else if (!isSelected) {
            selectGroupMembers.remove(friend);
        }
    }

    public List<GroupMember> getSelectContacts() {
        return selectGroupMembers;
    }

    public void clearSelectContacts() {
        selectGroupMembers.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new GroupMemberAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bindView(getItem(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView wechatView;
        CheckBox checkBox;
        CheckListener checkListener;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            nameView = itemView.findViewById(R.id.name);
            wechatView = itemView.findViewById(R.id.wechatId);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkListener = new CheckListener();
            checkBox.setOnCheckedChangeListener(checkListener);
            checkBox.setVisibility(View.VISIBLE);
        }

        void bindView(GroupMember friend) {
            checkListener.friend = friend;
            Glide.with(avatar.getContext()).load(friend.getAvatar()).into(avatar);
            nameView.setText(friend.getGroupNickName());
            wechatView.setText(friend.getWechatId());
            checkBox.setChecked(selectGroupMembers.contains(friend));
        }
    }

    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        private GroupMember friend;
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onSelectContact(friend, isChecked);
        }
    }
}
