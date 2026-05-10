package aiss.dailymotionminer.service;

import aiss.dailymotionminer.exception.ChannelNotFoundException;
import aiss.dailymotionminer.model.dailymotionminer.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DailymotionService {

    private final RestTemplate restTemplate = new RestTemplate();

    // ── CHANNEL ──────────────────────────────────────────────────────────────

    public DailymotionChannel getChannel(String channelId) {
        String url = "https://api.dailymotion.com/user/" + channelId
                + "?fields=id,screenname,description,created_time,avatar_240_url";
        try{
            return restTemplate.getForObject(url, DailymotionChannel.class);
        } catch (HttpClientErrorException e) {
            throw new ChannelNotFoundException(channelId);
        }
    }

    // ── VIDEO ────────────────────────────────────────────────────────────────

    public DailymotionVideo getVideo(String videoId) {
        String url = "https://api.dailymotion.com/video/" + videoId
                + "?fields=id,title,description,created_time";
        return restTemplate.getForObject(url, DailymotionVideo.class);
    }

    public DailymotionVideoList getVideosFromUser(String userId, Integer limit, Integer page) {
        String url = "https://api.dailymotion.com/user/" + userId + "/videos"
                + "?limit=" + limit
                + "&page=" + page
                + "&fields=id,title,description,created_time,tags";
        return restTemplate.getForObject(url, DailymotionVideoList.class);
    }

    // ── CAPTIONS ─────────────────────────────────────────────────────────────

    public DailymotionCaptionResponse getCaptions(String videoId) {
        try {
            String url = "https://api.dailymotion.com/video/" + videoId
                    + "/subtitles?fields=id,url,language";

            return restTemplate.getForObject(url, DailymotionCaptionResponse.class);

        } catch (Exception e) {
            System.err.println("Error subtítulos para vídeo " + videoId + ": " + e.getMessage());
            return null;
        }
    }
}