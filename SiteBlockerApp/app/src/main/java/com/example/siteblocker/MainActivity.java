package com.example.siteblocker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnGo;
    private EditText edtUrl;
    private Button btnAddBlock;
    private ListView listView;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "siteblock_prefs";
    private static final String KEY_BLOCKED = "blocked_sites";
    private ArrayAdapter<String> adapter;
    private List<String> blockedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        webView = findViewById(R.id.webview);
        btnGo = findViewById(R.id.btn_go);
        edtUrl = findViewById(R.id.edt_url);
        btnAddBlock = findViewById(R.id.btn_add_block);
        listView = findViewById(R.id.block_list);

        blockedList = new ArrayList<>(prefs.getStringSet(KEY_BLOCKED, new HashSet<String>()));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, blockedList);
        listView.setAdapter(adapter);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (isBlocked(url)) {
                    showBlockedDialog(url);
                    return true; // cancel load
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isBlocked(url)) {
                    showBlockedDialog(url);
                    return true;
                }
                return false;
            }
        });

        btnGo.setOnClickListener(v -> {
            String url = edtUrl.getText().toString().trim();
            if (url.isEmpty()) return;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            if (isBlocked(url)) {
                showBlockedDialog(url);
            } else {
                webView.loadUrl(url);
            }
        });

        btnAddBlock.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("example.com or https://example.com/path");

            new AlertDialog.Builder(this)
                    .setTitle("Add site to block list")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String text = input.getText().toString().trim();
                        if (!text.isEmpty()) {
                            addBlocked(text);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String item = blockedList.get(position);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Remove from blocked list?")
                    .setMessage(item)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        removeBlocked(item);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    private boolean isBlocked(String urlStr) {
        try {
            Uri u = Uri.parse(urlStr);
            String host = u.getHost();
            if (host == null) return false;
            host = host.toLowerCase();
            for (String s : blockedList) {
                String b = normalizeHost(s);
                if (host.equals(b) || host.endsWith("." + b)) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private String normalizeHost(String input) {
        String t = input.trim().toLowerCase();
        if (t.startsWith("http://") || t.startsWith("https://")) {
            try {
                URI uri = new URI(t);
                String h = uri.getHost();
                if (h != null) return h;
            } catch (Exception ignored) {}
        }
        // strip path if present
        if (t.contains("/")) t = t.split("/")[0];
        // strip port
        if (t.contains(":")) t = t.split(":")[0];
        return t;
    }

    private void showBlockedDialog(String url) {
        new AlertDialog.Builder(this)
                .setTitle("Blocked")
                .setMessage("This site is blocked:\n" + url)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addBlocked(String site) {
        String n = normalizeHost(site);
        if (n.isEmpty()) return;
        if (!blockedList.contains(n)) {
            blockedList.add(n);
            persistBlocked();
            adapter.notifyDataSetChanged();
        }
    }

    private void removeBlocked(String host) {
        blockedList.remove(host);
        persistBlocked();
        adapter.notifyDataSetChanged();
    }

    private void persistBlocked() {
        Set<String> set = new HashSet<>(blockedList);
        prefs.edit().putStringSet(KEY_BLOCKED, set).apply();
    }
}
