package com.synisys.ai;

import com.theaigames.engine.Engine;
import com.theaigames.game.warlight2.Warlight2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hayk.movsisyan on 2/23/17.
 */
@RestController
@RequestMapping()

public class WarlightEngineController {

    @Autowired
    ServletContext servletContext;
    private String uploadsDir = WarlightApplication.args[1];


    @RequestMapping(value = "/warlight/engine/play", params = { "map", "bot1", "bot2" }, method = RequestMethod.GET)
    @ResponseBody
    public String play(
            @RequestParam("map") String map,
            @RequestParam("bot1") String bot1Name,
            @RequestParam("bot2") String bot2Name,
            HttpServletResponse httpServletResponse) throws Exception {

        String botFolderTemplate = WarlightApplication.args[0] + "/%s";
        String result = playBattle(
                String.format("/map/map%s.txt", map),
                String.format(botFolderTemplate, bot1Name),
                String.format(botFolderTemplate, bot2Name));

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String dateString = dateFormat.format(date); //2016/11/16 12:08:43

        String gameId = String.format("%s-%s-%s-%s-game.", bot1Name, bot2Name, map, dateString);

        saveResultFile(gameId, result);

        //httpServletResponse.setHeader("Location", "/competitions/warlight-ai-challenge-2/games/index.html?data-file=" + gameId);
        //httpServletResponse.setStatus(302);


        File[] files = new File(uploadsDir).listFiles();
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName));
//If this pathname does not denote a directory, then listFiles() returns null.

        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            if (file.isFile()) {
                sb.append(String.format("<a href=\"%s\" target=\"x\">%s</a><br>",
                        "/competitions/warlight-ai-challenge-2/games/index.html?data-file=" + file.getName(), file.getName()));
            }
        }
//        return String.format("<html><body><a href=\"%s\">%s</a></body></html>",
//                "/competitions/warlight-ai-challenge-2/games/index.html?data-file=" + gameId, gameId);
        return "<html><body>" +sb.toString() + "</body></html>";
    }

    @RequestMapping(value = "/warlight/engine/view/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String view(@PathVariable("id") String id) throws Exception {


        return  new String(Files.readAllBytes(Paths.get(uploadsDir + "/" + id + ".")));
    }



    private String playBattle(String map, String bot1Name, String bot2Name) throws Exception {

        String resultFileName = "";


        // Construct engine
        Engine engine = new Engine();

        // Set logic
        Warlight2 warlight2 = new Warlight2(map);
        engine.setLogic(warlight2);

        // Add players
        engine.addPlayer(bot1Name);
        engine.addPlayer(bot2Name);

        engine.start();

        return warlight2.getPlayedGame();
    }


    private void saveResultFile(String name, String data) throws IOException {
        if(! new File(uploadsDir).exists())
        {
            new File(uploadsDir).mkdirs();
        }

        try( FileWriter fileWriter = new FileWriter(uploadsDir + "/" + name)){
            fileWriter.write(data);
        }
    }

}
