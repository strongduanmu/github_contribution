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
    
    private static final String GITHUB_PULL_REQUEST_URL = "https://github.com/apache/shardingsphere/pulls?page=%s&q=is:pr milestone:%s";
    private static final String USER_COOKIE = "user_session=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; __Host-user_session_same_site=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; _octo=GH1.1.430103238.1653223781; _device_id=445f868b5c137eb0769a6a8f8e934527; logged_in=yes; dotcom_user=strongduanmu; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; preferred_color_mode=light; tz=Asia%2FShanghai; SL_G_WPT_TO=zh-CN; SL_GWPT_Show_Hide_tmp=1; SL_wptGlobTipTmp=1; has_recent_activity=1; _gh_sess=pq%2Bt7R2DfF5tx0L%2BOUOcEwKIilRos%2Bjk1iBypmtqaCgKDjbivlA58Gg8SK7KYZmjh%2BMThWJNnesVDmXaeX653jDWqAkLOCmU3sranYmZkEmUF6kAkC6UeMYEs24Bnj85BeOu899cIjxBIunwcNJN2C7WYGQ5LOcpTBHuWyUEQdWZgvt960GpuVvS5wbfALcF1p8UDvz7R8fkcxjk3f3ZN0eaavnLCm4k2H5zd4%2BFotOzuO62mTA9vt8MeS%2FBuGb4Ece3Pc%2B5P5ZPnLBUcF7gRKujKxSYAu3vGy9OLrJwxeO73WaOx%2BZC1yLnVw0xYwffnjEiS6mP3032YbpvcRrLkJ1BaYa8sVIxQubXSLSK97nK4SUJRS6RwQ1yYBjAWUj9EodE4N%2BHfd0IJ97ZNY2a4I37tUZxvAv8l4QOT1GMCxMxH6pw%2BJDSdn0oKwfmorkqTj9QGijOuhb%2BxnE9l6AbclSjb9phge6qp8wpzN8Pyiv7Wt0zveX99dt8%2B%2BPn2GeouQTRq%2BdVGvrMbOzUVR1HwU%2FAWeJ5Js%2FvY7Ojsg%3D%3D--xH8I%2Fj1yBTPi14fw--65Xto09BDGcxqAyvRq2vRw%3D%3D";
    private static final String MILESTONE = "5.1.3";
    
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        int page = 1;
        Elements elements;
        do {
            String pullRequestUrl = String.format(GITHUB_PULL_REQUEST_URL, page, MILESTONE);
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
            builder.append(type.text().split(":")[1].trim()).append(",");    
        }
        return builder.substring(0, builder.length() - 1);
    }
}
