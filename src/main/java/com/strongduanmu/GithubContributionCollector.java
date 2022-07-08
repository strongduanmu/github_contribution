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
    private static final String USER_COOKIE = "user_session=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; __Host-user_session_same_site=Vg44G5Vae-g6zm6GUwRO8DToHtb-2pX56Q8DJlEhZlYowlmN; _octo=GH1.1.430103238.1653223781; _device_id=445f868b5c137eb0769a6a8f8e934527; logged_in=yes; dotcom_user=strongduanmu; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; preferred_color_mode=light; tz=Asia%2FShanghai; SL_G_WPT_TO=zh-CN; SL_GWPT_Show_Hide_tmp=1; SL_wptGlobTipTmp=1; has_recent_activity=1; _gh_sess=eDd%2BwP2kTvBeFf1HURX98jTSHP4DK48u80bl3ixJZ6C7ykY78U3Ds62qlZp9dRPIqfswqNUONrX71TU1sQd78Kdxz1mNrVFaN9nnk2Od%2FG5ZFDPPXn1B8yGTUoEmwpt2zJVG7bg4ffds%2ByCYGdXqks0LDTCkOLuEsnriSiIfFjomNmRHGhkwah5qJEl%2FxDSzh%2Fq3WVLKYX6cMBCfySYHLTnOJu5%2B66nm2DdOfAkD9ljlN6r1uRlOA%2F2pA88g314LTRPGZLZHCSqXX7MGcxsdIpAZEdHUGdqi2SFYAZgseYh%2FJoc3JhwPA9utMP7Ef2Zy81D03WjWs612%2BidJiA%2FCnnGyReIeutQxGB98Bw6Zrk4UcEakKs2tpOz6%2BvCBt7lXMA2m4r7pF0TjoUzDQ3IvFCbcTSM3GRyTGl7V4s%2FCyptbsgSsQbj7%2FmLShi8vwLGmD8l8xR8YAUW0fR8BzulhXpLqeP8lXwdgybxlzLr7eUH2Va5CXxRWMggl4o1NBIzHbOBGzeaf57VJ5Tbv6MGRUQFnkgPBT%2FlVGxxEiY1zANwuXenIM2%2FPEdTv2H9ykGWUwBn9DX973nQFTfdsboPxiO7DSiVEquseyxmreTPNRLiI9remFvXAvh4aUvKE%2ByY8g2wyL7qyjO5frwdo9vjZqTV9QavFgEsSj1UJ3hwHVXTY3uXgz%2BucJZN3BU8DwMm5QIY6tJSlCAQ4nJ%2BrJrUa1CImraB%2F9QVWAvuhkPDI896GGurUjLX0sAR0Vta2CatmP6ezrDoMTrBWUxrwtuKDUqS98QOYeEOwKWPAW%2FATETqFCs8A%2B%2BJfqlvRMV7%2BBBd9UozLY%2FMLabKNwktY64klleR2NRAZJlM9%2FxJkyw%3D%3D--O0HarIX8D039cdg1--NtFxtg2Ywg3pQ1Yje%2BhW8A%3D%3D";
    private static final String GITHUB_USER_NAME = "strongduanmu";
    
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
