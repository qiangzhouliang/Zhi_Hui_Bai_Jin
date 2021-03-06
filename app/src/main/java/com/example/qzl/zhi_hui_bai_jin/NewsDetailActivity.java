package com.example.qzl.zhi_hui_bai_jin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 新闻详情页
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.ll_tb_control)
    private LinearLayout mLlControl;
    @ViewInject(R.id.btn_base_pager_back)
    private ImageButton mBtnBack;
    @ViewInject(R.id.btn_base_pager_size)
    private ImageButton mBtnTextSize;
    @ViewInject(R.id.btn_base_pager_share)
    private ImageButton mBtnShare;
    @ViewInject(R.id.btn_base_pager_menu)
    private ImageButton mBtnMenu;
    @ViewInject(R.id.wv_and_news_detail)
    private WebView mWebView;
    @ViewInject(R.id.pb_and_loading)
    private ProgressBar mPbLoading;

    private String TAG = "NewsDetailActivity";
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        ViewUtils.inject(this);

        mLlControl.setVisibility(View.VISIBLE);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnMenu.setVisibility(View.GONE);

        mBtnBack.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnTextSize.setOnClickListener(this);

        mUrl = getIntent().getStringExtra("url");
        //mWebView.loadUrl("http://www.itheima.com");
        mWebView.loadUrl(mUrl);

        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);//设置显示缩放按钮(wap网页不支持)
        settings.setUseWideViewPort(true);//支持双击缩放
        settings.setJavaScriptEnabled(true);//支持js功能

        mWebView.setWebViewClient(new WebViewClient() {
            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "开始加载网页了。。。");
                mPbLoading.setVisibility(View.VISIBLE);
            }

            //网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "网页加载结束了");
                mPbLoading.setVisibility(View.GONE);
            }

            //所有链接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "跳转链接：" + url);
                view.loadUrl(url);//在跳转页面时，强制在当前webView中加载
                return true;
            }
        });
        //mWebView.goBack();//跳到上个页面
        //mWebView.goForward();//跳到上个页面
        mWebView.setWebChromeClient(new WebChromeClient() {
            //进度发生变化
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //进度发生变化
                Log.d(TAG, "进度：" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //网页标题
                Log.d(TAG, "网页标题:" + title);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_base_pager_back:
                finish();
                break;
            case R.id.btn_base_pager_size:
                //修改网页字体大小
                showChooseDialog();
                break;
            case R.id.btn_base_pager_share:
                showShare();
                break;
        }
    }

    private int mTempWhich;//记录临时选择的字体大小（点击确定之前）
    private int mcurrentWhich = 2;//记录当前选中的字体打下（点击确定之后）,默认正常字体

    /**
     * 展示选择字体大小的弹窗
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体设置");
        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        builder.setSingleChoiceItems(items, mcurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //根据选怎的字体来修改网页字体大小
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich) {
                    case 0://超大字体
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        //settings.setTextZoom(22);
                        break;
                    case 1://大字体
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2://正常字体
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3://小字体
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4://超小字体
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                }
                mcurrentWhich = mTempWhich;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //oks.setTheme();可以修改主题样式
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
