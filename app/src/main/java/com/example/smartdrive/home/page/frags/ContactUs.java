package com.example.smartdrive.home.page.frags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.smartdrive.R;
import com.example.smartdrive.ResetPassword;
import com.example.smartdrive.Utility.ProgressBar;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;


public class ContactUs extends Fragment {

    private WebView webView;
    ProgressBar progressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_us, container, false);

        //progressbar hook
        progressBar = new ProgressBar(getContext());

        webView = v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        String myUrl = "https://chouaib-saad.github.io/portfolio/#contact";
        webView.loadUrl(myUrl);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.show();
                }
                if (progress == 100) {
                    progressBar.dismiss();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                snackBar(description,3000,R.color.baseRed,R.drawable.ic_error);
            }
        });


        return v;

    } //Oncreate end





    private void snackBar(String message,int time,int backgroundColor,int icon) {

        CookieBar.build(getActivity())
                .setTitle("Avertissement")
                .setMessage(message)
                .setDuration(time)
                .setTitleColor(R.color.special_white)
                .setBackgroundColor(backgroundColor)
                .setCookiePosition(CookieBar.BOTTOM)
                .setIcon(icon)
                .setAction("D'accord", new OnActionClickListener() {
                    @Override
                    public void onClick() {
                        CookieBar.dismiss(getActivity());
                    }
                })
                .show();
    }





}  //class end
