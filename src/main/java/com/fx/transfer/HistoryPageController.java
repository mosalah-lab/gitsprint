package com.fx.transfer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Friendly route alias so users can visit /history instead of /history.html.
 */
@Controller
public class HistoryPageController {

    @GetMapping("/history")
    public String historyPage() {
        return "forward:/history.html";
    }
}
