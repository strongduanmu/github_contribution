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
    private static final String USER_COOKIE = "_octo=GH1.1.785409785.1699508531; _device_id=1945588271570352fdfec974bd9777d5; saved_user_sessions=10829171%3A492hGk1CjCJvPfshxenIoDJlIVTrKwVB_8laMdoiT6YNBvc3; user_session=492hGk1CjCJvPfshxenIoDJlIVTrKwVB_8laMdoiT6YNBvc3; __Host-user_session_same_site=492hGk1CjCJvPfshxenIoDJlIVTrKwVB_8laMdoiT6YNBvc3; logged_in=yes; dotcom_user=strongduanmu; GHCC=Required:1-Analytics:1-SocialMedia:1-Advertising:1; MicrosoftApplicationsTelemetryDeviceId=a19854df-8d45-4979-8daf-0789cf88c485; color_mode=%7B%22color_mode%22%3A%22light%22%2C%22light_theme%22%3A%7B%22name%22%3A%22light%22%2C%22color_mode%22%3A%22light%22%7D%2C%22dark_theme%22%3A%7B%22name%22%3A%22dark_dimmed%22%2C%22color_mode%22%3A%22dark%22%7D%7D; tz=Asia%2FShanghai; preferred_color_mode=light; has_recent_activity=1; _gh_sess=o%2BIVIJnLAbOeYXHo%2FFWUmRxsbsQh1k8xY6yJ2MB9CmJrcqDYUWczH53k0qCE8Czbidx8hqiw%2FNfehwL27Dz%2FrwyB3rx%2B5FfpVQTlAimlFWFaPfZbpxIZtb3TQQR54xk38M8gPC60djzDOKwhfWTnmz%2Bm72yKdOfayJsfpxpAC%2F4KTEtThBdOXJgrT6f1karXC5pUaBx1xhXeHdT7u%2BNRHjnk2sXexQIjhMTycD6NmgTbANaYeyD432OyTpIBqhq8idOJcNiDNj%2BR%2Ffa%2F42qPtyyXLsRAAKzaYuvASch92vgCI1SZLBwIxun%2BmRMuTfPVhxRZVuLoZLp5gHzoYHb0jR9WM6Buq3zlNQhA43EyVnV1SWwb047Zzmf4TRjmIpwiwklOkQrb%2Bi1B5h5J%2BRaagmtS6XyGrLsrx4Q%2B0NPDVSgEeU9%2B0cPRsLGpj2JvqkexkjBKh2mqiDVtMP9aEcYSak0nFqReVHlOTz9MjefTL8igqkjHSMJw0PcuxVxpQjvYA7vJZcPvGaPLwl4VrN9RkeTD12wR5xkRTglIcw9jGn8s4AtPOMYFnuRdN6q6rm7F8BpYCqiz1s90XV8W--rBHonVXeb6QhlDTq--%2FWsD7GlyqxN9nOciGWaeFg%3D%3D";
    private static final String GITHUB_USER_NAME = "TherChenYang";
    
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
