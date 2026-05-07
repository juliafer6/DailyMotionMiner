package aiss.dailymotionminer.controller;

import aiss.dailymotionminer.model.videominer.Channel;
import aiss.dailymotionminer.service.DailymotionService;
import aiss.dailymotionminer.etl.Transformer;
import aiss.dailymotionminer.service.VideoMinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailymotionminer/channel")
public class DailymotionController {

    @Autowired
    DailymotionService dailymotionService;

    @Autowired
    VideoMinerService videoMinerService;

    @Autowired
    Transformer transformer;


    // POST - Obtiene canal de Dailymotion y lo envía a VideoMiner
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public Channel send(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) {

        // Usamos el transformer igual que en Peertube
        Channel channel = transformer.buildChannel(id, maxVideos, maxComments);
        return videoMinerService.postChannel(channel);
    }

    // GET - Solo para comprobar el JSON en local
    @GetMapping("/{id}")
    public Channel get(
            @PathVariable String id,
            @RequestParam(defaultValue="10") Integer maxVideos,
            @RequestParam(defaultValue="2") Integer maxComments) {

        return transformer.buildChannel(id, maxVideos, maxComments);
    }
}