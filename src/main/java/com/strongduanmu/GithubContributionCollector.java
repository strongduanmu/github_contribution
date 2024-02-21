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
public final class GithubContributionCollector {
    
    private static final String GITHUB_PULL_REQUEST_URL = "https://github.com/apache/shardingsphere/pulls?page=%s&q=is:pr author:%s";
    private static final String USER_COOKIE = "_octo=GH1.1.785409785.1699508531; tz=Asia%2FShanghai; _device_id=1945588271570352fdfec974bd9777d5; tz=Asia%2FShanghai; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; preferred_color_mode=light; saved_user_sessions=10829171%3AMKCH8Pkl9YKXlFpyijhwcaAbk1wsDE9gJeNvxFE6bUllLfFN; user_session=MKCH8Pkl9YKXlFpyijhwcaAbk1wsDE9gJeNvxFE6bUllLfFN; __Host-user_session_same_site=MKCH8Pkl9YKXlFpyijhwcaAbk1wsDE9gJeNvxFE6bUllLfFN; logged_in=yes; dotcom_user=strongduanmu; has_recent_activity=1; _gh_sess=ICw7kTofytB9kfecTqLovn7e0bfdo4hLOFJ49hv0it%2FSwUWEEZ64lsJwP%2BGOceDLz8FRuG%2Fbz1QdyCMRgdwgH50O262x4oSSSxFhahWgIGGjv1g%2FFM841zBoxSoLPmt1qMbzW5fdAZGHXn5dH59H7jMu4%2FK6H44IyO6li32PdXFTm8LgAbqFuGGlD1kOjDcOAQZbmVQiyELIz9Fzr7goFKn9uwXzsEvlhCuMW%2FqZRaSEYw9afgPqGiD%2BK%2BFxaOt4t719%2FSnwb51yZGo3J%2FpdmcEn%2BrVNwBgWUs0RKJ5NajA8UTCuexa%2B0g0D0qqx9%2Fd13DTpYwrIThXiRPhwtUyppDQsOO4PGbUQCp6ZICFQ9RzOodxVgl87xiiCV4C3363kDNJAClHj1Vv1x%2BtBaldvUH1wlhCMja2WUJ0Jsp1leWvrWtjsZBCxqteWLtpWmqWI3nK6F40wH8xnEOciSHsi4tAzKN%2BpovYeLkP%2BOOjhwxkxekrpHe5mj%2Bglh8Zc9yBoBVt%2F4swqMbdDJGEkOhNgRrj3ZQPz9wIdFRCkRTAvPdsjm6vFkM3LendKd8C74byLQndZjzfTRAE%3D--RvA2rHPHn0Gfq0Ce--nvLk7rpPhjidstKF%2FJ8hfg%3D%3D";
    private static final String GITHUB_USER_NAME = "zihaoAK47";
    
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        int page = 1;
        Elements elements;
        int count = 0;
        do {
            String pullRequestUrl = String.format(GITHUB_PULL_REQUEST_URL, page, GITHUB_USER_NAME);
            Request request = new Request.Builder().url(pullRequestUrl).header("Accept", "text/html, application/xhtml+xml").addHeader("cookie", USER_COOKIE).build();
            Response response = client.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                Document document = Jsoup.parse(body.string());
                elements = document.getElementsByClass("Link--primary");
                for (Element each : elements) {
                    boolean merged = each.lastElementSibling().getElementsByClass("opened-by").text().contains("was merged");
                    if (!merged) {
                        continue;
                    }
                    System.out.println(++count + ". " + each.text() + "——https://github.com" + each.attr("href"));
                }
            }
            page++;
        } while (!elements.isEmpty());
    }
}
