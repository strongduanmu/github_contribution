package com.strongduanmu;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Refer：https://www.cnblogs.com/youyoui/p/11065923.html
 * Github contribution collector.
 */
public final class GithubReleaseNoteCollector {
    
    private static final String GITHUB_PULL_REQUEST_URL = "https://github.com/apache/shardingsphere/pulls?page=%s&q=is:pr milestone:%s reviewed-by:%s";
    private static final String USER_COOKIE = "_octo=GH1.1.785409785.1699508531; _device_id=1945588271570352fdfec974bd9777d5; user_session=MKCH8Pkl9YKXlFpyijhwcaAbk1wsDE9gJeNvxFE6bUllLfFN; __Host-user_session_same_site=MKCH8Pkl9YKXlFpyijhwcaAbk1wsDE9gJeNvxFE6bUllLfFN; logged_in=yes; dotcom_user=strongduanmu; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; tz=Asia%2FShanghai; preferred_color_mode=light; has_recent_activity=1; _gh_sess=rSAc5x5HVyHaI%2BGjDBYL5Yhik%2BKUP8pgVdfa8epzNgQmgu5GBQr9qrw8uIQj4u3dx46RVGbyQcpLNFiN%2BKA7wR9AtaC2w3c3%2BX6TDWhPFQQqxR2S9v1WhmcGUWe7JIe3k1TqOK3xRybeCidYOUn8yIavVnct%2FHbbcFTJfauhDmnd2MclSspvsuER6o1ZNlGPWLtInwJZXYuxXszpkI7IqHFVEUptbNLO0f2MkcEHrJxSFhqGToeM32qmkAo1MwCCPlmBtzgzWXzvqZeHGtwr2GxpoX00MHi6bpFMP5eWQA%2BZ%2FAaFwPuQgc%2F6UQMV4qUKaeleES%2B10hITStrkSGVdJ7xXxWPPjEmMq%2FBKEuPioIMjiKXj%2BRuf9oObNQRAC5If2Wq%2Bcp6HiLFU3pihC5c3gvax8ae8fbOXC9bbHgltHmxfTWC5%2Bz%2Bu8VvU4ybGyjM2tSvgjkkj2aujyBeMDYZ%2BPJDKGrkq3YlXAjNUUNc6ECgi9ZvBJey0LI%2FxyxykCRml0PLjX2nAMouhKNu9TSOBw6qDowokqVmDL40eLG431h7ZAwWsrx9qOOBhjpGWyHhrXO6EA0SVOphm%2BxExwAzrqrCJ8oIo6wyH71ZhNYFReiBXqioE7YElY1xEmY011pT2%2FZESoDJ6LJ47OYsN8kIEEDfArBH2PFj97Hv7y8EGdEAuqhkeOz6GkUQ4NewfMCigCFqb9X%2F2MRSR6zY%2BnRWIRFp9Rd8RXFPZymvE7%2FdeyjMgIY6XZjj%2BIqo%2Bj%2B3v%2FlgiaHhmQIoKMd3GMu60HCgYGg%3D%3D--1IhM6ngdc9RaIwnT--43LDNZl%2FV0ShbB28mjySxQ%3D%3D";
    private static final String MILESTONE = "5.5.0";
    private static final String AUTHOR = "@me";
    
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        int page = 1;
        Elements elements;
        do {
            String pullRequestUrl = String.format(GITHUB_PULL_REQUEST_URL, page, MILESTONE, AUTHOR);
            Request request = new Request.Builder().url(pullRequestUrl).header("Accept", "text/html, application/xhtml+xml").addHeader("cookie", USER_COOKIE).build();
            Response response = client.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                Document document = Jsoup.parse(body.string());
                elements = document.getElementsByClass("Link--primary");
                for (Element each : elements) {
                    Element statusElement = each.lastElementSibling();
                    String status = statusElement.getElementsByClass("opened-by").text();
                    boolean merged = status.contains("was merged");
                    boolean opened = status.contains("opened");
                    if (!merged && !opened) {
                        continue;
                    }
                    String types = getTypes(statusElement.previousElementSibling());
                    System.out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s%n", "", each.text(), types, "", "", "https://github.com" + each.attr("href"), "", merged ? "Merged" : "Opened", "");
                }
            }
            page++;
        } while (!elements.isEmpty());
    }
    
    private static String getTypes(final Element typeElement) {
        if (null == typeElement) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Element type : typeElement.getElementsByTag("a")) {
            String[] segments = type.text().split(":");
            String label = segments.length > 1 ? segments[1].trim(): segments[0].trim();
            builder.append(label).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }
}
