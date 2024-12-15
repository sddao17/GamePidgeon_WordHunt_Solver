
package server.com.portfolio.wordhunt.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.com.portfolio.wordhunt.model.Board;

@RestController
@RequestMapping(value = "/board", consumes = MediaType.ALL_VALUE)
public class BoardController {

    public BoardController() {}

    @GetMapping("/solve")
    public Board getSolvedBoard(@RequestParam("letters") String letters) {
        return new Board(letters);
    }
}
