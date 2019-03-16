package com.cc.wechatmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cc.core.wechat.WeChatMessageType;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.UnsupportMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.VoiceMessage;
import com.cc.core.wechat.model.message.VoipMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<WeChatMessage> messages = new ArrayList<>();

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
            .inflate(
                ViewType == 1 ? R.layout.layout_image_message_item : R.layout.layout_message_item,
                viewGroup, false));
    }

    private WeChatMessage getItem(int position) {
        if (position >= messages.size()) {
            return null;
        }

        return messages.get(position);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindView(getItem(i));
    }

    @Override public int getItemCount() {
        return messages.size();
    }

    @Override public int getItemViewType(int position) {
        WeChatMessage msg = getItem(position);
        if (msg instanceof ImageMessage) {
            return 1;
        }
        return 0;
    }

    public void addMessage(WeChatMessage msg) {
        if (msg == null) {
            return;
        }
        messages.add(0, msg);
        notifyItemRangeInserted(0, 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromView;
        TextView contentView;
        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            fromView = itemView.findViewById(R.id.from);
            contentView = itemView.findViewById(R.id.content);
            imgView = itemView.findViewById(R.id.image);
        }

        void bindView(WeChatMessage msg) {
            if (msg == null) {
                return;
            }

            fromView.setText(msg.getFrom());
            switch (msg.getType()) {
                case WeChatMessageType.TEXT:
                    contentView.setText("文本消息：\n" + ((TextMessage) msg).getContent());
                    break;
                case WeChatMessageType.VIDEO:
                    contentView.setText("视频消息：\n" + ((VideoMessage) msg).getVideoUrl());
                    break;
                case WeChatMessageType.VOICE:
                    contentView.setText("语音消息：\n" + ((VoiceMessage) msg).getVoiceUrl());
                    break;
                case WeChatMessageType.IMAGE:
                    contentView.setText("图片消息：");
                    Glide.with(imgView.getContext()).load(new File(((ImageMessage) msg).getImageUrl())).placeholder(R.mipmap.ic_launcher_round).into(imgView);
                    break;
                case WeChatMessageType.EMOJI:
                    contentView.setText("emoji表情：");
                    Glide.with(imgView.getContext()).load(((ImageMessage) msg).getImageUrl()).placeholder(R.mipmap.ic_launcher_round).into(imgView);
                    break;
                case WeChatMessageType.VOIP:
                    contentView.setText("Voip消息：\n" + (((VoipMessage)msg).getVoipType() == 1 ? "语音通信" : "视频通信"));
                    break;
                case WeChatMessageType.CARD:
                    contentView.setText("卡片消息：\n"
                        + ((CardMessage) msg).getTitle() + "\n\n"
                        + ((CardMessage) msg).getDescription() + "\n\n"
                        + "Url:"
                        + ((CardMessage) msg).getUrl());
                    break;
                default:
                    contentView.setText(((UnsupportMessage) msg).getContent() + "\n"
                        + ((UnsupportMessage) msg).getMessageDetails());
                    break;
            }
        }
    }
}
