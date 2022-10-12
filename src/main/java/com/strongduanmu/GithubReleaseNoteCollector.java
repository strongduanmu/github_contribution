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
    private static final String USER_COOKIE = "user_session=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; __Host-user_session_same_site=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; _octo=GH1.1.430103238.1653223781; _device_id=445f868b5c137eb0769a6a8f8e934527; logged_in=yes; dotcom_user=strongduanmu; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; tz=Asia%2FShanghai; SL_G_WPT_TO=zh-CN; SL_GWPT_Show_Hide_tmp=1; SL_wptGlobTipTmp=1; preferred_color_mode=light; _help_hub_session_v2=lNHQgIBZmHeaA%2BpB4ExjQWZwcGClU0ZwcMaB1fvxz8tesFHL3WhAoNS45YKsDQxrM7SbYZE%2FiGS3w1yBjdBQ1eEU5AYFax7yI%2Fx2nWNvjGnaWC9VzHhgUxMUvh0RdcD7Zq3PUu9QWWItwwEr2UuYzPrWUSujSaUGIXLTII4xwq9l4Mk2UzK9OH8IWDTncx3Q1moNojhjIuVbvDu4Uo1oPOBMQkuvOABpL4X%2BtRAPHTtZ07pvRnyw0C4XU7iBx11ZVPViMm1NwjXu1bxHS4lab7z5SnbkP10s7HhqMFY7hO3B1vGCP%2BQ7a35yBeUNdcTaPlDUuGLtmE9Y7sxj%2BbVcKaVGj4UjW0TdVT8HB%2FaxwLHC2e4oN0lH6wAtWV%2BJHOC9ow5p3qu5vVa6dcBYKGPX0BJ2%2B5dpcrAqk2MUqcfNuE3f2VxmDM1PmiX5HELBFHVT3LLBNO467ZN4wd%2FdVL77xqTW0veGT%2FHgsDrKCsciSKcZ31mn3CTo8cAszsTHrY%2Bol7PBvMHzsGIzgAa9M587weptZQTHI8QqHA%2F5N%2BCtpMyE145g7Fgl6fPYj9zaM7S1RxelVhuFFg2BsNiO%2FudPQRinvC69n8aIo1YmrQnpI4qJdRq0kUwNd3aU6UWaIXZmRjWfpOPjXZFhwK86JjPvQYr%2BI1%2Brk3Xt809Cb%2BN3jRr%2BJlqZPfQWsWYMbes5PXKiFVRrXoaE2WwC2VEEAjpQe4KPvMvaX1WOJamITi92gVu%2BsgGAXWI93OVVEyq0RqdbHHhwnJVCIZMd2VXC7uwRLftc%2Bkdlmd7CY0x8VanARkE%3D--qSo4%2FcpTmUqfV7eh--iPD%2F8GgNiIbV6fGt6OvdzQ%3D%3D; has_recent_activity=1; _gh_sess=QeTcrdC8i6tYXR1C9m4DPF9GnxoJGbdlcxQmf33egvMhQ57uZcinoCDaLcDOikfBM105IhTZtesVQXVa7OUtf4UIUopW25NhMB7nwlybTMUYsShE756nFWIAbB1gshG08uW%2BV1%2FnT6%2BiVdXG7ePXGUplMc0DsPaFCjdv27sgj2fvzv97AMU6UfjL7BS1jVzB7bghl4xl27DUtJduqbBrMtixC%2FIwN95o7XumT785d9vlet%2BjPQRec6wvUcaHq44y1gJKHw8b4wfNGaWjOO%2FQv0Qvb53HgRutXeg4Xht9pI5v6yVxG%2FEOViQvjzNNnpDDQoEIOYy%2BY3A%2FiGUDd1hGQTgBSss8R25csLuscMhIdNOwSAmNRcGE%2BkoAz1va3fXrXw%2FY1IayG%2FtGOizMXtnTcIyyZxkk%2F0B7wds7RYZJ925lk5XNHn%2BqhgnGnmRv3APZyN9yuQnCRdzkdEa%2BfQTPYJmWeefM7O4dDS4tNxD%2Fk1UJq8H1PXsZf%2BsWMqpMOlkQWJIDkAgl2vyVK5cy1aeyEk8soJypGBwl2UHm8aozRFR9N1%2Bxs%2FrPtiX6yCUl2LmHy6uerAg3kQoyz1L81k%2F536M2ewkjohHxFo%2B73384VOnXMJo59maoB10BHgiG%2BXI3zm30PVrH7bqkCJiBFUi0XA%3D%3D--R9flCyARuIcHDE1o--tNgI%2F4sS2RH0fUalo41Odg%3D%3D";
    private static final String MILESTONE = "5.2.1";
    
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
            String[] segments = type.text().split(":");
            String label = segments.length > 1 ? segments[1].trim(): segments[0].trim();
            builder.append(label).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }
}
