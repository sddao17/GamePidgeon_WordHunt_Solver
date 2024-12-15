
package server.com.portfolio.wordhunt.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class HomeController {

    public HomeController() {}

    @GetMapping()
    public String getSolvedBoard() {
        return """
                <p>
                    Hello from the Game Pigeon Word Hunt Solver server!
                    <br/>To call the endpoint correctly, use this format:
                    <br/><br/>
                    https://server-dot-game-pigeon-word-hunt-solver.wl.r.appspot.com/board/solve?letters=[ENTER_LETTERS_HERE]
                </p>""";
    }
}
