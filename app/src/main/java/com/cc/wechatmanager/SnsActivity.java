package com.cc.wechatmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cc.core.command.Callback;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.model.sns.SnsInfo;

import java.util.ArrayList;
import java.util.List;


public class SnsActivity extends AppCompatActivity {
    private ViewGroup imageContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns);
        imageContainer = findViewById(R.id.imageContainer);
        findViewById(R.id.textSnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.textSns);
                if (TextUtils.isEmpty(et.getText())) {
                    Utils.showToast("请输入朋友圈内容");
                }
                sentSns(genTextSnsInfo(et.getText().toString()));
            }
        });
        findViewById(R.id.addImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageContainer.getChildCount() > 9) {
                    Utils.showToast("最多添加9张图片");
                    return;
                }

                EditText et = new EditText(SnsActivity.this);
                et.setPadding(16,16,16,16);
                et.setHint("图片地址");
                imageContainer.addView(et, imageContainer.getChildCount() - 1);
            }
        });
        findViewById(R.id.imageSnsLabelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.imageSnsDesc);
                String desc = et.getText().toString();
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < imageContainer.getChildCount() - 1; ++i) {
                    et = (EditText) imageContainer.getChildAt(i);
                    if (!TextUtils.isEmpty(et.getText())) {
                        images.add(et.getText().toString());
                    }
                }
                if (images.isEmpty()) {
                    Utils.showToast("至少添加一张图片");
                    return;
                }
                sentSns(genImageSnsInfo(desc, images));
            }
        });
        findViewById(R.id.videoSnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.videoSns);


                EditText videoPath = findViewById(R.id.videoSnsUrl);
                if (TextUtils.isEmpty(et.getText())) {
                    Utils.showToast("请输入视频地址");
                    return;
                }
                sentSns(genVideoSnsInfo(et.getText().toString(), videoPath.getText().toString()));
            }
        });
        findViewById(R.id.cardSnsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.cardSnsDesc);
                String desc = et.getText().toString();
                et = findViewById(R.id.cardSnsTitle);
                String title = et.getText().toString();
                et = findViewById(R.id.cardSnsUrl);
                String url = et.getText().toString();
                et = findViewById(R.id.cardSnsThumb);
                String thumb = et.getText().toString();
                if (TextUtils.isEmpty(url)) {
                    Utils.showToast("请输入分享地址");
                    return;
                }
                sentSns(genCardSnsInfo(desc, title, url, thumb));
                //sentSns(genCardSnsInfo());
            }
        });
    }
    private void sentSns(SnsInfo sns) {
        Messenger.Companion.sendCommand(MainActivity.genCommand("uploadSns", sns), new Callback() {
            @Override
            public void onResult(String result) {

                KLog.e("---->>.", "uploadImageSns Result:" + result);
                Utils.showToast(result);
            }
        });
    }

    private SnsInfo genTextSnsInfo(String desc) {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription(desc);
        snsInfo.setSnsType(SnsInfo.Companion.getTEXT_TYPE());
        return snsInfo;
    }

    private SnsInfo genVideoSnsInfo(String desc, String video) {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription(desc);
        snsInfo.setSnsType(SnsInfo.Companion.getVIDEO_TYPE());
        ArrayList<String> medias = new ArrayList<>();
        medias.add(video);
        snsInfo.setMedias(medias);
        return snsInfo;
    }

    private SnsInfo genImageSnsInfo(String desc, ArrayList<String> medias) {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setDescription(desc);
        snsInfo.setSnsType(SnsInfo.Companion.getIMAGE_TYPE());
        snsInfo.setMedias(medias);
        return snsInfo;
    }

    private SnsInfo genCardSnsInfo(String desc, String title, String url, String thumb) {
        SnsInfo snsInfo = new SnsInfo();

        snsInfo.setShareTitle(title);
        snsInfo.setDescription(desc);
        snsInfo.setSnsType(SnsInfo.Companion.getCARD_TYPE());
        snsInfo.setUrl(url);
        ArrayList<String> medias = new ArrayList<>();
        medias.add(thumb);
        snsInfo.setMedias(medias);
        return snsInfo;
    }

    private SnsInfo genCardSnsInfo() {
        SnsInfo snsInfo = new SnsInfo();
        snsInfo.setShareTitle("检察机关认定河北涞源反杀案为正当防卫 决定不起诉女生父母");
        snsInfo.setDescription("检察机关认定河北涞源反杀案为正当防卫 决定不起诉女生父母");
        snsInfo.setSnsType(SnsInfo.Companion.getCARD_TYPE());
        snsInfo.setUrl("https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9762242423668539096%22%7D&n_type=0&p_from=1");
        ArrayList<String> medias = new ArrayList<>();
        medias.add("http://b.hiphotos.baidu.com/image/pic/item/11385343fbf2b2114a65cd70c48065380cd78e41.jpg");
        snsInfo.setMedias(medias);
        return snsInfo;
    }
}
