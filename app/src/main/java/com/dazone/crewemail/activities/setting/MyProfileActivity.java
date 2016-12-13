package com.dazone.crewemail.activities.setting;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.BaseActivity;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.UserDBHelper;
import com.dazone.crewemail.utils.Prefs;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maidinh on 23/3/2016.
 */
public class MyProfileActivity extends BaseActivity {
    private String TAG = "MyInfor";
    private ImageView img_bg;
    private TextView tv_name, tv_personal, tv_email, tv_company;
    private JSONObject object;
    private String CellPhone = "";
    private String MailAddress = "";
    private ImageView avatar_imv;
    private ImageView image_profile;
    private ImageView btn_back;
    public Prefs prefs;
    private RelativeLayout lay_image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myinfor);
        image_profile = (ImageView) findViewById(R.id.image_profile);
        lay_image_profile= (RelativeLayout) findViewById(R.id.lay_image_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_personal = (TextView) findViewById(R.id.tv_personal);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_company = (TextView) findViewById(R.id.tv_company);
        avatar_imv = (ImageView) findViewById(R.id.avatar_imv);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        UserData userDto = UserDBHelper.getUser();
        tv_name.setText(userDto.getFullName());

        tv_personal.setText(userDto.getUserId());
        tv_company.setText(userDto.getCompanyName());
//        ImageUtils.showImage(userDto, avatar_imv);
//        ImageUtils.showImage(userDto, image_profile);

        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + userDto.getAvatar());

        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);
        imageLoader.displayImage(String.valueOf(imageUri),avatar_imv);
        imageLoader.displayImage(String.valueOf(imageUri),image_profile);
//        avatar_imv.setImageURI(imageUri);
//        image_profile.setImageURI(imageUri);

//        ImageUtils.showImage(userDto, img_bg);
//        try {
//            object = new JSONObject(HomeActivity.myInfor);
//            CellPhone = object.optString("CellPhone");
//            MailAddress = object.optString("MailAddress");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        tv_email.setText("" + userDto.getmEmail());
        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_image_profile.setVisibility(View.VISIBLE);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_image_profile.setVisibility(View.GONE);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (lay_image_profile.getVisibility()==View.GONE) {
            super.onBackPressed();
        }else {
            lay_image_profile.setVisibility(View.GONE);
        }
    }
}
