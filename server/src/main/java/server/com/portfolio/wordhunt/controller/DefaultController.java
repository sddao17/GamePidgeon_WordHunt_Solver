
package server.com.portfolio.wordhunt.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.com.portfolio.wordhunt.model.Board;

@RestController
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class DefaultController {

    public DefaultController() {}

    @GetMapping()
    public String getSolvedBoard() {
        return """
                <p>
                    Hello from the Game Pigeon Word Hunt Solver!
                    <br/>To call the endpoint correctly, use this format:
                    <br/><br/>
                    https://server-dot-game-pigeon-word-hunt-solver.wl.r.appspot.com/solveBoard?letters=INSASEETITPRNEREASDHTITES
                </p>""";
    }
}
