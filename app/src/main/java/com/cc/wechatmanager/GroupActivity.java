package com.cc.wechatmanager;

;import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cc.core.command.Callback;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.model.group.GroupInfo;
import com.cc.core.wechat.model.group.GroupMember;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.user.Friend;
import com.cc.wechatmanager.model.CommandResult;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.CreateGroupResult;
import com.cc.wechatmanager.model.GroupInfoResult;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    RecyclerView contactListView, groupMemberListView;
    ContactsAdapter contactsAdapter;
    GroupMemberAdapter groupMemberAdapter;
    GroupInfo groupInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        contactListView = findViewById(R.id.contactsList);
        contactListView.setLayoutManager(new LinearLayoutManager(this));
        contactsAdapter = new ContactsAdapter(true);
        contactListView.setAdapter(contactsAdapter);

        groupMemberListView = findViewById(R.id.groupMemberList);
        groupMemberListView.setLayoutManager(new LinearLayoutManager(this));
        groupMemberAdapter = new GroupMemberAdapter();
        groupMemberListView.setAdapter(groupMemberAdapter);

        findViewById(R.id.createGroupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsAdapter.getSelectContacts().size() < 2) {
                    Utils.showToast("至少选两位好友！");
                    return;
                }

                createGroup(contactsAdapter.getSelectContacts());
            }
        });

        findViewById(R.id.addGroupMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupInfo == null) {
                    Utils.showToast("请先建群");
                } else if (contactsAdapter.getSelectContacts().isEmpty()) {
                    Utils.showToast("请先选择好友");
                } else {
                    addGroupMember(groupInfo.getGroupWechatId(), contactsAdapter.getSelectContacts());
                }
            }
        });

        findViewById(R.id.removeGroupMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupInfo == null) {
                    Utils.showToast("请先建群");
                } else if (groupMemberAdapter.getSelectContacts().isEmpty()) {
                    Utils.showToast("请先选择好友");
                } else {
                    removeGroupMember(groupInfo.getGroupWechatId(), groupMemberAdapter.getSelectContacts());
                }
            }
        });


        findViewById(R.id.sendTextMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv = findViewById(R.id.sendGroupMsgInput);
                String content = tv.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(GroupActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                TextMessage message = new TextMessage();
                message.setCreateTime(System.currentTimeMillis() / 1000);
                message.setTarget(groupInfo.getGroupWechatId());
                message.setContent(content);

                Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", message), new Callback() {
                    @Override
                    public void onResult(String result) {

                        KLog.e("---->>.", "sendMessage Result:" + result);
                    }
                });
            }

        });
        findViewById(R.id.sendImageMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EditText tv = findViewById(R.id.sendGroupMsgInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(GroupActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ImageMessage msg = new ImageMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(groupInfo.getGroupWechatId());
                        msg.setImageUrl(content);
                        msg.setFrom("denghongxing997955");

                        Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });
        findViewById(R.id.sendVideoMsgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Messenger.Companion.sendCommand(genCommand("wechat:HookXLog"), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "Result:" + result);

                            }
                        });*/
                        EditText tv = findViewById(R.id.sendGroupMsgInput);
                        String content = tv.getText().toString();
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(GroupActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        VideoMessage msg = new VideoMessage();
                        msg.setCreateTime(System.currentTimeMillis());
                        msg.setTarget(groupInfo.getGroupWechatId());
                        msg.setVideoUrl(content);

                        Messenger.Companion.sendCommand(MainActivity.genCommand("sendMessage", msg), new Callback() {
                            @Override
                            public void onResult(String result) {

                                KLog.e("---->>.", "sendMessage Result:" + result);
                            }
                        });
                    }
                }, 1000);
            }
        });

        getContacts();
    }

    private void addGroupMember(final String groupId, List<Friend> members) {
        List<String> args = new ArrayList<>();
        for (Friend f : members) {
            if (isAlreadyInGroup(f)) {
                continue;
            }
            args.add(f.getWechatId());
        }
        Messenger.Companion.sendCommand(MainActivity.genCommand("addGroupMember", groupId, args), new Callback() {
            @Override
            public void onResult(@org.jetbrains.annotations.Nullable String result) {
                KLog.e("---->>.", "addGroupMember Result:" + result);
                CommandResult result1 = StrUtils.fromJson(result, CommandResult.class);
                if (result1 == null) {
                    return;
                }

                if (!result1.isSuccess()) {
                    Utils.showToast(result1.getMessage());
                } else {
                    contactListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGroupInfo(groupId);
                        }
                    }, 1000);
                    Utils.showToast("添加成功");
                }
            }
        });

    }

    private void removeGroupMember(final String groupId, List<GroupMember> members) {
        Messenger.Companion.sendCommand(MainActivity.genCommand("removeGroupMember", groupId, genRemoveMemberParam(members)), new Callback() {
            @Override
            public void onResult(@org.jetbrains.annotations.Nullable String result) {
                KLog.e("---->>.", "addGroupMember Result:" + result);
                CommandResult result1 = StrUtils.fromJson(result, CommandResult.class);
                if (result1 == null) {
                    return;
                }

                if (!result1.isSuccess()) {
                    Utils.showToast(result1.getMessage());
                } else {
                    contactListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGroupInfo(groupId);
                        }
                    }, 1000);
                    Utils.showToast("删除成功");
                }
            }
        });

    }

    private List<String> genRemoveMemberParam(List<GroupMember> members) {
        List<String> parmas = new ArrayList<>(members.size());
        for (int i = 0; i < members.size(); ++i) {
            parmas.add(members.get(i).getWechatId());
        }

        return parmas;
    }

    private void getContacts() {
        Messenger.Companion.sendCommand(MainActivity.genCommand("getContacts"), new Callback() {
            @Override
            public void onResult(final String result) {

                KLog.e("---->>.", "getContacts Result:" + result);

                contactListView.post(new Runnable() {
                    @Override
                    public void run() {
                        final ContactsResult contacts = StrUtils.fromJson(result, ContactsResult.class);
                        contactsAdapter.refreshData(contacts.getData());
                    }
                });
            }
        });
    }

    private void createGroup(List<Friend> members) {
        Messenger.Companion.sendCommand(MainActivity.genCommand("createGroup", (Object[]) getMemberIds(members)), new Callback() {
            @Override
            public void onResult(String result) {
                final CreateGroupResult r = StrUtils.fromJson(result, CreateGroupResult.class);
                if (r.isSuccess()) {
                    contactListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGroupInfo(r.getData());
                        }
                    }, 1000);
                } else {
                    Utils.showToast("创建群失败");
                }

                contactListView.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.clearSelectContacts();
                    }
                });
                KLog.e("---->>.", "createGroup Result:" + result);
            }
        });
    }

    private boolean isAlreadyInGroup(Friend f) {
        if (groupInfo == null || f == null) {
            return true;
        }

        for (GroupMember m : groupInfo.getMemberList()) {
            if (f.getWechatId().equals(m.getWechatId())) {
                return true;
            }
        }

        return false;
    }

    private String[] getMemberIds(List<Friend> members) {
        String[] ids = new String[members.size()];
        for (int i = 0; i < members.size(); ++i) {
            ids[i] = members.get(i).getWechatId();
        }

        return ids;
    }

    private void getGroupInfo(String groupWechatId) {
        Messenger.Companion.sendCommand(MainActivity.genCommand("getGroupInfo", groupWechatId), new Callback() {
            @Override
            public void onResult(String result) {

                KLog.e("---->>.", "getGroupInfo Result:" + result);
                GroupInfoResult result1 = StrUtils.fromJson(result, GroupInfoResult.class);
                if (!result1.isSuccess()) {
                    Utils.showToast(result);
                } else {
                    List<GroupInfo> groupInfos = result1.getData();
                    if (groupInfos.isEmpty()) {
                        return;
                    }
                    groupInfo = groupInfos.get(0);

                    contactListView.post(new Runnable() {
                        @Override
                        public void run() {
                            groupMemberAdapter.refreshData(groupInfo.getMemberList());
                        }
                    });
                }
            }
        });
    }
}
