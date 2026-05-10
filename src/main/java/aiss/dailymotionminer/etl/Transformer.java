package aiss.dailymotionminer.etl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import aiss.dailymotionminer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import aiss.dailymotionminer.model.dailymotionminer.*;
import aiss.dailymotionminer.model.videominer.*;

@Component
public class Transformer {

    @Autowired
    DailymotionService service;

    private String convertirFecha(long unixTimestamp) {
        return Instant.ofEpochSecond(unixTimestamp) // los ajusta a zona horaria local
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public Channel buildChannel(String userId, Integer maxVideos, Integer maxComments) {
        // Llama a service para descargar el canal crudo
        DailymotionChannel dmChannel = service.getChannel(userId);
        if (dmChannel == null) return null; // si no existe, aborta

        // Crea la caja vacia de videominer
        Channel channel = new Channel();
        channel.setId(dmChannel.getId());

        // Empieza a rellenar la caja traduciendo los datos
        channel.setName(dmChannel.getScreenName());
        channel.setDescription(dmChannel.getDescription());
        channel.setCreatedTime(dmChannel.getCreatedTime() != null ? convertirFecha(dmChannel.getCreatedTime()) : null);

        // Pasamos dmChannel entero para poder extraer la foto más adelante
        channel.setVideos(this.mapVideos(userId, maxVideos, dmChannel, maxComments));

        return channel;
    }

    private List<Video> mapVideos(String userId, Integer maxVideos, DailymotionChannel dmChannel, Integer maxComments) {
        // Pide la lista de videos crudosal Service (solo la pag 1, con un limite)
        DailymotionVideoList dmVideoList = service.getVideosFromUser(userId, maxVideos, 1);
        // Prepara la lista vacia de videominer
        List<Video> videos = new ArrayList<>();

        if (dmVideoList != null && dmVideoList.getList() != null) {
            for (DailymotionVideo dmVideo : dmVideoList.getList()) {
                videos.add(this.buildVideo(dmVideo, dmChannel, userId, maxComments));
            }
        }
        return videos; // Devuelve la lista de videos mapeada
    }

    private Video buildVideo(DailymotionVideo dmVideo, DailymotionChannel dmChannel, String userId, Integer maxComments){
        // Crea el video limpio y le pone los datos basicos
        Video video = new Video();
        video.setId(dmVideo.getId());
        video.setName(dmVideo.getTitle());
        video.setDescription(dmVideo.getDescription());
        video.setReleaseTime(dmVideo.getCreatedTime() != null ? convertirFecha(dmVideo.getCreatedTime()) : null);

        // MAPEO DEL AUTOR (User)
        User author = new User();
        // Usamos Math.abs para asegurar que el ID sea positivo
        author.setId((long) Math.abs(userId.hashCode()));
        author.setName(dmChannel.getScreenName());
        author.setUser_link("https://www.dailymotion.com/" + userId);
        author.setPicture_link(dmChannel.getPictureLink());
        video.setAuthor(author);

        // MAPEO DE COMENTARIOS (Usando Tags)
        List<Comment> comments = new ArrayList<>();
        if (dmVideo.getTags() != null) {
            int contador = 0;
            for (String tag : dmVideo.getTags()) {
                if (contador >= maxComments) break; // Si llegamos al limite, paramos

                Comment comment = new Comment();
                comment.setId(dmVideo.getId() + "-tag-" + contador);
                comment.setText(tag);
                comment.setCreatedOn(video.getReleaseTime());

                comments.add(comment);
                contador++;
            }
        }
        video.setComments(comments);

        // MAPEO SUBTITULOS (Captions)
        List<Caption> captions = new ArrayList<>();
        // Llamada a la API
        DailymotionCaptionResponse dmCaptions = service.getCaptions(dmVideo.getId());

        if (dmCaptions != null && dmCaptions.getList() != null) {
            for (DailymotionCaption dmCap : dmCaptions.getList()) {
                Caption caption = new Caption();
                caption.setId(dmCap.getId());
                caption.setLanguage(dmCap.getLanguage());
                caption.setName("Subtitles " + dmCap.getLanguage());
                captions.add(caption);
            }
        }
        video.setCaptions(captions);

        return video;
    }
}