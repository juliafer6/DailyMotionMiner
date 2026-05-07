package aiss.dailymotionminer.service;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionChannel;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionVideo;
import aiss.dailymotionminer.model.dailymotionminer.DailymotionVideoList;
import aiss.dailymotionminer.model.videominer.Channel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VideoMinerService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Channel postChannel(Channel channel){
        String url = "http://localhost:8080/videominer/api/channels";

        // 1. Creamos las cabeceras y metemos la API Key
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", "trabajoAiss123");

        // 2. Metemos el canal y las cabeceras en un "paquete" llamado HttpEntity
        HttpEntity<Channel> request = new HttpEntity<>(channel, headers);

        // 3. Hacemos el POST enviando el paquete completo (request)
        return restTemplate.postForObject(url, request, Channel.class);
    }
}
