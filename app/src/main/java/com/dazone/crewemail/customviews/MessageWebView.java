package com.dazone.crewemail.customviews;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebSettings;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;


public class MessageWebView extends RigidWebView {

    private Context mContext;

    public MessageWebView(Context context) {
        super(context);
        mContext = context;
    }

    public MessageWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public MessageWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

    }

    /**
     * Configure a web view to load or not load network data. A <b>true</b> setting here means that
     * network data will be blocked.
     *
     * @param shouldBlockNetworkData True if network data should be blocked, false to allow network data.
     */
    public void blockNetworkData(final boolean shouldBlockNetworkData) {
        /*
         * Block network loads.
         *
         * Images with content: URIs will not be blocked, nor
         * will network images that are already in the WebView cache.
         *
         */
        getSettings().setBlockNetworkLoads(shouldBlockNetworkData);
    }


    /**
     * Configure a {@link android.webkit.WebView} to display a Message. This method takes into account a user's
     * preferences when configuring the view. This message is used to view a message and to display a message being
     * replied to.
     */
    public void configure() {
        //Display display = DaZoneApplication.getInstance().getApplicationContext().getWindowManager().getDefaultDisplay();
        //int width = display.getWidth();
        this.setVerticalScrollBarEnabled(false);
        this.setVerticalScrollbarOverlay(false);
        this.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        this.setScrollbarFadingEnabled(true);
        this.setHorizontalScrollBarEnabled(false);
        this.setLongClickable(true);
        boolean isAdjust = new Prefs().getBooleanValue(Statics.KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH, true);

        if (isAdjust) {
            this.setInitialScale(20);
        } else {
            this.setInitialScale(0);
        }


        final WebSettings webSettings = this.getSettings();

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setDefaultFontSize(14);
        //if (DaZoneApplication.autofitWidth()) {
        webSettings.setLoadWithOverviewMode(true);
        //}

        disableDisplayZoomControls();
        //webSettings.setLoadWithOverviewMode(true);
        //webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // TODO:  Review alternatives.  NARROW_COLUMNS is deprecated on KITKAT
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        setOverScrollMode(OVER_SCROLL_NEVER);

        webSettings.setTextZoom(DaZoneApplication.getFontSizes().getMessageViewContentAsPercent());

        // Disable network images by default.  This is overridden by preferences.
        blockNetworkData(false);
    }

    /**
     * Disable on-screen zoom controls on devices that support zooming via pinch-to-zoom.
     */
    private void disableDisplayZoomControls() {
        PackageManager pm = getContext().getPackageManager();
        boolean supportsMultiTouch =
                pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) ||
                        pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);

        getSettings().setDisplayZoomControls(!supportsMultiTouch);
    }

    /**
     * Load a message body into a {@code MessageWebView}
     * <p/>
     * <p>
     * Before loading, the text is wrapped in an HTML header and footer
     * so that it displays properly.
     * </p>
     *
     * @param text The message body to display.  Assumed to be MIME type text/html.
     */
    public void setText(String text) {


        // Include a meta tag so the WebView will not use a fixed viewport width of 980 px
        String content = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>";
                /* +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/ServiceContent.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Default_EN.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/jquery-1.7.2.min.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<script src=\"http://dazone.crewcloud.net/Js/jquery-extend.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<script src=\"http://dazone.crewcloud.net/Js/Common/Prototype.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<script src=\"http://dazone.crewcloud.net/Js/Common/ControlEvents.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<script src=\"http://dazone.crewcloud.net/Js/Common/TimeZoneOffset_Cookie.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<script src=\"http://dazone.crewcloud.net/Js/Common/IsDefaultContextMenu.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/UI/Mail3/Style/Start.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/UI/Mail3/Js/Start.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/TextBox.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/TextBox.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/SelectBox.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/SelectBox.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/ContextMenu.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/ContextMenu.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/HeaderMenus.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/HeaderMenus.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/Navigation.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/Navigation.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/Tree.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/Tree.js?t=20160421\" type=\"text/javascript\"></script>" +
                "<link href=\"http://dazone.crewcloud.net/CSS/Controls/ServiceContent.css?t=20160421\" rel=\"Stylesheet\" type=\"text/css\">" +
                "<script src=\"http://dazone.crewcloud.net/Js/Controls/ServiceContent.js?t=20160421\" type=\"text/javascript\"></script>";*/
       /* if (K9.getK9MessageViewTheme() == K9.Theme.DARK)  {
            content += "<style type=\"text/css\">" +
                   "* { background: black ! important; color: #F3F3F3 !important; line-height:1.2 }" +
                   ":link, :link * { color: #CCFF33 !important }" +
                   ":visited, :visited * { color: #551A8B !important }</style> ";
        }*/
        //content += HtmlConverter.cssStylePre();
        content += "</head><body>" + text + "</body></html>";
        //content.replaceAll("<img","<img width=100");

        String sanitizedContent = HtmlSanitizer.sanitize(content);
        loadDataWithBaseURL("http://", sanitizedContent, "text/html", "utf-8", null);
        resumeTimers();
    }

    /*
     * Emulate the shift key being pressed to trigger the text selection mode
     * of a WebView.
     */
    public void emulateShiftHeld() {
        try {

            KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
            shiftPressEvent.dispatch(this, null, null);
        } catch (Exception e) {
        }
    }

}
