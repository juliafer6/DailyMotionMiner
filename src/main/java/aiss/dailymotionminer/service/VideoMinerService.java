package aiss.dailymotionminer.service;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionChannel;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionVideo;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionVideoList;
import aiss.dailymotionminer.model.videominer.Channel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class VideoMinerService {
    private final RestTemplate restTemplate = new RestTemplate();
    // Si lo abrimos desde railway el post ira a VIDEOMINER_URL, si no, a al localhost
    @Value("${VIDEOMINER_URL:http://localhost:8080}")
    private String videominerUrl;

    public Channel postChannel(Channel channel){
        String url = videominerUrl + "/videominer/api/channels";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "trabajoAiss123");

        HttpEntity<Channel> request = new HttpEntity<>(channel, headers);

        return restTemplate.postForObject(url, request, Channel.class);
    }
}
